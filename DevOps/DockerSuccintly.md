# Docker

## Introduction

### What is docker

- Docker is an **application platform**. It lets you package your application with everything it needs, from the operating system upwards, into a single unit that you can share and run on any computer that has Docker.
- Docker runs your application in a lightweight, isolated component called a **container**. Containers are lightweight because they don’t need the extra load of a hypervisor, but **run directly within the host machine’s kernel**.
- The application package, called a **Docker image**, is typically only tens or hundreds of megabytes, so it’s cheap to store and fast to move.

### Docker engine

- Docker Engine is a client-server application with these major components:
  - A server which is a type of long-running program called a daemon process (the `dockerd` command).
  - A REST API which specifies interfaces that programs can use to talk to the daemon and instruct it what to do.
  - A command line interface (CLI) client (the `docker` command).

    ![Docker Engine](https://docs.docker.com/engine/images/engine-components-flow.png)

### Docker architecture

- Docker uses a client-server architecture. The Docker client talks to the Docker daemon, which does the heavy lifting of building, running, and distributing your Docker containers. The Docker client and daemon can run on the same system, or you can connect a Docker client to a remote Docker daemon. The Docker client and daemon communicate using a REST API, over UNIX sockets or a network interface.

  ![](https://docs.docker.com/engine/images/architecture.svg)

- **The Docker daemon**
  - The Docker daemon (`dockerd`) listens for Docker API requests and manages Docker objects such as images, containers, networks, and volumes. A daemon can also communicate with other daemons to manage Docker services.
- **The Docker client**
  - The Docker client (`docker`) is the primary way that many Docker users interact with Docker. When you use commands such as `docker run`, the client sends these commands to `dockerd`, which carries them out. The docker command uses the Docker API. The Docker client can communicate with more than one daemon.
- **Docker registries**
  - A Docker registry stores Docker images. Docker Hub and Docker Cloud are public registries that anyone can use, and Docker is configured to look for images on Docker Hub by default.
  - When you use the `docker pull` or `docker run` commands, the required images are pulled from your configured registry. When you use the `docker push` command, your image is pushed to your configured registry.
  - Docker store allows you to buy and sell Docker images or distribute them for free.
- **Docker objects**
  - **Images**
    - An image is a **read-only** template with instructions for creating a Docker container. Often, an image is based on another image, with some additional customization.
    - To build your own image, you create a *Dockerfile* with a simple syntax for defining the steps needed to create the image and run it. Each instruction in a Dockerfile creates a layer in the image. When you change the Dockerfile and rebuild the image, only those layers which have changed are rebuilt.
  - **Containers**
    - A container is a **runnable instance of an image**. You can create, start, stop, move, or delete a container using the Docker API or CLI.
- **Example**
  - `docker run -i -t ubuntu /bin/bash`
  - If you do not have the ubuntu image locally, Docker pulls it from your configured registry, as though you had run `docker pull` ubuntu manually.
  - Docker creates a new container, as though you had run a `docker container create` command manually.
  - Docker allocates a read-write filesystem to the container, as its final layer. This allows a running container to create or modify files and directories in its local filesystem.
  - Docker creates a network interface to connect the container to the default network, since you did not specify any networking options. This includes assigning an IP address to the container. By default, containers can connect to external networks using the host machine’s network connection.
  - Docker starts the container and executes `/bin/bash`. Because the container is running interactively and attached to your terminal (due to the `-i` and `-t` flags), you can provide input using your keyboard while the output is logged to your terminal.
  - When you type `exit` to terminate the `/bin/bash` command, the container stops but is not removed. You can start it again or remove it.

### Installing docker

- Docker has **three** components:
  - the background server that does the work
  - the Docker client, a command-line interface for working with the server
    - The client is cross-platform, which means you can run it natively from Linux, Windows, and OS/X machines, and you can manage Docker running locally or on a remote machine.
  - REST API for client-server communication
- You can find the  platform specific installation guide on the website.

### Hello World

- `docker container run hello-world`
  - Your local Docker client sends a request to the Docker server to run a container from the image called hello-world.
  - The Docker server checks to see if it has a copy of the image in its cache. If not, it will download the image from Docker Hub.
  - When the image is downloaded locally, the Docker server runs a container from the image, and sends the output back to the Docker client.
- With this image, the process inside the container ends when the console output has been written, and Docker containers exit when there are no processes running inside. You can check that by getting a list of running containers from Docker using the `docker container ls` (container list) command. (You can also use `docker container ls -all`)

### NgineX

- `docker container run nginx:alpine`
  - The container is listening for HTTP requests on port 80, but that’s port 80 **inside** the container, so **we can’t reach it from the host machine**. This container isn’t doing much, so we can kill it by ending the process with Ctrl+C.
- `docker container run --detach --publish 80:80 nginx:alpine`
  - Docker supports long-running background processes, such as web servers, by allowing containers to **run in detached mode**, so the container keeps running in the background.
  - Port 80 published with the `--publish` flag
  - That command publishes port 80 inside the container to port 80 on the host. Ports can’t be shared, so this will fail if you have another process listening on port 80. However, you can publish the container port to any free port on the host: `--publish 8081:80` maps port 8081 on the host to port 80 in the container.
  - Now you can reach the website in your browser at `localhost` -> Nginx welcome page.
- To kill the container running in the background use `docker kill <id>`

### Hello Ubuntu

- The last type of container is one you run interactively. It stays alive as long as you’re connected to it with the Docker CLI, and it behaves like a remote connection to a separate machine.
- Grab a Ubuntu container using `docker container run --interactive --tty ubuntu:16.04`
- With the `--interactive` and `--tty` flags, Docker runs the container interactively with terminal emulation (it’s commonly abbreviated to-it).
- The version of Ubuntu in the Docker image is a **heavily stripped down version of Ubuntu Server**, which means some of the **most basic utilities aren’t available**. (E.g.: nano editor is not available and apt-get is outdated.)
- You can use the `apt-get update` command but are only changing the container, hence when `exit`-ing, the changes won't be saved.

## Packaging Applications with Docker

### Dockerfile

- The Dockerfile uses a very simple domain-specific language that only requires a handful of instructions. Example (Ubuntu container with the nano package installed):

  ```docker
  FROM ubuntu
  RUN apt-get update && apt-get install nano
  ```

- The `FROM` instruction specifies the base image so that your image will start from there and layer on the changes in the rest of your Dockerfile.
- In order to build an image, you use the `docker image build` command. You need to specify both a repository name to identify the image, and the path Docker should use as the context for building the image.
  - `Dockerfile` (with no extension) is the default filename Docker looks for, but you can call your Dockerfile anything and identify it with the `--file` option. This means you can call your file server.dockerfile and build it with `docker image build --file server.dockerfile`.
- `docker image build --tag dockersuccinctly/ubuntu-with-nano .`
  - For this command have the file above saved as `Dockerfile`
  - In a **separate** folder! When building the image the client sends all data that is next to the dockerfile to the daemon as "context". --> If you have 500MB trash next to your dockerfile, they will be packed into your container.
  - The `.` at the end is the working directory.
  - The `dockersuccinctly/ubuntu-with-nano` will be used as "tag".
  - If you don’t specify a version in a tag, Docker uses the default `latest`.
- When you successfully build an image, it’s **stored in the Docker server’s local cache** and you can run containers from it.
- Use `docker image ls` to check your images. When you start using larger images, your local cache can use a lot of disk space. We will come back to this later.

### Dockerfile instructions

- **Basic Instructions**
  - `RUN`: Execute a command.
    - `RUN chmod +x /echoserver.sh`
  - `ENV`: Set environment variables.
    - `ENV LOG_FILE echo.out`
  - `COPY`: Copy files from the build context into the image.
    - `COPY ./echoserver.sh /echoserver.sh`
  - `EXPOSE`: Expose ports from the container that can be mapped to ports on the host.
    - `EXPOSE 8082`
  - `VOLUME`: Create a directory inside the image that can be mapped to external storage.
    - `VOLUME /server-logs`
  - `CMD`: Specify the command to run when the container starts
    - `CMD /echoserver.sh`
- Docker uses the CMD instruction to tell it how to start—in this case by running the echoserver.sh script.

- **Example**
  - Dockerfile:
    ```docker
    FROM ubuntu
    MAINTAINER Elton Stoneman <elton@sixeyed.com>

    RUN apt-get update && \
        apt-get install -y netcat-openbsd

    ENV LOG_FILE echo.out

    EXPOSE 8082

    VOLUME /server-logs

    COPY ./echoserver.sh /echoserver.sh

    RUN chmod +x /echoserver.sh

    CMD /echoserver.sh

    ```
  - echoserver.sh:
    ```bash
    netcat -k -l -p 8082 > /server-logs/$LOG_FILE
    ```
  - Build the image with `docker image build --tag dockersuccinctly/echoserver .`
  - Execute using `docker container run --detach --publish 8082:8082 --name echo-server dockersuccinctly/echoserver`
  - Upon starting and executing `nc localhost 8082` you should see a text printed to your command line.
  - You can also print out the content of the log file of the container: `docker container exec echo-server cat /server-logs/echo.out`

### Layered Build Process

- Docker uses a layered filesystem for images. Starting from the base image, the Docker server runs a temporary container from the image for each instruction in the Dockerfile, executes the instruction, then saves the temporary container as a new image, adding it to the local image cache. Docker uses the cache during the build process, which means that if it finds an image matching the current instruction stack—that is, one that matches the state you’re asking Docker to create—it will reuse the cached image.
- You can write your Dockerfile to make maximum use of the cache by ensuring that the Dockerfile is correctly structured and that the Dockerfiles for different applications each have similar structures. This way, they will use cached images as much as possible. Ideally, when you build apps with similar dependencies, Docker will need only to execute instructions in new layers that are specific to the application.
- The `docker image history` command displays all the layers in an image: `docker image history dockersuccinctly/a`
- This caching might pose a problem: image with the state `apt-get update` is cached, and if months alter you are building an image using the same command the old (cached) image is retrieved and hence the app-repository remains 1-month old! [Here](https://docs.docker.com/develop/develop-images/dockerfile_best-practices/) are the best practices for writing docker files.

## Image registries and Docker Hub

### About registries

- A registry is simply a shared location for images—you can push images you’ve created and pull images that you or other people have created.
- The Docker Hub is a free, public registry for images maintained by Docker, Inc. It is a hugely popular service.
- When you have an account registered with the Docker Hub, you will need to add your credentials to the Docker CLI using `docker login`.
- Once you have your account you can push images using `docker image push`
  - `docker image push dockersuccinctly/echoserver`
- The push works intelligently, pushing only layers that aren’t already available in the registry.

### Tags and Versions

- Docker image repositories use the basic format **{user}/{app}**, but often the tag is used to specify a release version, or a variant of the image, in the format **{user}/{app}:{tag}**. If you don’t specify a tag when you refer to an image, the default `latest` tag is used.
- You can search and filter for certain images via: `docker image ls --filter reference=ubuntu`
- Tip: It’s a good idea to specify a tag for base images in your `FROM` instruction. If you don’t, the image tagged as `latest` will be used. That tag can change to a different image without warning, and there can be breaking changes between image versions, which means your own images might fail to build. If you have an old image built on ubuntu:14.04 but the Dockerfile specifies `FROM` ubuntu, the base image will be ubuntu:16.04 the next time you build it, which is functionally different.

### Automating builds

- The automated build setup is only available when you’re logged in to the Docker Hub. From the top menu, select Create > Create Automated Build. From here, you can link your GitHub or Bitbucket account and select the source git repository.
- After the build is set up, any pushes to the git repo will trigger a build on Docker Hub.
- Note: Note: If you find the perfect base image and it’s not from an official repository, the publisher is not required to keep that image up-to-date or even available. If you build from another user’s base image on the Hub, that repository can be deleted, leaving you unable to build your own image. If that’s a concern, you should consider cloning the source and building your own version of the base image so that you can control the image stack up to a reliable, official source.

### Running a local registry

- `docker container run -d -p 5000:5000 registry:2`
  - Running Your Own Image Registry
  - Docker can use different registries from the default Docker Hub, and you need only to specify the registry location as a prefix to the image repository name. The Docker Registry runs on port 5000, which means that when you have a container running locally with port 5000 mapped, you can reference it at localhost:5000.
- `docker image tag dockersuccinctly/echoserver localhost:5000/dockersuccinctly/echoserver`
  - Tagging an Image for a Different Registry
- `docker image push localhost:5000/dockersuccinctly/echoserver`
  - Pushing to a Local Registry
- With Docker’s Registry image, you get the basic push and pull functionality but not the command-line search option, and there is no UI, so it’s not feature-equivalent to the Docker Hub. The REST API for the Registry gives you a lot of functionality, but without a friendly interface.
  - `curl localhost:5000/v2/_catalog`

## Data Storage in Docker

### Containers and their data

- Docker uses a layered filesystem for containers called the **Union File System**. When you run a container from an image, all the layers going into the image are mounted in the container as **read-only** parts of the filesystem.
- Next, a **read-write** layer is created on top for this instance of the container. You can change the data inside your container, but this will not change the image itself.
- In conclusion images are immutable and the only way to "edit" is to create a new one.

### Images, containers, and volumes

- When you run a container, **volumes can be added** to it or they can be configured in the Dockerfile so that they’re part of the image—the same as explicitly specifying ports to be made available with the `EXPOSE` instruction.
- The `VOLUME` instruction specifies a data volume with a named path that you can use as a file location in the container.
    ```docker
    RUN mkdir /v1 && touch /v1/file1.es.txt
    VOLUME /v1
    ```
- To run the container: `docker container run -it --rm dockersuccinctly/ubuntu-with-volume`
- Now, we can write some data in our running container. This is an Ubuntu container, and we can write to the usual paths—`/` is the filesystem root, and `/v1` is the name of the volume we specified in the Dockerfile.
- Now if we `exit` the container, **the container is removed** because we ran it with the `--rm` flag, but the files in the volume are in a location on the host, which raises the question: can they still be used? The short answer is that the files you created **while** running the container are lost. I.e. upon start `file1.es.txt` would be there but any new file that you created are lost.
- Note: The position of the `VOLUME` instruction in the Dockerfile is important. If you try to work with files in a volume after the `VOLUME` instruction, the work will be done in a temporary container during the build process, and because the volume is container-specific, the changes won’t be committed to the image. Instead, you should set up the data on the filesystem first, so that it’s part of the image, then expose the volume.

### Mounting into data volumes

- If you want to look at the data inside a container, you typically must attach to it or execute a command to run inside it. With data volumes, when you run a container, you can mount the volume from a specified path on the host, which means **you have a shared folder between the host and any containers** using the same mounted volume.

    ```docker
    mkdir ~/v1
    docker container run -it --rm -v ~/v1:/v1 dockersuccinctly/ubuntu-with-volume
    ```
- When a data volume is mounted from the host, **files from the image do not get copied into the container volume** when it starts because this is a shared location and copying files could overwrite data.
- However if we create a file, `exit` and re-run the container it will have the data, as the data on the host machine is not lost!
- Note: It’s good to be clear on the different ways of using Docker volumes. Many images on the Hub use volumes for variable data, such as configuration files, or for permanent state, such as database files. The **Nginx** image is a good example—it has a volume defined for website content, and if you mount the volume from a local directory containing your website files, you can run your site in an Nginx container directly from the official image without having to build your own image.

### Sharing data volumes

- We can demonstrate sharing volumes for the backup scenario with the official MongoDB image by using the following command:
  - `docker container run -d --name mongo mongo`
- The Mongo image stores database files in a volume named /data/db that we can access in a separate container by running it with the --volumes-from option, which shares the volumes from the named container in the new container.
- To run an interactive container with access to the database’s volumes and list the Mongo data directory contents:
  - `docker container run -it --volumes-from mongo ubuntu`
- The volume for this container is stored on the host machine, but because it’s not using a mount source, we don’t know where the data is physically stored. The `docker container inspect` command gives us that information along with a lot of extra details about the container.
  - `docker container inspect mongo`
  - Upon executing you will see the "source" property pointing to a directory on your local machine (`/var/lib/docker/volumes/bb05.../_data`)
- This is the physical storage location for the files on the host. Containers can see this data in the volume thanks to the virtual filesystem mounting the data into the container. The container doesn’t get a copy of the files—it uses the originals directly. **If you’re running Linux containers on Docker for Mac or Docker for Windows, remember the “host” is really a Linux VM, so you won’t see the files on your physical machine.**

### Removing containers, data volumes, and images

- **Containers**
  - Containers end when:
    - the process inside them finishes or
    - they are externally ended with the `docker container kill` or `docker container stop` commands.
  - But the container isn’t removed — **it’s just stopped**. The docker container ls command won’t show the container unless you add the `--all` flag, which will list all containers, including the stopped ones.
  - F**or single-task and interactive containers, it’s good practice to use the** `--rm` flag when you start the container **so that Docker will automatically remove the container when it ends**.
  - To explicitly remove containers, use the `docker container rm` command and specify containers by their ID or name
  - **Removing all containers**
    - `docker ps -aq` - to list all containers
    - `docker rm $(docker ps -aq)`
  - You should also be aware that when you remove a container that has a data volume, **the data volume is not automatically removed**. In order to remove the data volume along with the container, you need to explicitly state it with the `-v` option: `docker container rm -v {container}`.
  - If you stop and removed a container but forgot to remove the volume you just created a *"dangling volume"*.
    - `docker volume ls -qf dangling=true` - list dangling volume
    - `docker volume rm $(docker volume ls -qf dangling=true)` - remove them
- **Images**
  - Images are never automatically removed by Docker—they must always be explicitly deleted with the `docker image rm` command.
  - As with volumes, the Docker CLI includes the concept of dangling images—Docker images that are in the cache but have no containers based off them. To remove these use: 
    - `docker image rm $(docker image ls -f "dangling=true" -q)`

## Orchestrating Systems with Docker

### Orchestrating multicontainer solutions

- When you break down a large application into smaller parts with each part running in a separate container, you’ll need a way for containers to work together. That’s called orchestration.
- Orchestration requires a framework that allows communication between containers, configuration of how containers need to be connected, scheduling for container creation, monitoring of health, and scaling. There are external platforms for container orchestration— Mesos and Kubernetes are popular—but Docker has orchestration built into the platform.

### Docker networks

- At the network level, Docker creates a bridge network on your host. A bridge network gives every container its own IP address, and it allows communication between containers and also between containers and the host.
- Note: There are other types of networks supported by Docker. The *bridge* network is for working with containers on a single host. The *overlay* network is for working with containers that run across multiple hosts. There is also a plugin system that lets you use third-party network types.
- Creating a network in Docker is easy. Bridge networks are the default, which means you simply need to give the network a name:
  - `docker network create ch05`
- The bridge network is created with a designated IP address range, and every container in the network will have a dedicated IP address in that range.
- Now you can run a container and add it to your network:
  - `docker container run -d --name webcontainer --network ch05 nginx:alpine`
- We can get the IP address by inspecting the container or the network:
  - `docker network inspect ch05`
    - You will see something like `"IPv4Address": "172.20.0.2/16",` in the properties of the container meaning that you can open nginx in your browser using the `172.20.0.2` address (on Linux)
    - The bridge is between the container and the host. If you’re running on Linux you can access containers by their IP address. On Docker for Mac and Docker for Windows, **remember the host is a Linux VM, so you can’t connect directly to the container IP address from your host machine**.
- You can now start multiple containers. In order to see how the network connections are set up us the following command:
  - `root@acda23507c84:/# ip -f inet address` (Linux command, inside container!)
- Docker has a built-in DNS server, so containers on the same network can access each other by name as well as by IP address, as we can see with the `dig` and `ping` commands:
  - `dig webcontainer`
  - `ping webcontainer -c 2`
- The DNS server in Docker makes application configuration easy in a multicontainer system because, in your applications, you simply refer to dependencies by their container name. If you have a database container called mysql, the connection string in your web app can always use mysql as the server name.

### Docker Compose

- Docker Compose comes bundled with Docker for Mac, Docker for Windows, and the Docker Toolbox. On Linux, it’s a separate install, so you’ll need to follow the instructions to install Docker Compose.
- The Docker Compose syntax takes the arguments available in the docker container run command and structures them as YAML.

  ```yaml
  version: '2'
  services:
    web:
      image:
        nginx:alpine
    util:
      image: sixeyed/ubuntu-with-utils
      container_name: util
      command: ping web
  ```

- The first line defines this as using version 2 of the Docker Compose file format. In Compose, you define containers to run as services, then you specify the image name and any other optional properties, such as the container name and command in this example.
- By convention, Compose files are named `docker-compose.yml`.
- From the directory where the YAML file is saved, you can start all the services in the Compose file as containers by running the `up` command:
  - `docker-compose up -d`
  - As with the Docker CLI, we can specify `-d` to **daemonize** all the containers and keep them running in the background.
- The Nginx container has a name generated by Compose that uses the project name `code-listing-54_web_1`, which is derived by combining the folder where the `docker-compose.yml` file is on my machine (`code-listing-54` folder) with the service name (`web`) and the instance of the container (`1`).
- We can add more Nginx containers with the `scale` option to the up command:
  - `docker-compose up –d --scale web=5`
- The IP addresses of the containers start with **172.21.0**—a different range from the network we explicitly created earlier in the chapter. **Docker Compose creates a different network for each project, each named after the directory in which the Compose file lives**.
- `docker network ls` -> "code-listing-54-default" is sitting there.
- To stop the containers ran by the composer: `docker-composer stop`
- **Note:**
  - Other commands, such as `kill` and `rm`, are available in Docker Compose, but it’s important to remember that Compose is a client-side tool. **When you create services with Compose, Docker has no notion that they’re related** (other than being in the same network). **In order to manage the services as a single unit, you work in the directory on your client machine where the `docker-compose.yml` file lives**.

## Clustering Hosts with Docker Swarm

### Clustering with Docker swarm mode

- You can run Docker as a standalone host, as we’ve done so far, or you can join that same host to a swarm with a single command.
- When you have multiple machines running in a swarm, you start containers in a similar way, but you can specify how many instances of the container you want to run. You make a request to the swarm, and, based on the resources available, Docker decides which hosts will actually run the containers.

### Creating a Docker swarm

- Docker swarm follows a manager-worker architecture in which the manager schedules work on the workers. In a swarm, the type of work is always the same—running containers—and Docker is slightly unusual in that containers can run on the manager nodes, too. A **manager node must be running for the swarm to operate correctly**. You **can have multiple nodes** designated as managers in order to provide high availability, but **only one is active — the leader**.
- Docker swarm mode uses the *Raft Consensus Algorithm* for electing a new leader manager node if the current one is lost. Like similar algorithms, Raft relies on a majority election, which means **you should have an odd number of manager nodes** in order to avoid tied elections—three or five is common. Because Docker swarm managers can run containers, too, your secondary managers can work and you won't have wasted compute in your swarm.
- You’ll need a number of machines (or VMs) set up, and the only prerequisites for the machines are:
  - Docker installed, version 1.12 or higher
  - Ports 2377, 7946, and 4789 open so the machines can communicate
- When you create your swarm, Docker will provision certificates and ensure that **communication between all the nodes is encrypted**.
- With the machines ready to go, choose one to be the master, note its IP address, and create a swarm using the `swarm init` command.
- "I have three VMs in Azure running Ubuntu, which have Docker installed, and I'm going to make them into a swarm. create the swarm from the machine with the internal IP address 10.0.0.4, which switches this Docker host into swarm mode and sets it as the manager for the swarm."
  - `docker swarm init --advertise-addr 10.0.0.4`
    - In the `init` command, you need only to specify the IP address from which the master will listen for nodes if the manager has multiple IP addresses, but specifying the address is useful as a matter of course so that you’ll know exactly where the manager is advertising for new joiners.
    - The output from `swarm init` gives you a token that you use to join more nodes to the swarm.
- Now we can join another node to our swarm using the token:
  - `docker swarm join --token <token>`

### Working with Docker swarm

- First, all the machines in the swarm are called nodes, and you manage them with a set of **node** commands.
- Second, you don’t run containers on the swarm, you work at a higher level with services.
- From the swarm manager, you can see all the nodes in the swarm and the current status with the `node ls` command:
  - `docker node ls`
- To start a service on the swarm, use the `service create` command:
  - `docker service create --name website --publish 80:80 nginx:alpine`
- Creating a service is a lot like running a container. You specify the image and any options for the engine—for example, the service name and port publishing in this example. Nginx exposes port 80, and my Azure VMs are configured to allow port 80 requests from the Internet, which means I should be able to browse my website from any computer so long as I know the address of the node running the container.
- The `service ls` command tells me which services are running, `service ps` tells me which hosts are running which containers.
  - `docker service ls`
  - `docker service ps website`
- One of the great things about Docker swarm mode is its built-in request routing, which lets you send a request to any node in the swarm, and if that node isn’t running a container that can service the request, it will transparently route the request to another node where the container is running.

### Scaling services

- We can run multiple instances of a container using the `replicas` option. Replica is the swarm terminology for an instance of a container, and as the name suggests, these are replicas running from the same image with the same setup.
- Running multiple replicas with a load balancer means you can scale up your service by running it on more nodes. The replica level can be specified when services are created, and it can be changed when services are running.
- The `service scale` command adds another four instances of my website container to the Swarm:
  - `docker service scale website=5`
- You can check the running instances with:
  - `service ps`
  - I can see where the instances are running. In this case, I have more replicas than there are nodes in the swarm, which means nodes swarm-00 and swarm-02 have two replicas running and the node swarm-01 just has one.
- If I now make a request to a specific node, I’ll get a response from that node—all nodes have an instance of the container running, which means they won’t need to reroute requests internally. I have a load balancer set up in Azure that shares requests among all the VMs, which is outside of Docker at the infrastructure level. Using Apache Bench to test performance, with five replicas running across three nodes, I get served more than 180 requests per second.
  - `ab -n 3000 -c 150 http://docker-succinctly.northeurope.cloudapp.azure.com/`
- If the service scale is reduced to a single replica, performance drops to fewer than 120 requests per second.
- Tip: Tip: The routing mesh works at port level—if a node gets a request on a port from which there’s no container listening, it will forward the request on to another host that does have a container listening on that port. If you run only one service per port on your swarm, you don’t need a proxy—your load balancer and the swarm can handle it. If you’re running multiple services with the same port, such as several websites all running on port 80, you’ll need a proxy, like Nginx, running in the swarm. The proxy listens on port 80 while all your other services listen on custom ports, and Nginx routes traffic to different ports based on the incoming HTTP request.
- If you kill swarm-02 node then the containers that had been on node swarm-02 have been rescheduled on the other nodes so that node swarm-00 now has three containers and node swarm-01 has two.

## Docker on Linux

### Containers and kernels

- When you run a Docker container on a Linux host, **it runs in an isolated process boundary called a namespace**. Inside the container, the app thinks it’s running on its own computer with no other processes, but in fact the boundary is only a logical boundary and there can be many other processes running on the same physical host.
- When you run a Docker container on a Linux host, it runs in an isolated process boundary called a namespace. Inside the container, the app thinks it’s running on its own computer with no other processes, but in fact the boundary is only a logical boundary and there can be many other processes running on the same physical host.
- This is why Docker containers can run so efficiently—they use the underlying operating system kernel of the host machine so that processes inside the container are actually running on the host. For a host, running multiple containers is the same as running multiple processes (unlike virtual machines, for which each VM has its own kernel and a hypervisor running on the host in order to translate between the virtual kernel and the real kernel). That is why you can’t run Linux containers on Windows or run Windows containers on Linux. A container using Ubuntu as its base image needs to run on a Linux machine so that when the container launches an executable, the host is capable of running it. Linux executables aren’t compatible with Windows, which means you can’t run an Ubuntu-based container on Windows.

### Installation

- When you’re running Linux-based containers, you use a server-grade Linux distribution for your hosts, and Docker might be the only software you install. Everything else you’d want to run on your server would run as a container in Docker.
- To install docker: `curl -sSL http://get.docker.com | sh`
