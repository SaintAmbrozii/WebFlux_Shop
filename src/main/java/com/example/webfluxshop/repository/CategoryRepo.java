package com.example.webfluxshop.repository;

import com.example.webfluxshop.domain.Category;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryRepo extends ReactiveCrudRepository<Category, Long> {
}
