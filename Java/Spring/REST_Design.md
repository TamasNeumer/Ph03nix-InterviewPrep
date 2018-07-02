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

## Aloha

- The code
    ```java
    @Configuration
    @EnableAutoConfiguration
    @ComponentScan
    @Controller
    public class TicketManagerApplication {

        public static void main(String[] args) {
            SpringApplication.run(TicketManagerApplication.class, args);
        }

        @ResponseBody
        @RequestMapping("/")
        public String sayAloha(){
            return "Aloha";
        }
    }
    ```
  - `@Configuration` - Indicates that a class declares one or more @Bean methods and may be processed by the Spring container to generate bean definitions and service requests for those beans at runtime. `@Configuration` classes are typically bootstrapped using either AnnotationConfigApplicationContext or its web-capable variant, AnnotationConfigWebApplicationContext.
    ```java
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.register(AppConfig.class);
    ctx.refresh();
    MyBean myBean = ctx.getBean(MyBean.class);
    // use myBean ...
    ```
  - `@EnableAutoConfiguration` - Enable auto-configuration of the Spring Application Context, attempting to guess and configure beans that you are likely to need, based on the jar dependencies that you have added. Auto-configuration classes are usually applied based on your classpath and what beans you have defined. For example, if you have `tomcat-embedded.jar` on your classpath you are likely to want a `TomcatServletWebServerFactory`. When using `SpringBootApplication`, the auto-configuration of the context is automatically enabled and adding this annotation has therefore no additional effect.
  - `@ComponentScan` - Configures component scanning directives for use with `@Configuration` classes. Tells Spring to look for other components, configurations, and services in the the specified package. Spring is able to auto scan, detect and register your beans or components from pre-defined project package.
  - `@Component` - Indicates that an annotated class is a "Controller" (e.g. a web controller). This annotation serves as a specialization of `@Component`, allowing for implementation classes to be autodetected through classpath scanning. It is typically used in combination with annotated handler methods based on the `RequestMapping` annotation.
  - `@ResponseBody` - It is a Spring annotation which binds a method return value to the web response body. It is not interpreted as a view name. It uses HTTP Message converters to convert the return value to HTTP response body, based on the content-type in the request HTTP header. The message converter in our case is `MappingJackson2HttpMessageConverter`, which reads and writes JSON using Jackson's ObjectMapper.
  - `@RequestMapping` - Annotation for mapping web requests onto methods in request-handling classes with flexible method signatures.

## Designing a complete REST API with fle upload

### Basics of class design

- **Basic directory structure**
  - **Model/EntityDomain** - contains the annotated Java POJOs
  - **Service** - contains the functions that are used to operate on the database. (`getAllUser()`, `createUser(String username, String...)`)
  - **Repository** - An interface extending Spring's `CrudRepositoy` interface.
  - **Controller** - contains the functions that process the incoming HTTP requests, interpret the data and call the appropriate services with the correct arguments.
  - **Command** - Command objects represent an abstraction between the API endpoints and the actual Entities that are stored in the database.
  - **Converter** - Classes used to convert the command entities to their corresponding "database entity" types.

- **Maven dependencies**
    ```java
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-web</artifactId>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-data-jpa</artifactId>
            </dependency>
            <dependency>
                <groupId>org.springframework</groupId>
                <artifactId>spring-orm</artifactId>
                <version>5.0.6.RELEASE</version>
            </dependency>
            <dependency>
                <groupId>mysql</groupId>
                <artifactId>mysql-connector-java</artifactId>
                <scope>runtime</scope>
            </dependency>
            <dependency>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <optional>true</optional>
            </dependency>

            <!--Hibernate & HikariCP-->
            <dependency>
                <groupId>org.hibernate</groupId>
                <artifactId>hibernate-core</artifactId>
                <version>5.2.17.Final</version>
            </dependency>
            <dependency>
                <groupId>org.hibernate</groupId>
                <artifactId>hibernate-hikaricp</artifactId>
                <version>5.2.17.Final</version>
            </dependency>
            <dependency>
                <groupId>com.zaxxer</groupId>
                <artifactId>HikariCP</artifactId>
                <version>3.1.0</version>
            </dependency>

            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-test</artifactId>
                <scope>test</scope>
            </dependency>
        </dependencies>
    ```
