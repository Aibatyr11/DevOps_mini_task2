# DevOps Final Project — ToDo App  
**Jenkins · Docker · Kubernetes · Ansible**

## Overview
This repository demonstrates a production-oriented DevOps pipeline for a Spring Boot **ToDo** application:

- CI/CD pipeline implemented with **Jenkins**
- Containerization using **Docker**
- Deployment to **Kubernetes (Minikube)**
- Infrastructure automation with **Ansible (roles + vault)**

---

## Architecture

### High-level Architecture Diagram
```mermaid
flowchart TB
  Dev[Developer] -->|git push| GH[GitHub Repo]
  GH -->|webhook or poll| JENKINS[Jenkins CI/CD]
  JENKINS -->|mvn build and test| ART[Build artifact JAR]
  JENKINS -->|docker build| IMG[Docker Image]
  IMG -->|push| DH[Docker Hub]

  DH -->|pull| K8S[Kubernetes - Minikube]
  K8S --> DEP[Deployment todo-app]
  K8S --> SVC[Service NodePort]
  K8S --> CM[ConfigMap]
  K8S --> SEC[Secret]
  K8S --> HPA[HPA CPU based]
  SVC --> USER[User or REST Client]



## Key Components

- **Jenkins** — builds, tests, and pushes Docker images  
- **Docker Hub** — stores versioned images (`BUILD_NUMBER-SHORT_COMMIT`, `latest`)  
- **Kubernetes (Minikube)** — runs the application using ConfigMap, Secret, probes, and HPA  

---

## Repository Structure



