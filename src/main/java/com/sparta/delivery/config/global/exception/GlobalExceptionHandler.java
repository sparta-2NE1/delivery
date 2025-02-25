package com.sparta.delivery.config.global.exception;

import com.sparta.delivery.config.global.exception.custom.*;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> illegalArgumentException(IllegalArgumentException ex) {
        int status = HttpServletResponse.SC_BAD_REQUEST;
        ExceptionResponse response = new ExceptionResponse("Bad Request", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ExceptionResponse> nullPointerException(NullPointerException ex) {
        int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        ExceptionResponse response = new ExceptionResponse("INTERNAL_SERVER_ERROR", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ExceptionResponse> forbiddenException(ForbiddenException ex) {
        int status = HttpServletResponse.SC_FORBIDDEN;
        ExceptionResponse response = new ExceptionResponse("FORBIDDEN", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> userNotFoundException(UserNotFoundException ex) {
        int status = HttpServletResponse.SC_NOT_FOUND;
        ExceptionResponse response = new ExceptionResponse("User_Not_Found", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(RefreshTokenAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> RefreshTokenAlreadyExistsException(RefreshTokenAlreadyExistsException ex) {
        int status = HttpServletResponse.SC_CONFLICT;
        ExceptionResponse response = new ExceptionResponse("CONFLICT", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ExceptionResponse> InvalidTokenException(InvalidTokenException ex) {
        int status = HttpServletResponse.SC_UNAUTHORIZED;
        ExceptionResponse response = new ExceptionResponse("UNAUTHORIZED", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ExceptionResponse> ExpiredJwtException(ExpiredJwtException ex) {
        int status = HttpServletResponse.SC_UNAUTHORIZED;
        ExceptionResponse response = new ExceptionResponse("UNAUTHORIZED", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ExceptionResponse> InvalidRefreshTokenException(InvalidRefreshTokenException ex) {
        int status = HttpServletResponse.SC_UNAUTHORIZED;
        ExceptionResponse response = new ExceptionResponse("UNAUTHORIZED", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ExceptionResponse> unauthorizedException(UnauthorizedException ex) {
        int status = HttpServletResponse.SC_UNAUTHORIZED;
        ExceptionResponse response = new ExceptionResponse("UNAUTHORIZED", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ExceptionResponse> productNotFoundException(ProductNotFoundException ex) {
        int status = HttpServletResponse.SC_NOT_FOUND;
        ExceptionResponse response = new ExceptionResponse("PRODUCT_NOT_FOUND", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(DuplicateProductException.class)
    public ResponseEntity<ExceptionResponse> duplicateProductException(DuplicateProductException ex) {
        int status = HttpServletResponse.SC_CONFLICT;
        ExceptionResponse response = new ExceptionResponse("CONFLICT", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(ProductAlreadyDeletedException.class)
    public ResponseEntity<ExceptionResponse> productAlreadyDeletedException(ProductAlreadyDeletedException ex) {
        int status = HttpServletResponse.SC_CONFLICT;
        ExceptionResponse response = new ExceptionResponse("CONFLICT", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(PaymentAlreadyCompletedException.class)
    public ResponseEntity<ExceptionResponse> PaymentAlreadyCompletedException(PaymentAlreadyCompletedException ex) {
        int status = HttpServletResponse.SC_CONFLICT;
        ExceptionResponse response = new ExceptionResponse("Already Paid", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(ExistCardException.class)
    public ResponseEntity<ExceptionResponse> ExistCardException(ExistCardException ex) {
        int status = HttpServletResponse.SC_CONFLICT;
        ExceptionResponse response = new ExceptionResponse("Already Register Card", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(DeliveryAddressNotFoundException.class)
    public ResponseEntity<ExceptionResponse> DeliveryAddressNotFoundException(DeliveryAddressNotFoundException ex) {
        int status = HttpServletResponse.SC_NOT_FOUND;
        ExceptionResponse response = new ExceptionResponse("DeliveryAddress Not Found", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(StoreNotFoundException.class)
    public ResponseEntity<ExceptionResponse> StoreNotFoundException(StoreNotFoundException ex) {
        int status = HttpServletResponse.SC_NOT_FOUND;
        ExceptionResponse response = new ExceptionResponse("STORE_NOT_FOUND", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(RegionNotFoundException.class)
    public ResponseEntity<ExceptionResponse> RegionNotFoundException(RegionNotFoundException ex) {
        int status = HttpServletResponse.SC_NOT_FOUND;
        ExceptionResponse response = new ExceptionResponse("REGION_NOT_FOUND", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ExceptionResponse> OrderNotFoundException(OrderNotFoundException ex) {
        int status = HttpServletResponse.SC_NOT_FOUND;
        ExceptionResponse response = new ExceptionResponse("ORDER_NOT_FOUND", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<ExceptionResponse> ReviewNotFoundException(ReviewNotFoundException ex) {
        int status = HttpServletResponse.SC_NOT_FOUND;
        ExceptionResponse response = new ExceptionResponse("REVIEW_NOT_FOUND", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(UserOrderNotFoundException.class)
    public ResponseEntity<ExceptionResponse> UserOrderNotFoundException(UserOrderNotFoundException ex) {
        int status = HttpServletResponse.SC_NOT_FOUND;
        ExceptionResponse response = new ExceptionResponse("USER_ORDER_NOT_FOUND", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(ReviewNotAllowedException.class)
    public ResponseEntity<ExceptionResponse> ReviewNotAllowedException(ReviewNotAllowedException ex) {
        int status = HttpServletResponse.SC_FORBIDDEN;
        ExceptionResponse response = new ExceptionResponse("REVIEW_NOT_ALLOWED", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(ProductSelectionRequiredException.class)
    public ResponseEntity<ExceptionResponse> ProductSelectionRequiredException(ProductSelectionRequiredException ex) {
        int status = HttpServletResponse.SC_FORBIDDEN;
        ExceptionResponse response = new ExceptionResponse("SELECT_MIN_ONE_PRODUCT", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(OrderModificationNotAllowedException.class)
    public ResponseEntity<ExceptionResponse> OrderModificationNotAllowedException(OrderModificationNotAllowedException ex) {
        int status = HttpServletResponse.SC_FORBIDDEN;
        ExceptionResponse response = new ExceptionResponse("ORDER_MODIFICATION_NOT_ALLOWED", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(ProductQuantityNotAllowedException.class)
    public ResponseEntity<ExceptionResponse> ProductQuantityNotAllowedException(ProductQuantityNotAllowedException ex) {
        int status = HttpServletResponse.SC_FORBIDDEN;
        ExceptionResponse response = new ExceptionResponse("PRODUCT_QUANTITY_NOT_ALLOWED", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(NotStoreOwnerException.class)
    public ResponseEntity<ExceptionResponse> NotStoreOwnerException(NotStoreOwnerException ex) {
        int status = HttpServletResponse.SC_FORBIDDEN;
        ExceptionResponse response = new ExceptionResponse("NOT_STORE_OWNER", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(NotStoreProductException.class)
    public ResponseEntity<ExceptionResponse> NotStoreProductException(NotStoreProductException ex) {
        int status = HttpServletResponse.SC_FORBIDDEN;
        ExceptionResponse response = new ExceptionResponse("NOT_STORE_PRODUCT", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(ReviewAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> ReviewAlreadyExistsException(ReviewAlreadyExistsException ex) {
        int status = HttpServletResponse.SC_CONFLICT;
        ExceptionResponse response = new ExceptionResponse("CONFLICT", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ExceptionResponse> AuthorizationDeniedException(AuthorizationDeniedException ex) {
        int status = HttpServletResponse.SC_FORBIDDEN;
        ExceptionResponse response = new ExceptionResponse("Access Denied", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }


    @ExceptionHandler(InvalidApiResponseException.class)
    public ResponseEntity<ExceptionResponse> InvalidApiResponseException(InvalidApiResponseException ex){
        int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        ExceptionResponse response = new ExceptionResponse("INVALID_API_RESPONSE", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> exception(Exception ex) {
        int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        ExceptionResponse response = new ExceptionResponse("그 외 모든 에러", ex.getMessage(), status);
        return ResponseEntity.status(status).body(response);
    }

}