package com.example.webfluxshop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class BadResourceLocationException extends RuntimeException{
    public BadResourceLocationException(String msg) {
        super(msg);

    }

    public BadResourceLocationException(String msg, IOException ex) {
        super(msg, ex);
    }
}
