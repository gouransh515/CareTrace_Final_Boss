package com.krishu.caretracev2.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlaceGuesserSummaryResponse {
    private String sessionId;
    private Integer totalQuestions;
    private Integer Score;
    private Boolean completed;
}
