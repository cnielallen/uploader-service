package com.cnielallen.uploaderservice.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.LinkedMultiValueMap;

@Data
@Builder
@AllArgsConstructor
@ToString(exclude = {"password"})
public class FileUploadDefinition {
    private String fileName;
    private String directory;
    private String type;
    private byte[] content;
    private String apiUrl;
    private String httpMethod;
    private String username;
    private String password;
    private String contentType;

    public LinkedMultiValueMap getBody() {
        var map = new LinkedMultiValueMap<String, Object>();
        var contentAsResource = new ByteArrayResource(content) {
            @Override
            public String getFilename() { return fileName;}
        };
        map.add("filename", fileName);
        map.add("name", fileName);
        map.add("file", contentAsResource);
        return map;
    }
}
