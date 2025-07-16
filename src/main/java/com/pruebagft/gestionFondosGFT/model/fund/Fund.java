package com.pruebagft.gestionFondosGFT.model.fund;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "funds")
public class Fund {

    @Id
    private String id;
    private String name;
    private String productType;
    private BigDecimal minimumSubscriptionAmount;
}