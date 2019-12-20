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
import java.util.HashMap;
import java.util.Map;

@Service
public class StorageServiceImpl implements StorageService {


    private final Path rootLocation;

    private static final String UPLOADER_TO_ENCODER_QUE = "uploader-to-encoder-que";
    private static final String UPLOADER_TO_DATA_QUE = "uploader-to-data-que";
    private static final String DELETED_VIDEO_FILE_TOPIC = "delete-video-File-topic";

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
            //message to encoder-service that new file is uploaded
            sendJMS(UPLOADER_TO_ENCODER_QUE, 1, userId, videoName);
            //message to data-service that file upload was successful
            sendJMS(UPLOADER_TO_DATA_QUE, 1, userId, videoName);

            return filename;
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }


    //TODO: all encoded versions of video should also be deleted
    @Override
    public void delete(long userId, String title) throws IOException{
        String fileName = createFilename(userId, title);
        Files.deleteIfExists(Paths.get(rootLocation + "/" + fileName));

        sendJMS(DELETED_VIDEO_FILE_TOPIC, 1, userId, title);
    }
    private void sendJMS(String destination, int status, long userId, String title){
        Map<String, String> message = new HashMap<>();
        message.put("status", String.valueOf(status));
        message.put("userId", String.valueOf(userId));
        message.put("title", title);

        jmsTemplate.convertAndSend(destination, message);
    }


    public String createFilename(long userId, String title){
        return userId + "&" + title;
    }
}
