package com.krishu.caretracev2.Service;


import com.krishu.caretracev2.Model.AiConversation;
import com.krishu.caretracev2.Repository.AiConversationRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AiConversationService {

    private final AiConversationRepo aiConversationRepo;
    @Value("${expirtaion.duration}")
    private Long expirationDuration;

    public AiConversationService(AiConversationRepo aiConversationRepo){
        this.aiConversationRepo=aiConversationRepo;
    }

    public String getLastInteractionId(String patientId){
        Optional<AiConversation> optionalConversation=aiConversationRepo.findByPatientId(patientId);
        if(optionalConversation.isEmpty()){
            return null;
        }
        AiConversation conversation=optionalConversation.get();
        LocalDateTime updateAt=conversation.getUpdatedAt();
        if(updateAt.isBefore(LocalDateTime.now().minusDays(expirationDuration))){
            aiConversationRepo.deleteByPatientId(patientId);
            return null;
        }
        return conversation.getInteractionId();
    }

    public void saveInteraction(String patientId,String interactionId){
        AiConversation conversation=aiConversationRepo.findByPatientId(patientId).orElseGet(AiConversation::new);
        conversation.setInteractionId(interactionId);
        conversation.setPatientId(patientId);
        conversation.setUpdatedAt(LocalDateTime.now());
        aiConversationRepo.save(conversation);
    }
}
