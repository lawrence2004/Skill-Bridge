package com.project.SpringFreelancer.repository;

import com.project.SpringFreelancer.model.Job;
import com.project.SpringFreelancer.model.Review;
import com.project.SpringFreelancer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByReviewerAndReviewedUserAndJob(User reviewer, User reviewedUser, Job job);

    List<Review> findByReviewedUser(User user);

}