- **Application Config**
    ```properties
    # DataSource
    spring.datasource.url = jdbc:mysql://localhost:3306/hibernate?useSSL=false
    spring.datasource.username = root
    spring.datasource.password = master12345

    # Hikari will use the above plus the following to setup connection pooling
    spring.datasource.hikari.minimumIdle=5
    spring.datasource.hikari.maximumPoolSize=10
    spring.datasource.hikari.idleTimeout=30000
    spring.datasource.hikari.poolName=SpringBootJPAHikariCP
    spring.datasource.hikari.maxLifetime=2000000
    spring.datasource.hikari.connectionTimeout=30000

    # Without below HikariCP uses deprecated com.zaxxer.hikari.hibernate.HikariConnectionProvider
    # Surprisingly enough below ConnectionProvider is in hibernate-hikaricp dependency and not hibernate-core
    # So you need to pull that dependency but, make sure to exclude it's transitive dependencies or you will end up
    # with different versions of hibernate-core
    spring.jpa.hibernate.connection.provider_class=org.hibernate.hikaricp.internal.HikariCPConnectionProvider

    # JPA specific configs
    spring.jpa.properties.hibernate.show_sql=true
    spring.jpa.properties.hibernate.format_sql=true
    spring.jpa.properties.hibernate.use_sql=true
    spring.jpa.properties.hibernate.id.new_generator_mappings=false
    spring.jpa.properties.hibernate.default_schema=littracker
    spring.jpa.properties.hibernate.search.autoregister_listeners=false
    spring.jpa.properties.hibernate.bytecode.use_reflection_optimizer=false
    spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.MySQL57InnoDBDialect

    # Hibernate ddl auto (create, create-drop, validate, update)
    spring.jpa.hibernate.ddl-auto = create

    # Enable logging
    logging.level.org.hibernate.SQL=DEBUG
    # Enable logging for HikariCP to verify that it is used
    logging.level.com.zaxxer.hikari.HikariConfig=DEBUG
    logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
    ```

## Adding reactive programming

- **Basics of reactive programming**
  - Overall, Reactive programming is about non-blocking, event-driven applications that can be scaled with a small number of threads, with back pressure as a main component to make sure the producers (emitters) do not overwhelm consumers (receivers).
  - The main difference between Java 8 Streams and Reactive Streams is that Reactive is a push model, whereas Java 8 Streams focuses on pulling. In Reactive Streams, based on consumer needs and numbers, all events will be pushed to consumers.
  - Reactive Streams is a bundle of four Java interfaces:
    - `Publisher`
    - `Subscriber`
    - `Subscription`
    - `Processor`
  - `Publisher` will publish a stream of data items to the subscribers that registered with the Publisher. Using an executor, the Publisher publishes the items to the Subscriber. Also, Publisher makes sure that the Subscriber method invocations for each subscription are strictly ordered.
  - `Subscriber` consumes items only when requested. You can cancel the receiving process any time by using Subscription.
  - `Subscription` behaves as a message mediator between the Publisher and the Subscriber.
  - `Processor` represents a processing stage, which can include both Subscriber and a Publisher. Processor can initiate back pressure and cancel the subscription, as well.
- **Back pressures**
  - Back pressure is a mechanism that authorizes the receiver to define how much data it wants from the emitter (data provider).
