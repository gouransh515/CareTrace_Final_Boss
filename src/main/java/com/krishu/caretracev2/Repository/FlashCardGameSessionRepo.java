package com.krishu.caretracev2.Repository;

import com.krishu.caretracev2.Model.FlashCardGameSession;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FlashCardGameSessionRepo extends MongoRepository<FlashCardGameSession,String> {
}
