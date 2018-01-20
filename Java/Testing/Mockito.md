## Mockito

#### Using test-doubles
A unit test should test functionality in isolation. Side effects from other classes or the system should be eliminated for a unit test, if possible. This can be done via using test replacements (test doubles) for the real dependencies. Test doubles can be classified like the following:
- A **dummy** object is passed around but never used, i.e., its *methods are never called*. Such an object can for example be used to fill the parameter list of a method.
- **Fake objects** have working implementations, but are usually simplified. For example, they use an in memory database and not a real database. --> an object with limited capabilities (for the purposes of testing), e.g. a fake web service.
- A **stub** class is an *partial implementation* for an interface or class with the purpose of using an instance of this stub class during testing. Stubs usually don’t respond to anything outside what’s programmed in for the test. Stubs may also record information about calls. --> an object that provides predefined answers to method calls.
- A **mock** object is a dummy implementation for an interface or a class in which you define the output of certain method calls. Mock objects are configured to perform a certain behavior during a test. They typical record the interaction with the system and test can validate that. --> an object on which you set expectations.

#### Mockito
Mockito is a popular mock framework which can be used in conjunction with JUnit. Mockito allows you to create and configure mock objects. Using Mockito simplifies the development of tests for classes with external dependencies significantly.

If you use Mockito in tests you typically:
  1. Mock away external dependencies and insert the mocks into the code under test
  1. Execute the code under test
  1. Validate that the code executed correctly

#### Adding mockito to POM
```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <version>2.13.0</version>
</dependency>
```

#### Creating Mock objects
- Using the static ``mock()`` method.
- Using the ``@Mock`` annotation.
  - If you use the ``@Mock`` annotation, you must trigger the creation of annotated objects. The MockitoRule allows this. It invokes the static method ``MockitoAnnotations.initMocks(this)`` to populate the annotated fields.

#### First usage
- ``@Mock`` Tells Mockito to mock the databaseMock instance.
- ``@Rule`` Tells Mockito to create the mocks based on the ``@Mock`` annotation
- Instantiate the class under test using the created mock
- Execute some code of the class under
- Assert that the method call returned true
- Verify that the ``query`` method was called on the ``MyDatabase`` mock

```java
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MockitoTest {
    @Mock
    MyDatabase databaseMock;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void testQuery() {
        ClassToTest t  = new ClassToTest(databaseMock);
        boolean check = t.askDB("* from t");
        assertTrue(check);
        verify(databaseMock).query("* from t");
    }

    public class ClassToTest {
        MyDatabase myDatabase;

        public ClassToTest(MyDatabase myDatabase) {
            this.myDatabase = myDatabase;
        }

        public boolean askDB(String queryString){
            return myDatabase.query(queryString);
        }
    }

    public class MyDatabase {
        public boolean query(String query){
            return true;
        }
    }
}
```

- If you try this the test fails. Mockito allows to configure the return values of its mocks via a fluent API. Unspecified method calls return "empty" values:
  - null for objects
  - 0 for numbers
  - false for boolean
  - empty collections for collections
- Since the expected behaviour was not yet defined in the previous example, the mock returned false.

#### "when thenReturn" and "when thenThrow"
- Mocks can return different values depending on arguments passed into a method. The ``when(…​.).thenReturn(…​.)`` method chain is used to specify a a return value for a method call with pre-defined parameters.
- If you specify more than one value,	they are returned in the order of specification, until the last one is used. Afterwards the last	specified value	is returned.
  - By adding the line ``when(databaseMock.query("* from t")).thenReturn(true);`` to the function, the return will be true.
  - An example for providing multiple returns: ``when(i.next()).thenReturn("Mockito").thenReturn("rocks");``
  - Or throwing exception: ``when(properties.get(”Anddroid”)).thenThrow(new IllegalArgumentException(...));``

