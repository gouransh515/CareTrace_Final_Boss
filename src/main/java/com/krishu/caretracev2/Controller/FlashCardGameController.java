package com.krishu.caretracev2.Controller;

import com.krishu.caretracev2.DTO.FlashCardAnswerRequest;
import com.krishu.caretracev2.DTO.FlashCardAnswerResponse;
import com.krishu.caretracev2.DTO.FlashCardGameSummaryResponse;
import com.krishu.caretracev2.DTO.FlashCardQuestionResponse;
import com.krishu.caretracev2.Service.FlashCardGameService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/flashCard")
public class FlashCardGameController {

    private final FlashCardGameService flashCardService;

    public FlashCardGameController(FlashCardGameService flashCardService){
        this.flashCardService=flashCardService;
    }

    @PostMapping("/startGame")
    public ResponseEntity<FlashCardQuestionResponse> startGame(Authentication authentication){
        return ResponseEntity.ok(flashCardService.startGame(authentication));
    }

    @PostMapping("/submitAnswer/{sessionId}")
    public ResponseEntity<FlashCardAnswerResponse> submitAnswer(Authentication authentication,
                                                                @PathVariable String sessionId,
                                                                @Valid @RequestBody FlashCardAnswerRequest request){
        return ResponseEntity.ok(flashCardService.submitAnswer(request,authentication,sessionId));
    }

    @GetMapping("/getSummary/{sessionId}")
    public ResponseEntity<FlashCardGameSummaryResponse> getGameSummary(@PathVariable String sessionId,
                                                                       Authentication authentication){
        return ResponseEntity.ok(flashCardService.getGameSummary(sessionId,authentication));
    }
}
