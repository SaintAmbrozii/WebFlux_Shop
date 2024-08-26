package com.example.webfluxshop.repository;

import com.example.webfluxshop.domain.Order;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface OrderRepo extends ReactiveCrudRepository<Order, Long> {

    Flux<Order> findAllByUserId(Long id);
}
