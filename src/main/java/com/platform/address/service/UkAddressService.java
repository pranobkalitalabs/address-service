package com.platform.address.service;

import com.platform.address.dto.response.UkAddressLookupResponse;
import com.platform.address.dto.response.UkPremisesLookupResponse;

import java.util.List;

public interface UkAddressService {

    UkAddressLookupResponse lookupPostcode(String postcode);

    UkPremisesLookupResponse lookupPremises(String postcode);

    boolean isValidPostcode(String postcode);

    List<String> autocompletePostcode(String partialPostcode);
}
