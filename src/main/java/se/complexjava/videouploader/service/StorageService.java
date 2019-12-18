package se.complexjava.videouploader.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {

    void store(MultipartFile file, long userId);

    void delete(long userId, String title) throws IOException;
}
