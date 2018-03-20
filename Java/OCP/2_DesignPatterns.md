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
    - 2. Mark all of the instance variables `private` and `final`.
    - 3. Don’t define any setter methods.
    - 4. Don’t allow referenced mutable objects to be modified or accessed directly.
    - 5. Prevent methods from being overridden.
- **Builder Pattern**
  - **Motivation**: As classes grow, so do the constructors change. Adding new constructors and extending these results in a messy code.
  - **Solution**:
    - The builder pattern is a creational pattern in which parameters are passed to a builder object, often through method chaining, and an object is generated with a final build call.
    - `public AnimalBuilder setSpecies(String species)` a lot of methods like that you can chain on the initial constructor. Initialize the mandatory members in the constructor and allow the optional values to be chained.
    - Finally, we create our target object build method, usually named `build()`, allowing it to interact with the Animal’s constructor directly. (`public Animal build() {return new Animal(species,age,favoriteFoods);}`)
- **Factory Pattern**
  - **Problem**
    - How do we write code that creates objects in which the precise type of the object may not be known until runtime?
  - **Solution**
    - Use a factory class to produce instances of objects based on a set of input parameters.
    - Basic approach: `static` factory method with a switch case.
      ```java
      public static Food getFood(String animalName) {
        switch(animalName) {
          case "zebra": return new Hay(100);
          case "rabbit": return new Pellets(5);
          case "goat": return new Pellets(30);
          case "polar bear": return new Fish(10);
        }     
        // Good practice to throw an exception if no matching subclass could be found
        throw new UnsupportedOperationException("Unsupported animal: "+animalName);
      }
      /*CALLER*/
      final Food food = FoodFactory.getFood("polar bear");
      ```
    - Note: All the constructors of the classes are either `public` or `package default`, but in this case the classes must be located in the same package as the factory class. Using `public` constructors is not favourable, as the callers can bypass the factory and instantiate the classes directly.

#### Learnings
- Design principles are often applied throughout an application, whereas design patterns are applied to solve specific problems.
- Abstract classes are not functional interfaces (despite having a single `abstract` method)
- If you want to encapsulate a class and have a constructor like `public Seal(String name, List<Seal> friends)` you should make a deep copy of the list, so that the caller can't modify your inner list via his reference!
- **Lambdas**
  - Lambdas with 0 ore more than 1 parameter requires `()`
  - `(Camel c) -> {return;}` is a correct (`void`) lambda.
  - Specifying the data type for one parameter in a lambda expression requires you to specify the data type for all parameters in the expression
  - In order to use the curly braces `{}` you **must** use the `return` statement. hence `(e) -> {"Poof"}` is incorrect!
  - `caller((e) -> { String e = "", return "Poof";}` is **incorrect** as the variable `e` is defined twice!
- **Singleton**
  -  The name of the object itself, as well as the method to retrieve the singleton, is not defined in the pattern.
  - Making the singleton reference `final` would prevent lazy initialisation.
