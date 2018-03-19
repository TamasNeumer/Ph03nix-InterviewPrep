# Advanced Class Design

#### instanceof
- In a `instanceof` B, the expression returns `true` if the reference to which a points is an **instance of** class B, a **subclass of B** (directly or indirectly), or a class that **implements the B interface** (directly or indirectly
  - `boolean b1 = hippo instanceof Hippo;`
  - `boolean b5 = nullHippo instanceof Object;` - Only case where `instanceof Object` returns `false`!
  - `boolean b5 = anotherHippo instanceof Elephant;` - **Does not compile!**
- The compilation check only applies when `instanceof` is called on a class. If the class cannot be an instance of the other one, the compilation fails. When checking it on an interface Java waits until runtime to do the check.

#### Understanding Virtual Method Invocation and Annotating These
- Virtual Methods = overridden methods used in polymorphism
- An annotation is extra information about the program, and it is a type of metadata. It can be used by the compiler or even at runtime.
- The `@Override` annotation is used to express that you, the programmer, intend for this method to override one in a superclass or implement one from an interface.
- The `@Override` keyword can be above the method definition, or in the same line, as Java ignores whitespaces. (`@Override public void findDen(boolean b) { }`)
- This helps preventing scenarios, where the user does not override the method with the correct signature. (Compile error)
- The annotation can be only used on methods.
- When `@Override` appears check the followings:
  - Implementing a method from an interface
  - Overriding a superclass method of a class shown in the example
  - Overriding a method declared in `Object`, such as `hashCode`, equals, or `toString`

#### Coding `equals`, `hashCode`m `toString`
- **Remember the method signatures!**
- **`toString`**
  - Java automatically calls the `toString()` method if you try to print out an object.
  - `@Override public String toString() { return name; }`
  - Apache common langs has some nice utilities that reduce such boilerplate.
- **`equals`**
  - `String` does have an `equals()` method. It checks that the values are the same. `StringBuilder` uses the implementation of `equals()` provided by `Object`, which simply checks if the two objects being referred to are the same.
    ```java
    @Override public boolean equals(Object obj) {
      if ( !(obj instanceof Lion)) return false; Lion otherLion = (Lion) obj;
      return this.idNumber == otherLion.idNumber;
    }
    ```
  - **Contract for the equals method**
    - It is `reflexive`: For any non‐null reference value `x`, `x.equals(x)` should return `true`.
    - It is `symmetric`: For any non‐null reference values `x` and `y`, `x.equals(y)` should return
`true` if and only if `y.equals(x)` returns `true`.
    - It is `transitive`: For any non‐null reference values `x`, `y`, and `z`, if `x.equals(y)` returns
`true` and `y.equals(z)` returns `true`, then `x.equals(z)` should return `true`.
    - It is `consistent`: For any non‐null reference values `x` and `y`, multiple invocations of `x.equals(y)` consistently return `true` or consistently return `false`, provided no information used in equals comparisons on the objects is modified
    - For any non‐null reference value `x`, `x.equals(null)` should return `false`.
  - Apache common langs also has nice implementations to reduce boiler plate.
- **`hashCode`**
  - Whenever you override `equals()`, you are also expected to override `hashCode()`.
  - **The hashCode contract**
    - Within the same program, the result of `hashCode()` for a given object must not change. (Include the id into calculating the hashCode, but not the height or weight of a person as it changes often!)
    - If `equals()` returns `true` when called with two objects, calling `hashCode()` on each of those objects must return the same result.
    - If `equals()` returns `false` when called with two objects, calling `hashCode()` on each of those objects does not have to return a different result. (hashCode is not unique for when called on unequal methods!)
  - `public int hashCode()` is the correct signature.
  - It is common to multiply by a prime number when combining multiple fields in the hash code. This makes the hash code more unique, which helps when distributing objects into buckets.

#### Enums
- **Enum basics**
  - With numeric constants, you can pass an invalid value and not find out until runtime. With enums, it is impossible to create an invalid enum type without introducing a compiler error.
  - You create an enum using the `enum` keyword.
  - `public enum Season {WINTER, SPRING, SUMMER, FALL}`
  - Since you are suing constants use UPPERCASE letters.
  - Behind the scenes, an enum is a type of class that mainly contains static members.
  - Using the `values()` method you can get all the members of the `enum` and then fetch their content using `name()` and `ordinal()`
    ```java
    for(Season season: Season.values()) {
       System.out.println(season.name() + " " + season.ordinal());
    }
    ```
  - Comparing an `enum` with an `int` won't compile!
  - If passed invalid string to `valueOf` then it throws `IllegalArgumentException`
  - You **can't** extend an enum.
- **Enums and switches**
  - Using it in switch cases, when defining the case statements you don't need to provide the full enum name (`Season.WINTER`), but you can simply refer to the individual contents directly. (`case WINTER:`). This is because Java knows what we are talking about based on the switch input argument. Also you **must not** use the full names as it leads to compilation errors.
- **Adding constructors, fields and methods**
  - **Semicolon** required if if the enums has anything more than values! It is only **optional** if the enum contains only values.
  - The constructor is **private**. (Or package default ?!)
  - The first time that we ask for any of the enum values, Java constructs all of the enum values. After that, Java just returns the already‐constructed enum values. (So if you have some print in the constructor it will only print once.)
    ```java
    public enum Season {
      WINTER("Low"), SPRING("Medium"), SUMMER("High"), FALL("Medium"); // SEMICOLON!
      private String expectedVisitors;
      private Season(String expectedVisitors){
        this.expectedVisitors = expectedVisitors;
      }
      public void printExpectedVisitors(){System.out.println(expectedVisitors)}
    }
    ```
  - Final you can add functions to enum members. Here we have "overridden" some cases, while the others get the default implementation.
  - **Note the semicolon after** `FALL`!
  - If the `printHours` method were defined as abstract, all enum members must provide implementation!
    ```java
    public enum Season3 { WINTER {
      public void printHours() { System.out.println("short hours"); } }, SUMMER {
      public void printHours() { System.out.println("long hours"); } }, SPRING, FALL;
      public void printHours() { System.out.println("default hours"); }
    }
    ```

#### Nested classes
- A *nested class* is a class that is de ned within another class. A nested class that is not `static` is called an *inner class*.
- The four main types are:
  - A **member inner class** is a class defined at the same level as instance variables. It is not static. Often, this is just referred to as an inner class without explicitly saying the type.
  - A **local inner class** is defined within a method.
  - An **anonymous inner class** is a special case of a local inner class that does not have a name.
  - A **static nested class** is a `static` class that is defined at the same level as `static` variables.
- **Member Inner Classes**
  - Properties:
    - Can be declared public, private, or protected or use default access Can extend any class and implement interfaces
    - Can be `abstract` or `final`
    - Cannot declare `static` fields or methods
    - Can access members of the outer class including private members (as if it were his own).
  - `Inner inner = outer.new Inner();` is also a valid instantiation. (Almost never used in practice.)
  - FYI:  For the inner class, the compiler creates `Outer$Inner.class`.
  - You can have nested classes in nested classes, where you can end up with funky lines like this.
    - `System.out.println(B.this.x);` // First parent
    - `System.out.println(A.this.x);` // Second parent
    - Instantiation: `A.B.C c = b.new C();`
- **Local Inner Classes**
  - A local inner class is a nested class de ned within a method. Like local variables, a local inner class declaration does not exist until the method is invoked, and it goes out of scope when the method returns.
  - Properties:
    - They do not have an access specifier.
    - They cannot be declared `static` and cannot declare `static` fields or methods.
    - They have access to all fields and methods of the enclosing class.
    - They do not have access to local variables of a method unless those variables are `final` or **effectively final**.
  - Accessing final variables of the outer classes + final variables of the enclosing method. The usage must come after the declaration!
    ```java
      private int length = 5; // non-final class variable
      public void calculate() {
        final int width = 20; // final method-local variable
        class Inner {
          public void multiply() { System.out.println(length * width);}
        }
        Inner inner = new Inner();
        inner.multiply();
     }
    ```
  - In Java 8, the “effectively final” concept was introduced. If the code could still compile with the keyword final inserted before the local variable, the variable is **effectively final**.
    - (i.e. if a variable is assigned a value only once. Note: `int x; x = 2;` in two separate lines is also effectively final.)
    - Even tho there is only one assignment before inner class declaration and more follow after the declaration, the variable won't be treated as final, just because there was just assignment before the class.
- **Anonymous Inner Class**
  - An anonymous inner class is a local inner class that does not have a name. It is declared and instantiated all in one statement using the `new` keyword.
    ```java
    abstract class SaleTodayOnly { abstract int dollarsOff(); }
    public int admission(int basePrice) {
        SaleTodayOnly sale = new SaleTodayOnly() {int dollarsOff() { return 3; } };
        return basePrice - sale.dollarsOff();
      }
    ```
  - Watch out for the semicolon on the first line of the method!
  - If an interface had been implemented the method bust have been declared `public`!
  - You can't implement both interfaces and extend classes in anonymous classes! Only exception is extending `java.lang.Object`
  - You can define anonymous classes as arguments to methods.
- **Static nested Classes**
  - A static nested class is a static class defined at the member level. It can be instantiated without an object of the enclosing class, so it can’t access the instance variables without an explicit object of the enclosing class.
  - Properties:
    - The nesting creates a namespace because the enclosing class name must be used to refer to it.
    - It can be made `private` or use one of the other access modifiers to encapsulate it.
    - The enclosing class can refer to the fields and methods of the static nested class.
    ```java
    public class Enclosing {
    static class Nested {private int price = 6; }
    public static void main(String[] args) {
      Nested nested = new Nested();
      System.out.println(nested.price);
    } }
    ```
  - Importing static nested classes is interesting:
    - It works with both `import static` just as well as with normal import (`import bird.Toucan.Beak;`), where Toucan is the outer class.
#### Key takeways
- `instanceof` does a compile time check on classes, but not on interfaces.
- The `@Override` annotation can be only used on methods.
- `public String toString()` is the correct signature.
- `public boolean equals(Object o)` is the correct signature.
  - The `String` class has an implementation, the `StringBuilder` not!
  - Watch out for the passed argument. If not `Object` they are overloading it instead of overrriding!
- `public int hashCode()` is the correct signature.
  - Watch out for the letter casing!
  - Watch out for the `int` return type. Returning `long` would not compile!
- Enums
  - `values()`, `name()`, `ordinal()`, `valueOf(string)` methods.
  - Comparing an `enum` with an `int` won't compile!
  - You can't extend an enum! (Compilation error!)
  - Enums and Switches
    - Use the "shortened" names in the `case` statement. Using the extended (`case Seasons.FALL`) **leads to compilation error**
    - Using constant `int`s in the `case` is also a compilation error.
  - Enums and other class members
    - **Semicolon** required if if the enums has anything more than values! **ALWAYS**
    - Constructor is private, and called only once!
- Nested classes
  - <img src="./res/nestedclasses.png" alt="Drawing" style="width: 600px;"/>


#### Learnings
- Watch out for the correct overriding. A class might **overload** the `equals` method passing a type of its own. (`equals(Employee e)`) In this case if you call the `equals` method **manually** (`sout(e1.equals(e2))`) the overloaded version is called (just as a normal method). However if you add such objects with the same content to a set it will calle the `equals(Object o)` method to compare. Having it not implemented, it will add both instances.
- Watch out for `hashCode` implementations that use improper (often changing) variables.
- Only nested static classes are permitted to contain statics.
- A method local inner function might shadow & copy a variable of the outer class: `private int x = Outer.this.x;` Learn the sick syntax.
- `(null instanceof Chipmunk)` is correct and compiles. Returns false.
- Watch out for interfaces in the expressions with `instanceof`, as they compile!
- Member inner classes must be created via an instance of the outer class: `Outer.Inner in = new Outer().new Inner();`
- Enums are only allowed to have `private` constructors.
