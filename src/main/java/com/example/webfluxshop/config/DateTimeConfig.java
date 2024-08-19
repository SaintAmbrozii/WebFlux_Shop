package com.example.webfluxshop.config;

import com.example.webfluxshop.util.ZoneDateTimeUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.time.ZoneId;

@Configuration
public class DateTimeConfig implements WebFluxConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {

        registry.addConverter(new ZoneDateTimeUtil(ZoneId.systemDefault()));
    }
}
