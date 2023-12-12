package com.example.wexcodingchallenge.exception;

import com.example.wexcodingchallenge.api.model.ProblemDetails;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.UUID;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ProblemDetails> methodArgumentNotValid(MethodArgumentNotValidException exception) {
        StringBuilder detailsBuilder = new StringBuilder();

        for (final var error : exception.getBindingResult().getFieldErrors()) {
            detailsBuilder.append("Field: ")
                    .append(error.getField())
                    .append(": ")
                    .append(error.getDefaultMessage())
                    .append("; ");
        }

        final var details = detailsBuilder.toString();

        final var problemDetails = new ProblemDetails(
                "The request is malformed.",
                details,
                UUID.randomUUID().toString());
        log.warn("[{}] " + problemDetails.getTitle(), problemDetails.getLogReference(), exception.getLocalizedMessage());
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problemDetails);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ProblemDetails> messageNotReadable(HttpMessageNotReadableException exception) {

        Throwable rootCause = exception;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }

        final var detailsMessage = rootCause.getMessage() != null ? rootCause.getMessage() : "Malformed request body.";

        final var problemDetails = new ProblemDetails(
                "The request is malformed.",
                detailsMessage,
                UUID.randomUUID().toString());
        log.warn("[{}] " + problemDetails.getTitle(), problemDetails.getLogReference(), exception.getLocalizedMessage());
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problemDetails);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ProblemDetails> constraintViolation(ConstraintViolationException exception) {
        final var detailsBuilder = new StringBuilder();

        for (final var violation : exception.getConstraintViolations()) {
            detailsBuilder.append(violation.getPropertyPath())
                    .append(": ")
                    .append(violation.getMessage())
                    .append("; ");
        }

        final var detailsMessage = detailsBuilder.toString();

        final var problemDetails = new ProblemDetails(
                "The request is malformed.",
                detailsMessage,
                UUID.randomUUID().toString());
        log.warn("[{}] " + problemDetails.getDetails(), problemDetails.getLogReference());
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problemDetails);
    }

    @ExceptionHandler(CustomHttpException.class)
    ResponseEntity<ProblemDetails> customHttpException(CustomHttpException exception) {
        final var details = exception.getProblemDetails();
        log.warn("[{}] " + details.getDetails(), details.getLogReference());
        return ResponseEntity.status(exception.getHttpStatus())
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(details);
    }

    @ExceptionHandler(Throwable.class)
    ResponseEntity<ProblemDetails> internalServerError(Throwable exception) {
        final var details = new ProblemDetails(
                "Something unexpected happened.",
                "Check service availability, try again or contact support.",
                UUID.randomUUID().toString());
        log.warn("[{}] " + details.getTitle(), details.getLogReference(), exception);
        return ResponseEntity.internalServerError()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(details);
    }
}
