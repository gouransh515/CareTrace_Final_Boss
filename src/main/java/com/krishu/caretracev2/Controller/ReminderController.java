package com.krishu.caretracev2.Controller;

import com.krishu.caretracev2.DTO.ReminderRequest;
import com.krishu.caretracev2.DTO.ReminderResponse;
import com.krishu.caretracev2.Service.ReminderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reminder")
public class ReminderController {

    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @PostMapping("/createReminder/{patientId}")
    public ResponseEntity<ReminderResponse> createReminder(@RequestBody ReminderRequest request, @PathVariable String patientId, Authentication authentication){
        return ResponseEntity.ok(reminderService.createReminder(request,patientId,authentication));
    }

    @GetMapping("/getReminders/{patientId}")
    public ResponseEntity<List<ReminderResponse>> getPatientReminders(@PathVariable String patientId,Authentication authentication){
        return ResponseEntity.ok(reminderService.getPatientReminders(patientId,authentication));
    }

    @PutMapping("/updateReminder/{reminderId}/{patient}")
    public ResponseEntity<ReminderResponse> updateReminder(String reminderId,String patientId,ReminderRequest request,Authentication authentication){
        return ResponseEntity.ok(reminderService.updateReminder(reminderId,patientId,request,authentication));
    }

    @DeleteMapping("/deleteReminder/{reminderId}/{patientId}")
    public void deleteReminder(@PathVariable String reminderId,@PathVariable String patientId,Authentication authentication){
        reminderService.deleteReminder(reminderId,patientId,authentication);
    }

    @GetMapping("patient/me")
    public ResponseEntity<List<ReminderResponse>> getReminderForPatient(Authentication authentication){
        return ResponseEntity.ok(reminderService.getReminderForPatient(authentication));
    }
}
