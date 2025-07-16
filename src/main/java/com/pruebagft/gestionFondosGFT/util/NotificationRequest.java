package com.pruebagft.gestionFondosGFT.util;

import com.pruebagft.gestionFondosGFT.util.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationRequest {
    private String addressee; // Email o número de teléfono
    private String subject; // Solo para email
    private String message;
    private NotificationType type;
}