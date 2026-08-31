package com.platform.address.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UkAddressControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testValidatePostcode_Path_Valid() throws Exception {
        mockMvc.perform(get("/api/v1/address/uk/validate/SW1A 2AA")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void testValidatePostcode_QueryParam_Valid() throws Exception {
        mockMvc.perform(get("/api/v1/address/uk/validate")
                        .param("postcode", "SW1A 2AA")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void testValidatePostcode_Invalid() throws Exception {
        mockMvc.perform(get("/api/v1/address/uk/validate/INVALID123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(false));
    }

    @Test
    void testLookupPostcode_Path() throws Exception {
        mockMvc.perform(get("/api/v1/address/uk/lookup/SW1A 2AA")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.valid").value(true))
                .andExpect(jsonPath("$.data.postcode").value("SW1A 2AA"));
    }

    @Test
    void testLookupPostcode_QueryParam() throws Exception {
        mockMvc.perform(get("/api/v1/address/uk/lookup")
                        .param("postcode", "SW1A 2AA")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.valid").value(true))
                .andExpect(jsonPath("$.data.postcode").value("SW1A 2AA"));
    }

    @Test
    void testLookupPremises_Path() throws Exception {
        mockMvc.perform(get("/api/v1/address/uk/premises/HA9 7ES")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.valid").value(true))
                .andExpect(jsonPath("$.data.postcode").value("HA9 7ES"))
                .andExpect(jsonPath("$.data.totalPremises").isNumber())
                .andExpect(jsonPath("$.data.addresses[0].formattedAddress").isNotEmpty());
    }

    @Test
    void testLookupPremises_QueryParam() throws Exception {
        mockMvc.perform(get("/api/v1/address/uk/premises")
                        .param("postcode", "HA9 7ES")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.valid").value(true))
                .andExpect(jsonPath("$.data.postcode").value("HA9 7ES"))
                .andExpect(jsonPath("$.data.totalPremises").isNumber())
                .andExpect(jsonPath("$.data.addresses[0].formattedAddress").isNotEmpty());
    }

    @Test
    void testMissingPostcode_Returns400() throws Exception {
        mockMvc.perform(get("/api/v1/address/uk/lookup")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Postcode is required. Please provide it as a query parameter (?postcode=...) or in the URL path."));
    }

    @Test
    void testAutocomplete() throws Exception {
        mockMvc.perform(get("/api/v1/address/uk/autocomplete?query=SW1A")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }
}
