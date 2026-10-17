package com.krishu.caretracev2.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlashCardAnswerRequest {
    @NotBlank(message="Patient id is required")
    private String personId;
    @NotBlank(message="Answer is required")
    @Size(max=100,message="Answer must not exceed 100 characters")
    private String answer;
}
