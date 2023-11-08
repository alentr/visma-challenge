package com.visma.challenge.services;

import com.visma.challenge.configs.RoudingConstants;
import com.visma.challenge.domains.ZonePrice;
import com.visma.challenge.domains.ZonesTypes;
import com.visma.challenge.providers.IZonePriceProvider;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service("M2")
public class ParkingZoneM2ValueCalculatorService extends AbstractZoneValueCalculator implements
  IZoneCalculable {

  public static final float WEEKEND_MULTIPLIER = 2;


  public ParkingZoneM2ValueCalculatorService(IZonePriceProvider zonePriceProvider) {
    super(zonePriceProvider);
  }

  private static BigDecimal getWeekdayPrice(ZonePrice zonePrice, long weekdayHours) {
    return zonePrice.getPriceByHour().multiply(BigDecimal.valueOf(weekdayHours));
  }

  private static BigDecimal getWeekendPrice(ZonePrice zonePrice, long weekendHours) {
    return zonePrice.getPriceByHour().multiply(BigDecimal.valueOf(weekendHours)).multiply(BigDecimal.valueOf(
      WEEKEND_MULTIPLIER));
  }

  @Override
  public BigDecimal calculateParkingPrice(ZonesTypes zone, LocalDateTime entry, LocalDateTime exit) {
    return calculatePrice(getZonePrice(zone), entry, exit)
      .setScale(RoudingConstants.SCALE, RoudingConstants.ROUNDING_MODE);
  }

  private BigDecimal calculatePrice(ZonePrice zonePrice, LocalDateTime entry, LocalDateTime exit) {
    long weekendHours = 0;
    long weekdayHours = 0;

    for (LocalDateTime entryDate = entry; entryDate.isBefore(exit);
      entryDate = entryDate.plusHours(1)) {

      if (entryDate.getDayOfWeek().equals(DayOfWeek.SATURDAY) || entryDate.getDayOfWeek()
        .equals(DayOfWeek.SUNDAY)) {

        weekendHours += 1;
      } else {
        weekdayHours += 1;
      }
    }

    return getWeekendPrice(zonePrice, weekendHours).add(getWeekdayPrice(zonePrice, weekdayHours));
  }
}
