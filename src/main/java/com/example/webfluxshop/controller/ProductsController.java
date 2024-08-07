package com.example.webfluxshop.controller;

import com.example.webfluxshop.domain.Product;
import com.example.webfluxshop.dto.ProductDto;
import com.example.webfluxshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/products")
@RequiredArgsConstructor
public class ProductsController {

    private final ProductService productService;



    @GetMapping
    public Flux<Product> findAll() {
        return productService.findAll();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Product> create(@RequestPart(name = "data")Product product,
                                @RequestPart(name = "files",required = false)Flux<FilePart> files) {
        return productService.create(product, files);
    }
    @PutMapping("{id}")
    public Mono<Product> update(@PathVariable(name = "id")Long productId,
                                @RequestBody ProductDto product) {
        return productService.updateProduct(productId,product);
    }

    @GetMapping("{id}")
    public Mono<Product> findById(@PathVariable(name = "id")Long id) {
        return productService.findById(id);
    }

    @DeleteMapping("{id}")
    public Mono<Void> deleteById(@PathVariable(name = "id")Long id) {
        return productService.deleteById(id);
    }
}
