package com.cnielallen.uploaderservice.service;


import com.cnielallen.uploaderservice.config.FileProperties;
import com.cnielallen.uploaderservice.model.FileUploadDefinition;
import com.google.common.io.ByteStreams;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
@Slf4j
@RequiredArgsConstructor
public class FileExtractorService {

    private final FileProperties fileProperties;
    private final SftpService sftpService;

    @SneakyThrows
    public Mono<FileUploadDefinition> extract(String type, FileUploadDefinition fileUploadDefinition){
        log.info("Starting extraction for file type: {}", type);
        var fileConfig = fileProperties.forType(type).orElseThrow(() -> new RuntimeException("File mapping not found for : " + type));
        log.info("Start extracting file {} under SFTP Server", fileConfig.getFileName());

        var content = sftpService.get(fileConfig);

        if(content == null){
            sftpService.closeSftp();
            return Mono.empty();
        }

        fileUploadDefinition.setFileName(fileConfig.getFileName());
        fileUploadDefinition.setContent(ByteStreams.toByteArray(content));
        sftpService.closeSftp();

        return Mono.just(fileUploadDefinition);
    }
}
