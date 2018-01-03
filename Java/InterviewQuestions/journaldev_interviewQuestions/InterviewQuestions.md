# Interview questions (by JournalDev)

#### 1. What are the important features of Java 8 release?
- Interface changes with default and static methods
  - Default methods
    - Previously only method declarations allowed, now `default` and `static` methods as well.
    - You cannot extend multiple classes, but implementing multiple interfaces is common. If two interfaces have an (implemented) default method of the same name, the implementing class has to explicitly specify (i.e. `@Override`) with an implementation.
    - Default methods:
      - Help to extend interfaces without breaking the code
      - Help to avoid external utility classes
  - Static methods
    - can't be overridden in the implementation classes
    - You can use it via `ClassName.staticMethodName()`
    - Good for providing utility methods, for example null check, collection sorting etc.
    - Security (implementations cannot override stuff)
- Functional interfaces and Lambda Expressions
  - Problem: We often had to create anonymous classes only to pass the object to a method.
  - Solution: Functional interface
    - An interface with exactly one abstract method is called Functional Interface. ``@FunctionalInterface`` -->  we can use lambda expressions to instantiate them.
    - `Runnable r1 = () -> System.out.println("My Runnable");`
      - Runnable is a functional interface, that’s why we can use lambda expression to create it’s instance.
- Java Stream API for collection classes
- Java Date Time API

#### 2. What do you mean by platform independence of Java?
- You can run the same Java Program in any Operating System.

#### 3. What is JVM and is it platform independent?
- JVM converts bytecode to machine code, and NOT platform independent.
- It provides an interface, that doesn't depend on the OS.

#### 4. What is the difference between JDK and JVM?
- JDK is for development and contains all the dev. tools. to compile, debug java code.
- JVM is only to execute java code on your machine.

#### 5. What is the difference between JVM and JRE?
- Java Runtime Environment (JRE) is the implementation of JVM. JRE consists of JVM and java binaries and other classes to execute any program successfully. If you want to execute any java program, you should have JRE installed.

#### 6. Which class is the superclass of all classes?
- ``java.lang.Object`` is the root class for all the java classes and we don’t need to extend it.

#### 7. Why Java doesn’t support multiple inheritance?
- Interfaces allow multiple inheritance, hence an **interface can extend** multiple interfaces.
- **Classes can't implement** multiple interfaces, because of the naming clash.
- **TODO: Is it really the reason?**
- **TODO: But a class can implement multiple interfaces?! #24 -> class can implement multiple interfaces...**
- You can implement multiple interfaces, as these only define mehtods. Hence if both interfaces have the same method with the same signature, you only implement it once.
  - Class can implement multiple interfaces.
  - Interfaces can extend multiple interfaces.
- However as soon as you want to provide an implementation (i.e. in a class), you can't extend two classes as it might lead to implementation conflict.
- As of Java 8 with default methods in case of interface's default method clash, you have to specify otherwise won't compile (???)

#### 8. Why Java is not pure Object Oriented language?
- It support primitive types such as int, byte, short, long etc.

#### 9. What is difference between path and classpath variables?
- PATH is an environment variable used by operating system to locate the executables.
- CLASSPATH is specific to java and used by java executables to locate class files. We can provide the classpath location while running java application and it can be a directory, ZIP files, JAR files etc.

#### 10. What is the importance of main method in Java?
- Entry point to the application.
- Syntax: `public statis void main(String args[])`

#### 11. What is overloading and overriding in Java?
- Overloading: Multiple functions have the same method name but, with different argument types.
- Overriding: A child class specializes the parent class' implementation. It uses the `@Override` annotation.

#### 12. Can we overload main method?
- Yes, we can have multiple methods with name “main” in a single class, however when running the Main class Java will always pick up the default implementation.

#### 13. Can we have multiple public classes in a java source file?
- Note the **public** word.
- A file can only contain **one public class** definition, however it may contain multiple private classes.

#### 14. What is Java Package and which package is imported by default?
- Java package is the mechanism to organize the java classes by grouping them.
- ``java.lang`` package is imported by default and we don’t need to import any class from this package explicitly.

#### 15. What are access modifiers?
- Access modifiers restrict the access to class members and functions.
- Private/protected/public.
- **TODO: Read on class access modifiers**

#### 16. What is final keyword?
- On Classes: The class cannot be extended. (e.g.: String class)
- On Methods: Cannot be overridden.
- On Variables: Value can't be re-assigned. (The state of the underlying object may change though!)

