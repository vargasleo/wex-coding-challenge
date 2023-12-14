package com.example.wexcodingchallenge.exception;

import com.example.wexcodingchallenge.api.model.ProblemDetails;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public abstract class CustomHttpException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final ProblemDetails problemDetails;
}
