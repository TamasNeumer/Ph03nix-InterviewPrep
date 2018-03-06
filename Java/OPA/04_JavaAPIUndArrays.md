# Java API
#### Strings
- **Creating Strings**
  - `If a String object is created using the keyword new, it always results in the creation of a new String object. String objects created this way are never pooled. When a variable is assigned a String literal using the assignment operator, a new String object is created only if a String object with the same value isn’t found in the String constant pool.
  - `==` compares reference while the `.equals()` method compares the string values.
  - String constructors
    - `new String("asd")`
    - `new String(new char[] {'a','s','d'})`
    - `new String(stringBuilderInstance)` and `new String(stringBufferInstance)`
- **Immutability**
  - Strings are immutable. All the methods defined in the class String, such as substring, concat, toLowerCase, toUpperCase, trim, and so on, which seem to modify the contents of the String object on which they’re called, create and return a new String object rather than modify the existing value.
- **String Functions**
  - `charAt(int position)` - Index starts from 0. Watch out for not exceeding the string length, otherwise exception.
  - `indexOf(String in)` - If the specified char or String is found in the target String, this method returns the first matching position; otherwise, it returns -1.
  - `substring(int indexFrom)`, `substring(int indexFrom, int indexTo)`
    - **Inclusive on the FROM and exclusive on the TO side.**
  - `trim()` - return all trailing and leading white spaces.
  - `replace()` - return a new String by replacing all the occurrences of a char with another char. You can also specify strings to replace.
  - `length()` - number of characters
  - `startsWith(String prefix) / endsWith(String suffix)` determine whether the string starts / ends with a given prefix/suffix.
    -  The methods startsWith and endsWith accept **only arguments of type String**.
  - Since most of these methods return a string you can use **method chaining.** Method chaining is one of the favorite topics of the exam authors. You’re sure to encounter a question on method chaining in the OCA Java SE 8 Programmer I exam.
- **String operators**
  - Concatenation (`+=`)
  - The `+` operator can be used with the primitive values, and the expression intVal + intVal2 + aStr is evaluated from left to right. --> Result: (intVal + intVal2)aStr --> **the numbers are added first and only then appended!**



#### Key takeaways
- Literals are also placed in the string pool! `System.out.println("literal")`
