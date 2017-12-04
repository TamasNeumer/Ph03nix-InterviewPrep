# JUnit

#### Intro
- **Text Fixture:** A test fixture is a fixed state in code which is tested used as input for a test.
- **Unit tests:** A unit test is a piece of code written by a developer that executes a specific functionality in the code to be tested and asserts a certain behavior or state.
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

#### Creating the first tests
- To define that a certain method is a test method, annotate it with the ``@Test`` annotation.
- You use an ``assert`` method, provided by JUnit or another assert framework, to check an expected result versus the actual result.

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

#### Junit4 Annotations
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

#### Parameterized test
- This class can contain **one** test method and this method is executed with the different parameters provided.
- You mark a test class as a parameterized test with the ``@RunWith(Parameterized.class)`` annotation.
- Such a test class must contain a static method annotated with the ``@Parameters`` annotation.


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
        assertEquals("Result", result, tester.multiply(m1, m2));
    }

    // class to be tested
    class MyClass {
        public int multiply(int i, int j) {
            return i *j;
        }
    }
}
```
