package com.visma.challenge.exceptions;

public class RateLimitExceededException extends RuntimeException {

  public RateLimitExceededException(String msg) {
    super(msg);
  }
}
