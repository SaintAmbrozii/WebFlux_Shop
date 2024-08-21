package com.example.webfluxshop.controller;

import com.example.webfluxshop.domain.Order;
import com.example.webfluxshop.service.OrderService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("byUser")
    public Flux<Order> getAllByUserOwner() {
        return orderService.getOrderByUserOwner();
    }

    @PutMapping
    public Mono<Order> create(@RequestBody Order order) {
        return orderService.create(order);
    }
    @PatchMapping("{id}")
    public Mono<Order> update(@PathVariable(name = "id")Long orderId, @RequestBody Order order) {
        return orderService.update(orderId, order);
    }
    @DeleteMapping("{id}")
    public Mono<Void> deleteOrder(@PathVariable(name = "id")Long orderId) {
        return orderService.delete(orderId);
    }
}
