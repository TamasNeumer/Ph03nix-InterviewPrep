# Interview questions

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
