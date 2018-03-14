# Java Data Types

#### Primitive Data Types
- Java is a strongly typed language. You must declare a variable and define its type before you can assign a value to it.
- Java defines the following eight primitive data types:
  - `char, byte, short, int, long, float, double, boolean`
- **boolean**
  - can store one of two values: `true` or `false`
- **signed numeric**
  - **Integers** byte (8 bits), short (16 bits), int (32 bits), long (64 bits)
    - To designate an integer literal value as a long value, add the suffix L or l (L in lowercase), as follows: `long fishInSea = 764398609800L;`
    - Integer literal values come in four flavours: binary (0b10101), decimal, octal (0413 - leading zero), and hexadecimal (0x1001).
    - Since Java 7 underscores (`_`) can be used in order to make numbers visually more readable. (e.g.: `0x10_BA_75;`)
      - You can't use an underscore prefix before the `L` suffix or place these after the prefixes `0b`, `0x`. The octal prefix is an exception hence `0_100_267` is a valid octal number.
      - Also the prefix can't be the first nor the last element of a number. (_100 or 100_)
      - You can’t use an underscore in positions where a string of digits is expected. (`int i = Integer.parseInt("45_98");` will throw a **runtime** exception)
  - **Floats**
    - Float (32 bits), Double (64bits)
    - The default type of a decimal literal is double, but by suffixing a decimal literal value with F or f (e.g.: 1765.65f), you tell the compiler that the literal value should be treated like a float and not a double.
    - Underscore rules
      - You can’t place an underscore prior to a D, d, F, or f suffix
      - You can’t place an underscore adjacent to a decimal point.
  - **Character**
    - A char is an **unsigned integer**. It can store a single 16-bit Unicode character; that is, it can store characters from virtually all the existing scripts and languages.
    - Use **single** quotes to assign character values! (Otherwise compile error.)
    - Since internally stored as an integer you can assign numbers to chars. Assigning a negative number won't compile.
    - `char c3 = (char)-122;` is valid. You are casting in this case.

#### Identifiers
- Starts with a letter/currency sign/underscore, can use underscore and digits and currency signs in the name anywhere. Any other character is forbidden.
- camelCase is used in Java.
- You cant use keywords as identifiers.

#### Object reference variables
- Objects are instances of classes, including both predefined and user-defined classes. For a reference type in Java, the variable name evaluates to the address of the location in memory where the object referenced by the variable is stored. An object reference is, in fact, a memory address that points to a memory area where an object’s data is located.
- The default value of all types of object reference variables is null. You can also assign a null value to a reference variable explicitly.
- Reference variables vs primitive variables: The basic difference is that primitive variables store the actual values, whereas reference variables store the addresses of the objects they refer to.

#### Operators
- Assignment: `= += -= *= /=`
  - Note that unlike in Cpp there is no implicit type conversion. (`boolean myValue = 1;` is an invalid expression!)
  - Also assigning a long variable to an integer will result in compile error. (`int val = myLongVar;`) In order to squeeze it in you **must** cast it manually.
  - You can assign multiple values on the same line using the assignment operator. (`a = b = c;`)
- Arithmetic: `+ - * / % ++ --`
  - When you apply the addition operator to char values, their corresponding ASCII values are added and subtracted.
  - All byte, short, and char values are **automatically widened** to int when used as operands for arithmetic operations. If a long value is involved somewhere, then every- thing, including int values, is widened to long.
    - `short a = byteVar1 + byteVar2;` - "possible lossy conversion from int to short" --> when added the result is stored in an integer, and hence int -> short is not allowed!
    - Having `final` variables, the compiler it works.
  - When a unary operator is used in an expression, its placement with respect to its variable decides whether its value will increment or decrement before the evaluation of the expression or after the evaluation of the expression.
    ```java
    int a = 10;
    a = a++ + a + a-- - a-- + ++a;
    // a = 10 + 11 + 11 - 10 + 10;
    ```
    - The evaluation of an expression starts from left to right. For a prefix unary operator, the value of its operand increments or decrements just before its value is used in an expression. For a postfix unary operator, the value of its operand increments or decrements just after its value is used in an expression.
  - The modulo operator (%) works with floats as well!
- Relational: `< <= > >= == !=`
  - You can't compare incomparable values. It won't compile. (int < boolean etc.)
  - People often miss and use assignemnt operator instead of boolean equality.
    ```java
    System.out.println(b1 = true);  // prints true
    System.out.println(b1 = false); // prints false
    ```
