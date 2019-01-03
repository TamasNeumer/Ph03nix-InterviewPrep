# Intro to Kubernetes

## Introduction

- We would like to have a fault-tolerant and scalable solution, which can be achieved by creating a single controller/management unit, after connecting multiple nodes together. This controller/management unit is generally referred to as a container orchestrator.
- **Containers** are an application-centric way to deliver high-performing, scalable applications on the infrastructure of your choice.
- With a **container image**, we bundle the application along with its runtime and dependencies. We use that image to create an isolated executable environment, also known as container.
- In production we want (to be)
  - fault tolerant
  - scalable on demand
  - update/rollback without downtime
  - use resources optimally
- **Container orchestrators** are the tools which group hosts together to form a cluster, and help us fulfill the requirements mentioned above.
