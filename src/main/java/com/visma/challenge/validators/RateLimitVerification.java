package com.visma.challenge.validators;

import com.visma.challenge.exceptions.RateLimitExceededException;
import com.visma.challenge.services.BucketService;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimitVerification {

  @Aspect
  @Component
  class RateLimitVerificationAspect {

    private final BucketService bucketService;

    public RateLimitVerificationAspect(BucketService bucketService) {
      this.bucketService = bucketService;
    }

    @Before("@annotation(RateLimitVerification)")
    public void before(JoinPoint joinPoint) {
      //this can be improved to be dynamic and not hard-coded
      var customerId = (String) joinPoint.getArgs()[3];

      if (!bucketService.tryConsume(customerId)) {
        throw new RateLimitExceededException("The number of requests has been exceeded. Try again later.");
      }
    }
  }
}
