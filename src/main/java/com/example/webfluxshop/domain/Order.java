package com.example.webfluxshop.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Order {

    @Id
    private Long id;

    private Long userId;

    private Boolean confirmed;

    private Double totalCosts;

    private String description;

    private String address;

    private String email;

    private LocalDateTime created_at;

    private LocalDateTime updated;

    @Column("order_details_ids")
    private List<Long> orderDetails_ids;




}
