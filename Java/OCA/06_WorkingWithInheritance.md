# Working with Inheritance

#### Inheritance with classes
- the inherited class Employee is also referred to as the `superclass`, `base class`, or `parent class`
- the class that inherits are called `subclasses`, `derived classes`, `extended classes`, or `child classes`
- Inherited members:
  - *Default*—Members with default access can be accessed in a derived class only if the base and derived classes reside in the same package.
  - `protected`—Members with protected access are accessible to all the derived classes, regardless of the packages in which the base and derived classes are defined.
  - `public`—Members with public access are visible to all other classes.
- The base class' constructor is **NOT** inherited - the child class can call it explicitly
- **Abstract base class vs concrete base class**
  - It isn’t mandatory for an abstract class to define an abstract method. But if an abstract base class defines one or more abstract methods, the class must be marked as abstract and the abstract methods must be implemented in all its concrete derived classes. If a derived class doesn’t implement all the abstract methods defined by its base class, then it also needs to be an abstract class.

#### Interfaces
- "Contract" - states the **what** and not the how.
- Java **allows** to **implement** multiple interfaces.
- You can define methods and constants in an interface.
  - Constants:
    - The interface variables are **implicitly public, static, and final**.
  - Methods:
    - All methods of an interface are **implicitly public**.
    - Methods can be `abstract`, `static` or `default`
    - If not method body is present, implicitly `abstract`.
    - The definition of a default method **must** include the keyword `default`
    - A static method in an interface **can’t** be called using a reference variable. It **must** be called using the interface name.
- An interface might **extend** one or multiple interfaces.
- Obligatory parts: `interface` keyword, name of the interface, curly braces `{}`
- The **top-level** interface must be **public** (or default) and **without access modifier**
  - All the top-level Java types (classes, enums, and interfaces) can be declared using only two access levels: public and default. Inner or nested types can be declared using any access level.
- All interface members must be **public.**
- Only the `abstract` and `strictfp` non-access modifiers are allowed!
- **Implementing interfaces**
  - You must implement an abstract method of an interface using the explicit access modifier `public`.
  - When you implement an interface method in a class, it follows method-overriding rules (interface returns `Object`, you can override it by returning `String`)
  - While overriding a default method, you must not use the keyword default. Rules for overriding `default` and regular methods are the same.
  - If an interface defines a static method, the class that implements it can define a static method with the same name, but the method in the interface isn’t related to the method defined in the class.
  - A class can implement multiple interfaces with the same constant names, only if a reference to the constants isn’t ambiguous. (i.e. if both interfaces have a constant `int ASD = 5;` then any reference to this constant has to be made via the Interface name!)
  - But you can’t make a class extend multiple interfaces that define methods with the same name that don’t seem to be a correct combination of overloaded methods.
    ```java
    // OK
    interface Jumpable {
        abstract String currentPosition();
    }
    interface Moveable {
        abstract String currentPosition();
    }
    class Animal implements Jumpable, Moveable {
        public String currentPosition() {
            return "Home";
        }
    }

    // NOT OK
    interface Jumpable { abstract String currentPosition();}
    interface Moveable { abstract void currentPosition(); }
    class Animal implements Jumpable, Moveable {
        public String currentPosition() {
            return "Home";
        }
    }
    ```
    - class can implement multiple interfaces with the same abstract method names if they have the same signature or form an overloaded set of methods.
    - (**Remember** what would happen if you didn't assign the functions return value to anything? The compiler wouldn't be able to tell which implementation you are trying to call...)
  - A class can implement multiple interfaces with the same default method name and signature, if it overrides its default implementation.
  - A class can implement multiple interfaces with the same static method names, irrelevant of their return types or signature.

- **Class Extension**
  - Class can implement multiple interfaces but extend only one class.
    - Because a derived class may inherit different implementations for the same method signature from multiple base classes, **multiple inheritance isn’t allowed** in Java.

#### Casting
- You can use casting to get around the Java Compiler. But you must know what you are doing!
  - `((HRExecutive)interviewer).specialization = new String[] {"Staffing"};`

#### `this` and `super`
- `this` and `super` are implicit object references. These variables are defined and initialized by the JVM for every object in its memory.
- The `this` reference is required only when code executing within a method block needs to differentiate between an instance variable and its local variable or method parameters.
  - `this();` calls the default constructor of the current class
  - It must be the first line in the method.
  - With Java 8, you can use the keyword `this` in a default method to access the methods of an interface and its constants.
- `super` refers to the direct parent or base class of a class
    ```java
    class Employee{String name;}
    class Programmer extends Exmployee {
      String name;
      void setNames(){
        this.name = "Programmer";
        super.name = "Employee";
      }
    }
    ```
    - You can also use `super` to access the base class Constructor.
      - `super()` - calls the default constructor.
      - `super(name, age)` - calls the parent class constructor accepting the given variables.
- You **can't** use super and this in static methods.

#### Polymorphism
- Polymorphic methods are also called overridden methods.
- With inheritance, the **instance variables bind at compile time** and the methods bind at runtime.
  - This means that if the parent class (Parent) defines a `String name="mike";` and the child class (Child) defines a member `String name="Dave";` then if you have a Parent reference to a children and you call `parentRefToChild.name` the you obviously get "mike".
- Watch out for overloaded methods that seem to participate in polymorphism—overloaded methods don’t participate in polymorphism. Only overridden methods with the same method signatures participate in polymorphism.

#### Simple Lambda expressions
- On the exam, you’ll need to identify invalid lambda expressions. The return value of the lambda expression must match or must be compatible with the return value of the **only abstract method** in the interface.
- The **Predicate** interface
  -  This interface has **exactly one abstract method** named test, which takes any object as input and returns a boolean.
  - "It must have exactly one abstract method and may have other default or static methods."

#### Key takeaways
- Too many rules IMO, hence in order to solve these exercises you really have to think logically from the compiler's point of view.
- If the parent class defines a field (`String address = "EmpAddress";`) then the child classes inherit this field with the assigned value already!
- **An Overriding method is allowed to make the overridden method more accessible,** and since protected is more accessible than default (package), this is allowed.
- Since Java 1.5, an **overriding method is allowed to change the return type to any subclass of the original return type**, also known as covariant return type. This **does not apply to primitives**, in which case, the return type of the overriding method must match exactly to the return type of the overridden method.
