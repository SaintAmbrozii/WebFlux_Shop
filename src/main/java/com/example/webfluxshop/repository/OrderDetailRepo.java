package com.example.webfluxshop.repository;

import com.example.webfluxshop.domain.OrderDetails;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface OrderDetailRepo extends R2dbcRepository<OrderDetails,Long> {

    Flux<OrderDetails> findAllByUserId(Long id);

    Flux<OrderDetails> findAllByOrderId(Long id);
}
