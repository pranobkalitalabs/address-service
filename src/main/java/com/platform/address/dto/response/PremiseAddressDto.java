package com.platform.address.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Individual UK Premise / Building Address")
public class PremiseAddressDto implements Serializable {

    private String id;
    private String formattedAddress;
    private String buildingName;
    private String buildingNumber;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String county;
    private String postcode;
    private Double latitude;
    private Double longitude;

    public PremiseAddressDto() {
    }

    public PremiseAddressDto(String id, String formattedAddress, String buildingName, String buildingNumber, String addressLine1, String addressLine2, String city, String county, String postcode, Double latitude, Double longitude) {
        this.id = id;
        this.formattedAddress = formattedAddress;
        this.buildingName = buildingName;
        this.buildingNumber = buildingNumber;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.county = county;
        this.postcode = postcode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
