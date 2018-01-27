## Java Platform
#### 1. Why is Java so famous?
- Platform independence, Object oriented language

#### 2. What is platform independence?
-  Build once run anywhere
- Java code (.java) is compiled to byte code (.class) by your JDK's compiler
- Then the .class is JIT compiled to machine code.

#### 3. Look at JDK versus JVM versus JRE
- JDK: javac, jar, debugging tools etc.
- JRE: java, javaw, libraries, .jar + JVM: Just in time compiler (JIT)

#### 4. What are the essential contrasts amongst C++ and Java?
- Java is platform independent and JIT compiled
- Java is "more" Object-oriented. In C++ you can write completely functional code.
- No pointers, hence no direct access to memory.
- Memory management -> Garbage collection in Java.
- Multiple inheritance is supported in C++, while not in Java.
  - One reason why the Java programming language does not permit you to extend more than one class is to avoid the issues of multiple inheritance of state, which is the ability to inherit fields from multiple classes. For example, suppose that you are able to define a new class that extends multiple classes. When you create an object by instantiating that class, that object will inherit fields from all of the class's superclasses. What if methods or constructors from different superclasses instantiate the same field? Which method or constructor will take precedence? Because interfaces do not contain fields, you do not have to worry about problems that result from multiple inheritance of state. (Oracle docs.)
  - Tl;DR: You **can IMPLEMENT multiple** interfaces as these don't have field variables, however you **can't EXTEND multiple** classes.

#### 5. What is the part for a classloader in Java?
- Java uses the "ClassLoaders" to load the content of all the needed classes. Classes are searched for in the following order:
  - System Class Loader - loads all classes from CLASSPATH
  - Extension Class Loader - Loads all the classes from extension directory
  - Bootstrap Class Loader - Loads all the Java core files

## Wrapper Classes
#### 6. What are Wrapper classes?
- Wrapping classes around the primitive types, and it gives an object appearance.
- Boolean, Byte, Character, Float, Short, Integer, Long, Double.
- Why we do need them? To use them in collections. To allow "null" value (?!)

#### 7. What are the distinctive methods for making Wrapper class occasions?
- Using wrapper class constructor `new Integer(55)` or `new Integer("55")`
- ValueOf methods `Integer.valueOf("100")`

#### 8. What are differences in the two methods for making Wrapper classes?
- If you use the constructor you **always** create a new object, while using `valueOf()` static method it may return you a cached value with-in a range.
  - The cached values for long are between [-128 to 127] --> Hence using static method is better to save memory.
- BTW: Such wrapped objects are **IMMUTABLE**

#### 9. What is auto boxing?
- Automatic conversion of primitive types and their corresponding object wrapper classes.
- Autoboxing uses the **static valueOf** method

#### 10. What are the benefits of auto boxing?
- Memory saving by reusing already created Wrapper objects.

#### 11. What is casting?
- Converting one type to anohter
  - Implicit casting: Automatically done by the compiler. Automatic **widening** castings.
    - `int value = 100;` and `long number = value` --> implicit casting
  - Explicit casting: `int value = (int) longValue` --> Forcing **narrowing** casting.

## Strings
#### 12. Are all String's immutable?
- Yes they are.
- Thus for example the ``stringVar.concat("Value2")`` returns a new String instance.
  - toLowerCase, toUpperCase etc. **all create a new String**

#### 13. Where are String values stored in memory?
- **String constant pool** in the **heap memory**
- `String str1 = "value"; String str2 = "value"` --> same memory is used!

#### 13. Why would it be a good idea for you to be watchful about String concatenation(+) administrator in circles?
- Each concatenation creates a new object.
- Recommended to use a `StringBuilder` or `StringBuffer`

#### 14. How would you take care of above issue?
- ``StringBuffer`` is thread-safe, ``StringBuilder`` is not!
  - ``StringBuffer s3 = new StringBuffer("Value1"); s3.append("haha");``

