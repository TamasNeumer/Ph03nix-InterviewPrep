# Testing Spring Boot

## @SpringBootTest

- `@SpringBootTest` creates the `ApplicationContext` used in your tests through `SpringApplication`.
- If using Junit5 add the `@ExtendWith(SpringExtension)` annotation. For Junit4 use the `@RunWith(SpringRunner.class)`.
- By default, `@SpringBootTest` will not start a server. You can use the webEnvironment attribute of @SpringBootTest to further refine how your tests run:

- `SpringBootTest` configuration:

  - `MOCK`(Default)
    - Loads a web `ApplicationContext` and provides a mock web environment. Embedded servers are **not** started when using this annotation. If a web environment is not available on your classpath, this mode transparently falls back to creating a regular non-web `ApplicationContext`. It can be used in conjunction with `@AutoConfigureMockMvc` or `@AutoConfigureWebTestClient`for mock-based testing of your web application.
    - Thus the full application is started -> you can smoke test that the controllers were correctly autowired (`assertThat(controller).isNotNull();`), but you can't send HTTP requests, as the server is not started!
  - `RANDOM_PORT`

    - Loads a `WebServerApplicationContext` and provides a **real web environment**. Embedded servers are started and listen on a random port.

      ```java
      @ExtendWith(SpringExtension.class)
      @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
      class HttpRequestTest {

          @LocalServerPort
          private int port;

          @Autowired
          private TestRestTemplate restTemplate;

          @Test
          void greetingShouldReturnDefaultMessage() throws Exception {
              assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/",
                      String.class)).contains("Hello World");
          }
      }
      ```

    - To avoid starting the embedded server we can use the previously mentioned `@AutoConfigureMockMvc`. In this test, the full Spring application context is started, but without the server.

      ```java
      @ExtendWith(SpringExtension.class)
      @SpringBootTest
      @AutoConfigureMockMvc
      class ApplicationTest {

          @Autowired
          private MockMvc mockMvc;

          @Test
          void shouldReturnDefaultMessage() throws Exception {
              this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk())
                      .andExpect(content().string(containsString("Hello World")));
          }
      }
      ```

    - We can narrow down the tests to just the web layer by using `@WebMvcTest`. Here Spring Boot is only instantiating the web layer, not the whole context. In an application with multiple controllers you can even ask for just one to be instantiated, using, for example `@WebMvcTest(HomeController.class)`

    ```java
    @ExtendWith(SpringExtension.class)
    @WebMvcTest
    public class WebLayerTest {

        @Autowired
        private MockMvc mockMvc;

        @Test
        public void shouldReturnDefaultMessage() throws Exception {
            this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk())
                    .andExpect(content().string(containsString("Hello World")));
        }
    }
    ```

    - Alternatively if you only want to test the controller level you can mock the service classes, and ask Spring to initialize only the controller that is being tested.

    ```java
    @RunWith(SpringRunner.class)
    @WebMvcTest(GreetingController.class)
    public class WebMockTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private GreetingService service;

        @Test
        public void greetingShouldReturnMessageFromService() throws Exception {
            when(service.greet()).thenReturn("Hello Mock");
            this.mockMvc.perform(get("/greeting")).andDo(print()).andExpect(status().isOk())
                    .andExpect(content().string(containsString("Hello Mock")));
        }
    }
    ```

  - `DEFINED_PORT`: Loads a `WebServerApplicationContext` and provides a real web environment. Embedded servers are started and listen on a defined port (from your `application.properties`) or on the default port of `8080`.
  - `NONE`: Loads an `ApplicationContext` by using `SpringApplication` but does not provide _any_ web environment (mock or otherwise).

- `@Transactional`
  - If your test is `@Transactional`, it rolls back the transaction at the end of each test method by default. However, as using this arrangement with either `RANDOM_PORT` or `DEFINED_PORT` implicitly provides a real servlet environment, the HTTP client and server run in separate threads and, thus, in separate transactions. **Any transaction initiated on the server does not roll back in this case.**

## Context configuration

- Default behavior

  - Spring Boot's `@*Test` annotations search for your primary configuration automatically whenever you do not explicitly define one. The search algorithm works up from the package that contains the test until it finds a class annotated with `@SpringBootApplication` or `@SpringBootConfiguration`.

- Manual context configuration

  - `@TestConfiguration`

    - Unlike a nested `@Configuration` class, which would be used **instead** of your application's primary configuration, a **nested** `@TestConfiguration` class is **used in addition to your application's primary configuration**. Other put: `@TestConfiguration` does not prevent auto-detection of `@SpringBootConfiguration`.
    - When placed on a top-level class, `@TestConfiguration` indicates that classes in `src/test/java` should not be picked up by scanning. You can then import that class explicitly where it is required.

      ```java
      @TestConfiguration
      class IntegrationTestMockingConfig {
          private DetachedMockFactory factory = new DetachedMockFactory()

          @Bean
          ExternalRankingService externalRankingService() {
              factory.Mock(ExternalRankingService)
          }
      }

      /*...*/

      @RunWith(SpringRunner.class)
      @SpringBootTest
      @Import(IntegrationTestMockingConfig.class)
      public class MyTests {

        @Test
        public void exampleTest() {
          ...
        }

      }
      ```

