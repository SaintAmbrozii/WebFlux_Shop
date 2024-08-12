package com.example.webfluxshop.controller;


import com.example.webfluxshop.domain.User;
import com.example.webfluxshop.payload.AuthRequest;
import com.example.webfluxshop.payload.AuthResponse;
import com.example.webfluxshop.payload.RegisterRequest;
import com.example.webfluxshop.repository.UserRepo;
import com.example.webfluxshop.service.AccessTokenService;
import com.example.webfluxshop.service.RefreshTokenService;
import com.example.webfluxshop.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepo userRepo;
    private final UserService userService;


    private final PasswordEncoder encoder;


    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest request) {

       return userService.findByUsername(request.getEmail())
                .filter(user-> accessTokenService.isValidUser(request,user))
                .flatMap(userDetails -> {
                    Mono<String> accessToken = accessTokenService.generateAccessToken(userDetails);
                    Mono<String> refreshToken = refreshTokenService.generateRefreshToken(userDetails);
                    return Mono.zip(accessToken,refreshToken)
                            .map(tuple->new AuthResponse(tuple.getT1(),tuple.getT2()))
                            .map(ResponseEntity::ok);
                });

    }

    @PostMapping("/register")
    public Mono<ResponseEntity<User>> register(@RequestBody RegisterRequest request) {

        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.setRoles(Collections.singletonList("ROLE_USER"));
        user.setCreatedAt(LocalDateTime.now());
        userRepo.save(user);
        return Mono.just(ResponseEntity.ok(user));
    }

    @PostMapping("/refresh/{refresh}")
    public Mono<ResponseEntity<AuthResponse>> refreshToken(@PathVariable String refresh) {
        return refreshTokenService.getRefreshToken(refresh)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("{} -> {} ",e.getClass().getSimpleName(), e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
                });
    }




}
