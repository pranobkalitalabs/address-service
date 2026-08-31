# 🇬🇧 Pranob Kalita Labs - UK Address & Geocoding Service (`address-service`)

**Author & Creator**: Pranob Jyoti Kalita  
**Domain**: [`pranobkalitalabs.co.uk`](https://pranobkalitalabs.co.uk)  
**Production Endpoint**: `https://address.pranobkalitalabs.co.uk`  
**Swagger UI**: `https://address.pranobkalitalabs.co.uk/swagger-ui.html`

A high-performance, resilient, and production-ready Spring Boot 3 microservice for **UK Postcode Validation**, **Geocoding & Administrative Resolution**, **Complete Premise & Flat Address Lookups**, and **Prefix Autocompletion**, backed by **Redis Distributed Caching**.

---

## 📑 Table of Contents
1. [Architecture Overview](#-architecture-overview)
2. [Quick Start (Local Development)](#-quick-start-local-development)
3. [API Endpoints & Specifications](#-api-endpoints--specifications)
4. [QA & Tester Guide](#-qa--tester-guide)
5. [Redis Caching & Performance Verification](#-redis-caching--performance-verification)
6. [GCP Cloud Run Deployment Guide](#-gcp-cloud-run-deployment-guide)
7. [Environment Variables Reference](#-environment-variables-reference)

---

## 🏛️ Architecture Overview

```
                        Client / UI / Tester (Postman)
                                      │
                                      ▼
                        +----------------------------+
                        |      address-service       |
                        |   address.pranobkalitalabs |
                        |        (Port 8082)         |
                        +----------------------------+
                               /              \
                         (Cache Check)    (Cache Miss)
                             /                  \
                            v                    v
                  +-------------------+   +--------------------+
                  |    Redis Cache    |   |    Postcodes.io    |
                  |   (Docker / GCP)  |   |     (Free API)     |
                  +-------------------+   +--------------------+
                                                    │ (Offline / Fallback)
                                                    v
                                          [ Resilient Engine ]
```

### Key Capabilities:
- **Zero Cost Open Data Integration**: Integrates directly with the free UK Open Data geocoding infrastructure (`api.postcodes.io`).
- **Distributed Redis Caching**: Sub-millisecond response times for cached postcodes with automatic 24-hour TTL expiration.
- **Offline / Fault-Tolerant Fallback**: Service automatically falls back to regex validation and structured fallback models if upstream connectivity fails.
- **Premise & Building Level Resolution**: Provides structured list of individual flats, buildings, and street numbers for frontend dropdown selection.
- **Cloud-Native & Container Ready**: Optimized for Google Cloud Run (`PORT` env mapping, health probes, non-root runtime).

---

## 🚀 Quick Start (Local Development)

### Prerequisites
- **Java 21** (JDK)
- **Maven 3.9+**
- **Docker & Docker Compose** (for Redis)

### Option 1: Run with Local Maven + Docker Redis
```bash
# 1. Start Redis container
docker run -d --name platform-redis -p 6379:6379 redis:7-alpine

# 2. Start Address Service
cd address-service
mvn spring-boot:run
```
The service will start on **`http://localhost:8082`**.

### Option 2: Run via Docker Compose (Full Stack)
From the workspace root directory:
```bash
docker compose up -d redis address-service
```

---

## 📖 API Endpoints & Specifications

Interactive OpenAPI / Swagger documentation is available at:
👉 **Local**: `http://localhost:8082/swagger-ui.html`  
👉 **Cloud**: `https://address.pranobkalitalabs.co.uk/swagger-ui.html`

---

### 1. Validate Postcode
Determines whether a UK postcode is active, correctly formatted, and exists.

- **Method**: `GET`
- **Path**: `/api/v1/address/uk/validate/{postcode}`
- **Example Request**: `GET https://address.pranobkalitalabs.co.uk/api/v1/address/uk/validate/SW1A 2AA`

#### Response (`200 OK`):
```json
{
  "success": true,
  "message": "Postcode is valid",
  "data": true,
  "timestamp": "2026-08-31T17:00:00Z"
}
```

---

### 2. Lookup Postcode Geocoding & Region
Retrieves coordinates (latitude, longitude), country, region, administrative district, and parliamentary constituency.

- **Method**: `GET`
- **Path**: `/api/v1/address/uk/lookup/{postcode}`
- **Example Request**: `GET https://address.pranobkalitalabs.co.uk/api/v1/address/uk/lookup/SW1A 2AA`

#### Response (`200 OK`):
```json
{
  "success": true,
  "message": "Postcode details retrieved",
  "data": {
    "valid": true,
    "postcode": "SW1A 2AA",
    "country": "England",
    "region": "London",
    "adminDistrict": "Westminster",
    "parliamentaryConstituency": "Cities of London and Westminster",
    "latitude": 51.503541,
    "longitude": -0.12767
  },
  "timestamp": "2026-08-31T17:00:00Z"
}
```

---

### 3. Lookup Complete Premises (Flats & House Numbers)
Retrieves the complete list of individual flat numbers, apartment complexes, and building addresses for a postcode. **Used by frontend applications to populate address selection dropdowns.**

- **Method**: `GET`
- **Path**: `/api/v1/address/uk/premises/{postcode}`
- **Example Request**: `GET https://address.pranobkalitalabs.co.uk/api/v1/address/uk/premises/HA9 7ES`

#### Response (`200 OK`):
```json
{
  "success": true,
  "message": "Premises retrieved successfully",
  "data": {
    "valid": true,
    "postcode": "HA9 7ES",
    "totalPremises": 8,
    "addresses": [
      {
        "id": "ha9_1",
        "formattedAddress": "Flat 1, Bluebell Apartments, 12 Wembley Park Drive, Wembley, HA9 7ES",
        "buildingName": "Bluebell Apartments",
        "buildingNumber": "Flat 1",
        "addressLine1": "Flat 1, Bluebell Apartments",
        "addressLine2": "12 Wembley Park Drive",
        "city": "Wembley",
        "county": "London",
        "postcode": "HA9 7ES",
        "latitude": 51.5583,
        "longitude": -0.2816
      },
      {
        "id": "ha9_2",
        "formattedAddress": "Flat 2, Bluebell Apartments, 12 Wembley Park Drive, Wembley, HA9 7ES",
        "buildingName": "Bluebell Apartments",
        "buildingNumber": "Flat 2",
        "addressLine1": "Flat 2, Bluebell Apartments",
        "addressLine2": "12 Wembley Park Drive",
        "city": "Wembley",
        "county": "London",
        "postcode": "HA9 7ES",
        "latitude": 51.5583,
        "longitude": -0.2816
      }
    ]
  },
  "timestamp": "2026-08-31T17:00:00Z"
}
```

---

### 4. Autocomplete Postcode Prefix
Returns suggestions for partial UK postcode queries as the user types.

- **Method**: `GET`
- **Path**: `/api/v1/address/uk/autocomplete?query={query}`
- **Example Request**: `GET https://address.pranobkalitalabs.co.uk/api/v1/address/uk/autocomplete?query=SW1A`

---

## 🧪 QA & Tester Guide

### 📋 Pre-Configured Test Postcodes Cheat Sheet

Testers can use these standardized postcodes to test different application behaviors:

| Postcode | Test Scenario / Expected Data | Description |
| :--- | :--- | :--- |
| **`HA9 7ES`** | **Multi-Premise Flats** (*Bluebell Apartments, 12 Wembley Park Drive*) | Tests flat selection dropdowns, apartment numbering, and Wembley district. |
| **`SW1A 2AA`** | **Government & Landmark** (*10 Downing Street, Westminster*) | Tests Central London landmark premises and geocoding. |
| **`NW1 6XE`** | **Marylebone Premises** (*221B Baker Street, London*) | Tests standard Central London premises. |
| **`INVALID123`** | **Negative Validation** | Tests system response when an invalid postcode is entered (returns `data: false`). |
| **`SW1A`** | **Autocomplete Prefix** | Tests postcode query autocompletion dropdown. |

---

### 🤖 Running Automated Tests with Postman & Newman

```bash
# Test Local Instance
npx -y newman run ../postman/address-service.postman_collection.json

# Test Cloud Production Instance (pranobkalitalabs.co.uk)
npx -y newman run ../postman/address-service.postman_collection.json \
  --environment ../postman/pranobkalitalabs-cloud.postman_environment.json
```

---

## ⚡ Redis Caching & Performance Verification

```bash
# View all cached keys
docker exec platform-redis redis-cli keys "*"

# View cached premises JSON payload for HA9 7ES
docker exec platform-redis redis-cli get "postcode-premises::HA9 7ES"
```

---

## ☁️ GCP Cloud Run Deployment Guide

### Step 1: Build & Push Docker Image to GCP Artifact Registry
```bash
export PROJECT_ID="your-gcp-project-id"
export REGION="europe-west2" # London region
export REPO_NAME="pranobkalitalabs"
export IMAGE_NAME="address-service"

gcloud auth configure-docker ${REGION}-docker.pkg.dev

docker build -t ${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO_NAME}/${IMAGE_NAME}:latest .
docker push ${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO_NAME}/${IMAGE_NAME}:latest
```

### Step 2: Deploy to Google Cloud Run with Custom Domain
```bash
gcloud run deploy address-service \
  --image ${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO_NAME}/${IMAGE_NAME}:latest \
  --platform managed \
  --region ${REGION} \
  --allow-unauthenticated \
  --set-env-vars="SPRING_PROFILES_ACTIVE=prod,REDIS_HOST=YOUR_MEMORYSTORE_IP,REDIS_PORT=6379" \
  --min-instances=1 \
  --max-instances=10 \
  --memory=512Mi \
  --cpu=1

# Map Custom Domain in GCP
gcloud beta run domain-mappings create \
  --service address-service \
  --domain address.pranobkalitalabs.co.uk \
  --region ${REGION}
```

---

## ⚙️ Environment Variables Reference

| Variable Name | Default Value | Description |
| :--- | :--- | :--- |
| `PORT` | `8082` | HTTP server listening port (automatically set by GCP Cloud Run). |
| `REDIS_HOST` | `localhost` | Redis server hostname or GCP Memorystore IP. |
| `REDIS_PORT` | `6379` | Redis port. |
| `POSTCODES_API_URL` | `https://api.postcodes.io` | Upstream UK postcode OpenData API. |
| `CORS_ALLOWED_ORIGINS` | `https://pranobkalitalabs.co.uk,https://*.pranobkalitalabs.co.uk` | Allowed origins for web frontend. |
