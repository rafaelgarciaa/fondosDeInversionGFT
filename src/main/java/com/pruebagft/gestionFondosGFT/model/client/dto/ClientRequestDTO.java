package com.pruebagft.gestionFondosGFT.model.client.dto;

import com.pruebagft.gestionFondosGFT.util.enums.NotificationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// OpenAPI Import
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for creating a new client") // Overall description for the DTO
public class ClientRequestDTO {

    @NotBlank(message = "First name is required")
    @Schema(description = "Client's first name", example = "Juan") // Description and example for a field
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(description = "Client's last name", example = "Perez")
    private String lastName;

    @NotBlank(message = "City is required")
    @Schema(description = "City where the client resides", example = "Bogota")
    private String city;

    @NotNull(message = "Notification preference is required")
    @Schema(description = "Preferred notification type for the client", example = "EMAIL")
    private NotificationType notificationPreference;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Phone number must be valid")
    @Schema(description = "Client's phone number (optional, international format supported)", example = "+573001234567", nullable = true)
    private String phoneNumber;

    @Email(message = "Email should be valid")
    @Schema(description = "Client's email address (optional, must be valid format)", example = "juan.perez@example.com", nullable = true)
    private String email;
}