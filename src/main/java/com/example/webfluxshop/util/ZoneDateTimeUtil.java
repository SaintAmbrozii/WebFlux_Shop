package com.example.webfluxshop.util;

import org.springframework.core.convert.converter.Converter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ZoneDateTimeUtil implements Converter<String, ZonedDateTime> {

    private final ZoneId zoneId;

    public ZoneDateTimeUtil(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    @Override
    public ZonedDateTime convert(String source) {
        long startTime = Long.parseLong(source);
        return Instant.ofEpochSecond(startTime).atZone(zoneId);
    }
}
