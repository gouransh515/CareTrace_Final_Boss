package com.krishu.caretracev2.Controller;

import com.krishu.caretracev2.DTO.AiRequest;
import com.krishu.caretracev2.DTO.AiResponse;
import com.krishu.caretracev2.Service.AiService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService){
        this.aiService=aiService;
    }

    @PostMapping("/ask")
    public ResponseEntity<AiResponse> askAnything(Authentication authentication, AiRequest request){
        return ResponseEntity.ok(aiService.chat(request,authentication));
    }
}
