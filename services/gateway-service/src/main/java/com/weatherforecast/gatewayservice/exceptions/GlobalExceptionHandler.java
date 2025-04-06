package com.weatherforecast.gatewayservice.exceptions;

import com.weatherforecast.gatewayservice.dto.http.ValidationErrorResponseDto;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
    log.error("Validation error: {}", ex.getMessage());
    HashMap<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              String filedName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(filedName, errorMessage);
            });
    var response = ValidationErrorResponseDto.builder().errors(errors).build();
    return ResponseEntity.badRequest().body(response);
  }
}
