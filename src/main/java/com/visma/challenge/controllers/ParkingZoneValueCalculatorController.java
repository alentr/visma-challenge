package com.visma.challenge.controllers;

import com.visma.challenge.controllers.views.ZoneValueView;
import com.visma.challenge.domains.ZonesTypes;
import com.visma.challenge.interactors.ParkingZoneValueCalculatorInteractor;
import com.visma.challenge.validators.RateLimitVerification;
import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/zone")
public class ParkingZoneValueCalculatorController {

  private final ParkingZoneValueCalculatorInteractor parkingZoneValueCalculatorInteractor;

  public ParkingZoneValueCalculatorController(ParkingZoneValueCalculatorInteractor parkingZoneValueCalculatorInteractor) {
    this.parkingZoneValueCalculatorInteractor = parkingZoneValueCalculatorInteractor;
  }

  @GetMapping
  @RateLimitVerification
  public ResponseEntity<ZoneValueView> getZoneValue(
    @RequestParam(name = "zone") ZonesTypes zone,
    @RequestParam(name = "entry") LocalDateTime entry,
    @RequestParam(name = "exit") LocalDateTime exit,
    @RequestParam(name = "customerId") String customerId
  ) {
    return ResponseEntity.ok(parkingZoneValueCalculatorInteractor.execute(zone, entry, exit));
  }
}
