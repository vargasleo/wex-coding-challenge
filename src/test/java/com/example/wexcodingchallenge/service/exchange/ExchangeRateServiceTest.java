package com.example.wexcodingchallenge.service.exchange;

import com.example.wexcodingchallenge.exception.CurrencyNotFoundException;
import com.example.wexcodingchallenge.fixture.ExchangeRateConfigFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    public void setUp() {
        final var exchangeRateConfig = ExchangeRateConfigFixture.createMockExchangeRateConfig();
        ReflectionTestUtils.setField(exchangeRateService, "exchangeRateConfig", exchangeRateConfig);
        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    public void handleWebClientResponseError() {
        final var webClientException = new WebClientResponseException(
                INTERNAL_SERVER_ERROR.getReasonPhrase(),
                INTERNAL_SERVER_ERROR.value(),
                INTERNAL_SERVER_ERROR.getReasonPhrase(),
                HttpHeaders.EMPTY, null, null);

        when(responseSpec.bodyToMono(ExchangeRateResponse.class))
                .thenReturn(Mono.error(webClientException));

        StepVerifier.create(exchangeRateService.getExchangeRates(LocalDate.now(), "Brazil-Real"))
                .expectError(WebClientResponseException.class)
                .verify();
    }

    private static Stream<Arguments> handleCurrencyNotFoundArgs() {
        return Stream.of(
                arguments(null, CurrencyNotFoundException.class),
                arguments(emptyList(), CurrencyNotFoundException.class)
        );
    }

    @ParameterizedTest
    @MethodSource("handleCurrencyNotFoundArgs")
    public void handleResponseScenarios(
            List<ExchangeRateResponse.ExchangeRateData> responseData,
            Class<? extends Throwable> expectedException) {
        final var response = new ExchangeRateResponse();
        response.setData(responseData);

        when(responseSpec.bodyToMono(ExchangeRateResponse.class)).thenReturn(Mono.just(response));

        StepVerifier.create(exchangeRateService.getExchangeRates(LocalDate.now(), "Brazil-Real"))
                .expectError(expectedException)
                .verify();
    }

    @Test
    public void returnMostRecentExchangeRate() {
        final var response = new ExchangeRateResponse();

        final var mostRecent = new ExchangeRateResponse.ExchangeRateData();
        mostRecent.setRecordDate(LocalDate.now());
        mostRecent.setExchangeRate(BigDecimal.ONE);
        final var olderData = new ExchangeRateResponse.ExchangeRateData();
        olderData.setRecordDate(LocalDate.now().minusDays(1));
        olderData.setExchangeRate(BigDecimal.TEN);

        response.setData(List.of(olderData, mostRecent));

        when(responseSpec.bodyToMono(ExchangeRateResponse.class))
                .thenReturn(Mono.just(response));

        StepVerifier.create(exchangeRateService.getExchangeRates(LocalDate.now(), "Brazil-Real"))
                .expectNextMatches(data -> data.getRecordDate().equals(mostRecent.getRecordDate())
                        && data.getExchangeRate().equals(mostRecent.getExchangeRate()))
                .verifyComplete();
    }

}
