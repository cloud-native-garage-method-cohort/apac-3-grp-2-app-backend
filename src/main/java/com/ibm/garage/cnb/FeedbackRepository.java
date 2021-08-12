package com.ibm.garage.cnb;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface FeedbackRepository extends ReactiveCrudRepository<Feedback, String> {
}
