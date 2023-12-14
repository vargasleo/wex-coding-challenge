package com.example.wexcodingchallenge.exception;

import com.example.wexcodingchallenge.api.model.ProblemDetails;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.NONE)
public class CustomHttpExceptionFactory {

    public static Supplier<NotFoundException> notFound(String title, String details) {
        return createException(NotFoundException::new, title, details);
    }

    public static Supplier<ServiceUnavailableException> serviceUnavailable(String title, String details) {
        return createException(ServiceUnavailableException::new, title, details);
    }

    public static Supplier<BadRequestException> badRequest(String title, String details) {
        return createException(BadRequestException::new, title, details);
    }

    public static Supplier<CurrencyNotFoundException> currencyNotFound(String targetCurrency, String exchangeRateDateStart, String transactionDate) {
        final var title = "No exchange rate for provided time interval and currency.";
        final var detail = "There's no exchange rate for '" + targetCurrency + "' between " + exchangeRateDateStart + " and " + transactionDate + ".";
        return createException(CurrencyNotFoundException::new, title, detail);
    }

    private static <T extends CustomHttpException> Supplier<T> createException(
            Function<ProblemDetails, T> exceptionConstructor, String title, String details) {
        return () -> {
            final var problemDetails = new ProblemDetails(title, details, UUID.randomUUID().toString());
            return exceptionConstructor.apply(problemDetails);
        };
    }
}
