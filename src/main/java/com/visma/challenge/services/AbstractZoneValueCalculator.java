package com.visma.challenge.services;

import com.visma.challenge.domains.ZonePrice;
import com.visma.challenge.domains.ZonesTypes;
import com.visma.challenge.providers.IZonePriceProvider;

public abstract class AbstractZoneValueCalculator {

  private final IZonePriceProvider zonePriceProvider;

  public AbstractZoneValueCalculator(IZonePriceProvider zonePriceProvider) {
    this.zonePriceProvider = zonePriceProvider;
  }

  public ZonePrice getZonePrice(ZonesTypes zone) {
    return zonePriceProvider.execute(zone);
  }
}
