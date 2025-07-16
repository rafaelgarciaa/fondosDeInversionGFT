package com.pruebagft.gestionFondosGFT.service.fund;

import com.pruebagft.gestionFondosGFT.model.fund.Fund;
import com.pruebagft.gestionFondosGFT.repository.fund.FundRepository;
import jakarta.annotation.PostConstruct; // Importación correcta para PostConstruct
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FundService {

    private final FundRepository fondoRepository;

    @Autowired
    public FundService(FundRepository fondoRepository) {
        this.fondoRepository = fondoRepository;
    }

    /**
     * Carga fondos iniciales si la base de datos está vacía.
     * Se ejecuta automáticamente después de que el bean sea inicializado por Spring.
     */
    @PostConstruct
    public void initializeFunds() {
        if (fondoRepository.count() == 0) {
            // Ejemplo de fondos iniciales con sus montos mínimos de vinculación
            Fund fondo1 = new Fund("1", "Fondo BTG Liquidez", "FPV", new BigDecimal("100000.00")); // $100,000
            Fund fondo2 = new Fund("2", "Fondo BTG Acciones", "FIC", new BigDecimal("250000.00")); // $250,000
            Fund fondo3 = new Fund("3", "Fondo BTG Renta Fija", "FPV", new BigDecimal("150000.00")); // $150,000
            Fund fondo4 = new Fund("4", "Fondo BTG Global", "FIC", new BigDecimal("300000.00")); // $300,000

            fondoRepository.saveAll(List.of(fondo1, fondo2, fondo3, fondo4));
            System.out.println("Fondos iniciales cargados.");
        }
    }

    public List<Fund> getAllFunds() {
        return fondoRepository.findAll();
    }

    public Fund getFondoById(String id) {
        return fondoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fondo no encontrado con ID: " + id)); // Implementar una excepción personalizada más adelante
    }
}