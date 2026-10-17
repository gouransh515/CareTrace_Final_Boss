package com.krishu.caretracev2.Service;

import com.krishu.caretracev2.CustomExceptions.*;
import com.krishu.caretracev2.DTO.*;
import com.krishu.caretracev2.Model.FlashCardGameSession;
import com.krishu.caretracev2.Model.ImportantPerson;
import com.krishu.caretracev2.Model.Patient;
import com.krishu.caretracev2.Repository.FlashCardGameSessionRepo;
import com.krishu.caretracev2.Repository.ImportantPersonRepo;
import com.krishu.caretracev2.Repository.PatientRepo;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FlashCardGameService {

    private final FlashCardGameSessionRepo flashCardGameSessionRepo;
    private final ImportantPersonService importantPersonService;
    private final ImportantPersonRepo importantPersonRepo;
    private final PatientRepo patientRepo;

    public FlashCardGameService(FlashCardGameSessionRepo flashCardGameSessionRepo,
                                ImportantPersonService importantPersonService,
                                PatientRepo patientRepo,ImportantPersonRepo importantPersonRepo){
        this.flashCardGameSessionRepo=flashCardGameSessionRepo;
        this.importantPersonService=importantPersonService;
        this.patientRepo=patientRepo;
        this.importantPersonRepo=importantPersonRepo;
    }

    public FlashCardQuestionResponse startGame(Authentication authentication){
        Patient patient=patientRepo.findByUserId(authentication.getName()).
                orElseThrow(()->new NotFoundException("Patient not found"));
        List<ImportantPerson> persons=importantPersonService.getPersonsPhotos(patient.getId());
        if(persons.isEmpty()){
            throw new NotFoundException("No important people with photos are available for the game");
        }
        Collections.shuffle(persons);
        List<String> personIds=persons.stream().map(ImportantPerson::getId).toList();
        FlashCardGameSession session=new FlashCardGameSession();
        session.setCompleted(false);
        session.setPatientId(patient.getId());
        session.setPersonIds(personIds);
        session.setQuestinIndex(0);
        session.setScore(0);

        session=flashCardGameSessionRepo.save(session);
        return buildQuestion(session);
    }

    public FlashCardAnswerResponse submitAnswer(FlashCardAnswerRequest request,
                                                Authentication authentication,String sessionId){
        Patient patient=patientRepo.findByUserId(authentication.getName()).
                orElseThrow(()->new NotFoundException("Patient not found"));
        FlashCardGameSession session=flashCardGameSessionRepo.findById(sessionId).
                orElseThrow(()->new NotFoundException("Game Session not found"));
        if(!session.getPatientId().equals(patient.getId())){
            throw new UnauthorizedException("This game session do not belong to you");
        }

        if(session.getCompleted()){
            throw new GameAlreadyCompletedException("Game is already completed");
        }

        int index=session.getQuestinIndex();
        if(index<0 || index>=session.getPersonIds().size()){
            throw new OutOfRangeException("Invalid Question index");
        }

        String currentPersonId=session.getPersonIds().get(index);
        if(!currentPersonId.equals(request.getPersonId())){
            throw new IllegalArgumentException("This is not current question");
        }

        ImportantPerson currentImportantPerson=importantPersonRepo.findById(currentPersonId).
                orElseThrow(()->new NotFoundException("Person not found"));
        if(!patient.getId().equals(currentImportantPerson.getPatientId())){
            throw new UnauthorizedException("Person do not belong to this Patient");
        }
        boolean correct=(currentImportantPerson.getRelation()!=null &&
                currentImportantPerson.getRelation().
                        equalsIgnoreCase(request.getAnswer()==null?"":request.getAnswer().trim()));
        if(correct){
            session.setScore(session.getScore()+1);
        }
        session.setQuestinIndex(session.getQuestinIndex()+1);
        FlashCardAnswerResponse response=new FlashCardAnswerResponse();
        response.setCorrect(correct);
        response.setMessage(correct?"Correct Answer!!":"Wrong Answer!!");
        response.setScore(session.getScore());
        if(session.getQuestinIndex()>=session.getPersonIds().size()){
            session.setCompleted(true);
            response.setCompleted(true);
            response.setNextQuestion(null);
        }else{
            response.setCompleted(false);
            response.setNextQuestion(buildQuestion(session));
        }
        try {
            flashCardGameSessionRepo.save(session);
        } catch (OptimisticLockingFailureException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This question has already been " +
                    "answered or the game state has changed.");
        }
        return response;
    }

    public FlashCardGameSummaryResponse getGameSummary(String sessionId, Authentication authentication){
        FlashCardGameSession session=flashCardGameSessionRepo.findById(sessionId).
                orElseThrow(()->new NotFoundException("Session not found"));
        Patient patient=patientRepo.findByUserId(authentication.getName()).
                orElseThrow(()->new NotFoundException("Patient not found"));
        if(!patient.getId().equals(session.getPatientId())){
            throw new UnauthorizedException("Patient is not authorized for this session");
        }
        if(!session.getCompleted()){
            throw new GameNotCompleteException("Game is not completed yet");
        }

        FlashCardGameSummaryResponse response=new FlashCardGameSummaryResponse();
        response.setCompleted(true);
        response.setScore(session.getScore());
        response.setSessionId(session.getId());
        response.setTotalQuestion(session.getPersonIds().size());

        return response;
    }

    private FlashCardQuestionResponse buildQuestion(FlashCardGameSession session){
        String personId=session.getPersonIds().get(session.getQuestinIndex());
        ImportantPerson person=importantPersonRepo.findById(personId).orElseThrow(()->
                new NotFoundException("Person not found"));
        List<ImportantPerson> persons=importantPersonRepo.findByPatientId(session.getPatientId());
        List<String> options=persons.stream().map(ImportantPerson::getRelation).filter(Objects::nonNull)
                .filter(relation->!relation.isBlank()).distinct().
                collect(Collectors.toCollection(ArrayList::new));
        if(!options.contains(person.getRelation())){
            options.add(person.getRelation());
        }
        Collections.shuffle(options);
        if(options.size()>4){
            options.remove(person.getRelation());
            List<String> selectedOptions=new ArrayList<>(options.subList(0,3));
            options=selectedOptions;
            options.add(person.getRelation());
            Collections.shuffle(options);
        }

        FlashCardQuestionResponse response=new FlashCardQuestionResponse();
        response.setOptions(options);
        response.setPersonId(personId);
        response.setPhoto(person.getPhoto());
        response.setQuestionNumber(session.getQuestinIndex()+1);
        response.setSessionId(session.getId());
        response.setTotalQuestions(session.getPersonIds().size());

        return response;
    }
}
