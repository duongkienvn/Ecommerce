package com.project.shopapp.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiErrorResponse {
    HttpStatus status;
    String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<String> errors;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp;

    public ApiErrorResponse(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
        timestamp = LocalDateTime.now();
    }

    public ApiErrorResponse(HttpStatus status, String message, List<String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
        timestamp = LocalDateTime.now();
    }
}
