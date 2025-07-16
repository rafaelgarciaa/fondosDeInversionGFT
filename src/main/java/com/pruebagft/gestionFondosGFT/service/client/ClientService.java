package com.pruebagft.gestionFondosGFT.service.client;
import com.pruebagft.gestionFondosGFT.model.client.Client;
import com.pruebagft.gestionFondosGFT.repository.client.ClientRepository;
import com.pruebagft.gestionFondosGFT.util.enums.NotificationType;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clienteRepository) {
        this.clientRepository = clienteRepository;
    }

    public List<Client> getAllClientes() {
        return clientRepository.findAll();
    }

    public Optional<Client> getClienteById(String id) {
        return clientRepository.findById(id);
    }

    public Client createCliente(Client client) {
        // Asegurarse de que el saldo inicial sea el especificado si no viene
        if (client.getCurrentBalance() == null) {
            client.setCurrentBalance(new BigDecimal("500000.00")); // Monto inicial para nuevos clientes
        }
        if (client.getActiveInvestments() == null) {
            client.setActiveInvestments(new ArrayList<>());
        }
        return clientRepository.save(client);
    }

    // Método para crear un cliente de prueba rápidamente si la DB está vacía
    @PostConstruct
    public void initializeClientes() {
        if (clientRepository.count() == 0) {
            Client cliente1 = new Client("CLIENTE001", "Juan", "Perez", "Bogotá", NotificationType.EMAIL, "573001234567", "juan.perez@example.com");
            Client cliente2 = new Client("CLIENTE002", "Maria", "Gomez", "Medellín", NotificationType.SMS, "573109876543", "maria.gomez@example.com");
            clientRepository.saveAll(List.of(cliente1, cliente2));
            System.out.println("Clientes iniciales cargados.");
        }
    }

    public Client updateCliente(Client cliente) {
        return clientRepository.save(cliente);
    }
}