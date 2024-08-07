package com.example.webfluxshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

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
