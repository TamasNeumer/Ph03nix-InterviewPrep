# gRPC

## Basics

### What is RPC & gRPC

- In distributed computing, a remote procedure call (RPC) is when a computer program causes a procedure (subroutine) to execute in a different address space (commonly on another computer on a shared network), which is coded as if it were a normal (local) procedure call, without the programmer explicitly coding the details for the remote interaction. That is, the programmer writes essentially the same code whether the subroutine is local to the executing program, or remote.
- As in many RPC systems, gRPC is based around the idea of defining a service, specifying the methods that can be called remotely with their parameters and return types. On the server side, the server implements this interface and runs a gRPC server to handle client calls. On the client side, the client has a stub (referred to as just a client in some languages) that provides the same methods as the server.

![gRPC](https://grpc.io/img/landing-2.svg)

### Proto Buffers

- By default gRPC uses protocol buffers, Google’s mature open source mechanism for serializing structured data (although it can be used with other data formats such as JSON).
- You define how you want your data to be structured once, then you can use special generated source code to easily write and read your structured data to and from a variety of data streams and using a variety of languages. ou can even update your data structure without breaking deployed programs that are compiled against the "old" format.
- You specify how you want the information you're serializing to be structured by defining protocol buffer message types in `.proto` files.

    ```protobuf
    message Person {
      required string name = 1;
      required int32 id = 2;
      optional string email = 3;

      enum PhoneType {
        MOBILE = 0;
        HOME = 1;
        WORK = 2;
      }

      message PhoneNumber {
        required string number = 1;
        optional PhoneType type = 2 [default = HOME];
      }

      repeated PhoneNumber phone = 4;
    }
    ```
- Each message type has one or more **uniquely numbered fields**, and each field has a **name** and a value **type**, where value types can be numbers (integer or floating-point), booleans, strings, raw bytes, or even (as in the example above) other protocol buffer message types, allowing you to structure your data hierarchically.
- These field numbers are used to identify your fields in the message binary format, and **should not be changed once your message type is in use**. If you update a message type by entirely removing a field, or commenting it out, future users can reuse the field number when making their own updates to the type. This can cause severe issues if they later load old versions of the same `.proto`, including data corruption, privacy bugs, and so on. Hence **mark** the deleted fields with the reserve keyword. Note that you can't mix field names and field numbers in the same reserved statement.

  ```protobuf
  message Foo {
    reserved 2, 15, 9 to 11;
    reserved "foo", "bar";
  }
  ```

- When you run the protocol buffer compiler on a `.proto`, the compiler generates the code in your chosen language you'll need to work with the message types you've described in the file, including getting and setting field values, serializing your messages to an output stream, and parsing your messages from an input stream.
- When a message is parsed, if the encoded message does not contain a particular singular element, the corresponding field in the parsed object is set to the default value for that field. This is kind of the same in Java objects. However for enums, the default value is the **first defined enum** value, which must be 0.
- You can use other message types as field types. For example, let's say you wanted to include `Result` messages in each `SearchResponse` message:

    ```protobuf
    message SearchResponse {
      repeated Result results = 1;
    }

    message Result {
      string url = 1;
      string title = 2;
      repeated string snippets = 3;
    }
    ```

- Importing definitions can be done via `import "myproject/other_protos.proto";`
- You can also nest types:
    ```protobuf
    message SearchResponse {
      message Result {
        string url = 1;
        string title = 2;
        repeated string snippets = 3;
      }
      repeated Result results = 1;
    }
    ```
- **Any**
  - The `Any` message type lets you use messages as embedded types without having their .proto definition. An `Any` contains an arbitrary serialized message as bytes, along with a URL that acts as a globally unique identifier for and resolves to that message's type.
  - To use the `Any` type, you need to import `google/protobuf/any.proto`.

    ```protobuf
    import "google/protobuf/any.proto";

    message ErrorStatus {
      string message = 1;
      repeated google.protobuf.Any details = 2;
    }
    ```
  - Different language implementations will support runtime library helpers to pack and unpack `Any` values in a typesafe manner – for example, in Java, the `Any` type will have special `pack()` and `unpack()` accessors, while in C++ there are `PackFrom()` and `UnpackTo()` methods.

    ```cpp
    status.add_details()->PackFrom(details);
    //...
    NetworkErrorDetails network_error;
    detail.UnpackTo(&network_error);
    ```

- **Oneof**
  - Oneof fields are like regular fields except all the fields in a oneof share memory, and at most one field can be set at the same time. Setting any member of the oneof automatically clears all the other members. You can check which value in a oneof is set (if any) using a special `case()` or `WhichOneof()` method, depending on your chosen language.

    ```protobuf
    message SampleMessage {
      oneof test_oneof {
        string name = 4;
        SubMessage sub_message = 9;
      }
    }
    ```
  - You then add your oneof fields to the oneof definition. You can add fields of any type, **but cannot use repeated fields**.

- **Maps**
  - `map<key_type, value_type> map_field = N;`
    - Note that enum is not a valid `key_type`. The `value_type` can be any type except another map.
  - Map fields **cannot be `repeated`**.

- **Packages**
  - You can add an optional package specifier to a `.proto` file to prevent name clashes between protocol message types.
  - `package foo.bar; message Open { ... }`

- **Generating your classes**
  - You need to run the protocol buffer compiler `protoc` on the `.proto`

### gRPC Concepts

- **Service definition**
  - Like many RPC systems, gRPC is based around the idea of defining a service, specifying the methods that can be called remotely with their parameters and return types.

    ```protobuf
    service HelloService {
      rpc SayHello (HelloRequest) returns (HelloResponse);
    }

    message HelloRequest {
      string greeting = 1;
    }

    message HelloResponse {
      string reply = 1;
    }
    ```
  - gRPC lets you define four kinds of service method:
    - **Unary** RPCs where the client sends a single request to the server and gets a single response back, just like a normal function call.
      - `rpc SayHello(HelloRequest) returns (HelloResponse){}`
    - **Server streaming** RPCs where the client sends a request to the server and gets a stream to read a sequence of messages back.
      - `rpc LotsOfReplies(HelloRequest) returns (stream HelloResponse){}`
    - **Client streaming** RPCs.
      - `rpc LotsOfGreetings(stream HelloRequest) returns (HelloResponse) {}`
    - **Bidirectional streaming** RPCs where both sides send a sequence of messages using a read-write stream.
      - `rpc BidiHello(stream HelloRequest) returns (stream HelloResponse){}`

- **Using the API surface**
  - **Basics**
    - Starting from a service definition in a `.prot`o file, gRPC provides protocol buffer compiler plugins that generate client- and server-side code. (Maven, Gradle ...)
    - When running this gRPC generates the `HelloRequest` and `HelloResponse` classes in your `generated` folder.
    - As for the service a `GreeterGrpc` class is generated that contains (among many) the language-specific (Java) method signature. This class will be used to create server-side implementations for the services.
  - **Server side implementation**
    - On the server side, the server implements the methods declared by the service (`GreeterGrpc`) and runs a gRPC server to handle client calls. The gRPC infrastructure decodes incoming requests (from binary (?) to Java objects), executes service methods, and encodes service responses.
    - Typically you have some (for the example grpc specific) boiler plate code that is responsible running the server.
    - And inside your server class you have a static inner class that handles the RPC requests:

      ```java
      // Your server class containing boiler plate for starting server.
      //...
      // Service Implementation:
      static class GreeterImpl extends GreeterGrpc.GreeterImplBase {
          @Override
          public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
            HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
          }
        }
      ```

      - The `req` contains the request, while the server can communicate with the client via the `responseObserver`, that is given by the client at method call. (Kind of like the client sends a reference to himself to the server while sending the request, so that the server knows where to send the response.)
      - It's important to see that the implemented function returns `void` and the server sends the `HelloReply` via the `onNext()` method (in order to conform to the signature defined in the `.proto`.
  - **Client side implementation**
    - On the client side, the client has a local object known as **stub** (for some languages, the preferred term is client) that implements the same methods as the service. (Given via the generated `GreeterGrpc` class)
    - `private final GreeterGrpc.GreeterBlockingStub blockingStub;`

    ```java
    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    //...

    // channel contains host and port
    HelloWorldClient(ManagedChannel channel) {
        this.channel = channel;
        blockingStub = GreeterGrpc.newBlockingStub(channel);
      }

    public void greet(String name) {
        logger.info("Will try to greet " + name + " ...");
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response;
        try {
          response = blockingStub.sayHello(request);
        } catch (StatusRuntimeException e) {
          logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
          return;
        }
        logger.info("Greeting: " + response.getMessage());
      }
    ```

    - The client can then just call those methods on the local object, wrapping the parameters for the call in the appropriate protocol buffer message type - gRPC looks after sending the request(s) to the server and returning the server’s protocol buffer response(s).

- **Synchronous vs. asynchronous**
  - gRPC supports both synchronous (blocking) and asynchronous (non-blocking) communication.

- **Timeout**
  - gRPC allows clients to specify how long they are willing to wait for an RPC to complete before the RPC is terminated with the error `DEADLINE_EXCEEDED`. On the server side, the server can query to see if a particular RPC has timed out, or how much time is left to complete the RPC.

- **Metadata**
  - Metadata is information about a particular RPC call (such as authentication details) in the form of a list of key-value pairs, where the keys are strings and the values are typically strings (but can be binary data). Metadata is opaque to gRPC itself - it lets the client provide information associated with the call to the server and vice versa.

### Advanced Java Tutorial

#### Proto setup

- **Specify Java Package**
  - As we’re generating Java code in this example, we’ve specified a `java_package` file option in our `.proto`
  - `option java_package = "io.grpc.examples.routeguide";`
  - This specifies the package we want to use for our generated Java classes. If no explicit java_package option is given in the .proto file, then by default the proto package (specified using the “package” keyword) will be used.
- **4 service methods definitions**
  - Simple RPC
    - `rpc GetFeature(Point) returns (Feature) {}`
  - A server-side streaming RPC
    - `rpc ListFeatures(Rectangle) returns (stream Feature) {}`
  - A client-side streaming RPC
    - `rpc RecordRoute(stream Point) returns (RouteSummary) {}`
  - A bidirectional streaming RPC
    - `rpc RouteChat(stream RouteNote) returns (stream RouteNote) {}`
- Finally your `.proto` will look something like this:

    ```protobuf
    syntax = "proto3";

    option java_multiple_files = true;
    option java_package = "io.grpc.examples.routeguide";
    option java_outer_classname = "RouteGuideProto";
    option objc_class_prefix = "RTG";

    package routeguide;

    service RouteGuide {
      rpc GetFeature(Point) returns (Feature) {}
      rpc ListFeatures(Rectangle) returns (stream Feature) {}
      rpc RecordRoute(stream Point) returns (RouteSummary) {}
      rpc RouteChat(stream RouteNote) returns (stream RouteNote) {}
    }

    message Point {
      int32 latitude = 1;
      int32 longitude = 2;
    }

    message Rectangle {
      Point lo = 1;
      Point hi = 2;
    }

    message Feature {
      string name = 1;
      Point location = 2;
    }

    message FeatureDatabase {
      repeated Feature feature = 1;
    }

    message RouteNote {
      Point location = 1;
      string message = 2;
    }

    message RouteSummary {
      int32 point_count = 1;
      int32 feature_count = 2;
      int32 distance = 3;
      int32 elapsed_time = 4;
    }
    ```

#### Server & Client

- **Simple RPC**
  - Inside the server class we define the `static` inner class:
    - `private static class RouteGuideService extends RouteGuideGrpc.RouteGuideImplBase {...}`

    ```java
    // Check if there is a Feature for the given point.
    @Override
    public void getFeature(Point request, StreamObserver<Feature> responseObserver) {
      responseObserver.onNext(checkFeature(request));
      responseObserver.onCompleted();
    }
    ...

    private Feature checkFeature(Point location) {
      // features is class variable.
      for (Feature feature : features) {
        if (feature.getLocation().getLatitude() == location.getLatitude()
            && feature.getLocation().getLongitude() == location.getLongitude()) {
          return feature;
        }
      }

      // No feature was found, return an unnamed feature.
      return Feature.newBuilder().setName("").setLocation(location).build();
    }
    ```
    - Same as in the hello world example. The server returns the `Feature` via the `onNext` method and then signals that the process was completed.
    - The simplified client code also looks similar to the hello world example:

    ```java
    public void getFeature(int lat, int lon) {
      Point request = Point.newBuilder().setLatitude(lat).setLongitude(lon).build();
      Feature feature;
      feature = blockingStub.getFeature(request);
    }
    ```
- **Server-side streaming RPC**
  - The server side streaming occurs by calling the `onNext` method multiple times. The end is signaled by calling the `onCompleted` method.
    ```java
    // Given a rectangle on the map stream back the points that fall inside
    @Override
    public void listFeatures(Rectangle request, StreamObserver<Feature> responseObserver) {
      // left,right,bottom,top calculation based on request

      for (Feature feature : features) {
        if (!RouteGuideUtil.exists(feature)) {
          continue;
        }

        int lat = feature.getLocation().getLatitude();
        int lon = feature.getLocation().getLongitude();
        if (lon >= left && lon <= right && lat >= bottom && lat <= top) {
          responseObserver.onNext(feature);
        }
      }
      responseObserver.onCompleted();
    }
    ```
  - The blocking request returns an iterator. The client can now iterate through the returned values.
    ```java
    public void listFeatures(int lowLat, int lowLon, int hiLat, int hiLon) {
        Rectangle request = // Coordinates -> Points -> Build rectangle
        Iterator<Feature> features;
        try {
          features = blockingStub.listFeatures(request);
          for (int i = 1; features.hasNext(); i++) {
            Feature feature = features.next();
            info("Result #" + i + ": {0}", feature);
            if (testHelper != null) {
              testHelper.onMessage(feature);
            }
          }
        } // Catch...
    ```
- **Client-side streaming RPC**
  - The server gets a stream of `Points` from the client and return a single `RouteSummary` with information about their trip. The server returns a `StreamObserver<Point>` - an anonymous class implementing the server's functionality in the `onNext` and `onCompleted` methods.

    ```java
    @Override
        public StreamObserver<Point> recordRoute(final StreamObserver<RouteSummary> responseObserver) {
          return new StreamObserver<Point>() {
            int pointCount;
            int featureCount;
            int distance;
            Point previous;
            final long startTime = System.nanoTime();

            @Override
            public void onNext(Point point) {
              pointCount++;
              if (RouteGuideUtil.exists(checkFeature(point))) {
                featureCount++;
              }
              // For each point after the first, add the incremental distance from the previous point to
              // the total distance value.
              if (previous != null) {
                distance += calcDistance(previous, point);
              }
              previous = point;
            }

            @Override
            public void onCompleted() {
              responseObserver.onNext(/*Build Summary based on fields*/);
              responseObserver.onCompleted();
            }
          };
        }
    ```

  - The client defines its own anonymous class for `responseObserver`. This defines what the client does with the result, once received. (=observing response)
  - The `asyncStub` provides the connection to the server. We create a `requestObserver` using the `responseObserver`. By doing so, once the async response arrives, the passed object's (`responseObserver`'s) `onNext` and `onCompleted` methods will be called.
  - Finally using the `requestObserver` the client "streams" the data to the server using a `for` loop.

    ```java
    public void recordRoute(List<Feature> features, int numPoints) throws InterruptedException {
        // Anonymous class - describing callback events
        StreamObserver<RouteSummary> responseObserver = new StreamObserver<RouteSummary>() {
          @Override
          public void onNext(RouteSummary summary) {
            // Print summary content via format string.
          }

          @Override
          public void onCompleted() {
            // sout("Finished");
          }
        };

        // Register callback to async
        StreamObserver<Point> requestObserver = asyncStub.recordRoute(responseObserver);
        try {
          // Send numPoints points randomly selected from the features list (function arg).
          for (int i = 0; i < numPoints; ++i) {
            int index = random.nextInt(features.size());
            Point point = features.get(index).getLocation();
            requestObserver.onNext(point);
          }
        }
        //Catch...
        // Mark the end of requests
        requestObserver.onCompleted();

        // Receiving happens asynchronously
        if (!finishLatch.await(1, TimeUnit.MINUTES)) {
          warning("recordRoute can not finish within 1 minutes");
        }
      }
    ```
