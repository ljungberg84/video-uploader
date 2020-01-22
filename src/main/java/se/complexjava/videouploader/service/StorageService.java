package se.complexjava.videouploader.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {

    String store(MultipartFile file, long userId, long videoId) throws Exception;

    void delete(long userId, long videoId) throws IOException;
}
