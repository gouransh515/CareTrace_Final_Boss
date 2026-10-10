package com.krishu.caretracev2.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonForFlashCard {
    private byte[] photo;
    private String relation;
    private String personName;
}
