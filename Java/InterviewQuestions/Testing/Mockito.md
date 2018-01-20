# Mockito

#### 1. What is Mockito
- Mocking framework used for testing. Mockito allows creation of mock object for the purpose of Test Driven Development and Behavior Driven development. Unlike creating actual object, Mockito allows creation of fake object (external dependencies) which allows it to give consistent results to a given invocation.

#### 2. Why Do We Need Mockito? What Are The Advantages?
- Mockito differentiates itself from the other testing framework by removing the expectation beforehand. So, by doing this, it reduces the coupling. Most of the testing framework works on the "expect-run-verify". Mockito allows it to make it "run-verify" framework. Mockito also provides annotation which allows to reduce the boilerplate code.

#### 3. Given an example for DI using mockitoRule
```java
@RunWith(MockitoJUnitRunner.class)
public class Test{
  @Mock
  Server mockServer;

  @InjectMocks
  LibraryApp libraryApp;

  @Test
  public void dITest(){
    LedgerBook ledgerBook = new LedgerBook();
    ledgerBook.setName("default");
    when(mockServer.getBookById(anyLong())).thenReturn(ledgerBook):

    /*Do calls and assert*/

  }
}

```

Source: https://www.wisdomjobs.com/e-university/mockito-interview-questions.html
