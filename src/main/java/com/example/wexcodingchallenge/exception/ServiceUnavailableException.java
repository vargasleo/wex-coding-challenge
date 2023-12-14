package com.example.wexcodingchallenge.exception;

import com.example.wexcodingchallenge.api.model.ProblemDetails;
import org.springframework.http.HttpStatus;

public class ServiceUnavailableException extends CustomHttpException {

    public ServiceUnavailableException(ProblemDetails problemDetails) {
        super(HttpStatus.SERVICE_UNAVAILABLE, problemDetails);
    }
}
