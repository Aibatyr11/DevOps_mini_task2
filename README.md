# DevOps Final Project — ToDo App (Jenkins + Docker + Kubernetes + Ansible)

## Overview
This repository contains a production-oriented DevOps pipeline for a Spring Boot ToDo application:
- CI/CD with Jenkins (build → test → docker build/push → optional deploy)
- Containerization with Docker
- Kubernetes deployment on Minikube (Deployment, Service, ConfigMap, Secret, HPA)
- Infrastructure automation with Ansible (roles + vault)

---

## Architecture

### High-level diagram (Mermaid)
```mermaid
flowchart TB
  Dev[Developer] -->|git push| GH[GitHub Repo]
  GH -->|webhook/poll| JENKINS[Jenkins CI/CD]
  JENKINS -->|mvn build/test| ART[Build Artifacts (JAR)]
  JENKINS -->|docker build| IMG[Docker Image]
  IMG -->|push| DH[Docker Hub Registry]

  DH -->|pull image| K8S[Kubernetes (Minikube)]
  K8S --> DEP[Deployment: todo-app]
  K8S --> SVC[Service: NodePort]
  K8S --> CM[ConfigMap]
  K8S --> SEC[Secret]
  K8S --> HPA[Horizontal Pod Autoscaler]
  SVC --> USER[User / REST Client]

Components

Jenkins runs pipeline stages and pushes Docker image to Docker Hub

Docker Hub stores versioned images (BUILD_NUMBER-SHORT_COMMIT + latest)

Minikube Kubernetes runs the application with:

ConfigMap for non-sensitive config

Secret for sensitive config

Liveness/Readiness probes

HPA based on CPU usage

Repository Structure (important parts)

Jenkinsfile — CI/CD pipeline

Dockerfile_BakhitbekovAibatyr — multi-stage Docker build

k8s/ — Kubernetes manifests (deployment.yaml, service.yaml, configmap.yaml, secret.yaml, hpa.yaml)

k8s/ansible/ — Ansible automation (playbooks/, roles/, group_vars/)

Step-by-step Setup (Linux VM)
Prerequisites

Ubuntu 24.04 VM

Docker installed and running

Jenkins installed as a system service

Minikube installed (docker driver)

kubectl installed and configured for minikube

Ansible installed (with kubernetes.core collection)

1) Clone repository

git clone https://github.com/Aibatyr11/DevOps_mini_task2
cd DevOps_mini_task2

2) Jenkins (CI/CD)

Create a Pipeline job in Jenkins and link it to this repository.

Add credentials:

GitHub token (if needed for private repo)

Docker Hub credentials (ID: dockerhub-creds)

Run Build Now and ensure pipeline succeeds.

3) Kubernetes (Minikube)

Start cluster:
minikube start --driver=docker
kubectl config use-context minikube
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
kubectl apply -f k8s/hpa.yaml

Check status:

kubectl get pods -o wide
kubectl get svc
kubectl get hpa


Get service URL:

minikube service todo-app-service --url

4) Ansible (Automation)

Run playbook (local execution):

cd k8s/ansible
ansible-playbook -i inventory/inventory.ini playbooks/site.yml --ask-vault-pass --ask-become-pass

CI/CD Pipeline Flow (Jenkinsfile)

Pipeline stages:

Checkout — pulls source code and creates dynamic tag: BUILD_NUMBER-SHORT_COMMIT

Build (Maven) — mvn clean package -DskipTests (artifact archived)

Test (Maven) — mvn test (pipeline fails if tests fail)

Docker Build — builds image with two tags:

${IMAGE_TAG} (dynamic)

latest

Docker Login + Push — pushes both tags to Docker Hub (pipeline fails if push fails)

Deploy (optional) — runs only on main branch if compose file exists

Verification Evidence (Screenshots / Outputs)
Jenkins pipeline success

Console Output showing successful stages and image pushed.

Kubernetes running workload
kubectl get pods -o wide
kubectl get svc
kubectl get hpa

REST endpoint output

After getting the NodePort URL:

curl -i http://<MINIKUBE_IP>:30081/



