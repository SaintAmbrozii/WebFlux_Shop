package com.example.webfluxshop.service;

import com.example.webfluxshop.domain.Image;
import com.example.webfluxshop.repository.ImageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepo imageRepo;

    public Flux<Image> findAll() {
        return imageRepo.findAll();
    }

    public Flux<Image> findAllByProductId(Long id) {
        return imageRepo.getAllByProductId(id);
    }

    public Mono<Image> findById(Long id){
        return imageRepo.findById(id);
    }
}
