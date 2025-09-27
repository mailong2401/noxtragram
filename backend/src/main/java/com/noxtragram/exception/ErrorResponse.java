package com.noxtragram.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {
  private int status;
  private String message;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime timestamp;

  public ErrorResponse(int status, String message, LocalDateTime timestamp) {
    this.status = status;
    this.message = message;
    this.timestamp = timestamp;
  }

  // Getters and Setters
  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }
}

class ValidationErrorResponse extends ErrorResponse {
  private Map<String, String> errors;

  public ValidationErrorResponse(int status, String message, LocalDateTime timestamp, Map<String, String> errors) {
    super(status, message, timestamp);
    this.errors = errors;
  }

  public Map<String, String> getErrors() {
    return errors;
  }

  public void setErrors(Map<String, String> errors) {
    this.errors = errors;
  }
}
