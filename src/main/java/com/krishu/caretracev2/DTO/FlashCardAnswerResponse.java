package com.krishu.caretracev2.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlashCardAnswerResponse {
    private Boolean correct;
    private String message;
    private int score;
    private Boolean completed;
    private FlashCardQuestionResponse nextQuestion;
}
