package com.visma.challenge.interactors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.visma.challenge.domains.ZonesTypes;
import com.visma.challenge.services.IZoneCalculable;
import com.visma.challenge.services.ParkingZoneM1ValueCalculatorService;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

class ParkingZoneValueCalculatorInteractorTest {

  private final ApplicationContext applicationContext = mock();

  private ParkingZoneM1ValueCalculatorService parkingZoneM1ValueCalculatorService = mock();

  private ParkingZoneValueCalculatorInteractor subject = new ParkingZoneValueCalculatorInteractor(applicationContext);

  @Nested
  @DisplayName("when it calculates the price for a parking zone")
  class CalculatePrice {

    @BeforeEach
    void setUp() {
      when(applicationContext.getBeansOfType(IZoneCalculable.class)).thenReturn(
        Map.of(ZonesTypes.M1.name(), parkingZoneM1ValueCalculatorService));
    }

    @Test
    @DisplayName("it should call the proper bean")
    void callProperBean() {

      var zone = ZonesTypes.M1;
      var entry = LocalDateTime.now();
      var exit = LocalDateTime.now();

      subject.execute(zone, entry, exit);

      verify(parkingZoneM1ValueCalculatorService).calculateParkingPrice(zone, entry, exit);
    }
  }
}
