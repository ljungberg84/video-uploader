package se.complexjava.videouploader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.jms.annotation.EnableJms;

@EnableJms
@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class VideoUploaderApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideoUploaderApplication.class, args);
	}

}
