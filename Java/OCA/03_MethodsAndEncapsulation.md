# Methods and Encapsulation

#### Scope of variables
- Local variables
  - Limited to the *enclosing* `}`
  - Forward reference (usage before definition) is not allowed.
- Method parameter
  - Valid inside the method.
- Instance variables of classes
  - Accessible inside the class + outside based on accessibility.
- Static variables of classes
  - Note: Accessing a static variable of a null instance works, because the static variable belongs to the class!
    - `System.out.println(nullReferenceInstance.staticVariable);`
- You can’t define a static *class* variable and an instance variable with the same name in a class.
  - `static boolean softKeyboard = true;` and `boolean softKeyboard = true;` won't compile for the class.
- Local variables and method parameters can’t be defined with the same name.
  - (If a method argument is `int five` you can't create a new `int five = 5;` variable within the function)
- However a class **CAN** define local variables with the same name as the instance or class variables, also referred to as shadowing.
  ```java
  class MyPhone {
    static boolean softKeyboard = true; String phoneNumber;
    void myMethod() {
      boolean softKeyboard = true; // OK
      String phoneNumber;  // OK
    }
  }
  ```

#### Object life cycle
- Unlike some other programming languages, such as C, Java doesn’t allow you to allocate or deallocate memory yourself when you create or destroy objects.
- An object’s life cycle starts when it’s created and lasts until it goes out of scope or is no longer referenced by a variable.
- The method `finalize` is defined in the class `java.lang.Object`. All Java classes can override the method `finalize`, which exe- cutes just before an object is garbage collected. In theory, you can use this method to free up resources being used by an object, although doing so isn’t recommended because its execution isn’t guaranteed to happen.
- Object is declared with the name and initialised with the `new` keyword.
- Once an object is created, it can be accessed using its reference variable. It remains accessible until it goes out of scope or its reference variable is explicitly set to null. Also if the reference is reassigned and there are no more references pointed to the object, the object becomes inaccessible and hence ready for GC.
  - The exam may query you on the total number of objects that are eligible for garbage collection after a particular line of code.
  - You can determine only which objects are eligible to be garbage collected. You can never determine when a particular object will be garbage collected.
    - *Watch out for questions with wordings such as “which objects are sure to be collected during the next GC cycle,” for which the real answer can never be known.*
- As a programmer, you can’t start execution of Java’s garbage collector. You can only request it to be started by calling `System.gc()`or `Runtime.getRuntime().gc()`. But calling this method doesn’t guarantee when the garbage collector would start (the call can even be ignored by the JVM).
- **Island of Isolation**
  - Having to objects that refer to each other, but no external references are pointing to any of these objects. ("Circular dependency"). Java can detect such cases and collect these objects.

#### Create methods with arguments and return values
- Specify the return type or `void` if there is none.
- `double newWeight = p.setWeight(20.0);` --> won't compile if method returns void!
- Method **parameters are the variables** that appear in the definition of a method. Method **arguments are the actual values** that are passed to a method while executing it.
- Variable arguments are defined with `...` for example `void myFunct(String ... arg)`
  - When you define a variable-length argument for a method, Java creates an array behind the scenes to implement it.
  - You can define **only one variable argument in a parameter list**, and it **must be the last variable** in the parameter list.
- Returning
  - There is no point adding code after the return statement -> fails to compile! (The return statement need not be the last statement in a method, but it must be the last statement to execute in a method.)
- **Overloaded methods**
  - Overloaded methods are methods with the same name but different method parameter lists.
  - Overloaded methods can’t be defined by only changing their return type or access modifiers or both.
  - Overloaded methods can be done via changing parameter **type**, parameter **order** (based on param. type), parameter **number**.
  - Note that you can have a functions accepting a pair of (`int`, `double`) and (`double`, `int`) however if you pass integer values
  into this function the compiler won't be able to know which overloaded method you are about to call, hence a compiler error is given...

#### Constructors
- Constructors are special methods that create and return an object of the class in which they’re defined. Constructors have the same name as the class in which they’re defined, and they don’t specify a return type — not even void.
- A constructor must not define any return type. Instead, it creates and returns an object of the class in which it’s defined. If you define a return type for a constructor, it’ll no longer be treated as a constructor. Instead, it’ll be treated as a regular method, even though it shares the same name as its class.
- Initializer block:
  - An initializer block is defined within a class, not as a part of a method. It executes for every object that’s created for a class.
  - If you define both an initializer and a constructor for a class, both of these will exe- cute. The initializer block will execute prior to the constructor!
    ```java
    class Employee {
        { System.out.println("Employee:initializer"); }
      }
    ```
  - If a class defines multiple initializer blocks, their order of execution depends on their placement in a class. But all of them execute before the class’s constructor.
  - Initializer blocks are used to initialize the variables of anonymous classes. In the absence of a name, anonymous classes can’t define a constructor and rely on an initializer block to initialize their variables upon the creation of an object of their class.
-  In the absence of a user-defined constructor, Java inserts a *default constructor*. This constructor doesn’t accept any method arguments. It calls the constructor of the super (parent) class and assigns default values to all the instance variables.
  - The accessibility of a default constructor matches the accessibility of its class. Java creates a public default constructor for a public class. It creates a default constructor with package access for a class with package-level access.
  - Note: **Once you have created a non-default constructor java won't create any no-arg constructor!!!**
- **Warning!** You can’t invoke a constructor within a class by using the class’s name. (`Employee(){Employee(null,0);}` WONT work!!! Instead: `this(null,0);`) **Also** when calling another constructor in a method it **must** be the first statement in the function. Hence you can call only **ONE** constructor per method.
- Java detects circular constructor calls and gives a compiler error.

#### Manipulating object fields
- Ways:
  - Using methods to read and write object fields
  - Using constructors to write values to object fields
  - Directly accessing instance variables to read and write object fields
-  Watch out for the following points in the exam:
  - Access modifier of the object field
  - Access modifiers of methods used to read and write the value of the object field
  - Constructors that assign values to object fields
- A method that accepts a vararg can be invoked with an array in place of the vararg.

#### Passing objects and primitives to methods
- In Java everything is passed by value. References as well as primitive types.
- Due to that you can't change the values of primitives outside of the scope and you can't re-assign references for the outside scope either.

#### Key takeaways
- Be careful of shadowing, they might trick you in a question that seems Exception handling but in fact they are have a small shadowing issue and it is about scopes.
- Two types: **user defined** and **compiler generated**
- Watch out for public members. (They provide setters that limit the range for setting the class variable. Then they ask if the class variable is limited to the range, which is not, since the public variable can be reached from anywhere...)
- Correct overloading: Same method name and different **types**/**number**/**order** of arguments.
