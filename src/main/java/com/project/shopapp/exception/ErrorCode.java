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
    DATA_NOT_FOUND("Invalid phonenumber or password!", HttpStatus.BAD_REQUEST),
    VALIDATION_ERROR("Validation failed!", HttpStatus.BAD_REQUEST),
    UNMATCHED_PASSWORD("Password is unmatched!", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND("Category not found", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND("Product not found", HttpStatus.NOT_FOUND),
    ORDER_NOT_FOUND("Order not found", HttpStatus.BAD_REQUEST),
    ORDER_DETAILS_NOT_FOUND("Order_Details not found", HttpStatus.BAD_REQUEST),
    INVALID_DATE("Shipping date must not before the current date!", HttpStatus.BAD_REQUEST),
    INVALID_PARAM("The number of image must <= %d", HttpStatus.BAD_REQUEST),
    BAD_CREDENTIALS("Phone number or password is wrong!", HttpStatus.BAD_REQUEST),
    PERMISSION_DENY("You can't register an admin account!", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXISTED("Role doesn't exist!", HttpStatus.NOT_FOUND),
    DATA_INTEGRITY_VIOLATION("Phone number has already existed!", HttpStatus.BAD_REQUEST),
    COMMENT_NOT_FOUND("Comment not existed!", HttpStatus.NOT_FOUND),
    EMAIL_NOT_FOUND("Email not existed!", HttpStatus.NOT_FOUND),
    INVALID_PASSWORD("Current Password is wrong!", HttpStatus.BAD_REQUEST)
    ;

    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
