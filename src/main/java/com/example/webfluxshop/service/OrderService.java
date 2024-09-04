package com.example.webfluxshop.service;

import com.example.webfluxshop.domain.Order;
import com.example.webfluxshop.domain.OrderDetails;
import com.example.webfluxshop.domain.Product;
import com.example.webfluxshop.domain.User;
import com.example.webfluxshop.repository.OrderDetailRepo;
import com.example.webfluxshop.repository.OrderRepo;
import com.example.webfluxshop.repository.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
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

    public Flux<OrderDetails> getByOrderId(Long id) {
        Mono<Order> orderMono = orderRepo.findById(id);
        return orderMono.flatMapMany(order -> {
           return orderDetailRepo.findAllById(order.getOrderDetails_ids());
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
            newOrder.setCreated_at(LocalDateTime.now());
            newOrder.setTotalCosts(totalCosts);
            newOrder.getOrderDetailsList().addAll(orderDetails);
            orderRepo.save(newOrder);
            return Mono.just(newOrder);

        });

    }



    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Mono<Order> confirmed(Long id,Order order, OrderDetails detail) {

        Mono<Order> orderMono = orderRepo.findById(id);
        Mono<User> userMono = userService.getUserInSession();
        return Mono.zip(orderMono,userMono).flatMap(tuple->{
            Order currentOrder = tuple.getT1();
            User authUser = tuple.getT2();

            Flux<OrderDetails> orderDetailsFlux = orderDetailRepo.findAllById(currentOrder.getOrderDetails_ids());

            Flux<OrderDetails> updateInOrder = updatedOrderDetails(orderDetailsFlux,detail);

            List<OrderDetails> orderUpdated = new ArrayList<>();

            orderDetailsFlux.collectList().subscribe(orderUpdated::addAll);


            List<Long> currentDetails = orderUpdated.stream().map(OrderDetails::getId).collect(Collectors.toList());
            Double orderAmount = orderUpdated.stream().mapToDouble(OrderDetails::getCost).sum();



            updateInOrder.flatMap(orderDetails -> {

                OrderDetails confirmedOrderDetail = OrderDetails.builder()
                        .orderId(currentOrder.getId())
                        .payed(true).build();
                return orderDetailRepo.save(confirmedOrderDetail);
            });


            currentOrder.setTotalCosts(orderAmount);
            currentOrder.setOrderDetails_ids(currentDetails);
            currentOrder.setUpdated(LocalDateTime.now());
            currentOrder.setConfirmed(true);
            currentOrder.setDescription(order.getDescription());
            currentOrder.setAddress(order.getAddress());
            orderRepo.save(currentOrder);
            return Mono.just(currentOrder);

        });
    }

    public Mono<Void> cancelOrder(Long id) {
        Mono<Order> findById = orderRepo.findById(id);
        Mono<User> userMono = userService.getUserInSession();
        return Mono.zip(findById,userMono).flatMap(tulpe->{
            Order order = tulpe.getT1();
            User user = tulpe.getT2();
            List<Long> detailIds = order.getOrderDetails_ids();
            Flux<OrderDetails> orderDetailsFlux = orderDetailRepo.findAllById(detailIds);
            orderDetailsFlux.flatMap(orderDetails -> {
                Mono<Product> productMono = productRepo.findById(orderDetails.getProductId());
                productMono.flatMap(product -> {
                    product.addQuantity(orderDetails.getCount());
                    productRepo.save(product);
                    return Mono.just(product);
                });
                orderRepo.deleteById(id);
               return Flux.empty();
            });

            return Mono.empty();

        });
    }

    public Mono<Void> delete(Long id) {
        return orderRepo.deleteById(id);
    }



    private Mono<Order> validateUserId(Mono<Order> orderMono, User user) {
        return orderMono.flatMap(dbOrder -> {
            if (!dbOrder.getUserId().equals(user.getId())) {
                return Mono.error(new IllegalArgumentException("Not authorized to save this order"));
            }
            return orderMono;
        });
    }



    private Flux<OrderDetails> updatedOrderDetails (Flux<OrderDetails> detailsFlux,OrderDetails details) {

      detailsFlux.flatMap(orderDetails -> {
            Mono<Product> product = productRepo.findById(orderDetails.getProductId());
            product.flatMap(prod -> {
                Product pr = new Product();
                if (details.getCount()>prod.getQuantity()) {
                    pr.decreaseQuantity(details.getCount());
                    productRepo.save(pr);
                }
                if (details.getCount()<prod.getQuantity()) {
                    pr.addQuantity(details.getCount());
                    productRepo.save(pr);
                }
                if (details.getCount()==0) {
                    pr.addQuantity(prod.getQuantity());
                    orderDetailRepo.deleteById(orderDetails.getId());
                    productRepo.save(pr);
                }
                return Mono.just(prod);
            });
            return Flux.just(detailsFlux);
        });

        return detailsFlux;
    }


}
