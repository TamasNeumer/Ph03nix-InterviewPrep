# JUnit 5

## Intro

- **Text Fixture:** A test fixture is a fixed state of a set of objects used as a baseline for running tests. The purpose of a test fixture is to ensure that there is a well known and fixed environment in which tests are run so that results are repeatable.
- **Unit tests:** A unit test is a piece of code written by a developer that executes a specific functionality in the code to be tested and asserts a certain behaviour or state.
- **Integration test:** An integration test aims to test the behavior of a component or the integration between a set of components. The term functional test is sometimes used as synonym for integration test. Integration tests check that the whole system works as intended, therefore they are reducing the need for intensive manual tests.
- **Performance test:** Performance tests are used to benchmark software components repeatedly. Their purpose is to ensure that the code under test runs fast enough even if it’s under high load.

- **Behavior vs state testing:**
  - A test is a behavior test (also called interaction test) if it checks if certain methods were called with the correct input parameters. A behavior test does not validate the result of a method call.
  - State testing is about validating the result.

- **Test location:** The standard convention from the Maven and Gradle build tools is to use:
  - src/main/java - for Java classes
  - src/test/java - for test classes

- **Naming conventions:**
  - A widely-used solution for classes is to use the "Test" suffix at the end of test classes names.
  - As a general rule, a test name should explain what the test does. If that is done correctly, reading the actual implementation can be avoided. One possible convention is to use the "should" in the test method name. For example, "ordersShouldBeCreated" or "menuShouldGetActive". Another approach is to use **"Given[ExplainYourInput]When[WhatIsDone]Then[ExpectedResult]"** for the display name of the test method.

## The Test Pyramid

