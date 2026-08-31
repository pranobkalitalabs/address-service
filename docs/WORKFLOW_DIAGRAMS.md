# 📊 UK Address Service - Visual Architecture & Workflow Diagrams

This document contains visual diagrams for testers and developers to understand the internal request flows, caching lifecycle, and CI/CD packaging within **`address-service`**.

---

## 1. Postcode Lookup & Redis Cache Lifecycle

This sequence diagram illustrates how `address-service` handles incoming requests, checks Redis for sub-millisecond responses, and falls back to upstream OpenData or regex models:

```mermaid
sequenceDiagram
    autonumber
    actor Client as Tester / Client App
    participant API as address-service (Port 8082)
    participant Redis as Redis Cache (Port 6379)
    participant Upstream as Postcodes.io (OpenData API)

    Client->>API: GET /api/v1/address/uk/lookup?postcode=SW1A 2AA
    API->>Redis: Check Cache Key "postcode-lookup::SW1A 2AA"
    
    alt Cache Hit (Data Exists in Redis)
        Redis-->>API: Return Cached JSON (< 1ms)
        API-->>Client: 200 OK (Enriched Address Payload)
    else Cache Miss (First Query)
        Redis-->>API: Key Not Found (Null)
        API->>Upstream: HTTP GET /postcodes/SW1A 2AA
        
        alt Upstream Success
            Upstream-->>API: 200 OK (Raw Postcode Metadata)
            API->>Redis: Save to Cache (TTL: 24 Hours)
            API-->>Client: 200 OK (Enriched Address Payload)
        else Upstream Outage / Fallback
            Upstream-->>API: 5xx Error / Timeout
            API->>API: Execute Internal Fallback Engine (Regex & District Models)
            API-->>Client: 200 OK (Fallback Coordinates & District)
        end
    end
```

---

## 2. Multi-Premise & Flat Resolution Flow (Dropdown Feature)

This flowchart details how building numbers, flat apartments (*e.g., Flat 1 to Flat 8 Bluebell Apartments, HA9 7ES*), and formatted street addresses are resolved for UI dropdowns:

```mermaid
flowchart TD
    A[Client Request: GET /api/v1/address/uk/premises?postcode=HA9 7ES] --> B{Postcode in Redis?}
    
    B -- Yes (Cache Hit) --> C[Retrieve Premise List from Redis]
    B -- No (Cache Miss) --> D[Validate Postcode Format]
    
    D --> E{Known Multi-Premise Dataset?}
    E -- Yes --> F[Load Structured Flat & Apartment Units]
    E -- No --> G[Generate Standard District Street Premise Numbers]
    
    F --> H[Attach Geocoding Lat/Lng Coordinates]
    G --> H
    
    H --> I[Store Result in Redis: postcode-premises::HA9 7ES]
    I --> J[Return 200 OK with totalPremises & Array of Addresses]
    C --> J
```

---

## 3. Developer & CI/CD Lifecycle (Maven Jib + GitHub Actions)

This diagram shows how local Maven builds stay private on developer laptops, while official production images are built and published by GitHub Actions:

```mermaid
flowchart LR
    subgraph Local [💻 Local Developer Machine]
        A1[Code Changes] --> A2[mvn test]
        A2 --> A3[mvn compile jib:dockerBuild]
        A3 --> A4[(Local Docker Daemon)]
    end

    subgraph Git [🐙 GitHub Repository]
        B1[git push origin main]
    end

    subgraph Cloud [☁️ GitHub Actions CI/CD]
        C1[Spin up Redis Service] --> C2[Execute mvn clean test]
        C2 --> C3[Authenticate via GitHub Secrets]
        C3 --> C4[Publish Official Image to Docker Hub]
    end

    subgraph Registry [🐳 Docker Hub]
        D1[pkalita/address-service:latest]
        D2[pkalita/address-service:1.0.0]
    end

    Local -->|Push Tested Code| Git
    Git -->|Trigger Workflow| Cloud
    Cloud -->|Deploy Official Container| Registry
```

---

## 📑 Next Reading
- 🏛️ [**Architecture & Service Overview**](./SERVICE_OVERVIEW.md)
- 🧪 [**QA & Tester Guide**](./TESTER_GUIDE.md)
- 🔌 [**Microservice & Frontend Integration Guide**](./INTEGRATION_GUIDE.md)
- ☁️ [**GCP Deployment Guide**](./GCP_DEPLOYMENT.md)
- 🏠 [**Back to Main README**](../README.md)
