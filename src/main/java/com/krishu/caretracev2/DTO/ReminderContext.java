package com.krishu.caretracev2.DTO;

import com.krishu.caretracev2.ReminderType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ReminderContext {
    private LocalTime time;
    private LocalDate date;
    private ReminderType reminderType;
    private String medicationId;
    private String routineId;
}
