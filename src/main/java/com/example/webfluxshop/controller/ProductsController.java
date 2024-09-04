package com.example.webfluxshop.controller;

import com.example.webfluxshop.domain.Product;
import com.example.webfluxshop.dto.ProductDto;
import com.example.webfluxshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    @GetMapping("/page")
    public Mono<Page<Product>> getProducts(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize) {
        return productService.findAllProductsPaged(PageRequest.of(page, pageSize));
    }

    @PostMapping
    public Mono<Product> create(@RequestBody Product product) {
        return productService.create(product);
    }
    @PatchMapping("{id}")
    public Mono<Product> update(@PathVariable(name = "id")Long productId,
                                @RequestBody ProductDto product) {
        return productService.updateProduct(productId,product);
    }

    @PatchMapping("{id}/images")
    public Mono<Product> uploadImage(@PathVariable(name = "id")Long id,@RequestPart Flux<FilePart> files) {
        return productService.uploadImages(id, files);
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
