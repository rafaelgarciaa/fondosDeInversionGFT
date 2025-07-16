package com.pruebagft.gestionFondosGFT.model.client.dto;

import com.pruebagft.gestionFondosGFT.model.investment.dto.InvestmentResponseDTO;
import com.pruebagft.gestionFondosGFT.util.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponseDTO {
    private String id;
    private String firstName;
    private String lastName;
    private String city;
    private BigDecimal currentBalance;
    private NotificationType notificationPreference;
    private String phoneNumber;
    private String email;
    private List<InvestmentResponseDTO> activeInvestments; // To display current investments
}