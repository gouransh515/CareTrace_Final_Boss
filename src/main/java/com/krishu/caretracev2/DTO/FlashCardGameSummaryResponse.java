package com.krishu.caretracev2.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlashCardGameSummaryResponse {
    private String sessionId;
    private Integer score;
    private Integer totalQuestion;
    private Boolean completed;
}
