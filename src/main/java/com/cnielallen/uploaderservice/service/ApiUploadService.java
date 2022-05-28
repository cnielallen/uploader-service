package com.cnielallen.uploaderservice.service;

import com.cnielallen.uploaderservice.config.FileProperties;
import com.cnielallen.uploaderservice.dto.UploaderResponse;
import com.cnielallen.uploaderservice.exception.FilelUploadException;
import com.cnielallen.uploaderservice.model.FileUploadDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiUploadService {
    private final WebClient webClient;
    private final FileProperties properties;

    public Mono<Object> upload(FileUploadDefinition fileUploadDefinition){
        log.info("Sending API Request to {}", fileUploadDefinition.getApiUrl());
        var apiResponse = webClient.method(HttpMethod.valueOf(fileUploadDefinition.getHttpMethod()))
                .uri(fileUploadDefinition.getApiUrl())
                .headers( httpHeaders -> headers(httpHeaders, fileUploadDefinition, properties.getDefaultCredentials()))
                .accept(MediaType.APPLICATION_JSON, MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(fileUploadDefinition.getBody()))
                .retrieve()
                .bodyToMono(UploaderResponse.class)
                .onErrorResume(error -> Mono.error(new FilelUploadException(error)));

        return apiResponse.map(response -> response);
    }

    private void headers(HttpHeaders httpHeaders, FileUploadDefinition fileUploadDefinition, FileProperties.ApiCredentials apiCredentials){
        Optional.ofNullable(fileUploadDefinition.getUsername())
            .ifPresentOrElse(username -> httpHeaders.setBasicAuth(
                fileUploadDefinition.getUsername(), fileUploadDefinition.getUsername()),
                () -> {
                    if(apiCredentials != null && isNotEmpty(apiCredentials.getUsername()) && isNotEmpty(apiCredentials.getPassword())) {
                        httpHeaders.setBasicAuth(
                                apiCredentials.getUsername(), apiCredentials.getUsername());
                    }
            });

    }
}
