package se.complexjava.videouploader.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import se.complexjava.videouploader.StorageProperties;
import se.complexjava.videouploader.exception.StorageException;
import se.complexjava.videouploader.exception.StorageFileNotFoundException;

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

    @Autowired
    public StorageServiceImpl(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }


    @Override
    public void store(MultipartFile file, long userId) {

        String videoName = StringUtils.cleanPath(file.getOriginalFilename());
        String filename = userId + ":" + videoName;

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
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }


    @Override
    public void delete(long userId, String title) {

        String fileIdentifier = userId + ":" + title;
        File file = new File("./videos/" + fileIdentifier);
        if(!file.delete())
            throw new StorageFileNotFoundException("error deleting file: " + fileIdentifier);
    }
}
