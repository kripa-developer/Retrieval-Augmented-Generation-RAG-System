package com.example.rag.controller;

import com.example.rag.service.DataLoadingService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin("*")
public class FileUploadController {

    private final DataLoadingService dataLoadingService;

    public FileUploadController(DataLoadingService dataLoadingService) {
        this.dataLoadingService = dataLoadingService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload.");
        }

        try {
            // Wrap the multipart file input stream into a resource
            InputStreamResource resource = new InputStreamResource(file.getInputStream()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            dataLoadingService.loadDocument(resource);
            return ResponseEntity.ok("File uploaded and processed successfully: " + file.getOriginalFilename());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to process file: " + e.getMessage());
        }
    }
}
