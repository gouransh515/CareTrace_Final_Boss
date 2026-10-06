package com.krishu.caretracev2.Service;

import com.google.genai.Client;
import com.google.genai.gaos.models.interactions.*;
import com.google.genai.gaos.models.operations.CreateInteractionRequestBody;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeminiService {

    private final Client client;
    private final AiConversationService aiConversationService;

    public GeminiService(Client client, AiConversationService aiConversationService) {
        this.client = client;
        this.aiConversationService = aiConversationService;
    }

    public Interaction generateResponse(String prompt,String interactionId) {

        CreateModelInteraction.Builder request =
                CreateModelInteraction.builder()
                        .model(Model.of("gemini-3.5-flash"))
                        .input(InteractionsInput.of(prompt))
                        .systemInstruction("Answer only what the patient asks, directly and briefly.\n" +
                                "Do not greet or list all their details unless they ask for them.\n" +
                                "If the answer is not in the information, say you don't know.")
                        ;

        if(interactionId!=null){
            request.previousInteractionId(interactionId);
        }

        return client.interactions
                        .create(CreateInteractionRequestBody.of(request.build()))
                        .interaction()
                        .get();
    }
}