package com.example.webfluxshop.controller;

import com.example.webfluxshop.domain.Order;
import com.example.webfluxshop.domain.OrderDetails;
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

    @GetMapping("byUserList")
    public Flux<Order> getAllByUserOwner() {
        return orderService.getAllOrderByUserOwner();
    }
    @GetMapping("{id}")
    public Mono<Order> getByIdUserOwner(@PathVariable(name = "id")Long id) {
        return orderService.getOrderByUser(id);
    }

    @PutMapping
    public Mono<Order> create() {
        return orderService.create();
    }

    @PatchMapping("{id}")
    public Mono<Order> confirmed(@PathVariable(name = "id")Long orderId,
                                 @RequestPart Order order,
                                 @RequestPart OrderDetails orderDetails) {
        return orderService.confirmed(orderId, order,orderDetails);
    }
    @DeleteMapping("{id}")
    public Mono<Void> deleteOrder(@PathVariable(name = "id")Long orderId) {
        return orderService.delete(orderId);
    }
}
