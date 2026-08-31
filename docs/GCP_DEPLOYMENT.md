# ☁️ Google Cloud Platform (GCP) Deployment Guide

This guide details the exact steps to deploy **`address-service`** to **Google Cloud Run** with **GCP Memorystore (Redis)** and custom domain mapping to **`address.pranobkalitalabs.co.uk`**.

---

## 🏛️ GCP Architecture

```
                    Internet / Client HTTPS Request
                                 │
                                 ▼
                 https://address.pranobkalitalabs.co.uk
                                 │
                                 ▼
                     [ Google Cloud Run ]
                     (Auto-scaling 0 - 10)
                                 │
                 ┌───────────────┴───────────────┐
                 ▼                               ▼
       [ GCP Memorystore ]             [ api.postcodes.io ]
        (Managed Redis)                 (External OpenData)
```

---

## 🛠️ Step-by-Step Deployment

### Prerequisites
1. **Google Cloud SDK (`gcloud`)** installed and authenticated (`gcloud auth login`).
2. A GCP Project with billing enabled.
3. Enabled APIs:
   ```bash
   gcloud services enable run.googleapis.com \
                          artifactregistry.googleapis.com \
                          redis.googleapis.com \
                          vpcaccess.googleapis.com
   ```

---

### Step 1: Create an Artifact Registry Repository
```bash
export PROJECT_ID="your-gcp-project-id"
export REGION="europe-west2" # London
export REPO_NAME="pranobkalitalabs"
export IMAGE_NAME="address-service"

# Create Docker repository in Artifact Registry
gcloud artifacts repositories create ${REPO_NAME} \
  --repository-format=docker \
  --location=${REGION} \
  --description="Pranob Kalita Labs Container Images"
```

---

### Step 2: Build & Push Container Image
```bash
# Configure Docker authentication with GCP
gcloud auth configure-docker ${REGION}-docker.pkg.dev

# Build & Push
docker build -t ${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO_NAME}/${IMAGE_NAME}:latest .
docker push ${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO_NAME}/${IMAGE_NAME}:latest
```

*(Alternatively, you can pull directly from Docker Hub `pkalita/address-service:latest`)*.

---

### Step 3: Set Up GCP Memorystore (Redis) & Serverless VPC Access
Cloud Run communicates with Memorystore Redis through a Serverless VPC Access connector:

```bash
# 1. Create a Serverless VPC Connector
gcloud compute networks vpc-access connectors create redis-vpc-connector \
  --region=${REGION} \
  --range=10.8.0.0/28

# 2. Create Memorystore Redis Instance
gcloud redis instances create address-redis \
  --size=1 \
  --region=${REGION} \
  --tier=basic \
  --redis-version=redis_7_0
```
Note the **Host IP** from the Redis instance output (e.g. `10.0.0.4`).

---

### Step 4: Deploy to Google Cloud Run
```bash
gcloud run deploy address-service \
  --image ${REGION}-docker.pkg.dev/${PROJECT_ID}/${REPO_NAME}/${IMAGE_NAME}:latest \
  --platform managed \
  --region ${REGION} \
  --allow-unauthenticated \
  --vpc-connector redis-vpc-connector \
  --set-env-vars="SPRING_PROFILES_ACTIVE=prod,REDIS_HOST=10.0.0.4,REDIS_PORT=6379,CORS_ALLOWED_ORIGINS=https://pranobkalitalabs.co.uk,https://*.pranobkalitalabs.co.uk" \
  --min-instances=1 \
  --max-instances=10 \
  --memory=512Mi \
  --cpu=1
```

---

### Step 5: Map Custom Domain (`address.pranobkalitalabs.co.uk`)

1. **Create the domain mapping**:
   ```bash
   gcloud beta run domain-mappings create \
     --service address-service \
     --domain address.pranobkalitalabs.co.uk \
     --region ${REGION}
   ```
2. **Update DNS Records**:
   Google Cloud will provide DNS `CNAME` or `A` records (e.g. `ghs.googlehosted.com`).
   Add this `CNAME` record in your domain registrar for `address.pranobkalitalabs.co.uk`.
3. **Automatic SSL**: Google automatically provisions and manages free SSL/TLS certificates for your custom domain.

---

## 🔒 Post-Deployment Health Check

Test the live production deployment:

```bash
# Health probe
curl https://address.pranobkalitalabs.co.uk/actuator/health

# Premise lookup
curl https://address.pranobkalitalabs.co.uk/api/v1/address/uk/premises/HA9%207ES

# Swagger UI
open https://address.pranobkalitalabs.co.uk/swagger-ui.html
```
