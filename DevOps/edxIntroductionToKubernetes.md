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
  - Implementations: Docker Swarm, Kubernetes, Apache Mesos, Amazon ECS...
  - Capabilities:
    - Bring multiple hosts together and make them part of a cluster
    - Schedule start/stop containers

## Kubernetes

- Features:

  - Automatic binpacking (=scheduling)
  - Self-healing
  - Horizontal scaling
  - Service discovery and Load balancing -> Kubernetes groups sets of containers and refers to them via a **Domain Name System (DNS)**. This DNS is also called a Kubernetes service. Kubernetes can discover these services automatically, and load-balance requests between containers of a given service.
  - Automated rollouts and rollbacks
  - Secrets and configuration management
  - Storage orchestration
  - Batch execution

- Architecture

  ![Architecture](./res/3.png)

  - One or more **master nodes**
    - **Tasks**:
      - Managing the cluster
      - Entry point for administrative tasks. (Communication via CLI (`kubectl`), GUI, API)
      - If there are more master nodes only one of them is the leader.
    - **Processes**:
      - `kube-apiserver`: Validates and configures data for the api objects which include pods, services, replication-controllers. Resulting state stored in `etcd`!)
      - `kube-scheduler`: Watches newly created pods that have no node assigned, and selects a node for them to run on. (Lot of factors taken into while scheduling such as resource requirements, HW/SW/policy constraints etc.)
      - `kube-controller-manager`: Runs controllers. Logically, each controller is a separate process, but to reduce complexity, they are all compiled into a single binary and run in a single process. Such controllers include:
        - Node Controller: noticing when nodes go down
        - Replication Controller: maintaining correct number of pods
        - Endpoints Controller: Populates endpoints object (joins Services and Pods)
        - Service Account & Token Controllers: Create default accounts and API access tokens for new namespaces
      - `etcd`: Consistent and highly-available key value store used as Kubernetes’ backing store for all cluster data. Always have a backup! - The key-value store can be part of the master node. It can also be configured externally, in which case, the master nodes would connect to it. Works in Raft Consensus Algorithm -> _Failsafe_.
  - One or more **worker nodes**
    - **Tasks**
      - Runs apps using **Pods**.
        - A Pod is a scheduling unit in Kubernetes.
        - It is a logical collection of one or more containers which are _always scheduled together_.
    - **Components**:
      - `Container runtime`
        - To run and manage a container's lifecycle, we need a container runtime on the worker node. Examples: `containerd`, `rkt`, `lxd`.
        - Note: Docker is a platform which uses `containerd` as a container runtime.
      - `kublet`
        - Agent which runs on each worker node and communicates with the master node. Also supervises the healthiness of the pods.
        - The kubelet connects to the container runtime using **Container Runtime Interface (CRI).** This is a plugin interface which enables `kubelet` to use a wide variety of container runtimes (rktnetes, Docker etc.) Hence you can develop your own container runtime.
          - CRI protocol buffer includes two services:
            - `ImageService`: provides RPCs to pull an image from a repository, inspect, and remove an image.
            - `RuntimeService`: contains RPCs to manage the lifecycle of the pods and containers, as well as calls to interact with containers
        - CRI shims:
          - **dockershim**: containers are created using Docker installed on the worker nodes (which uses `containerd`)
          - **cri-containerd**: directly use `containerd`
            ![Shrims](./res/4.png)
      - `kube-proxy`
        - Enables the Kubernetes service abstraction by maintaining network rules on the host and performing connection forwarding.
          -It is a network proxy which runs on each worker node and listens to the API server for each Service endpoint creation/deletion. For each Service endpoint, kube-proxy sets up the routes so that it can reach to it. -> helps to expose your service.

- Cluster Networking
  - The Docker version
    - Creates a virtual bridge (`docker0`) and allocates a subnet from one of the private address blocks for that bridge.
    - For each container that Docker creates, it allocates a virtual Ethernet device (called `veth`) which is attached to the bridge.
    - The `veth` is mapped to appear as `eth0` in the container, using Linux namespaces. The in-container `eth0` interface is given an IP address from the bridge’s address range.
    - The result is that Docker containers **can talk to other containers only if they are on the same machine -> same virtual bridge**. In order for Docker containers to communicate across nodes, there must be allocated ports on the machine’s own IP address, which are then forwarded or proxied to the containers.
      ![Docker Networking](./res/5.png)
    - Docker can start a container and rather than creating a new virtual network interface for it, specify that it shares an existing interface. In this case the drawing above looks a little different:
      ![Docker Networking II](./res/6.png)
      - Both containers are addressable from the outside on 172.17.0.2
      - On the inside each can hit ports opened by the other on localhost
  - The Kubernetes version
    - Kubernetes imposes the following fundamental requirements on any networking implementation:
      - all containers can communicate with all other containers without NAT
      - all nodes can communicate with all containers (and vice-versa) without NAT
      - the IP that a container sees itself as is the same IP that others see it as
    - What this means in practice is that **you can not just take two computers running Docker and expect Kubernetes to work**. You must **ensure that the fundamental requirements** are met.
    - Kubernetes applies **IP addresses at the Pod scope** - containers within a Pod share their network namespaces - including their IP address. This means that containers within a **Pod** can all reach each other’s ports on **localhost**. -> Containers within a Pod **must coordinate port usage**, but this is no different than processes in a VM.
    - Kubernetes does something like the second example of docker networking: it creates a special container for each pod whose only purpose is to provide a network interface for the other containers.
      - `docker ps` and you will see at least one container that was started with the `pause` command
      - Despite this lack of activity the “pause” container is the heart of the pod, providing the virtual network interface that all the other containers will use to communicate with each other and the outside world.
        ![KubiNetwork1](./res/7.png)
    - Let's step out and see how this looks like on a cluster with multiple nodes. One node typically has no idea what private address space was assigned to a bridge on another node, and we need to know that if we’re going to send packets to it and have them arrive at the right place.
      ![KubiNetwork1](./res/8.png)
    - Kubernetes comes to the help! It assigns an overall address space for the bridges on each node, and then assigns the bridges addresses within that space, based on the node the bridge is built on. It also adds routing rules to the gateway at 10.100.0.1 telling it how packets destined for each bridge should be routed, i.e. which node’s eth0 the bridge can be reached through.
      ![KubiNetwork1](./res/9.png)
      - Kubernetes does not use the standard docker bridge device and in fact “cbr” is short for “custom bridge.”

