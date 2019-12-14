package se.complexjava.videouploader.rest;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import se.complexjava.videouploader.service.StorageService;

@RestController
@RequestMapping("/upload")
public class VideoUploadController {

    private final StorageService storageService;

    @Autowired
    public VideoUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

//change to return names of all videos or videos by user?
//    @GetMapping
//    public String listUploadedFiles(Model model) throws IOException {
//
//        model.addAttribute("files", storageService.loadAll().map(
//                path -> MvcUriComponentsBuilder.fromMethodName(VideoUploadController.class,
//                        "serveFile", path.getFileName().toString()).build().toString())
//                .collect(Collectors.toList()));
//
//        return "uploadForm";
//    }


    @PostMapping
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

        storageService.store(file);
//        redirectAttributes.addFlashAttribute("message",
//                "You successfully uploaded " + file.getOriginalFilename() + "!");
        //return "redirect:/";

        return ResponseEntity.status(HttpStatus.CREATED).body("Video successfully uploaded.");
    }
}
