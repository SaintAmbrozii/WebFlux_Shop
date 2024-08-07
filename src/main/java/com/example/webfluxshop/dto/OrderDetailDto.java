package com.example.webfluxshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailDto {

    private Long id;

    private Long userId;

    private Long productId;

    private Long orderId;

    private Integer count;

    private Double cost;
}
