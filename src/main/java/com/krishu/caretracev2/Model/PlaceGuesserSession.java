package com.krishu.caretracev2.Model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Document(collection="place_guesser_session")
public class PlaceGuesserSession {
    @Id
    private String id;
    private String patientId;
    private List<String> importantPlaceIds;
    private Integer currQuestionIndex;
    private Integer score;
    private Integer totalQuestions;
    private Boolean completed;
    private LocalDateTime startedAt;
    @Version
    private long version;
}
