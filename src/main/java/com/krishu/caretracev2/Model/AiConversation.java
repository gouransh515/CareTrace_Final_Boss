package com.krishu.caretracev2.Model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection="aiConversation")
public class AiConversation {
    @Id
    private String id;
    private String interactionId;
    private String patientId;
    private LocalDateTime updatedAt;
}
