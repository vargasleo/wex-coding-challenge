package com.example.wexcodingchallenge.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PurchaseTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 50)
    private String description;

    @Column(nullable = false)
    private LocalDate transactionDate;

    @Column(nullable = false)
    private BigDecimal purchaseAmount;

    public void setPurchaseAmount(BigDecimal amount) {
        purchaseAmount = amount == null ? null : amount.setScale(2, RoundingMode.HALF_UP);
    }
}
