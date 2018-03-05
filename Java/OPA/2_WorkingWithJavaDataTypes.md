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
- Arithmetic: `+ - * / % ++ --`
- Relational: `< <= > >= == !=`
- Logical `!, &&, ||`

####





#### Key takeaways
- Watch out for the variable naming. (`bool` instead of `boolean`). These f@#*&#s love to play around with letter casing and C-type variable names.
