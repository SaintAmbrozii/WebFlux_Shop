package com.example.webfluxshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    private Long orderId;
    private Long userId;
    private OrderDetailDto orderDetail;
}
