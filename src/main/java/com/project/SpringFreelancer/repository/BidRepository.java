package com.project.SpringFreelancer.repository;

import com.project.SpringFreelancer.model.Bid;
import com.project.SpringFreelancer.model.Job;
import com.project.SpringFreelancer.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {

    List<Bid> findByJob(Job job);

    List<Bid> findByFreelancer(User freelancer);

    // Optional: To prevent duplicate bids from same freelancer
    boolean existsByFreelancerAndJob(User freelancer, Job job);

    Optional<Bid> findByFreelancerAndJob(User freelancer, Job job);

    @Modifying
    @Transactional
    @Query("UPDATE Bid b SET b.status = 'REJECTED' WHERE b.job = :job AND b.freelancer <> :freelancer")
    void rejectOtherBids(Job job, User freelancer);

}

