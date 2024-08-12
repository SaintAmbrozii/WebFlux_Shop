package com.example.webfluxshop.service;



import com.example.webfluxshop.domain.RefreshToken;
import com.example.webfluxshop.domain.User;
import com.example.webfluxshop.exception.UnauthorizedException;
import com.example.webfluxshop.payload.AuthResponse;
import com.example.webfluxshop.repository.TokenRepo;
import com.example.webfluxshop.security.JwtProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Date;

;


@Service
@RequiredArgsConstructor
public class RefreshTokenService {



    private final UserService userService;
    private final AccessTokenService accessTokenService;
    private final JwtProvider provider;
    private final TokenRepo tokenRepo;

    public Mono<AuthResponse> getRefreshToken(String refreshToken) {
        final Claims claims = provider.getClaims(refreshToken);
        final String email = claims.getSubject();
        userService.findByUsername(email).flatMap(
                userDetail -> {
                    if (provider.validateToken(refreshToken, (UserDetails) userDetail)) {
                        Mono<String> accessToken = accessTokenService.generateAccessToken(userDetail);
                        Mono<String> newRefreshToken = generateRefreshToken(userDetail);
                        return Mono.zip(accessToken, newRefreshToken)
                                .map(tulpe -> new AuthResponse(tulpe.getT1(), tulpe.getT2()));
                    }
                    return Mono.error(new UnauthorizedException(""));
                }
        );
        return Mono.empty();
    }
    public Mono<String> generateRefreshToken(User user)
    {
        String refreshToken = provider.generateRefreshToken(user);
        final Claims claims = provider.getClaims(refreshToken);
        final Date duration = claims.getExpiration();
        final String email = claims.getSubject();
        RefreshToken token = RefreshToken.builder().token(refreshToken).duration(duration).username(email).build();
       return tokenRepo.save(token).thenReturn(refreshToken);
    }


}
