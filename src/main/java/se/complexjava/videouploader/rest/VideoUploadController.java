package se.complexjava.videouploader.rest;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
    @PostMapping("/{userId}")
    public ResponseEntity<String> handleVideoFileUpload(@PathVariable long userId, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        String filename = storageService.store(file, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Video: " + filename + " successfully uploaded.");
    }


    @DeleteMapping("/{userId}/{title}")
    public ResponseEntity<String> deleteVideoFile(@PathVariable long userId, @PathVariable String title) throws IOException {
        storageService.delete(userId, title);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Video successfully deleted");
    }
}
