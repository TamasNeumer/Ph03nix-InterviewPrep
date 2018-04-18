# Learnings 2

#### Class Design

- If the `equals()` method returns true, the `hashCode()` of the two objects **MUST** be the same, in order to be consistent.
- Java 5 onwards supports **co-variant return types**, which means that an overriding method can declare any sub-class of the return type declared in the overridden method as its return type.
- `static` and non-`static` blocs in code: The `static` block will be executed only once when the class is loaded. **A class is loaded when it is first referenced.** `A1 a1 = null` is not a valid reference, hence at this point the class is not yet loaded!
- Normally a class is loaded in this order: `static` initializer, non-`static` initializer, constructor.
- Method `m1` throws `IOExceotion` in interface I1, while method `m1` throws `SQLException` in interface I2. A class can implement BOTH interfaces if the overriding method doesn't throw anything!
- **Static, non-static, abstract, final**
  - Watch out for `static` interface methods, as these don't "get inherited" and static method of an interface can only be accessed by using the name of that interface.
  - `static` method cannot be overriden with `non-static` and vice versa! You can, however, redeclare a `static` method of a super interface as a default method in the sub interface.
  - An interface can redeclare a `default` method and also make it `abstract`.
  - A `static` method can NEVER be `abstract`!
  - Something `final` can not be `abstract`. (Usually)
- **Enum**
  - In the `enum` the list of objects has to be the very first thing!!!
- **Abstract class**
  - You want to define common method signatures in the class but force subclasses to provide implementations for such methods.
  - You need to have a root class for a hierarchy of related classes.
  - You want to pass different implementations for the same abstraction in method calls.

#### Collections and Generics

- **List and queue**
  - `LinkedList` implements `Queue`
- **Map**
  - A Map is nothing but a set of buckets holding key-value pairs. Each bucket corresponds to a unique hashcode.
  - When you store a key-value pair in a `Map`, the following things happen:
    1. Hashcode of the key is computed. This key is used to identify the bucket where the key-value must be stored.
    2. The key - value pair is stored in that bucket wrapped in a `Map.Entry` object.
    3. If multiple keys have same hashcode value, all those key-value pairs are stored in the same bucket.
  - Now, a look up in a Map is a three step process:
    1. Hashcode of the key is computed. This code is used to identify the bucket where the key should be looked for.
    2. For all the key-value pairs in that bucket, check whether the supplied key is equal to the key in the bucket using `equals()` method.
    3. If a match exists, return the value, otherwise, return `null`. (!)
  - As you can see, it is critically important to make sure that `hashCode()` method return the same value for two objects that are equal as per `equals()` method.
  - `LinkedHashMap` insertion order is not affected if a key is re-inserted into the map.
  - `ConcurrentHashMap` doesn't support adding `null` keys/values. (`HashMap` does support these.)
  - `Hashtable` doesn't allow null values at all, `TreeHashMap` allows only `null` values but not keys (as sorting is based on keys), while `HashMap` and `LinkedHashMap` both allow `null` values and keys.
  - `keySet, values, entrySet` are the methods to get the correct collections of the map. To check values there is NO `contains` method but `containsKey` and `containsValues`
  - **Compute**
    - `public V compute(K key, BiFunction<? super K,? super V,? extends V> remappingFunction)`
      - `map.compute(key, (k, v) -> (v == null) ? msg : v.concat(msg))`
      - Attempts to compute a mapping for the specified key and its current mapped value (or null if there is no current mapping). For example, to either create or append a String msg to a value mapping:x
      - If the function returns null, the mapping is removed (or remains absent if initially absent). If the function itself throws an (unchecked) exception, the exception is rethrown, and the current mapping is left unchanged.
    - ` public V computeIfAbsent(K key, Function<? super K,? extends V> mappingFunction)`
      - `map.computeIfAbsent(key, k -> new Value(f(k)));`
      - If the specified key is not already associated with a value (or is mapped to null), attempts to compute its value using the given mapping function and enters it into this map unless null. If the function returns null no mapping is recorded.
    - `public V computeIfPresent(K key, BiFunction<? super K,? super V,? extends V> remappingFunction)`s
      - If the value for the specified key is present and non-null, attempts to compute a new mapping given the key and its current mapped value.
- **Misc**
  - `List<?> list2 = new ArrayList<>(Arrays.asList(p));` this is a valid statement.
  - Given an array of length 4, `Arrays.binarySearch` may return values in the range of -5 and 3.
    - The "counting" starts from 0 up to 3. Hence the last element is searched and found 3 is returned. If you are searching for an element that is greater than the last value, but is not in the array, it will return -5, as the element should have been at the 4th position and -4-1 = -5.

#### Functional Interfaces

- **Primitive Functional Interfaces**
  - `R IntFunction<R>	apply(int value)` takes an int primitive as argument. Usually used in combination with `IntStream`, which works on (`int`) primitives. The benefit of using primitive FIs is to avoid autoboxing/unboxing.
  - The primitive suppliers (`DoubleSupplier`) with the method `getAsDouble` and returns a **primitive** double.
- `process(List<String> names, Carnivore c)` expects a `List<String>` and a `Carnivore` instance as arguments. `Carnivore` has exactly one abstract method and therefore it is a functional interface. You can either pass a `Carnivore` instance explicitly **or pass a reference to a method that matches the parameter list of Carnivore's abstract method eat(List<String> foods);**. --> not full method name, only parameter list is enough!!!
  - `Tiger t = new Tiger();` -> `t::eat` is okay! (Method is not `static`!)
  - `TestClass::size` is okay. (Method is `static`)
  - However you can't playy method references to interface default methods (`Carnivore::calories`) as it does not refer to any object upon which calories method can be invoked.
