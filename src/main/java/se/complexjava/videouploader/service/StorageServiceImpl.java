package se.complexjava.videouploader.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import se.complexjava.videouploader.StorageProperties;
import se.complexjava.videouploader.exception.StorageException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class StorageServiceImpl implements StorageService {

    private final Path rootLocation;

    private static final String UPLOADER_TO_ENCODER_QUE = "uploader-to-encoder-que";
    private static final String UPLOADER_TO_DATA_QUE = "uploader-to-data-que";
    private static final String DELETED_VIDEO_FILE_TOPIC = "delete-video-File-topic";

    private JmsTemplate jmsTemplate;
    private Logger logger = LoggerFactory.getLogger(StorageServiceImpl.class);

    @Autowired
    public StorageServiceImpl(StorageProperties properties, JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public String store(MultipartFile file, long userId, long videoId) throws Exception {

        String fileName = userId + "\\" + videoId + "\\" + videoId + ".mp4";

        File dir = new File(rootLocation + "\\" + userId + "\\" + videoId + "\\");
        dir.mkdirs();

        File f = new File(dir.getPath() + fileName);

        if (f.exists()) {
            logger.info("file already exist: '{}'", fileName);
            throw  new FileAlreadyExistsException(String.format("Video with name: %s already exists", videoId));
        }
        if (file.isEmpty()) {
            throw new StorageException("Failed to store empty file " + fileName);
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, this.rootLocation.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING);
        }

        logger.info("video: '{}' saved", videoId);
        logger.info("sending message to brokers");
        sendJMS(UPLOADER_TO_ENCODER_QUE, 1, userId, videoId);
        //message to data-service that file upload was successful
        sendJMS(UPLOADER_TO_DATA_QUE, 1, userId, videoId);

        return String.valueOf(videoId);

    }

    //TODO: all encoded versions of video should also be deleted
    @Override
    public void delete(long userId, long videoId) throws IOException{
        Files.deleteIfExists(Paths.get(rootLocation + "/" + userId + "/" + videoId));

        sendJMS(DELETED_VIDEO_FILE_TOPIC, 1, userId, videoId);
    }

    private void sendJMS(String destination, int status, long userId, long videoId){
        Map<String, String> message = new HashMap<>();
        message.put("status", String.valueOf(status));
        message.put("userId", String.valueOf(userId));
        message.put("videoId", String.valueOf(videoId));

        jmsTemplate.convertAndSend(destination, message);
    }
}
