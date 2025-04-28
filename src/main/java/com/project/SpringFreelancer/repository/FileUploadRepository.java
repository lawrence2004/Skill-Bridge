package com.project.SpringFreelancer.repository;

import com.project.SpringFreelancer.model.FileUpload;
import com.project.SpringFreelancer.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {

    List<FileUpload> findByJob(Job job);
}
