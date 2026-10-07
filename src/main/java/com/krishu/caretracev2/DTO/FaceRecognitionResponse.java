package com.krishu.caretracev2.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class FaceRecognitionResponse {
    private String name;
    private String relation;
    private Float similarity;
}
