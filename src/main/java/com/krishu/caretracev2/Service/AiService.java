package com.krishu.caretracev2.Service;

import com.krishu.caretracev2.DTO.AiRequest;
import com.krishu.caretracev2.DTO.AiResponse;
import com.krishu.caretracev2.DTO.PatientContext;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    private final ContextService contextService;
    private final GeminiService geminiService;

    public AiService(ContextService contextService,GeminiService geminiService){
        this.contextService=contextService;
        this.geminiService=geminiService;
    }

    public AiResponse chat(AiRequest request, Authentication authentication){
        PatientContext context=contextService.createContext(authentication);
        String prompt=buildPrompt(context,request.getMessage());
        String response=geminiService.generateResponse(prompt);
        return new AiResponse(response);
    }

    private String buildPrompt(PatientContext context,String aiRequest){

        return """
                You are an AI assistant for a dementia care application.

                Patient information:
                Age: %s
                Preferred language: %s

                Important people:
                %s

                Medications:
                %s

                Routines:
                %s

                Reminders:
                %s

                Patient's question:
                %s

                Answer the patient in a simple, friendly and clear way.
                Use the patient information when it is relevant.
                Do not invent medical information or patient details.
                """
                .formatted(
                        context.getAge(),
                        context.getLanguage(),
                        context.getImportantPersons(),
                        context.getMedications(),
                        context.getRoutines(),
                        context.getReminders(),
                        aiRequest
                );
    }
}
