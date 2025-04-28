package com.project.SpringFreelancer.service;

import com.project.SpringFreelancer.dto.File.FileUploadResponse;
import com.project.SpringFreelancer.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import com.project.SpringFreelancer.Exception.ResourceNotFoundException;
import com.project.SpringFreelancer.dto.Job.JobSummaryDTO;
import com.project.SpringFreelancer.dto.Payment.PaymentResponse;
import com.project.SpringFreelancer.dto.Review.ReviewRequest;
import com.project.SpringFreelancer.dto.Review.ReviewResponse;
import com.project.SpringFreelancer.dto.User.UserSummaryDTO;
import com.project.SpringFreelancer.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final FileUploadRepository fileUploadRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public FileUploadResponse uploadFile(MultipartFile file, Long jobId, String email) throws IOException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

        boolean isClient = job.getClient().getId().equals(user.getId());
        boolean isFreelancer = job.getHiredFreelancer() != null && job.getHiredFreelancer().getId().equals(user.getId());

        // Create upload directory if it doesn't exist
        File directory = new File(uploadDir + File.separator + jobId);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Generate a unique filename
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String filename = System.currentTimeMillis() + "_" + originalFilename;
        String filePath = uploadDir + File.separator + jobId + "/" + filename;

        // Copy file to the target location
        Path targetLocation = Paths.get(filePath);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        FileUpload fileUpload = FileUpload.builder()
                .fileName(originalFilename)
                .filePath(filePath)
                .uploadedBy(user)
                .job(job)
                .build();

        FileUpload savedFile = fileUploadRepository.save(fileUpload);
        return mapToFileUploadResponse(savedFile);
    }

    public List<FileUploadResponse> getJobFiles(Long jobId, String email) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if user is part of the job
        boolean isClient = job.getClient().getId().equals(user.getId());
        boolean isFreelancer = job.getHiredFreelancer() != null &&
                job.getHiredFreelancer().getId().equals(user.getId());

        if (!isClient && !isFreelancer) {
            throw new IllegalArgumentException("You are not part of this job");
        }

        return fileUploadRepository.findByJob(job).stream()
                .map(this::mapToFileUploadResponse)
                .collect(Collectors.toList());
    }

    public Resource downloadFile(Long fileId, String email) throws IOException {
        FileUpload fileUpload = fileUploadRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Job job = fileUpload.getJob();

        // Check if user is part of the job
        boolean isClient = job.getClient().getId().equals(user.getId());
        boolean isFreelancer = job.getHiredFreelancer() != null &&
                job.getHiredFreelancer().getId().equals(user.getId());

        if (!isClient && !isFreelancer) {
            throw new IllegalArgumentException("You are not part of this job");
        }

        Path filePath = Paths.get(fileUpload.getFilePath());
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists()) {
            return resource;
        } else {
            throw new FileNotFoundException("File not found: " + fileUpload.getFileName());
        }
    }

    private FileUploadResponse mapToFileUploadResponse(FileUpload fileUpload) {
        UserSummaryDTO uploaderDTO = UserSummaryDTO.builder()
                .id(fileUpload.getUploadedBy().getId())
                .name(fileUpload.getUploadedBy().getName())
                .role(fileUpload.getUploadedBy().getRole())
                .build();

        JobSummaryDTO jobDTO = JobSummaryDTO.builder()
                .id(fileUpload.getJob().getId())
                .title(fileUpload.getJob().getTitle())
                .budget(fileUpload.getJob().getBudget())
                .build();

        return FileUploadResponse.builder()
                .id(fileUpload.getId())
                .fileName(fileUpload.getFileName())
                .uploadedAt(fileUpload.getUploadedAt())
                .uploadedBy(uploaderDTO)
                .job(jobDTO)
                .build();
    }
}