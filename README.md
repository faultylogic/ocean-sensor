# ocean-sensor
An oceanic sensor buoy network simulator instrumented with **OpenTelemetry**, deployable on **Kubernetes with Kind**, with metrics flowing through **BindPlane → Dynatrace**.

This repository is ideal for:
- Local development and Dynatrace metric instrumentation testing
- Simulating a distributed network of oceanic sensor buoys around Nova Scotia and the Canadian eastern coastline
- Generating realistic oceanographic, atmospheric, tidal, wave, and current OTLP metrics for Dynatrace dashboards
- Validating observability pipelines end-to-end on Kind

---

## 📚 Table of Contents
- Overview
- Architecture
- Prerequisites
- Installation
- Cluster Setup
- Dynatrace Setup
- BindPlane Install
- BindPlane Pipeline Config
- Simulation App
- Verification
- Cleanup

---

## 🧭 Overview
This project provisions a **Kind-based Kubernetes cluster** configured with:

- **NGINX Ingress Controller**  
- **PersistentVolume mount point**  
- **Dynatrace Operator**  
- **Dynakube configuration** for OneAgent and ActiveGate  

It provides a fast, local environment to validate Dynatrace monitoring on Kubernetes workloads.

---

## 🧱 Architecture

```
+---------------------------+
|        Your Host          |
|  Docker Engine + Brew     |
+------------+--------------+
             |
             v
+---------------------------+
|        Kind Cluster       |
|  - Control Plane Node     |
|  - PV Mount (/mnt/root)   |
+------------+--------------+
             |
             v
+---------------------------+
|   NGINX Ingress Controller|
+---------------------------+
             |
             v
+---------------------------+
|    ocean-sensor Pod       |
|  Spring Boot 3 / Java 21  |
|  STOMP WebSocket          |
|  Micrometer OTLP          |
+---------------------------+
             |
          OTLP HTTP
          (port 4317)
             |
             v
+---------------------------+
|     BindPlane Agent       |
|  192.168.1.190:4317       |
|  OpenTelemetry Pipeline   |
+---------------------------+
             |
             v
+---------------------------+
|        Dynatrace          |
|  Metrics + Dashboards     |
+---------------------------+
             |
             v (OneAgent)
+---------------------------+
|    Dynatrace Operator     |
|    Dynakube Deployment    |
+---------------------------+
```

---

## 🚀 Prerequisites

### Ubuntu 

This deployment was done leveraging Ubuntu server 24.04.3 LTS.  It was done both on the full server installation as well as the minimum server instation (recommended)


### 🐳 Docker
```bash
sudo apt-get update
sudo apt install -y docker.io
sudo usermod -aG docker $USER && newgrp docker
```

### 🍺 Homebrew (Linuxbrew)

I'm using brew to avoid having to curl a bunch of files for each tools and there are known issues with go and `apt-get` and there are issues using `snap` with kind so brew avoids a lot of these issues.

```bash
sudo apt install -y build-essential procps curl file git
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

echo 'eval "$(/home/linuxbrew/.linuxbrew/bin/brew shellenv)"' >> ~/.bashrc
eval "$(/home/linuxbrew/.linuxbrew/bin/brew shellenv)"
```

### 📦 Required Tools
```bash
brew install go
brew install kind
brew install helm
brew install cloud-provider-kind
brew install kubectl
```

---

## 📁 Prepare PersistentVolume Mount
```bash
sudo mkdir /mnt/root
```

---

## ☸️ Create the Kind Cluster
```bash
kind create cluster --name kindk8 --config kind-cluster.yaml
```

---

## 🌐 Install NGINX Ingress
```bash
kubectl apply \
  -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml
```

Apply local config after pods initialize:

```bash
kubectl apply -f nginx.yaml
```

Possible error: 

```bash
kubectl apply -f nginx.yaml
deployment.apps/nginx-deployment unchanged
service/nginx unchanged
Error from server (InternalError): error when creating "nginx.yaml": Internal error occurred: failed calling webhook "validate.nginx.ingress.kubernetes.io": failed to call webhook: Post "https://ingress-nginx-controller-admission.ingress-nginx.svc:443/networking/v1/ingresses?timeout=10s": dial tcp 10.96.125.170:443: connect: connection refused
```
This occures because the ngix pods are not fully deployed yet wait until the pods in the `ingress-nginx` namespace is fully deployed

---

## 🧭 Install Dynatrace Operator

The Dynakube file can be found in your dynatrace environment (\<dynatrace-environment\>/ui/apps/dynatrace.kubernetes/onboarding)
```bash
helm install dynatrace-operator oci://public.ecr.aws/dynatrace/dynatrace-operator \
  --create-namespace \
  --namespace dynatrace \
  --atomic
```

Apply your Dynakube configuration:

```bash
kubectl apply -f dynakube.yaml
```

---

## 📡 Install BindPlane

BindPlane receives OTLP metrics from the simulator and forwards them to Dynatrace. Install it on any host that is reachable from the Kind cluster — typically the same machine running Kind.

