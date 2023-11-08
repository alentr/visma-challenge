package com.visma.challenge.providers;

import com.visma.challenge.domains.ZonePrice;
import com.visma.challenge.domains.ZonesTypes;

public interface IZonePriceProvider {

  ZonePrice execute(ZonesTypes zone);

}