#### 15. What are the differences between String and StringBuffer?
- Strings are immutable. StringBuffer is used to represent values taht can be modified.
- Both String and StringBuffer are thread-safe.
- StringBuffer is implemented usint eh `synchroznized` keyword on all methods.

#### 16. What are contrasts amongst StringBuilder and StringBuffer?
- StringBuffer is thread-safe, while StringBuilder is not!

## Object oriented programming nuts and bolts
#### 17. What is a class?
- A class is a template for creating multiple objects. A class defines state and behavior that an object can exhibit.

#### 18. What is an object? What is the state of an object? What is the behavior of an Object?
- An instance of a class.
- State = valued assigned to instance variables of an object.
- Behavior = methods supported by an object.

#### 19. What is the super class of each class in Java?
- Every class is a sub-class of the `Object `

#### 20. Explain the toString() method?
- Used to print the content of an object. (If not overridden the Object class' implementation is used.)
- `public String toString()` is the syntax!

#### 21. Equals method in Java?
- ``Obj1 == Obj2`` won't be true, unless they have the same reference.
- Steps: Override method using the correct Signature, Typecast, Comparison.

  ```java
  @Override
  public boolean equals(Object obj){
    Client other = (Client) obj;
    if(id != other.id)
      return false;
    return true;
  }
  ```

#### 22. What are the basic rules of equals method?
- Reflexive (equal with itself)
- Symmetric (a=b -> b=a)
- Transitive (a=b && b=c --> a = c)
- Consistent (return same result over and over with same conditions)
- For any non-null reference ``x.equals(null)`` should return ``false``.

  ```java
  @Override
  public boolean equals(Object obj){
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if(getClass() != obj.getClass())
      return fasle
    /*... Same logic as before ...*/
  }
  ```

#### 23. What is the hashCode method used for in Java?
- Hash codes are used in hashing to decide which group (or bucket) an object should be placed into.
- The quality/implementation decides the effectiveness.
- hashCode properties:
  - If two objects are equal, their hash has to be equal. However if they are not equal, it doesn't mean that hashCode must be different. Two unequal objects **might** have the same hashCode.
  - Consistency
- For proper implementation see [Effective Java](https://stackoverflow.com/questions/113511/best-implementation-for-hashcode-method)

## Inheritance
#### 24. What is inheritance?
- Reuse of class templates.

#### 25. What is method over-loading?
- Method with a same name taking different number/types of arguments.
- Overloading in **same class**
- Overloading in **sub-class**

#### 25. What is method overriding?
- Creating a Sub-class method with the same signature as of the Super Class', and changing behavior.
- Examples: ``HashMap public int size()`` overrides ``AbstractMap public int size()``

#### 26. Can super class reference variable hold an object of sub class?
- Yes.
  - `Object object = new Hero()` is allowed, however you can only use Object's methods.
- Or classic `Animal animal = new Cat()`

#### 27. Is multiple inheritance allowed in Java?
- Inheritance yes, extension no!
- You can create extension chain.

#### 28. What is an interface?
- An interface defines a **contract** for responsibilities (methods) of a class.
- Examples: List, Map, Collection interface.

#### 29. How would you define/create an interface?
- `public interface MyInterface{}`
- All methods are pubblic in the interface.

#### 30. How would you implement an interface?
- `public class MyClass implements MyInterface{ @Override ... }`

#### 31. Can you explain a few of tricky things about interfaces?
- **Variables** in an interface are **always** ``public static final``
- Since Java7 you can create `default` method implementations.

#### 32. Can you extend an interface?
- An **Interface can extend ANOTHER Interface** (but NOT a class!)

#### 33. Can a class extend multiple interfaces?
- Multiple interfaces IMPLEMENTATION yes, EXTENSION no.
- e.g.: HashMap implements Map, Clonebale, Serializeable
- (Clonable, Serializeable are **marker interfaces**)
  - Marker interface in Java is interfaces with no field or methods. In simple words, empty interface in java is called marker interface.

#### 34. What is an abstract class?
- A class that can't be instantiated. Usually at least a function is **not** implemented.
- Marked with the `abstract` keyword.
- Example: ``AbstractMap``. (get, put, isEmpty(), containsKey(Object o) are implemented however `entrySet()` is not implemented.

#### 35. When do you use an abstract class?
- Provide a common implemented functionality among all implementations of your component.

#### 36. Abstract class vs Interface?
- Visibility (abstract mehtods can be private)
- You can only extend one abstract class, however implement multiple interfaces.
- Child-class can define methods with the same or less restrictive visibility.

#### 37. What is a constructor?
- A constructor is invoked to create an instance of a class.

#### 38. What is a default constructor?
- Compiler generated constructor. It has **no arguments**. (Making sure that you can create an instance even if you haven't specified a constructor)

#### 39. How do you call a Super Class Constructor from a Constructor?
- Using the `super()` keyword. It must be the **FIRST** statement in the constructor! (Otherwise compile error.)
- Reason: The parent class' constructor needs to be called before the subclass' constructor. This will ensure that if you call any methods on the parent class in your constructor, the parent class has already been set up correctly.

  ```java
  public Animal(){
    super();
    this.name = "Default name";
  }
  ```

#### 40. What is the use of this()?
- Another constructor of the same class can be invoked in the constructor using `this(argument)`

#### 41. Can a constructor be called directly from (another) method?
- No.

#### 42. Is a super class constructor called even when there is no explicit call from a sub class constructor?
- Yes. (You call the super class' default constructor.)

## Advanced Object Oriented Ideas:
#### 43 . What is polymorphism?
- **Dynamic method lookup** (Same code giving different behavior.)

#### 44. What is the utilization of ``instanceof`` function in Java?
- Determines if a given class is an instance of another class.
- `subclass instanceof SubClass`

#### 45. What is coupling?
- Measure of how much a class depends on other classes.
- Assume that you have a ShoppingCart with items in it. If you have the following function:
  - `... cart.items.length` ... --> You are highly coupled as you know the implementation details! If `Cart` changes its own implementation, then the client code must also change!
  - Solution use private properties, and methods to access them. (**This** is the true essence of getters/setters, to achieve low coupling.)
- Same with using classes directly instead of interfaces!

#### 46. What is cohesion?
- How related the responsibilities of a class are. A class' method should be highly related.
- **Problem**: Class is opened and changes too many times.
- Better approach would be `Downloader`, `Parser`, `Storage` classes.

  ```java
  class DataStorage{
    /*...*/
    void doEverything{
      downloadFromInternet();
      parseData();
      storeIntoDatabase();
    }
    /*...*/
  }
  ```

#### 47. What is encapsulation?
- Hiding the implementation of a Class behind a well defined interface.
- Helps us to change implementation of a class without braking other code (that depends on our class).

#### 48. What is an inner class? What is a static inner class?
- Inner class is a class created inside another class.
- Also called nested classes

  ```java
  public class OuterClass{
    public class InnerClass{}
    public static class StaticNestedClass {}
  }
  ```

- Static nested classes can be created without their parent.
  - `OuterClass.StaticNestedClass o = new OuterClass.StaticNestedClass()`
  - You cannot access OuterClass variables. (since they are not static)
- For a normal inner class you need an instance of the OuterClass.
- In **method local inner classes** you can access **final** variables.

  ```java
  class OuterClass {
  	private int outerClassInstanceVariable;

  	public void exampleMethod() {
  		int localVariable;
  		final int finalVariable = 5;

  		class MethodLocalInnerClass {
  			public void method() {
  				// Can access class instance variables
  				System.out.println(outerClassInstanceVariable);

  				// Cannot access method's non-final local variables
  				// localVariable = 5;//Compiler Error
  				System.out.println(finalVariable);// Final variable is fine..
  			}
  		}

  		// MethodLocalInnerClass can be instantiated only in this method
  		MethodLocalInnerClass m1 = new MethodLocalInnerClass();
  		m1.method();
  	}
  }
  ```

#### 49. What is an anonymous class?
- It is an inner class without a name and for which only a single object is created. An anonymous inner class can be useful when making an instance of an object with certain “extras” such as overloading methods of a class or interface, without having to actually subclass a class.
- Useful when you create a single instance in the entire application.

  ```java
  /* Second Anonymous Class - SubClass of Animal */
  		Animal animal = new Animal() {
  			void bark() {
  				System.out.println("Subclass bark");
  			}
  		};

  		animal.bark();// Subclass bark
  ```

## Modifiers
#### 50. What is default class modifier?
- A class is called **Default Class** if there is no access modifier on a class.
- Visible **inside the same package only**. ("Package private")

#### 51. What is private access modifier?
- Private members and methods can be only accessed within the class.
- **NOT** available in child class.

#### 52. What is default or "package" access modifier?
- Default variables and methods can be accessed in the **same package Classes**
- Default variables and methods from SuperClass are available only to SubClasses in same package.

#### 53. What is protected access modifier?
- Vars and methods can be accessed in the **same** package Classes
- Protected vars and methods from SuperClass are available to SubClass in **ANY** package.

#### 54. What is public modifier?
- Available for all classes, as well as SubClasses.

#### 55. What access types of variables can be accessed to from a class in same package?
- Default, Public, Protected

#### 55. What access types of variables can be accessed to from a class in a different package?
- Public.
- Protected if Class is a child-class.

#### 56. What is the use of the `final` modifier on a class?
- Class cannot be inherited/extended. E.g.: String, Integer
- Making sure that they are not broken by sub-classes and pose security leaks.

#### 57. What is the utilization of a ``final`` modifier on a method?
- Cannot be overriden.

#### 58. What is a ``final`` variable?
- Once initialized cannot be changed.
- Note it's tricky with references. The reference's underlying value can change, however the reference (pointer) not. Hence if you have a reference to an List you can still change the content of the list.

#### 59. What is a final argument?
- Final arguments cannot be modified.

#### 60. What is a static variable?
- They become class level variables. --> One variable exists for the entire set of classes, and the variable is shared between these classes.
- You don't need an instance to access these.
- Static method is almost the same, however in a static method you can access only static variables and other static methods.

## Conditions and Loops
#### 61. Why should you use blocks around if statement?
- Then only the first line is part of the loop/ if statement.

#### 62. Guess the output
```java
int m = 15;
if(m>20)
if(m<20)
  System.out.println("m>20");
else
  System.out.println("Who am I?");
```

- Nothing. First condition is false and hence the "block" after it is not executed.

#### 63. Guess the output
```java
boolean isTrue = false;
if(isTrue==true) Sout("TRUE TRUE");
if(isTrue = true) Sout("TRUE");
```

#### 64. Guess the output
```java
int number = 2;
switch (number){
  case 1:
    System.out.println(1);
  case 2:
    System.out.println(2);
  case 3:
    System.out.println(3);
  default:
    System.out.println("Default");
}
```

- Output: 2 3 Default. (No breaks!)
- BTW: default can be anywhere (beginning, end, middle)

#### 65. Can you switch case a String?
- Yes. (String, Enums are allowed.)

#### 66. What is an enhanced for loop?
- `for(int i : myList)`

#### 67. Explain breaks and break with label.
- `break` breaks the "inner loop"
- You can use labels to break the outerloop

  ```java
  outerLoop:
  for(;;){
    for(int i = 1; i < 10; i++)
      if(i == 5) break outerLoop;
  }
  ```

## Exceptions
#### 68. Why is exception handling important?
- Sometimes unexpected thing happen (e.g. Network Address unreachable, File is in use and can't be read, file is damaged etc.) --> Instead of breaking down, the program should "fail properly".

#### 69. What design pattern is used to implement Exception handling features in most languages?
- Chain of responsibility --> If I don't know how to handle, I throw it a "layer" upwards. There should be somewhere someone in the call-stack that will catch it. (If it is their responsibility.)

#### 70. What does a finally block do?
- It executes anyway (regardless if try/catch was successful). Optimal for closing files/streams/connections.
- Finally executes even if there is a return in try/catch (?!)

#### 71. Are there scenarios when finally is not executed?
- If exception is thrown in finally
- If JVM crashes. (Error - out of memory etc.)

#### 72. Is try without a catch allowed?
- Yes. `try{/*...*/} finally {/*...*/}`
- Useful if you want to close a connection but you don't want to handle the exception.

#### 73. Is a try without catch and finally allowed?
- No. At least one of them is required.


#### 74. Can you explain the hierarchy of Exception Handling classes?
- Throwable
  - Error
    - You can do nothing about it. (JVM errors etc.)
  - Exception
    - Checked (Any class that **does NOT** extend RuntimeException but extends exception)
      - The calling method knows that the given method might throw an exception. Hence it has to either catch it, or re-throw it.
    - Unchecked (RuntimeException)
      - I'm throwing an exception, but I'm not sure if the calling method can do anything about it / handle it.

#### 75. How do you thrown an exception?
- Throwing unchecked exception

  ```java
  if(condition){
    throw new RuntimeException("Message");
  }
  ```

- Throwing checked exception

  ```java
  if(condition){
    throw new Exception("Message");
  }
  ```

  - In such cases the method has to mark itself with `throws Exception`
  - The caller method MUST catch this OR mark itself with `throws Exception`

#### 76. How do you throw custom exception?
- Create a class in which you extend an exception.

  ```java
  class CurrenciesDoNotMatchException extends Exception {}
  class CurrenciesDoNotMatchException2 extends RuntimeException {}  
  ```

#### 77. What is the output of the program below?
```java
try{
  MyClass.funct(5);
} catch (Exception e) {e.printStackTrace();}
catch (CurrenciesDoNotMatchException e) {/*...*/}
```

- Compilation error. Specific exception MUST come first.

#### 78. How do you handle multiple exception types with the same block?
- `catch(IOException | SQLException ex)`

#### 79. Can you explain the try with resources?
- Helps to reduce boilerplate. The resource implements the `AutoClosable` interface, hence it will be automatically closed.
- It is available to any class that implements the `AutoClosable.`

## Various Topics
#### 80. What are the default values in an array?
- Depends on the underlying type.
- byte,short,int,long: 0
- float, double: 0.0
- boolean: false
- object: null

#### 81. How would you print the content of an array?
- ``System.out.println(myArray);`` prints [I@6db3f682
- Use **Arrays.toString()** / **Arrays.deepToString()**
  - ``System.out.println(Arrays.toString(myArray));``
  - ``System.out.println(Arrays.deepToString(my2DArray));``


#### 82. How do you compare two arrays?
- `Arrays.equals()` method.

#### 83. What are the advanced enums?

```java
// Enum with a variable,method and constructor
	enum SeasonCustomized {
		WINTER(1), SPRING(2), SUMMER(3), FALL(4);

		// variable
		private int code;

		// method
		public int getCode() {
			return code;
		}

		// Constructor-only private or (default)
		// modifiers are allowed
		SeasonCustomized(int code) {
			this.code = code;
		}

		// Getting value of enum from code
		public static SeasonCustomized valueOf(int code) {
			for (SeasonCustomized season : SeasonCustomized.values()) {
				if (season.getCode() == code)
					return season;
			}
			throw new RuntimeException("value not found");// Just for kicks
		}

		// Using switch statement on an enum
		public int getExpectedMaxTemperature() {
			switch (this) {
			case WINTER:
				return 5;
			case SPRING:
			case FALL:
				return 10;
			case SUMMER:
				return 20;
			}
			return -1;// Dummy since Java does not recognize this is possible :)
		}

	};
```

#### 84. What are variable arguments?
- Something that can accept multiple parameters
- The variable argument kind of acts like an array.
- Variable argument shuld be the last parameter of the array.

  ```java
  public int sum(int... numbers){
    int sum = 0;
    for(int number: numbers)
      sum+=number;
    return sum;
  }
  ```

#### 85. What are assertions used for?
- Assertions are used to validate assumptions in the code.
  - `assert(principal>0)`
- To use them you have to enable assertions with the -ea argument (short for -enableassertions).
- Don't use assertions to validate parameters. Use IllegalArgumentException.

#### 86. What is GC?
- Automatic memory management in Java.
  - When available memory is low.
  - When CPU is free.

#### 87. When does GC run?
  - Whenever the JVM thinks it is time.

#### 88. Can you request GC? How?
- `System.gc()`
- It is just a request, not an order. Not recommended.

#### 89. What are initialization blocs?
- **Static initializer**: runs when a class is loaded

  ```java
  class MyClass{
    static{
      Sout("Staticly initializing");
    }
    /*...*/
  }
  ```

- **Instance initializer**: when constructor runs

  ```java
  class MyClass{
    {
      Sout("Instance initializer runs every time an instance is created.");
    }
    /*...*/
  }
  ```
#### 90. What is serialization?
- Serialization: converting object state to some internal object representation
- De-serialization -> reverse
- `ObjectOutputStream.writeObject()`, `ObjectInputStream.readObject()`

#### 91. How do you serialize an object?
- The object must implement the `Serializable` interface. (The interface doesn't declare any methods to be implemented.)

  ```java
  FileOutputStream fileStream = new FileOutputStream("Rectangle.ser");
  ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
  objectStream.writeObject(new Rectangle(5, 6));
  objectStream.close();
  ```

#### 92. How would you de-serialize in Java?
```java
FileInputStream fileInputStream = new FileInputStream("Rectangle.ser");
ObjectInputStream objectInputStream = new ObjectInputStream(
				fileInputStream);
Rectangle rectangle = (Rectangle) objectInputStream.readObject();
```

#### 93. What do you do if just parts of the item must be serialized?
- Mark properties with the `transient` keyword.
  - `transient int area;`
  - Especially useful with calculated class properties.

#### 94. How would you serialize a hierarchy of objects? (Objects composed of other objects.)
- You mark the delegate with `transient`
- The delegate should implement the `Serializable` and then it will be serialized as well.

#### 95. Are the constructors in an object invoke, when it is de-serialized?
- No. The **state** of the object is restored, but it has nothing to do with the constructor.

#### 96. Are static variables serialized?
- No, since they are not part of the object.

## Collections
#### 97. Why do we need collections in Java?
- Array size is static. We need collections for situations where data is **dynamic**.

#### 98. What are the most important interfaces in the gathering progressive system?
- Iterable, Collection
- Set, List, Queue
- Map

#### 99. What are the most important methods in the Collection interface?
- add, remove, clear, isEmpty, size, contains, containsAll, addAll, removeAll, iterator

#### 100. What are the most important methods if the List interface?
- Keyword is "**position**"
- indexOf(), lastIndexOf() get(), set()

#### 101. What is the difference between ArrayList and Vector?
- Vector is synchronized. (`public synchronized void ...`)

#### 102. Describe the Set interface.
- No duplications allowed. (No additional methods defined.)

#### 103. What are: SortedSet, NavigableSet?
- They are **set INTERFACES**
- SortedSet: Maintains elements in a sorted order.
  - subSet(fromElement, toElement)
  - headSet(toElement)
  - tailSet(tailSet)
  - first(), last()
- NavigableSet (extends Sorted set)
  - A sorted set extended with navigation mehtods.
  - ``E lower(E e)`` --> find the nearest value close to this value.

#### 104. HashSet, LinkedHashSet, TreeSet?
- Set **implementations**
- HashSet: key value pairs
- LinekdHashSet: elements are ordered according to their insertion order
- TreeSet - sorted (natural order)
  - TreeSet provides guaranteed O(log(n)) time for common operations like add, remove and contains
  - Internally TreeSet implements NavigableSet.

#### 105. Describe the Map interface
- Key-value pairs.
- Methods: isEmpty, get, put, remove, putAll, clear, size, keySet(), valueSet(), entrySet()

#### 106. What is a navigable map?
- Similar to Set -> NaviagableSet
- A sorted map extended with navigation methods.

#### 107. What is the difference between HashTable and Hashmap
- HashTable is synchronized.
- HashTable does NOT allow a key with null value. HashMap does.

#### 108. What do you know about queues / dequeues / blocking queue?
- Extends collection.
- FIFO
- Dequeueu: you can add/remove at both sides
- Blocking queue: Used in multi threaded environment, where one thread blocks until there is something in the queue to grab / get.

## Advanced Collections
#### 109. What is the difference between synchronized and concurrent collections in Java?
- Pre Java 5 collections use the ``synchronized`` keyword for synchronizing method calls.
- Post Java 4 collections use new approaches.

#### 110. Explain the new approaches for synchronization.
- These are the "cuncurrnet collections"
- Copy on write
- Compare and Swap
- Locks

#### 111. Clarify about CopyOnWrite concurrent collections approach?
- e.g.: `CopyOnArrayList`
- All values in the collection are stored in an internal immutable array. When there is modification done to the array the original array is **copied.**
- Read operations are not synchronized. Only write operations.
  - Used when readers outnumber the writers.


#### 112. What is the CompareAndSwap approach?
- Java 5 approach to handle synchronization.
- The value of the member variable is cached before the calculation. After the calculation the cached value is compared to the member variable.
  - If another thread has modified the original value, then:
    - You do the calculation again with the latest value
    - Skip the calculation, assuming that another thread is also working on the variables.

#### 113. What is a lock?
- Problem with synchronize: If I have 100 synchronized methods, only **ONE** thread can execute **ANY** of the synchronized methods.
- Solution: `final ReentrantLock lock = new ReentrantLock();`
- You can create multiple locks, and now not the entre class' methods are "blocked".
  - These 3 methods use lockA, the other 4 use lockB etc.

#### 114. What is the initial capacity and the load factor of a HashMap?
- Initial capacity: number of buckets in the hash table
- Load factor: how full a hash table is allowed to get before extenind and rehashing it.

#### 115. What is the UnsupportedOperationException?
- All java Collections extend the Collection interface.

  ```java
  List<Integer> myList = Arrays.asList(new Integer[]{1,2,3});
  myList.remove(); // throws exception, as list is unmodifyable
  ```

#### 116. Fail-safe vs fail-fast iterator?
- Fail-fast: When using the iterator another thread modifies the underlying collection, and exception is thrown.
- Fail-safe: A copy is made of the collection, and you loop the original collection.

#### 117. What are atomic operations in Java?
- Operation effectively happens at once. (Preferred in multi-threaded environment, where you want to make sure, that the data you are working with is not changed.)
- ``AtomicInteger`` is a great example.

175 . What is distinction between safeguard and come up short quick iterators?

176 . What are nuclear operations in Java?

177 . What is BlockingQueue in Java?

Generics

178 . What are Generics?

179 . Why do we require Generics? Could you give a case of how Generics make a system more adaptable?

180 . How would you pronounce a nonexclusive class?

181 . What are the confinements in utilizing bland sort that is pronounced in a class assertion?

182 . In what capacity would we be able to confine Generics to a subclass of specific cl
