package com.example.webfluxshop.service;


import com.example.webfluxshop.domain.User;
import com.example.webfluxshop.exception.UnauthorizedException;
import com.example.webfluxshop.payload.AuthRequest;
import com.example.webfluxshop.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class AccessTokenService {


    private final JwtProvider jwtProvider;

    private final PasswordEncoder passwordEncoder;


    public Mono<String> generateAccessToken(User user) {
        String accessToken = jwtProvider.generateAccessToken(user);
        return Mono.just(accessToken);
    }

    @SneakyThrows
    public boolean isValidUser(final AuthRequest request, final User user)  {
        if (!(request.getEmail().equals(user.getEmail())) &&
                passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("");
        }
        return true;
    }
















}
