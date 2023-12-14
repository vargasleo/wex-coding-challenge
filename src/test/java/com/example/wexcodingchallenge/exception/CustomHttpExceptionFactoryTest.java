package com.example.wexcodingchallenge.exception;

import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static com.example.wexcodingchallenge.exception.CustomHttpExceptionFactory.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomHttpExceptionFactoryTest {

    @Test
    void notFoundExceptionIsCreatedCorrectly() {
        final var title = "Not Found Title.";
        final var details = "Not Found Details.";

        Supplier<NotFoundException> exceptionSupplier = notFound(title, details);
        final var exception = exceptionSupplier.get();

        assertEquals(title, exception.getProblemDetails().getTitle());
        assertEquals(details, exception.getProblemDetails().getDetails());
    }

    @Test
    void serviceUnavailableExceptionIsCreatedCorrectly() {
        final var title = "Service Unavailable Title.";
        final var details = "Service Unavailable Details.";

        Supplier<ServiceUnavailableException> exceptionSupplier = CustomHttpExceptionFactory.serviceUnavailable(title, details);
        final var exception = exceptionSupplier.get();

        assertEquals(title, exception.getProblemDetails().getTitle());
        assertEquals(details, exception.getProblemDetails().getDetails());
    }

    @Test
    void badRequestExceptionIsCreatedCorrectly() {
        final var title = "Bad Request Title.";
        final var details = "Bad Request Details.";

        Supplier<BadRequestException> exceptionSupplier = badRequest(title, details);
        final var exception = exceptionSupplier.get();

        assertEquals(title, exception.getProblemDetails().getTitle());
        assertEquals(details, exception.getProblemDetails().getDetails());
    }

    @Test
    void currencyNotFoundExceptionIsCreatedCorrectly() {
        final var targetCurrency = "Brazil-Real";
        final var exchangeRateDateStart = "2023-01-01";
        final var transactionDate = "2023-01-02";
        final var expectedTitle = "No exchange rate for provided time interval and currency.";
        final var expectedDetail = "There's no exchange rate for 'Brazil-Real' between 2023-01-01 and 2023-01-02.";

        Supplier<CurrencyNotFoundException> exceptionSupplier = currencyNotFound(targetCurrency, exchangeRateDateStart, transactionDate);
        final var exception = exceptionSupplier.get();

        assertEquals(expectedTitle, exception.getProblemDetails().getTitle());
        assertEquals(expectedDetail, exception.getProblemDetails().getDetails());
    }
}