- **Bidirectional streaming**
  - Server similar, but instead responding once, server responds in a streaming manner.
    ```java
    @Override
        public StreamObserver<RouteNote> routeChat(final StreamObserver<RouteNote> responseObserver) {
          return new StreamObserver<RouteNote>() {
            @Override
            public void onNext(RouteNote note) {
              List<RouteNote> notes = getOrCreateNotes(note.getLocation());

              // Respond with all previous notes at this location. (streaming manner)
              for (RouteNote prevNote : notes.toArray(new RouteNote[0])) {
                responseObserver.onNext(prevNote);
              }

              // Now add the new note to the list
              notes.add(note);
            }

            @Override
            public void onCompleted() {
              responseObserver.onCompleted();
            }
          };
        }
    ```
  - The client

    ```java
    public void routeChat() {
        // Async stub + anonymous class -> describe "incoming request" action.
        StreamObserver<RouteNote> requestObserver =
            asyncStub.routeChat(new StreamObserver<RouteNote>() {
              @Override
              public void onNext(RouteNote note) {
                // sout received note
              }

              @Override
              public void onCompleted() {
                // sout("finished");
              }
            });

        try {
          RouteNote[] requests = // Array instantiation with values
          for (RouteNote request : requests) {
            // Spam the server with the stream.
            requestObserver.onNext(request);
          }
        }// catch exception

        // Mark the end of requests
        requestObserver.onCompleted();
      }
    ```

- **Building**
  - Look into the example project's `build.gradle`
  - Worth to [Read](https://github.com/grpc/grpc-java/blob/master/COMPILING.md)