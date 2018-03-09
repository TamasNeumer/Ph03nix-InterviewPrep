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
- **String comparison:**
  - `==`compares the reference. Use the `.equals()` method to check whether the contents are equal.
  - The `substring` function creates strings using the `new` keyword and hence these are not stored in the string pool! (This is probably true for all string methods that return a new string!)
- **Mutable strings**
  - **StringBuilder**
    - Use when dealing with large strings or content is modified often.
    - Uses a **non-final** char array to store the text.
    - The default capacity when created with the no-arg constructor is 16 characters.
    - `append()` The append method adds the specified value at the end of the existing value of a `StringBuilder` object. The method can accept data of any type. (int, char, char array, double, string, Object.) --> If you add Objects using the StringBuilder make sure these objects have their `toString` method correctly implemented.
      - For classes that haven’t overridden the toString method, the append method results in appending the output from the default implementation of method toString defined in the class Object (if the parameter isn’t null).
      - When appending boolean values "true"/"false" are appended.
    - `insert()` - enables you to insert data at the desired position.
      - Note that the `insert(whereToPosition, fromString, startIndex, endIndex)` version is exclusive on the end, however the `insert(whereToPosition, fromCharArray, startIndex, length)` is inclusive, since it uses the length.
    - `delete(fromIndex, toIndex)`, `deleteCharAt(index)`
    - **TRIM method doesn't exist in StringBuilder**
    - `replace(fromIndex, toIndex, newConent)` replaces the range (exclusive on the end) with the new content.
    - `subSequence(fromIndex, toIndex)` - like substring but it returns a `CharSequence`
    - `StringBuilder` is **NOT** synchronised! (StringBuilder is.)

