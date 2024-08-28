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
import java.util.*;
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
    private final ProductRepo productRepo;


    public Flux<Order> findAll() {
        return orderRepo.findAll();
    }

    public Flux<Order> getAllOrderByUserOwner() {
        Mono<User> authUser = userService.getUserInSession();
        return authUser.flatMapMany(user -> {
            return orderRepo.findAllByUserId(user.getId());
        });
    }

    public Mono<Order> getOrderByUser(Long id) {
        Mono<User> authUser = userService.getUserInSession();
        Mono<Order> orderMono = orderRepo.findById(id);
        return authUser.flatMap(user -> {
            validateUserId(orderMono,user);
            return orderMono;
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
            newOrder.getOrderDetailsList().addAll(orderDetails);
            orderRepo.save(newOrder);
            return Mono.just(newOrder);

        });

    }



    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Mono<Order> confirmed(Long id,Order order, OrderDetails orderDetail) {

        Mono<Order> orderMono = orderRepo.findById(id);
        Mono<User> userMono = userService.getUserInSession();
        return Mono.zip(orderMono,userMono).flatMap(tuple->{
            Order currentOrder = tuple.getT1();
            User authUser = tuple.getT2();

            HashSet<Long> detailsIds = new HashSet<>();

            currentOrder.getOrderDetails_ids().forEach(i->{
                 detailsIds.addAll(currentOrder.getOrderDetails_ids());
            });

            List<OrderDetails> deleteOrderDetails = currentOrder.getOrderDetailsList().stream()
                    .filter(i->!detailsIds.contains(i.getId())).collect(Collectors.toList());
            if (deleteOrderDetails.size()>0) {
                deleteOrderDetails(deleteOrderDetails);
            }

            deleteOrderDetails.forEach(orderDetails -> {
                Mono<Product> product = productRepo.findById(orderDetails.getProductId());
                product.flatMap(prod -> {
                    Product pr = new Product();
                    if (orderDetail.getCount()>prod.getQuantity())
                    pr.decreaseQuantity(orderDetail.getCount());
                    if (orderDetail.getCount()<prod.getQuantity());
                    pr.addQuantity(orderDetail.getCount());
                   return productRepo.save(pr);

                });
            });

            Flux<OrderDetails> inBasketDetails = Flux.fromIterable(deleteOrderDetails);

            inBasketDetails.flatMap(orderDetails -> {

                OrderDetails confirmedOrderDetail = OrderDetails.builder()
                        .orderId(currentOrder.getId())
                        .payed(true).build();
                return orderDetailRepo.save(confirmedOrderDetail);
            });


            currentOrder.setUpdated(LocalDateTime.now());
            currentOrder.setConfirmed(true);
            currentOrder.setDescription(order.getDescription());
            currentOrder.setAddress(order.getAddress());
            return Mono.just(currentOrder);

        });
    }

    public Mono<Void> delete(Long id) {
        return orderRepo.deleteById(id);
    }

    private Mono<Order> validateUserId(Order cart, Mono<Order> cartMono, User user) {

        if (cartMono != null) {
            return validateUserId(cartMono, user);
        }
        return Mono.just(cart);
    }

    private Mono<Order> validateUserId(Mono<Order> cart, User user) {
        return cart.flatMap(dbCart -> {
            if (!dbCart.getUserId().equals(user.getId())) {
                throw new IllegalArgumentException("Not authorized to save this order");
            }
            return cart;
        });
    }

    private Mono<Void> deleteOrderDetails(List<OrderDetails> orderDetailsList) {
        return orderDetailRepo.deleteAll(orderDetailsList);
    }






}
