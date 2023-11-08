package com.visma.challenge.services;

import static com.visma.challenge.services.ParkingZoneM3ValueCalculatorService.EXTRA_HOURS_MULTIPLIER;
import static com.visma.challenge.services.ParkingZoneM3ValueCalculatorService.FIRST_MINUTES_FREE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.visma.challenge.configs.RoudingConstants;
import com.visma.challenge.domains.ZonePrice;
import com.visma.challenge.domains.ZonesTypes;
import com.visma.challenge.providers.IZonePriceProvider;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ParkingZoneM3ValueCalculatorServiceTest {

  private IZonePriceProvider zonePriceProvider = mock();

  private ParkingZoneM3ValueCalculatorService subject = new ParkingZoneM3ValueCalculatorService(
    zonePriceProvider);

  @Nested
  @DisplayName("when it calculates the price for a M3 parking zone by hours")
  class CalculateParkingPrice {

    final ZonesTypes zone = ZonesTypes.M3;
    final ZonePrice zonePrice = new ZonePrice(zone, new BigDecimal(120));

    @BeforeEach
    void setUp() {
      when(zonePriceProvider.execute(zone)).thenReturn(zonePrice);
    }

    @Nested
    @DisplayName("when it is on Sunday")
    class Sunday {

      @Test
      @DisplayName("it should not charge any value")
      void calculate() {
        var entry = LocalDateTime.of(2023, 11, 12, 10, 10);
        var exit = entry.plusHours(3).plusMinutes(1);

        var response = subject.calculateParkingPrice(zone, entry, exit);

        assertEquals(BigDecimal.ZERO.setScale(RoudingConstants.SCALE, RoudingConstants.ROUNDING_MODE), response);
      }
    }

    @Nested
    @DisplayName("when it only has normal price")
    class WeekDayHours {

      @ParameterizedTest
      @ValueSource(ints = {15, 60, 180, 345})
      @DisplayName("it should return the right value")
      void calculate(long totalTimeInMinutes) {
        var entry = LocalDateTime.of(2023, 11, 10, 9, 0);
        var exit = entry.plusMinutes(totalTimeInMinutes);
        var firstMinutesFree = FIRST_MINUTES_FREE;

        totalTimeInMinutes = totalTimeInMinutes <= firstMinutesFree
          ? 0 : totalTimeInMinutes - firstMinutesFree;

        var expected = zonePrice.getPriceByMinute().multiply(BigDecimal.valueOf(totalTimeInMinutes)).setScale(
          RoudingConstants.SCALE, RoudingConstants.ROUNDING_MODE);

        var response = subject.calculateParkingPrice(zone, entry, exit);

        assertEquals(expected, response);
      }
    }

    @Nested
    @DisplayName("when it only has extra price")
    class WeekendHours {

      @ParameterizedTest
      @ValueSource(ints = {10, 45, 145, 425})
      @DisplayName("it should return the right value")
      void calculate(long totalTimeInMinutes) {
        var entry = LocalDateTime.of(2023, 11, 10, 16, 30);
        var exit = entry.plusMinutes(totalTimeInMinutes);

        var expected = zonePrice.getPriceByMinute().multiply(
            BigDecimal.valueOf(EXTRA_HOURS_MULTIPLIER)).multiply(BigDecimal.valueOf(totalTimeInMinutes))
          .setScale(
            RoudingConstants.SCALE, RoudingConstants.ROUNDING_MODE);

        var response = subject.calculateParkingPrice(zone, entry, exit);

        assertEquals(expected, response);
      }
    }

    @Nested
    @DisplayName("when it has normal and extra hours and Sunday")
    class WeekdaysAndWeekenddHours {

      @Test
      @DisplayName("it should return the right value")
      void calculate() {
        var entry = LocalDateTime.of(2023, 11, 12, 15, 0);

        var priceByMinute = zonePrice.getPriceByMinute();

        var sundayMinutes = 540;
        var extraMinutes = 480;
        var normalMinutes = 180;

        var exit = entry.plusMinutes(sundayMinutes + extraMinutes + normalMinutes);

        var sundayPrice = BigDecimal.ZERO;

        var extraPrice = priceByMinute.multiply(BigDecimal.valueOf(EXTRA_HOURS_MULTIPLIER))
          .multiply(BigDecimal.valueOf(extraMinutes));

        var normalPrice = priceByMinute.multiply(BigDecimal.valueOf(normalMinutes - FIRST_MINUTES_FREE));

        var expected = sundayPrice.add(extraPrice).add(normalPrice)
          .setScale(RoudingConstants.SCALE, RoudingConstants.ROUNDING_MODE);

        var response = subject.calculateParkingPrice(zone, entry, exit);

        assertEquals(expected, response);
      }
    }
  }
}