#### Arrays
- An array is an **object** that stores a collection of values. It stores references to the data it stores.
- **Storage**
  - A collection of primitive data types. (An array of primitives stores a collection of values that constitute the primitive values themselves.)
    - `int intArray[] = new int[] {4, 8, 107};`
  - A collection of objects. (An array of objects stores a collection of values, which are in fact heap-memory addresses or pointers.)
    - `String objArray[] = new String[] {"Harry", "Shreya", "Paul", "Selvan"};``
- **Dimensions**
  - Single dimension:
    - `int intArray[]`, `String[] strArray`
  - Multi dimension:
    - `int[] multiArray[]`
  - It seems that you can place the brackets anywhere!
  - Because no elements of an array are created when it’s declared, it’s invalid to define the size of an array with its declaration. (`int intArray[2];` --> WRONG!) You have to inizialize it (using the `new` keyword.)
- **Allocation**
  - Done with the `new` keyword. You **MUST** provide the array size, that has to be an integer.
  - `multiArr = new int[][3];` won't compile because you **must specify the size of the first dimension** too! However `multiArr = new int[2][]` is okay. `int [][][] iaaa = new int [3][][];` is also okay.
  - Default value is 0/0.0 for primitives (false for boolean) and null for references.
- **Initialization**
  - Filling the array with values.
  - You can combine the above described steps in one line. In this case you **don't** necessarily need the ´new´ keyword. --> `String[] strArray = {"Summer", "Winter"};` (You can use it though, but make sure you **DON'T** specify the size here either. --> `new int[]{1,3}` It is calculated for you by the compiler.) Such "without new" initialization only works in-line.
- **Arrays of type Interface, Class etc.**
  - If the type is interface, then the array can store classes that implement the interfaces (or null).
  - If the type of an array is an abstract class, its elements are either null or objects of concrete classes that extend the relevant abstract class.
  - Array of Objects --> can point to any objects.
- **Member functions and properties**
  - `length`
  - `clone()`
  - Inherited methods from `Object`

#### ArrayList
- **Creating an array list**
  - use the **import statement** `import java.util.ArrayList;`
  - Declare an object: `ArrayList<String> myArrList = new ArrayList<>();`
  - Initial size is 10.
  - In the source code ArrayList uses an array of Objects. This is the reason why you can't store primitive types in the ArrayList. `private transient Object[] elementData;`
- **Methods**
  - **Adding elements**
    - `add(Object o)`, `add(position, Object o)`, `addAll(...)`
    - ArrayList automatically resizes itself, if needed.
    - Watch out! - you are adding references to these lists. Hence after the `addAll()` method you have two lists pointing to the same set of objects! Modifying the underlying objects in any of the lists is visible to the others!
  - **Accessing elements**
    - Enhanced for loop, `.listIterator()`, `.Iterator()`
  - **Modifying elements**
    - `set(posIndex, content)`
  - **Deleting elements**
    - `remove(index)`, `remove(Object o)`
    - Comparison is done by calling the class' `equals()` method. Note that `myArrList.remove(new StringBuilder("Four"));` won't remove the element because StringBuilder doesn't override the `equals()` method.
  - **Clearning**
    - `clear()`
  - **Cloning**
    - `clone()` returns a shallow copy. (References are copied but not the underlying objects.)
  - **Others**
    - `get(int index)`, `size()`, `contains(Object o)`, `indexOf(Object o)`, `lastIndexOf(Object o)`, `toArray()`

#### Comparing objects for Equality
- The method `equals` is defined in class java.lang.Object. All the Java classes directly or indirectly inherit this class. The default implementation of the equals method only compares whether two object variables refer to the same object.
- A sample override of the equals method:
  ```java
  public boolean equals(Object anObject) {
          if (anObject instanceof BankAccount) {
              BankAccount b = (BankAccount)anObject;
              return (acctNumber.equals(b.acctNumber) &&
                acctType == b.acctType);
          }
          else
            return false;
      }
  }
  ```
- Watch out for the correct signature!
  - Return type is `boolean`
  - Function name is `equals`
  - Arguments are type of `Object`
- **Contract of the equals method**
  - It is **reflexive**: for any non-null reference value x, x.equals(x) should return true.
  - It is **symmetric**: for any non-null reference values x and y, x.equals(y) should return true if and only if y.equals(x) returns true.
  - It is **transitive**: for any non-null reference values x, y, and z, if x.equals(y) returns true and y.equals(z) returns true, then x.equals(z) should return true.
  - It is **consistent**: for any non-null reference values x and y, multiple invocations of x.equals(y) consistently return true or consistently return false, provided no information used in equals() comparisons on the objects is modified.
  - For any non-null reference value x, **x.equals(null)** should **return false**.

### DateTime API
- They are in package `java.time` and they have no relation at all to the old `java.util.Date` and `java.sql.Date`.
- `java.time.temporal.TemporalAccessor` is the base interface that is implemented by LocalDate, LocalTime, and LocalDateTime concrete classes.
- `LocalDate`, `LocalTime`, and `LocalDateTime` implement `TemporalAccessor` and extend `java.util.Date`.
-  LocalDate, LocalTime, and LocalDateTime classes do not have any parent/child relationship among themselves.


- **LocalDate**
  - Used to store dates (without time or timezone).
  - Immutable, hence thread-safe.
  - **Creation**
    - Constructor is private --> use factory methods!
      - `of()` - `LocalDate.of(2015,12,27)`
      - `now()` - `LocalDate.now()`
      - `parse()` - `LocalDate.parse("2025-08-09")`
      - (On invalid input for any of these functions you get a `TimeParseException`)
    - **Query**
      - `date.getDayOfMonth()`, `getDayOfWeek`, `getDayOfYear`, `getMonth`, `getMonthValue`, `getYear`
      - Chronological comparison: `.isAfter(otherDate)`, `.isBefore(otherDate)`
    - **Modification**
      - `plusXXX`, `minusXXX` (Days/Weeks etc.). These consider leap years.
      - `withXXX` - replaces the content in the date. e.g.: `xDate.withYear(2009)` --> same date, but year changed
      - `atTime(16,30)` --> returns a LocalDateTime with the time specified
      - `toEpochDay()` - number of days since Jan 1 1970
- **LocalTime**
  -  It stores time in the format hours-minutes-seconds (without a time zone) and to nanosecond precision.
  - Immutable and thus thread-safe
  - The `of()` method uses a 24-hour clock to specify the hour value.
    - You’ll get a compiler error if the range of values passed to a method doesn’t comply with the method’s argument type. (i.e. number > integer allowed range)
    - You will get runtime exception if the number is within the integer range but is greater than 24 for hours for example.
    - The format must be **99:99:99**
  - `getHour`, `getMinute`, `getSecond`, `getNano` **NOT IN PLURAL!**
  - `isAfter`, `isBefore`, `plus/minus` functions are the same.
    - The plus/minus methods are in **PLURAL**
  - `atDate(date)` - to return a complete LocalDateTime
**LocalDateTime**
  - The LocalDateTime class uses the letter T to separate date and time values in its printed value.
    - `2050-06-18T14:20:30:908765`
**Period**
  - The static methods `of()`, `ofYears()`, `ofMonths()`, `ofWeeks()`, and `ofDays()` accept int values to create periods of years, months, weeks, or day.
  - A period of 35 days is not stored as 1 month and 5 days. Its individual elements, that is, days, months, and years, are stored the way it is initialized.
    - You can also define negative periods by passing negative integer values to all the preceding methods.
  - You can also parse a string to instantiate Period by using its static method `parse()`. This method parses string values of the format `PnYnMnD` or `PnW`, where n represents a number and the letters (P, Y, M, D, and W) represent parse, year, month, day, and week.
    - If you pass invalid string values to `parse()`, the code will compile but will throw a runtime exception.
  `between(localDate1, localDate2)` - returns the peroid.
  - Because `Period` instances can represent positive or negative peri- ods (like 15 days or -15 days), you can subtract days from a `LocalDate` or `LocalDateTime` by calling the method `plus`.
    - `dateTime.minus(Period.ofYears(2));`
  - Adding a Period of 10 months to a Period of 5 months gives 15 months, **not** 1 year and 3 months.
**DateTimeFormatter**
- Used to format and parse date and time objects.
- You can instantiate or access a DateTimeFormatter object in multiple ways:
  - By calling a static `ofXXX` method, passing it a FormatStyle value
    - (FormatStyle.FULL/LONG/MEDIUM/SHORT)
  - By access public static fields of DateTimeFormatter
  - By using the static method ofPattern and passing it a string value
  ```java
  DateTimeFormatter formatter =
    DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
  LocalDate date = LocalDate.of(2057,8,11);
  System.out.println(formatter.format(date));
  ```
- If you pass a date object to the method format on a DateTimeFor- matter instance that defines rules to format a time object, it will throw a run- time exception.
- When calling parse on `LocalDate`, `LocalTime`, or `LocalDateTime` instances, you might not specify a formatter. In this case `DateTimeFormatter.ISO_LOCAL_DATE`, `DateTimeFormatter.ISO_LOCAL_TIME`, and `DateTimeFormatter.ISO_LOCAL_DATE_TIME` are used to parse text, respectively.
#### Key takeaways
- Literals are also placed in the string pool! `System.out.println("literal")``
- The `String`, `StringBuilder`, `StringBuffer` classes don't have constructors taking a character!
- The Java compiler doesn’t check the range of the index positions at which you try to access an array element. It only checks whether it is a **whole number** (byte, char, short, int) or not! `System.out.println(intArray[-10]);` will compile happily, while `System.out.println(intArray[10.2]);` won't.
- You can assign a character to an int array! (Assigning a char value to an int array element `(arr[1] = 'c')`)
- You MUST add longs to a Long ArrayList. (Without l it doesn't compile.)
  ```java
  ArrayList<Long> lst = new ArrayList<>();
  lst.add(10l);
  ```
- String class has the `equals` method overridden.
- When the + operator encounters a String object, it treats all the remaining operands as String objects. (int + int + String + int + int)
- The ArrayList class has the `toString` method overriden!
- Watch out: Boolean, Byte, Character, Double, Float, Integer, Long, Short, String objects are immutable! Even if you add the references of these to multiple lists, changing the original objects value won't be reflected in the list, as in this case new objects are created!
- get(Hour/Day...)/with(...) functions are in **SINGULAR**
- plus(Days/Hours) functions are in **PLURAL**
- `String printDate = LocalDate.format(DateTimeFormatter.ISO_DATE_TIME).parse("2057-08-11");` won't compile because of the order of the functions.
