package com.example.wexcodingchallenge.service;

import com.example.wexcodingchallenge.api.model.PurchaseTransaction;
import com.example.wexcodingchallenge.api.model.PurchaseTransactionResponse;
import com.example.wexcodingchallenge.domain.repository.PurchaseTransactionRepository;
import com.example.wexcodingchallenge.mapper.PurchaseTransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurchaseTransactionCreatorService {

    private final PurchaseTransactionRepository repo;
    private final PurchaseTransactionMapper mapper;

    public PurchaseTransactionResponse createTransaction(PurchaseTransaction request) {
        final var entity = mapper.toEntity(request);
        repo.save(entity);
        return mapper.toResponse(entity);
    }
}
