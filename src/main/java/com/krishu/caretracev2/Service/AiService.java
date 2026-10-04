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

    private String buildPrompt(PatientContext context, String userMessage) {
        System.out.println("USER MESSAGE = [" + userMessage + "]");
        StringBuilder prompt = new StringBuilder();

        prompt.append("""
            You are an AI assistant for a dementia care application.

            Use the following patient information to answer the patient's question.
            Only use information provided in this context.
            Do not invent patient details or medical information.
            Keep your response simple, friendly and easy to understand.

            PATIENT INFORMATION
            """);

        prompt.append("\nAge: ").append(context.getAge());
        prompt.append("\nPreferred Language: ").append(context.getLanguage());

        prompt.append("\n\nIMPORTANT PEOPLE:\n");

        if (context.getImportantPersons() == null || context.getImportantPersons().isEmpty()) {
            prompt.append("No important people information available.\n");
        } else {
            context.getImportantPersons().forEach(person -> {
                prompt.append("- Name: ")
                        .append(person.getName())
                        .append(", Relation: ")
                        .append(person.getRelation())
                        .append("\n");
            });
        }

        prompt.append("\nMEDICATIONS:\n");

        if (context.getMedications() == null || context.getMedications().isEmpty()) {
            prompt.append("No medication information available.\n");
        } else {
            context.getMedications().forEach(medication -> {
                prompt.append("- Name: ")
                        .append(medication.getName())
                        .append(", Dosage: ")
                        .append(medication.getDosage())
                        .append(", Frequency: ")
                        .append(medication.getFrequency())
                        .append(", Instructions: ")
                        .append(medication.getInstructions())
                        .append("\n");
            });
        }

        prompt.append("\nROUTINES:\n");
        if (context.getRoutines() == null || context.getRoutines().isEmpty()) {
            prompt.append("No routine information available.\n");
        } else {
            context.getRoutines().forEach(routine -> {
                prompt.append("- ")
                        .append(routine.getRoutineName())
                        .append(" at ")
                        .append(routine.getTime())
                        .append(", Days: ")
                        .append(routine.getDays())
                        .append(", Description: ")
                        .append(routine.getDescription())
                        .append("\n");
            });
        }


        prompt.append("\nREMINDERS:\n");
        if (context.getReminders() == null || context.getReminders().isEmpty()) {
            prompt.append("No reminder information available.\n");
        } else {
            context.getReminders().forEach(reminder -> {
                prompt.append("- Type: ")
                        .append(reminder.getReminderType())
                        .append(", Date: ")
                        .append(reminder.getDate())
                        .append(", Time: ")
                        .append(reminder.getTime())
                        .append("\n");
            });
        }


        prompt.append("\nPATIENT'S QUESTION:\n");
        prompt.append(userMessage);
        prompt.append("\n\nANSWER:");
        return prompt.toString();
    }
}
