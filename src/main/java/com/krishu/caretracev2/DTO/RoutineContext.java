package com.krishu.caretracev2.DTO;

import com.krishu.caretracev2.Day;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class RoutineContext {
    private String routineName;
    private String description;
    private LocalTime time;
    private List<Day> days;
}
