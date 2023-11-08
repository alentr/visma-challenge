package com.visma.challenge.services;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BucketService {

  private final ProxyManager<String> localProxyManager;

  private final Duration refillInterval;
  private final long replenishRate;
  private final long burstCapacity;

  public BucketService(
    @Qualifier("localProxyManager") ProxyManager<String> localProxyManager,
    @Value("${rate-limit.refillInterval}") Duration refillInterval,
    @Value("${rate-limit.replenishRate}") long replenishRate,
    @Value("${rate-limit.burstCapacity}") long burstCapacity) {

    this.localProxyManager = localProxyManager;
    this.refillInterval = refillInterval;
    this.replenishRate = replenishRate;
    this.burstCapacity = burstCapacity;
  }

  public boolean tryConsume(String customerId) {
    return localProxyManager.builder().build(customerId, bucketConfigurationSupplier()).tryConsume(1);
  }

  private Supplier<BucketConfiguration> bucketConfigurationSupplier() {
    var refill = Refill.intervally(replenishRate, refillInterval);
    var bandwidth = Bandwidth.classic(burstCapacity, refill);

    return () -> new BucketConfiguration(List.of(bandwidth));
  }
}
