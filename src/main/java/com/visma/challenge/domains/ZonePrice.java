package com.visma.challenge.domains;

import com.visma.challenge.configs.RoudingConstants;
import java.math.BigDecimal;

public class ZonePrice {

  private ZonesTypes zone;

  private BigDecimal zonePriceByHour;

  public ZonePrice(ZonesTypes zone, BigDecimal zonePriceByHour) {
    this.zone = zone;
    this.zonePriceByHour = zonePriceByHour;
  }

  public ZonesTypes getZone() {
    return this.zone;
  }

  public BigDecimal getPriceByHour() {
    return this.zonePriceByHour;
  }

  public BigDecimal getPriceByMinute() {
    return zonePriceByHour.divide(BigDecimal.valueOf(60), RoudingConstants.SCALE,
      RoudingConstants.ROUNDING_MODE);
  }
}