- **Main Types in Reactor**
  - `Flux`
    - A Flux is the equivalent of an RxJava Observable, capable of emitting zero or more items, and then, optionally, either completing or failing.
    - Flux implements the `Publisher` interface from the reactive stream manifesto.
    - Flux mainly represents a stream of N elements.
  - `Mono`
    - A type of reactor that can emit only one item at the most, hence used for signaling.
    - Concatenating two Monos together will produce a Flux; on the other hand, calling `single()` on `Flux<T>` will return a `Mono<T>`.
- **Adding dependencies to the POM**
    ```xml
        <dependencyManagement>
            <dependencies>
                <dependency>
                    <groupId>io.projectreactor</groupId>
                    <artifactId>reactor-bom</artifactId>
                    <version>Bismuth-RELEASE</version>
                    <type>pom</type>
                    <scope>import</scope>
                </dependency>
            </dependencies>
        </dependencyManagement>

        <dependencies>
            ...
            <dependency>
              <groupId>org.reactivestreams</groupId>
              <artifactId>reactive-streams</artifactId>
              <version>1.0.2</version>
          </dependency>
          <dependency>
              <groupId>io.projectreactor</groupId>
              <artifactId>reactor-core</artifactId>
              <version>3.1.7.RELEASE</version>
          </dependency>
          <dependency>
              <groupId>io.projectreactor.ipc</groupId>
              <artifactId>reactor-netty</artifactId>
              <version>0.7.7.RELEASE</version>
          </dependency>
          <dependency>
              <groupId>org.apache.tomcat.embed</groupId>
              <artifactId>tomcat-embed-core</artifactId>
              <version>8.5.4</version>
          </dependency>
          <dependency>
              <groupId>org.springframework</groupId>
              <artifactId>spring-context</artifactId>
              <version>5.0.0.RELEASE</version>
          </dependency>
          <dependency>
              <groupId>org.springframework</groupId>
              <artifactId>spring-webflux</artifactId>
              <version>5.0.0.RELEASE</version>
          </dependency>
        </dependencies>
    ```
- **Java Code**
    ```java
    @Data
    public class User {
        private Integer userid;
        private String username;

        public User(Integer userid, String username) {
            this.userid = userid;
            this.username = username;
        }
    }

    public interface UserRepository {
        Flux<User> getAllUsers();
    }

    public class UserRepositoryImpl implements UserRepository {
        private Map<Integer, User> users = null;

        public UserRepositoryImpl() {
            users = new HashMap<>();
            users.put(1, new User(1, "Onur"));
            users.put(2, new User(2, "Jan"));
            users.put(3, new User(3, "Tamas"));
        }

        @Override
        public Flux<User> getAllUsers() {
            return Flux.fromIterable(this.users.values());
        }
    }

    public class UserHandler {

        private final UserRepository userRepository;

        public UserHandler(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        public Mono<ServerResponse> getAllUsers(ServerRequest request) {
            Flux<User> users = this.userRepository.getAllUsers();

            return ServerResponse.ok().contentType(APPLICATION_JSON).body(users, User.class);
        }
    }

    public class Server {

        public static final String HOST = "localhost";
        public static final int PORT = 8081;

        public static void main(String[] args) throws InterruptedException, IOException {
            Server server = new Server();
            server.startReactorServer();

            System.out.println("Press ENTER to exit.");
            System.in.read();
        }

        public void startReactorServer() throws InterruptedException {
            RouterFunction<ServerResponse> route = routingFunction();
            HttpHandler httpHandler = toHttpHandler(route);

            ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);
            HttpServer server = HttpServer.create(HOST, PORT);
            server.newHandler(adapter).block();
        }

        public RouterFunction<ServerResponse> routingFunction() {
            UserRepository repository = new UserRepositoryImpl();
            UserHandler handler = new UserHandler(repository);

            return nest(path("/user"),
                    nest(accept(APPLICATION_JSON),
                            route(GET("/{id}"), handler::getAllUsers).andRoute(method(HttpMethod.GET),
                                    handler::getAllUsers)).andRoute(POST("/").and(contentType(APPLICATION_JSON)),
                            handler::getAllUsers));
        }
    }
    ```