package com.example.wexcodingchallenge.service;

import com.example.wexcodingchallenge.api.model.ConvertedTransactionResponse;
import com.example.wexcodingchallenge.api.model.PurchaseTransaction;
import com.example.wexcodingchallenge.domain.entity.PurchaseTransactionEntity;
import com.example.wexcodingchallenge.domain.repository.PurchaseTransactionRepository;
import com.example.wexcodingchallenge.exception.NotFoundException;
import com.example.wexcodingchallenge.exception.ServiceUnavailableException;
import com.example.wexcodingchallenge.mapper.ConvertedPurchaseTransactionMapper;
import com.example.wexcodingchallenge.service.exchange.ExchangeRateResponse;
import com.example.wexcodingchallenge.service.exchange.ExchangeRateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Optional;

import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseTransactionRetrievalServiceTest {

    @InjectMocks
    private PurchaseTransactionRetrievalService service;

    @Mock
    private ExchangeRateService exchangeRateService;

    @Mock
    private PurchaseTransactionRepository repo;

    @Mock
    private ConvertedPurchaseTransactionMapper mapper;

    private PurchaseTransaction request;
    private PurchaseTransactionEntity entity;
    private ConvertedTransactionResponse expectedResponse;

    @BeforeEach
    public void setUp() {
        request = new PurchaseTransaction(LocalDate.now(), BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP));
        entity = new PurchaseTransactionEntity();
        entity.setTransactionDate(request.getTransactionDate());
        entity.setPurchaseAmount(request.getPurchaseAmount());
        expectedResponse = new ConvertedTransactionResponse();
        expectedResponse.setTransactionDate(entity.getTransactionDate());
        expectedResponse.setPurchaseAmount(entity.getPurchaseAmount());
    }

    @Test
    public void handleTransactionNotFound() {
        when(repo.findById(anyString())).thenReturn(empty());

        assertThrows(NotFoundException.class,
                () -> service.retrieveConvertedPurchaseTransaction("invalid_id", "Brazil-Real"));
    }

    @Test
    public void handleTargetCurrencyNull() {
        when(repo.findById(anyString())).thenReturn(Optional.of(entity));

        final var transactionResponse = new ConvertedTransactionResponse();
        when(mapper.toResponse(any(PurchaseTransactionEntity.class))).thenReturn(transactionResponse);

        final var result =
                service.retrieveConvertedPurchaseTransaction("c17ce8ab-299f-47f7-bf0e-f5c95853fc08", null);

        assertNull(result.getTargetCurrency());
        assertEquals(transactionResponse, result);
    }

    @Test
    public void handleExchangeRateServiceEmpty() {
        when(repo.findById(anyString())).thenReturn(Optional.of(entity));
        when(mapper.toResponse(any(PurchaseTransactionEntity.class))).thenReturn(expectedResponse);
        when(exchangeRateService.getExchangeRates(any(LocalDate.class), anyString())).thenReturn(Mono.empty());

        assertThrows(ServiceUnavailableException.class, () -> {
            service.retrieveConvertedPurchaseTransaction("c17ce8ab-299f-47f7-bf0e-f5c95853fc08", "Brazil-Real");
        });
    }

    @Test
    public void handleSuccess() {
        final var exchangeRateData = new ExchangeRateResponse.ExchangeRateData();
        exchangeRateData.setExchangeRate(BigDecimal.valueOf(1.5));
        exchangeRateData.setRecordDate(LocalDate.now());

        when(repo.findById(anyString())).thenReturn(Optional.of(entity));
        when(mapper.toResponse(any(PurchaseTransactionEntity.class))).thenReturn(expectedResponse);
        when(exchangeRateService.getExchangeRates(any(LocalDate.class), anyString())).thenReturn(Mono.just(exchangeRateData));

        final var result =
                service.retrieveConvertedPurchaseTransaction("c17ce8ab-299f-47f7-bf0e-f5c95853fc08", "Brazil-Real");

        assertNotNull(result.getConvertedAmount());
        assertEquals("Brazil-Real", result.getTargetCurrency());

        final var purchaseAmountResult = result.getPurchaseAmount()
                .multiply(exchangeRateData.getExchangeRate())
                .setScale(2, RoundingMode.HALF_UP);

        assertEquals(purchaseAmountResult, result.getConvertedAmount());
    }
}
