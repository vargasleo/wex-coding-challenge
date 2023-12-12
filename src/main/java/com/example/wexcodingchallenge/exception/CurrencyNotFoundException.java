package com.example.wexcodingchallenge.exception;

import com.example.wexcodingchallenge.api.model.ProblemDetails;
import org.springframework.http.HttpStatus;

public class CurrencyNotFoundException extends CustomHttpException {

    public CurrencyNotFoundException(ProblemDetails problemDetails) {
        super(HttpStatus.BAD_REQUEST, problemDetails);
    }
}
