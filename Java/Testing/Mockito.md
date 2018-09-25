# Mockito

## Review Questions

1. What is Mockito?
2. What is the difference between a dummy, fake object, stub and mock?

## Theory

### What is Mockito

- Mockito is a popular mock framework that allows you to create and configure mock objects.
- If you use Mockito in tests you typically:
  1. Mock away external dependencies and insert the mocks into the code under test
  2. Execute the code under test
  3. Validate that the code executed correctly

### Using test-doubles

- A unit test should test functionality in isolation --> Remove dependencies via mocks!
- A **dummy** object is passed around but never used, i.e., its *methods are never called*. Such an object can for example be used to fill the parameter list of a method.
- **Fake objects** have working implementations, but are usually simplified. For example, they use an in memory database and not a real database. --> an object with limited capabilities (for the purposes of testing), e.g. a fake web service.
- A **stub** class is an *partial implementation* for an interface or class with the purpose of using an instance of this stub class during testing. Stubs provide predefined answers to method calls.
- A **mock** object is a dummy implementation for an interface or a class in which you define the output of certain method calls. Mock objects **are configured to perform a certain behavior during a test**.  --> **Mocks in a way are determined at runtime** since the code that sets the expectations has to run before they do anything and that's the main difference compared to stubs!

### How to test properly

- Interaction that is **asking an object for data (or indirect input)** is basically providing necessary input for processing. This kind of interaction is **meant to be stubbed before** running code under test (exercising the behaviour).
- Interaction that is **telling an object to do something (or indirect output)** is basically the outcome of processing. This kind of interaction is **meant to be asserted after** running code under test.
- To put these principles in code:
    ```java
        // stub
        stub(repository.findArticle("foo")).toReturn(article);
        // run
        articleManager.deleteByHeadline("foo");
        // assert
        verify(repository).delete(article);
    ```
- As a general rule:
  - **If I stub** then it is verified for free, so **I don’t verify**.
  - **If I verify** then I don’t care about the return value, so **I don’t stub.** (Had I called `verify(repository).findArticle("foo")` I would have been interested in the working of the repository class and stubbing would have been redundant.) (See more on this at the stubbing example.)

## Creating and Using Mock Objects

### Creating and Verifying Mocks

#### Mocks

- There are two ways to instantiate mock objects:
  - Using the static `public static <T> T mock(Class<T> classToMock)` method.
  - Using the `@Mock` annotation over a given field.
    - When doing so you have to call the `MockitoAnnotations.initMocks(this);` method in the `@Before` section of your code!
    - **JUnit 4.5 runner** initializes mocks annotated with Mock, so that explicit usage of `MockitoAnnotations.initMocks(Object)` **is not necessary**. Mocks are initialized before each test method.
- By default, for all methods that return a value, a mock will return either `null`, a primitive/primitive wrapper value, or an empty collection, as appropriate. For example `0` for an int/Integer and `false` for a boolean/Boolean.

```java
@RunWith(JUnit4.class)
//...
@Mock
MyClassType myVar;
//...
```

- Mocking `final` types, `enum`s and `final` methods
  - This mock maker is **turned off by default**.
  - It can be activated explicitly by the mockito extension mechanism, just create in the classpath a file `/mockito-extensions/org.mockito.plugins.MockMaker` containing the value `mock-maker-inline`.
  - As a convenience, the Mockito team provides an artifact where this mock maker is preconfigured. Instead of using the `mockito-core artifact`, include the `mockito-inline` artifact in your project.

#### Verify

- `public static <T> T verify(T mock)`
  - Verifies certain behavior happened **once**. (Alias to `verify(mock, times(1))`)
