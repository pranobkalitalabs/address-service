# 🧪 QA & Tester Guide - Local Testing, Environment Variables & Postman

This guide is designed for **QA Engineers, Testers, and Developers** to easily spin up, configure, and thoroughly test the **UK Address & Geocoding Service**.

---

## 🚀 1. How to Run Locally

### Option A: Standalone Docker Compose (1-Click Full Local Stack)
If you are working on `address-service` independently:
```bash
cd address-service
docker compose up -d
```
This automatically boots:
- `address-service` (Port **`8082`**)
- `address-redis` (Port **`6379`**)

### Option B: Run via Local Maven
```bash
# Start Redis container
docker run -d --name address-redis -p 6379:6379 redis:7-alpine

# Start Spring Boot application
mvn spring-boot:run
```
The service will be live at: **`http://localhost:8082`**.

---

## 🥒 2. Behavior-Driven Development (BDD) Cucumber Testing

We use **Cucumber 7**, **Gherkin (.feature files)**, and **REST Assured** for living test specifications:

```bash
# Run only the BDD Cucumber Suite:
mvn test -Dtest=AddressCucumberTest

# Run all Unit + Integration + BDD Tests:
mvn clean test
```

### 📊 Living HTML Reports:
Every test run automatically generates an interactive, styled HTML test report at:  
👉 **`address-service/target/cucumber-reports/cucumber.html`** *(open in any web browser)*.

---

## ⚙️ 2. Environment Variables & Secret Configuration

Configuration is managed via environment variables. When running locally, you can create a private `.env` file (copied from `.env.example`).

| Variable Name | Default Value | Purpose |
| :--- | :--- | :--- |
| `PORT` | `8082` | HTTP server listening port. |
| `REDIS_HOST` | `localhost` | Redis server hostname (or `platform-redis` in Docker network). |
| `REDIS_PORT` | `6379` | Redis port. |
| `POSTCODES_API_URL` | `https://api.postcodes.io` | Upstream OpenData API URL. |
| `API_TIMEOUT_MS` | `3000` | HTTP request timeout in milliseconds. |
| `CORS_ALLOWED_ORIGINS` | `*` (Localhost + `pranobkalitalabs.co.uk`) | Allowed CORS origins for web clients. |
| `SPRING_PROFILES_ACTIVE` | `dev` | Active Spring profile (`dev`, `prod`, `docker`). |

> 🔒 **Security Rule**: The `.env` file is in `.gitignore` and is **never pushed to GitHub**. In CI/CD, credentials are read from GitHub Actions Secrets.

---

## 📋 3. Standardized Test Postcodes Cheat Sheet

Use these verified test postcodes to test specific features and edge cases:

| Postcode | Test Scenario / Expected Data | Verification Points |
| :--- | :--- | :--- |
| **`HA9 7ES`** | **Multi-Premise Flats** (*Bluebell Apartments, Wembley*) | • `GET /premises/HA9 7ES`<br>• Returns **8 distinct apartments** (*Flat 1 to Flat 5*, *14 to 18 Wembley Park Drive*)<br>• District: *Brent / Wembley* |
| **`SW1A 2AA`** | **Landmark & Central London** (*10 Downing Street*) | • `GET /lookup/SW1A 2AA` & `/premises/SW1A 2AA`<br>• Latitude: `51.503541`, Longitude: `-0.12767`<br>• District: *Westminster* |
| **`NW1 6XE`** | **Marylebone Premises** (*221B Baker Street*) | • `GET /premises/NW1 6XE`<br>• Returns *221B, 221A, 223 Baker Street* |
| **`INVALID123`** | **Negative Validation** | • `GET /validate/INVALID123`<br>• Returns `success: true, data: false` |
| **`SW1A`** | **Autocomplete Prefix Search** | • `GET /autocomplete?query=SW1A`<br>• Returns array of suggestions (`SW1A 1AA`, `SW1A 2AA`, etc.) |

---

## 📖 4. API Endpoints Reference & Examples

