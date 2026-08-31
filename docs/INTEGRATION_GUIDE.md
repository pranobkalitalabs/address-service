# 🔌 Microservice & Frontend Integration Guide

This guide provides clean, production-ready integration examples for other **Spring Boot microservices** (such as `auth-service`, `order-service`, `billing-service`) and **Frontend applications** to consume **`address-service`**.

---

## 🏛️ Base Service Endpoints

| Environment | Base URL |
| :--- | :--- |
| **Local Docker / Dev** | `http://localhost:8082` |
| **Cloud Production** | `https://address.pranobkalitalabs.co.uk` |

> 💡 **Tip for Integrators**: You can pass postcodes either as a **Query Parameter** (`?postcode=SW1A 2AA`) or as a **Path Variable** (`/SW1A 2AA`).

---

## ☕ 1. Java / Spring Boot Microservice Integration

### A. Data Transfer Objects (DTOs)
Add these reusable DTO classes into your consuming microservice:

```java
package com.platform.client.dto;

import java.util.List;

// Envelope response structure
public record ApiResponse<T>(
    boolean success,
    String message,
    T data,
    String timestamp
) {}

// Postcode details DTO
public record UkAddressLookupResponse(
    boolean valid,
    String postcode,
    String country,
    String region,
    String adminDistrict,
    String parliamentaryConstituency,
    Double latitude,
    Double longitude
) {}

// Full Premises and Flats response
public record UkPremisesLookupResponse(
    boolean valid,
    String postcode,
    int totalPremises,
    List<PremiseAddressDto> addresses
) {}

public record PremiseAddressDto(
    String id,
    String formattedAddress,
    String buildingName,
    String buildingNumber,
    String addressLine1,
    String addressLine2,
    String city,
    String county,
    String postcode,
    Double latitude,
    Double longitude
) {}
```

---

### B. Spring Boot 3 `RestClient` Client Implementation
Using Spring Boot 3's modern, non-blocking `RestClient`:

```java
package com.platform.client;

import com.platform.client.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Service
public class AddressServiceClient {

    private final RestClient restClient;

    public AddressServiceClient(
            @Value("${address-service.url:http://localhost:8082}") String addressServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(addressServiceUrl)
                .build();
    }

    /**
     * Validates whether a UK postcode is valid and active.
     */
    public boolean validatePostcode(String postcode) {
        try {
            ApiResponse<Boolean> response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/address/uk/validate")
                            .queryParam("postcode", postcode)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<Boolean>>() {});
            return response != null && Boolean.TRUE.equals(response.data());
        } catch (Exception ex) {
            return false; // Fallback gracefully on network error
        }
    }

    /**
     * Retrieves geocoding coordinates (latitude/longitude) and district.
     */
    public UkAddressLookupResponse lookupPostcode(String postcode) {
        ApiResponse<UkAddressLookupResponse> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/address/uk/lookup")
                        .queryParam("postcode", postcode)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<UkAddressLookupResponse>>() {});
        return response != null ? response.data() : null;
    }

    /**
     * Retrieves all individual flat numbers and building addresses for a postcode.
     */
    public UkPremisesLookupResponse lookupPremises(String postcode) {
        ApiResponse<UkPremisesLookupResponse> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/address/uk/premises")
                        .queryParam("postcode", postcode)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<UkPremisesLookupResponse>>() {});
        return response != null ? response.data() : null;
    }
}
```

---

## 🎨 2. Frontend UI Integration (React & Vanilla JS)

Populate an address selection dropdown whenever a user enters their postcode on registration/checkout forms:

### React / TypeScript Component Example:
```tsx
import React, { useState } from 'react';

interface PremiseAddress {
  id: string;
  formattedAddress: string;
  addressLine1: string;
  addressLine2: string;
  city: string;
  postcode: string;
  latitude: number;
  longitude: number;
}

export const UkAddressLookup: React.FC = () => {
  const [postcode, setPostcode] = useState('');
  const [premises, setPremises] = useState<PremiseAddress[]>([]);
  const [loading, setLoading] = useState(false);
  const [selectedAddress, setSelectedAddress] = useState<PremiseAddress | null>(null);

  const fetchPremises = async () => {
    if (!postcode) return;
    setLoading(true);
    try {
      const res = await fetch(
        `https://address.pranobkalitalabs.co.uk/api/v1/address/uk/premises?postcode=${encodeURIComponent(postcode)}`
      );
      const json = await res.json();
      if (json.success && json.data.addresses) {
        setPremises(json.data.addresses);
      }
    } catch (err) {
      console.error('Failed to load premises:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="address-lookup-container">
      <label>UK Postcode:</label>
      <input
        type="text"
        placeholder="e.g. HA9 7ES"
        value={postcode}
        onChange={(e) => setPostcode(e.target.value.toUpperCase())}
      />
      <button onClick={fetchPremises} disabled={loading}>
        {loading ? 'Finding Addresses...' : 'Find Address'}
      </button>

      {premises.length > 0 && (
        <div className="dropdown-section">
          <label>Select your flat / building:</label>
          <select
            onChange={(e) => {
              const selected = premises.find((p) => p.id === e.target.value);
              setSelectedAddress(selected || null);
            }}
          >
            <option value="">-- Choose an address --</option>
            {premises.map((p) => (
              <option key={p.id} value={p.id}>
                {p.formattedAddress}
              </option>
            ))}
          </select>
        </div>
      )}
    </div>
  );
};
```

---

## 🛡️ 3. Standardized Error Handling & HTTP Status Dictionary

All responses from `address-service` follow a uniform JSON envelope:

### Successful Response Envelope (`200 OK`):
```json
{
  "success": true,
  "message": "Premises retrieved successfully",
  "data": { ... },
  "timestamp": "2026-08-31T19:00:00Z"
}
```

### Error Response Envelope:
```json
{
  "success": false,
  "message": "Postcode is required. Please provide it as a query parameter (?postcode=...) or in the URL path.",
  "data": null,
  "timestamp": "2026-08-31T19:00:00Z"
}
```

### HTTP Status Code Reference:

| HTTP Status | Scenario | Handling Recommendation |
| :--- | :--- | :--- |
| **`200 OK`** | Request was successful and processed. | Parse `data` object/array. |
| **`400 Bad Request`** | Postcode missing, null, or query parameter empty. | Display validation prompt to user. |
| **`404 Not Found`** | Postcode not recognized and fallback evaluation produced no coordinates. | Prompt user to manually enter address lines. |
| **`500 Server Error`** | Internal exception. | Log exception and gracefully bypass postcode enrichment. |

---

## 📑 Next Reading
- 🏛️ [**Architecture & Service Overview**](./SERVICE_OVERVIEW.md)
- 🧪 [**QA & Tester Guide**](./TESTER_GUIDE.md)
- ☁️ [**GCP Deployment Guide**](./GCP_DEPLOYMENT.md)
- 🏠 [**Back to Main README**](../README.md)
