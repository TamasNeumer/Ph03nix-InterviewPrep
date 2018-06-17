# Testing Microservices with Spring Boot

[Source](https://martinfowler.com/articles/practical-test-pyramid.html)

## Intro

- Stick to the pyramid shape to come up with a healthy, fast and maintainable test suite: Write lots of small and fast unit tests. Write some more coarse-grained tests and very few high-level tests that test your application from end to end.
- Don't become too attached to the names of the individual layers in Cohn's test pyramid. 

## Unit tests

- The foundation of your test suite will be made up of unit tests. Your unit tests make sure that a certain unit (your subject under test) of your codebase works as intended. Unit tests have the narrowest scope of all the tests in your test suite. The number of unit tests in your test suite will largely outnumber any other type of test.
- If you're working in a functional language a unit will most likely be a single function. Your unit tests will call a function with different parameters and ensure that it returns the expected values.
- In an object-oriented language a unit can range from a single method to an entire class.

### Sociable and Solitary

- Occasionally people label these two sorts of tests as solitary unit tests for tests that stub all collaborators and sociable unit tests for tests that allow talking to real collaborators.
- At the end of the day it's not important to decide if you go for solitary or sociable unit tests. Writing automated tests is what's important. Personally, I find myself using both approaches all the time. If it becomes awkward to use real collaborators I will use mocks and stubs generously. If I feel like involving the real collaborator gives me more confidence in a test I'll only stub the outermost parts of my service.

### What to unit test

- Simply stick to the one test class per production class rule of thumb and you're off to a good start.
- A unit test class should **at least test the public interface** of the class. Private methods can't be tested anyways since you simply can't call them from a different test class. Protected or package-private are accessible from a test class (given the package structure of your test class is the same as with the production class) but testing these methods could already go too far.
- There's a fine line when it comes to writing unit tests: **They should ensure that all your non-trivial code paths are tested (including happy path and edge cases). At the same time they shouldn't be tied to your implementation too closely.** Don't reflect your internal code structure within your unit tests. Test for observable behaviour instead.
- Private methods should generally be considered an implementation detail. That's why you shouldn't even have the urge to test them.

### Implementing Unit Tests

- Example: 
  ```java
  @RestController
  public class ExampleController {

      private final PersonRepository personRepo;

      @Autowired
      public ExampleController(final PersonRepository personRepo) {
          this.personRepo = personRepo;
      }

      @GetMapping("/hello/{lastName}")
      public String hello(@PathVariable final String lastName) {
          Optional<Person> foundPerson = personRepo.findByLastName(lastName);

          return foundPerson
                  .map(person -> String.format("Hello %s %s!",
                          person.getFirstName(),
                          person.getLastName()))
                  .orElse(String.format("Who is this '%s' you're talking about?",
                          lastName));
      }
  }

  public class ExampleControllerTest {

      private ExampleController subject;

      @Mock
      private PersonRepository personRepo;

      @Before
      public void setUp() throws Exception {
          initMocks(this);
          subject = new ExampleController(personRepo);
      }

      @Test
      public void shouldReturnFullNameOfAPerson() throws Exception {
          Person peter = new Person("Peter", "Pan");
          given(personRepo.findByLastName("Pan"))
              .willReturn(Optional.of(peter));

          String greeting = subject.hello("Pan");

          assertThat(greeting, is("Hello Peter Pan!"));
      }

      @Test
      public void shouldTellIfPersonIsUnknown() throws Exception {
          given(personRepo.findByLastName(anyString()))
              .willReturn(Optional.empty());

          String greeting = subject.hello("Pan");

          assertThat(greeting, is("Who is this 'Pan' you're talking about?"));
      }
  }
  ```

- The controller instance is called `subject`
- Both positive and negative outcomes are tested
- Mocks are initialized in the `@Before` block.
- We're writing the unit tests using JUnit, the de-facto standard testing framework for Java. We use Mockito to replace the real PersonRepository class with a stub for our test. This stub allows us to define canned responses the stubbed method should return in this test. Stubbing makes our test more simple, predictable and allows us to easily setup test data.
- Following the arrange, act, assert structure, we write two unit tests - a positive case and a case where the searched person cannot be found. The first, positive test case creates a new person object and tells the mocked repository to return this object when it's called with "Pan" as the value for the lastName parameter. The test then goes on to call the method that should be tested. Finally it asserts that the response is equal to the expected response.
- Also note how we are using JUnit Matchers.

## Integration Tests

- They test the integration of your application with all the parts that live outside of your application.
- If you're testing the integration with a database you need to run a database when running your tests. For testing that you can read files from a disk you need to save a file to your disk and load it in your integration test. --> **You have real collaborators instead of stubs**
- I like to treat integration testing more narrowly and test one integration point at a time by replacing separate services and databases with test doubles. Together with contract testing and running contract tests against test doubles as well as the real implementations you can come up with integration tests that are faster, more independent and usually easier to reason about.
- **Write integration tests for all pieces of code where you either serialize or deserialize data.** This happens more often than you might think. Think about:
  1. Calls to your services' REST API
  1. Reading from and writing to databases
  1. Calling other application's APIs
  1. Reading from and writing to queues
  1. Writing to the filesystem
- Integration test for DB (example):
  1. start a database
  1. connect your application to the database
  1. trigger a function within your code that writes data to the database
  1. check that the expected data has been written to the database by reading the data from the database
- **Run locally!**
  - When writing narrow integration tests you should aim to run your external dependencies locally: spin up a local MySQL database, test against a local ext4 filesystem. If you're integrating with a separate service either run an instance of that service locally or build and run a fake version that mimics the behaviour of the real service.
  - If there's no way to run a third-party service locally you should opt for running a dedicated test instance and point at this test instance when running your integration tests. **Avoid integrating with the real production system in your automated tests**. Blasting thousands of test requests against a production system is a surefire way to get people angry because you're cluttering their logs (in the best case) or even DoS 'ing their service (in the worst case). Integrating with a service over the network is a typical characteristic of a broad integration test and makes your tests slower and usually harder to write.

### Database Integration

- The PersonRepository is the only repository class in the codebase. It relies on Spring Data and has no actual implementation.

  ```java
  public interface PersonRepository extends CrudRepository<Person, String> {
      Optional<Person> findByLastName(String lastName);
  }
  ```

- You might argue that this is testing the framework and something that I should avoid as it's not our code that we're testing. Still it's worth to test whether the framework does what you expect.
- To make it easier for you to run the tests on your machine (without having to install a PostgreSQL database) our test connects to an **in-memory H2 database**.
  - I've defined H2 as a test dependency in the `build.gradle` file. - `testCompile('com.h2database:h2')` The `application.properties` in the test directory doesn't define any `spring.datasource` properties. Having no data-source defined, Spring Data will use an in-memory database. As it finds H2 on the classpath it simply uses H2 when running our tests.

  ```java
  @RunWith(SpringRunner.class)
  @DataJpaTest
  public class PersonRepositoryIntegrationTest {

      @Autowired
      private PersonRepository subject;

      @After
      public void tearDown() throws Exception {
          subject.deleteAll();
      }

      @Test
      public void shouldSaveAndFetchPerson() throws Exception {
          Person peter = new Person("Peter", "Pan");
          subject.save(peter);

          Optional<Person> maybePeter = subject.findByLastName("Pan");

          assertThat(maybePeter, is(Optional.of(peter)));
      }
  }
  ```

- When running the real application with the int profile (e.g. by setting `SPRING_PROFILES_ACTIVE=int` as environment variable) it connects to a PostgreSQL database as defined in the application-int.properties.
- **On top of that going with an in-memory database is risky business.** After all, our integration tests run against a different type of database than they would in production.
- You can see that our integration test follows the same *arrange, act, assert structure* as the unit tests. Told you that this was a universal concept!

### Integration With Separate Services

- Our microservice talks to darksky.net, a weather REST API. Of course we want to ensure that our service sends requests and parses the responses correctly.
- **We want to avoid hitting the real darksky servers when running automated tests.** The tests should run even if the darksky servers are down.
- We can avoid hitting the real darksky servers by running our own, fake darksky server while running our integration tests. This might sound like a huge task. Thanks to tools like **Wiremock** it's easy peasy.

  ```java
  @RunWith(SpringRunner.class)
  @SpringBootTest
  public class WeatherClientIntegrationTest {

      @Autowired
      private WeatherClient subject;

      @Rule
      public WireMockRule wireMockRule = new WireMockRule(8089);

      @Test
      public void shouldCallWeatherService() throws Exception {
          wireMockRule.stubFor(get(urlPathEqualTo("/some-test-api-key/53.5511,9.9937"))
                  .willReturn(aResponse()
                          .withBody(FileLoader.read("classpath:weatherApiResponse.json"))
                          .withHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                          .withStatus(200)));

          Optional<WeatherResponse> weatherResponse = subject.fetchWeather();

          Optional<WeatherResponse> expectedResponse = Optional.of(new WeatherResponse("Rain"));
          assertThat(weatherResponse, is(expectedResponse));
      }
  }
  ```

- To use Wiremock we instantiate a WireMockRule on a fixed port (8089). Using the DSL we can set up the Wiremock server, define the endpoints it should listen on and set canned responses it should respond with.
- It's important to understand how the test knows that it should call the fake Wiremock server instead of the real darksky API. The secret is in our `application.properties` file contained in src/test/resources. This is the properties file Spring loads when running tests. In this file we override configuration like API keys and URLs with values that are suitable for our testing purposes, e.g. calling the fake Wiremock server instead of the real one:
  - `weather.url = http://localhost:8089`
- Replacing the real weather API's URL with a fake one in our tests is made possible by injecting the URL in our WeatherClient class' constructor:

  ```java
  @Autowired
  public WeatherClient(final RestTemplate restTemplate,
                      @Value("${weather.url}") final String weatherServiceUrl,
                      @Value("${weather.api_key}") final String weatherServiceApiKey) {
      this.restTemplate = restTemplate;
      this.weatherServiceUrl = weatherServiceUrl;
      this.weatherServiceApiKey = weatherServiceApiKey;
  }
  ```

- Dilemma: What if the API changes but then still all of our tests pass? Solution: Running **contract tests against the fake and the real server** ensures that the fake we use in our integration tests is a faithful test double. Let's see how this works next.

## Contract Tests

- Interfaces between different applications can come in different shapes and technologies. Common ones are
  - REST and JSON via HTTPS
  - RPC using something like gRPC
  - building an event-driven architecture using queues
- For each interface there are two parties involved: the provider and the consumer. The **provider** serves data to consumers. The **consumer** processes data obtained from a provider.

### Consumer-Driven Contract tests

- Use Case: You have an organization in which one team is working on the API implementation, while the other team is consuming the API.
- **Process:** Using CDC, consumers of an interface write tests that check the interface for all data they need from that interface. The consuming team then publishes these tests so that the publishing team can fetch and execute these tests easily. The providing team can now develop their API by running the CDC tests. Once all tests pass they know they have implemented everything the consuming team needs.
- **Process**
  1. The consuming team writes automated tests with all consumer expectations
  1. They publish the tests for the providing team
  1. The providing team runs the CDC tests continuously and keeps them green
  1. Both teams talk to each other once the CDC tests break
- **Pact**
  - When working with CDC test Pact is a great open source library.

#### Consumer Test (our team)

- Our microservice consumes the weather API. So it's our responsibility to write a consumer test that defines our expectations for the contract (the API) between our microservice and the weather service.
- Gradle: `testCompile('au.com.dius:pact-jvm-consumer-junit_2.11:3.5.5')`

    ```java
    @RunWith(SpringRunner.class)
    @SpringBootTest
    public class WeatherClientConsumerTest {

        @Autowired
        private WeatherClient weatherClient;

        @Rule
        public PactProviderRuleMk2 weatherProvider =
                new PactProviderRuleMk2("weather_provider", "localhost", 8089, this);

        @Pact(consumer="test_consumer")
        public RequestResponsePact createPact(PactDslWithProvider builder) throws IOException {
            return builder
                    .given("weather forecast data")
                    .uponReceiving("a request for a weather request for Hamburg")
                        .path("/some-test-api-key/53.5511,9.9937")
                        .method("GET")
                    .willRespondWith()
                        .status(200)
                        .body(FileLoader.read("classpath:weatherApiResponse.json"),
                                ContentType.APPLICATION_JSON)
                    .toPact();
        }

        @Test
        @PactVerification("weather_provider")
        public void shouldFetchWeatherInformation() throws Exception {
            Optional<WeatherResponse> weatherResponse = weatherClient.fetchWeather();
            assertThat(weatherResponse.isPresent(), is(true));
            assertThat(weatherResponse.get().getSummary(), is("Rain"));
        }
    }
    ```
- If you look closely, you'll see that the WeatherClientConsumerTest is very similar to the WeatherClientIntegrationTest. Instead of using Wiremock for the server stub we use Pact this time. In fact the consumer test works exactly as the integration test, we replace the real third-party server with a stub, define the expected response and check that our client can parse the response correctly. In this sense the WeatherClientConsumerTest is a narrow integration test itself. **The advantage over the wiremock-based test is that this test generates a pact file** (found in `target/pacts/&pact-name>.json`) each time it runs.
- This pact file describes our expectations for the contract in a special JSON format. This pact file can then be used to verify that our stub server behaves like the real server. We can take the pact file and hand it to the team providing the interface. They take this pact file and write a provider test using the expectations defined in there. This way they test if their API fulfils all our expectations.
- In your real-world application you don't need both, an integration test and a consumer test for a client class. The sample codebase contains both to show you how to use either one. If you want to write CDC tests using pact I recommend sticking to the latter. The effort of writing the tests is the same. Using pact has the benefit that you automatically get a pact file with the expectations to the contract that other teams can use to easily implement their provider tests. Of course this only makes sense if you can convince the other team to use pact as well. If this doesn't work, using the integration test and Wiremock combination is a decent plan b.

#### Provider Test (the other team)

- The providing team gets the pact file and runs it against their providing service. To do so they implement a provider test that reads the pact file, stubs out some test data and runs the expectations defined in the pact file against their service.
- The pact folks have written several libraries for implementing provider tests. Their main GitHub repo gives you a nice overview which consumer and which provider libraries are available. Pick the one that best matches your tech stack.
- A hypothetical provider test that the darksky.net team would implement could look like this:

    ```java
    @RunWith(RestPactRunner.class)
    @Provider("weather_provider") // same as the "provider_name" in our clientConsumerTest
    @PactFolder("target/pacts") // tells pact where to load the pact files from
    public class WeatherProviderTest {
        @InjectMocks
        private ForecastController forecastController = new ForecastController();

        @Mock
        private ForecastService forecastService;

        @TestTarget
        public final MockMvcTarget target = new MockMvcTarget();

        @Before
        public void before() {
            initMocks(this);
            target.setControllers(forecastController);
        }

        @State("weather forecast data") // same as the "given()" in our clientConsumerTest
        public void weatherForecastData() {
            when(forecastService.fetchForecastFor(any(String.class), any(String.class)))
                    .thenReturn(weatherForecast("Rain"));
        }
    }
    ```

### UI Tests

- Yes, testing your application end-to-end often means driving your tests through the user interface. The inverse, however, is not true.
- Testing your user interface doesn't have to be done in an end-to-end fashion. Depending on the technology you use, testing your user interface can be as simple as writing some unit tests for your frontend javascript code with your backend stubbed out.
- With traditional web applications testing the user interface can be achieved with tools like **Selenium**. If you consider a REST API to be your user interface you should have everything you need by writing proper integration tests around your API.