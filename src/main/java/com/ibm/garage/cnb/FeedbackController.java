package com.ibm.garage.cnb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @GetMapping(path = "/feedbacks", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    @PostMapping(path = "/feedbacks", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Feedback> createFeedback(@RequestBody Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    @DeleteMapping(path = "/feedbacks/{id}")
    public Mono<Void> deleteFeedback(@PathVariable("id") String id) {
        return feedbackRepository.deleteById(id);
    }
}
