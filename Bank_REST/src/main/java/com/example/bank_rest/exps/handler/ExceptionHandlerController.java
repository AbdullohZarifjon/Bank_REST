package com.example.bank_rest.exps.handler;

import com.example.bank_rest.exps.*;
import com.example.bank_rest.payload.common.ApiResponse;
import com.example.bank_rest.payload.common.ApiResponseFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(BadCredentialsException.class)
    public ApiResponse<Void> handleBadCredentials(BadCredentialsException ex) {
        return ApiResponseFactory.error(HttpStatus.UNAUTHORIZED, "Incorrect username or password.");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse<Void> handleAccessDenied(AccessDeniedException ex) {
        return ApiResponseFactory.error(HttpStatus.FORBIDDEN, "Access denied. Admin role required.");
    }

    @ExceptionHandler(RecordAlreadyException.class)
    public ApiResponse<Void> handleRecordAlreadyException(RecordAlreadyException ex) {
        return ApiResponseFactory.error(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public ApiResponse<Void> handleRecordNotFoundException(RecordNotFoundException ex) {
        return ApiResponseFactory.error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                errors.put(fieldError.getField(), fieldError.getDefaultMessage())
        );
        return ApiResponseFactory.error(HttpStatus.BAD_REQUEST, "Validation error", errors);
    }

    @ExceptionHandler(InvalidOldPasswordException.class)
    public ApiResponse<Void> handleInvalidOldPassword(InvalidOldPasswordException ex) {
        return ApiResponseFactory.error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InactiveAccountException.class)
    public ApiResponse<Void> handleInactiveAccount(InactiveAccountException ex) {
        return ApiResponseFactory.error(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(BlockedAccountException.class)
    public ApiResponse<Void> handleBlockedAccount(BlockedAccountException ex) {
        return ApiResponseFactory.error(HttpStatus.LOCKED, ex.getMessage());
    }

    @ExceptionHandler(CardNotActiveException.class)
    public ApiResponse<Void> handleCardNotActive(CardNotActiveException ex) {
        return ApiResponseFactory.error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(InvalidCardOwnerException.class)
    public ApiResponse<Void> handleInvalidCardOwner(InvalidCardOwnerException ex) {
        return ApiResponseFactory.error(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ApiResponse<Void> handleInsufficientBalance(InsufficientBalanceException ex) {
        return ApiResponseFactory.error(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ApiResponse<Void> handleUnauthorized(UnauthorizedException ex) {
        return ApiResponseFactory.error(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleGlobalException(Exception ex) {
        ex.printStackTrace();
        return ApiResponseFactory.error(HttpStatus.INTERNAL_SERVER_ERROR, "Internal system error: " + ex.getMessage());
    }
}
