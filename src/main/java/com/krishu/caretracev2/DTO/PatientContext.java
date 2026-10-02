package com.krishu.caretracev2.DTO;

import com.krishu.caretracev2.PreferredLanguage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PatientContext {
    private String patientId;
    private Integer age;
    private PreferredLanguage language;
    private List<ImportantPersonContext> importantPersons;
    private List<MedicationContext> medications;
    private List<RoutineContext> routines;
    private List<ReminderContext> reminders;
}
