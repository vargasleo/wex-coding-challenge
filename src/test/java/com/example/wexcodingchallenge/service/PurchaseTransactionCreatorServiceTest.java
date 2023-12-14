package com.example.wexcodingchallenge.service;

import com.example.wexcodingchallenge.api.model.PurchaseTransaction;
import com.example.wexcodingchallenge.api.model.PurchaseTransactionResponse;
import com.example.wexcodingchallenge.domain.entity.PurchaseTransactionEntity;
import com.example.wexcodingchallenge.domain.repository.PurchaseTransactionRepository;
import com.example.wexcodingchallenge.mapper.PurchaseTransactionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseTransactionCreatorServiceTest {

    @InjectMocks
    private PurchaseTransactionCreatorService service;

    @Mock
    private PurchaseTransactionRepository repo;

    @Mock
    private PurchaseTransactionMapper mapper;

    private PurchaseTransaction request;
    private PurchaseTransactionEntity entity;
    private PurchaseTransactionResponse expectedResponse;

    @BeforeEach
    void setUp() {
        request = new PurchaseTransaction(LocalDate.now(), BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP));
        entity = new PurchaseTransactionEntity();
        entity.setTransactionDate(request.getTransactionDate());
        entity.setPurchaseAmount(request.getPurchaseAmount());
        expectedResponse = new PurchaseTransactionResponse();
        expectedResponse.setTransactionDate(entity.getTransactionDate());
        expectedResponse.setPurchaseAmount(entity.getPurchaseAmount());
        when(mapper.toEntity(request)).thenReturn(entity);
        when(repo.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(expectedResponse);
    }

    @Test
    void testCreateTransaction() {
        final var actualResponse = service.createTransaction(request);

        verify(mapper).toEntity(request);
        verify(repo).save(entity);
        verify(mapper).toResponse(entity);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getTransactionDate(), actualResponse.getTransactionDate());
        assertEquals(expectedResponse.getPurchaseAmount(), actualResponse.getPurchaseAmount());
        assertEquals(request.getTransactionDate(), entity.getTransactionDate());
        assertEquals(request.getPurchaseAmount(), entity.getPurchaseAmount());
    }
}

