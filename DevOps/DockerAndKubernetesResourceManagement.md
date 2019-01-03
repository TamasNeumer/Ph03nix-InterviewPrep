# Docker and Kubernetes Resource Management

## Docker resource management

- By default, a **container has no resource constraints** and can use as much of a given resource as the host’s kernel scheduler allows. Limit this by runtime configuration flags of the `docker run` command.

### Memory on Docker

- Out of Memory: On Linux hosts, if the kernel detects that there is not enough memory to perform important system functions, it throws an `OOME`, or `Out Of Memory Exception`, and starts killing processes to free up memory.
- `-m` or `--memory=`: The maximum amount of memory the container can use in Megabyte. -> minimum is `4m`.
- `--memory-swap`: Amount of memory the container is allowed to swap to disk.
  - `--memory-swap` represents the **total amount of memory and swap** that can be used, and `--memory`controls the amount used by non-swap memory. So if `--memory="300m"` and `--memory-swap="1g"`, the container can use 300m of memory and 700m (1g - 300m swap.
  - If `--memory-swap` is set to the same value as `--memory`, and `--memory` is set to a positive integer, the container does not have right to access the swap.
  - If `--memory-swap` is unset, and `--memory` is set, the container can use twice as much swap as the `--memory` setting, if the host container has swap memory configured. For instance, if `--memory="300m"` and `--memory-swap` is not set, the container can use 300m of memory and 600m of swap.
- `--memory-reservation` Allows you to specify a soft limit smaller than `--memory` which is activated when Docker detects contention or low memory on the host machine. If you use it must be lower than `--memory`.
- `--kernel-memory`
  - **Unlimited memory, unlimited kernel memory**: This is the default behavior.
  - **Unlimited memory, limited kernel memory**: This is appropriate when the amount of memory needed by all cgroups is greater than the amount of memory that actually exists on the host machine. You can configure the kernel memory to never go over what is available on the host machine, and containers which need more memory need to wait for it.
  - **Limited memory, unlimited kernel memory**: The overall memory is limited, but the kernel memory is not.
  - **Limited memory, limited kernel memory**: Limiting both user and kernel memory can be useful for debugging memory-related problems. If a container is using an unexpected amount of either type of memory, it runs out of memory without affecting other containers or the host machine.

### CPU on Docker

- By default, each container’s access to the host machine’s CPU cycles is unlimited. You can set various constraints to limit a given container’s access to the host machine’s CPU cycles. Most users use and configure the default CFS scheduler.
- `--cpus=<value>`: Specify how much of the available CPU resources a container can use. For instance, if the host machine has two CPUs and you set `--cpus="1.5"`, the container is guaranteed at most one and a half of the CPUs.
  - e.g.: `docker run -it --cpus=".5" ubuntu /bin/bash`
- `--cpuset-cpus`: Limit the specific CPUs or cores a container can use. A comma-separated list or hyphen-separated range of CPUs a container can use, if you have more than one CPU. The first CPU is numbered `0`. A valid value might be `0-3` (to use the first, second, third, and fourth CPU) or `1,3` (to use the second and fourth CPU).

## Kubernetes resource management

- Each container has `requests` and `limits`. A pod can contain multiple container definitions. If so, the defined requests and limits (for each container) add up to define the total request and total limits for the given pod.

### CPU on Kubernetes

- Defined in milicores
  - If your container only needs ¼ of a core, you would put a value of “250m”.
- If you put in a value larger than the core count of your biggest node, your pod will never be scheduled.
- It is usually a best practice to keep the CPU request at ‘1’ or below. (Exceed 1 only if you benefit from multi-core stuff e.g: scientific computing)
- CPU is **compressable**: If your app starts hitting your CPU limits, Kubernetes starts throttling your container.

### Memory on Kubernetes

- Normally, you give a MB value for memory.
- Unlike CPU resources, **memory cannot be compressed**. Because there is no way to throttle memory usage, **if a container goes past its memory limit it will be terminated**.

```yml
containers:
- name: container1
image: busybox
resources:
  requests:
    memory: "32Mi"
    cpu: "200m"
  limits:
    memory: "64Mi"
    cpu: "250m"
- name: container2
image: busybox
resources:
  requests:
    memory: "96Mi"
    cpu: "300m"
  limits:
    memory: "192Mi"
    cpu: "750m"
```

### Namespace settings

- If people forget to set these values they can take up more than their fair share of the cluster. To prevent these scenarios, you can set up ResourceQuotas and LimitRanges at the Namespace level.

#### ResourceQuota

- After creating Namespaces, you can lock them down using ResourceQuotas.

```yml
apiVersion: v1
kind: ResourceQuota
metadata:
  name: demo
spec:
  hard:
    requests.cpu: 500m
    requests.memory: 100Mib
    limits.cpu: 700m
    limits.memory: 600Mib
```

- All of the quotas are for the **combined** resources within the namespace.
- If you are using a production and development Namespace (in contrast to a Namespace per team or service), a common pattern is to put no quota on the production Namespace and strict quotas on the development Namespace. This allows production to take all the resources it needs in case of a spike in traffic.

#### LimitRange

- Unlike a Quota, which looks at the Namespace as a whole, a LimitRange applies to an individual container. This can help prevent people from creating super tiny or super large containers inside the Namespace.

```yml
apiVersion: v1
kind: LimitRange
metadata:
  name: demo
spec:
  limits:
- default:
    cpu: 600m
    memory: 100Mib
  defaultRequests:
    cpu: 100m
    memory: 50Mib
  max:
    cpu: 1000m
    memory: 200Mib
  min:
    cpu: 10m
    memory: 10Mib
  type: Container
```
