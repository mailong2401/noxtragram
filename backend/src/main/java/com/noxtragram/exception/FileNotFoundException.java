package com.noxtragram.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FileNotFoundException extends RuntimeException {

  public FileNotFoundException(String message) {
    super(message);
  }

  public FileNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  // Constructor với thông tin file cụ thể
  public FileNotFoundException(String filename, String directory) {
    super(String.format("File not found: %s in directory: %s", filename, directory));
  }

  public FileNotFoundException(String filename, String directory, Throwable cause) {
    super(String.format("File not found: %s in directory: %s", filename, directory), cause);
  }
}