- `public static <T> T verify(T mock, VerificationMode mode)`
  - `Interface VerificationMode` allows verifying that certain behavior happened at least once / exact number of times / never.
  - Implementers:
    - `Interface VerificationWithTimeout` -> `Timeout` (Implementing class)
      - `atLeast(int minNumberOfInvocations)`
      - `atLeastOnce()`
      - `only()`
      - `times(int wantedNumberOfInvocations)`
      - `verify(mock, timeout(100)).someMethod();`
    - `Interface VerificationAfterDelay` -> `After` (Implementing class)
      - VerificationAfterDelay is a VerificationMode that allows combining existing verification modes with an initial delay
      - `atLeast(int minNumberOfInvocations), atLeastOnce(), atMost(), never(), only(), times(int wantedNumberOfInvocations)`
        ```java
        verify(mock, after(100).atMost(5)).foo();
        verify(mock, after(100).never()).bar();
        verify(mock, after(200).atLeastOnce()).baz();
        ```
- Verify example:
  ```java
  //mock creation of INTERFACE
  List mockedList = mock(List.class);

  //using mock object
  mockedList.add("one");
  mockedList.clear();

  //verification that the functions have been called ONCE!
  verify(mockedList).add("one");
  verify(mockedList, times(1)).add("once");
  verify(mockedList).clear();
  verify(mockedList, never()).add("never happened");
  verify(mockedList, atLeastOnce()).add("once");
  verify(mockedList, atMost(5)).add("once");
  ```

### Stubbing

- `public static <T> OngoingStubbing<T> when(T methodCall)`
  - Enables stubbing methods. Use it when you want the mock to return particular value when particular method is called.
- Once stubbed, the method will always return a stubbed value, regardless of how many times it is called.
- As you can see the method returns an instance of `OngoingStubbing<T>`. An `OngoingStub` allows you to add the "then" part of the relation with methods as:
  - `thenReturn(T value)`
  - `thenThrow(Class<? extends Throwable> throwableType)`
  - `thenCallRealMethod()`- Sets the real implementation to be called when the method is called on a mock object.
  - `then(Answer<?> answer)` Sets a generic Answer for the method. This method is an alias of thenAnswer(Answer). This alias allows more readable tests on occasion, for example:
    ```java
    //using 'then' alias:
    when(mock.foo()).then(returnCoolValue());

    //versus good old 'thenAnswer:
    when(mock.foo()).thenAnswer(byReturningCoolValue());
    ```

- Stubbing examples:
  ```java
  // One liner stub:
  Car boringStubbedCar = when(mock(Car.class).shiftGear()).thenThrow(EngineNotStarted.class).getMock();

  //You can mock CONCRETE CLASS, not just interfaces
  LinkedList mockedList = mock(LinkedList.class);

  //stubbing
  when(mockedList.get(0)).thenReturn("first");
  when(mockedList.get(1)).thenThrow(new RuntimeException());

  //following prints "first"
  System.out.println(mockedList.get(0));

  //following throws runtime exception
  System.out.println(mockedList.get(1));

  //following prints "null" because get(999) was not stubbed
  System.out.println(mockedList.get(999));

  //Although it is possible to verify a stubbed invocation, usually it's just redundant
  //If your code cares what get(0) returns, then something else breaks (often even before verify() gets executed).
  //If your code doesn't care what get(0) returns, then it should not be stubbed.
  verify(mockedList).get(0);
  ```

### Argument matchers

- Mockito verifies argument values in natural java style: by using an `equals()` method. Sometimes, when extra flexibility is required then you might use **argument matchers**.

#### Any

- `static <T> T any()` - Matches anything, including nulls and varargs.
- `static <T> T any(Class<T> type)` - Matches any object of given type, *excluding nulls*.

#### Any Wrappers & Primitives

- `anyBoolean() anyByte() anyChar() anyDouble() anyFloat() anyInt() anyLong() anyShort() anyString()`
- These allow primitive or **non-null** wrapper
- Collections & Iterables
  - `anyCollection() anyIterable() anyMap() anyObject() anySet()`

#### ArgumentMatcher Functions

- These functions accept argument matchers - i.e. classes that implement the `Interface ArgumentMatcher<T>`'s `boolean matches(T t)` method.
- Examples:
  ```java
  class ListOfTwoElements implements ArgumentMatcher<List> {
      public boolean matches(List list) {
          return list.size() == 2;
      }
      public String toString() {
          //printed in verification errors
          return "[list of 2 elements]";
      }
  }

  // In Java 8 you can treat ArgumentMatcher as a functional interface and use a lambda, e.g.:
  verify(mock).addAll(argThat(list -> list.size() == 2));
  ```

