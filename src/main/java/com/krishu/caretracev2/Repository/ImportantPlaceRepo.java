package com.krishu.caretracev2.Repository;

import com.krishu.caretracev2.Model.ImportantPlace;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ImportantPlaceRepo extends MongoRepository<ImportantPlace,String> {
    List<ImportantPlace> findByPatientId(String patientId);
}
