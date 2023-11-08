package com.visma.challenge.services;

import com.visma.challenge.domains.ZonesTypes;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface IZoneCalculable {

  BigDecimal calculateParkingPrice(ZonesTypes zone, LocalDateTime entry, LocalDateTime exit);

}
