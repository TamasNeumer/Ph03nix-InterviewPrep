# Reactive Spring

## Basics

- **Terms**
  - **Data Streams**
    - Can be just anything (sequence of events ordered in time)
  - **Asynchronous**
    - Events are asynchronously captured.
    - A function is defined to execute when an event is emitted.
    - Another function is defined if an error is emitted.
    - Another function is defined when complete is emitted.
  - **GoF Observer Pattern**
    - Subject, Observer - when the subject changes observers are notified.
  - **Non-Blocking**
    - In blocking mode you stop and wait for data (e.g. for I/O or netwrok)
    - In non-blocking the thread will de something else until resource is available.
  - **Back Pressure**
    - Ability of the subscriber to throttle data.
  - **Failure as Messages**
    - Exceptions are not thrown as it would break the stream.
    - Instead it's handled gracefully.

- **Reactive Streams API**
  - `Publisher` - provider of a potentially unbounded number of sequenced elements, publishing them according to the demand received from its `Subscriber`.
  - `Subscriber` - receives the data stream of the `Publisher`.
  - `Subscription` - Represents a one-to-one lifecycle of a `Subscriber` subscribing to a `Publisher`
  - `Processor` - Represents a processing stage which is both a Subscriber and a Publisher and obeys the contracts of both.

- **Spring WebFlux**
  - Servlet API is blocking --> created a new stack called "WebFlux"
  - Tomcat, Jetty, Netty, Undertow -> Reactive Implementations (?!)
- **Spring 5 Reactive Types**
  - `Mono` publisher with zero or one element in the datastream
  - `Flux` publisher with zero or many elements in the data stream