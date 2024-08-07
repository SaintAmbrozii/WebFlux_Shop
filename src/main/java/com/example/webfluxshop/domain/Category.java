package com.example.webfluxshop.domain;

import io.r2dbc.spi.Parameter;
import lombok.*;
import org.springframework.beans.propertyeditors.UUIDEditor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "category",schema = "public")
@ToString
public class Category {
    @Id
    @Column("id")
    private Long id;
    @Column("name")
    private String name;
    @Column("description")
    private String description;
    @Column("preview_uri")
    private String preview_uri;
    @Column("parent_id")
    private Long parent_Id;

    @Transient
    private List<Product> products;

}