- **Functions accepting ArgumentMatchers:**
  - `argThat(ArgumentMatcher<T> matcher)`
  - `booleanThat(...), byteThat(...), charThat(...), doubleThat(...), floatThat(...), intThat(...), longThat(...), shortThat(...)`

#### Equality

- `eq(boolean), eq(byte), eq(char), eq(double), eq(float), eq(int), eq(long), eq(short)`
- `same(T value)` - Object argument that is the same as the given value.
- `refEq(T value, String... excludeFields)` - Object argument that is reflection-equal to the given value with support for excluding selected fields from a class.

#### String matchers

- `contains(String substring), endsWith(String suffix), startsWith(String prefix), matches(Pattern pattern), matches(String regex)`

#### Null and Not nulls

- `isNotNull(), notNull(), isNull(), nullable()`

#### WARNING

- If you are using argument matchers, all arguments have to be provided by matchers.

    ```java
    verify(mock).someMethod(anyInt(), anyString(), eq("third argument"));
    //above is correct - eq() is also an argument matcher

    verify(mock).someMethod(anyInt(), anyString(), "third argument");
    //above is incorrect - exception will be thrown because third argument is given without an argument matcher.
    ```

- Matcher methods like `anyObject()`, `eq()` do not return matchers. Internally, they record a matcher on a stack and return a dummy value (usually null). This implementation is due to static type safety imposed by the java compiler. The consequence is that you cannot use `anyObject()`, `eq()` methods outside of verified/stubbed method.

#### Example of argument matching

  ```java
  //stubbing using built-in anyInt() argument matcher
  when(mockedList.get(anyInt())).thenReturn("element");

  //stubbing using custom matcher (let's say isValid() returns your own matcher implementation):
  when(mockedList.contains(argThat(isValid()))).thenReturn("element");

  //following prints "element"
  System.out.println(mockedList.get(999));

  //you can also verify using an argument matcher
  verify(mockedList).get(anyInt());

  //argument matchers can also be written as Java 8 Lambdas
  verify(mockedList).add(argThat(someString -> someString.length() > 5));
  ```

### doReturn()|doThrow()| doAnswer()|doNothing()|doCallRealMethod() family of methods

- **Stubbing voids** requires different approach from `when(Object)` because the compiler does not like `void` methods inside brackets. Use `doThrow()` when you want to stub the void method with an exception.

  ```java
  doThrow(RuntimeException.class).when(mock).someVoidMethod();
  doThrow(new RuntimeException()).when(mockedList).clear();
  ```

- You can `use doThrow(), doAnswer(), doNothing(), doReturn() and doCallRealMethod()` in place of the corresponding call with `when()`, for any method. It is necessary when you:
  - stub void methods
  - stub methods on spy objects (see below)
  - stub the same method more than once, to change the behaviour of a mock in the middle of a test.

### Verifying the order of mehtod invocation

- Order Verification:
    ```java
    // A. Single mock whose methods must be invoked in a particular order
    List singleMock = mock(List.class);

    //using a single mock
    singleMock.add("was added first");
    singleMock.add("was added second");

    //create an inOrder verifier for a single mock
    InOrder inOrder = inOrder(singleMock);

    //following will make sure that add is first called with "was added first, then with "was added second"
    inOrder.verify(singleMock).add("was added first");
    inOrder.verify(singleMock).add("was added second");

    // B. Multiple mocks that must be used in a particular order
    List firstMock = mock(List.class);
    List secondMock = mock(List.class);

    //using mocks
    firstMock.add("was called first");
    secondMock.add("was called second");

    //create inOrder object passing any mocks that need to be verified in order
    InOrder inOrder = inOrder(firstMock, secondMock);

    //following will make sure that firstMock was called before secondMock
    inOrder.verify(firstMock).add("was called first");
    inOrder.verify(secondMock).add("was called second");

    // Oh, and A + B can be mixed together at will
    ```
