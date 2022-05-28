package com.cnielallen.uploaderservice.config;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;

@Data
@Slf4j
@ConfigurationProperties(prefix = "file-configs")
@ToString(exclude = {"password"})
public class FileProperties {


    private List<FileTemplate> files;

    private ApiCredentials defaultCredentials;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Map<String, FileTemplate> typeToFileMappings = Map.of();

    void setFiles(List<FileTemplate> files){
        log.info("Populating mapping for uploader service files: {}", files);
        typeToFileMappings = files.stream()
                .collect(toMap(FileTemplate::getType, identity()));

        if(log.isInfoEnabled()){
            typeToFileMappings.forEach((type, file) -> log.info("Uploader Service: {} Directory: {}", type, file.getFileDirectory()));
        }

        log.info("Total Uploader Service File Mappings: {}", typeToFileMappings.size());
        this.files = files;
    }

    public Optional<FileTemplate> forType(String type) { return Optional.ofNullable(typeToFileMappings.get(type));}

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileTemplate {
        private String type;
        private String fileName;
        private String fileDirectory;
        private String fileNameFormat;
        private String apiUrl;
        private String httpMethod;
        private String username;
        private String password;
        private String contentType;
    }

    @Data
    public static class ApiCredentials {
        private String username;
        private String password;
    }
}
