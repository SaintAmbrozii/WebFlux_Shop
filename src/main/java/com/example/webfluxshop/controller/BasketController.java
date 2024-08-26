package com.example.webfluxshop.controller;

import com.example.webfluxshop.domain.OrderDetails;
import com.example.webfluxshop.domain.User;
import com.example.webfluxshop.security.UserPrincipal;
import com.example.webfluxshop.service.BasketService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping("api/basket")
public class BasketController {

    private final BasketService basketService;


    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    @GetMapping
    public Flux<OrderDetails> getProductInBasketUserOwner() {
        return basketService.getProductInBasketByUserOwner();
    }

    @PostMapping("{id}")
    public Mono<OrderDetails> addToBasket(@PathVariable(name = "id")Long productId,
                                          @RequestBody OrderDetails orderDetails) {
        return basketService.create(productId, orderDetails);
    }

    @PatchMapping("{id}")
    public Mono<OrderDetails> updateQuantity(@PathVariable(name = "id")Long productId,
                                             @RequestBody OrderDetails orderDetails) {
        return basketService.updateQuantity(productId, orderDetails);
    }
    @DeleteMapping("{id}")
    public Mono<Void> deleteById(@PathVariable(name = "id")Long productId) {
        return basketService.deleteOrderDetail(productId);
    }

    @DeleteMapping("clear")
    public Mono<Void> clearAllBasket() {
        return basketService.clearAllBasket();
    }
}
