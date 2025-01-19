package com.project.shopapp.exception;

import com.project.shopapp.entity.ProductImageEntity;
import com.project.shopapp.model.response.ApiErrorResponse;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

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
        List<String> errorMessages = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(errorCode.getHttpStatus(),
                errorCode.getMessage(),
                errorMessages);

        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiErrorResponse);
    }

    @ExceptionHandler(InvalidParamException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidParamException(InvalidParamException e) {
        ErrorCode errorCode = ErrorCode.INVALID_PARAM;
        String message = String.format(errorCode.getMessage(), ProductImageEntity.MAXIMUM_IMAGES_PER_PRODUCT);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(errorCode.getHttpStatus(), message);
        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiErrorResponse);
    }
}
