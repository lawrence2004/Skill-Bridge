package com.project.SpringFreelancer.controller;

import com.project.SpringFreelancer.dto.File.FileUploadResponse;
import com.project.SpringFreelancer.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Tag(name = "Files", description = "Endpoints for uploading and downloading files related to Jobs")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @Operation(
            description = "Endpoint for uploading a file for a specific job",
            summary = "Upload file for a job"
    )
    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("jobId") Long jobId,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            String email = userDetails.getUsername();
            FileUploadResponse response = fileUploadService.uploadFile(file, jobId, email);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            description = "Endpoint to get all uploaded files for a specific job",
            summary = "Get all files for a job"
    )
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<FileUploadResponse>> getJobFiles(
            @PathVariable Long jobId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        List<FileUploadResponse> files = fileUploadService.getJobFiles(jobId, email);
        return ResponseEntity.ok(files);
    }

    @Operation(
            description = "Endpoint to download a specific uploaded file",
            summary = "Download file by file ID"
    )
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long fileId,
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            String email = userDetails.getUsername();
            Resource resource = fileUploadService.downloadFile(fileId, email);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Long extractUserId(UserDetails userDetails) {
        // Implementation depends on your UserDetails implementation
        return Long.parseLong(userDetails.getUsername());
    }
}