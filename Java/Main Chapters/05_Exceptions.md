# Exceptions & Logging
Don't use errorCodes. Use exceptions.

**Key points**
1. When you throw an exception, control is transferred to the nearest handler of the
exception.
2. In Java, checked exceptions are tracked by the compiler.
3. Use the try/catch construct to handle exceptions.
4. The try-with-resources statement automatically closes resources after normal
execution or when an exception occurred.
5. Use the try/finally construct to deal with other actions that must occur
whether or not execution proceeded normally.
6. You can catch and rethrow an exception, or chain it to another exception.
7. A stack trace describes all method calls that are pending at a point of execution.
8. An assertion checks a condition, provided that assertion checking is enabled for
the class, and throws an error if the condition is not fulfilled.
9. Loggers are arranged in a hierarchy, and they can receive logging messages with
levels ranging from SEVERE to FINEST.
10. Log handlers can send logging messages to alternate destinations, and formatters
control the message format.
11. You can control logging properties with a log configuration file.

#### When to use exceptions
- Resource missing, invalid arguments etc.

#### Checked vs Unchecked
- **unchecked** exceptions are used to signal about erroneous conditions related to program logic and assumptions being made (invalid arguments, null pointers, unsupported operations, …). Any unchecked exception is a subclass of RuntimeException and that is how Java compiler understands that a particular exception belongs to the class of unchecked ones. You shouldn't bother catching these, as literally any method can throw them.
- **checked** exceptions represent invalid conditions in the areas which are outside of the immediate control of the program (like memory, network, file system, …). Any checked exception is a subclass of Exception. In contrast to the unchecked exceptions, checked exceptions must be listed as a part of the method signature (using throws keyword). (So that the caller can anticipate the throw.)

#### Finally clause
- If returns value, it overwrites the return of try/catch
- Shouldn't throw exceptions

#### Try with resources
- A resource must belong to a class implementing the AutoCloseable interface.

```java
public void readFile( final File file ) {
    try( InputStream in = new FileInputStream( file ) ) {
        // Some implementation here
    } catch( final IOException ex ) {
        // Some implementation here
    }
}
```

#### Own exception
It is strongly advised that all user-defined exceptions should be inherited from RuntimeException class and fall into the class of unchecked exceptions (however, there are always exclusions from the rule). For example, let us defined exception to dial with authentication:

```java
public class NotAuthenticatedException extends RuntimeException {
    private static final long serialVersionUID = 2079235381336055509L;

    public NotAuthenticatedException() {
        super();
    }

    public NotAuthenticatedException( final String message ) {
        super( message );
    }

    public NotAuthenticatedException( final String message, final Throwable cause ) {
        super( message, cause );
    }
}

public class FileFormatException extends IOException {
  public FileFormatException() {}
  public FileFormatException(String message){super(message);}
}
```

#### Logging
