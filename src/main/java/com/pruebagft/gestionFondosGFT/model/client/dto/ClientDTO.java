package com.pruebagft.gestionFondosGFT.model.client.dto;

import com.pruebagft.gestionFondosGFT.model.investment.dto.InvestmentDTO;
import com.pruebagft.gestionFondosGFT.util.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {

    private String id;
    private String firstName;
    private String lastName;
    private String city;
    private BigDecimal currentBalance;
    private NotificationType notificationPreference;
    private List<InvestmentDTO> activeInvestments;

    private String phoneNumber;
    private String email;
}