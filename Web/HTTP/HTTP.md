# HTTP

## Resources

### Resource Locators

- We can break the last URL into three parts:
  1. **http**, the part before the ://, is what we call the **URL scheme**. The scheme describes how to access a particular resource, and in this case it tells the browser to use the hypertext transfer protocol. Later we'll also look at a different scheme, HTTPS, which is the secure HTTP protocol. You might run into other schemes too, like FTP for the file transfer protocol, and mailto for email addresses. Everything after the :// will be specific to a particular scheme. So, a legal HTTP URL may not be a legal mailto URL—those two aren't really interchangeable (which makes sense because they describe different types of resources).
  2. **food.com** is the **host**. This host name tells the browser the name of the computer hosting the resource. The computer will use the Domain Name System (DNS) to translate food.com into a network address, and then it will know exactly where to send the request for the resource. You can also specify the host portion of a URL using an IP address.
  3. **/recipe/grilled-cauliflower-19710/** is the **URL path**. The food.com host should recognize the specific resource being requested by this path and respond appropriately.

### Ports, Query Strings, and Fragments

- `http://food.com:80/recipes/broccoli/`
  - The number 80 represents the port number the host is using to listen for HTTP requests. **The default port number for HTTP is port 8**0, so you generally see this port number omitted from a URL. You only need to specify a port number if the server is listening on a port other than port 80, which usually only happens in testing, debugging, or development environments.
- `http://www.bing.com/search?q=broccoli`
  - Everything after `?` (the question mark) is known as the query. The query, also called the query string, contains information for the destination website to use or interpret. There is no formal standard for how the query string should look as it is technically up to the application to interpret the values it finds, but you'll see the majority of query strings used to pass name–value pairs in the form `name1=value1&name2=value2`. 
  - For example: `http://foo.com?first=Scott&last=Allen` There are two name–value pairs in this example. The first pair has the name "first" and the value "Scott". The second pair has the name "last" with the value "Allen".
- Finally `http://server.com?recipe=broccoli#ingredients`. The part after the `#` sign is known as the **fragment**. Unlike the URL path and query string, **the fragment is not processed by the server**. The fragment is only used on the client and it identifies a particular section of a resource. Web browsers will typically align the initial display of a webpage such that the top of the element identified by the fragment is at the top of the screen.
- Putting things together: **`<scheme>://<host>:<port>/<path>?<query>#<fragment>`**

### URL Encoding

- To avoid confusion some characters (e.g.: blank space) have been banned from URLs. In fact, RFC 3986 (the "law" for URLs), defines the safe characters for URLs to be the alphanumeric characters in US-ASCII, plus a few special characters like the colon (:) and the slash mark (/).
- Fortunately, you can still transmit unsafe characters in a URL, but all **unsafe characters must be percent-encoded** (aka URL encoded). `%20` is the encoding for a space character (where 20 is the hexadecimal value for the **US-ASCII** space character).
- "`^my resume.txt`" would look like `http://someserver.com/%5Emy%20resume.txt`

### Resource and Media Types

- When a host responds to an HTTP request, it returns a resource and also specifies the content type (also known as the media type) of the resource.
- For example, when a client requests an HTML webpage, the host can respond to the HTTP request with some HTML that it labels as "text/html". The "text" part is the **primary media type**, and the "html" is the **media subtype**.
- The media types are not only for the host to tag outgoing resources, but also for clients to specify the media type they want to consume.

## Messages

### Requests and Responses

- HTTP is a **request and response protocol**. A client sends an **HTTP request** to a server using a carefully formatted message that the server will understand. A server responds by sending an **HTTP response** that the client will understand. The request and the response are **two different message types** that are exchanged in a **single HTTP transaction**.

### A Raw Request and Response

- You can send a raw request using telnet:
  - `telnet www.odetocode.com 80`
  - Type: `GET / HTTP/1.1` + `Enter` + `host:www.odetocode.com` + `Enter` **twice**
- What you see next in the Telnet window is the HTTP response from the web server.

### HTTP Request Methods

- `GET`, `PUT`, `DELETE`, `POST`, `HEAD` (Retrieve the headers for a resource)
- `GET` is a "safe" method, while `POST`/`PUT`/`DELETE` are not, as these modify the server state.
- **POST/Redirect/GET**
  - You post something to the server and instead of seeing the raw result you are redirected to a page that says "account creation was successful" etc.

### Common Scenario—GET

- `<a href="http://odetocode.com/Articles/741.aspx">Part I</a>` the user clicks on the link and the following request is submitted:

    ```http
    GET http://odetocode.com/Articles/741.aspx HTTP/1.1
    Host: odetocode.com
    ```

### Scenario—POST

- The following HTML will generate a POST request toward the server at the specified endpoint.
    ```html
    <form action="/account/create" method="POST">
      <label for="firstName">First name</label>
      <input id="firstName" name="firstName" type="text" />

      <label for="lastName">Last name</label>
      <input id="lastName" name="lastName" type="text" />

      <input type="submit" value="Sign up!"/>
    </form>
    ```
    ```http
    POST http://localhost:1060/account/create HTTP/1.1
    Host: server.com
    firstName=Scott&lastName=Allen
    ```
- At this point the server may respond with:
  - A HTML telling the user that the account has been created. Doing so will leave the user viewing the result of a POST request, which could lead to issues if he or she refreshes the page—it might try to sign them up a second time! a raw HTML telling us the account ahs been created.
  - A redirect instruction like we saw earlier to have the browser issue a safe GET request for a page that tells the user the account has been created.
  - Respond with an error, or redirect to an error page. We'll take a look at error scenarios a little later in the book.

### Forms and GET Requests

- Consider the following form:
    ```html
    <form action="/search" method="GET">
      <label for="term">Search:</label>
      <input id="term" name="term" type="text" />
      <input type="submit" value="Sign up!"/>
    </form>
    ```

- Notice the method on this form is GET, not POST. That's because a search is a safe retrieval operation, unlike creating an account or booking a flight to Belgium. The browser will collect the inputs in the form and issue a GET request to the server:

    ```http
    GET http://localhost:1060/search?term=love HTTP/1.1
    Host: searchengine.com
    ```

- Notice instead of putting the input values into the body of the message, the inputs go into the query string portion of the URL. The browser is sending a GET request for `/search?term=love`.

### HTTP Request Headers

- A full HTTP request message consists of the following parts:

    ```html
    [method] [URL] [version]
    [headers]

    [body]
    ```

- There are numerous headers defined by the HTTP specification. Some of the headers are general headers that can appear in either a request or a response message. An example is the Date header. The client or server can include a Date header indicating when it created the message.
- Some of the more popular request headers appear in the following table.
  - **Referer** - When the user clicks on a link, the client can send the URL of the referring page in this header.
  - **User-Agent** - Information about the user agent (the software) making the request. Many applications use the information in this header, when present, to figure out what browser is making the request (Internet Explorer 6 versus Internet Explorer 9 versus Chrome, etc.).
  - **Accept** - Describes the media types the user agent is willing to accept. This header is used for content negotiation.
  - **Accept-Language** - Describes the languages the user agent prefers.
  - **Cookie** - Contains cookie information, which we will look at in a later chapter. Cookie information generally helps a server track or identify a user.
  - **If-Modified-Since** - Will contain a date of when the user agent last retrieved (and cached) the resource. The server only has to send back the entire resource if it's been modified since that time.

- A full HTTP request might look like the following.

    ```http
    GET http://odetocode.com/ HTTP/1.1
    Host: odetocode.com
    Connection: keep-alive
    User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) Chrome/16.0.912.75 Safari/535.7
    Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8
    Referer: http://www.google.com/url?&q=odetocode
    Accept-Encoding: gzip,deflate,sdch
    Accept-Language: en-US,en;q=0.8
    Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.3
    ```

### Response Status Codes

- `200` - **OK**
- `301` - **Moved Permanently** - The resource has moved to the URL specified in the **Location** header and the client never needs to check this URL again.
- `302` - **Moved Temporarily** - The resource has moved to the URL specified in the Location header. In the future, the client can still request the URL because it's a temporary move. This type of response code is typically used after a POST operation to move a client to a resource it can retrieve with GET (the POST/Redirect/GET pattern we talked about earlier).
- `304` - **Not Modified** - This is the server telling the client that the resource hasn't changed since the last time the client retrieved the resource, so it can just use a locally cached copy.
- `400` - **Bad Request** - The server could not understand the request. The request probably used incorrect syntax.
- `403` - **Forbidden** - The server refused access to the resource.
- `404` - **Not Found** - A popular code meaning the resource was not found.
- `500` - **Internal Server Error** - The server encountered an error in processing the request. Commonly happens because of programming errors in a web application.
- `503` - **Service Unavailable** - The server will currently not service the request. This status code can appear when a server is throttling requests because it is under heavy load.

## Connections

### A Whirlwind Tour of Networking

- Network communication, like many applications, consists of layers. Each layer in a communication stack is responsible for a specific and limited number of responsibilities.
- **1:** The layer underneath HTTP is a **transport layer protocol**. Almost all HTTP traffic travels over **TCP** (short for Transmission Control Protocol), although this isn't required by HTTP. When a user types a URL into the browser, the browser first extracts the host name from the URL (and port number, if any), and opens a TCP socket by specifying the server address (derived from the host name) and port (which defaults to 80). Once an application has an open socket it can begin writing data into the socket. The only thing the browser needs to worry about is writing a properly formatted HTTP request message into the socket. The TCP layer accepts the data and ensures the message gets delivered to the server without getting lost or duplicated. The TCP layer accepts the data and ensures the message gets delivered to the server without getting lost or duplicated.
- **2:** **IP** is short for **Internet Protocol**. While TCP is responsible for error detection, flow control, and overall reliability, IP is responsible for taking pieces of information and moving them through the various switches, routers, gateways, repeaters, and other devices that move information from one network to the next and all around the world. IP tries hard to deliver the data at the destination (but it doesn't guarantee delivery—that's TCP's job). IP is also responsible for breaking data into packets (often called datagrams), and sometimes fragmenting and reassembling these packets so they are optimized for a particular network segment.
- **3:** Everything we've talked about so far happens inside a computer, but eventually these IP packets have to travel over a piece of wire, a fiber optic cable, a wireless network, or a satellite link. This is the responsibility of the **data link layer**. A common choice of technology at this point is Ethernet. At this level, data packets become frames, and low-level protocols like Ethernet are focused on 1s, 0s, and electrical signals.
- *Plus:* Wireshark is a nice tool, in which you can see the incoming / outgoing messages.

### Parallel Connections

- Most user agents (aka web browsers) will not make requests in a serial one-by-one fashion. Instead, they open multiple parallel connections to a server. For example, when downloading the HTML for a page the browser might see two `<img>` tags in the page, so the browser will open two parallel connections to download the two images simultaneously.
- For a long time the number of connections were limited to two by the HTTP 1.1 standard ("A single-user client SHOULD NOT maintain more than 2 connections with any server or proxy."), but there were some workarounds to increase performance: By hosting images on a different server, websites could increase the number of parallel downloads and make their pages load faster (even if the DNS records were set up to point all four requests to the same server, because the two-connection limit is per host name, not IP address). (two parallel connections to www.odetocode.com, and two parallel connections to images.odetocode.com.)#
- Now this rule has been lifted.

### Persistent Connections

- In the early days of the web, a user agent would **open and close a connection for each individual request** it sent to a server. This implementation was in accordance with HTTP's idea of being a completely stateless protocol. As the number of requests per page grew, so did the overhead generated by TCP handshakes and the in-memory data structures required to establish each TCP socket. To reduce this overhead and improve performance, **the HTTP 1.1 specification suggests that clients and servers should implement persistent connections**, and make persistent connections the default type of connection.
- A persistent connection stays open after the completion of one request-response transaction. This behavior leaves a user agent with **an already open socket it can use to continue making requests to the server without the overhead of opening a new socket**.
- Downside: The server might not be able too keep up that many live connections. Denial of Service is also an issue, where hackers open 1000 connections and thus the "real clients" are not able to get served by the server. Hence web servers like apache close the connection after 5 seconds. The only visibility into connections closed is through a network analyzer like Wireshark.
- The `Connection: close` header is a signal to the user agent that the connection will not be persistent and should be closed as soon as possible.

## Web Architecture

### Proxies

- **Forward Proxy**
  - A **proxy server** is a computer that sits between a client and server. A proxy is mostly transparent to end users. You think you are sending HTTP request messages directly to a server, but the messages are actually going to a proxy. The proxy accepts HTTP request messages from a client and forwards the messages to the desired server. The proxy then takes the server response and forwards the response back to the client. Before forwarding these messages, the proxy can inspect the messages and potentially take some additional actions.
  - A proxy server **could also inspect messages to remove confidential data**, like the `Referer` headers that point to internal resources on the company network.
- **Reverse Proxy**
  - A reverse proxy is a proxy server that is closer to the server than the client, and is completely transparent to the client.
  - Both types of proxies can provide a wide range of services. If we return to the gzip compression scenario we talked about earlier, (where we want to compress the message and indicate the gzip type in the header), a proxy server has the capability to compress response message bodies. A company might use a reverse proxy server for compression to take the computational load off the web servers where the application lives. Now, neither the application nor the web server has to worry about compression. Instead, compression is a feature that is layered-in via a proxy. That’s the beauty of HTTP.
- **Load balancing proxies** can take a message and forward it to one of several web servers on a round-robin basis, or by knowing which server is currently processing the fewest number of requests.
- **SSL acceleration proxies** can encrypt and decrypt HTTP messages, taking the encryption load off a web server. We’ll talk more about SSL in the next chapter.
- **Caching proxies** can store copies of frequently accessed resources and respond to messages requesting those resources directly.

### Caching

- Caching can reduce latency, help prevent bottlenecks, and allow a web application to survive when every user shows up at once to buy the newest product or see the latest press release.
- A **public cache** is a cache shared among multiple users. A public cache generally resides on a proxy server. A public cache on a forward proxy is generally caching the resources that are popular in a community of users, like the users of a specific company, or the users of a specific Internet service provider. A public cache on a reverse proxy is generally caching the resources that are popular on a specific website, like popular product images from Amazon.com.
- A **private cache** is dedicated to a single user. Web browsers always keep a private cache of resources on your disk (these are the “Temporary Internet Files” in IE, or type about:cache in the address bar of Google Chrome to see files in Chrome’s private cache). Anything a browser has cached on the file system can appear almost instantly on the screen.
- An HTTP response can have a value for `Cache-Control` of `public`, `private`, or `no-cache`. A value of public means public proxy servers can cache the response. A value of private means only the browser can cache the response. A value of no-cache means nobody should cache the response. There is also a `no-store` value, meaning the message might contain sensitive information and should not be persisted, but should be removed from memory as soon as possible.

    ```http
    HTTP/1.1 200 OK
    Last-Modified: Wed, 25 Jan 2012 17:55:15 GMT
    Expires: Sat, 22 Jan 2022 17:55:15 GMT
    Cache-Control: max-age=315360000,public
    ```

- Notice the `Cache-Control` allows public and private caches to cache the file, and they can keep it around for more than 315 million seconds (10 years). They also use an Expires header to give a specific date of expiration. If a client is HTTP 1.1 compliant and understands `Cache-Control`, it should use the value in max-age instead of Expires. Note that this doesn't mean Flickr plans on using the same CSS file for 10 years. When Flickr changes its design, it’ll probably just use a different URL for its updated CSS file.
- The response also includes a `Last-Modified` header to indicate when the representation was last changed (which might just be the time of the request). Cache logic can use this value as a validator, or a value the client can use to see if the cached representation is still valid. For example, if the agent decides it needs to check on the resource it can issue the following request.

    ```http
    GET … HTTP/1.1
    If-Modified-Since: Wed, 25 Jan 2012 17:55:15 GMT
    ```

- The `If-Modified-Since` header is telling the server the client only needs the full response if the resource has changed. If the resource hasn’t changed, the server can respond with a 304 Not Modified message.
- Another validator you’ll commonly see is the `ETag`. (E.g.: `ETag: "8e5bcd-59f-4b5dfef104d00"`) The ETag is an opaque identifier, meaning it doesn’t have any inherent meaning. An ETag is often created using a hashing algorithm against the resource. If the resource ever changes, the server will compute a new ETag. A cache entry can be validated by comparing two ETags. If the ETags are the same, nothing has changed. If the ETags are different, it’s time to invalidate the cache.

## State and Security

### The Stateless (Yet Stateful) Web

- HTTP in itself is a stateless protocol, but the applications building on the top of that (e.g.: banking) often store state. (Is the user logged in?) One approach is to embed state in the resources being transferred to the client, so that all the state required by the application will travel back on the next request. This approach typically requires some hidden input fields and works best for short-lived state (like the state required for moving through a three-page wizard). Embedding state in the resource keeps all the state inside of HTTP messages, so it is a highly scalable approach, but it can complicate the application programming. --> REST
- Another option is to store the state on the server (or behind the server). This option is required for state that has to be around a long time. Let's say the user submits a form to change his or her email address. The email address must always be associated with the user, so the application can take the new address, validate the address, and store the address in a database, a file, or call a web service to let someone else take care of saving the address.

### Identification and Cookies

- Websites that want to track users will often turn to cookies. Cookies are defined by RFC6265 (http://tools.ietf.org/html/rfc6265), and this RFC is aptly titled "HTTP State Management Mechanism". When a user first visits a website, the site can give the user's browser a cookie using an HTTP header. The browser then knows to send the cookie in the headers of every additional request it sends to the site. Assuming the website has placed some sort of unique identifier into the cookie, **then the site can now track a user as he or she makes requests, and differentiate one user from another**.
- If a user disables cookies a workaround is to track its activity via the url -> **fat url** technique.

### Setting Cookies

- When a website wants to give a user a cookie, it uses a Set-Cookie header in an HTTP response.
    ```http
    HTTP/1.1 200 OK
    Content-Type: text/html; charset=utf-8
    Set-Cookie: fname=Scott$lname=Allen;
                domain=.mywebsite.com; path=/
    ```
- There are three areas of information in the cookie shown in this sample. The **three areas are delimited by semicolons (`;`)**. First, there are one or more **name–value pairs**. These name–value **pairs are delimited by a dollar sign (`$`)**, and look very similar to how query parameters are formatted into a URL. In the example cookie, the server wanted to store the user’s first name and last name in the cookie. The second and third areas are the domain and path, respectively.
- **Example / Imprtant:** Again, it's worth pointing out that the firstName and lastName data stored in the session object is not going into the cookie. The **cookie only contains a session identifier**. The values associated with the session identifier are safe on the server. By default, the session data goes into an in-memory data structure and stays alive for 20 minutes.
- The `path` and `domain` parts are also important - with these attributes you tell the browser **where to send** the cookies. (Hence amazon cookies won't be sent to google etc.) 
  - The `domain` attribute allows a cookie to span sub-domains. In other words, if you set a cookie from www.server.com, the user agent will only deliver the cookie to www.server.com -> hence it is set to `.mywebsite.com` so that other "sub-domains" (`images.mywebsite.com`) will also work.
  - The `path` attribute can restrict a cookie to a specific resource path. In the previous example, the cookie will only travel to a server.com site when the request URL is pointing to /stuff, or a location underneath /stuff, like /stuff/images. Path settings can help to organize cookies when multiple teams are building web applications in different paths.

### XSS and HTTP only

- Another security concern around cookies is how vulnerable they are to a cross-site scripting attack (XSS). In an XSS attack, a malicious user injects malevolent JavaScript code into someone else's website. If the other website sends the malicious script to its users, the malicious script can modify, or inspect and steal cookie information (which can lead to session hijacking, or worse).
- To combat this vulnerability, Microsoft introduced the `HttpOnly` flag. The `HttpOnly` flag tells the user agent to not allow script code to access the cookie. The cookie exists for "HTTP only"—i.e. to travel out in the header of every HTTP request message. **Browsers that implement HttpOnly will not allow JavaScript to read or write the cookie on the client.**

### Persistent Cookies

- Persistent cookies can outlive a single browsing session and a user agent will store the cookies to disk. You can shut down a computer and come back one week later, go to your favorite website, and a persistent cookie will still be there for the first request.
- The only difference between the two (persistent vs non-persistent) is that a persistent a cookie needs an Expires value.

### Cookie Downsides

- **Tracing the client**
  - Cookies have been vulnerable to XSS attacks as we've mentioned earlier, and also receive bad publicity when sites (particularly advertising sites) use **third-party cookies** to track users across the Internet. Third-party cookies are cookies that get set from a different domain than the domain in the browser's address bar. Third-party cookies have this opportunity because many websites, when sending a page resource back to the client, will include links to scripts or images from other URLs. The requests that go to the other URLs allow the other sites to set cookies.
  - As an example, the home page at server.com can include a `<script`> tag with a source set to bigadvertising.com. This allows bigadvertising.com to deliver a cookie while the user is viewing content from server.com. The cookie can only go back to bigadvertising.com, but if enough websites use bigadvertising.com, then Big Advertising can start to profile individual users and the sites they visit. Most web browsers will allow you to disable third-party cookies (but they are enabled by default).
  - Any response with a Set-Cookie header should not be cached, at least not the headers, since this can interfere with user identification and create security problems. Also, keep in mind that anything stored in a cookie is visible as it travels across the network (and in the case of a persistent cookie, as it sits on the file system). Since we know there are lots of devices that can listen and interpret HTTP traffic, a **cookie should never store sensitive information**. Even session identifiers are risky, because if someone can intercept another user's ID, he or she can steal the session data from the server.

### Authentication

#### Basic Authentication

- The `401` status code tells the client the request is unauthorized. The `WWW-Authenticate` header tells the client to collect the user credentials and try again. The realm attribute gives the user agent a string it can use as a description for the protected area. What happens next depends on the user agent, but most browsers can display a UI for the user to enter credentials.
    ```htttp
    HTTP/1.1 401 Unauthorized
    WWW-Authenticate: Basic realm="localhost"
    ```
- With the credentials in hand, the browser can send another request to the server. This request will include an Authorization header.

    ```http
    GET http://localhost/html5/ HTTP/1.1
    Authorization: Basic bm86aXdvdWxkbnRkb3RoYXQh
    ```

- The value of the authorization header is the client's username and password in a base 64 encoding. Basic authentication is insecure by default, because anyone with a base 64 decoder who can view the message can steal a user's password. For this reason, basic authentication is rarely used without using secure HTTP, which we'll look at later.

#### Digest Authentication

- Instead, the client must send a **digest** of the password. The client computes the digest using the MD5 hashing algorithm with a nonce the server provides during the authentication challenge (a nonce is a cryptographic number used to help prevent replay attacks). In this case the server sends something like this:

    ```http
    HTTP/1.0 401 Unauthorized
    WWW-Authenticate: Digest realm="localhost",
                      qop="auth,auth-int",
                      nonce="dcd98b7102dd2f0e8b11d0f600bfb0c093",
                      opaque="5ccc069c403ebaf9f0171e9517f40e41"
    ```

#### Windows Authentication

- Windows Integrated Authentication is not a standard authentication protocol but it is popular among Microsoft products and servers.
- Windows Authentication has the advantage of being secure even without using secure HTTP, and of being unobtrusive for users of Internet Explorer. IE will automatically authenticate a user when challenged by a server, and will do so using the user's credentials that he or she used to log into the Windows operating system.

#### Forms-based Authentication

- Forms authentication is the most popular approach to user authentication over the Internet. Forms-based authentication is not a standard authentication protocol and doesn't use WWW-Authenticate or Authorization headers. However, many web application frameworks provide some out of the box support for forms-based authentication.
- With a restricted resource the user is redirected to the login page.
- The login page for forms-based authentication is an HTML form with inputs for the user to enter credentials. When the user clicks submit, the form values will POST to a destination where the application needs to take the credentials and validate them against a database record or operating system. If the auth was successful the user is redirected to the previously denied resource.

#### OpenID

- With OpenID, a user registers with an OpenID identity provider, and the identity provider is the only site that needs to store and validate user credentials. There are many OpenID providers around, including Google, Yahoo, and Verisign.

#### Secure HTTP

- Secure HTTP solves the problem of readable HTTP messages by encrypting messages before the messages start traveling across the network.
- The default port for HTTP is port 80, and **the default port for HTTPS is port 443**.
- **The security layer exists between the HTTP and TCP layers**, and features the use of the Transport Layer Security protocol (TLS) or the TLS predecessor known as Secure Sockets Layer (SSL).
- HTTPS requires a server to have a cryptographic certificate. The certificate is sent to the client during setup of the HTTPS communication. The certificate includes the server's host name, and a user agent can use the certificate to validate that it is truly talking to the server it thinks it is talking to. The validation is all made possible using public key cryptography and the existence of certificate authorities, like Verisign, that will sign and vouch for the integrity of a certificate.
- **All traffic over HTTPS is encrypted** in the request and response, including the HTTP headers and message body, and also everything after the host name in the URL. This means the path and query string data are encrypted, as well as all cookies. HTTPS prevents session hijacking because no eavesdroppers can inspect a message and steal a cookie.
- **The server is authenticated to the client thanks to the server certificate**. If you are talking to mybigbank.com over HTTPS, you can be sure your messages are really going to mybigbank.com and not someone who stuck a proxy server on the network to intercept requests and spoof response traffic from mybigbank.com
- **HTTPS does not authenticate the client.** Applications still need to implement forms authentication or one of the other authentication protocols mentioned previously if they need to know the user’s identity. HTTPS does make forms-based authentication and basic authentication more secure since all data is encrypted. There is the possibility of using client-side certificates with HTTPS, and client-side certificates would authenticate the client in the most secure manner possible. However, client-side certificates are generally not used on the open Internet since not many users will purchase and install a personal certificate. Corporations might require client certificates for employees to access corporate servers, but in this case the corporation can act as a certificate authority and issue employees certificates they create and manage.