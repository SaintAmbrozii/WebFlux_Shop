package com.example.webfluxshop.domain;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_details",schema = "public")
@Builder
@ToString
public class OrderDetails {

    @Id
    @Column("id")
    private Long id;
    @Column("user_id")
    private Long userId;
    @Column("product_id")
    private Long productId;
    @Column("product_cost")
    private Double product_cost;
    @Column("order_id")
    private Long orderId;
    @Column("count")
    private Integer count;
    @Column("cost")
    private Double cost;
    @Column("payed")
    private Boolean payed;

    @Transient
    private Product product;


}
