package com.project.SpringFreelancer.repository;

import com.project.SpringFreelancer.model.Job;
import com.project.SpringFreelancer.model.JobStatus;
import com.project.SpringFreelancer.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByClient(User client);

    @Query("SELECT j FROM Job j " +
            "WHERE (:title IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
            "AND (:category IS NULL OR LOWER(REPLACE(j.category, ' ', '')) = LOWER(REPLACE(:category, ' ', ''))) " +
            "AND (:minBudget IS NULL OR j.budget >= :minBudget) " +
            "AND (:maxBudget IS NULL OR j.budget <= :maxBudget) " +
            "AND (:deadlineBefore IS NULL OR j.deadline <= :deadlineBefore) " +
            "AND (:status IS NULL OR j.status = :status)")
    List<Job> searchJobs(
            @Param("title") String title,
            @Param("category") String category,
            @Param("minBudget") Double minBudget,
            @Param("maxBudget") Double maxBudget,
            @Param("deadlineBefore") LocalDate deadlineBefore,
            @Param("status") JobStatus status
    );

    @Query("SELECT j FROM Job j " +
            "WHERE LOWER(j.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(j.category) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Job> findByTitleOrCategoryContaining(@Param("searchTerm") String searchTerm);
}