Interactive Swagger UI is available at:  
👉 **`http://localhost:8082/swagger-ui.html`**

> 🌟 **Tester Flexibility**: Every endpoint accepts the postcode either as a **Query Parameter** (e.g. `?postcode=SW1A 2AA` - ideal for Postman Params tab) or as a **Path Variable** (e.g. `/SW1A 2AA`).

---

### Endpoint 1: Validate Postcode
- **Query Param**: `GET /api/v1/address/uk/validate?postcode=SW1A 2AA`
- **Path Variable**: `GET /api/v1/address/uk/validate/SW1A 2AA`

```json
{
  "success": true,
  "message": "Postcode is valid",
  "data": true,
  "timestamp": "2026-08-31T17:00:00Z"
}
```

---

### Endpoint 2: Postcode Geocoding & Region Lookup
- **Query Param**: `GET /api/v1/address/uk/lookup?postcode=SW1A 2AA`
- **Path Variable**: `GET /api/v1/address/uk/lookup/SW1A 2AA`

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

### Endpoint 3: Full Premise / Apartment Lookup (Dropdown Feature)
- **Query Param**: `GET /api/v1/address/uk/premises?postcode=HA9 7ES`
- **Path Variable**: `GET /api/v1/address/uk/premises/HA9 7ES`

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
      }
    ]
  },
  "timestamp": "2026-08-31T17:00:00Z"
}
```

---

### Endpoint 4: Autocomplete Suggestions
- **Method**: `GET /api/v1/address/uk/autocomplete?query={query}`
- **Example**: `GET http://localhost:8082/api/v1/address/uk/autocomplete?query=SW1A`

```json
{
  "success": true,
  "message": "Autocomplete results",
  "data": ["SW1A 0AA", "SW1A 1AA", "SW1A 2AA"],
  "timestamp": "2026-08-31T17:00:00Z"
}
```

---

### Endpoint 5: Health & Monitoring
- `GET http://localhost:8082/actuator/health`
- `GET http://localhost:8082/v3/api-docs`

---

## 📮 5. Testing with Postman & Newman

Downloadable Postman test collections and environments are provided directly inside this repository:

| File | Purpose | Direct Link |
| :--- | :--- | :--- |
| **Postman Collection** | Complete automated test suite (8 requests, 16 assertions) | [📥 `address-service.postman_collection.json`](./postman/address-service.postman_collection.json) |
| **Local Environment** | Targets `http://localhost:8082` | [📥 `local-dev.postman_environment.json`](./postman/local-dev.postman_environment.json) |
| **Cloud Environment** | Targets `https://address.pranobkalitalabs.co.uk` | [📥 `pranobkalitalabs-cloud.postman_environment.json`](./postman/pranobkalitalabs-cloud.postman_environment.json) |

### How to Import into Postman App:
1. Open Postman $\rightarrow$ Click **Import** (top left).
2. Drag and drop the downloaded collection and environment files.
3. Select the imported environment (`Address Service - Local Dev`) in the top-right dropdown.
4. Click **Send** on any request or run the collection runner!

### Run via Newman CLI from Terminal:
```bash
# Run against Local Dev Server
npx -y newman run ./docs/postman/address-service.postman_collection.json \
  --environment ./docs/postman/local-dev.postman_environment.json

# Run against Cloud Production Server (pranobkalitalabs.co.uk)
npx -y newman run ./docs/postman/address-service.postman_collection.json \
  --environment ./docs/postman/pranobkalitalabs-cloud.postman_environment.json
```
**Expected Result**: `8 requests executed, 16 assertions passed (100% Passed)`.

---

## 🔍 6. Redis Cache Inspection for Testers

You can inspect the live cache state in Redis using `redis-cli`:

```bash
# View all cached keys
docker exec platform-redis redis-cli keys "*"

# View cached premise details for HA9 7ES
docker exec platform-redis redis-cli get "postcode-premises::HA9 7ES"

# Check remaining TTL in seconds
docker exec platform-redis redis-cli ttl "postcode-premises::HA9 7ES"
```
