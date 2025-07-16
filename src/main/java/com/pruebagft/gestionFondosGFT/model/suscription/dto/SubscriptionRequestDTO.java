package com.pruebagft.gestionFondosGFT.model.suscription.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// OpenAPI Import
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for subscribing to a fund")
public class SubscriptionRequestDTO {

    @NotBlank(message = "Client ID is required")
    @Schema(description = "The unique identifier of the client", example = "6543210abcdef0123456789")
    private String clientId;

    @NotBlank(message = "Fund ID is required")
    @Schema(description = "The unique identifier of the fund to subscribe to", example = "9876543fedcba0123456789")
    private String fundId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be a positive value")
    @Schema(description = "The amount of money to invest in the fund", example = "1000.00")
    private BigDecimal amount;
}