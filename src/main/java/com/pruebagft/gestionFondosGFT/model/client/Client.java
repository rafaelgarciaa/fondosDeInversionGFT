package com.pruebagft.gestionFondosGFT.model.client;

import com.pruebagft.gestionFondosGFT.model.investment.Investment;
import com.pruebagft.gestionFondosGFT.util.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "clients")
public class Client {

    @Id
    private String id;

    private String firstName;
    private String lastName;
    private String city;
    private BigDecimal currentBalance;

    private NotificationType notificationPreference;

    private List<Investment> activeInvestments;

    private String phoneNumber;
    private String email;

    public Client(String id, String firstName, String lastName, String city, NotificationType notificationPreference, String phoneNumber, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.currentBalance = new BigDecimal("500000.00");
        this.notificationPreference = notificationPreference;
        this.activeInvestments = new ArrayList<>();
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
}