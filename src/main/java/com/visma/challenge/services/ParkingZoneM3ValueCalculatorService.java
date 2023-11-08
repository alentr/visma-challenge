package com.visma.challenge.services;

import com.visma.challenge.configs.RoudingConstants;
import com.visma.challenge.domains.ZonesTypes;
import com.visma.challenge.providers.IZonePriceProvider;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service("M3")
public class ParkingZoneM3ValueCalculatorService extends AbstractZoneValueCalculator implements
  IZoneCalculable {

  private static final DayOfWeek FREE_DAY = DayOfWeek.SUNDAY;

  private static final DayOfWeek START_DAY_NORMAL_PRICE = DayOfWeek.MONDAY;

  private static final int START_HOUR_NORMAL_PRICE = 8;

  private static final DayOfWeek END_DAY_NORMAL_PRICE = DayOfWeek.SATURDAY;

  private static final int END_HOUR_NORMAL_PRICE = 16;

  public static final long FIRST_MINUTES_FREE = 60;

  public static final float EXTRA_HOURS_MULTIPLIER = 1.5F;

  private final String sundayTime = "SUNDAY_TIME";

  private final String normalTime = "NORMAL_TIME";

  private final String extraTime = "EXTRA_TIME";

  public ParkingZoneM3ValueCalculatorService(IZonePriceProvider zonePriceProvider) {
    super(zonePriceProvider);
  }

  @Override
  public BigDecimal calculateParkingPrice(ZonesTypes zone, LocalDateTime entry, LocalDateTime exit) {
    if (isSunday(entry, exit)) {
      return BigDecimal.ZERO.setScale(RoudingConstants.SCALE, RoudingConstants.ROUNDING_MODE);
    } else {
      var parkingTime = calculateParkingTime(entry, exit);

      long normalTimeInMinutes = subtractNonChargeableNormalTime(parkingTime.get(normalTime));
      long extraTimeInMinutes = parkingTime.get(extraTime);

      return calculateParkingPrice(zone, normalTimeInMinutes, extraTimeInMinutes)
        .setScale(RoudingConstants.SCALE, RoudingConstants.ROUNDING_MODE);
    }
  }

  private BigDecimal calculateParkingPrice(ZonesTypes zone, long normalTimeInMinutes, long extraTimeInMinutes) {
    var zonePrice = getZonePrice(zone);
    var priceByMinute = zonePrice.getPriceByMinute();

    return priceByMinute.multiply(BigDecimal.valueOf(normalTimeInMinutes)).add(
      priceByMinute.multiply(BigDecimal.valueOf(EXTRA_HOURS_MULTIPLIER))
        .multiply(BigDecimal.valueOf(extraTimeInMinutes)));
  }

  private long subtractNonChargeableNormalTime(long timeInMinutesNormalPrice) {
    return timeInMinutesNormalPrice <= FIRST_MINUTES_FREE ? 0 : timeInMinutesNormalPrice - FIRST_MINUTES_FREE;
  }

  private Map<String, Long> calculateParkingTime(LocalDateTime entry, LocalDateTime exit) {
    long sundayTimeInMinutes = 0;
    long normalTimeInMinutes = 0;
    long extraTimeInMinutes = 0;

    // it will just consider completed minutes
    for (LocalDateTime dateTime = entry.withSecond(0); dateTime.isBefore(exit.withSecond(0));
      dateTime = dateTime.plusMinutes(1).withSecond(0)) {

      if (isSundayTime(dateTime)) {
        sundayTimeInMinutes++;
      } else if (isInNormalPrice(dateTime)) {
        normalTimeInMinutes++;
      } else {
        extraTimeInMinutes++;
      }
    }

    return Map.of(
      sundayTime, sundayTimeInMinutes,
      normalTime, normalTimeInMinutes,
      extraTime, extraTimeInMinutes);
  }

  private boolean isSundayTime(LocalDateTime dateTime) {
    return dateTime.getDayOfWeek().equals(ParkingZoneM3ValueCalculatorService.FREE_DAY);
  }

  private boolean isInNormalPrice(LocalDateTime dateTime) {
    return dateTime.getDayOfWeek().getValue() >= START_DAY_NORMAL_PRICE.getValue()
      && dateTime.getHour() >= START_HOUR_NORMAL_PRICE
      && dateTime.getDayOfWeek().getValue() <= END_DAY_NORMAL_PRICE.getValue()
      && dateTime.getHour() < END_HOUR_NORMAL_PRICE;
  }

  private boolean isSunday(LocalDateTime entry, LocalDateTime exit) {
    return entry.getDayOfWeek().equals(FREE_DAY)
      && entry.withHour(0).withMinute(0).withSecond(0).equals(exit.withHour(0).withMinute(0).withSecond(0));
  }
}