- Configure only slices

  - Spring Boot’s auto-configuration system works well for applications but can sometimes be a little too much for tests. It often helps to load only the parts of the configuration that are required to test a “slice” of your application.
  - The `spring-boot-test-autoconfigure` module includes a number of annotations that can be used to automatically configure such “slices”. Each of them works in a similar way, providing a `@…​Test` annotation that loads the `ApplicationContext` and one or more `@AutoConfigure…​` annotations that can be used to customize auto-configuration settings.

  - **Auto-configured JSON Tests**

    - To test that object JSON serialization and deserialization is working as expected, you can use the `@JsonTest` annotation. `@JsonTest` auto-configures the available supported JSON mapper (Jackson, Gson, Jsonb etc.)
    - The `JacksonTester`, `GsonTester`, `JsonbTester`, and `BasicJsonTester` classes can be used for Jackson, Gson, Jsonb, and Strings respectively.

    ```java
    @RunWith(SpringRunner.class)
    @JsonTest
    public class MyJsonTests {

      @Autowired
      private JacksonTester<VehicleDetails> json;

      @Test
      public void testSerialize() throws Exception {
        VehicleDetails details = new VehicleDetails("Honda", "Civic");
        // Assert against a `.json` file in the same package as the test
        assertThat(this.json.write(details)).isEqualToJson("expected.json");
        // Or use JSON path based assertions
        assertThat(this.json.write(details)).hasJsonPathStringValue("@.make");
        assertThat(this.json.write(details)).extractingJsonPathStringValue("@.make")
            .isEqualTo("Honda");
      }

      @Test
      public void testDeserialize() throws Exception {
        String content = "{\"make\":\"Ford\",\"model\":\"Focus\"}";
        assertThat(this.json.parse(content))
            .isEqualTo(new VehicleDetails("Ford", "Focus"));
        assertThat(this.json.parseObject(content).getMake()).isEqualTo("Ford");
      }

    }
    ```

  - **Testing controllers**

    - To test whether Spring MVC controllers are working as expected, use the `@WebMvcTest` annotation. `@WebMvcTest` auto-configures the Spring MVC infrastructure and limits scanned beans to `@Controller`, `@ControllerAdvice`, `@JsonComponent`, `Converter`, `GenericConverter`, `Filter`, `WebMvcConfigurer`, and `HandlerMethodArgumentResolver`.
    - If you need to register extra components, such as the Jackson Module, you can import additional configuration classes by using `@Import` on your test.
    - Often, `@WebMvcTest` is limited to a single controller and is used in combination with `@MockBean` **to provide mock implementations for required collaborators**.
    - (see example above in previous `WebMockTest` example)
    - If you use HtmlUnit or Selenium, auto-configuration also provides an HTMLUnit `WebClient` bean and/or a `WebDriver` bean.
    - By default, Spring Boot puts `WebDriver` beans in a special “scope” to ensure that the driver exits after each test and that a new instance is injected. If you do not want this behavior, you can add `@Scope("singleton")` to your `WebDriver` `@Bean` definition.

      ```java
      @RunWith(SpringRunner.class)
      @WebMvcTest(UserVehicleController.class)
      public class MyHtmlUnitTests {

        @Autowired
        private WebClient webClient;

        @MockBean
        private UserVehicleService userVehicleService;

        @Test
        public void testExample() throws Exception {
          given(this.userVehicleService.getVehicleDetails("sboot"))
              .willReturn(new VehicleDetails("Honda", "Civic"));
          HtmlPage page = this.webClient.getPage("/sboot/vehicle.html");
          assertThat(page.getBody().getTextContent()).isEqualTo("Honda Civic");
        }
      }
      ```

  - **Data JPA Tests**

    - You can use the `@DataJpaTest` annotation to test JPA applications. By default, it configures an in-memory embedded database, scans for `@Entity` classes, and configures Spring Data JPA repositories. Regular `@Component` beans are not loaded into the `ApplicationContext`.
    - By default, data JPA tests are transactional and roll back at the end of each test. You can disable this by `@Transactional(propagation = Propagation.NOT_SUPPORTED)`.
    - Such tests are useful if you want to test your custom functions (such as custom query functions).
    - If you prefer your test to run against a real database, you can use the `@AutoConfigureTestDatabase` annotation. (i.e. `@AutoConfigureTestDatabase(replace=Replace.NONE)`)

    ```java
    @RunWith(SpringRunner.class)
    @DataJpaTest
    public class ExampleRepositoryTests {

      @Autowired
      private TestEntityManager entityManager;

      @Autowired
      private UserRepository repository;

      @Test
      public void testExample() throws Exception {
        this.entityManager.persist(new User("sboot", "1234"));
        User user = this.repository.findByUsername("sboot");
        assertThat(user.getUsername()).isEqualTo("sboot");
        assertThat(user.getVin()).isEqualTo("1234");
      }
    }
    ```

  - **JDBC Tests**

    - `@JdbcTest` is similar to `@DataJpaTest` but is for tests that only require a DataSource and do not use Spring Data JDBC. By default, it configures an in-memory embedded database and a `JdbcTemplate`. Regular `@Component` beans are not loaded into the `ApplicationContext`.
    - By default, JDBC tests are transactional and roll back at the end of each test.

  - **MongoDB Tests**

    - By default, it configures an in-memory embedded MongoDB (if available), configures a MongoTemplate, scans for `@Document` classes, and configures Spring Data MongoDB repositories. Regular `@Component` beans are not loaded into the `ApplicationContext`.
    - To run tests against real db: `@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)`

    ```java
    @RunWith(SpringRunner.class)
    @DataMongoTest
    public class ExampleDataMongoTests {

      @Autowired
      private MongoTemplate mongoTemplate;

      //
    }
    ```

  - **REST Clients**

    - You can use the `@RestClientTest` annotation to test REST clients. By default, it auto-configures Jackson, GSON, and Jsonb support, configures a `RestTemplateBuilder`, and adds support for `MockRestServiceServer`. Regular `@Component` beans are not loaded into the `ApplicationContext`.

    ```java
    @RunWith(SpringRunner.class)
    @RestClientTest(RemoteVehicleDetailsService.class)
    public class ExampleRestClientTest {

      @Autowired
      private RemoteVehicleDetailsService service;

      @Autowired
      private MockRestServiceServer server;

      @Test
      public void getVehicleDetailsWhenResultIsSuccessShouldReturnDetails()
          throws Exception {
        this.server.expect(requestTo("/greet/details"))
            .andRespond(withSuccess("hello", MediaType.TEXT_PLAIN));
        String greeting = this.service.callRestService();
        assertThat(greeting).isEqualTo("hello");
      }
    }
    ```

