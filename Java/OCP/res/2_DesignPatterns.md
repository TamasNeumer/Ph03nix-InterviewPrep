# Design Patterns

#### Functional Interfaces
- Java defines a *functional interface* as an interface that contains a single abstract method.
- A *lambda expression* is a block of code that gets passed around, like an anonymous method.
  ```java
  @FunctionalInterface public interface Sprint {
    public void sprint(Animal animal);
  }
  ```
- It is not required to mark functional interfaces with `@FunctionalInterface`. Used for compile time checking whether the interface contains only one (not more, not less) abstract methods.
- Be cautious with the annotation. If you later modify the interface to have other abstract methods, suddenly the clients code will break since it will no longer be a functional interface.
- **Working with functional interfaces**
```java
public interface CheckTrait { public boolean test(Animal a);}
/*...*/
private static void print(Animal animal, CheckTrait trait) {
  if(trait.test(animal)) System.out.println(animal);
}
/*...*/
public static void main(String[] args) {
  print(new Animal("fish", false, true), a -> a.canHop());
  print(new Animal("kangaroo", true, false), a -> a.canHop());
}
```
- **Lambda rules**
  - The parentheses `()` can be omitted in a lambda expression if there is exactly one input parameter and the type is not explicitly stated in the expression. This means that **expressions that have zero or more than one** input parameter will still **require parentheses**.
    - `(a, b) -> a.startsWith("test")` as well as `(String a, String b) -> a.startsWith("test")` are correct!
  - If you want to explicitly name the type in the lambda expression use parentheses. (`Animal a) -> { return a.canHop(); }`)
  - If you want to use a return statement or use multiple lines in the body you must us curly braces!
  - When one parameter has a data type listed, though, all parameters must provide a data type.
  - A lambda can't re-delare variables in the block. `(a, b) -> { int a = 0; return 5;}` --> a was already in the local scope, hence redifining it is invalid.
- **Using the predicate interface**
  - `private static void print(Animal animal, Predicate<Animal> trait)` ... `if(trait.test(animal))` ...

#### Implementing polymorphism
- The type of the object determines which properties exist within the object in memory.
- The type of the reference to the object determines which methods and variables are accessible to the Java program.
- **Casting**
  - Casting a superclass to a subclass requires explicit casting. `Primate primate = lemur; Lemur lemur2 = (Lemur)primate;`
  - The compiler will not allow casts to unrelated types.
  - Exception is thrown if an explicit cast is invalid (i.e. unrelated classes)

#### Encapsulation
- A *JavaBean* is a design principle for encapsulating data in an object in Java.
- Properties are `private`.
- Getter for `non‐boolean` properties begins with get.
- Getters for `boolean` properties may begin with is or get.
- Setter methods begin with set.
- The method name must have a prefix of set/get/is followed by the first letter of the property in uppercase and followed by the rest of the property name.

#### Relationships
- *is-a* = inheritance
- *has-a* = delegation/reference
- One of the advantages of object composition over inheritance is that it tends to promote greater code reuse. By using object composition, you gain access to other classes and methods that would be difficult to obtain via Java’s single‐inheritance model.

#### Design patterns
- **Singleton Pattern**
  - **What does it solve?**
    - Passing around references to an object in many levels / many functions etc.
    - Work around of heavy object initialization
  - **Solution**
    - Creating only one instance of an object in memory within an application, sharable by all classes and threads within the application.
    - **Private `static`** variable containing the main reference: `private static final HayStorage instance = new HayStorage();`
    - And a getter function: `public static HayStorage getInstance() {return instance;}`
    - **`Private` constructors** - All constructors in a singleton class are marked `private`, which ensures that no other class is capable of instantiating another version of the class. `private HayStorage() {}`
      - If all of the constructors are declared private in the singleton class, then it is impossible to create a subclass with a valid constructor; therefore, the singleton class is effectively final.
    - Methods are **synchronized**. `public synchronized boolean removeHay (int amount)`
    - You can also use static initialization block to initialize the singleton reference. (And in the block you can execute other methods etc.)
    - **Lazy initialization**
      - `if(instance == null) {instance = new VisitorTicketTracker(); // NOT THREAD-SAFE!}`
      - In case of the lazy initialisation the compiler doesn't let us assign the `final` modifier to the `static` reference.
      - For threadsafety add `synchronized` keyword!
      - The professional version looks somewhat like this. This solution is better than our previous version, as it performs the synchronization step only when the singleton does not exist.
      ```java
      private static volatile VisitorTicketTracker instance;
      public static VisitorTicketTracker getInstance() {
        if(instance == null) {
        synchronized(VisitorTicketTracker.class) {
          if(instance == null) {instance = new VisitorTicketTracker();}
          }
        }
      return instance; }      
      ```
- **Immutable objects pattern**
  - **Solution**
    - 1. Use a constructor to set all properties of the object.
    - 2. Mark all of the instance variables private and final.
    - 3. Don’t define any setter methods.
    - 4. Don’t allow referenced mutable objects to be modified or accessed directly.
    - 5. Prevent methods from being overridden.
