package com.platform.address.controller;

import com.platform.address.dto.response.ApiResponse;
import com.platform.address.dto.response.UkAddressLookupResponse;
import com.platform.address.dto.response.UkPremisesLookupResponse;
import com.platform.address.service.UkAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/address/uk")
@Tag(name = "UK Address Service", description = "Standalone microservice for UK Postcode validation, premise lookup, and autocomplete")
public class UkAddressController {

    private final UkAddressService ukAddressService;

    public UkAddressController(UkAddressService ukAddressService) {
        this.ukAddressService = ukAddressService;
    }

    @GetMapping("/validate/{postcode}")
    @Operation(summary = "Validate if a UK postcode is active and valid")
    public ResponseEntity<ApiResponse<Boolean>> validatePostcode(@PathVariable String postcode) {
        boolean valid = ukAddressService.isValidPostcode(postcode);
        return ResponseEntity.ok(ApiResponse.success(valid ? "Postcode is valid" : "Postcode is invalid", valid));
    }

    @GetMapping("/lookup/{postcode}")
    @Operation(summary = "Lookup geo coordinates, region, and administrative district for a UK postcode")
    public ResponseEntity<ApiResponse<UkAddressLookupResponse>> lookupPostcode(@PathVariable String postcode) {
        UkAddressLookupResponse response = ukAddressService.lookupPostcode(postcode);
        return ResponseEntity.ok(ApiResponse.success("Postcode details retrieved", response));
    }

    @GetMapping("/premises/{postcode}")
    @Operation(summary = "Lookup full list of individual premise addresses (flats, house numbers) for a UK postcode")
    public ResponseEntity<ApiResponse<UkPremisesLookupResponse>> lookupPremises(@PathVariable String postcode) {
        UkPremisesLookupResponse response = ukAddressService.lookupPremises(postcode);
        return ResponseEntity.ok(ApiResponse.success("Premises retrieved successfully", response));
    }

    @GetMapping("/autocomplete")
    @Operation(summary = "Autocomplete partial UK postcode queries")
    public ResponseEntity<ApiResponse<List<String>>> autocomplete(@RequestParam String query) {
        List<String> results = ukAddressService.autocompletePostcode(query);
        return ResponseEntity.ok(ApiResponse.success("Autocomplete results", results));
    }
}
