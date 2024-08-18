package com.example.webfluxshop.exception;

public class UserNotAuthorizedException extends RuntimeException{

    public UserNotAuthorizedException() {
        super();
    }

    public UserNotAuthorizedException(String message) {
        super(message);
    }

    public UserNotAuthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
