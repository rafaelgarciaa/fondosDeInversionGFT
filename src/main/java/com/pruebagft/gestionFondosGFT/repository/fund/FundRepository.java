package com.pruebagft.gestionFondosGFT.repository.fund;

import com.pruebagft.gestionFondosGFT.model.fund.Fund;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FundRepository extends MongoRepository<Fund, String> {
}
