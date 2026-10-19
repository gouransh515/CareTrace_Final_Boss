package com.krishu.caretracev2.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlaceGuesserAnswerResponse {
    private Boolean correct;
    private String correctAnswer;
    private String message;
    private Integer score;
    private Boolean completed;
    private PlaceGuesserQuestionResponse nextQuestion;
}
