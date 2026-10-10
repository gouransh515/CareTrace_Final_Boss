package com.krishu.caretracev2.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlashCardAnswerRequest {
    private String personId;
    private String answer;
}
