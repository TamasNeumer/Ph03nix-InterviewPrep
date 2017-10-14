# Interfaces and lambda expressions

## Interfaces
**General Info**
- All interface members must be **public**.
  - "The Java programming language provides mechanisms for access control, to prevent the users of a package or class from depending on unnecessary details of the implementation of that package or class."
  - When implementing these interfaces the functions **must remain public**
  - A class might implement only some of the interface methods, then it is an **abstract class**
- Interfaces are a stepping stone to polymorphism. In java you can test, whether an object is of type S the following way:
  - `object instanceof Type` --> `System.out.println(myObj instanceof Integer)`
- Interfaces can extend another interface.
- Any variable defined in an interface is **public static final**

Abstract class | Interface
--|--
Methods without keywords (might) have bodies  |  Methods without keywords don't have bodies
*abstract* keyword lets you to remove body  | *default* keyowrd lets you add a body  
Methods can be public, private, protected, or package-private (by default)  | All methods are public  


**Static and Default methods**
In interfaces no method can be implemented. There are however two exceptions:
- Static methods (that are usually used as factory functions) can have a defined body. (`public static IntSequence digitsOf(int n){return new DigitSequence(n)}`)
- Default methods (you can supply a default implementation to any interface method)
  - Reason: Adding a non-default method while extending an interface is not compile-friendly, meaning that old classes that rely on that interface don't have the new method implemented --> won't compile. **Use default when extending interfaces!!!**

**Interface clashing**
- When 2 interfaces define the same method you must distinguish the two!
- The `super` keyowrd enables you to call the supertype method.

  ```java
  public class Employee implements Person, Identified {
    public int getID() {return Identified.super.getID();}
  }
  ```

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
- **In Java a method can access private features of ANY object of its class.**

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
- A lambda expression is a block of code that you can pass around so it can be executed later once or multiple times.
- Point of lambda expressions is deferred executions (run on separate thread, run multiple times etc.)
- **Syntax:** `(type varName1, type varName2) -> varName2 - varName1`
- If the type of a lambda expression can be inferred you can omit them.
  - `Arrays.stream(inputArray)
  .map(o -> o == elemToReplace ? substitutionElem : o)`
- In Java there is only one thing you can do with a lambda expression: put it in a variable whose type is a functional interface.
- A *method reference* is equivalent to a lambda expression.
   - (`String::compareToIgnoreCase same as (x,y) -> x.compareToIgnoreCase(y)`) x.compareToIgnoreCase(y)`)
  - `Employee::new` is a reference to an Employee constructor

**Implementing deferred execution**
```java
public static void repeat(int n, Runnable action){
  for (int i = 0; i < n; i++) action.run();
}
```
- Now you can pass a lambda to the function that will be called via the Runnable interfaces's run function.

**Scope**
- The body of a lambda expression has the same scope as a nested block.
- Thus the `this` keyword in a lambda denotes the `this` of the method that creates the lambda.
- A lambda expression can only reference variables whose value don't change. --> Lambda can only access local variables from an enclosing scope that are effectively final.
  - You can't capture the `i` of a forloop, however you can the arg of the enhanced forloop (`String arg: args`)
- Lambda cannot mutate any captured variables.

**Examples**
- Sorting an array of strings based on their lengths:

```java
Arrays.sort(arrayVar, (s1,s2) -> Integer.compare(s1.length(), s2.length()));
```

**Anonymous classes**
```java
public static IntSequence randomInts(int low, int high) {
	return new IntSequence()
		{
			public int next() {return low + generator.nextInt(high - low + 1);}
			public boolean hasNext() {return true;}
		}
}
```
