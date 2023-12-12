package com.example.wexcodingchallenge.exception;

import com.example.wexcodingchallenge.api.model.ProblemDetails;
import org.springframework.http.HttpStatus;

public class NotFoundException extends CustomHttpException {

    public NotFoundException(ProblemDetails problemDetails) {
        super(HttpStatus.NOT_FOUND, problemDetails);
    }
}
