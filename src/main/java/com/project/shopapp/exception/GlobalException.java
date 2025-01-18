package com.project.shopapp.exception;

import com.project.shopapp.entity.ProductEntity;
import com.project.shopapp.entity.ProductImageEntity;
import com.project.shopapp.model.response.ApiErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleRuntimeException(RuntimeException e) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                ErrorCode.UNCATEGORIZED_EXCEPTION.getHttpStatus(),
                ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage()
        );

        return ResponseEntity.badRequest().body(apiErrorResponse);
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

        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(ErrorCode.VALIDATION_ERROR.getHttpStatus(),
                ErrorCode.VALIDATION_ERROR.getMessage(),
                errorMessages);

        return ResponseEntity.badRequest().body(apiErrorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;

        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(
                errorCode.getHttpStatus(), errorCode.getMessage()
        );

        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiErrorResponse);
    }

    @ExceptionHandler(InvalidParamException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidParamException(InvalidParamException e) {
        ErrorCode errorCode = ErrorCode.INVALID_PARAM;
        String message = String.format(errorCode.getMessage(), ProductImageEntity.MAXIMUM_IMAGES_PER_PRODUCT);
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(errorCode.getHttpStatus(), errorCode.getMessage());

        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiErrorResponse);
    }
}
