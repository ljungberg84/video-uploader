package se.complexjava.videouploader.rest;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.complexjava.videouploader.service.StorageService;

@RestController
@RequestMapping("/file-management")
public class VideoUploadController {

    private final StorageService storageService;

    @Autowired
    public VideoUploadController(StorageService storageService) {
        this.storageService = storageService;
    }


    @PostMapping("/{userId}")
    public ResponseEntity<String> handleVideoFileUpload(@PathVariable long userId, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

        storageService.store(file, userId);
        //send jms message
        return ResponseEntity.status(HttpStatus.CREATED).body("Video successfully uploaded.");
    }


    @DeleteMapping("/{userId}/{title}")
    public ResponseEntity<String> deleteVideoFile(@PathVariable long userId, @PathVariable String title) {
        storageService.delete(userId, title);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Video successfully deleted");
    }
}
