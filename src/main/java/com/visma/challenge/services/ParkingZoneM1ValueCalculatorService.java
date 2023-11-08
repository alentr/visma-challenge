package com.visma.challenge.services;

import com.visma.challenge.configs.RoudingConstants;
import com.visma.challenge.domains.ZonesTypes;
import com.visma.challenge.providers.IZonePriceProvider;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;

@Service("M1")
public class ParkingZoneM1ValueCalculatorService extends AbstractZoneValueCalculator implements
  IZoneCalculable {

  public ParkingZoneM1ValueCalculatorService(IZonePriceProvider zonePriceProvider) {
    super(zonePriceProvider);
  }

  @Override
  public BigDecimal calculateParkingPrice(ZonesTypes zone, LocalDateTime entry, LocalDateTime exit) {

    var parkingTimeInMinutes = (int) entry.until(exit, ChronoUnit.MINUTES);

    return getZonePrice(zone).getPriceByMinute().multiply(BigDecimal.valueOf(parkingTimeInMinutes))
      .setScale(RoudingConstants.SCALE, RoudingConstants.ROUNDING_MODE);
  }
}
