package com.project.shopapp.exception;

import com.project.shopapp.entity.ProductImageEntity;
import com.project.shopapp.model.response.ApiErrorResponse;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleRuntimeException(RuntimeException e) {
        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                errorCode.getHttpStatus(),
                errorCode.getMessage()
        );

        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiErrorResponse);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiErrorResponse> handleAppException(AppException e) {
        ErrorCode errorCode = e.getErrorCode();

        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                errorCode.getHttpStatus(), errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiErrorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ObjectError> errorMessages = e.getBindingResult().getAllErrors();
        Map<String, String> map = new HashMap<>();

        errorMessages.forEach((error) -> {
            String key = ((FieldError) error).getField();
            String val = error.getDefaultMessage();
            map.put(key, val);
        });

        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                errorCode.getHttpStatus(),
                errorCode.getMessage(),
                map);

        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiErrorResponse);
    }

    @ExceptionHandler(InvalidParamException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidParamException(InvalidParamException e) {
        ErrorCode errorCode = ErrorCode.INVALID_PARAM;
        String message = String.format(errorCode.getMessage(), ProductImageEntity.MAXIMUM_IMAGES_PER_PRODUCT);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(errorCode.getHttpStatus(), message);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiErrorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                errorCode.getHttpStatus(), errorCode.getMessage()
        );

        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiErrorResponse);
    }
}