## Installing K8s

- Configurations
  - **All-in-One Single-Node Installation**
    - Master and worker components are installed on a single node. -> Learning, Development, Testing. **Don't** use this in production. Minikube is an example.
  - **Single-Node etcd, Single-Master, and Multi-Worker Installation**
    - Single master node, which also runs a single-node etcd instance. Multiple worker nodes are connected to the master node.
  - **Single-Node etcd, Multi-Master, and Multi-Worker Installation**
    - Multiple master nodes, which work in an HA mode, but we have a single-node etcd instance. Multiple worker nodes are connected to the master nodes.
  - **Multi-Node etcd, Multi-Master, and Multi-Worker Installation**
    - Etcd is configured in a clustered mode, outside the Kubernetes cluster, and the nodes connect to it. The master nodes are all configured in an HA mode, connecting to multiple worker nodes. This is the most advanced and _recommended production setup._
- Installations
  - Localhost: Minikube, Ubuntu on LXD
  - On premise:
    - VMs: Kubernetes can be installed on VMs created via Vagrant, VMware vSphere, KVM, etc.
    - Bare Metal: On top of different operating systems, like RHEL, CoreOS, CentOS, Fedora, Ubuntu, etc.
  - Hosted solutions: GKE, AKS, EKS, etc.
- Tools/Resources
  - kubeadm, KubeSpray, Kops

## Setting Up a Single-Node Cluster with Minikube

- Install `kubectl`. (Binary used to access any Kubernetes cluster).
  - `brew install kubernetes-cli`
- Install `minikube`
  - `brew cask install minikube`
- Start minikube
  - `minikube start`
    - If hangs for more than 3-5 min do `minikube delete` to delete the current cluster.
  - `minikube status`
  - `minikube stop`
- Accessing minikube
  - `kubectl` is the Command Line Interface (CLI) tool to manage the Kubernetes cluster resources and applications.
  - The Kubernetes dashboard provides the Graphical User Interface (GUI) to interact with its resources and containerized applications.
  - API
    - **Core Group (/api/v1)**
      - This group includes objects such as Pods, Services, nodes, etc.
    - **Named Group**
      - This group includes objects in `/apis/$NAME/$VERSION` format.
    - **System-wide**
      - This group consists of system-wide API endpoints, like `/healthz`, `/logs`, `/metrics`, `/ui`, etc.
  - Connecting to a node:
    - To connect to the Kubernetes cluster, kubectl needs the **master node endpoint** and the **credentials** to connect to it.
    - When starting Minikube it creates these be default. These are at `~/.kube/config`. You can also view them via `kubectl config view`.
    - Once the cluster is running get info of it: `kubectl cluster-info` -> "Kubernetes master is running at https://192.168.99.100:8443"
    - You can also use the dashboard (once minikube is running): `minikube dashboard` -> opens a new tab on our web browser. (Wait 1 min...). Also `kubectl proxy` command, `kubectl` would authenticate with the API server on the master node and would make the dashboard available on `http://127.0.0.1:8001/api/v1/namespaces/kube-system/services/kubernetes-dashboard:/proxy/#!/overview?namespace=default`
    - When started with `kubectl proxy` you can curl for the api: `curl http://localhost:8001/`
    - **Without `kubectl proxy` configured**, we can get the **Bearer Token** using `kubectl`, and then send it with the API request. A Bearer Token is an access token which is generated by the authentication server (the API server on the master node) and given back to the client. Using that token, the client can connect back to the Kubernetes API server without providing further authentication details, and then, access resources.
      - To get the token: `TOKEN=$(kubectl describe secret -n kube-system $(kubectl get secrets -n kube-system | grep default | cut -f1 -d ' ') | grep -E '^token' | cut -f2 -d':' | tr -d '\t' | tr -d " ")`
      - `APISERVER=$(kubectl config view | grep https | cut -f 2- -d ":" | tr -d " ")`
      - Check the correct value of APISERVER: `echo $APISERVER`. If there are multiple entries copy the `https://192.168.99.100:8443`.
      - `curl $APISERVER --header "Authorization: Bearer $TOKEN" --insecure`

## Kubernetes Objects

-
