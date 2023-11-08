package com.visma.challenge.interactors;

import com.visma.challenge.controllers.views.ZoneValueView;
import com.visma.challenge.domains.ZonesTypes;
import com.visma.challenge.services.IZoneCalculable;
import java.time.LocalDateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class ParkingZoneValueCalculatorInteractor {

  private final ApplicationContext appContext;

  public ParkingZoneValueCalculatorInteractor(ApplicationContext appContext) {
    this.appContext = appContext;
  }

  public ZoneValueView execute(ZonesTypes zone, LocalDateTime entry, LocalDateTime exit) {
    var zonesCalculator = appContext.getBeansOfType(IZoneCalculable.class);

    return new ZoneValueView(zone.name(),
      zonesCalculator.get(zone.name()).calculateParkingPrice(zone, entry, exit));
  }
}
