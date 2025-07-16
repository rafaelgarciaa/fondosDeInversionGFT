package com.pruebagft.gestionFondosGFT.service.notification;

import com.pruebagft.gestionFondosGFT.util.NotificationRequest;
import com.pruebagft.gestionFondosGFT.util.enums.NotificationType;
import io.awspring.cloud.ses.SimpleEmailServiceMailSender;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    private final SimpleEmailServiceMailSender mailSender;
    private final SnsTemplate snsTemplate;


    @Value("${cloud.aws.ses.from-address}")
    private String sesFromAddress;

    @Autowired
    public NotificationService(SimpleEmailServiceMailSender mailSender, SnsTemplate snsTemplate) {
        this.mailSender = mailSender;
        this.snsTemplate = snsTemplate;
    }

    public void sendNotification(NotificationRequest request) {
        // Añadir una comprobación general para el destinatario
        if (request.getAddressee() == null || request.getAddressee().trim().isEmpty()) {
            log.warn("No se puede enviar notificación para el tipo {} porque el destinatario está vacío o nulo.", request.getType());
            return; // Salir del método si no hay destinatario
        }

        if (request.getType() == NotificationType.EMAIL) {
            sendEmail(request);
        } else if (request.getType() == NotificationType.SMS) {
            sendSms(request);
        } else {
            log.info("No se requiere notificación para el tipo: {}", request.getType());
        }
    }

    private void sendEmail(NotificationRequest request) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sesFromAddress);
            mailMessage.setTo(request.getAddressee());
            mailMessage.setSubject(request.getSubject());
            mailMessage.setText(request.getMessage());

            mailSender.send(mailMessage);
            log.info("Email enviado a: {} con asunto: {}", request.getAddressee(), request.getSubject());
        } catch (Exception e) {
            log.error("Error al enviar email a {}: {}", request.getAddressee(), e.getMessage());
            // Aquí podrías lanzar una excepción personalizada o manejar el error
        }
    }

    private void sendSms(NotificationRequest request) {
        try {
            Message<String> snsMessage = MessageBuilder.withPayload(request.getMessage()).build();
            snsTemplate.send(request.getAddressee(), snsMessage);

            log.info("SMS enviado a: {}", request.getAddressee());
        } catch (Exception e) {
            log.error("Error al enviar SMS a {}: {}", request.getAddressee(), e.getMessage());
            // Aquí podrías lanzar una excepción personalizada o manejar el error
        }
    }
}