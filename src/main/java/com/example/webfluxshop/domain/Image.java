package com.example.webfluxshop.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "images",schema = "public")
@Builder
public class Image  {
    @Id
    @Column("id")
    private Long id;
    @Column("product_id")
    private Long productId;
    @Column("uri")
    private String uri;
    @Column("name")
    private String name;


}
