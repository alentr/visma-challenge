package com.visma.challenge.gateways;

import com.visma.challenge.domains.ZonePrice;
import com.visma.challenge.domains.ZonesTypes;
import com.visma.challenge.providers.IZonePriceProvider;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class ZonePriceProviderGateway implements IZonePriceProvider {

  @Override
  public ZonePrice execute(ZonesTypes zone) {
    return switch (zone) {
      case M1 -> new ZonePrice(zone, new BigDecimal(60));
      case M2 -> new ZonePrice(zone, new BigDecimal(100));
      case M3 -> new ZonePrice(zone, new BigDecimal(120));
    };
  }
}
