package com.krishu.caretracev2.Service;

import com.krishu.caretracev2.CustomExceptions.UnauthorizedException;
import com.krishu.caretracev2.DTO.FlashCardAnswerRequest;
import com.krishu.caretracev2.DTO.FlashCardAnswerResponse;
import com.krishu.caretracev2.Model.FlashCardGameSession;
import com.krishu.caretracev2.Model.ImportantPerson;
import com.krishu.caretracev2.Model.Patient;
import com.krishu.caretracev2.Repository.FlashCardGameSessionRepo;
import com.krishu.caretracev2.Repository.ImportantPersonRepo;
import com.krishu.caretracev2.Repository.PatientRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FlashCardGameTestService {

    @Mock
    private FlashCardGameSessionRepo flashCardGameSessionRepo;
    @Mock
    private ImportantPersonService importantPersonService;
    @Mock
    private ImportantPersonRepo importantPersonRepo;
    @Mock
    private PatientRepo patientRepo;
    @Mock
    private Authentication authentication;

    @InjectMocks
    private FlashCardGameService flashCardGameService;

    private Patient patient;
    private ImportantPerson importantPerson;
    private FlashCardGameSession session;

    @BeforeEach
    void beforeSetUp(){
        patient=new Patient();
        patient.setId("patient-1");

        importantPerson=new ImportantPerson();
        importantPerson.setId("person-1");
        importantPerson.setPatientId("patient-1");
        importantPerson.setRelation("wife");
        importantPerson.setPhoto(new byte[]{1,2,3});

        session=new FlashCardGameSession();
        session.setId("session-1");
        session.setCompleted(false);
        session.setScore(0);
        session.setQuestinIndex(0);
        session.setPersonIds(List.of("person-1"));
        session.setPatientId("patient-1");

        when(authentication.getName()).thenReturn("user-1");
        when(patientRepo.findByUserId("user-1")).thenReturn(Optional.of(patient));
        when(flashCardGameSessionRepo.findById("session-1")).thenReturn(Optional.of(session));
    }

    @Test
    void submitAnswer_correctAnswer_increaseScoreAndCompleteGame(){
        FlashCardAnswerRequest request=new FlashCardAnswerRequest();
        request.setPersonId("person-1");
        request.setAnswer("wife");

        when(importantPersonRepo.findById("person-1")).thenReturn(Optional.of(importantPerson));
        when(flashCardGameSessionRepo.save(any(FlashCardGameSession.class))).thenAnswer(innovation
                -> innovation.getArgument(0));
        FlashCardAnswerResponse response=flashCardGameService.
                submitAnswer(request,authentication,"session-1");

        assertTrue(response.getCorrect());
        assertEquals(1,response.getScore());
        assertTrue(response.getCompleted());
        assertNull(response.getNextQuestion());

        assertEquals(1,session.getScore());
        assertEquals(1,session.getQuestinIndex());
        assertTrue(session.getCompleted());

        verify(flashCardGameSessionRepo).save(session);
    }

    @Test
    void submitAnswer_incorrectAnswer_doNotIncrementScore(){
        FlashCardAnswerRequest request=new FlashCardAnswerRequest();
        request.setPersonId("person-1");
        request.setAnswer("dad");

        when(importantPersonRepo.findById("person-1")).thenReturn(Optional.of(importantPerson));
        when(flashCardGameSessionRepo.save(any(FlashCardGameSession.class))).thenAnswer(innovation
                -> innovation.getArgument(0));

        FlashCardAnswerResponse response=flashCardGameService.
                submitAnswer(request,authentication,"session-1");
        assertFalse(response.getCorrect());
        assertEquals(0,response.getScore());
        assertTrue(response.getCompleted());
        assertNull(response.getNextQuestion());

        assertEquals(0,session.getScore());
        assertEquals(1,session.getQuestinIndex());
        assertTrue(session.getCompleted());

        verify(flashCardGameSessionRepo).save(session);
    }

    @Test
    void submitAnswer_otherPatientSession_throwUnauthorizedException(){
        session.setPatientId("another-patient");

        FlashCardAnswerRequest request=new FlashCardAnswerRequest();
        request.setPersonId("person-1");
        request.setAnswer("wife");

        assertThrows(UnauthorizedException.class,()->flashCardGameService.
                submitAnswer(request,authentication,"session-1"));

        verify(flashCardGameSessionRepo,never()).save(any(FlashCardGameSession.class));
    }

    @Test
    void submitAnswer_optimisticLocking(){
        FlashCardAnswerRequest request=new FlashCardAnswerRequest();
        request.setPersonId("person-1");
        request.setAnswer("wife");

        when(importantPersonRepo.findById("person-1")).thenReturn(Optional.of(importantPerson));
        when(flashCardGameSessionRepo.save(any(FlashCardGameSession.class)))
                .thenThrow(new OptimisticLockingFailureException("Concurrent Locking"));
        ResponseStatusException exception=assertThrows(ResponseStatusException.class,
                ()->flashCardGameService.submitAnswer(request,authentication,"session-1"));
        assertEquals(HttpStatus.CONFLICT,exception.getStatusCode());
    }
}
