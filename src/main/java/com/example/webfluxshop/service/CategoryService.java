package com.example.webfluxshop.service;

import com.example.webfluxshop.domain.Category;
import com.example.webfluxshop.domain.Image;
import com.example.webfluxshop.exception.BadResourceLocationException;
import com.example.webfluxshop.repository.CategoryRepo;
import com.example.webfluxshop.repository.ImageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    @Value("{files.location}")
    private String fileLocation;

    private final CategoryRepo categoryRepo;
    private final ImageRepo imageRepo;

    public Flux<Category> getAll() {
        return categoryRepo.findAll();
    }

    public Mono<Category> create(Category category, Mono<FilePart> filePart) {
        if (filePart == null) {
            return Mono.just(category).flatMap(newCategory->{
                newCategory.setName(category.getName());
                newCategory.setDescription(category.getDescription());
                categoryRepo.save(newCategory);
                return Mono.just(newCategory);
            });
        } else {
            return filePart.flatMap(file -> {

                Category newCategory = new Category();

                String randomName = UUID.randomUUID()
                        .toString().substring(0, 13);

                String newFileName = randomName+"_"+file.filename();
                String filePath = Paths.get(fileLocation,newFileName).normalize().toAbsolutePath()
                        .toString();

                newCategory.setPreview_uri(filePath);
                newCategory.setName(category.getName());
                newCategory.setDescription(category.getDescription());

                return categoryRepo.save(newCategory)
                        .doOnNext(savedFile-> file.transferTo(Path.of(filePath))
                                .subscribe())
                        .then(Mono.just(newCategory))
                        .switchIfEmpty(Mono.error(new BadResourceLocationException("ошибка пути хранилища")));

            });
        }

    }

    public Mono<Category> createByParentId(@PathVariable(name = "id") Long id, @RequestPart(name = "data") Category category) {
        return categoryRepo.findById(id).flatMap(newCategory->{
            newCategory.setName(category.getName());
            newCategory.setDescription(category.getDescription());
            newCategory.setParent_Id(newCategory.getId()+1L);
            return Mono.just(newCategory);

        });
    }


    public Mono<Category> update(Long id,Category category) {
        return categoryRepo.findById(id).flatMap(updateCategory->{
            updateCategory.setName(category.getName());
            updateCategory.setDescription(category.getDescription());
            updateCategory.setPreview_uri(category.getPreview_uri());
            updateCategory.setParent_Id(category.getParent_Id());
            categoryRepo.save(updateCategory);
            return Mono.just(updateCategory);
        });
    }
    public Mono<Category> findById(Long id) {
        return categoryRepo.findById(id);
    }

    public Mono<Void> deleteById(Long id) {
        return categoryRepo.deleteById(id);
    }
}