- Context caching

  - A nice (**but sometimes confusing**) feature of the Spring Test support is that the **application context is cached in between tests**, so if you have multiple methods in a test case, or multiple test cases with the same configuration, they only incur the cost of starting the application once. You can control the cache using the `@DirtiesContext` annotation.
  - The use of `@MockBean` or `@SpyBean` influences the cache key, which will most likely increase the number of contexts.

## Test Utilities

- ConfigFileApplicationContextInitializer

  - `ConfigFileApplicationContextInitializer` is an `ApplicationContextInitializer` that you can apply to your tests to load Spring Boot `application.properties` files. You can use it when you do not need the full set of features provided by `@SpringBootTest`
  - Using `ConfigFileApplicationContextInitializer` alone does not provide support for `@Value("${…​}")` injection. Its only job is to ensure that `application.properties` files are loaded into Spring’s Environment. For `@Value` support, you need to either additionally configure a `PropertySourcesPlaceholderConfigurer` or use `@SpringBootTest`, which auto-configures one for you.

  ```java
  @ContextConfiguration(classes = Config.class,
  		  initializers = ConfigFileApplicationContextInitializer.class)
  ```

- TestPropertyValues

  - `TestPropertyValues` lets you quickly add properties to a `ConfigurableEnvironment` or `ConfigurableApplicationContext`.

  ```java
  TestPropertyValues.of("org=Spring", "name=Boot").applyTo(env);
  ```

- OutputCapture

  - `OutputCapture` is a JUnit `Rule` that you can use to capture `System.out` and `System.err` output. You can declare the capture as a `@Rule` and then use `toString()` for assertions, as follows:

    ```java
    /*JUnit 4*/
    public class MyTest {

      @Rule
      public OutputCapture capture = new OutputCapture();

      @Test
      public void testName() throws Exception {
        System.out.println("Hello World!");
        assertThat(capture.toString(), containsString("World"));
      }
    }
    ```

  - In Junit5 to enable it, simply set the `junit.platform.output.capture.stdout` and/or `junit.platform.output.capture.stderr` configuration parameter to `true`
  - If enabled, the JUnit Platform captures the corresponding output and publishes it as a report entry using the `stdout` or `stderr` keys to all registered `TestExecutionListener` instances immediately before reporting the test or container as finished.

- TestRestTemplate

  - `TestRestTemplate` is a convenience alternative to Spring's `RestTemplate` that is useful in integration tests. You can get a vanilla template or one that sends Basic HTTP authentication (with a username and password). In either case, the template behaves in a test-friendly way by not throwing exceptions on server-side errors. It is recommended, but not mandatory, to use the Apache HTTP Client (version 4.3.2 or better). If you have that on your classpath, the `TestRestTemplate` responds by configuring the client appropriately.
  - Alternatively, if you use the `@SpringBootTest` annotation with `WebEnvironment.RANDOM_PORT` or `WebEnvironment.DEFINED_PORT`, you can inject a fully configured `TestRestTemplate` and start using it.

  ```java
  public class MyTest {
    private TestRestTemplate template = new TestRestTemplate();
    @Test
    public void testRequest() throws Exception {
      HttpHeaders headers = this.template.getForEntity(
          "http://myhost.example.com/example", String.class).getHeaders();
      assertThat(headers.getLocation()).hasHost("other.example.com");
    }
  }
  ```
