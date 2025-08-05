package com.codegym.projectmodule5.exception;

import com.codegym.projectmodule5.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ApiResponse> handleValidation(MethodArgumentNotValidException ex) {
//        String error = ex.getBindingResult().getFieldError().getDefaultMessage();
//        return ResponseEntity.badRequest().body(new ApiResponse(false, error));
//    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse> handleCustom(CustomException ex) {
        return ResponseEntity.badRequest().body(new ApiResponse(false, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleDefault(Exception ex) {
        return ResponseEntity.internalServerError().body(new ApiResponse(false, "Server error"));
    }
}
