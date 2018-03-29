# Learnings from the Book's Tests

#### Advanced Class Design

- **Class Inheritance and Composition**
  - B implements A, C implements A. You still **can NOT** cast an B instance to C, as it will result in a runtime `ClassCastException`. (Even if they have a single method with the same signature etc etc.) Simply doesn't work.
  - In a chain of inheritance always check whether **all** abstract methods are implemented! Also check whether the class is `abstract`, as in this case it doesn't have to.
  - If a class has `final` instance variables **and** the getter methods are also `final` **and** the class only provides a consturctor that takes the arguments and initializes the instance variables then the class is "immutable". Even if you override the class, the child class' constructor **must** call `super(finalArg1, finalArg2)` and then you can't mutate the inherited members after that...
  - A singleton class doesn't require object composition, as it has a reference to *itself*.
  - Casting a parent to a child class compiles, but throws exception at runime!
- **Instanceof**
  - `bus instanceof ArrayList` - ArrayList being an actual class "enables" compile time checking. On the other hand `bus instanceof Collection` being an interface not!
  - Arrays can be also tested for `instanceof`. For example if `Van` implements `Vehicle`, and  `Van[] vans = new Van[0];` then `vans instanceof Vehicle[]` is true!
- **Enums**
  - If you pass an invalid `String` to the enum's `valueOf()` function an `IllegalArgumentException` is thrown.
  - Enums can have `static` member functions.
  - Enums can't be extended.
  - Enums are not allowed to have anything but `private` and default (package-private) constructor. This constructor is called for each declared constant!
  - Enum constructors are assumed to be private if no access modifier is specified, **unlike** regular classes where package-private is assumed if no access modifier is specified.
- **Anonymous Inner Classes**
  - Anonymous classes don't explicitely use the `implements` keyword. The righ hand side contains an interface reference (`Interface if = new Interface{...};`), and the compiler knows what the implemented methods should be. Note the **semicolon** at the end. The expression can last many lines, but make sure to check the last line! Also the implemented functions must be `public`!
  - Anonymous inner class may implement at most one interface, since it does not have a class definition to implement any others.
  - Java was updated to include `default` interface methods in order to support backward compatibility of interfaces. By adding a `default` method to an existing interface, we can create a new version of the interface, which can be used without breaking the functionality of existing classes that implement an older version of the interface.
  - Only anonymous classes can't be marked static or final, as they don't have a "full" class definition.
- **Member classes**
  - Member classes must be instantiated using an instance of the outer class. Even if the method trying to instantiate lies withing the Outer class!
- **Static inner class**
  - Inner classes can **NOT** have static member. (Except for `static` nested class.)
  - `static` inner class can **not** be instantiated via outer reference! **Only via class name!** (Since it is not a "member" of the class, you don't refer to it via an instance!)
  - Methods **outside** the Outer class may instantiate the `static` class using the "full class path": `Outer.StaticInnerClass sic = ...`, while methods **inside** the Outer class may simply refer to it by its name: `StaticInnerClass sic = ...`
- **Misc**
  - You can create a "deep" copy like this: `return new ArrayList<>(counts);`
  - A `static` initializer is not allowed inside of a method. (`static { ...}`)
  - `abstract public Leader getPartner(int count);` is correct (i mean the order of the words doesn't cause compile error)
  - Having multiple arguments in a lambda expression without type specification is not wrong, as the types are inferred from the left-side definition. `BiConsumer<Integer, Integer> p = (n, q) -> System.out.println("asd");`.

#### Generics

- a