#### 17. What is static keyword?
- On Variables: variables will be global, and all objects will share that value.
- On Methods: methods can be called using the class name. A static method can access only static variables of class and invoke only static methods of the class.
- **TODO: Are classes that contain static methods eagerly initialized, and when calling ClassName.funct() am I referencing the class?**
- On Classes: You **CAN'T** declare classes OUTER classes static, however you might declare inner classes static. Static nested class is same as any other top-level class and is nested for only packaging convenience.

#### 18. What is finally and finalize in java?
- Finally is a block used in the try-catch-finally, that is always executed.
- `finalize()` is a special method in Object class that we can override in our classes. This method get’s called by garbage collector when the object is getting garbage collected. This method is usually overridden to release system resources when object is garbage collected.

#### 19. Can we declare a class as static?
- See ``#17``.

#### 20. What is static import?
- Normally you import the class (`java.lang.Math`) and then you refer to members & functions.
- You can also import the variable right away: `import static java.lang.Math.PI;`
- Use of static import can cause confusion, so it’s better to avoid it.

#### 21. What is try-with-resources in java?
- From Java 7, we can create resources inside try block and use it. Java takes care of closing it as soon as try-catch block gets finished.

#### 22. What is multi-catch block in java?
- Catch multiple exceptions in a single catch block.
- If a catch block handles multiple exception, you can separate them using a pipe (|) and in this case exception parameter (ex) is final, so you can’t change it.

#### 23. What is static block?
- Java static block is the group of statements that gets executed when the class is loaded into memory by Java ClassLoader. It is used to initialize static variables of the class. Mostly it’s used to create static resources when class is loaded.

#### 24. What is an interface?
- Interfaces are method templates designed to be implemented later.
- Interfaces provide a way to achieve abstraction in java and used to define the contract for the subclasses to implement.

#### 25. What is an abstract class?
- Abstract classes are used in java to create a class with some default method implementation for subclasses. An abstract class can have abstract method without body and it can have methods with implementation also.
- Abstract classes can't be instantiated.

#### 26. What is the difference between abstract class and interface?
- Abstract classes can have method implementations whereas interfaces can’t.
- A class can extend only one abstract class but it can implement multiple interfaces.

#### 27. Can an interface implement or extend another interface?
- Interfaces don’t implement another interface, they extend it. Since interfaces can’t have method implementations, there is no issue of diamond problem.
- From Java 8 onwards, interfaces can have default method implementations. So to handle diamond problem when a common default method is present in multiple interfaces, it’s mandatory to provide implementation of the method in the class implementing them.

#### 28. What is Marker interface?
- A marker interface is an empty interface without any method but used to force some functionality in implementing classes by Java. Some of the well known marker interfaces are Serializable and Cloneable.
- **TODO read on it!**

#### 29. What are Wrapper classes?
- Java wrapper classes are the Object representation of eight primitive types in java. All the wrapper classes in java are immutable and final. Java 5 autoboxing and unboxing allows easy conversion between primitive types and their corresponding wrapper classes.

#### 30. What is Enum in Java?
- ``enum`` is the keyword to create an enum type and similar to class.
- Enum constants are **implicitly static and final**.

#### 31. What is Java Annotations?
- Annotation is metadata about the program embedded in the program itself. It can be parsed by the annotation parsing tool or by compiler.
- Java Built-in annotations are ``@Override``, ``@Deprecated`` and ``@SuppressWarnings``.

#### 32. What is Java Reflection API? Why it’s so important to have?
- Java Reflection API provides ability to inspect and modify the runtime behavior of java application.
- Using reflection you can access private members, hence break design patterns etc.
- Primarily used for libraries (Spring, Hibernate, TomCat etc.)

#### 33. What is composition in java?
- Composition is the design technique to implement **has-a relationship** in classes.
- Java composition is achieved by using instance variables that refers to other objects.

#### 34. What is the benefit of Composition over Inheritance?
- Inheritance exposes all the super class methods and variables to client and if we have no control in designing superclass, it can lead to security holes.
- We can get runtime binding in composition, whereas inheritance binds the classes at compile time.
- Hence the saying: **Favor composition instead of inheritance.**

#### 35. How to sort a collection of custom Objects in Java?
- `java.util.Collections.sort(List)` and `java.util.Arrays.sort(Object[])` methods can be used to sort using natural ordering of objects.
- `java.util.Collections.sort(List, Comparator)` and `java.util.Arrays.sort(Object[], Comparator)` methods can be used if a Comparator is available for comparison.
- **Since Java 8:**
  - the List interface is supports the `sort` method directly, no need to use Collections.sort anymore. These are destructive however (i.e. original data is modified)
  - Since streams are immutable when sorting a new (sorted) stream is returned.

