package se.complexjava.videouploader.rest;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import se.complexjava.videouploader.service.StorageService;

import java.io.IOException;

@RestController
@RequestMapping("/file-management")
public class VideoUploadController {

    private final StorageService storageService;

    @Autowired
    public VideoUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    //Todo: set a proper filelimit for upload in application properties
    @PostMapping("/{userId}/{videoId}")
    public ResponseEntity<String> handleVideoFileUpload(@PathVariable long userId, @PathVariable long videoId, @RequestParam("file") MultipartFile file) throws Exception{
        String filename = storageService.store(file, userId, videoId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Video: " + filename + " successfully uploaded.");
    }

    @DeleteMapping("/{userId}/{videoId}")
    public ResponseEntity<String> deleteVideoFile(@PathVariable long userId, @PathVariable long videoId) throws IOException {
        storageService.delete(userId, videoId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Video successfully deleted");
    }
}
