package com.pruebagft.gestionFondosGFT.model.transaction.dto;

import com.pruebagft.gestionFondosGFT.util.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

    private String id;
    private String businessTransactionId;
    private String clientId;
    private String fundId;
    private String fundName;
    private TransactionType type;
    private BigDecimal amount;
    private LocalDateTime date;
    private BigDecimal clientBalanceBefore;
    private BigDecimal clientBalanceAfter;
    private String status;
    private String errorMessage;
}