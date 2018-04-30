# TomCat

#### Web server

- A web server uses **HTTP** protocol to transfer data.
- **HTTP Protocol**
  - *Application layer protocol* that is used to transmit virtually all files and other data on the World Wide Web.
  - HTTP protocol uses TCP protocol to create an established, reliable connection between the client (the web browser) and the server (wikibooks.org).
  - **TCP Connection Tuple** = Client IP & Port, Server IP & Port
    - Almost all HTTP requests are sent using TCP port 80, of course any port can be used.
    - If we want to load www.wikibooks.org, we need to first resolve the wikibooks.org IP address from a **Domain Name System (DNS) server** The router is usually set to the **Internet Service Provide (ISP)** DNS server, however you can use alternative as OpenDNS or Google Public DNS.
  - Request verbs:
    - **GET**, **POST**, **PUT**, **DELETE**
  - Status codes
    - 1xx - informational
    - 2xx - successful
    - 3xx - redirection
    - 4xx - client error (bad request / unauthorized)
    - 5xx - server error (service unavailable etc.)
- **Servlets**
  - `Servlet` is an interface defined in `javax.servlet` package. It declares three essential methods for the life cycle of a servlet – `init()`, `service()`, and `destroy()`.
    - The `service()` method is invoked upon each request after its initialization. Each request is serviced in its own separate thread. The web container calls the `service()` method of the servlet for every request. The `service()` method determines the kind of request being made and dispatches it to an appropriate method to handle the request.
- **Servlet container**
  - The basic idea of Servlet container is using Java to dynamically generate the web page on the server side.
  - **Processing requests**
    - Web server receives HTTP request
    - Web server forwards the request to servlet container
    - The servlet is dynamically retrieved and loaded into the address space of the container, if it is not in the container.
    - The container invokes the init() method of the servlet for initialization(invoked once when the servlet is loaded first time)
    - The container invokes the service() method of the servlet to process the HTTP request, i.e., read data in the request and formulate a response. The servlet remains in the container’s address space and can process other HTTP requests.
    - Web server return the dynamically generated results to the correct location
  - Using servlets allows the JVM to *handle each request within a separate Java thread*, and this is one of the key advantage of Servlet container.