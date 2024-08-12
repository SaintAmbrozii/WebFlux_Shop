package com.example.webfluxshop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperty {

    private String secret;
    private long accessExpiration;
    private long refreshExpiration;
}
