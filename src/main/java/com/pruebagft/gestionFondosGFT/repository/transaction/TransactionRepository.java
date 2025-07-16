package com.pruebagft.gestionFondosGFT.repository.transaction;

import com.pruebagft.gestionFondosGFT.model.transaction.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    List<Transaction> findByClientIdOrderByDateDesc(String clienteId);
}
