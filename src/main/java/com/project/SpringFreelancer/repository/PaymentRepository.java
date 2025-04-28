package com.project.SpringFreelancer.repository;

import com.project.SpringFreelancer.model.Job;
import com.project.SpringFreelancer.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByJob(Job job);

    Optional<Payment> findByJob(Job job);
}
