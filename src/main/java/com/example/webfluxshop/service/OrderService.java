package com.example.webfluxshop.service;

import com.example.webfluxshop.domain.Order;
import com.example.webfluxshop.domain.OrderDetails;
import com.example.webfluxshop.domain.Product;
import com.example.webfluxshop.domain.User;
import com.example.webfluxshop.repository.OrderDetailRepo;
import com.example.webfluxshop.repository.OrderRepo;
import com.example.webfluxshop.repository.ProductRepo;
import com.example.webfluxshop.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepo orderRepo;
    private final OrderDetailRepo orderDetailRepo;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Mono<Order> create(Order order, String email) {

        Order newOrder = new Order();
        return userRepo.findByEmail(email).flatMap(user -> {
            Flux<OrderDetails> orderDetailsFlux = orderDetailRepo.findAllByUserId(user.getId());
            List<OrderDetails> orderDetails = new ArrayList<>();
            orderDetailsFlux.collectList().subscribe(orderDetails::addAll);
            List<Long> order_Ids = orderDetails.stream().map(OrderDetails::getId).collect(Collectors.toList());
            Double totalCosts = orderDetails.stream().mapToDouble(OrderDetails::getCost).sum();
            newOrder.setOrderDetails_ids(order_Ids);
            newOrder.setUserId(user.getId());
            newOrder.setCreated_at(LocalDateTime.now());
            newOrder.setConfirmed(true);
            newOrder.setTotalCosts(totalCosts);
            newOrder.setDescription(order.getDescription());
            newOrder.setAddress(order.getAddress());
            orderRepo.save(newOrder);
            return Mono.just(newOrder);

        });

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Mono<Order> update(Long id,Order order,String email) {

        Mono<Order> orderMono = orderRepo.findById(id);
        Mono<User> userMono = userRepo.findByEmail(email);
        return Mono.zip(orderMono,userMono).flatMap(tuple->{
            Order currentOrder = tuple.getT1();
            User authUser = tuple.getT2();
            currentOrder.setAddress(order.getAddress());
            currentOrder.setDescription(order.getDescription());
            currentOrder.setUpdated(LocalDateTime.now());
            return Mono.just(order);

        });
    }

    public Mono<Void> delete(Long id) {
        return orderRepo.deleteById(id);
    }





}
