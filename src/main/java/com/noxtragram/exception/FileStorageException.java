package com.noxtragram.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FileStorageException extends RuntimeException {

  public FileStorageException(String message) {
    super(message);
  }

  public FileStorageException(String message, Throwable cause) {
    super(message, cause);
  }

  // Constructor với thông tin file cụ thể
  public FileStorageException(String operation, String fileName, Throwable cause) {
    super(String.format("Failed to %s file: %s", operation, fileName), cause);
  }
}
