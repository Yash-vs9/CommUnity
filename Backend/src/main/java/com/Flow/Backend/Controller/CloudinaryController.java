package com.Flow.Backend.controller;

import com.Flow.Backend.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
@RestController
@RequestMapping("/cloud")
public class CloudinaryController {
    @Autowired
    private  CloudinaryService cloudinaryService;


    @PostMapping("/uploadFile")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            Map uploadResult = cloudinaryService.uploadFile(file);
            String url = (String) uploadResult.get("secure_url");
            return ResponseEntity.ok(Map.of("url", url));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Upload failed"));
        }
    }
    @PostMapping("/profile-pic")
    public ResponseEntity<?> uploadProfilePic(@RequestParam("profilePic") MultipartFile file) {
        try {
            Map uploadResult = cloudinaryService.uploadFile(file);

            // Retrieve the secure URL of the uploaded image
            String url = (String) uploadResult.get("secure_url");

            // Here, you should save this URL to the user’s profile in your database
            // Example: userService.updateProfilePic(userId, url);

            return ResponseEntity.ok(Map.of("url", url, "message", "Profile picture uploaded successfully"));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Profile picture upload failed"));
        }
    }

}
