package com.visma.challenge.services;

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

class ParkingZoneM2ValueCalculatorServiceTest {

  private IZonePriceProvider zonePriceProvider = mock();

  private ParkingZoneM2ValueCalculatorService subject = new ParkingZoneM2ValueCalculatorService(zonePriceProvider);

  @Nested
  @DisplayName("when it calculates the price for a M2 parking zone by hours")
  class CalculateParkingPrice {

    final ZonesTypes zone = ZonesTypes.M2;
    final ZonePrice zonePrice = new ZonePrice(zone, new BigDecimal(100));

    @BeforeEach
    void setUp() {
      when(zonePriceProvider.execute(zone)).thenReturn(zonePrice);
    }

    @Nested
    @DisplayName("when it only has weekdays hours")
    class WeekDayHours {

      @ParameterizedTest
      @ValueSource(ints = {0, 1, 60, 50})
      @DisplayName("it should return the right value")
      void calculate(int totalTimeInHours) {
        var entry = LocalDateTime.of(2023, 11, 6, 15, 10);
        var exit = entry.plusHours(totalTimeInHours);

        var expected = zonePrice.getPriceByHour().multiply(BigDecimal.valueOf(totalTimeInHours)).setScale(
          RoudingConstants.SCALE, RoudingConstants.ROUNDING_MODE);

        var response = subject.calculateParkingPrice(zone, entry, exit);

        assertEquals(expected, response);
      }
    }

    @Nested
    @DisplayName("when it only has weekend hours")
    class WeekendHours {

      @ParameterizedTest
      @ValueSource(ints = {0, 1, 7, 31})
      @DisplayName("it should apply the multiplier and return the right value")
      void calculate(int totalTimeInHours) {
        var entry = LocalDateTime.of(2023, 11, 11, 15, 10);
        var exit = entry.plusHours(totalTimeInHours);

        var valuePerHour = zonePrice.getPriceByHour()
          .multiply(BigDecimal.valueOf(ParkingZoneM2ValueCalculatorService.WEEKEND_MULTIPLIER));

        var expected = valuePerHour.multiply(BigDecimal.valueOf(totalTimeInHours))
          .setScale(RoudingConstants.SCALE, RoudingConstants.ROUNDING_MODE);

        var response = subject.calculateParkingPrice(zone, entry, exit);

        assertEquals(expected, response);
      }
    }

    @Nested
    @DisplayName("when it has both weekdays and weekend hours")
    class WeekdaysAndWeekenddHours {

      @Test
      @DisplayName("it should return the right value")
      void calculate() {
        var entry = LocalDateTime.of(2023, 11, 10, 22, 10);
        var exit = entry.plusHours(3).plusMinutes(1);

        final int weekdaysHours = 2;
        final int weekendHours = 2;

        var valuePerWeekendHours = zonePrice.getPriceByHour()
          .multiply(BigDecimal.valueOf(ParkingZoneM2ValueCalculatorService.WEEKEND_MULTIPLIER));

        var valuePerWeekdaysHours = zonePrice.getPriceByHour();

        var expected = valuePerWeekendHours.multiply(BigDecimal.valueOf(weekendHours))
          .add(valuePerWeekdaysHours.multiply(BigDecimal.valueOf(weekdaysHours)))
          .setScale(RoudingConstants.SCALE, RoudingConstants.ROUNDING_MODE);

        var response = subject.calculateParkingPrice(zone, entry, exit);

        assertEquals(expected, response);
      }
    }
  }
}