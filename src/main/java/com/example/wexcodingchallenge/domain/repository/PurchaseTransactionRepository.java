package com.example.wexcodingchallenge.domain.repository;

import com.example.wexcodingchallenge.domain.entity.PurchaseTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseTransactionRepository extends JpaRepository<PurchaseTransactionEntity, String> {
}
