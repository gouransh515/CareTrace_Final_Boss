package com.krishu.caretracev2.Service;

import com.krishu.caretracev2.CustomExceptions.NotFoundException;
import com.krishu.caretracev2.DTO.*;
import com.krishu.caretracev2.Model.*;
import com.krishu.caretracev2.Repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContextService {

    private final PatientRepo patientRepo;
    private final ImportantPersonRepo importantPersonRepo;
    private final RoutineRepo routineRepo;
    private final MedicationRepo medicationRepo;
    private final ReminderRepo reminderRepo;

    public ContextService(PatientRepo patientRepo, ImportantPersonRepo importantPersonRepo, RoutineRepo routineRepo, MedicationRepo medicationRepo, ReminderRepo reminderRepo) {
        this.patientRepo = patientRepo;
        this.importantPersonRepo = importantPersonRepo;
        this.routineRepo = routineRepo;
        this.medicationRepo = medicationRepo;
        this.reminderRepo = reminderRepo;
    }

    public PatientContext createContext(Authentication authentication){
        String userId=authentication.getName();
        Patient patient=patientRepo.findByUserId(userId).orElseThrow(()->new NotFoundException("Patient not found"));
        String patientId=patient.getId();
        List<ImportantPerson> importantPersons=importantPersonRepo.findByPatientId(patientId);
        List<Routine> routines=routineRepo.findByPatientId(patientId);
        List<Medication> medications=medicationRepo.findByPatientId(patientId);
        List<Reminder> reminders=reminderRepo.findByPatientId(patientId);
        PatientContext context=new PatientContext();
        context.setPatientId(patientId);
        context.setAge(patient.getAge());
        context.setLanguage(patient.getPreferred_language());
        context.setImportantPersons(importantPersons.stream().map(this::mapToImportantPersonContext).toList());
        context.setRoutines(routines.stream().map(this::mapToRoutineContext).toList());
        context.setMedications(medications.stream().map(this::mapToMedicationContext).toList());
        context.setReminders(reminders.stream().map(this::mapToReminderContext).toList());
        return context;
    }

    private ImportantPersonContext mapToImportantPersonContext(ImportantPerson importantPerson){
        ImportantPersonContext context=new ImportantPersonContext();
        context.setName(importantPerson.getName());
        context.setRelation(importantPerson.getRelation());
        return context;
    }

    private RoutineContext mapToRoutineContext(Routine routine){
        RoutineContext context=new RoutineContext();
        context.setRoutineName(routine.getTitle());
        context.setDays(routine.getDays());
        context.setDescription(routine.getDescription());
        context.setTime(routine.getTime());
        return context;
    }

    private MedicationContext mapToMedicationContext(Medication medication){
        MedicationContext context=new MedicationContext();
        context.setName(medication.getName());
        context.setInstructions(medication.getInstructions());
        context.setDosage(medication.getDosage());
        context.setFrequency(medication.getFrequency());
        return context;
    }

    private ReminderContext mapToReminderContext(Reminder reminder){
        ReminderContext context=new ReminderContext();
        context.setDate(reminder.getDate());
        context.setTime(reminder.getTime());
        context.setReminderType(reminder.getType());
        context.setMedicationId(reminder.getMedicationId());
        context.setRoutineId(reminder.getRoutineId());
        return context;
    }
}
