package com.example.webfluxshop.service;

import com.example.webfluxshop.domain.Order;
import com.example.webfluxshop.domain.OrderDetails;
import com.example.webfluxshop.domain.Product;
import com.example.webfluxshop.domain.User;
import com.example.webfluxshop.exception.NotFoundException;
import com.example.webfluxshop.exception.ProductQuantityException;
import com.example.webfluxshop.repository.OrderDetailRepo;
import com.example.webfluxshop.repository.ProductRepo;
import com.example.webfluxshop.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class BasketService {

    private final OrderDetailRepo orderDetailRepo;
    private final ProductRepo productRepo;
    private final UserService userService;

    public Flux<OrderDetails> getProductInBasketByUserOwner() {
        Mono<User> authUser = userService.getUserInSession();
        return authUser.flatMapMany(user -> {
            return orderDetailRepo.findAllByUserIdAndPayedIsFalse(user.getId());
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = ProductQuantityException.class)
    public Mono<OrderDetails> create (Long productId,OrderDetails orderDetails) {
        Mono<Product> productMono = productRepo.findById(productId)
                .switchIfEmpty(Mono.error(new NotFoundException("данный товар отсутствует в базе")));
        Mono<User> userMono = userService.getUserInSession();
        return Mono.zip(userMono,productMono).flatMap(tulpe->{

            OrderDetails orderDetail = new OrderDetails();
            Product product = tulpe.getT2();
            User user = tulpe.getT1();
            int orderDetailsAmount = orderDetails.getCount();
            if (product.getQuantity()<=orderDetailsAmount) {
                Double costOrderDetail = product.getPrice()* orderDetailsAmount;
                product.decreaseQuantity(orderDetailsAmount);
                productRepo.save(product);
                orderDetail.setProductId(product.getId());
                orderDetail.setProduct_cost(product.getPrice());
                orderDetail.setUserId(user.getId());
                orderDetail.setCost(costOrderDetail);
                orderDetail.setCount(orderDetailsAmount);
                orderDetailRepo.save(orderDetail);
                return Mono.just(orderDetail);
            }
            return Mono.error(new ProductQuantityException("Отсутствует товар в данном количестве"));

        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = ProductQuantityException.class)
    public Mono<OrderDetails> updateQuantity(Long id, OrderDetails orderDetails) {

        Mono<OrderDetails> orderDetailsMono = orderDetailRepo.findById(id);
        Mono<User> userMono = userService.getUserInSession();
        return Mono.zip(orderDetailsMono,userMono).flatMap(tuple->{
            OrderDetails orderDetail = tuple.getT1();
            User user = tuple.getT2();
            int  orderDetailsAmount = orderDetails.getCount();
            Double costOrderDetail = orderDetail.getProduct_cost()* orderDetailsAmount;
            Mono<Product> product = productRepo.findById(orderDetail.getProductId())
                    .switchIfEmpty(Mono.error(new NotFoundException("данный товар отсутствует в базе")));
            product.flatMap(updProduct->{
                if (updProduct.getQuantity()<=orderDetailsAmount && orderDetail.getCount()<orderDetailsAmount) {
                    updProduct.decreaseQuantity(orderDetailsAmount);
                    productRepo.save(updProduct);
                    return Mono.just(updProduct);
                }if (updProduct.getQuantity()<=orderDetailsAmount && orderDetail.getCount()>orderDetailsAmount) {
                    updProduct.addQuantity(orderDetailsAmount);
                    productRepo.save(updProduct);
                    return Mono.just(updProduct);
                }
                else {
                    return Mono.error(new ProductQuantityException("недостаточно количества товара"));
                }
            });
            orderDetail.setCount(orderDetailsAmount);
            orderDetail.setCost(costOrderDetail);
            orderDetailRepo.save(orderDetail);
            return Mono.just(orderDetail);

        });

    }
    @Transactional(propagation = Propagation.REQUIRES_NEW,rollbackFor = NotFoundException.class)
    public Mono<Void> clearAllBasket() {

        Mono<User> authUser = userService.getUserInSession();

        return authUser.flatMap(user -> {
            Flux<OrderDetails> toUserBasket = orderDetailRepo.findAllByUserIdAndPayedIsFalse(user.getId());
            toUserBasket.flatMap(orderDetails -> {
                Mono<Product> product = productRepo.findById(orderDetails.getProductId())
                        .switchIfEmpty(Mono.error(new NotFoundException("данный товар отсутствует в базе")));
                product.flatMap(updProduct->{
                    updProduct.addQuantity(orderDetails.getCount());
                    productRepo.save(updProduct);
                    return Mono.just(updProduct);
                });
              orderDetailRepo.deleteById(orderDetails.getId());
              return Flux.empty();
            });
            return Mono.empty();
        });

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Mono<Void> deleteOrderDetail(Long id) {
        return orderDetailRepo.findById(id).flatMap(orderDetails -> {

            Mono<Product> productMono = productRepo.findById(orderDetails.getProductId());
            productMono.flatMap(product -> {
                product.addQuantity(orderDetails.getCount());
                productRepo.save(product);
                return Mono.just(product);
            });
            return orderDetailRepo.deleteById(orderDetails.getOrderId());

        });
    }




}

