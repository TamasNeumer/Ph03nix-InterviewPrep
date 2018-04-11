# Learnings 2

#### Class Design

- If the `equals()` method returns true, the `hashCode()` of the two objects **MUST** be the same.
- Java 5 onwards supports **co-variant return types**, which means that an overriding method can declare any sub-class of the return type declared in the overridden method as its return type.
- `static` and non-`static` blocs in code: The `static` block will be executed only once when the class is loaded. **A class is loaded when it is first referenced.** `A1 a1 = null` is not a valid reference, hence at this point the class is not yet loaded!
- Normally a class is loaded in this order: `static` initializer, non-`static` initializer, constructor.
- Method `m1` throws `IOExceotion` in interface I1, while method `m1` throws `SQLException` in interface I2. A class can implement BOTH interfaces if the overriding method doesn't throw anything!

- **Static, non-static, abstract, final**
  - Watch out for `static` interface methods, as these don't "get inherited" and static method of an interface can only be accessed by using the name of that interface.
  - `static` method cannot be overriden with `non-static` and vice versa! You can, however, redeclare a `static` method of a super interface as a default method in the sub interface.
  - An interface can redeclare a default method and also make it abstract.
  - A `static` method can NEVER be `abstract`!
  - Something `final` can not be `abstract`. (Usually)
- **Enum**
  - Unlike a regular java class, you cannot access a non-`final` `static` field from an enum's constructor. (= can't access "simply" `static` fields)
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
- **Misc**
  - `List<?> list2 = new ArrayList<>(Arrays.asList(p));` this is a valid statement.
  - Given an array of length 4, `Arrays.binarySearch` may return values in the range of -5 and 3.
    - The "counting" starts from 0 up to 3. Hence the last element is searched and found 3 is returned. If you are searching for an element that is greater than the last value, but is not in the array, it will return -5, as the element should have been at the 4th position and -4-1 = -5.

#### Functional Interfaces

- **Primitive Functional Interfaces**
  - `R IntFunction<R>	apply(int value)` takes an int primitive as argument. Usually used in combination with `IntStream`, which works on (`int`) primitives. The benefit of using primitive FIs is to avoid autoboxing/unboxing.
  - The primitive suppliers (`DoubleSupplier`) with the method `getAsDouble` and returns a **primitive** double.



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

#### Localization
