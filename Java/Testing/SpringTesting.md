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

  -

* Context caching

  - A nice (**but sometimes confusing**) feature of the Spring Test support is that the **application context is cached in between tests**, so if you have multiple methods in a test case, or multiple test cases with the same configuration, they only incur the cost of starting the application once. You can control the cache using the `@DirtiesContext` annotation.
  - The use of `@MockBean` or `@SpyBean` influences the cache key, which will most likely increase the number of contexts.
