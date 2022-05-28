package com.cnielallen.uploaderservice.service;

import com.cnielallen.uploaderservice.config.TestBase;
import com.cnielallen.uploaderservice.dto.UploaderResponse;
import com.cnielallen.uploaderservice.model.FileUploadDefinition;
import com.tyro.oss.logtesting.logback.LogbackCaptor;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.charset.StandardCharsets;

import static com.tyro.oss.randomdata.RandomString.randomString;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@EnableConfigurationProperties
@SpringBootTest
public class UploaderServiceTest extends TestBase {

    @Autowired
    private UploaderService service;

    @MockBean
    private FileExtractorService extractorService;

    @MockBean
    private ApiUploadService uploadService;

    @MockBean
    private SftpService sftpService;

    @RegisterExtension
    LogbackCaptor logbackCaptor = new LogbackCaptor(UploaderService.class);


    @ParameterizedTest
    @ValueSource( strings = { "journal"})
    void shouldProcess(String type) {
        var expectedFile = FileUploadDefinition.builder()
                .fileName(randomAlphanumeric(10,20))
                .directory("/journals")
                .type(randomString())
                .content("testfile".getBytes())
                .build();
        var uploaderResponse = UploaderResponse.builder()
                .fileId(randomAlphanumeric(10,20))
                .message("success")
                .success(true)
                .build();

        when(extractorService.extract(type, expectedFile)).thenReturn(Mono.just(expectedFile));
        when(uploadService.upload(expectedFile)).thenReturn(Mono.just(uploaderResponse));

        service.process(type, expectedFile);

        verify(extractorService).extract(type, expectedFile);
        verify(uploadService).upload(expectedFile);
    }
}
