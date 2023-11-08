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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ParkingZoneM1ValueCalculatorServiceTest {

  private IZonePriceProvider zonePriceProvider = mock();

  private ParkingZoneM1ValueCalculatorService subject = new ParkingZoneM1ValueCalculatorService(zonePriceProvider);

  @Nested
  @DisplayName("when it calculates the price for a M1 parking zone by minutes")
  class CalculateParkingPrice {

    final ZonesTypes zone = ZonesTypes.M1;
    final ZonePrice zonePrice = new ZonePrice(zone, new BigDecimal(60));

    @BeforeEach
    void setUp() {
      when(zonePriceProvider.execute(zone)).thenReturn(zonePrice);
    }

    @ParameterizedTest
    @ValueSource(ints = {-3, 0, 5, 15000})
    @DisplayName("it should return the right value")
    void calculate(int totalTimeInMinutes) {
      var timeNow = LocalDateTime.now();
      var entry = timeNow;
      var exit = timeNow.plusMinutes(totalTimeInMinutes);

      var expected = zonePrice.getPriceByMinute().multiply(BigDecimal.valueOf(totalTimeInMinutes)).setScale(
        RoudingConstants.SCALE, RoudingConstants.ROUNDING_MODE);

      var response = subject.calculateParkingPrice(zone, entry, exit);

      assertEquals(expected, response);
    }
  }
}