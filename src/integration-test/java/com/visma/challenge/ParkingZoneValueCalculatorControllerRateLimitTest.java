package com.visma.challenge;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.visma.challenge.domains.ZonesTypes;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest(properties = {
  "rate-limit.replenishRate=1",
  "rate-limit.burstCapacity=1"})
@AutoConfigureMockMvc
public class ParkingZoneValueCalculatorControllerRateLimitTest {

  @Autowired
  private MockMvc mockMvc;

  @Nested
  @DisplayName("when it calls GET /zone to calculate the parking price for a given zone")
  class CalculateParkingPriceForGivenZone {

    @Nested
    @DisplayName("when it reaches the rate limit")
    class RateLimitReached {

      @Test
      @DisplayName("it http status code 429 after reached the rate limit")
      void returnParkingPrice() throws Exception {
        var timeNow = LocalDateTime.now();

        var request = MockMvcRequestBuilders.get("/zone")
          .queryParam("zone", ZonesTypes.M1.name())
          .queryParam("entry", timeNow.toString())
          .queryParam("exit", timeNow.plusMinutes(3).toString())
          .queryParam("customerId", "customer_123");

        // consume the rate limit (1)
        mockMvc.perform(request)
          .andExpect(status().isOk())
          .andExpect(content().string("{\"zone\":\"M1\",\"value\":3.00}"));

        for (int i = 0; i <= 3; i++) {
          mockMvc.perform(request)
            .andExpect(status().is(HttpStatus.TOO_MANY_REQUESTS.value()));
        }
      }
    }
  }
}
