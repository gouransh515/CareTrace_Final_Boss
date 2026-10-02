package com.krishu.caretracev2.Service;

import com.google.genai.Client;
import com.google.genai.gaos.models.interactions.CreateModelInteraction;
import com.google.genai.gaos.models.interactions.Interaction;
import com.google.genai.gaos.models.interactions.InteractionsInput;
import com.google.genai.gaos.models.operations.CreateInteractionRequestBody;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final Client client;

    public GeminiService(Client client){
        this.client=client;
    }

    public String generateResponse(String prompt){
        CreateModelInteraction request=CreateModelInteraction.builder().model("gemini-2.5-flash").input(InteractionsInput.of(prompt)).
                systemInstruction("You are a helpful AI assistant for a dementia care application.").build();
        Interaction interaction=client.interactions.create(CreateInteractionRequestBody.of(request)).interaction().get();
        return interaction.outputText().orElse("");
    }
}
