package com.cnielallen.uploaderservice.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@Slf4j
@Getter
public class ContainerEnvironmentResource extends Environment implements ExtensionContext.Store.CloseableResource, AutoCloseable {
    private static final String DOCKER_REGISTRY = "";
    private static final DockerImageName SFTP_IMAGE = DockerImageName.parse(DOCKER_REGISTRY + "eeacms/scp-server:latest");



    @Container
    protected GenericContainer sftpContainer = new GenericContainer<>(SFTP_IMAGE)
            .withEnv("DATADIR", "/usr/local/apache2/htdocs")
            .withEnv("USERID", "500")
            .withEnv("AUTHORIZED_KEYS", "ssh-rsa")
            .withEnv("GROUPID", "500")
            .withExposedPorts(22);

    ContainerEnvironmentResource(){
        sftpContainer.start();

        this.setSftpHost(sftpContainer.getHost());
        this.setSftpPort(sftpContainer.getMappedPort(22));
        log.info("Started SFTP Server docker test container, host {} {}", sftpContainer.getHost(), sftpContainer);
    }
    @Override
    public void close(){
        log.info("Stopping all test containers.");
        sftpContainer.stop();
    }
}
