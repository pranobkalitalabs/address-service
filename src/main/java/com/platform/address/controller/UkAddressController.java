package com.platform.address.controller;

import com.platform.address.dto.response.ApiResponse;
import com.platform.address.dto.response.UkAddressLookupResponse;
import com.platform.address.dto.response.UkPremisesLookupResponse;
import com.platform.address.exception.BadRequestException;
import com.platform.address.service.UkAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/address/uk")
@Tag(name = "UK Address Service", description = "Standalone microservice for UK Postcode validation, premise lookup, and autocomplete. Accepts postcodes as Query Params (?postcode=) or Path Variables (/{postcode}).")
public class UkAddressController {

    private final UkAddressService ukAddressService;

    public UkAddressController(UkAddressService ukAddressService) {
        this.ukAddressService = ukAddressService;
    }

    @GetMapping({"/validate", "/validate/{postcode}"})
    @Operation(
            summary = "Validate if a UK postcode is active and valid",
            description = "Validates a UK postcode. You can pass the postcode either as a query param (?postcode=SW1A 2AA) or as a path variable (/validate/SW1A 2AA)."
    )
    public ResponseEntity<ApiResponse<Boolean>> validatePostcode(
            @Parameter(description = "UK Postcode in path (optional if query param is provided)")
            @PathVariable(required = false) String postcode,
            @Parameter(description = "UK Postcode as query param (e.g. ?postcode=SW1A 2AA)")
            @RequestParam(name = "postcode", required = false) String postcodeParam) {
        String targetPostcode = resolvePostcode(postcode, postcodeParam);
        boolean valid = ukAddressService.isValidPostcode(targetPostcode);
        return ResponseEntity.ok(ApiResponse.success(valid ? "Postcode is valid" : "Postcode is invalid", valid));
    }

    @GetMapping({"/lookup", "/lookup/{postcode}"})
    @Operation(
            summary = "Lookup geo coordinates, region, and administrative district for a UK postcode",
            description = "Retrieves geolocation, country, district, and region. Accepts postcode via query param (?postcode=SW1A 2AA) or path (/lookup/SW1A 2AA)."
    )
    public ResponseEntity<ApiResponse<UkAddressLookupResponse>> lookupPostcode(
            @Parameter(description = "UK Postcode in path (optional if query param is provided)")
            @PathVariable(required = false) String postcode,
            @Parameter(description = "UK Postcode as query param (e.g. ?postcode=SW1A 2AA)")
            @RequestParam(name = "postcode", required = false) String postcodeParam) {
        String targetPostcode = resolvePostcode(postcode, postcodeParam);
        UkAddressLookupResponse response = ukAddressService.lookupPostcode(targetPostcode);
        return ResponseEntity.ok(ApiResponse.success("Postcode details retrieved", response));
    }

    @GetMapping({"/premises", "/premises/{postcode}"})
    @Operation(
            summary = "Lookup full list of individual premise addresses (flats, apartments, house numbers) for a UK postcode",
            description = "Retrieves full building premises and flat addresses for dropdown population. Accepts postcode via query param (?postcode=HA9 7ES) or path (/premises/HA9 7ES)."
    )
    public ResponseEntity<ApiResponse<UkPremisesLookupResponse>> lookupPremises(
            @Parameter(description = "UK Postcode in path (optional if query param is provided)")
            @PathVariable(required = false) String postcode,
            @Parameter(description = "UK Postcode as query param (e.g. ?postcode=HA9 7ES)")
            @RequestParam(name = "postcode", required = false) String postcodeParam) {
        String targetPostcode = resolvePostcode(postcode, postcodeParam);
        UkPremisesLookupResponse response = ukAddressService.lookupPremises(targetPostcode);
        return ResponseEntity.ok(ApiResponse.success("Premises retrieved successfully", response));
    }

    @GetMapping("/autocomplete")
    @Operation(
            summary = "Autocomplete partial UK postcode queries",
            description = "Returns matching active UK postcode suggestions for a given search prefix (e.g. ?query=SW1A)."
    )
    public ResponseEntity<ApiResponse<List<String>>> autocomplete(
            @Parameter(description = "Partial postcode prefix (e.g. SW1A)")
            @RequestParam(name = "query", required = false) String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new BadRequestException("Query parameter 'query' is required for autocomplete");
        }
        List<String> results = ukAddressService.autocompletePostcode(query.trim());
        return ResponseEntity.ok(ApiResponse.success("Autocomplete results", results));
    }

    /**
     * Resolves the postcode from either the path variable or query parameter.
     */
    private String resolvePostcode(String pathPostcode, String queryPostcode) {
        if (pathPostcode != null && !pathPostcode.trim().isEmpty()) {
            return pathPostcode.trim();
        }
        if (queryPostcode != null && !queryPostcode.trim().isEmpty()) {
            return queryPostcode.trim();
        }
        throw new BadRequestException("Postcode is required. Please provide it as a query parameter (?postcode=...) or in the URL path.");
    }
}
