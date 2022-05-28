package com.cnielallen.uploaderservice.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ToString
public class UploaderResponse {
    private final  String fileId;
    private final  String message;
    private final boolean success;
}
