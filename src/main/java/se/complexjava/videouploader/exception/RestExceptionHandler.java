package se.complexjava.videouploader.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);


    @ExceptionHandler(FileAlreadyExistsException.class)
    public ResponseEntity handleFileExistsException(FileAlreadyExistsException e){

        logger.info(e.getMessage(), e.getStackTrace());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity handleStorageFileNotFound(StorageFileNotFoundException e) {

        logger.info(e.getMessage(), e.getStackTrace());
        return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body("file not found: " +e.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity handleIoException(IOException e){

        logger.info(e.getMessage(), e.getStackTrace());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception e){

        logger.info(e.getMessage(), e.getStackTrace());
        return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body("unknown exception: " + e.getMessage());
    }
}
