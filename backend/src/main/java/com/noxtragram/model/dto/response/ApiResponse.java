package com.noxtragram.model.dto.response;

import java.time.LocalDateTime;

public class ApiResponse<T> {
  private boolean success;
  private String message;
  private T data;
  private LocalDateTime timestamp;
  private String error;

  // Manual getters and setters
  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public static <T> ApiResponse<T> success(String message, T data) {
    ApiResponse<T> response = new ApiResponse<>();
    response.setSuccess(true);
    response.setMessage(message);
    response.setData(data);
    response.setTimestamp(LocalDateTime.now());
    return response;
  }

  public static <T> ApiResponse<T> error(String error) {
    ApiResponse<T> response = new ApiResponse<>();
    response.setSuccess(false);
    response.setError(error);
    response.setTimestamp(LocalDateTime.now());
    return response;
  }
}
