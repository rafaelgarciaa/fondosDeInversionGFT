package com.pruebagft.gestionFondosGFT.model.investment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Investment {
    private String fundId;
    private String fundName;
    private BigDecimal initialAmountInvested;
    private BigDecimal currentAmount;
    private LocalDateTime subscriptionDate;
    private String transactionId;
}