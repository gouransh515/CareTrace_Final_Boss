package com.krishu.caretracev2.Model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document(collection="flashcardsession")
public class FlashCardGameSession {
    private String id;
    private String patientId;
    private List<String> personIds;
    private int questinIndex;
    private int score;
    private Boolean completed;
}
