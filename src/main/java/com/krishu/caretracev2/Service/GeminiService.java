package com.krishu.caretracev2.Service;

import com.google.genai.Client;
import com.google.genai.gaos.models.interactions.*;
import com.google.genai.gaos.models.operations.CreateInteractionRequestBody;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeminiService {

    private final Client client;

    public GeminiService(Client client) {
        this.client = client;
    }

    public String generateResponse(String prompt) {

        CreateModelInteraction request =
                CreateModelInteraction.builder()
                        .model(Model.of("gemini-3.5-flash"))
                        .input(InteractionsInput.of(prompt))
                        .systemInstruction("Answer only what the patient asks, directly and briefly.\n" +
                                "Do not greet or list all their details unless they ask for them.\n" +
                                "If the answer is not in the information, say you don't know.")
                        .build();

        Interaction interaction =
                client.interactions
                        .create(CreateInteractionRequestBody.of(request))
                        .interaction()
                        .get();

        String direct = interaction.outputText().orElse("");
        if (!direct.isEmpty()) {
            return direct;
        }

        StringBuilder sb = new StringBuilder();
        for (var step : interaction.steps().orElse(List.of())) {
            if (step instanceof ModelOutputStep modelStep) {
                for (var content : modelStep.content().orElse(List.of())) {
                    if (content instanceof TextContent textContent) {
                        sb.append(textContent.text().orElse(""));
                    }
                }
            }
        }
        return sb.toString();
    }
}