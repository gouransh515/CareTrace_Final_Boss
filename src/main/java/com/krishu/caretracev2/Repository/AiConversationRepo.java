package com.krishu.caretracev2.Repository;

import com.krishu.caretracev2.Model.AiConversation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AiConversationRepo extends MongoRepository<AiConversation,String> {
    Optional<AiConversation> findByPatientId(String patientId);
    void deleteByPatientId(String patientId);
}
