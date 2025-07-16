package com.pruebagft.gestionFondosGFT.model.transaction;
import com.pruebagft.gestionFondosGFT.util.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transactions")
public class Transaction {

    @Id
    private String id;
    private String businessTransactionId; // Renamed for clarity to distinguish from MongoDB's _id
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