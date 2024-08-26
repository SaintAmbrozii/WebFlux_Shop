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
import java.time.ZonedDateTime;
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
    private final UserService userService;


    public Flux<Order> findAll() {
        return orderRepo.findAll();
    }

    public Flux<Order> getOrderByUserOwner() {
        Mono<User> authUser = userService.getUserInSession();
        return authUser.flatMapMany(user -> {
            return orderRepo.findAllByUserId(user.getId());
        });
    }


    public Mono<Order> create() {
        Order newOrder = new Order();
        Mono<User> authUser = userService.getUserInSession();
        return authUser.flatMap(user -> {
            Flux<OrderDetails> orderDetailsFlux = orderDetailRepo.findAllByUserIdAndPayedIsFalse(user.getId());
            List<OrderDetails> orderDetails = new ArrayList<>();
            orderDetailsFlux.collectList().subscribe(orderDetails::addAll);
            List<Long> order_Ids = orderDetails.stream().map(OrderDetails::getId).collect(Collectors.toList());
            Double totalCosts = orderDetails.stream().mapToDouble(OrderDetails::getCost).sum();
            newOrder.setOrderDetails_ids(order_Ids);
            newOrder.setUserId(user.getId());
            newOrder.setCreated_at(ZonedDateTime.now());
            newOrder.setTotalCosts(totalCosts);
            orderRepo.save(newOrder);
            return Mono.just(newOrder);

        });

    }



    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Mono<Order> confirmed(Long id,Order order) {

        Mono<Order> orderMono = orderRepo.findById(id);
        Mono<User> userMono = userService.getUserInSession();
        return Mono.zip(orderMono,userMono).flatMap(tuple->{
            Order currentOrder = tuple.getT1();
            User authUser = tuple.getT2();
            currentOrder.setAddress(order.getAddress());
            currentOrder.setDescription(order.getDescription());
            currentOrder.setUpdated(LocalDateTime.now());
            Flux<OrderDetails> inBasketDetails = orderDetailRepo.findAllByUserIdAndPayedIsFalse(authUser.getId());
            inBasketDetails.flatMap(orderDetails -> {
                OrderDetails confirmedOrderDetail = OrderDetails.builder()
                        .orderId(currentOrder.getId())
                        .payed(true).build();
                return orderDetailRepo.save(confirmedOrderDetail);
            });
            currentOrder.setConfirmed(true);
            currentOrder.setDescription(order.getDescription());
            currentOrder.setAddress(order.getAddress());
            return Mono.just(currentOrder);

        });
    }

    public Mono<Void> delete(Long id) {
        return orderRepo.deleteById(id);
    }







}
