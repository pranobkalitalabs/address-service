# 🚀 Address Service - Google Cloud Platform (GCP) Deployment Guide

Step-by-step documentation for deploying `address-service` to Google Cloud Run, configuring Upstash Redis with TLS, domain mapping on Namecheap, and GitHub Actions CI/CD automation.

---

## 🏗️ 1. Service Specifications

* **Service Name**: `address-service`
* **Region**: `europe-west1 (Belgium)`
* **Container Image**: `pkalita/address-service:latest`
* **Port**: `8082`
* **Allocated Memory**: `512 MiB - 1 GiB`
* **Allocated CPU**: `1 vCPU`
* **Authentication**: `Allow unauthenticated invocations`

---

## 📋 2. Cloud Run Environment Variables

These variables are configured in the Google Cloud Run Service under **Container $\rightarrow$ Environment Variables**:

| Variable Name | Category | Purpose / Description | Format / Example Value |
| :--- | :--- | :--- | :--- |
| `PORT` | System | Container HTTP server port | `8082` |
| `SPRING_PROFILES_ACTIVE` | Runtime | Spring Boot active profile | `prod` (or `docker`) |
| `POSTCODES_API_URL` | Integration | Upstream UK Geocoding API endpoint | `https://api.postcodes.io` |
| `API_TIMEOUT_MS` | Network | Timeout for external postcode requests | `3000` |
| `REDIS_HOST` | Cache | Upstash / Cloud Redis endpoint hostname | `<upstash-redis-hostname>.upstash.io` |
| `REDIS_PORT` | Cache | Redis port | `6379` |
| `REDIS_PASSWORD` | Cache | Redis authentication password | `<upstash-redis-password>` |
| `REDIS_SSL_ENABLED` | Cache | Enables TLS connection for cloud Redis | `true` |
| `CORS_ALLOWED_ORIGINS` | Security | Allowed Web origins for cross-origin requests | `https://pranobkalitalabs.co.uk,https://*.pranobkalitalabs.co.uk` |

---

## 🌐 3. Domain Mapping & Namecheap DNS

### Cloud Run Domain Mapping:
* **Service**: `address-service (europe-west1)`
* **Verified Domain**: `pranobkalitalabs.co.uk`
* **Subdomain**: `address` *(mapping resolves to `address.pranobkalitalabs.co.uk`)*

### Namecheap Host Record:
* **Type**: `CNAME Record`
* **Host**: `address`
* **Value**: `ghs.googlehosted.com.`
* **TTL**: `Automatic`

---

## 🤖 4. Automated CI/CD Deployment (GitHub Actions)

Every push to `main` executes `.github/workflows/docker-ci-cd.yml`:
1. Executes unit & Cucumber 7 BDD test suite.
2. Builds multi-arch container image with Docker Buildx and pushes to Docker Hub.
3. Authenticates with Google Cloud and rolls out a new Cloud Run revision.

### Required GitHub Repository Secrets:
* `DOCKERHUB_USERNAME`: Docker Hub user ID.
* `DOCKERHUB_TOKEN`: Docker Hub Personal Access Token.
* `GCP_SA_KEY`: Google Cloud Service Account JSON key (`Cloud Run Admin` + `Service Account User`).

---

## 🔒 5. Health & Smoke Testing

```bash
# 1. Health Probe
curl -s https://address.pranobkalitalabs.co.uk/actuator/health

# 2. Premise Lookup
curl -s "https://address.pranobkalitalabs.co.uk/api/v1/address/uk/premises/HA9%207ES"

# 3. Swagger UI
open https://address.pranobkalitalabs.co.uk/swagger-ui.html
```
