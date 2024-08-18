package com.example.webfluxshop.controller;

import com.example.webfluxshop.domain.OrderDetails;
import com.example.webfluxshop.domain.User;
import com.example.webfluxshop.security.UserPrincipal;
import com.example.webfluxshop.service.BasketService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping("api/basket")
public class BasketController {

    private final BasketService basketService;


    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    public Mono<OrderDetails> addToBasket(@PathVariable(name = "id")Long productId,
                                          @RequestBody OrderDetails orderDetails, Mono<Principal> principal) {




        return null;


    }
}
