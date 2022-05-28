package com.cnielallen.uploaderservice.service;


import com.cnielallen.uploaderservice.config.FileProperties;
import com.cnielallen.uploaderservice.config.SftpConfig;
import com.cnielallen.uploaderservice.config.SftpServer;
import com.cnielallen.uploaderservice.config.TestBase;
import com.google.common.io.ByteStreams;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@EnableConfigurationProperties
@SpringBootTest
@TestPropertySource(properties = "uploader.service.scheduling.enable = false")
public class SftpServiceTest extends TestBase {
    @Autowired
    private FileProperties fileProperties;

    @Autowired
    private SftpService sftpService;

    @Autowired
    private SftpConfig sftpConfig;

    @Autowired
    private SftpServer sftpServer;


    private static final String JOURNAL_FILENAME = "Journals.csv";
    private static final String BOOK_FILENAME = "Books.pdf";

    private static final String FILE_CONTENTS = "test";

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("validFiles")
    void shouldReturnFileFromSFTPServer(String type, String filename){
        var fileConfig = fileProperties.forType(type).orElseThrow(() -> new RuntimeException("Missing file mapping for : " + type));
        fileConfig.setFileDirectory(sftpServer.getDirectory());
        fileConfig.setFileName(filename);
        setup(filename);
        var result = ByteStreams.toByteArray(sftpService.get(fileConfig));
        assertThat(result).asString().contains(FILE_CONTENTS);
        sftpService.closeSftp();
    }

    @SneakyThrows
    private void setup(String filename) {
        var session = sftpConfig.openSession();
        var path = sftpServer.getDirectory();
        session.connect();
        try{
            try{
                var channelSftp = (ChannelSftp) session.openChannel("sftp");
                channelSftp.connect();
                try{
                    channelSftp.put(path);
                    channelSftp.put(new ByteArrayInputStream(FILE_CONTENTS.getBytes()), filename);
                } finally {
                    channelSftp.exit();
                }
            } finally {
                session.disconnect();
            }
        } catch (JSchException | SftpException e) {
            throw new IOException(e);
        }
    }



    private static Stream<Arguments> validFiles() {
        return Stream.of(
                Arguments.arguments("journal", JOURNAL_FILENAME),
                Arguments.arguments("book", BOOK_FILENAME)
        );
    }

}
