# Interfaces and lambda expressions

## Interfaces
**General Info**
- All interface members must be **public**.
  - "The Java programming language provides mechanisms for access control, to prevent the users of a package or class from depending on unnecessary details of the implementation of that package or class."
  - When implementing these interfaces the functions **must remain public**
  - A class might implement only some of the interface methods, then it is an **abstract class**
- Interfaces are a stepping stone to polymorphism. In java you can test, whether an object is of type S the following way:
  - `object instanceof Type` --> `System.out.println(myObj instanceof Integer)`
- Interfaces can extend another interface. (Or even multiple interfaces.)
- Any variable defined in an interface is **public static final**
- Also multiple *extension* is prohibited, however multiple *inheritance of interfaces* is allowed. Multiple inheritance is allowed with interfaces, but again that works because only the method signatures are inherited.

Abstract class | Interface
--|--
Methods without keywords (might) have bodies  |  Methods without keywords don't have bodies
*abstract* keyword lets you to remove body  | *default* keyowrd lets you add a body  
Methods can be public, private, protected, or package-private (by default)  | All methods are public  


**Static and Default methods**  
In interfaces no method can be implemented. There are however two exceptions:
- Static methods (that are usually used as factory functions) can have a defined body. (`public static IntSequence digitsOf(int n){return new DigitSequence(n)}`)
  - Access the method using the interface name. **Classes do not need to implement an interface to use its static methods.**
- Default methods (you can supply a default implementation to any interface method)
  - Reason: Adding a non-default method while extending an interface is not compile-friendly, meaning that old classes that rely on that interface don't have the new method implemented --> won't compile. **Use default when extending interfaces!!!**


**The Comparable Interface**
- A comparable object is capable of **comparing itself with another object.** The class itself must implements the java.lang.Comparable interface in order to be able to compare its instances.
- When calling `x.compareTo(y)` the method returns an integer value to indicate which one should come first. A negative integer (not necessarily -1) indicates that x should come after y. If equal 0 is returned.

```java
public interface Comparable<T> {
  int compareTo(T other);
}

public class Employee implements Comparable<Employee> {
  public int compareTo(Employee other){
    return Double.compare(salary, other.salary)
  }
}
```
- **In Java a method can access private features of ANY object of it's class.**

**The Comparator Interface**
- A comparator object is capable of comparing two different objects. The class is not comparing its instances, but some other class’s instances. This comparator class must implement the java.util.Comparator interface.
- In some cases you cannot define your own `comapreTo` method. (e.g.: the string class has it already defined and you can't override it.) In such cases you create your own `Comparator` object, that implements the `compare` method.

  ```java
  public interface Comparator<T> {
    int compare(T first, T second);
  }

  class LengthComparator implements Comparator<String> {
    public int compare(String first, String second) {
      return first.length() - second.length();
    }
  }

  Comparator<String> comp = new LengthComparator();
  if (comp.compare(words[i], words[j]) > 0) ...
  ```

**The Runnable Interface**
- To define a task you need to implement the `Runnable` interface.
- The run method executes in a separate thread, and the current thread can proceed with other work

  ```java
  class HelloTask implements Runnable {
    public void run() {
      for (int i = 0; i < 1000; i++) {
      System.out.println(“Hello, World!”);
      }
    }
  }

  Runnable task = new HelloTask();
  Thread thread = new Thread(task);
  thread.start();
  ```

## Lambda expressions
**Basics**
- Syntax: (type varName1, type varName2) -> varName2 - varName1`
  -  If the type of a lambda expression can be inferred you can omit them.
- A *functional interface* is an interface with a single abstract method (SAM). Such interface is the `Runnable` that has the single method `run()`.
- Note: There is no class in the Java library called Lambda. Lambda expressions
can only be assigned to functional interface references.

**Examples**
- Old School version using anonymous inner class:

  ```java
  public class RunnableDemo {
    public static void main(String[] args) {
      new Thread(new Runnable() {
        @Override
        public void run() {
          System.out.println("inside runnable using an anonymous inner class");
        }
      }).start();
    }
  }
  ```

- Same with lambda:

  ```java
  new Thread(() -> System.out.println("inside Thread constructor using lambda")).start();

  /*OR*/
  Runnable r = () -> System.out.println("lambda expression implementing the run method");
  new Thread(r).start();

  /* IF you have multiple arguments using BLOCK SYNTAX*/
  File directory = new File("./src/main/java");
    String[] names = directory.list((File dir, String name) -> {
    return name.endsWith(".java");
  });
  ```

**Method References**
- Problem: You want to use a method reference to access an existing method and treat it like a lambda expression.
- There are three types of method references:
  - `object::instanceMethod`
    - Refer to an instance method using a reference to the supplied object, as in `System.out::println`
  - `Class::staticMethod`
    - Refer to static method, as in ``Math::max``
  - ``Class::instanceMethod``
    - Invoke the instance method **on** a reference to an object supplied by the context, as in ``String::length``. (The lambda equivalent would be `x -> x.length())`
- Note: If you refer to a method that takes multiple arguments via the class
name, the **first element** supplied by the context **becomes the target** and the **remaining elements are arguments** to the method. Below you see an example for lambda / function reference.

```java
List<String> strings =
  Arrays.asList("this", "is", "a", "list", "of", "strings");
List<String> sorted = strings.stream()
  .sorted((s1, s2) -> s1.compareTo(s2))
  .collect(Collectors.toList());

List<String> sorted = strings.stream()
  .sorted(String::compareTo)
  .collect(Collectors.toList());
```

**Constructor References**
- Problem: You want to instantiate an object using a method reference as part of a stream pipeline.
- Use the `new` keyword as part of a method reference.
- Example: Given a collection of strings, you can map each one into a ``Person`` (object) using either a lambda expression or the constructor reference. You can also create an array of Person using the `Person[]::new` array constructor reference.

```java
List<Person> people = names.stream()
  .map(name -> new Person(name))
  .collect(Collectors.toList());

List<Person> people = names.stream()
  .map(Person::new)
  .collect(Collectors.toList());

Person[] people = names.stream()
  .map(Person::new)
  .toArray(Person[]::new);
```

- Constructor references are also useful if you want to create new objects while operating with Streams. (aka you want to avoid `List<Person> people = Stream.of(before) .collect(Collectors.toList());`, as in this case before == people as reference.

```java
people = Stream.of(before)
  .map(Person::new)
  .collect(Collectors.toList());
```

**Scope**
- The body of a lambda expression has the same scope as a nested block.
- Thus the `this` keyword in a lambda denotes the `this` of the method that creates the lambda.
- A lambda expression can only reference variables whose value don't change. --> Lambda can only access local variables from an enclosing scope that are effectively final.
  - You can't capture the `i` of a forloop, however you can the arg of the enhanced forloop (`String arg: args`)
- Lambda cannot mutate any captured variables.
