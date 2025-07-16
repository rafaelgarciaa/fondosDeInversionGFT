package com.pruebagft.gestionFondosGFT.model.suscription.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// OpenAPI Import
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for canceling a fund subscription")
public class CancellationRequestDTO {
    @NotBlank(message = "Client ID is required")
    @Schema(description = "The unique identifier of the client initiating the cancellation", example = "6543210abcdef0123456789")
    private String clientId;

    @NotBlank(message = "Fund ID is required")
    @Schema(description = "The unique identifier of the fund to cancel subscription from", example = "9876543fedcba0123456789")
    private String fundId;
}