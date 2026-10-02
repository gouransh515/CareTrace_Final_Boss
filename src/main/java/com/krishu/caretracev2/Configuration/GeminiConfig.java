package com.krishu.caretracev2.Configuration;

import com.google.genai.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiConfig {

    @Value("${gemini.key}")
    private String apiKey;

    @Bean
    public Client getGeminiClient(){
        return Client.builder().apiKey(apiKey).build();
    }
}
