package com.krishu.caretracev2.Configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;

@Configuration
public class AwsConfig {

    @Value("${rekognition.access.key}")
    private String accessKey;

    @Value("${rekognition.secret.key}")
    private String secretKey;

    @Value("${rekognition.region}")
    private String region;

    @Bean
    public RekognitionClient getRekognitionClient(){
        AwsBasicCredentials credentials=AwsBasicCredentials.create(accessKey,secretKey);
        return RekognitionClient.builder().region(Region.of(region)).
                credentialsProvider(StaticCredentialsProvider.create(credentials)).build();
    }
}
