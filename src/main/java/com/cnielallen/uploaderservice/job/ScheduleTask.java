package com.cnielallen.uploaderservice.job;

import com.cnielallen.uploaderservice.config.FileProperties;
import com.cnielallen.uploaderservice.model.FileUploadDefinition;
import com.cnielallen.uploaderservice.service.UploaderService;
import com.cnielallen.uploaderservice.util.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        value = "uploader.service.scheduling.enable", havingValue = "true", matchIfMissing = true
)
public class ScheduleTask {

    private final FileProperties fileProperties;
    private final UploaderService service;
    private Map<String, FileUploadDefinition> fileUploadDefinitionMap;

    @Scheduled(fixedDelayString = "${processor.fixed-delay-ms}", initialDelayString = "${processor.initial-delay-ms}")
    public void runProcessor(){
        if(fileUploadDefinitionMap == null || fileUploadDefinitionMap.isEmpty()){
            fileUploadDefinitionMap = fileProperties.getFiles().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(FileProperties.FileTemplate::getType, fileTemplate -> {
                        var type = fileTemplate.getType();
                        var fileConfig  = fileProperties.forType(type).orElseThrow(()-> new RuntimeException("Mapping not found"));
                        return FileUploadDefinition.builder()
                                .fileName(fileConfig.getFileName())
                                .apiUrl(fileConfig.getApiUrl())
                                .directory(fileConfig.getFileDirectory())
                                .username(fileConfig.getUsername())
                                .password(fileConfig.getPassword())
                                .httpMethod(fileConfig.getHttpMethod())
                                .contentType(fileConfig.getContentType())
                                .build();
                    }));
        }

        fileUploadDefinitionMap.entrySet().stream().peek(Wrappers.wrap(map -> service.process(map.getKey(), map.getValue())))
                .forEach(map -> log.debug("File extract for {}", map.getKey()));

    }

}
