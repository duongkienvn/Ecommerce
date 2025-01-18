package com.project.shopapp.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION("Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_NOT_EXISTED("User not existed", HttpStatus.NOT_FOUND),
    USER_EXISTED("User existed", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED("Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED("You do not have permission", HttpStatus.FORBIDDEN),
    DATA_NOT_FOUND("Data doesn't exist!", HttpStatus.NOT_FOUND),
    VALIDATION_ERROR("Validattion failed!", HttpStatus.BAD_REQUEST),
    UNMATCHED_PASSWORD("Password is unmatched!", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND("Category not found", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND("Product not found", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND("Order not found", HttpStatus.BAD_REQUEST),
    ORDER_DETAILS_NOT_FOUND("Order_Details not found", HttpStatus.BAD_REQUEST),
    INVALID_DATE("Shipping date must not before the current date!", HttpStatus.BAD_REQUEST),
    INVALID_PARAM("The number of image must <= %d", HttpStatus.BAD_REQUEST)
    ;


    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
