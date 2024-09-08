package com.olatunbosun.FileUploadTutorial.upload;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

/**
 * @author olulodeolatunbosun
 * @created 9/8/24/09/2024 - 3:37 pm
 */

@Configuration
public class CloudflareR2Service {

    @Value("${application.cloudflare.r2.access-key}")
    private String accessKey;

    @Value("${application.cloudflare.r2.secret-key}")
    private String secretKey;

    @Value("${application.cloudflare.r2.endpoint}")
    private String endpoint;

    @Value("${application.cloudflare.r2.bucket-name}")
    private String bucketName;

    @Value("${application.cloudflare.r2.public-url}")
    private String publicUrl;


    public String uploadFile(MultipartFile file, boolean isPublic) throws IOException {


        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);

        S3Configuration serviceConfiguration = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build();

        S3Client s3Client =  S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .endpointOverride(URI.create(endpoint))
                .serviceConfiguration(serviceConfiguration)
                .region(Region.US_EAST_1)
                .build();

        // Ensure the bucket exists (create it if it does not)
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
        } catch (NoSuchBucketException e) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
        }

        // Generate a unique key name for the file
        String keyName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        //converting the file to byte
        byte[] fileBytes = file.getBytes();

        // Create the PutObjectRequest with the appropriate ACL
        PutObjectRequest.Builder putObjectRequestBuilder = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName);

        if (isPublic) {
            putObjectRequestBuilder.acl(ObjectCannedACL.PUBLIC_READ); // Set the object to be public
        }

        // Put the object into the R2 bucket
        s3Client.putObject(
                putObjectRequestBuilder.build(),
                RequestBody.fromBytes(fileBytes)
        );

        // Construct the full URL to the uploaded file
        return String.format("%s/%s", publicUrl, keyName);
    }
}
