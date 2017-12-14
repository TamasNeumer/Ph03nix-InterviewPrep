## Mockito

#### Using test-doubles
A unit test should test functionality in isolation. Side effects from other classes or the system should be eliminated for a unit test, if possible. This can be done via using test replacements (test doubles) for the real dependencies. Test doubles can be classified like the following:
- A **dummy** object is passed around but never used, i.e., its *methods are never called*. Such an object can for example be used to fill the parameter list of a method.
- **Fake objects** have working implementations, but are usually simplified. For example, they use an in memory database and not a real database.
- A **stub** class is an *partial implementation* for an interface or class with the purpose of using an instance of this stub class during testing. Stubs usually don’t respond to anything outside what’s programmed in for the test. Stubs may also record information about calls.
- A **mock** object is a dummy implementation for an interface or a class in which you define the output of certain method calls. Mock objects are configured to perform a certain behavior during a test. They typical record the interaction with the system and test can validate that.

#### Mockito
Mockito is a popular mock framework which can be used in conjunction with JUnit. Mockito allows you to create and configure mock objects. Using Mockito simplifies the development of tests for classes with external dependencies significantly.

If you use Mockito in tests you typically:
  1. Mock away external dependencies and insert the mocks into the code under test
  1. Execute the code under test
  1. Validate that the code executed correctly

#### Creating Mock objects
- Using the static ``mock()`` method.
- Using the ``@Mock`` annotation.
