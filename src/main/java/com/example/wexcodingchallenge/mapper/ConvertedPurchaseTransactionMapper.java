package com.example.wexcodingchallenge.mapper;

import com.example.wexcodingchallenge.api.model.ConvertedTransactionResponse;
import com.example.wexcodingchallenge.domain.entity.PurchaseTransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ConvertedPurchaseTransactionMapper {

    ConvertedTransactionResponse toResponse(PurchaseTransactionEntity purchaseTransaction);
}
