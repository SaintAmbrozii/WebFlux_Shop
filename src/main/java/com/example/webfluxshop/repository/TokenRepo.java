package com.example.webfluxshop.repository;


import com.example.webfluxshop.domain.RefreshToken;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface TokenRepo extends ReactiveCrudRepository<RefreshToken,Long> {

    Mono<RefreshToken> findByToken(String token);

}
