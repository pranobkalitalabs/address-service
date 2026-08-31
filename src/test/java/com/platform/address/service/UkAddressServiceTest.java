package com.platform.address.service;

import com.platform.address.dto.response.UkAddressLookupResponse;
import com.platform.address.service.impl.UkAddressServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;

class UkAddressServiceTest {

    private UkAddressService ukAddressService;

    @BeforeEach
    void setUp() {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://api.postcodes.io")
                .build();
        ukAddressService = new UkAddressServiceImpl(restClient);
    }

    @Test
    void testValidatePostcode_NullOrEmpty() {
        assertFalse(ukAddressService.isValidPostcode(null));
        assertFalse(ukAddressService.isValidPostcode(""));
        assertFalse(ukAddressService.isValidPostcode("   "));
    }

    @Test
    void testLookupPostcode_NullOrEmpty() {
        UkAddressLookupResponse response = ukAddressService.lookupPostcode(null);
        assertNotNull(response);
        assertFalse(response.isValid());

        UkAddressLookupResponse emptyResponse = ukAddressService.lookupPostcode("");
        assertNotNull(emptyResponse);
        assertFalse(emptyResponse.isValid());
    }

    @Test
    void testLookupPostcode_FallbackSW1A() {
        UkAddressLookupResponse response = ukAddressService.lookupPostcode("SW1A 1AA");
        assertNotNull(response);
        assertTrue(response.isValid());
        assertEquals("SW1A 1AA", response.getPostcode());
        assertNotNull(response.getLatitude());
        assertNotNull(response.getLongitude());
    }

    @Test
    void testLookupPremises_WembleyHA9() {
        var response = ukAddressService.lookupPremises("HA9 7ES");
        assertNotNull(response);
        assertTrue(response.isValid());
        assertTrue(response.getTotalPremises() >= 5);
        assertNotNull(response.getAddresses().get(0).getFormattedAddress());
        assertTrue(response.getAddresses().get(0).getFormattedAddress().contains("Bluebell"));
    }

    @Test
    void testLookupPremises_NullOrInvalid() {
        var response = ukAddressService.lookupPremises(null);
        assertNotNull(response);
        assertFalse(response.isValid());
        assertEquals(0, response.getTotalPremises());
    }
}
