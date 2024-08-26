package com.example.webfluxshop.repository;

import com.example.webfluxshop.domain.OrderDetails;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface OrderDetailRepo extends ReactiveCrudRepository<OrderDetails,Long> {

    Flux<OrderDetails> findAllByUserIdAndPayedIsFalse(Long id);

    Flux<OrderDetails> findAllByOrderId(Long id);
}