- You can also check if:
  - `verifyNoMoreInteractions(Object... mocks)` - Checks if any of given mocks has any unverified interaction.
  - `verifyZeroInteractions(Object... mocks)` - Verifies that no interactions happened on given mocks beyond the previously verified interactions.

    ```java
    mock.foo(); //1st
    mock.bar(); //2nd
    mock.baz(); //3rd

    InOrder inOrder = inOrder(mock);

    inOrder.verify(mock).bar(); //2n
    inOrder.verify(mock).baz(); //3rd (last method)

    //passes because there are no more interactions after last method:
    inOrder.verifyNoMoreInteractions();

    //however this fails because 1st method was not verified:
    Mockito.verifyNoMoreInteractions(mock);
    ```

### Stubbing consecutive calls (iterator-style stubbing)

  ```java
  when(mock.someMethod("some arg"))
    .thenThrow(new RuntimeException())
    .thenReturn("foo");

  when(mock.someMethod("some arg"))
    .thenReturn("one", "two", "three");
  ```

### Spying on real objects

- You can create spies of real objects. When you use the spy then the **real** methods are called (unless a method was stubbed).
- Real spies should be **used carefully and occasionally**, for example when dealing with legacy code.
- You can also use the `@Spy` annotation

    ```java
    List list = new LinkedList();
    List spy = spy(list);

    //optionally, you can stub out some methods:
    when(spy.size()).thenReturn(100);

    //using the spy calls *real* methods
    spy.add("one");
    spy.add("two");

    //prints "one" - the first element of a list
    System.out.println(spy.get(0));

    //size() method was stubbed - 100 is printed
    System.out.println(spy.size());

    //optionally, you can verify
    verify(spy).add("one");
    verify(spy).add("two");
    ```

- With spy objects it is impossible or impractical to use the `when(Object)` method. Instead:

    ```java
    List list = new LinkedList();
    List spy = spy(list);

    //Impossible: real method is called so spy.get(0) throws IndexOutOfBoundsException (the list is yet empty)
    when(spy.get(0)).thenReturn("foo");

    //You have to use doReturn() for stubbing
    doReturn("foo").when(spy).get(0);
    ```

### Resetting Mocks

- When resetting a mock it forgets any interactions & stubbing
- `reset(mock);`

### Aliases for behavior driven development

- Behavior Driven Development style of writing tests uses //given //when //then comments as fundamental parts of your test methods. This is exactly how we write our tests and we warmly encourage you to do so!
- The problem is that current stubbing api with canonical role of when word does not integrate nicely with //given //when //then comments. It's because stubbing belongs to given component of the test and not to the when component of the test. Hence `BDDMockito` class introduces an alias so that you stub method calls with `BDDMockito.given(Object)` method. Now it really nicely integrates with the given component of a BDD style test!

  ```java
  import static org.mockito.BDDMockito.*;

  Seller seller = mock(Seller.class);
  Shop shop = new Shop(seller);

  public void shouldBuyBread() throws Exception {
    //given
    given(seller.askForBread()).willReturn(new Bread());

    //when
    Goods goods = shop.buyBread();

    //then
    assertThat(goods, containBread());
  }
  ```

### @InjectMocks

- `@Mock` creates a mock. `@InjectMocks` creates an instance of the class and injects the mocks that are created with the `@Mock` (or `@Spy`) annotations into this instance. Note that you must use `@RunWith`(`MockitoJUnitRunner.class`) or `Mockito.initMocks(this)` to initialise these mocks and inject them.

  ```java
  @RunWith(MockitoJUnitRunner.class)
  public class SomeManagerTest {

      @InjectMocks
      private SomeManager someManager;

      @Mock
      private SomeDependency someDependency; // this will be injected into someManager

      //tests...
  }
  ```

### Custom verification failure message

- Allows specifying a custom message to be printed if verification fails.
    ```java
    // will print a custom message on verification failure
    verify(mock, description("This will print on failure")).someMethod();

    // will work with any verification mode
    verify(mock, times(2).description("someMethod should be called twice")).someMethod();
    ```