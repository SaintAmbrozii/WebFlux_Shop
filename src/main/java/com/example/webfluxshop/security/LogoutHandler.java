package com.example.webfluxshop.security;

import com.example.webfluxshop.domain.RefreshToken;
import com.example.webfluxshop.domain.User;
import com.example.webfluxshop.payload.LogoutResponse;
import com.example.webfluxshop.repository.TokenRepo;
import com.example.webfluxshop.util.JsonObjectMapper;
import io.jsonwebtoken.Claims;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class LogoutHandler implements ServerLogoutHandler {

    private final JwtProvider jwtProvider;
    private final JsonObjectMapper jsonObjectMapper;
    private final TokenRepo tokenRepo;
    private final ServerWebExchangeMatcher matcher = ServerWebExchangeMatchers.pathMatchers("/logout");

    public LogoutHandler(JwtProvider jwtProvider, JsonObjectMapper jsonObjectMapper, TokenRepo tokenRepo) {
        this.jwtProvider = jwtProvider;
        this.jsonObjectMapper = jsonObjectMapper;
        this.tokenRepo = tokenRepo;
    }

    @Override
    public Mono<Void> logout(WebFilterExchange exchange, Authentication authentication) {
        return this.matcher.matches(exchange.getExchange())
                .filter(ServerWebExchangeMatcher.MatchResult::isMatch)
                .switchIfEmpty(exchange.getChain().filter(exchange.getExchange()).then(Mono.empty()))
                .flatMap(matchResult -> this.logoutHandler(exchange, authentication));
    }

    private Mono<Void> logoutHandler(WebFilterExchange exchange, Authentication authentication) {
        return ReactiveSecurityContextHolder.getContext()
                .doOnNext(securityContext -> securityContext.getAuthentication().setAuthenticated(false))
                .then(Mono.just(exchange))
                .flatMap(webFilterExchange -> {
                    ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
                    ServerHttpRequest request = webFilterExchange.getExchange().getRequest();

                    HttpHeaders responseHeaders = response.getHeaders();
                    HttpHeaders requestHeaders = request.getHeaders();

                    List<String> authorizationHeaderList = requestHeaders.get(HttpHeaders.AUTHORIZATION);

                    if (authorizationHeaderList == null) {
                        return exchange.getChain().filter(exchange.getExchange());
                    }
                    String authHeader = authorizationHeaderList.get(0);

                    if(StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")){

                        final String jwt = authHeader.substring(7);
                        Mono<RefreshToken> token = tokenRepo.findByToken(jwt);
                        token.flatMap(userToken -> {
                            RefreshToken refreshToken = RefreshToken.builder()
                                    .expired(true)
                                    .revoked(true).build();
                            return tokenRepo.save(refreshToken);
                        });

                    }

                    Claims claims = jwtProvider.getClaims(authHeader);

                    String user = claims.get("user", String.class);

                    User userFromJson = jsonObjectMapper.deserializeJson(user, User.class);

                    LogoutResponse logoutResponse = new LogoutResponse(
                            userFromJson.getId(),
                            userFromJson.getEmail()
                    );

                    DataBufferFactory dataBufferFactory = response.bufferFactory();

                    String logoutResponseJson = jsonObjectMapper.serializeObject(logoutResponse);

                    DataBuffer wrappedLogoutResponse = dataBufferFactory
                            .wrap(logoutResponseJson.getBytes(StandardCharsets.UTF_8));

                    responseHeaders.remove("accessToken");
                    responseHeaders.remove("roles");
                    responseHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

                    return response.writeWith(Flux.just(wrappedLogoutResponse));
                });
    }
}
