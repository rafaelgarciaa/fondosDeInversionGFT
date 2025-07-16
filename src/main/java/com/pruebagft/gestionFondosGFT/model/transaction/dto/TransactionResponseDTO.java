package com.pruebagft.gestionFondosGFT.model.transaction.dto;

import com.pruebagft.gestionFondosGFT.util.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// OpenAPI Import
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for transaction details (subscription or cancellation)")
public class TransactionResponseDTO {
    @Schema(description = "Unique identifier of the transaction", example = "txn_123abc456def789ghi")
    private String id;

    @Schema(description = "Business-level unique identifier for this transaction", example = "BUS_TXN_001")
    private String businessTransactionId;

    @Schema(description = "ID of the client involved in the transaction", example = "cli_abcdef123456")
    private String clientId;

    @Schema(description = "ID of the fund involved in the transaction", example = "fnd_ghijkl789012")
    private String fundId;

    @Schema(description = "Name of the fund involved in the transaction", example = "Global Growth Fund")
    private String fundName;

    @Schema(description = "Type of transaction (SUBSCRIPTION or CANCELLATION)", example = "SUBSCRIPTION")
    private TransactionType type;

    @Schema(description = "Amount of money involved in the transaction", example = "5000.00")
    private BigDecimal amount;

    @Schema(description = "Date and time when the transaction occurred", example = "2024-07-15T10:30:00")
    private LocalDateTime date;

    @Schema(description = "Status of the transaction (e.g., COMPLETED, PENDING, FAILED)", example = "COMPLETED")
    private String status;

    @Schema(description = "Client's balance before this transaction", example = "10000.00", nullable = true)
    private BigDecimal clientBalanceBefore;

    @Schema(description = "Client's balance after this transaction", example = "5000.00", nullable = true)
    private BigDecimal clientBalanceAfter;

    @Schema(description = "Error message if the transaction failed", example = "Insufficient funds", nullable = true)
    private String errorMessage;
}