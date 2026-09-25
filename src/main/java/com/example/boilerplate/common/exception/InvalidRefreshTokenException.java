package com.example.boilerplate.common.exception;

public class InvalidRefreshTokenException extends RuntimeException {
  public InvalidRefreshTokenException() {
    super("Refresh token is invalid or expired");
  }
}
