package com.example.wexcodingchallenge.mapper;

import com.example.wexcodingchallenge.api.model.PurchaseTransaction;
import com.example.wexcodingchallenge.api.model.PurchaseTransactionResponse;
import com.example.wexcodingchallenge.domain.entity.PurchaseTransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PurchaseTransactionMapper {

    PurchaseTransactionEntity toEntity(PurchaseTransaction purchaseTransaction);

    PurchaseTransactionResponse toResponse(PurchaseTransactionEntity purchaseTransaction);
}
