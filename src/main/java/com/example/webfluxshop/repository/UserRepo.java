package com.example.webfluxshop.repository;

import com.example.webfluxshop.domain.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface UserRepo extends ReactiveCrudRepository<User, Long> {

    Mono<User> findByEmail(String email);
}
