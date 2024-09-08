package com.olatunbosun.FileUploadTutorial.upload;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author olulodeolatunbosun
 * @created 9/8/24/09/2024 - 1:43 pm
 */

@RestController
@RequestMapping("/api/v1/upload")
public class ArticleController {

    private final CloudflareR2Service cloudflareR2Config;

    public ArticleController(CloudflareR2Service cloudflareR2Config) {
        this.cloudflareR2Config = cloudflareR2Config;
    }


    // Add your endpoints here
    @PostMapping(value = "/file", consumes = {"multipart/form-data"})
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String  url =   cloudflareR2Config.uploadFile(file, false);
        return ResponseEntity.ok("File uploaded successfully to " + url);
    }


}
