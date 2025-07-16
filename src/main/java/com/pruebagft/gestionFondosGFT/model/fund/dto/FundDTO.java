package com.pruebagft.gestionFondosGFT.model.fund.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundDTO {

    private String id;
    private String name;
    private String productType;
    private BigDecimal minimumSubscriptionAmount;
}