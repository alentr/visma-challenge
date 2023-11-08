package com.visma.challenge.configs;

import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.caffeine.CaffeineProxyManager;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Bucket4jConfiguration {

  private final Duration caffeineKeepAfterRefillDuration;

  public Bucket4jConfiguration(
    @Value("${rate-limit.in-memory-duration}") Duration caffeineKeepAfterRefillDuration) {
    this.caffeineKeepAfterRefillDuration = caffeineKeepAfterRefillDuration;
  }

  @Bean("localProxyManager")
  public ProxyManager<String> localProxyManager() {
    return new CaffeineProxyManager<>(Caffeine.newBuilder(), caffeineKeepAfterRefillDuration);
  }
}
