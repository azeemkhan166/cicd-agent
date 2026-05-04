/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.config;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.StorageOptions;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.io.ClassPathResource;
import com.google.cloud.storage.Storage;

/**
 *
 * @author sharm
 */
@Configuration
@Slf4j

public class GcpConfig {

    @Value("${gcp.config.file}")
    private String gcpConfigFile;
    
    Storage storage = StorageOptions.getDefaultInstance().getService();

    @Bean
    public Storage storage() {

        try {
            InputStream inputStream = new ClassPathResource(gcpConfigFile).getInputStream();
            Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.fromStream(inputStream))
                    .build().getService();
            return storage;
        } catch (Exception ex) {
            return StorageOptions.newBuilder().build().getService();
        }
    }

    public String getSignedUrl(String bucketName, String url) {
        
        System.out.println("getSignedUrl "+bucketName);
        System.out.println("url "+url);
        String fileName = null;
        try {
  
            int index = url.indexOf(bucketName);
            if (index != -1) {
                fileName = url.substring(index + bucketName.length());
            }

            fileName = fileName.substring(1);
            System.out.println("fileName" + " " + fileName);
            Storage storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.fromStream(new ClassPathResource(gcpConfigFile).getInputStream()))
                    .build().getService();

            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName).build();
            String signedUrl = storage.signUrl(blobInfo, 30, TimeUnit.MINUTES).toString();
            fileName = signedUrl;
            System.out.println("fileName" + " " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }

}