```java
List<Employee> employeeList = new ArrayList<>();
employeeList.add(new Employee(17, "Adam Mani"));
employeeList.add(new Employee(15, "Michael Mani"));
employeeList.add(new Employee(12, "Andrew Mani"));

Collections.sort(employeeList, Comparator.comparingInt(Employee::getAge));
```

#### 36. What is inner class in java?
- We can define a class inside a class and they are called nested classes. Any non-static nested class is known as inner class. Inner classes are associated with the object of the class and they can access all the variables and methods of the outer class. Since inner classes are associated with instance, we can’t have any static variables in them.

#### 37. What is anonymous inner class?
- A local inner class without name is known as anonymous inner class. An anonymous class is defined and instantiated in a single statement. Anonymous inner class always extend a class or implement an interface.
- You can use anonymous classes while passing them to sorting functions.

#### 38. What is Classloader in Java?
- Java Classloader is the program that loads byte code program into memory when we want to access any class. We can create our own classloader by extending ClassLoader class and overriding loadClass(String name) method.
- **TODO read on!**

#### 39. What are different types of classloaders?
There are three types of built-in Class Loaders in Java:
- Bootstrap Class Loader – It loads JDK internal classes, typically loads rt.jar and other core classes.
- Extensions Class Loader – It loads classes from the JDK extensions directory, usually $JAVA_HOME/lib/ext directory.
- System Class Loader – It loads classes from the current classpath that can be set while invoking a program using -cp or -classpath command line options.

#### 40. What is ternary operator in java?
- One liner for if else. (logicalStatement ? ifTrue : ifFalse

#### 41. What does super keyword do?
- super keyword can be used to access super class method when you have overridden the method in the child class.
- We can use super keyword to invoke super class constructor in child class constructor but in this case it should be the first statement in the constructor method.

#### 42. What is break and continue statement?
- Break: Used to terminate loops.
- Continue: Used to jump to the next execution of the loop.

#### 43. What is this keyword?
- this keyword provides reference to the current object and it’s mostly used to make sure that object variables are used, not the local variables having same name.

#### 44. What is default constructor?
- No argument constructor of a class is known as default constructor.
- If no constructor defined, java automatically generates a default constructor.
- If there are other constructors defined, then compiler won’t create default constructor for us.

#### 45. Can we have try without catch block?
- Yes, try-finally

#### 46. What is Garbage Collection?
- Garbage Collection is the process of looking at heap memory,dentifying which objects are in use and which are not, and deleting the unused objects.
- We can run the garbage collector with code ``Runtime.getRuntime().gc()`` or use utility method ``System.gc()``

#### 47. What is Serialization and Deserialization?
- Converting object to Stream (serialization) and vice vera (deserialization).
- The object should implement Serializable interface and we can use java.io.ObjectOutputStream to write object to file or to any OutputStream object.
- **TODO Read on!**

#### 48. How to run a JAR file through command prompt?
- We can run a jar file using java command but it requires Main-Class entry in jar manifest file. Main-Class is the entry point of the jar and used by java command to execute the class.
- **TODO!!!**

#### 49. What is the use of System class?
- Java System Class is one of the core classes. One of the easiest way to log information for debugging is System.out.print() method.
- System class is final so that we can’t subclass and override it’s behavior through inheritance. System class doesn’t provide any public constructors, so we can’t instantiate this class and that’s why all of it’s methods are static.

#### 50. What is instanceof keyword?
- We can use instanceof keyword to check if an object belongs to a class or not. We should avoid it’s usage as much as possible.

#### 51. Can we use String with switch case?
- Since Java 7 you can.

#### 52. Java is Pass by Value or Pass by Reference?
- Pass by value.

#### 53. What is difference between Heap and Stack Memory?
- Heap memory is used by all the parts of the application whereas stack memory is used only by one thread of execution.
- Whenever an object is created, it’s always stored in the Heap space and stack memory contains the reference to it. Stack memory only contains local primitive variables and reference variables to objects in heap space.
- Memory management in stack is done in LIFO manner whereas it’s more complex in Heap memory because it’s used globally.

#### 54. Java Compiler is stored in JDK, JRE or JVM?
- The task of java compiler is to convert java program into bytecode, we have javac executable for that. So it must be stored in JDK, we don’t need it in JRE and JVM is just the specs.
