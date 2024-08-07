package com.example.webfluxshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableWebFlux
@SpringBootApplication
public class WebFluxShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebFluxShopApplication.class, args);
    }

}
