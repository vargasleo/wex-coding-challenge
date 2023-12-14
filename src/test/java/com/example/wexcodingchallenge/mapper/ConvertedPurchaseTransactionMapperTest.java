package com.example.wexcodingchallenge.mapper;

import com.example.wexcodingchallenge.domain.entity.PurchaseTransactionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class ConvertedPurchaseTransactionMapperTest {

    private ConvertedPurchaseTransactionMapper mapper;

    @BeforeEach
    void setup() {
        mapper = Mappers.getMapper(ConvertedPurchaseTransactionMapper.class);
    }

    @Test
    void shouldMapEntityToResponseIgnoringNullFields() {
        final var entity = new PurchaseTransactionEntity();
        entity.setId("c17ce8ab-299f-47f7-bf0e-f5c95853fc08");
        entity.setDescription("Books");
        entity.setTransactionDate(LocalDate.of(2023, 12, 10));
        entity.setPurchaseAmount(BigDecimal.valueOf(120));

        final var response = mapper.toResponse(entity);

        assertEquals(entity.getId(), response.getId());
        assertEquals(entity.getDescription(), response.getDescription());
        assertEquals(entity.getTransactionDate(), response.getTransactionDate());
        assertEquals(entity.getPurchaseAmount(), response.getPurchaseAmount());
        assertNull(response.getTargetCurrency());
        assertNull(response.getExchangeRate());
        assertNull(response.getConvertedAmount());
    }

    @Test
    void shouldHandleNullEntity() {
        final var response = mapper.toResponse(null);
        assertNull(response);
    }
}