- The test pyramid is a way of thinking about different kinds of automated tests should be used to create a balanced portfolio. Its essential point is that you should have many more low-level UnitTests than high level BroadStackTests running through a GUI.

  ![Pyramid](https://martinfowler.com/bliki/images/testPyramid/test-pyramid.png)

- **UI**
  - UI tests are fragile - you often "record" a series of clicks or events but as the UI change these tests are not usable anymore. In short, tests that run end-to-end through the UI are: brittle, expensive to write, and time consuming to run.
- **Service**
  - These can provide many of the advantages of end-to-end tests but avoid many of the complexities of dealing with UI frameworks. In web applications this would correspond to testing through an API layer while the top UI part of the pyramid would correspond to tests using something like Selenium or Sahi.
- **Unit tests**
  - Smallest tests, where each unit test focuses only on a single code snippet.

- Note that for this to work the Test class has to be next to the main, or it has to be added to the class path.

## Junit5 Annotations

**JUnit 5**  |  **Description**
--|--
`import org.junit.*`  |  Import statement for using the following annotations.
`@Test`  |  Identifies a method as a test method.
`@BeforeEach`  |  Executed before each test. It is used to prepare the test environment (e.g., read input data, initialize the class).
`@AfterEach`  |  Executed after each test. It is used to cleanup the test environment (e.g., delete temporary data, restore defaults). It can also save memory by cleaning up expensive memory structures.
`@BeforeAll`  |  Executed once, before the start of all tests. It is used to perform time intensive activities, for example, to connect to a database. Methods marked with this annotation need to be defined as static to work with JUnit.
`@AfterAll`  |  Executed once, after all tests have been finished. It is used to perform clean-up activities, for example, to disconnect from a database. Methods annotated with this annotation need to be defined as static to work with JUnit.
`@Disabled or @Disabled("Why disabled")`  |  Marks that the test should be disabled. This is useful when the underlying code has been changed and the test case has not yet been adapted.
`@Test (expected = Exception.class)`  |  Fails if the method does not throw the named exception.
`@Test(timeout=100)`  |  Fails if the method takes longer than 100 milliseconds.
`@ParameterizedTest` | Denotes that a method is a parameterized test.
`@RepeatedTest(n)` | Denotes that a method is a test template for a repeated test. -> Test executed n times.
`@TestFactory` | Denotes that a method is a test factory for dynamic tests.
`@TestInstance` | Used to configure the test instance lifecycle for the annotated test class.
`@TestTemplate` | Denotes that a method is a template for test cases designed to be invoked multiple times depending on the number of invocation contexts returned by the registered providers.
`@DisplayName("String/ unicode emoji")` | Declares a custom display name for the test class or test method. Such annotations are not inherited.
`@Description` | Add description to test
`@Nested` | Denotes that the annotated class is a nested, non-static test class. `@BeforeAll` and `@AfterAll` methods cannot be used directly in a `@Nested` test class unless the "per-class" test instance lifecycle is used.
`@Tag` | Used to declare tags for filtering tests, either at the class or method level; analogous to test groups in TestNG or Categories in JUnit 4.

## Test Instance Lifecycle

- In order to allow individual test methods to be executed in isolation and to avoid unexpected side effects due to mutable test instance state, **JUnit creates a new instance of each test class before executing each test method.**
- Please note that the **test class will still be instantiated if a given test method is disabled via a condition** (e.g., `@Disabled`, `@DisabledOnOs`, etc.) even when the "per-method" test instance lifecycle mode is active.
- `@TestInstance(Lifecycle.PER_CLASS)` -> use same test class

## JUnit most used assertions

- `assertTrue()`, `assertFalse()`
- `assertEquals` --> Note: for arrays the reference is checked not the content of the arrays.
- `assertNull()`, `assertNotNull()`
- `assertSame()`, `assertNotSame()` --> Checks that both variables refer to the same/different object.
- `assertAll()`
  - The interesting thing about assertAll is that it always checks all of the assertions that are passed to it, no matter how many fail.
  - It is best used for asserting a set of properties that belong together conceptionally.
    ```java
    Address address = unitUnderTest.methodUnderTest();
    assertAll("Should return address of Oracle's headquarter",
        () -> assertEquals("Redwood Shores", address.getCity()),
        () -> assertEquals("Oracle Parkway", address.getStreet()),
        () -> assertEquals("500", address.getNumber())
    );
    ```

- Exception testing
    ```java
    @Test
        void exceptionTesting() {
            Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
                throw new IllegalArgumentException("a message");
            });
            assertEquals("a message", exception.getMessage());
        }
    ```
- Timeout testing
    ```java
      assertTimeout(ofMillis(10), () -> {
                // Simulate task that takes more than 10 ms.
                Thread.sleep(100);
            });
    ```

## Conditional test execution

### Disable

- The `@Disabled` annotation allow to statically disable/ignore a test. 

### Assumptions

- Alternatively you can use `Assume.assumeFalse` or `Assume.assumeTrue` to define a condition for the test. For example, the following disables a test on Linux:
  - `Assume.assumeFalse(System.getProperty("os.name").contains("Linux"));`
- Assumptions is a collection of utility methods that support conditional test execution based on assumptions.
- In direct contrast to failed assertions, failed assumptions do not result in a test failure; rather, a failed assumption results in a test being aborted.
- Assumptions are typically used whenever it does not make sense to continue execution of a given test method — *for example, if the test depends on something that does not exist in the current runtime environment*.
    ```java
    @Test
    void testOnlyOnCiServer() {
        assumeTrue("CI".equals(System.getenv("ENV")));
        // remainder of test - skipped if assumption broken
    }

    @Test
    void testInAllEnvironments() {
        assumingThat("CI".equals(System.getenv("ENV")),
            () -> {
                // perform these assertions only on the CI server
                assertEquals(2, 2);
            });

        // perform these assertions in all environments
        assertEquals("a string", "a string");
    }
    ```

### Operating System Conditions

- The following annotations can be hanged above methods and classes:
- `@EnabledOnOs(MAC)`, `@EnabledOnOs({ LINUX, MAC })`
- `@DisabledOnOs(WINDOWS)`
- Or you can create your own annotation:
    ```java
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Test
    @EnabledOnOs(MAC)
    @interface TestOnMac {
    }
    ```

### JRE conditions

- `@EnabledOnJre(JAVA_8)`, `@EnabledOnJre({ JAVA_9, JAVA_10 })`

### System Property Conditions

- `@EnabledIfSystemProperty(named = "os.arch", matches = ".*64.*")`
- `@DisabledIfSystemProperty(named = "ci-server", matches = "true")`

### Environment variable conditions

- `@EnabledIfEnvironmentVariable(named = "ENV", matches = "staging-server")`
- `@DisabledIfEnvironmentVariable(named = "ENV", matches = ".*development.*")`

### Script-based Conditions

- JUnit Jupiter provides the ability to either enable or disable a container or test depending on the evaluation of a script configured via the `@EnabledIf` or `@DisabledIf` annotation. Scripts can be written in JavaScript, Groovy, or any other scripting language for which there is support for the Java Scripting API, defined by JSR 223.

## Tagging and Filtering

- Test classes and methods can be tagged via the `@Tag` annotation. Those tags can later be used to filter test discovery and execution.
    ```java
    @Tag("fast")
    @Tag("model")
    class TaggingDemo {
        @Test
        @Tag("taxes")
        void testingTaxCalculation() {
        }
    }
    ```
- Later you can create test plans using `@IncludeTags` and `@ExcludeTags` annotations.
    ```java
    @RunWith(JUnitPlatform.class)
    @SelectPackages("com.howtodoinjava.junit5.examples")
    @IncludeTags({"production","development"})
    public class JUnit5Example
    {
    }
    ```

## Nesting tests

- Nested tests give the test writer more capabilities to express the relationship among several group of tests.
    ```java
    @DisplayName("A stack")
    class TestingAStackDemo {

        Stack<Object> stack;

        @Test
        @DisplayName("is instantiated with new Stack()")
        void isInstantiatedWithNew() {
            new Stack<>();
        }

        @Nested
        @DisplayName("when new")
        class WhenNew {

            @BeforeEach
            void createNewStack() {
                stack = new Stack<>();
            }

            @Test
            @DisplayName("is empty")
            void isEmpty() {
                assertTrue(stack.isEmpty());
            }

            /*...*/
        }
    }
    ```

## Dependency Injection for Constructors and Methods

### Parameter Resolver

- `ParameterResolver` defines the API for test extensions that wish to dynamically resolve parameters at runtime. If a test constructor or a `@Test, @TestFactory, @BeforeEach, @AfterEach, @BeforeAll, or @AfterAll` method accepts a parameter, the parameter must be resolved at runtime by a registered ParameterResolver.
- There are currently three built-in resolvers that are registered automatically:
  - `TestInfoParameterResolver`
    - If a method parameter is of type `TestInfo`, the `TestInfoParameterResolver` will supply an instance of `TestInfo` corresponding to the current test as the value for the parameter.
    - The `TestInfo` can then be used to retrieve information about the current test such as the test’s display name, the test class, the test method, or associated tags.

    ```java
    @DisplayName("TestInfo Demo")
    class TestInfoDemo {
        // Test constructor example.
        TestInfoDemo(TestInfo testInfo) {
            assertEquals("TestInfo Demo", testInfo.getDisplayName());
        }

        // testInfo.getDisplayName() -> the method's name that will be executed
        @BeforeEach
        void init(TestInfo testInfo) {
            String displayName = testInfo.getDisplayName();
            assertTrue(displayName.equals("TEST 1") || displayName.equals("test2()"));
        }

        @Test
        @DisplayName("TEST 1")
        @Tag("my-tag")
        void test1(TestInfo testInfo) {
            assertEquals("TEST 1", testInfo.getDisplayName());
            assertTrue(testInfo.getTags().contains("my-tag"));
        }

        @Test
        void test2() {
        }
    }
    ```

### RepetitionInfoParameterResolver

- RepetitionInfo can then be used to retrieve information about the current repetition and the total number of repetitions for the corresponding `@RepeatedTest`.
  
### TestReporterParameterResolver

- The `TestReporter` can be used to publish additional data about the current test run. The data can be consumed through `TestExecutionListener.reportingEntryPublished()` and thus be viewed by IDEs or included in reports.
- In JUnit Jupiter you should use `TestReporter` where you used to print information to `stdout` or `stderr` in JUnit 4. Using `@RunWith`(`JUnitPlatform.class`) will even output all reported entries to stdout.

```java
class TestReporterDemo {
    @Test
    void reportSingleValue(TestReporter testReporter) {
        testReporter.publishEntry("a status message");
    }

    @Test
    void reportKeyValuePair(TestReporter testReporter) {
        testReporter.publishEntry("a key", "a value");
    }

    @Test
    void reportMultipleKeyValuePairs(TestReporter testReporter) {
        testReporter.publishEntry(
            Map.of(
                "user name", "dk38",
                "award year", "1974"
            ));
    }
}
```

## Test Interfaces and Default Methods

- JUnit Jupiter allows `@Test, @RepeatedTest, @ParameterizedTest, @TestFactory, @TestTemplate, @BeforeEach, and @AfterEach` to be declared on interface default methods.
- `@BeforeAll` and `@AfterAll` can either be declared on static methods in a test interface or on interface default methods if the test interface or test class is annotated with `@TestInstance(Lifecycle.PER_CLASS)`

```java
@TestInstance(Lifecycle.PER_CLASS)
interface TestLifecycleLogger {
    static final Logger LOG = Logger.getLogger(TestLifecycleLogger.class.getName());

    @BeforeAll
    default void beforeAllTests() {
        LOG.info("Before all tests");
    }

    @AfterAll
    default void afterAllTests() {
        LOG.info("After all tests");
    }

    @BeforeEach
    default void beforeEachTest(TestInfo testInfo) {
        LOG.info(() -> String.format("About to execute [%s]",
            testInfo.getDisplayName()));
    }

    @AfterEach
    default void afterEachTest(TestInfo testInfo) {
        LOG.info(() -> String.format("Finished executing [%s]",
            testInfo.getDisplayName()));
    }
}

interface TestInterfaceDynamicTestsDemo {
    @TestFactory
    default Collection<DynamicTest> dynamicTestsFromCollection() {
        return Arrays.asList(
            dynamicTest("1st dynamic test in test interface", () -> assertTrue(true)),
            dynamicTest("2nd dynamic test in test interface", () -> assertEquals(4, 2 * 2))
        );
    }
}

@Tag("timed")
@ExtendWith(TimingExtension.class)
interface TimeExecutionLogger {
}

class TestInterfaceDemo implements TestLifecycleLogger,
        TimeExecutionLogger, TestInterfaceDynamicTestsDemo {
    @Test
    void isEqualValue() {
        assertEquals(1, 1, "is always equal");
    }
}

:junitPlatformTest
INFO  example.TestLifecycleLogger - Before all tests
INFO  example.TestLifecycleLogger - About to execute [dynamicTestsFromCollection()]
INFO  example.TimingExtension - Method [dynamicTestsFromCollection] took 13 ms.
INFO  example.TestLifecycleLogger - Finished executing [dynamicTestsFromCollection()]
INFO  example.TestLifecycleLogger - About to execute [isEqualValue()]
INFO  example.TimingExtension - Method [isEqualValue] took 1 ms.
INFO  example.TestLifecycleLogger - Finished executing [isEqualValue()]
INFO  example.TestLifecycleLogger - After all tests
```

## Parameterized test

- This class can contain **one** test method and this method is executed with the different parameters provided.
- You mark a test class as a parameterized test with the ``@RunWith(Parameterized.class)`` annotation.
- Such a test class must contain a static method annotated with the ``@Parameters`` annotation. This method generates and returns a collection of arrays. Each item in this collection is used as parameter for the test method.
- Steps:
  - You define the variables that you use (``public int m1;``).
  - You order a parameter number to each varable (``@Parameter(0)``). This means that the 0-th indexed element of the parameter array is injected to the variable.
  - You create the function, that returns the test-cases. (Arrays of arrays)
  - Write the test functions using the variables defined in #1 point.

    ```java
    @RunWith(Parameterized.class)
    public class ParameterizedTestFields {

        // fields used together with @Parameter must be public
        @Parameter(0)
        public int m1;
        @Parameter(1)
        public int m2;
        @Parameter(2)
        public int result;

        // creates the test data
        @Parameters
        public static Collection<Object[]> data() {
            Object[][] data = new Object[][] { { 1 , 2, 2 }, { 5, 3, 15 }, { 121, 4, 484 } };
            return Arrays.asList(data);
        }

        @Test
        public void testMultiplyException() {
            MyClass tester = new MyClass();
            assertEquals(result, tester.multiply(m1, m2));
        }

        // class to be tested
        class MyClass {
            public int multiply(int i, int j) {
                return i *j;
            }
        }
    }
    ```

## Test suites

- You can combine multiple test classes into test suits. Running a test suite executes all test classes in that suite in the specified order.

  ```java
  @RunWith(Suite.class)
  @Suite.SuiteClasses({
    TestFeatureLogin.class,
    TestFeatureLogout.class,
    TestFeatureNavigate.class,
    TestFeatureUpdate.class
  })

  public class FeatureTestSuite {
    // the class remains empty,
    // used only as a holder for the above annotations
  }
  ```

## JUnit Rules

- A JUnit rule is a component that intercepts test method calls and allows us to do something before a test method is invoked and after a test method has been invoked.
- For example, the TemporaryFolder class allows to setup files and folders which are automatically removed after each test run.

  ```java
  public class RuleTester {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testUsingTempFolder() throws IOException {
      File createdFolder = folder.newFolder("newfolder");
      File createdFile = folder.newFile("myfilefile.txt");
      assertTrue(createdFile.exists());
    }
  }
  ```

- With rules you can "expect" an exception and their error messages. Or even test the properties!

  ```java
  public class RuleExceptionTesterExample {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void throwsIllegalArgumentExceptionIfIconIsNull() {
      exception.expect(IllegalArgumentException.class);
      exception.expectMessage("Negative value not allowed");
      exception.expect(MPCardException.class);
      exception.expect(hasProperty("mpCardErrorStatus", hasProperty("code", is(70610))));
      ClassToBeTested t = new ClassToBeTested();
      t.methodToBeTest(-1);
    }
  }
  ```

## Categories

- You can add "categories" to individual tests or to complete classes. Then you can exclude / include them based on these categories.
- ``@Category({ SlowTests.class, FastTests.class })`` --> category definition
- Execution
  - ``@IncludeCategory(SlowTests.class)`` --> Execute all "slow" tests
  - ``@SuiteClasses({ A.class, B.class })`` --> From these two classes

## Executing multiple tests in CI

- The ``org.junit.runner.JUnitCore`` class provides the ``runClasses()``method. This method allows you to run one or several tests classes. As a return parameter you receive an object of the type ``org.junit.runner.Result.`` This object can be used to retrieve information about the tests.

  ```java
  public class MyTestRunner {
    public static void main(String[] args) {
      Result result = JUnitCore.runClasses(MyClassTest.class);
      for (Failure failure : result.getFailures()) {
        System.out.println(failure.toString());
      }
    }
  }
  ```

## Parameterized Tests (alä JUnit5 - `@ParameterizedTest`)

- experimental feature TBD