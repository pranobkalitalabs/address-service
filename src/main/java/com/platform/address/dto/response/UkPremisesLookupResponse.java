package com.platform.address.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "List of full UK premise addresses for a postcode")
public class UkPremisesLookupResponse implements Serializable {

    private boolean valid;
    private String postcode;
    private int totalPremises;
    private List<PremiseAddressDto> addresses = new ArrayList<>();

    public UkPremisesLookupResponse() {
    }

    public UkPremisesLookupResponse(boolean valid, String postcode, List<PremiseAddressDto> addresses) {
        this.valid = valid;
        this.postcode = postcode;
        this.addresses = addresses != null ? addresses : new ArrayList<>();
        this.totalPremises = this.addresses.size();
    }

    public static UkPremisesLookupResponse invalid(String postcode) {
        UkPremisesLookupResponse res = new UkPremisesLookupResponse();
        res.setValid(false);
        res.setPostcode(postcode);
        res.setTotalPremises(0);
        res.setAddresses(new ArrayList<>());
        return res;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public int getTotalPremises() {
        return totalPremises;
    }

    public void setTotalPremises(int totalPremises) {
        this.totalPremises = totalPremises;
    }

    public List<PremiseAddressDto> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<PremiseAddressDto> addresses) {
        this.addresses = addresses != null ? addresses : new ArrayList<>();
        this.totalPremises = this.addresses.size();
    }
}
