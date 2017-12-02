## Primitive Types
#### Size of fundamental types (C++)
In C/C++ the actual size depends on the processor for which a program is compiled. In Java it is not the case.

Type  |  Size
--|--
bool, char, unsigned char, signed char int8|  1 byte
int16, short, unsigned short, wchar_t, wchar_t  |  	2 bytes
float, int32, int, unsigned int, long, unsigned long  |  4 bytes
double, int64, long double, long long  |  8 bytes

#### Size of fundamental types (Java)
Type  |  Size
--|--
bool |  1 byte
short, char(utf)  |  	2 bytes
float, int |  4 bytes
double, long  |  8 bytes

#### Representing numbers in binary, decimal and octal
- `0xABC` --> Hex (Java,C++, )
- `0b1001` --> binary (Java, Cpp14+)
- `010` --> Octal. (Octab1l numbers have leading zero. Java, Cpp)

#### Java Notes
- Primitive types are not packed in an ojbect. They don't have reference, hence when passed to functions they are passed by value!

#### Char (Java)
- Chars are using UTF-16. (16 -> 16 bits. get it?)
- Minimum value is `\u0000` (default), maximum is `\uffff`
- Special chars such as single quote, backlash have to be escaped. (`\t \b \n \r \f \' \" \\`)
- `char a = 'a'; char A = 65`

#### Boolean (Java)
- booleans can have two values: `false` and `true`. There is no relation between bools and 0/1 as in C/C++. (Hence no implicit conversion.)

#### Byte (Java)
- A byte is a 8-bit signed integer. It can store a minimum value of -27 (-128), and a maximum value of 27 - 1 (127)

#### Long / Short (Java)
- By default, long is a 64-bit signed integer (in Java 8, it can be either signed or unsigned).
- Shorts are 16-bit signed integers.

#### Floats / Doubles (Java)
- A float is a single-precision 32-bit IEEE 754 floating point number. By default, decimals are interpreted as doubles. To create a float, simply append an f to the decimal literal. (`float floatExample = 0.5f;`)
- While using float is fine for most applications, neither float nor double should be used to store exact representations of decimal numbers (like monetary amounts), or numbers where higher precision is required. Instead, the `BigDecimal` class should be used.
  - In this case you have to use the class' methods: k.multiply(...) k.add(...) etc, as java doesn't permit the use operators with objects. [**No, Java doesn't support user-defined operator overloading!**] Operators are only overloaded on the string objects.
- A float is precise to roughly an error of 1 in 10 million.

#### Converting primitive types (Java)
- Widening conversion vs Narrowing conversion.
  - Java performs widening conversions automatically.
  - For narrowing you have to use case (`(int)0.5f`) explicitly, otherwise exception thrown at compile time.
- `boolean` is the only primitive data-type that cannot be converted.

#### Naming convention (Java)
- Java uses camelCase for function and variable naming (starting with a lower character). Class names start with a capital letter.
- Cpp uses `const`, java uses `final`
  - In Java use UPPERCASE variable names for constants. (Bit like `#define` in Cpp)
  - In Java you can define and later initialize. But once initialized with final you can't change it's value. (Hence you can define and initialize using a switch case.)

#### Bitwise operations
- Left shift: `>>`
- Right shift: `<<`
- Bitwise NOT: `~`
- Bitwise AND: `&&`
- Bitwise OR: `|`
- Bitwise XOR: `^`

#### Endianness
Endianness refers to the sequential order used to numerically interpret a range of bytes **in computer memory** as a larger, composed word value.
- Big-Endian: The digits are written starting from the left and to the right, with the most significant digit, 1, written first. (just like in decimal - e.g.: 123) This is analogous to the lowest address of memory being used first.
- Little-Endian: The little-endian way of writing the same number, one hundred twenty-three, would place the hundreds-digit 1 in the right-most position: 321.

#### Bitwise operation on Endianness
- The bitwise operators abstract away the endianness. For example, the >> operator always shifts the bits towards the least significant digit.
- However when referring to bytes **from memory** you have to consider endianness!
```cpp
short temp = 0x1234;
temp = temp >> 8; // -> temp = 0x0012, regardless endianness
// on little endian, c will be 0x12, on big endian, it will be 0x0
char c=((char*)&temp)[0];
```

#### Shifting (un)signed values
- Right shift of a negative signed number has implementation-defined behaviour.
  - Shifting right may fill "empty" bits with the original MSB (i.e. perform sign extension) or it may shift in zeros, depending on platform and/or compiler.
- A left shift, if the number either starts out negative (with 0 e.g.: 01010...), or the shift operation would shift a 1 either to or beyond the sign bit, has undefined behaviour (as do most operations on signed values which cause an overflow).
- The same operations on unsigned values are well-defined in both cases: the "empty" bits will be filled with 0.

Conclusion: Don't use shifting on singed numbers.


#### Bitwise operation tricks
- Reversing sign of number (+ <--> -)
  - Negate the number and add 1.
  - Why?
    - 01111111 = +127
    - 01111110 = +126
    - 01111101 = +125
      ...
    - 00000001 = +1
    - 00000000 =  0
    - 11111111 = -1
      ...
    - 10000010 = -126
    - 10000001 = -127
    - 10000000 = -128
- Find the right-most (1) bit: `y = x & ~(x-1)`
  - x-1 flips all bits until the right-most 1: x = 10100, x-1 = 10011.
  - Negating it results in 01100, making the numbers before the right-most 1 flip.
  - AND operation on these numbers thus yield 00100.
- Remove the right-most (1) bit: `y = x & (x-1)`
  - Subtracting 1 from the number flips the bits up to the first right-most 1, thus AND with this number sets these bits to 0.
- Set the rightmost 0 to 1: `y = x | (x+1)`
- Multiply by 2: Left shift <<
- Divide by 2: Right shift >>

#### Examples
1. Count the number of "1s" in the binary representation of a number.
2. Compute the party of a number. (Parity if the number of "1"s is odd, 0 otherwise.) --> Do better than the brute-force O(n)!
