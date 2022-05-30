package com.cnielallen.uploaderservice.service;

import com.cnielallen.uploaderservice.config.FileProperties;
import com.cnielallen.uploaderservice.config.WebClientConfig;
import com.cnielallen.uploaderservice.dto.UploaderResponse;
import com.cnielallen.uploaderservice.model.FileUploadDefinition;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import com.cnielallen.uploaderservice.exception.FilelUploadException;
import org.junit.After;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

@Slf4j
@EnableConfigurationProperties
@SpringBootTest(classes = {WebClientConfig.class, FileProperties.class, ApiUploadService.class})
public class ApiUploadServiceTest {

    @Autowired
    private FileProperties fileProperties;

    @Autowired
    private ApiUploadService apiUploadService;

    private FileUploadDefinition fileUploadDefinition;

    private MockMultipartFile file = new MockMultipartFile("test", "filename", "application/json", "{\"test\":\"value\"}".getBytes());

    private static final WireMockServer WIRE_MOCK_SERVER = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());

    @BeforeAll
    static void beforeAll(){
        WIRE_MOCK_SERVER.start();
    }

    @AfterAll
    static void afterAll(){
        WIRE_MOCK_SERVER.stop();
    }

    @SneakyThrows
    @BeforeEach
    void setupEach(){
        WIRE_MOCK_SERVER.resetAll();
        var type = "journal";
        var fileConfig = fileProperties.forType(type).orElseThrow(()-> new RuntimeException("Missing file mapping for type : "+ type));
        fileUploadDefinition = FileUploadDefinition.builder()
                .httpMethod(fileConfig.getHttpMethod())
                .fileName("filename")
                .content(file.getBytes())
                .apiUrl(WIRE_MOCK_SERVER.baseUrl() + "/upload-journals").build();
    }

    @Test
    void shouldReturnUploaderResponseIfHTTPStatus200(){
        stubForReturnSuccessHTTPStatus();
        assertThat(apiUploadService.upload(fileUploadDefinition).block()).isInstanceOf(UploaderResponse.class);
    }

    @Test
    void shouldThrowFileUploadExceptionResponseIfErrorHTTPStatus(){
        stubForReturnErrorHTTPStatus();
        assertThrows(FilelUploadException.class, ()-> apiUploadService.upload(fileUploadDefinition).block());
    }



    private void stubForReturnErrorHTTPStatus() {
        WIRE_MOCK_SERVER.stubFor(post(urlPathEqualTo("/upload-journals"))
                        .withHeader("Accept", equalTo("application/json, multipart/form-data"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(400)
                        .withBody("{\"json\": \"value\"}")));

    }

    private void stubForReturnSuccessHTTPStatus() {
        WIRE_MOCK_SERVER.stubFor(post(urlPathEqualTo("/upload-journals"))
                .withHeader("Accept", equalTo("application/json, multipart/form-data"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("{\"json\": \"value\"}")));

    }
}
