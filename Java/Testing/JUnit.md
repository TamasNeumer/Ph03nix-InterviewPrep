# JUnit

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

## Creating the first tests

- JUnit tests have their separate classes. (Usually in src/test/...). As a naming convention use the "Test" suffix.
- Import ``org.junit.Test`` to use the library.
- To define that a certain method is a test method, annotate it with the ``@Test`` annotation.
- You use an ``assert`` method (``import static org.junit.Assert.*``), provided by JUnit or another assert framework, to check an expected result versus the actual result.

  ```java
  public class MyTests {
      @Test
      public void multiplicationOfZeroIntegersShouldReturnZero() {
          MyClass tester = new MyClass(); // MyClass is tested

          // assert statements
          assertEquals(0, tester.multiply(10, 0), "10 x 0 must be 0");
          assertEquals(0, tester.multiply(0, 10), "0 x 10 must be 0");
          assertEquals(0, tester.multiply(0, 0), "0 x 0 must be 0");
      }
  }
  ```

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

- Note that for this to work the Test class has to be next to the main, or it has to be added to the class path.

## Junit4 Annotations
**JUnit 4**  |  **Description**
--|--
`import org.junit.*`  |  Import statement for using the following annotations.
`@Test`  |  Identifies a method as a test method.
`@Before`  |  Executed before each test. It is used to prepare the test environment (e.g., read input data, initialize the class).
`@After`  |  Executed after each test. It is used to cleanup the test environment (e.g., delete temporary data, restore defaults). It can also save memory by cleaning up expensive memory structures.
`@BeforeClass`  |  Executed once, before the start of all tests. It is used to perform time intensive activities, for example, to connect to a database. Methods marked with this annotation need to be defined as static to work with JUnit.
`@AfterClass`  |  Executed once, after all tests have been finished. It is used to perform clean-up activities, for example, to disconnect from a database. Methods annotated with this annotation need to be defined as static to work with JUnit.
`@Ignore or @Ignore("Why disabled")`  |  Marks that the test should be disabled. This is useful when the underlying code has been changed and the test case has not yet been adapted.
`@Test (expected = Exception.class)`  |  Fails if the method does not throw the named exception.
`@Test(timeout=100)`  |  Fails if the method takes longer than 100 milliseconds.

- The ``@Ignore`` annotation allow to statically ignore a test. Alternatively you can use ``Assume.assumeFalse`` or ``Assume.assumeTrue`` to define a condition for the test. For example, the following disables a test on Linux:
  - `Assume.assumeFalse(System.getProperty("os.name").contains("Linux"));`

#### JUnit Assertations

- `assertTrue()`, `assertFalse()`
- `assertEquals` --> Note: for arrays the reference is checked not the content of the arrays.
- `assertNull()`, `assertNotNull()`
- `assertSame()`, `assertNotSame()` --> Checks that both variables refer to the same/different object.

#### Test suites

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

#### Parameterized test

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

#### JUnit Rules

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

- With rules you can "expect" an exception and their error messages.

  ```java
  public class RuleExceptionTesterExample {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void throwsIllegalArgumentExceptionIfIconIsNull() {
      exception.expect(IllegalArgumentException.class);
      exception.expectMessage("Negative value not allowed");
      ClassToBeTested t = new ClassToBeTested();
      t.methodToBeTest(-1);
    }
  }
  ```

#### Categories

- You can add "categories" to individual tests or to complete classes. Then you can exclude / include them based on these categories.
- ``@Category({ SlowTests.class, FastTests.class })`` --> category definition
- Execution
  - ``@IncludeCategory(SlowTests.class)`` --> Execute all "slow" tests
  - ``@SuiteClasses({ A.class, B.class })`` --> From these two classes
