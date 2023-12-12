package com.example.wexcodingchallenge.service;

import com.example.wexcodingchallenge.api.model.ConvertedTransactionResponse;
import com.example.wexcodingchallenge.domain.repository.PurchaseTransactionRepository;
import com.example.wexcodingchallenge.mapper.ConvertedPurchaseTransactionMapper;
import com.example.wexcodingchallenge.service.exchange.ExchangeRateResponse;
import com.example.wexcodingchallenge.service.exchange.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.example.wexcodingchallenge.exception.CustomHttpExceptionFactory.notFound;
import static com.example.wexcodingchallenge.exception.CustomHttpExceptionFactory.serviceUnavailable;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseTransactionRetrievalService {

    private final ExchangeRateService exchangeRateService;
    private final PurchaseTransactionRepository repo;
    private final ConvertedPurchaseTransactionMapper mapper;

    public ConvertedTransactionResponse retrieveConvertedPurchaseTransaction(String id, String targetCurrency) {
        return repo.findById(id)
                .map(mapper::toResponse)
                .map(transaction -> applyExchangeRate(transaction, targetCurrency))
                .orElseThrow(notFound("Transaction not found.", "Check the transaction id or contact support."));
    }

    private ConvertedTransactionResponse applyExchangeRate(ConvertedTransactionResponse transaction, String targetCurrency) {
        if (targetCurrency == null) return transaction;

        return exchangeRateService.getExchangeRates(transaction.getTransactionDate(), targetCurrency)
                .map(exchangeRate ->
                        transaction
                                .exchangeRate(exchangeRate.getExchangeRate())
                                .convertedAmount(applyConversion(transaction, exchangeRate))
                                .targetCurrency(targetCurrency))
                .blockOptional()
                .orElseThrow(serviceUnavailable(
                        "The Exchange Rate API is currently inaccessible.",
                        "Server encountered an error interacting with Exchange Rate API, try again."));
    }

    private BigDecimal applyConversion(ConvertedTransactionResponse transaction, ExchangeRateResponse.ExchangeRateData exchangeRate) {
        return exchangeRate
                .getExchangeRate()
                .multiply(transaction.getPurchaseAmount())
                .setScale(2, RoundingMode.HALF_UP);
    }
}