#### "doReturn when" and "doThrow when"
- It is useful for mocking methods which give an exception during a call, e.g., if you use use functionality like Wrapping Java objects with Spy. (See later!)
- The doThrow variant can be used for methods which return void to throw an exception. This usage is demonstrated by the following code snippet.

  ```java
  Properties properties = new Properties();
  Properties spyProperties = spy(properties);
  doReturn(“42”).when(spyProperties).get(”shoeSize”);
  String value = spyProperties.get(”shoeSize”);
  assertEquals(”42”, value);
  ```

#### Wrapping Java objects with Spy
- ``@Spy`` or the ``spy()`` method can be used to wrap a real object. Every call, unless specified otherwise, is delegated to the object.

  ```java
  @Test
  public void testLinkedListSpyWrong() {
      // Lets mock a LinkedList
      List<String> list = new LinkedList<>();
      List<String> spy = spy(list);

      // this does not work
      // real method is called so spy.get(0)
      // throws IndexOutOfBoundsException (list is still empty)
      when(spy.get(0)).thenReturn("foo");

      assertEquals("foo", spy.get(0));
  }

  @Test
  public void testLinkedListSpyCorrect() {
      // Lets mock a LinkedList
      List<String> list = new LinkedList<>();
      List<String> spy = spy(list);

      // You have to use doReturn() for stubbing
      doReturn("foo").when(spy).get(0);

      assertEquals("foo", spy.get(0));
  }
  ```

#### Verifying calls on mock object
- Mockito keeps track of all the method calls and their parameters to the mock object. You can use the ``verify()`` method on the mock object to verify that the specified conditions are met.
- This kind of testing is sometimes called **behavior testing**. Behavior testing does not check the result of a method call, but it checks that a method is called with the right parameters.

  ```java
  // now check if method "testing" was called with the parameter 12
  verify(mockObj).testing(ArgumentMatchers.eq(12));

  // was the method called twice?
  verify(mockObj, times(2)).getUniqueId();

  // Some alternatives:
  verify(mockObj, never()).someMethod("never called");
  verify(mockObj, atLeastOnce()).someMethod("called at least once");
  verify(mockObj, atLeast(2)).someMethod("called at least twice");
  verify(mockObj, times(5)).someMethod("called five times");
  verify(mockObj, atMost(3)).someMethod("called at most 3 times");
  ```

#### Dependency Injection with Mockito
- Assume that the ``Example``'s constructor' takes a ``Delegate`` argument.
- Create these argument as a mock.
- Use the ``@InjectMocks`` annotation while defining ``Example`` object.
-


```java
@RunWith(MockitoJUnitRunner.class)
public class ExampleTest {

    @Mock
    Delegate delegateMock;

    @InjectMocks
    Example example;

    @Test
    public void testDoIt() {
        example.doIt();
        verify(delegateMock).execute();
    }
}
```

- Mockito supports dependency injection by using constructor arguments, setter methods and field injection. The underlying implementation relies on reflection, which means that there is no dependency on the Java EE 6 or Spring annotations as far as Mockito is concerned.
- Things to note:
  - The order of which the dependency injection is attempted is constructor injection, setter injection and lastly field injection.
  - Only one dependency injection strategy will occur for each test case. For example, if a suitable constructor is found, neither the setter injection nor the field injection will come to play.
  -  Mockito does not report if any dependency injection strategy fails.
  - For constructor injection the “biggest” constructor is chosen, and null will be passed as argument for dependencies that are neither mocks nor spies.

#### Capturing Arguments
- The ArgumentCaptor class allows to access the arguments of method calls during the verification.

  ```java
  public class MockitoTests {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Captor
    private ArgumentCaptor<List<String>> captor;

    @Test
    public final void shouldContainCertainListItem() {
        List<String> asList = Arrays.asList("someElement_test", "someElement");
        final List<String> mockedList = mock(List.class);
        mockedList.addAll(asList);

        verify(mockedList).addAll(captor.capture());
        final List<String> capturedArgument = captor.getValue();
        assertThat(capturedArgument, hasItem("someElement"));
    }
  }
  ```

Source: http://www.vogella.com/tutorials/Mockito/article.html
