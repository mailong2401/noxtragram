package com.noxtragram.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class OperationNotAllowedException extends RuntimeException {

  public OperationNotAllowedException(String message) {
    super(message);
  }

  public OperationNotAllowedException(String message, Throwable cause) {
    super(message, cause);
  }

  public OperationNotAllowedException(String operation, String reason) {
    super(String.format("Operation '%s' not allowed: %s", operation, reason));
  }
}
