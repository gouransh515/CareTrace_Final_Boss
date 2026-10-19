package com.krishu.caretracev2.Service;

import com.krishu.caretracev2.CustomExceptions.GameAlreadyCompletedException;
import com.krishu.caretracev2.CustomExceptions.NotFoundException;
import com.krishu.caretracev2.CustomExceptions.OutOfRangeException;
import com.krishu.caretracev2.CustomExceptions.UnauthorizedException;
import com.krishu.caretracev2.DTO.PlaceGuesserAnswerRequest;
import com.krishu.caretracev2.DTO.PlaceGuesserAnswerResponse;
import com.krishu.caretracev2.DTO.PlaceGuesserQuestionResponse;
import com.krishu.caretracev2.DTO.PlaceGuesserSummaryResponse;
import com.krishu.caretracev2.Model.ImportantPlace;
import com.krishu.caretracev2.Model.Patient;
import com.krishu.caretracev2.Model.PlaceGuesserSession;
import com.krishu.caretracev2.Repository.ImportantPlaceRepo;
import com.krishu.caretracev2.Repository.PatientRepo;
import com.krishu.caretracev2.Repository.PlaceGuesserRepo;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class PlaceGuesserService {

    private final PlaceGuesserRepo placeGuesserRepo;
    private final ImportantPlaceRepo placeRepo;
    private final PatientRepo patientRepo;

    private final Integer MAX_QUESTION=5;
    private final Integer MAX_OPTION=4;

    public PlaceGuesserService(PlaceGuesserRepo placeGuesserRepo,ImportantPlaceRepo placeRepo
            ,PatientRepo patientRepo){
        this.placeGuesserRepo=placeGuesserRepo;
        this.placeRepo=placeRepo;
        this.patientRepo=patientRepo;
    }

    public PlaceGuesserQuestionResponse startGame(Authentication authentication){
        Patient patient=patientRepo.findByUserId(authentication.getName())
                .orElseThrow(()->new NotFoundException("Patient not found"));
        List<ImportantPlace> places=placeRepo.findByPatientId(patient.getId());
        List<ImportantPlace> uniquePlace=new ArrayList<>(new LinkedHashSet<>(places.stream().
                map(ImportantPlace::getName).toList().stream().
                map(name->places.stream().
                        filter(p->p.getName().
                                equals(name)).findFirst().orElseThrow()).toList()));

        if(uniquePlace.size()<2){
            throw new IllegalArgumentException("At least two places with " +
                    "different names are required");
        }
        Collections.shuffle(uniquePlace);
        int questionCount=Math.min(MAX_QUESTION,uniquePlace.size());
        List<String> placeIds=uniquePlace.stream().limit(questionCount)
                .map(ImportantPlace::getId).toList();
        PlaceGuesserSession session=new PlaceGuesserSession();
        session.setPatientId(patient.getId());
        session.setImportantPlaceIds(placeIds);
        session.setCurrQuestionIndex(0);
        session.setScore(0);
        session.setTotalQuestions(questionCount);
        session.setCompleted(false);
        session.setStartedAt(LocalDateTime.now());

        PlaceGuesserSession savedSession=placeGuesserRepo.save(session);
        return buildCurrentQuestion(savedSession);
    }

    public PlaceGuesserAnswerResponse submitAnswer(Authentication authentication
            , PlaceGuesserAnswerRequest request,String sessionId){
        PlaceGuesserSession session=getSessionAuthentication(authentication,sessionId);

        if(session.getCompleted()){
            throw new GameAlreadyCompletedException("Game has already completed");
        }

        int index=session.getCurrQuestionIndex();
        if(index<0 || index >=MAX_QUESTION){
            throw new OutOfRangeException("Invalid Question Index");
        }

        ImportantPlace correctPlace=placeRepo.findById(session.getImportantPlaceIds()
                        .get(session.getCurrQuestionIndex()))
                .orElseThrow(()->new NotFoundException("No such place"));

        if(!correctPlace.getId().equals(request.getPlaceId())){
            throw new IllegalArgumentException("This is not current question");
        }

        boolean correct=(correctPlace.getName()!=null && correctPlace.getName()
                .equalsIgnoreCase((request.getAnswer()==null)?"":request.getAnswer().trim()));
        String message="Wrong answer!!";
        if(correct){
            session.setScore(session.getScore()+1);
            message="Correct Answer!!";
        }

        session.setCurrQuestionIndex(session.getCurrQuestionIndex()+1);

        PlaceGuesserAnswerResponse response=new PlaceGuesserAnswerResponse();
        response.setCorrect(correct);
        response.setCorrectAnswer(correctPlace.getName());
        response.setScore(session.getScore());
        response.setMessage(message);
        if(session.getCurrQuestionIndex()>=session.getTotalQuestions()){
            session.setCompleted(true);
            response.setCompleted(true);
            response.setNextQuestion(null);
        }else{
            response.setCompleted(false);
            response.setNextQuestion(buildCurrentQuestion(session));
        }
        try {
            placeGuesserRepo.save(session);
        } catch (OptimisticLockingFailureException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This question has already been " +
                    "answered or the game state has changed.");
        }
        return response;
    }

    public PlaceGuesserSummaryResponse getGameSummary(Authentication authentication, String sessionId){
        PlaceGuesserSession session=getSessionAuthentication(authentication,sessionId);

        PlaceGuesserSummaryResponse response=new PlaceGuesserSummaryResponse();
        response.setCompleted(session.getCompleted());
        response.setScore(session.getScore());
        response.setSessionId(session.getId());
        response.setTotalQuestions(session.getTotalQuestions());

        return response;
    }


    private PlaceGuesserQuestionResponse buildCurrentQuestion(PlaceGuesserSession session){
        String placeId=session.getImportantPlaceIds().get(session.getCurrQuestionIndex());
        ImportantPlace place=placeRepo.findById(placeId).
                orElseThrow(()->new NotFoundException("Place not found"));
        List<ImportantPlace> importantPlaces=placeRepo.findByPatientId(session.getPatientId());

        List<String> options=new ArrayList<>();
        List<String> placeNames=importantPlaces.stream().map(ImportantPlace::getName)
                .filter(name->name!=null && !name.isBlank()).distinct().
                filter(name->!name.equalsIgnoreCase(place.getName())).toList();
        Collections.shuffle(placeNames);
        options.add(place.getName());
        options.addAll(placeNames.stream().limit(MAX_OPTION-1).toList());

        Collections.shuffle(options);

        PlaceGuesserQuestionResponse response=new PlaceGuesserQuestionResponse();
        response.setSessionId(session.getId());
        response.setQuestionNo(session.getCurrQuestionIndex()+1);
        response.setTotalQuestions(session.getTotalQuestions());
        response.setPlaceId(place.getId());
        response.setOptions(options);
        response.setPlacePhoto(place.getPhoto());

        return response;
    }

    private PlaceGuesserSession getSessionAuthentication(Authentication authentication
            ,String sessionId){
        Patient patient=patientRepo.findByUserId(authentication.getName())
                .orElseThrow(()->new NotFoundException("Patient not found"));
        PlaceGuesserSession session=placeGuesserRepo.findById(sessionId)
                .orElseThrow(()->new NotFoundException("Session not found"));
        if(!session.getPatientId().equals(patient.getId())){
            throw new UnauthorizedException("You dont have permission for this game session");
        }
        return session;
    }
}
