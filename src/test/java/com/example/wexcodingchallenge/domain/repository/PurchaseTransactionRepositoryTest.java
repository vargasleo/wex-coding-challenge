package com.example.wexcodingchallenge.domain.repository;

import com.example.wexcodingchallenge.domain.entity.PurchaseTransactionEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class PurchaseTransactionRepositoryTest {

    @Autowired
    private PurchaseTransactionRepository repository;

    @Test
    void shouldSaveEntityWithValidFields() {
        final var entity = new PurchaseTransactionEntity();
        entity.setDescription("Valid Description.");
        entity.setTransactionDate(LocalDate.now());
        entity.setPurchaseAmount(new BigDecimal("123.456"));

        final var savedEntity = repository.save(entity);
        assertNotNull(savedEntity.getId());
        assertEquals("Valid Description.", savedEntity.getDescription());
        assertNotNull(savedEntity.getTransactionDate());
        assertEquals(BigDecimal.valueOf(123.46), savedEntity.getPurchaseAmount());
    }

    @Test
    void findByIdShouldRetrieveCorrectEntity() {
        final var entity = new PurchaseTransactionEntity();
        entity.setDescription("Valid Description.");
        entity.setTransactionDate(LocalDate.now());
        entity.setPurchaseAmount(new BigDecimal("100"));

        final var savedEntity = repository.save(entity);
        assertNotNull(savedEntity.getId());

        final var foundEntity = repository.findById(savedEntity.getId());
        assertTrue(foundEntity.isPresent());
        assertEquals(savedEntity.getId(), foundEntity.get().getId());
        assertEquals(entity.getDescription(), foundEntity.get().getDescription());
        assertEquals(entity.getPurchaseAmount(), foundEntity.get().getPurchaseAmount());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidEntities")
    void shouldNotSaveInvalidEntity(PurchaseTransactionEntity entity) {
        repository.save(entity);
        assertThrows(DataIntegrityViolationException.class, () -> repository.flush());
    }

    private static Stream<PurchaseTransactionEntity> provideInvalidEntities() {
        return Stream.of(
                createEntityWithNullTransactionDate(),
                createEntityWithNullPurchaseAmount(),
                createEntityWithLongDescription()
        );
    }

    private static PurchaseTransactionEntity createEntityWithNullTransactionDate() {
        final var entity = new PurchaseTransactionEntity();
        entity.setDescription("Valid Description.");
        entity.setTransactionDate(null);
        entity.setPurchaseAmount(BigDecimal.valueOf(100));
        return entity;
    }

    private static PurchaseTransactionEntity createEntityWithNullPurchaseAmount() {
        final var entity = new PurchaseTransactionEntity();
        entity.setDescription("Valid Description.");
        entity.setTransactionDate(LocalDate.now());
        entity.setPurchaseAmount(null);
        return entity;
    }

    private static PurchaseTransactionEntity createEntityWithLongDescription() {
        final var entity = new PurchaseTransactionEntity();
        entity.setDescription("This description is way too long and should cause an exception to be thrown.");
        entity.setTransactionDate(LocalDate.now());
        entity.setPurchaseAmount(BigDecimal.valueOf(100));
        return entity;
    }
}
