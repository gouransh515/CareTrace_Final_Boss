package com.krishu.caretracev2.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ImportantPersonRequest {
    private String name;
    private String relation;
    private Long phoneNo;
}
