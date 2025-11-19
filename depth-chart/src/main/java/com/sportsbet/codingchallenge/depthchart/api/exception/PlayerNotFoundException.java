package com.sportsbet.codingchallenge.depthchart.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code= HttpStatus.NOT_FOUND)
public class PlayerNotFoundException extends RuntimeException{

    public PlayerNotFoundException(String message) {
        super(message);
    }
}
