package com.pruebagft.gestionFondosGFT.controller.transaction;
import com.pruebagft.gestionFondosGFT.model.error.ErrorResponse;
import com.pruebagft.gestionFondosGFT.model.suscription.dto.CancellationRequestDTO;
import com.pruebagft.gestionFondosGFT.model.suscription.dto.SubscriptionRequestDTO;
import com.pruebagft.gestionFondosGFT.model.transaction.Transaction;
import com.pruebagft.gestionFondosGFT.model.transaction.dto.TransactionResponseDTO;
import com.pruebagft.gestionFondosGFT.service.transaction.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions")
@Slf4j
@Tag(name = "Transaction Management", description = "Operations for fund subscriptions, cancellations, and transaction history") // Tag for transaction endpoints
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Handles a fund subscription request.
     * Requires the authenticated user to have either 'USER' or 'ADMIN' role.
     *
     * @param requestDTO Subscription details.
     * @return ResponseEntity with the created TransactionResponseDTO.
     */
    @Operation(summary = "Subscribe to a fund",
            description = "Allows a client to subscribe to an investment fund with a specified amount. Requires 'USER' or 'ADMIN' role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Fund subscribed successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or business rule violation (e.g., insufficient balance, fund not found, minimum amount not met)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have the required role")
    })
    @PostMapping("/subscribe")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<TransactionResponseDTO> subscribeFund(@Valid @RequestBody SubscriptionRequestDTO requestDTO) {
        // The RuntimeException will now be caught by GlobalExceptionHandler
        Transaction transaction = transactionService.subscribeFund(
                requestDTO.getClientId(),
                requestDTO.getFundId(),
                requestDTO.getAmount()
        );
        TransactionResponseDTO responseDTO = mapTransactionToTransactionResponseDTO(transaction);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    /**
     * Handles a fund cancellation request.
     * Requires the authenticated user to have 'USER' role or 'ADMIN' role.
     *
     * @param requestDTO Cancellation details.
     * @return ResponseEntity with the created TransactionResponseDTO.
     */
    @Operation(summary = "Cancel a fund subscription",
            description = "Allows a client to cancel their subscription from an investment fund. Requires 'USER' or 'ADMIN' role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fund cancellation initiated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or business rule violation (e.g., investment not found, fund not active)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have the required role")
    })
    @PostMapping("/cancel")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<TransactionResponseDTO> cancelFund(@Valid @RequestBody CancellationRequestDTO requestDTO) {
        // The RuntimeException will now be caught by GlobalExceptionHandler
        Transaction transaction = transactionService.cancelFund(
                requestDTO.getClientId(),
                requestDTO.getFundId()
        );
        TransactionResponseDTO responseDTO = mapTransactionToTransactionResponseDTO(transaction);
        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Retrieves the transaction history for a specific client.
     * Requires 'ADMIN' role to view any client's history, or 'USER' role to view their own history.
     *
     * @param clientId The ID of the client.
     * @return ResponseEntity with a list of TransactionResponseDTOs.
     */
    @Operation(summary = "Get client transaction history",
            description = "Retrieves a list of all transactions (subscriptions and cancellations) for a specific client. Access is restricted by role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction history retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TransactionResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have the required role or is not authorized for this client ID"),
            @ApiResponse(responseCode = "404", description = "Client not found (if implemented in service)")
    })
    @GetMapping("/history/{clientId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #clientId == authentication.name)")
    public ResponseEntity<List<TransactionResponseDTO>> getClientTransactionHistory(@PathVariable String clientId) {
        List<Transaction> transactions = transactionService.getTransactionsHistory(clientId);
        List<TransactionResponseDTO> responseDTOs = transactions.stream()
                .map(this::mapTransactionToTransactionResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    // Helper method to map Transaction entity to TransactionResponseDTO
    private TransactionResponseDTO mapTransactionToTransactionResponseDTO(Transaction transaction) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        BeanUtils.copyProperties(transaction, dto);
        return dto;
    }
}