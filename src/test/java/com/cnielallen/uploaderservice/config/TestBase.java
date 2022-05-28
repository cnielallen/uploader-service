package com.cnielallen.uploaderservice.config;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@Slf4j
@ExtendWith(ContainerEnvironmentExtension.class)
@ResourceLock(Environment.ID)
public abstract class TestBase {

    protected static String sftpHost;
    protected static int sftpPort;


    @BeforeAll
    static void prepareContainerEnvironment(Environment resource) {
        sftpHost = resource.getSftpHost();
        sftpPort = resource.getSftpPort();
    }


    @DynamicPropertySource
    static void dynamicPropertySource(DynamicPropertyRegistry registry) {
        registry.add("UPLOADER_SERVICE_HOST", () -> sftpHost);
        registry.add("UPLOADER_SERVICE_PORT", () -> sftpPort);
        registry.add("UPLOADER_SERVICE_COMPRESSION", () -> true);
        registry.add("UPLOADER_SERVICE_CHANNEL_TIMEOUT", () -> "3000");
        registry.add("UPLOADER_SERVICE_DIRECTORY", () -> "/usr/local/apache2/htdocs");
        registry.add("UPLOADER_SERVICE_SSH_KEY", () -> "./local/id_rsa");
        registry.add("UPLOADER_SERVICE_PASSPHRASE", () -> "file:./local/id_rsa");
    }
}
