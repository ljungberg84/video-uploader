package se.complexjava.videouploader.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {

    String store(MultipartFile file, long userId) throws Exception;

    void delete(long userId, String title) throws IOException;
}
