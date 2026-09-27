package com.example.boilerplate.web;

import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class WebGlobalExceptionHandler extends ResponseEntityExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(WebGlobalExceptionHandler.class);

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
    int code = status.value();
    String reason = (status instanceof HttpStatus hs) ? hs.getReasonPhrase() : "";
    ApiError apiError = new ApiError(code, reason, message, request.getDescription(false));
    log.warn("Validation failed: {}", message);
    return new ResponseEntity<>(apiError, headers, status);
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
    log.error("Unhandled exception", ex);
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    ApiError apiError =
        new ApiError(
            status.value(),
            status.getReasonPhrase(),
            ex.getMessage(),
            request.getDescription(false));
    return new ResponseEntity<>(apiError, status);
  }
}
