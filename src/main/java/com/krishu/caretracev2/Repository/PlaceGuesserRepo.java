package com.krishu.caretracev2.Repository;

import com.krishu.caretracev2.Model.PlaceGuesserSession;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PlaceGuesserRepo extends MongoRepository<PlaceGuesserSession,String> {
    List<PlaceGuesserSession> findByPatientId(String patientId);
}
