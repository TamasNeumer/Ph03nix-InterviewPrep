# Exceptions

#### Basics

- Checked exceptions must be caught.
- Unchecked (Runtime) exceptions may be caught, but it's not a must.
- It is legal to catch en error, however it shouldn't be done so in practice.
- Hierarchy:
  - `Throwable` extends `Object`
  - `Exception` extends `Throwable`
  - `Error` extends `Throwable`
  - `RuntimeException` extends `Exception`
  - (Any other user created checked exceptions extend `Error`)
- New (checked) exceptions to learn (compared to OCA)
  - `java.text.ParseException` - Converting a String to a number.
  - `java.io.IOException, java.io.FileNotFoundException, java.io.NotSerializableException` - Dealing with IO and NIO.2 issues. `IOException` is the parent class. There are a number of subclasses.
- New unchecked exceptions:
  - `java.sql.SQLException` - Dealing with database issues. SQLException is the parent class.
  - `java.lang.ArrayStoreException` - Trying to store the wrong data type in an array.
  - `java.time.DateTimeException` - Receiving an invalid format string for a date.
  - `java.util.MissingResourceException` - Trying to access a key or resource bundle that does not exist.
  - `java.lang.IllegalStateException, java.lang.UnsupportedOperationException` - Attempting to run an invalid operation in collections and concurrency.
  - There is also a rule that says a `try` statement is required to have either or both of the `catch` and `finally` clauses. This is true for `try` statements, but is not true for try-with-resources statements.
  - Unreachable blocks
    - It is illegal to declare a subclass exception in a `catch` block that is lower down in the list than a superclass exception because it will be unreachable code.
    - Java will not allow you to declare a `catch` block for a checked exception type that cannot potentially be thrown by the `try` clause body.

#### Crating custom exceptions

- While you can extend any exception class, it is most common to extend `Exception` (for checked) or `RuntimeException` (for unchecked.)

    ```java
    class CannotSwimException extends Exception {}
    class DangerInTheWater extends RuntimeException {}
    class SharkInTheWaterException extends DangerInTheWater {}

    // OR
    public class CannotSwimException extends Exception {
      public CannotSwimException() {super(); }
      public CannotSwimException(Exception e) { super(e);}
      public CannotSwimException(String message) {super(message); }
    }
    ```

      - The first constructor is the default constructor with no parameters. The second constructor shows how to wrap another exception inside yours. The third constructor shows how to pass a custom error message.
- You can print the stack trace manually using `e.printStackTrace();`

#### Using Multi Catch

- When something goes wrong in a program, it is common to log the error and convert it to a different exception type.
  - `catch (DateTimeParseException e) { e.printStackTrace(); throw new RuntimeException(e); }`
  - It is better though to wrap the above two lines into a function and call this function when handling the intended exceptions.
- The best approach is to use a **multi-catch** block.

    ```java
    catch (DateTimeParseException | IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    ```

- This is much better. There’s no duplicate code, the common logic is all in one place, and the logic is exactly where we would expect to  nd it.
- Remember that the **exceptions can be listed in any order within the catch clause**.
- **Variable** name must appear **only once and at the end**
  - `catch(Exception1 e | Exception2 e | Exception3 e) // DOES NOT COMPILE`
- Java intends multi-catch to be used for **exceptions that aren’t related**, and it prevents you from specifying redundant types in a multi-catch.
  - `catch (FileNotFoundException | IOException e) { } // DOES NOT COMPILE`
  - The exception `FileNotFoundException` is already caught by the alternative `IOException`
- Reassigning the caught exception is **allowed** in the traditional catch block, howver **not allowed** in the multi-catch block!
  - `catch(RuntimeException e) { e = new RuntimeException();}` -- OK
  - `catch(IOException | RuntimeException e) {e = new RuntimeException();}`-- DOES NOT COMPILE!

#### Try with resources

  ```java
  try (
    BufferedReader in = Files.newBufferedReader(path1);
    BufferedWriter out = Files.newBufferedWriter(path2)
    ) {
      out.write(in.readLine());
    }
  ```

- Note the followings:
  - Remember that only a try-with-resources statement is permitted to omit both the catch and finally blocks.
  - One or more resources can be opened in the try block. Statements are separated with `;`
  - You **can** add explicit `catch` and `finally` blocks, however these run **after** the implicit finally block.
  - The **resources created in the try clause are only in scope within the try block**.
    - `in` or `out` are not visible in the catch clauses! (The implicit close in the implicit `final` block has run already, and the resource is no longer available.)
      - It's the same in the traditional try/catch too...
- **Auto Closable**
  - In order to use any class within the try with resources block, the object has to implement the `java.lang.AutoCloseable` interface!
  - `public void close() throws Exception;` method has to be implemented! (You can override it without throwing an exception.)
- **Suppressed Exceptions**
  - What happens if not only your resource but your `try` block throws as well? In this case the exception thrown in the `try` block is the primary exception. As soon as thrown, the code exits the `try` block, however then the implicit call to `close()` is called that also throws an exception. In this case this exception is **suppressed** behind the previously thrown exception. The following prints "`caught: turkeys ran off`" and then `Cage door does not close` as it was the message within the exception thrown from the `close()` method.

    ```java
    try (JammedTurkeyCage t = new JammedTurkeyCage())
    { throw new IllegalStateException("turkeys ran off");}
    catch (IllegalStateException e) { 
      System.out.println("caught: " + e.getMessage());
      for (Throwable t: e.getSuppressed())
        System.out.println(t.getMessage());
      }
    ```

  - Important thing to remember is that java starts closing the files in the reverse order. If you have declared files `t1` and `t2`then `t2`is attempted to be closed first, hence its exception will be the "main" and `t1`'s the suppressed exception.
  - Finally, keep in mind that **suppressed exceptions apply only to exceptions thrown in the `try` clause**. If the `finally` clause throws an exception your previously thrown exception and its suppressed exception are all lost!
- **Working with assertions**
  - An *assertion* is a Boolean expression that you place at a point in your code where you expect something to be true.
  - The syntax for an assert statement has two forms:
    - `assert boolean_expression;`
    - `assert boolean_expression: error_message;`
  - An assertion throws an `AssertionError` if it is false.
  - By default, assert statements are ignored by the JVM at runtime. To enable assertions, use the `-enableassertions`  ag on the command line
    - `java -enableassertions Rectangle` or `java -ea Rectangle`
    - (This doesn't enable assertions in system classes.)
  - You can disable assertions using the `-disableassertions` (or `-da`)  flag for a specific class or package that was previously enabled.

#### Learnings

- A traditional try statement with only one statement can NOT omit the {}.
- You can have a `try` block with only a `finally`, but only if the content of the `try` block doesn't throw checked exceptions. If it does, a `catch` block is mandatory.
- When working with assertions watch out if the executing command enables the assertions!
- Assertions should not have side effects, i.e. they shouldn't change the value of variables!
- If you want to reassign the `IOException e`, then you can only assign the declared exception type (`IOException`) or a sub-type!
- In a multi-catch block the variable `e` is effectively final and can't be reassigned!
- A checked exception extends `Exception` but not `RuntimeException`.
- `Closable` extends `AutoClosable` for backward compatibility.
- The main difference between `AutoCloseable` and `Closeable` is that `AutoCloseable` has `Exception` in the signature and `Closeable` has only `IOException` in the signature.
- In a multi catch block you **can't** try to catch exceptions that are not thrown by the methods in the try part. I.e. `catch(ExceptionA | ExceptionB e)` if the method doesn't throw `ExceptionB` then the statement is invalid!