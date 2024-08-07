package com.example.webfluxshop.repository;

import com.example.webfluxshop.domain.Image;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

@Repository
public interface ImageRepo extends R2dbcRepository<Image, Long> {

   Flux<Image> getAllByProductId(Long id);

}
