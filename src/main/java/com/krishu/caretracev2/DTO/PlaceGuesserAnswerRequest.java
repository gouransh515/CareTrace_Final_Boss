package com.krishu.caretracev2.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlaceGuesserAnswerRequest {
    private String placeId;
    private String answer;
}
