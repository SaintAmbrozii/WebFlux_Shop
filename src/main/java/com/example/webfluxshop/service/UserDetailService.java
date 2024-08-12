package com.example.webfluxshop.service;


import com.example.webfluxshop.repository.UserRepo;
import com.example.webfluxshop.security.UserPrincipal;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserDetailService implements ReactiveUserDetailsService {

    private final UserRepo userRepo;

    public UserDetailService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }


    @Override
    public Mono<UserDetails> findByUsername(String email) {
        return userRepo.findByEmail(email).map(UserPrincipal::create);

    }
}