- `Supplier` has a method `get`, while `DoubleSupploer` has `getAsDouble`!
- Comparator.comparing method requires a Function that takes an input and returns a Comparable. This Comparable, in turn, is used by the comparing method to create a Comparator. The max method uses the Comparator to compare the elements int he stream. The call to get() is required because max(Comparator ) return an Optional object.
- `ls.stream.max(Integer::compare).get()` is okay, since the method returns the value 0 if x == y; a value less than 0 if x < y; and a value greater than 0 if x > y --> in accordance with our "Function" requirements.
- `Collectors.partitioningBy(i->{ return i>5 && i<15; })).get("true")...` -> should have been `get(true)` as the keys produced by `pratitioningBy` are raw booleans.
- `IntStream.of(XYZ)`
  - XYZ might be `int` or `int...` but not `List<Integer>`!
- `Comparator` has three flavors of thenComparing method. One of them takes a `Function` (instead of a `Comparator`). It is this method that is being used here: `comparator1.thenComparing(Book::GetStringTItile)` --> since the returned object (`String`) implements the `Comparable` interface it is used to compare the objects.

#### Concurrency
- **Atomics**
  - `AtomicInteger` is not a wrapper class and so auto unboxing will not happen here and so it will not compile. --> `int x = atomicInteger + 1;` won't compile!

- **Assertations**
  - Assertions can be enabled or disabled for specific packages or classes. To specify a class, use the class name. To specify a package, use the package name followed by `...` (three dots):
    - `java -ea:<class> myPackage.MyProgram`
    - `java -da:<package>... myPackage.MyProgram` --> You can enable or disable assertions in the unnamed root (default)package (the one in the current directory) using the following commands: `java -ea:bad... Main`
- **Barriers and blocking**
  - Object's `wait()` causes the current thread to wait until another thread invokes the `notify()` method or the `notifyAll()` method for this object. `InterruptedException` is thrown if it is interrupted by another thread, `IllegalMonitorStateException` if not used in a synchronized block.
- **Concurrent Collections**
  - Any operations done on the `ConcurrentHashMap` instance may be reflected in the `Iterator` that is iterating at the same time on the collection.

#### Exceptions and Assertion

- **Try with resources, closable**
  - Check whether the class `implements AutoClosable` and overrides `public void close()`!
  - Remember that most of the I/O operations (such as opening a stream on a file, reading or writing from/to a file) throw `IOException`, hence check whether the try-with-resource's `catch` clause catches this.
  - The auto-closeable variables defined in the try-with-resources statement are **implicitly final**. Thus, they cannot be reassigned.
- **Assertions**
  - `assert <boolean_expression> : <Expression_evaluating_to_Object_Primitive_Null>;` --> this is the way to write assertions! The second phrase must evaluate to an Object, Primitive or `null`. Void is not allowed, throwing is not allowed! The expression will be passed to the exception's constructor.
  - Enabling and disabling assertions:
    - `java -ea:<class> myPackage.myProgram`
    - `java -da:<package>... myPackage.myProgram`
  - Due to the `assert` keyword code written for JDK version 1.3 cannot be compiled under JDK version 1.4  without using -source flag: `javac -source 1.3 classname.java`
  - "This code will not work in all situations." -> True. If assertions are disabled it wont work...
  - As a rule, assertions should not be used to assert the validity of the input parameters of a public method. However, assertions may be used to validate the input parameters of a private method.
  - Assertions require changes at the API level. Besides the 'assert' keyword, new methods are added in java.lang.Class and java.lang.ClassLoader classes.
  - Enable assertions for system level classes: `-enablesystemassertions`, `-esa`
- **Exception usage**
  - `Error` is used for a serious system errors from which there can be no recovery. Exceptions for application programs almost never extend from this class. --> Only write Error if something super fatal happens and your program should not recover! `Exception` can be used to create new exception classes for reporting business logic failure. For example, a LowBalanceException that extends exception would be appropriate if the code finds that the funds in the account are not enough for the withdrawal. `RuntimeException`, which extends `Exception`, should be used for situations that are unexpected for a piece of code. For example, if you get an array as a parameter and your application logic may expect that all elements are non-null and if you do get a null, you may throw a `NullPointerException`. There is usually no need for creating your own `RuntimeException` because Java API provides several `RuntimeExceptions` suitable for various situations.
 
#### DateTime

- **Instant**
  - Instant also supports `plus/minus` methods.
  - Note that `LocalDateTime ldt = LocalDateTime.ofInstant(Instant, Zone.of("GMT+2"))` pushes the time to the given timezone (from GMT) and hence in this case it adds two hours.
- **Time shift**
  - `ChronoUnit.HOURS.between(...2AM, ...3AM)` on daylight shifting gives 0 hour.
  - Therefore, Period doesn't mess with the time component of the date while Duration may change the time component if the date is close to the DST boundary.
    - Adding a **duration** to a ZonedDateTime triggers the time shifting effect.
    - Adding a  **period** does NOT trigger the effect.
- **Duration/Period**
  - Duration:
    - `Duration.toString -> PT8H6M12.345S` If a section has a zero value, it is omitted. If you construct 1D -> it becomes 24H on the output.
    - If the duration is negative a minus is added: `P-1D`
  - Period:
    - A zero period will be represented as zero days, '`P0D`'.
    - Output format: `P6Y3M1D`
