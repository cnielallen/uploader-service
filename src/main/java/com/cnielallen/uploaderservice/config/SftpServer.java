package com.cnielallen.uploaderservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("uploader.service.sftp")
public class SftpServer {
    private String host;
    private Integer port;
    private Boolean enableCompression;
    private Integer channelTimeout;
    private String directory;
    private String username;
    private String sshKey;
    private String passphrase;

    public String shortDescription() {
        return String.format("%s@%s:%d", username, host, port);
    }
}
