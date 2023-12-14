package com.example.wexcodingchallenge.service.exchange;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static com.example.wexcodingchallenge.exception.CustomHttpExceptionFactory.currencyNotFound;
import static java.util.Comparator.comparing;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final WebClient.Builder webClient;
    private final ExchangeRateConfig exchangeRateConfig;

    public Mono<ExchangeRateResponse.ExchangeRateData> getExchangeRates(@NotNull @Valid LocalDate transactionDate, @NotNull String currency) {
        final var httpConfig = exchangeRateConfig.getHttp();
        final var exchangeRateMonths = exchangeRateConfig.getMonthsBack();
        final var exchangeRateDateStart = transactionDate.minusMonths(exchangeRateMonths);
        final var parameters = httpConfig.buildParameters(exchangeRateDateStart, transactionDate, currency);
        return webClient.baseUrl(httpConfig.getBaseUrl())
                .build()
                .get()
                .uri(httpConfig.getUri() + parameters)
                .retrieve()
                .bodyToMono(ExchangeRateResponse.class)
                .flatMap(response -> extractExchangeRateData(response, currency, exchangeRateDateStart, transactionDate));
    }

    private Mono<ExchangeRateResponse.ExchangeRateData> extractExchangeRateData(
            ExchangeRateResponse response, String currency, LocalDate exchangeRateDateStart, LocalDate transactionDate) {
        if (response == null || response.getData() == null || response.getData().isEmpty()) {
            return Mono.error(currencyNotFound(currency, exchangeRateDateStart.toString(), transactionDate.toString()));
        }

        return Mono.justOrEmpty(response.getData().stream()
                .max(comparing(ExchangeRateResponse.ExchangeRateData::getRecordDate)));
    }
}
