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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
public class ParkingZoneValueCalculatorControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Nested
  @DisplayName("when it calls GET /zone to calculate the parking price for a given zone")
  class CalculateParkingPriceForGivenZone {

    @Nested
    @DisplayName("when is the M1 Zone")
    class M1Zone {

      @Test
      @DisplayName("it returns the parking price for the M1 zone")
      void returnParkingPrice() throws Exception {
        var timeNow = LocalDateTime.now();

        var request = MockMvcRequestBuilders.get("/zone")
          .queryParam("zone", ZonesTypes.M1.name())
          .queryParam("entry", timeNow.toString())
          .queryParam("exit", timeNow.plusMinutes(3).toString())
          .queryParam("customerId", "customer_123");

        mockMvc.perform(request)
          .andExpect(status().isOk())
          .andExpect(content().string("{\"zone\":\"M1\",\"value\":3.00}"));
      }
    }

    @Nested
    @DisplayName("when is the M2 Zone")
    class M2Zone {

      @Test
      @DisplayName("it returns the parking price for the M2 zone")
      void returnParkingPrice() throws Exception {
        var entry = LocalDateTime.of(2023, 11, 10, 22, 10);
        var exit = entry.plusHours(3).plusMinutes(1);

        var request = MockMvcRequestBuilders.get("/zone")
          .queryParam("zone", ZonesTypes.M2.name())
          .queryParam("entry", entry.toString())
          .queryParam("exit", exit.toString())
          .queryParam("customerId", "customer_123");

        mockMvc.perform(request)
          .andExpect(status().isOk())
          .andExpect(content().string("{\"zone\":\"M2\",\"value\":600.00}"));
      }
    }

    @Nested
    @DisplayName("when is the M3 Zone")
    class M3Zone {

      @Test
      @DisplayName("it returns the parking price for the M3 zone")
      void returnParkingPrice() throws Exception {
        var entry = LocalDateTime.of(2023, 11, 12, 15, 0);
        var exit = entry.plusHours(20);

        var request = MockMvcRequestBuilders.get("/zone")
          .queryParam("zone", ZonesTypes.M3.name())
          .queryParam("entry", entry.toString())
          .queryParam("exit", exit.toString())
          .queryParam("customerId", "customer_123");

        mockMvc.perform(request)
          .andExpect(status().isOk())
          .andExpect(content().string("{\"zone\":\"M3\",\"value\":1680.00}"));
      }
    }
  }
}