- Logical `!, &&, ||`
  - Another interesting point to note with respect to the logical operators `&&` and `||` is that they’re also called short-circuit operators because of the way they evaluate their operands to determine the result. **However `|` and `&` operators evaluate the full expression no matter what.**
    - The && operator returns true only if both the operands are true. If the first operand and to this operator evaluates to false, the result can never be true. Therefore, && does not evaluate the second operand. Similarly, the || operator.
    - This is especially important if you have statements like `total < marks && ++marks > 5` because you must know that in this case the value of marks won't change.
- Bitwise `| & ~ ^ << >> >>>`
  - `>>>` is the unsigned right shift. There is no such thing as `<<<`!
- **Operator precedence** (highest at the top)
  - Postfix `Expression++, expression--`
  - Unary `++expression, --expression, +expression, -expression, !`
  - Multiplication `* (multiply), / (divide), % (remainder)`
  - Addition `+ (add), - (subtract)`
  - Relational `<,>,<=,>=`
  - Equality `==, !=`
  - Logical AND `&&`
  - Logical OR `||`
  - Assignment `=, +=, -=, *=, /=, %=`
  - Use parentheses to overwrite the precedence.

#### Wrapper classes
- Java defines a wrapper class for each of its primitive data types. The wrapper classes are used to wrap primitives in an object, so they can be added to a collection object.
- You can create objects of all the wrapper classes in multiple ways:
  - Assignment—By assigning a primitive to a wrapper class variable (autoboxing)
  - Constructor—By using wrapper class constructors
  - Static methods—By calling static method of wrapper classes, like, valueOf()
- Wrapper classes are immutable. Adding a primitive value to a wrapper class variable doesn’t modify the value of the object it refers to. The wrapper class variable is assigned a new object.
- **Character does NOT** have a constructor accepting strings!
- Wrapper classes **DON'T** have no arg constructors!!!
- To  retrieve primitive values from wrapper classes use the `**Value()` function. (byteValue(), shortValue() ...)
- To get a primitive data type value corresponding to a string value, you can use the static utility method parseDataType, where DataType refers to the type of the return value. (`parseByte()`, `parseInt()` etc. --> **parseChar doesn't exist!**)
  - All parse methods throw `NumberFormatExceptionÂ` except `Boolean.parseBoolean()`. This method returns false whenever the string it parses is not equal to “true” (**case-insensitive** comparison).
- Wrapper classes Byte, Short, Integer, and Long **cache objects** with values in the **range of -128 to 127**. (The Character class caches objects with values 0 to 127.). If you request an object of any of these classes, from this range, the valueOf() method returns a reference to a predefined object; otherwise, it creates a new object and returns its reference.
  - Wrapper classes Float and Double **don’t cache** objects for any range of values.
  - In the case of the Boolean class, the cached instances are accessible directly because only two exist: static constants `Boolean.TRUE` and `Boolean.FALSE`.
- **Method `equals()` always compares the primitive value stored by a wrapper instance, and `==` compares object references.**
- **Autoboxing and `valueOf()`** use caching (in the above mentioned range)!
- You can’t compare wrapper instances for equality using equals() or ==, if they aren’t of the same class.
  - `System.out.println(shortObj1.equals(IntegerObj2));` --> prints false
  - `System.out.println(shortObj1 == IntegerObj2);` --> doesn't compile

#### Key takeaways
- Watch out for the variable naming. (`bool` instead of `boolean`). These f@#*&#s love to play around with letter casing and C-type variable names.
- In arithmetic operations the components are caseted (to at least) int, hence ``System.out.printline('a' + 'b')`` will result in an integer.
  ```java
  public class Prim {
      public static void main(String[] args) {
          char a = 'a';
          char b = -10;
          char c = '1';
          integer d = 1000;
          System.out.println(++a + b++ * c - d);
      }
  }
  ```
  - So the last line also fails to compile because it uses variables that were introduced in lines that also failed!
- The constructor type reference creation always creates a new instance without caching. (Even in the case of Booleans)
- Whenever you see "Compilation error" as a possible answer check for minor issues (unclosed parentheses, wrong primitive type names etc.)
- a short VARIABLE can NEVER be assigned to a char without explicit casting. A short CONSTANT can be assigned to a char only if the value fits into a char.
