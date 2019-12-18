package se.complexjava.videouploader;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {


    /**
     * Folder location for storing files
     */

    //set this value from outside?
    private String location = "./videos/";

    public String getLocation() {
        return location;
    }

        public void setLocation(String location) {
            this.location = location;
        }


}
