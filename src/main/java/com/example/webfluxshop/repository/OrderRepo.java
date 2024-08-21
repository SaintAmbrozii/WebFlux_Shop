package com.example.webfluxshop.repository;

import com.example.webfluxshop.domain.Order;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface OrderRepo extends R2dbcRepository<Order, Long> {

    Flux<Order> findAllByUserId(Long id);
}
