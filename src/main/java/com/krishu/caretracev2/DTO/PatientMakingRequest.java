package com.krishu.caretracev2.DTO;

import com.krishu.caretracev2.PreferredLanguage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PatientMakingRequest {
    private String name;
    private String email;
    private String password;
    private Integer age;
    private PreferredLanguage preferredLanguage;
}
