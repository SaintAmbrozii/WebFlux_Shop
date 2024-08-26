package com.example.webfluxshop.controller;

import com.example.webfluxshop.domain.Category;
import com.example.webfluxshop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("api/category")
@RequiredArgsConstructor
public class CategoriesController {

    private final CategoryService categoryService;

    @GetMapping
    public Flux<Category> getAll() {
        return categoryService.getAll();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Category> create(@RequestPart(name = "data",required = false) Category category,
                                 @RequestPart(name = "file",required = false)Mono<FilePart> filePartMono) {
        return categoryService.create(category,filePartMono);
    }

    @GetMapping("{id}")
    public Mono<Category> findById(@PathVariable(name = "id") Long id) {
        return categoryService.findById(id);
    }

    @PatchMapping("{id}")
    public Mono<Category> update(@PathVariable(name = "id") Long id,@RequestBody Category category) {
        return categoryService.update(id, category);
    }
    @DeleteMapping("{id}")
    public Mono<Void> delete(@PathVariable(name = "id")Long id) {
        return categoryService.deleteById(id);
    }

}
