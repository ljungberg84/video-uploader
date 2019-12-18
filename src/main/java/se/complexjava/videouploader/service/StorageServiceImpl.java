package se.complexjava.videouploader.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import se.complexjava.videouploader.StorageProperties;
import se.complexjava.videouploader.exception.StorageException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class StorageServiceImpl implements StorageService {


    private final Path rootLocation;

    private JmsTemplate jmsTemplate;

    @Autowired
    public StorageServiceImpl(StorageProperties properties, JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
        this.rootLocation = Paths.get(properties.getLocation());
    }


    @Override
    public String store(MultipartFile file, long userId) {

        String videoName = StringUtils.cleanPath(file.getOriginalFilename());
        String filename = createFilename(userId, videoName);

        try {
            File f = new File(rootLocation + "/" + filename);
            if(f.exists()){
                throw  new StorageException(String.format("Video with name: %s already exists", videoName));
            }
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + videoName);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, this.rootLocation.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING);
            }
            //message to encoder that new file is uploaded
            jmsTemplate.convertAndSend("video-file", 1 + " " + filename);
            //message to data-service that file upload was successful
            jmsTemplate.convertAndSend("file-uploaded", 1 + " " + filename);

            return filename;
        }
        catch (IOException e) {
            //message to data-service that upload failed
            jmsTemplate.convertAndSend("file-uploaded", -1 + " " + filename);
            throw new StorageException("Failed to store file " + filename, e);
        }
    }


    //TODO: all encoded versions of video should also be deleted
    @Override
    public void delete(long userId, String title) throws IOException{
        String fileIdentifier = createFilename(userId, title);
        Files.deleteIfExists(Paths.get(rootLocation + "/" + fileIdentifier));
        //send message to data-service that video is deleted
        jmsTemplate.convertAndSend("file-deleted", 1 + " " + fileIdentifier);
    }

    public String createFilename(long userId, String title){
        return userId + "&" + title;
    }
}
