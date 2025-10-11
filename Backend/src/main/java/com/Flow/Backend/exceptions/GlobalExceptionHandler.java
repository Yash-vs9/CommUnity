package com.Flow.Backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String, String>> handleInvalidToken(InvalidTokenException ex) {
        Map<String, String> errorBody = new HashMap<>();
        errorBody.put("message", ex.getMessage());
        return new ResponseEntity<>(errorBody, HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyExistException(UserAlreadyExistException ex) {
        Map<String, String> errorBody = new HashMap<>();
        errorBody.put("message", ex.getMessage());
        return new ResponseEntity<>(errorBody, HttpStatus.CONFLICT);
    }
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePostNotFoundException(PostNotFoundException ex) {
        Map<String, String> errorBody = new HashMap<>();
        errorBody.put("message", ex.getMessage());
        return new ResponseEntity<>(errorBody, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(PasswordWrongException.class)
    public ResponseEntity<Map<String, String>> handlePasswordWrongException(PasswordWrongException ex) {
        Map<String, String> errorBody = new HashMap<>();
        errorBody.put("message", ex.getMessage());
        return new ResponseEntity<>(errorBody, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

}
