package com.example.webfluxshop.repository;

import com.example.webfluxshop.domain.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;


@Repository
public interface ProductRepo extends ReactiveCrudRepository<Product,Long> {


    @Query("Select * from products order by id desc LIMIT $1 OFFSET $2")
    Flux<Product> findAllPageable(int limit, long offset);


    Flux<Product> findAllByName (String name);

    Flux<Product> findAllById (List<Long> uuids);
}
