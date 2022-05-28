package com.cnielallen.uploaderservice.service;

import com.cnielallen.uploaderservice.config.FileProperties;
import com.cnielallen.uploaderservice.dto.UploaderResponse;
import com.cnielallen.uploaderservice.model.FileUploadDefinition;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UploaderService {

    private final FileExtractorService fileExtractorService;
    private final ApiUploadService apiUploadService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final FileProperties fileProperties;


    public void process(String type, FileUploadDefinition fileUploadDefinition){
        var fileConfig= fileProperties.forType(type).orElseThrow(() -> new RuntimeException("File mapping not found for : " + type));

        fileExtractorService.extract(type, fileUploadDefinition).subscribe(file -> {
            if(file!=null && file.getContent() != null) {
                try {
                    log.info("Upload Service has successfully retrieved file for  {}", type);
                    if (fileConfig.getApiUrl() != null) {
                        apiUploadService.upload(file).subscribe(
                                response -> {
                                    var apiResponse = mapper.convertValue(response, UploaderResponse.class);
                                    log.info("File received for file type: {} for the filename : {} is  {}", file.getType(), file.getFileName(), response);
                                    if (!apiResponse.isSuccess()) {
                                        log.error("File Upload for file type {} contains error", file.getType());
                                    }
                                },
                                error -> {
                                    log.error("Error occurred when invoking api for file type {} with the filename  {} is {}", file.getType(), file.getFileName(), error);
                                }
                        );
                    }
                } catch (Exception e) {
                    log.info("File not found for {}" , type);
                }
            }
        });

    }
}
