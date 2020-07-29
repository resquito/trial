package org.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class UserExistsException extends RuntimeException {

  public UserExistsException(String message) {
    super(message);
  }
}
