# Rest Design

## Basics

- **NOT** a protocol but an **Achitectural principle** for managing state information.
- All resources (represented as XML, JSON or RDF - JSON being the primary) are identified by the Uniform Resource Identifier (URI)
- The server doesn't keep any state about the client session on the server side!
- With RESTful web services, a client can cache any response coming from the server. The server can mention how, and for how long, it can cache the responses.
- Leverages HTTP, hence inherits all the caching properties that HTTP offers.
- Typically used over HTTP, hence the `GET`, `POST`, `PUT`, `DELETE` methods are usually used.
- REST is often used along reactive programming:
  - The Publisher publishes a stream of data, to which the Subscriber is asynchronously subscribed. The Processor transforms the data stream without the need for changing the Publisher or the Subscriber. The Processor (or multiple Processors) sits between the Publisher and the Subscriber to transform one stream of data to another.