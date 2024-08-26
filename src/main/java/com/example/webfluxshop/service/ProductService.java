package com.example.webfluxshop.service;

import com.example.webfluxshop.domain.Image;
import com.example.webfluxshop.domain.Product;
import com.example.webfluxshop.dto.ProductDto;
import com.example.webfluxshop.repository.CategoryRepo;
import com.example.webfluxshop.repository.ImageRepo;
import com.example.webfluxshop.repository.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.util.function.Tuple2;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static reactor.core.publisher.Mono.*;

@Service
@RequiredArgsConstructor
public class ProductService {

    @Value("{files.location}")
    private String fileLocation;

    private final ProductRepo productRepo;
    private final ImageRepo imageRepo;

    public Flux<Product> findAll() {
        return productRepo.findAll();
    }


    public Mono<Page<Product>> findAllProductsPaged(Pageable pageable) {
        Mono<List<Product>> products = productRepo.findAllPageable(pageable.getPageSize(), pageable.getOffset()).collectList();
        Mono<Long> totalProductsCount = productRepo.count();
        return products.flatMap(productList ->
                totalProductsCount.flatMap(totalCount -> Mono.just(new PageImpl<Product>(productList, pageable, totalCount)))
        );
    }


    public Mono<Product> create(Product product) {

        return Mono.just(product).flatMap(createProduct -> {

            createProduct.setCategoryId(product.getCategoryId());
            createProduct.setName(product.getName());
            createProduct.setSmallDesc(product.getSmallDesc());
            createProduct.setDescription(product.getDescription());
            createProduct.setQuantity(product.getQuantity());
            createProduct.setPrice(product.getPrice());
            productRepo.save(createProduct);

            return Mono.just(createProduct);

        });
    }

    public Mono<Product> updateProduct(Long id, ProductDto productDto) {
        return productRepo.findById(id).flatMap(product -> {
            product.setCategoryId(productDto.getCategory_id());
            product.setName(productDto.getName());
            product.setSmallDesc(productDto.getSmallDesc());
            product.setDescription(productDto.getDescription());
            product.setPrice(productDto.getPrice());
            product.setQuantity(productDto.getQuantity());
        return  productRepo.save(product);
        });
    }

    public Mono<Product> uploadImages(Long id,Flux<FilePart> fileParts) {
        Mono<Product> productMono = productRepo.findById(id);

        fileParts.zipWith(productMono).flatMap(filePart -> {

            Product product = filePart.getT2();
            FilePart file = filePart.getT1();
            String filename = file.filename();

            String randomName = UUID.randomUUID()
                    .toString().substring(0, 13);

            String newFileName = randomName+"_"+ filename;
            String filePath = Paths.get(fileLocation,newFileName).normalize().toAbsolutePath()
                    .toString();

            Image addImage = Image.builder().name(newFileName).
                    productId(product.getId()).build();
           return imageRepo.save(addImage).doOnNext(f->file.transferTo(Path.of(filePath)).subscribe())
                    .then(Mono.just(addImage));
        });

        return productMono;

    }





    public Mono<Product> findById (Long id) {
        return productRepo.findById(id);

    }

    public Mono<Void> deleteById(Long id) {
        return productRepo.deleteById(id);
    }






}











 //   String randomName = UUID.randomUUID()
//            .toString().substring(0, 13);
//    String newFileName = randomName+"_"+filePart.filename();
//    String filePath = Paths.get(fileLocation,newFileName).normalize().toAbsolutePath()
 ////           .toString();
 //   Image productImage = Image.builder().productId(createProduct.getId()).name(newFileName).uri(filePath).build();
 /// /           imageRepo.save(productImage).doOnNext(savedFile-> filePart.transferTo(Path.of(filePath))
  //          .subscribe()).then(Mono.just(productImage))
 //           .switchIfEmpty(Mono.error(new BadResourceLocationException("ошибка пути хранилища")));



