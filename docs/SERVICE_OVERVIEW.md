# 🇬🇧 UK Address & Geocoding Service - Architecture & Service Overview

**Author & Creator**: Pranob Jyoti Kalita  
**Domain**: [`pranobkalitalabs.co.uk`](https://pranobkalitalabs.co.uk)  
**Microservice Name**: `address-service`

---

## 📌 Executive Summary

The **UK Address & Geocoding Service** is a high-performance, containerized Spring Boot 3 microservice designed to handle all aspects of UK postal addresses:
1. **Postcode Validation**: Instant validation of UK postcode format and active status.
2. **Geocoding & Administrative Resolution**: Resolves exact geographic coordinates (Latitude, Longitude), Country, Region, Parliamentary Constituency, and Administrative District.
3. **Premise & Building Level Resolution (Dropdown Feature)**: Returns the complete list of individual flat numbers, apartment complexes (e.g. *Flat 1 Bluebell Apartments*), and building addresses for a postcode to populate frontend selection dropdowns.
4. **Autocomplete Suggestions**: Provides dynamic autocomplete suggestions for partial UK postcode queries.

---

## 🏛️ System Architecture

```
                    Client (Web UI / Mobile / Postman)
                                   │
                                   ▼
                    +-----------------------------+
                    |       address-service       |
                    |    (Spring Boot 3 / JRE 21) |
                    +-----------------------------+
                            /               \
                   [ 1. Check Redis ]  [ 2. Cache Miss ]
                          /                   \
                         v                     v
              +--------------------+   +--------------------+
              |    Redis Cache     |   |    Postcodes.io    |
              | (Sub-ms Retrieval) |   | (Free OpenData API)|
              +--------------------+   +--------------------+
                                                 │ (Offline / Fallback)
                                                 v
                                       [ Fallback Engine ]
```

---

## ⚡ Caching Strategy (Redis)

- **Storage Format**: Portable JSON serialized with `GenericJackson2JsonRedisSerializer`.
- **Default TTL**: 24 Hours (`postcode-lookup` and `postcode-premises`), 48 Hours (`postcode-validation`).
- **Cache Keys**:
  - `postcode-validation::{postcode}`
  - `postcode-lookup::{postcode}`
  - `postcode-premises::{postcode}`
- **Performance**:
  - **Cache Miss (First Query)**: $\sim 30\text{ms} - 60\text{ms}$ (fetches upstream & writes to Redis).
  - **Cache Hit (Subsequent Queries)**: **$< 1\text{ms}$** (served directly from Redis).

---

## 🛡️ Resilience & Fault Tolerance

The service includes an intelligent fallback mechanism:
- If upstream network connectivity fails or an API outage occurs, `address-service` automatically falls back to offline UK regex format validators and structured geographic models.
- Core operations (such as user registration or address autocomplete) will never crash or block upstream caller services.

---

## 📑 Next Reading
- 🧪 [**QA & Tester Guide**](./TESTER_GUIDE.md) - How to test locally, Postman guide, and test postcodes cheat sheet.
- ☁️ [**GCP Deployment Guide**](./GCP_DEPLOYMENT.md) - Deploying to Google Cloud Run and GCP Memorystore.
- 🏠 [**Back to Main README**](../README.md)
