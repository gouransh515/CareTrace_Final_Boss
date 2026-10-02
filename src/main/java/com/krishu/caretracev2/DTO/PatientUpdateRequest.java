package com.krishu.caretracev2.DTO;

import com.krishu.caretracev2.PreferredLanguage;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PatientUpdateRequest {
    private Integer age;
    private PreferredLanguage language;
}
