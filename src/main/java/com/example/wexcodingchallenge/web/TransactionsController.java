package com.example.wexcodingchallenge.web;

import com.example.wexcodingchallenge.api.TransactionsApi;
import com.example.wexcodingchallenge.api.model.ConvertedTransactionResponse;
import com.example.wexcodingchallenge.api.model.PurchaseTransaction;
import com.example.wexcodingchallenge.api.model.PurchaseTransactionResponse;
import com.example.wexcodingchallenge.service.PurchaseTransactionCreatorService;
import com.example.wexcodingchallenge.service.PurchaseTransactionRetrievalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class TransactionsController implements TransactionsApi {

    private final PurchaseTransactionCreatorService transactionCreator;
    private final PurchaseTransactionRetrievalService transactionRetrieval;

    @Override
    public ResponseEntity<PurchaseTransactionResponse> createTransaction(PurchaseTransaction purchaseTransaction) {
        final var response = transactionCreator.createTransaction(purchaseTransaction);
        final var location = URI.create("/transactions/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @Override
    public ResponseEntity<ConvertedTransactionResponse> getConvertedTransaction(String id, String targetCurrency) {
        return ResponseEntity.ok(transactionRetrieval.retrieveConvertedPurchaseTransaction(id, targetCurrency));
    }
}
