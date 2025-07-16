package com.pruebagft.gestionFondosGFT.service.transaction;

import com.pruebagft.gestionFondosGFT.model.client.Client;
import com.pruebagft.gestionFondosGFT.model.fund.Fund;
import com.pruebagft.gestionFondosGFT.model.investment.Investment;
import com.pruebagft.gestionFondosGFT.model.transaction.Transaction;
import com.pruebagft.gestionFondosGFT.repository.client.ClientRepository;
import com.pruebagft.gestionFondosGFT.repository.fund.FundRepository;
import com.pruebagft.gestionFondosGFT.repository.transaction.TransactionRepository;
import com.pruebagft.gestionFondosGFT.service.notification.NotificationService;
import com.pruebagft.gestionFondosGFT.util.NotificationRequest;
import com.pruebagft.gestionFondosGFT.util.enums.NotificationType;
import com.pruebagft.gestionFondosGFT.util.enums.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class TransactionService {

    private final ClientRepository clientRepository;
    private final FundRepository fundRepository;
    private final TransactionRepository transactionRepository;
    private final NotificationService notificationService;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public TransactionService(
            ClientRepository clientRepository, // Renombrado
            FundRepository fundRepository,     // Renombrado
            TransactionRepository transactionRepository, // Renombrado
            NotificationService notificationService, // Renombrado
            MongoTemplate mongoTemplate) {
        this.clientRepository = clientRepository;
        this.fundRepository = fundRepository;
        this.transactionRepository = transactionRepository;
        this.notificationService = notificationService;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Permite a un cliente suscribirse a un fondo.
     *
     * @param clientId ID del cliente.
     * @param fundId   ID del fondo.
     * @param amount   Monto a suscribir.
     * @return Transaction creada.
     * @throws RuntimeException si hay errores de validación o no se encuentra el cliente/fondo.
     */
    @Transactional
    public Transaction subscribeFund(String clientId, String fundId, BigDecimal amount) { // Renombrado
        log.info("Iniciando suscripción: ClientID={}, FundID={}, Monto={}", clientId, fundId, amount);

        // 1. Obtener Client y Fund
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client no encontrado con ID: " + clientId));
        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> new RuntimeException("Fund no encontrado con ID: " + fundId));

        // 2. Validaciones de Negocio
        if (amount.compareTo(fund.getMinimumSubscriptionAmount()) < 0) {
            String errorMessage = "El monto ingresado para suscripción (" + amount + ") es menor al monto mínimo del fondo (" + fund.getMinimumSubscriptionAmount() + ").";
            log.warn(errorMessage);
            throw new RuntimeException(errorMessage);
        }
        if (client.getCurrentBalance().compareTo(amount) < 0) {
            String errorMessage = "Saldo insuficiente. Saldo actual: " + client.getCurrentBalance() + ", Monto de suscripción: " + amount;
            log.warn(errorMessage);
            throw new RuntimeException(errorMessage);
        }
        // Verificar si el client ya tiene una inversión activa en este fund
        boolean alreadySubscribed = client.getActiveInvestments().stream()
                .anyMatch(inv -> inv.getFundId().equals(fundId));
        if (alreadySubscribed) {
            String errorMessage = "El cliente ya tiene una inversión activa en el fondo " + fund.getName();
            log.warn(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        // 3. Crear Registro de Transaction
        Transaction transaction = new Transaction();
        transaction.setBusinessTransactionId(UUID.randomUUID().toString());
        transaction.setClientId(client.getId());
        transaction.setFundId(fund.getId());
        transaction.setFundName(fund.getName());
        transaction.setType(TransactionType.SUSCRIPTION);
        transaction.setAmount(amount);
        transaction.setDate(LocalDateTime.now());
        transaction.setStatus("COMPLETADA");
        transaction.setClientBalanceBefore(client.getCurrentBalance());

        // 4. Actualizar Saldo del Client y Añadir Investment
        client.setCurrentBalance(client.getCurrentBalance().subtract(amount));
        Investment newInvestment = new Investment( // Renombrado
                fund.getId(),
                fund.getName(),
                amount,
                amount,
                transaction.getDate(),
                transaction.getBusinessTransactionId()
        );
        client.getActiveInvestments().add(newInvestment); // Usando newInvestment
        clientRepository.save(client);

        transaction.setClientBalanceAfter(client.getCurrentBalance());
        Transaction savedTransaction = transactionRepository.save(transaction); // Renombrado

        // 5. Enviar Notificación (¡CAMBIOS AQUÍ PARA USAR email y phoneNumber!)
        String notificationMessage = String.format(
                "Estimado %s %s, su suscripción al fondo %s ha sido exitosa por un monto de COP %.2f. " +
                        "Su nuevo saldo disponible es COP %.2f.",
                client.getFirstName(), client.getLastName(), fund.getName(), amount, client.getCurrentBalance()
        );
        String emailSubject = "Confirmación de Suscripción a Fondo";

        NotificationRequest notification = NotificationRequest.builder()
                .subject(emailSubject)
                .message(notificationMessage)
                .type(client.getNotificationPreference())
                .build();

        // Asignar el destinatario según la preferencia
        if (client.getNotificationPreference() == NotificationType.EMAIL) {
            if (client.getEmail() == null || client.getEmail().isEmpty()) {
                log.warn("Cliente {} prefiere email, pero no tiene una dirección de email registrada. No se enviará notificación.", client.getId());
            } else {
                notification.setAddressee(client.getEmail());
                notificationService.sendNotification(notification);
            }
        } else if (client.getNotificationPreference() == NotificationType.SMS) {
            if (client.getPhoneNumber() == null || client.getPhoneNumber().isEmpty()) {
                log.warn("Cliente {} prefiere SMS, pero no tiene un número de teléfono registrado. No se enviará notificación.", client.getId());
            } else {
                notification.setAddressee(client.getPhoneNumber());
                notificationService.sendNotification(notification);
            }
        } else {
            log.info("Cliente {} no desea notificaciones.", client.getId());
        }

        log.info("Suscripción completada y notificación enviada para ClientID={}, FundID={}", clientId, fundId);
        return savedTransaction;
    }

    /**
     * Permite a un cliente cancelar la totalidad de su suscripción a un fondo.
     *
     * @param clientId ID del cliente.
     * @param fundId   ID del fondo a cancelar.
     * @return Transaction creada.
     * @throws RuntimeException si hay errores de validación o no se encuentra la inversión.
     */
    @Transactional
    public Transaction cancelFund(String clientId, String fundId) { // Renombrado
        log.info("Iniciando cancelación: ClientID={}, FundID={}", clientId, fundId);

        // 1. Obtener Client
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client no encontrado con ID: " + clientId));

        // 2. Encontrar la inversión activa del cliente para este fondo
        Optional<Investment> investmentOptional = client.getActiveInvestments().stream() // Renombrado
                .filter(inv -> inv.getFundId().equals(fundId))
                .findFirst();

        if (investmentOptional.isEmpty()) {
            String errorMessage = "El cliente no tiene una inversión activa en el fondo con ID: " + fundId;
            log.warn(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        Investment investmentToCancel = investmentOptional.get(); // Renombrado
        BigDecimal amountToReturn = investmentToCancel.getInitialAmountInvested(); // Renombrado

        // 3. Crear Registro de Transaction de Cancelación
        Transaction transaction = new Transaction();
        transaction.setBusinessTransactionId(UUID.randomUUID().toString());
        transaction.setClientId(client.getId());
        transaction.setFundId(investmentToCancel.getFundId());
        transaction.setFundName(investmentToCancel.getFundName());
        transaction.setType(TransactionType.CANCELATION);
        transaction.setAmount(amountToReturn);
        transaction.setDate(LocalDateTime.now());
        transaction.setStatus("COMPLETADA");
        transaction.setClientBalanceBefore(client.getCurrentBalance());

        // 4. Actualizar Saldo del Client y Eliminar Investment
        client.setCurrentBalance(client.getCurrentBalance().add(amountToReturn));
        client.getActiveInvestments().remove(investmentToCancel); // Usando investmentToCancel
        clientRepository.save(client);

        transaction.setClientBalanceAfter(client.getCurrentBalance());
        Transaction savedTransaction = transactionRepository.save(transaction);

        // 5. Enviar Notificación (¡CAMBIOS AQUÍ PARA USAR email y phoneNumber!)
        String notificationMessage = String.format(
                "Estimado %s %s, la cancelación de su suscripción al fondo %s ha sido exitosa. " +
                        "Se ha retornado COP %.2f a su cuenta. Su nuevo saldo disponible es COP %.2f.",
                client.getFirstName(), client.getLastName(), investmentToCancel.getFundName(), amountToReturn, client.getCurrentBalance()
        );
        String emailSubject = "Confirmación de Cancelación de Suscripción";

        NotificationRequest notification = NotificationRequest.builder()
                .subject(emailSubject)
                .message(notificationMessage)
                .type(client.getNotificationPreference())
                .build();

        // Asignar el destinatario según la preferencia
        if (client.getNotificationPreference() == NotificationType.EMAIL) {
            if (client.getEmail() == null || client.getEmail().isEmpty()) {
                log.warn("Cliente {} prefiere email, pero no tiene una dirección de email registrada. No se enviará notificación.", client.getId());
            } else {
                notification.setAddressee(client.getEmail());
                notificationService.sendNotification(notification);
            }
        } else if (client.getNotificationPreference() == NotificationType.SMS) {
            if (client.getPhoneNumber() == null || client.getPhoneNumber().isEmpty()) {
                log.warn("Cliente {} prefiere SMS, pero no tiene un número de teléfono registrado. No se enviará notificación.", client.getId());
            } else {
                notification.setAddressee(client.getPhoneNumber());
                notificationService.sendNotification(notification);
            }
        } else {
            log.info("Cliente {} no desea notificaciones.", client.getId());
        }

        log.info("Cancelación completada y notificación enviada para ClientID={}, FundID={}", clientId, fundId);
        return savedTransaction;
    }

    /**
     * Obtiene el historial de transacciones para un cliente específico.
     *
     * @param clientId ID del cliente.
     * @return Lista de transacciones.
     */
    public List<Transaction> getTransactionsHistory(String clientId) {
        return transactionRepository.findByClientIdOrderByDateDesc(clientId);
    }
}