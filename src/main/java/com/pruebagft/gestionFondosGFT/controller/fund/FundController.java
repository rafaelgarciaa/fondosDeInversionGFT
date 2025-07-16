package com.pruebagft.gestionFondosGFT.controller.fund;
import com.pruebagft.gestionFondosGFT.model.fund.Fund; // Import the Fund entity
import com.pruebagft.gestionFondosGFT.model.fund.dto.FundResponseDTO;
import com.pruebagft.gestionFondosGFT.service.fund.FundService; // Adjust import path
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/funds")
@Tag(name = "Fund Information", description = "Operations to retrieve information about available investment funds")

public class FundController {

    private final FundService fundService;

    @Autowired
    public FundController(FundService fundService) {
        this.fundService = fundService;
    }

    @Operation(summary = "Get all available funds", description = "Retrieves a list of all investment funds offered by GFT.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of funds",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = FundResponseDTO.class)))
    @GetMapping
    public ResponseEntity<List<FundResponseDTO>> getAllFunds() {
        List<Fund> funds = fundService.getAllFunds();
        // Map Fund entities to FundResponseDTOs
        List<FundResponseDTO> fundDTOs = funds.stream()
                .map(fund -> new FundResponseDTO(
                        fund.getId(),
                        fund.getName(),
                        fund.getProductType(),
                        fund.getMinimumSubscriptionAmount()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(fundDTOs);
    }
}