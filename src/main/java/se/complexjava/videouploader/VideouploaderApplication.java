package se.complexjava.videouploader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class VideouploaderApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideouploaderApplication.class, args);
	}

}
