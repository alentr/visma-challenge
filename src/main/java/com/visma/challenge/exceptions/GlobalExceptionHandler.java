package com.visma.challenge.exceptions;

import static java.lang.System.currentTimeMillis;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(RateLimitExceededException.class)
  public ResponseEntity<StandardError> rateLimitExceededException(RateLimitExceededException e) {

    var err = new StandardError(HttpStatus.TOO_MANY_REQUESTS.value(), e.getMessage(), currentTimeMillis());

    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(err);
  }

}
