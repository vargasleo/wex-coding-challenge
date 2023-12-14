package com.example.wexcodingchallenge.web;

import com.example.wexcodingchallenge.domain.entity.PurchaseTransactionEntity;
import com.example.wexcodingchallenge.domain.repository.PurchaseTransactionRepository;
import com.example.wexcodingchallenge.service.exchange.ExchangeRateService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class TransactionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private PurchaseTransactionRepository repo;

    @SpyBean
    private ExchangeRateService exchangeRateService;

    final PurchaseTransactionEntity mockEntity =
            new PurchaseTransactionEntity(
                    "c17ce8ab-299f-47f7-bf0e-f5c95853fc08",
                    "Not Books",
                    LocalDate.of(2023, 12, 10),
                    BigDecimal.valueOf(100));

    @AfterEach
    void shutdown() {
        repo.deleteAll();
    }

    @Test
    @DisplayName("Successful creation of a transaction")
    void successfulCreationOfTransaction() throws Exception {
        final var transactionJson = "{\"description\":\"Books\",\"transactionDate\":\"2023-12-10\",\"purchaseAmount\":120.00}";

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.description").value("Books"))
                .andExpect(jsonPath("$.transactionDate").value("2023-12-10"))
                .andExpect(jsonPath("$.purchaseAmount").value(120.00));
    }

    @Test
    @DisplayName("Creation fails due to missing required fields")
    void creationFailsDueToMissingFields() throws Exception {
        final var transactionJson = "{\"description\":\"Books\"}";

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("The request is malformed."))
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.details").value(containsString("Field: transactionDate: must not be null")))
                .andExpect(jsonPath("$.details").value(containsString("Field: purchaseAmount: must not be null")));
    }

    @Test
    @DisplayName("Handling server-side errors during transaction creation")
    void handleServerSideErrorsOnCreation() throws Exception {
        final var transactionJson = "{\"description\":\"Books\",\"transactionDate\":\"2023-12-10\",\"purchaseAmount\":120.00}";

        doThrow(new RuntimeException()).when(repo).save(any(PurchaseTransactionEntity.class));

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionJson))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.title").value("Something unexpected happened."))
                .andExpect(jsonPath("$.details").value("Check service availability, try again or contact support."));
    }

    @Test
    @DisplayName("Successful retrieval of a transaction with currency conversion")
    void successfulRetrievalWithCurrencyConversion() throws Exception {
        repo.save(mockEntity);

        mockMvc.perform(get("/transactions/{id}", mockEntity.getId())
                        .queryParam("targetCurrency", "Brazil-Real"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockEntity.getId()))
                .andExpect(jsonPath("$.description").value(mockEntity.getDescription()))
                .andExpect(jsonPath("$.transactionDate").value(mockEntity.getTransactionDate().toString()))
                .andExpect(jsonPath("$.purchaseAmount").value(mockEntity.getPurchaseAmount().doubleValue()))
                .andExpect(jsonPath("$.targetCurrency").value("Brazil-Real"))
                .andExpect(jsonPath("$.exchangeRate").value(5.033))
                .andExpect(jsonPath("$.convertedAmount").value(mockEntity.getPurchaseAmount().multiply(BigDecimal.valueOf(5.033)).doubleValue()));
    }

    @Test
    @DisplayName("Retrieval fails for malformed transaction ID")
    void retrievalFailsForConstrainViolationId() throws Exception {
        mockMvc.perform(get("/transactions/{id}", "not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("The request is malformed."))
                .andExpect(jsonPath("$.details").value("getConvertedTransaction.id: must match \"^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$\"; "));
    }

    @Test
    @DisplayName("Retrieval fails for non-existent transaction ID")
    void retrievalFailsForNonExistentId() throws Exception {
        mockMvc.perform(get("/transactions/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Transaction not found."))
                .andExpect(jsonPath("$.details").value("Check the transaction id or contact support."));
    }

    @Test
    @DisplayName("Retrieval fails due to unavailable target currency in the provided interval for conversion")
    void retrievalFailsDueToUnavailableCurrency() throws Exception {
        repo.save(mockEntity);

        mockMvc.perform(get("/transactions/{id}", mockEntity.getId())
                        .queryParam("targetCurrency", "Unknown-Currency"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("No exchange rate for provided time interval and currency."))
                .andExpect(jsonPath("$.details").value("There's no exchange rate for 'Unknown-Currency' between 2023-06-10 and 2023-12-10."));
    }

    @Test
    @DisplayName("Handling server-side errors during exchange rate retrieval")
    void handleServerSideErrorsOnExchangeRateRetrieval() throws Exception {
        repo.save(mockEntity);

        doReturn(Mono.empty()).when(exchangeRateService).getExchangeRates(any(LocalDate.class), anyString());

        mockMvc.perform(get("/transactions/{id}", mockEntity.getId())
                        .queryParam("targetCurrency", "Brazil-Real"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.title").value("The Exchange Rate API is currently inaccessible."))
                .andExpect(jsonPath("$.details").value("Server encountered an error interacting with Exchange Rate API, try again."));
    }

    @Test
    @DisplayName("Handling server-side errors during transaction retrieval")
    void handleServerSideErrorsOnRetrieval() throws Exception {
        doThrow(new RuntimeException()).when(repo).findById(anyString());

        mockMvc.perform(get("/transactions/{id}", "c17ce8ab-299f-47f7-bf0e-f5c95853fc08"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.title").value("Something unexpected happened."))
                .andExpect(jsonPath("$.details").value("Check service availability, try again or contact support."));
    }
}
