package com.example.webfluxshop.dto;

import com.example.webfluxshop.domain.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {


    private Long id;

    private Long category_id;

    private String name;

    private String smallDesc;

    private String description;

    private Integer quantity;

    private Double price;

    private String preview_uri;


}
