package com.ibm.garage.cnb;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;


@DataMongoTest
@ExtendWith(SpringExtension.class)
public class MongoDbSpringIntegrationTest {
    @DisplayName("""
            given object to save
            when save object using MongoDB template
            then object is saved
            """)
    @Test
    public void test(@Autowired FeedbackRepository feedbackRepository) {
        // given
        Feedback feedback = new Feedback();
        feedback.setFeedback("Hi There!");
        // when
        Feedback savedFeedback = feedbackRepository.save(feedback).block();
        // then
        StepVerifier.create(feedbackRepository.findById(savedFeedback.getId())).assertNext( it -> savedFeedback.getFeedback().equals(it.getFeedback())).expectComplete();
    }
}
