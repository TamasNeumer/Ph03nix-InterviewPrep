# JUnit Questions

## [IntelliPaat](https://intellipaat.com/interview-question/junit-interview-questions/)
#### 1. What are (some) important features of JUnit?
- Open Source, Annotations to identify tests, Assertions to test expected behaviour, Test runners to execute tests.
- Tests can be organised into test suits, can be categorised and executed based on categories/suits.

#### 2. What is a Unit Test Case?
- A Unit Test Case is a part of code which ensures that the another part of code (method) works as expected.

#### 3. Why does JUnit only report the first failure in a single test?
- Reporting multiple failures in a single test is generally a sign that the test does too much and it is too big a unit test. JUnit is designed to work best with a number of small tests. It executes each test within a separate instance of the test class. It reports failure on each test.

#### 4. In Java, assert is a keyword. Won’t this conflict with JUnit’sassert() method?
- JUnit 3.7 deprecated assert() and replaced it with assertTrue(), which works exactly the same way. JUnit 4 is compatible with the assert keyword. If you run with the -ea JVM switch, assertions that fail will be reported by JUnit.

#### 5. What are JUnit classes? List some of them?
- Assert – A set of assert methods.
- Test Case – It defines the fixture to run multiple tests.
- Test Result – It collects the results of executing a test case.
- Test Suite – It is a Composite of Tests.

#### 6. What is a text fixture?
- A test fixture is a fixed state of a set of objects used as a baseline for running tests. Their purpose is to ensure that there is a well known and fixed environment in which tests are run so that results are repeatable. t includes following methods:
  - ``setUp()`` method which runs before every test invocation.
  - ``tearDown()`` method which runs after every test method.

## [TutorialsPoint](https://www.tutorialspoint.com/junit/junit_interview_questions.htm)

#### 1. Explain manual vs automated testing.
- Manual: Executing the test cases manually without any tool support is known as manual testing.
  - Time consuming (slower), less reliable, non-programmable
- Automated: Taking tool support and executing the test cases by using automation tool is known as automation testing.
  - Fast, more reliable, programmable

#### 2. When are Unit Tests written in Development Cycle?
- Tests are written before the code during development in order to help coders write the best code.

#### 3. How to install JUnit?
- Download the ``.jar``, add it to a folder which is referred as ``$JUNIT_HOME``. Finally add this path to your CLASSPATH: ``export CLASSPATH=$CLASSPATH:$JUNIT_HOME/junit.jar``

#### 4. What is a test suite?
- Test suite means bundle a few unit test cases and run it together. In JUnit, both ``@RunWith`` and ``@Suite`` annotation are used to run the suite test.

#### 5. What are annotations and how are they useful in JUnit?
- Annotations are like meta-tags that you can add to you code and apply them to methods or in class. The annotation in JUnit gives us information about test methods, which methods are going to run before & after test methods, which methods run before & after all the methods, which methods or class will be ignore during execution.

#### 6. How would you run JUnit from command line?
- Set CLASSPATH, invoke runner.

#### 7. What is the purpose of ``@Before`` and ``@After``, ``@BeforeClass``, ``@AfterClass`` annotations in JUnit?
- @Before/@After: Executed before and after each tests
- @BeofreClass/@AfterClass: Executed **once** before/after test class.

#### 8. What is the purpose of org.junit.JUnitCore class?
- The test cases are executed using JUnitCore class. JUnitCore is a facade for running tests. It supports running JUnit 4 tests, JUnit 3.8.x tests, and mixtures.

#### 9. How to simulate timeout situation in JUnit?
- Junit provides a handy option of Timeout. If a test case takes more time than specified number of milliseconds then Junit will automatically mark it as failed. The timeout parameter is used along with @Test annotation.
  - ``@Test(timeout=1000)``
  - ``@Rule public Timeout globalTimeout = Timeout.seconds(10);``

#### 10. How can you use JUnit to test that the code throws desired exception?
- ``@Test(expected = IndexOutOfBoundsException.class)``

#### 11. What are parametrized tests? How do you create a parametrized test?
- Junit 4 has introduced a new feature Parameterized tests. Parameterized tests allow developer to run the same test over and over again using different values.
- Annotate test class with ``@RunWith(Parameterized.class)``.
- Create a public static method annotated with ``@Parameters`` that returns a Collection of Objects (as Array) as test data set.
- Create an instance variable for each "column" of test data.
- Create your tests case(s) using the instance variables as the source of the test data.
- The test case will be invoked once per each row of data.

#### 12. What happens if a JUnit Test Method is Declared as "private"?
- If a JUnit test method is declared as "private", it compiles successfully. But the execution will fail. This is because JUnit requires that all test methods must be declared as "public".

#### 13. How do you test a "protected" method?
- When a method is declared as "protected", it can only be accessed within the same package where the class is defined. Hence to test a "protected" method of a target class, define your test class in the same package as the target class.

#### 14. How do you test a "private" method?
- When a method is declared as "private", it can only be accessed within the same class. So there is no way to test a "private" method of a target class from any test class. Hence you need to perform unit testing manually. Or you have to change your method from "private" to "protected".

#### 15. When are tests garbage collected?
- The test runner holds strong references to all Test instances for the duration of the test execution. This means that for a very long test run with many Test instances, none of the tests may be garbage collected until the end of the entire test run. Explicitly setting an object to null in the tearDown() method, for example, allows it to be garbage collected before the end of the entire test run.

#### 16. What is a Mock Object?
- In a unit test, mock objects can simulate the behaviour of complex, real (non-mock) objects and are therefore useful when a real object is impractical or impossible to incorporate into a unit test.

#### 17. Explain unit testing using Mock Objects.
- Create instances of mock objects.
- Set state and expectations in the mock objects.
- Invoke domain code with mock objects as parameters.
- Verify consistency in the mock objects.

## [DZone](https://dzone.com/articles/java-unit-testing-interview)
#### 1. Write a sample unit testing method for testing exception named as IndexOutOfBoundsException when working with ArrayList?

```java
@Test(expected=IndexOutOfBoundsException.class)
  public void outOfBounds() {
  new ArrayList<Object>().get(1);
}
```

#### 2. Write a sample unit testing method for testing timeout??

```java
@Test(timeout=100)
  public void infinity() {
  while(true);
}
```

#### 3. What are top 2-3 characteristics of Hard-to-test code?
- Long methods, Long conditionals, mixing concerns, bulky constructors


#### 4. What's the difference between faking, mocking, and stubbing?
- **Fake:** a class that implements an interface but contains fixed data and no logic. Simply returns "good" or "bad" data depending on the implementation. --> an object with limited capabilities (for the purposes of testing), e.g. a fake web service.
- **Mock:** a class that implements an interface and allows the ability to dynamically set the values to return/exceptions to throw from particular methods and provides the ability to check if particular methods have been called/not called. --> an object on which you set expectations.
- **Stub:** Like a mock class, except that it doesn't provide the ability to verify that methods have been called/not called. --> an object that provides predefined answers to method calls.

#### 5. What are top 2-3 advantages of mocking?
- 1. Verify interactions 2. Test the block of code in isolation