### Install via Linux package

```bash
curl -fsSlL https://github.com/observIQ/bindplane-op-release/releases/latest/download/install-linux.sh | bash
```

Enable and start the service:

```bash
sudo systemctl enable bindplane-op
sudo systemctl start bindplane-op
```

Check it is running:

```bash
sudo systemctl status bindplane-op
```

### Service management

```bash
sudo systemctl start   bindplane-op
sudo systemctl stop    bindplane-op
sudo systemctl restart bindplane-op
sudo systemctl status  bindplane-op
```

Logs:

```bash
sudo journalctl -u bindplane-op -f
```

### Access the UI

Open `http://<host-ip>:3001` — default credentials are `admin` / `admin`.

> Change the default password immediately after first login.

### Configure the Dynatrace pipeline

1. Go to **Destinations** → **Add Destination** → select **Dynatrace**
2. Set the OTLP endpoint:
   ```
   https://<your-environment-id>.live.dynatrace.com/api/v2/otlp
   ```
3. Set your Dynatrace API token — requires the following scopes:
   - `metrics.ingest`
   - `logs.ingest`
4. Go to **Sources** → **Add Source** → select **OTLP**
5. Go to **Pipelines** → create a new pipeline linking the OTLP source to the Dynatrace destination
6. **Deploy** the pipeline

### Verify BindPlane is receiving data

After the simulator pod starts, open the BindPlane UI → **Pipelines** → your pipeline → **Throughput**. You should see metrics flowing within 5–10 seconds.

---

## 🔗 Configure BindPlane

BindPlane acts as the OpenTelemetry pipeline between the simulator and Dynatrace. It receives OTLP metrics from the app on HTTP port `4317` and forwards them to your Dynatrace environment.

### Update `k8s/configmap.yaml`
Before deploying, set your BindPlane agent host and ports:

```yaml
data:
  BINDPLANE_HOST: "192.168.1.190"   # IP or hostname of your BindPlane agent
  BINDPLANE_HTTP_PORT: "4317"        # OTLP HTTP receiver port (metrics)
  BINDPLANE_GRPC_PORT: "4318"        # OTLP gRPC receiver port
  BINDPLANE_SERVICE_NAME: "ocean-sensor"
  SIMULATOR_BUOY_COUNT: "8"
```

> **Note:** BindPlane uses inverted port conventions from standard OpenTelemetry — HTTP is on `4317` and gRPC on `4318`.

### Verify connectivity
Once the pod is running, check the startup logs to confirm BindPlane is reachable:
```bash
kubectl logs deployment/ocean-sensor | grep BindPlane
```

Expected output:
```
BindPlane metrics export configured -> http://192.168.1.190:4317/v1/metrics (HTTP)
Publishing metrics for OtlpMeterRegistry every 3s ...
```

---

## 🌊 Install The Ocean Sensor

### Add DNS record in Pi-hole (or /etc/hosts)
Add a local DNS record pointing to your host machine IP:
```
ocean-sensor.kind.local → 192.168.1.97
```

Or add to `/etc/hosts` on any client machine:
```bash
echo "192.168.1.97  ocean-sensor.kind.local" | sudo tee -a /etc/hosts
```

### Deploy to Kubernetes
```bash
kubectl apply -f k8s/
```

### Configure buoy count and BindPlane (before first deploy)
Edit `k8s/configmap.yaml` to set your BindPlane address and desired buoy count:
```yaml
data:
  BINDPLANE_HOST: "192.168.1.190"   # your BindPlane agent IP
  BINDPLANE_HTTP_PORT: "4317"
  BINDPLANE_GRPC_PORT: "4318"
  BINDPLANE_SERVICE_NAME: "ocean-sensor"
  SIMULATOR_BUOY_COUNT: "8"
```

### Access the live map
```
http://ocean-sensor.kind.local/ocean-overview/mapview.html
```

### Fleet Control API
Resize the buoy fleet at runtime via the REST API:
```bash
curl -X PUT http://ocean-sensor.kind.local/ocean-overview/api/fleet/config \
  -H "Content-Type: application/json" \
  -d '{"buoyCount": 20}'
```

### Run locally (without Kubernetes)
```bash
BINDPLANE_HTTP_ENDPOINT=http://192.168.1.190:4317 \
SIMULATOR_BUOY_COUNT=8 \
./mvnw spring-boot:run
```

Then open: `http://localhost:8081/ocean-overview/mapview.html`

---

## 🔍 Verification

### Check pods
```bash
kubectl get pods -A
```

You should see:
- `dynatrace-operator` running  
- `dynakube` components (ActiveGate, OneAgent)  
- `ingress-nginx` pods
- `ocean-sensor` pods

### Test ingress
test the ingress of the application deployment `http://ocean-sensor.kind.local/ocean-overview/mapview.html` and you should see the application load

---

## 🧹 Cleanup
```bash
kind delete cluster --name kindk8
sudo rm -rf /mnt/root
```

---

## 🧰 Optional: Helpful Alias
```bash
# alias k="kubectl"
```
