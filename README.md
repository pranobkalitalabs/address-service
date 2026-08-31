# 🇬🇧 UK Address & Geocoding Service (`address-service`)

[![Docker Pulls](https://img.shields.io/docker/pulls/pkalita/address-service?logo=docker&style=flat-square)](https://hub.docker.com/r/pkalita/address-service)
[![Docker Image Version](https://img.shields.io/docker/v/pkalita/address-service/latest?logo=docker&style=flat-square)](https://hub.docker.com/r/pkalita/address-service)
[![CI/CD Pipeline](https://img.shields.io/github/actions/workflow/status/pranobkalitalabs/address-service/docker-ci-cd.yml?branch=main&label=CI%2FCD&logo=github&style=flat-square)](https://github.com/pranobkalitalabs/address-service/actions)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0)

A high-performance, containerized Spring Boot 3 microservice for **UK Postcode Validation**, **Geocoding Coordinates**, **Building & Flat Resolution (Dropdown feature)**, and **Postcode Autocomplete**, backed by **Redis Distributed Caching**.

---

## 🌐 Live API & Swagger Documentation

| Environment | Base URL | Interactive Swagger / OpenAPI UI | Health Status |
| :--- | :--- | :--- | :--- |
| **Local Dev** | `http://localhost:8082` | [Local Swagger UI](http://localhost:8082/swagger-ui.html) | [Local Health](http://localhost:8082/actuator/health) |
| **Cloud Production** | `https://address.pranobkalitalabs.co.uk` | [Production Swagger UI](https://address.pranobkalitalabs.co.uk/swagger-ui.html) | [Production Health](https://address.pranobkalitalabs.co.uk/actuator/health) |

---

## 📚 In-Depth Documentation

For detailed technical guides, please refer to the dedicated documents in the [`docs/`](./docs) folder:

| Document | Description |
| :--- | :--- |
| 🏛️ [**Architecture & Service Overview**](./docs/SERVICE_OVERVIEW.md) | High-level system architecture, Redis caching strategy, open data integration, and fault tolerance fallback engine. |
| 🧪 [**QA & Tester Guide**](./docs/TESTER_GUIDE.md) | Local startup instructions, `.env` variables reference table, standardized test postcodes cheat sheet (`HA9 7ES`, `SW1A 2AA`), and Redis inspection commands. |
| 🔌 [**Microservice & Frontend Integration Guide**](./docs/INTEGRATION_GUIDE.md) | Production integration guides for Spring Boot microservices (`RestClient`, DTOs), React frontend dropdown components, and HTTP status codes dictionary. |
| 📮 [**Postman Collection & Environments**](./docs/postman/address-service.postman_collection.json) | Ready-to-import Postman test collection with 8 automated requests and 16 assertions. |
| ☁️ [**GCP Deployment Guide**](./docs/GCP_DEPLOYMENT.md) | Step-by-step production deployment to Google Cloud Run, GCP Memorystore (Redis), and custom domain mapping for `address.pranobkalitalabs.co.uk`. |

---

## ⚡ Quick Start (1-Liner)

Run the full stack (Address Service + Redis) directly using Docker:

```bash
# 1. Start Redis
docker run -d --name platform-redis -p 6379:6379 redis:7-alpine

# 2. Run Address Service (from Docker Hub)
docker run -d --name address-service -p 8082:8082 -e REDIS_HOST=localhost pkalita/address-service:latest
```

Test that the service is running:
```bash
curl http://localhost:8082/api/v1/address/uk/premises/HA9%207ES
```

---

## 👨‍💻 Author & Maintainer
- **Creator**: Pranob Jyoti Kalita
- **Organization**: [Pranob Kalita Labs](https://pranobkalitalabs.co.uk)
- **Docker Hub**: [`pkalita/address-service`](https://hub.docker.com/r/pkalita/address-service)
