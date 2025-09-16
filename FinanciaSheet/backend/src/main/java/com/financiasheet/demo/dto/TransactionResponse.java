package com.financiasheet.demo.dto;

import com.financiasheet.demo.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        LocalDate date,
        BigDecimal amount,
        String description,
        String category,
        String account,
        String kind,
        String source,
        String externalId
) {
    public static TransactionResponse fromEntity(Transaction tx) {
        return new TransactionResponse(
                tx.getId(),
                tx.getDate(),
                tx.getAmount(),
                tx.getDescription(),
                tx.getCategory(),
                tx.getAccount(),
                tx.getKind(),
                tx.getSource(),
                tx.getExternalId()
        );
    }
}