package com.pruebagft.gestionFondosGFT.repository.client;

import com.pruebagft.gestionFondosGFT.model.client.Client;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends MongoRepository<Client, String> {
}
