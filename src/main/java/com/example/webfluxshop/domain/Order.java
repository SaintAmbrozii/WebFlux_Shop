package com.example.webfluxshop.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "orders",schema = "public")
public class Order {

    @Id
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("confirmed")
    private Boolean confirmed;

    @Column("total_costs")
    private Double totalCosts;

    @Column("description")
    private String description;

    @Column("address")
    private String address;

    @Column("email")
    private String email;

    @Column("created_at")
    private ZonedDateTime created_at;

    @Column("updated")
    private LocalDateTime updated;

    @Column("order_details_ids")
    private List<Long> orderDetails_ids;

    @Transient
    private List<OrderDetails> orderDetailsList;




}
