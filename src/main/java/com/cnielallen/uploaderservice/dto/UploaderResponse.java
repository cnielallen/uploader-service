package com.cnielallen.uploaderservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploaderResponse {
    private   String fileId;
    private   String message;
    private  boolean success;
}
