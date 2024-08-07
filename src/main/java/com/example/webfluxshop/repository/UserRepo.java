package com.example.webfluxshop.repository;

import com.example.webfluxshop.domain.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserRepo extends R2dbcRepository<User, UUID> {

    Mono<User> findByEmail(String email);
}
