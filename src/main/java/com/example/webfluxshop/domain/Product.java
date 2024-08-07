package com.example.webfluxshop.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products",schema = "public")
@ToString
public class Product  {

    @Id
    @Column("id")
    private Long id;
    @Column("name")
    private String name;
    @Column("small_desc")
    private String smallDesc;
    @Column("description")
    private String description;
    @Column("quantity")
    private Integer quantity;
    @Column("price")
    private Double price;
    @Column("preview_uri")
    private String preview_uri;
    @Column("category_id")
    private Long categoryId;

    @Transient
    public void addQuantity(int amount) {
        quantity += amount;
    }

    @Transient
    public void decreaseQuantity(int amount) {
        quantity -= amount;
    }




    @Transient
    private List<Image> images = new ArrayList<>();





}
