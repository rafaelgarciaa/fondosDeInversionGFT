package com.pruebagft.gestionFondosGFT.controller.client;

import com.pruebagft.gestionFondosGFT.model.client.Client; // Import the Client entity
import com.pruebagft.gestionFondosGFT.model.client.dto.ClientRequestDTO;
import com.pruebagft.gestionFondosGFT.model.client.dto.ClientResponseDTO;
import com.pruebagft.gestionFondosGFT.model.error.ErrorResponse;
import com.pruebagft.gestionFondosGFT.model.investment.dto.InvestmentResponseDTO;
import com.pruebagft.gestionFondosGFT.service.client.ClientService; // Adjust import path
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid; // Import for validation
import org.springframework.beans.BeanUtils; // Utility for copying properties
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clients") // Base path for client-related endpoints
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * Creates a new client.
     * @param clientRequestDTO Client data for creation.
     * @return ResponseEntity with the created ClientResponseDTO.
     */
    @Operation(summary = "Create a new client", description = "Registers a new client in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Client created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))) // Document error response
    })
    @PostMapping
    public ResponseEntity<ClientResponseDTO> createClient(@Valid @RequestBody ClientRequestDTO clientRequestDTO) {
        Client client = new Client();
        // Copy properties from DTO to entity. Note: ID, currentBalance, activeInvestments are handled by the service/entity constructor
        BeanUtils.copyProperties(clientRequestDTO, client);

        // Manually set fields that might have different names or require specific logic
        client.setFirstName(clientRequestDTO.getFirstName()); // Assuming your Client entity has setFirstName
        client.setLastName(clientRequestDTO.getLastName());
        client.setCity(clientRequestDTO.getCity());
        client.setNotificationPreference(clientRequestDTO.getNotificationPreference());
        client.setEmail(clientRequestDTO.getEmail());
        client.setPhoneNumber(clientRequestDTO.getPhoneNumber());

        Client savedClient = clientService.createCliente(client);

        ClientResponseDTO responseDTO = mapClientToClientResponseDTO(savedClient);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }


    /**
     * Retrieves a client by their ID.
     * @param clientId The ID of the client to retrieve.
     * @return ResponseEntity with the ClientResponseDTO or 404 Not Found.
     */
    @Operation(summary = "Get client by ID", description = "Retrieves detailed information about a specific client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Client not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{clientId}")
    public ResponseEntity<ClientResponseDTO> getClientById(@PathVariable String clientId) {
        return clientService.getClienteById(clientId)
                .map(this::mapClientToClientResponseDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Retrieves all clients.
     * @return ResponseEntity with a list of ClientResponseDTOs.
     */
    @Operation(summary = "Get all clients", description = "Retrieves a list of all registered clients.")
    @ApiResponse(responseCode = "200", description = "List of clients retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ClientResponseDTO.class))) // Note: For lists, Springdoc understands List<DTO>
    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> getAllClients() {
        List<ClientResponseDTO> clients = clientService.getAllClientes().stream()
                .map(this::mapClientToClientResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(clients);
    }

    // Helper method to map Client entity to ClientResponseDTO
    private ClientResponseDTO mapClientToClientResponseDTO(Client client) {
        ClientResponseDTO dto = new ClientResponseDTO();
        BeanUtils.copyProperties(client, dto); // Copies matching fields by name

        // Map active investments if available
        if (client.getActiveInvestments() != null) {
            dto.setActiveInvestments(client.getActiveInvestments().stream()
                    .map(inv -> new InvestmentResponseDTO(
                            inv.getFundId(),
                            inv.getFundName(),
                            inv.getInitialAmountInvested(),
                            inv.getCurrentAmount(),
                            inv.getSubscriptionDate(),
                            inv.getTransactionId()))
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}