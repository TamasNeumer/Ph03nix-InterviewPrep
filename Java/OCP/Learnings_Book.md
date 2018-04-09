# Learnings from the Book's Tests

#### Advanced Class Design

- **Class Inheritance and Composition**
  - B implements A, C implements A. You still **can NOT** cast an B instance to C, as it will result in a runtime `ClassCastException`. (Even if they have a single method with the same signature etc etc.) Simply doesn't work.

      ```java
              A aInstance = new A();
              B bInstance = new B();
              A aRefBackedByB = new B();
              B bRefOfABackedByB = (B) aRefBackedByB;
              B bRefBackedByA = (B) new A(); // Fails!
      ```

  - In a chain of inheritance always check whether **all** abstract methods are implemented! Also check whether the class is `abstract`, as in this case it doesn't have to.
  - If a class has `final` instance variables **and** the getter methods are also `final` **and** the class only provides a consturctor that takes the arguments and initializes the instance variables then the class is "immutable". Even if you override the class, the child class' constructor **must** call `super(finalArg1, finalArg2)` and then you can't mutate the inherited members after that...
  - A singleton class doesn't require object composition, as it has a reference to *itself*.
  - Casting a parent to a child class compiles, but throws exception at runime!
- **Instanceof**
  - `bus instanceof ArrayList` - ArrayList being an actual class "enables" compile time checking. On the other hand `bus instanceof Collection` being an interface not!
  - Arrays can be also tested for `instanceof`. For example if `Van` implements `Vehicle`, and  `Van[] vans = new Van[0];` then `vans instanceof Vehicle[]` is true!
- **Enums**
  - If you pass an invalid `String` to the enum's `valueOf()` function an `IllegalArgumentException` is thrown.
  - Enums can have `static` member functions.
  - Enums can't be extended.
  - Enums are not allowed to have anything but `private` and default (package-private) constructor. This constructor is called for each declared constant!
  - Enum constructors are assumed to be private if no access modifier is specified, **unlike** regular classes where package-private is assumed if no access modifier is specified.
- **Anonymous Inner Classes**
  - Anonymous classes don't explicitely use the `implements` keyword. The righ hand side contains an interface reference (`Interface if = new Interface{...};`), and the compiler knows what the implemented methods should be. Note the **semicolon** at the end. The expression can last many lines, but make sure to check the last line! Also the implemented functions must be `public`!
  - Anonymous inner class may implement at most one interface, since it does not have a class definition to implement any others.
  - Java was updated to include `default` interface methods in order to support backward compatibility of interfaces. By adding a `default` method to an existing interface, we can create a new version of the interface, which can be used without breaking the functionality of existing classes that implement an older version of the interface.
  - Only anonymous classes can't be marked static or final, as they don't have a "full" class definition.
- **Member classes**
  - Member classes must be instantiated using an instance of the outer class. Even if the method trying to instantiate lies withing the Outer class!
- **Static inner class**
  - Inner classes can **NOT** have static member. (Except for `static` nested class.)
  - `static` inner class can **not** be instantiated via outer reference! **Only via class name!** (Since it is not a "member" of the class, you don't refer to it via an instance!)
  - Methods **outside** the Outer class may instantiate the `static` class using the "full class path": `Outer.StaticInnerClass sic = ...`, while methods **inside** the Outer class may simply refer to it by its name: `StaticInnerClass sic = ...`
- **Misc**
  - You can create a "deep" copy like this: `return new ArrayList<>(counts);`
  - A `static` initializer is not allowed inside of a method. (`static { ...}`)
  - `abstract public Leader getPartner(int count);` is correct (i mean the order of the words doesn't cause compile error)
  - Having multiple arguments in a lambda expression without type specification is not wrong, as the types are inferred from the left-side definition. `BiConsumer<Integer, Integer> p = (n, q) -> System.out.println("asd");`.

#### Generics

- **Generics**
  - Since every class is an `Object`, in a generic class you can reach out to all of `Object` methods. (Unless more specified - e.g. `T extends MyClass` - you can only work with the object methods on your generic class members.)
  - `for (String s: list)` this won't compile at all if list is an old-school non-generic list.
  - You can call a generic function with specifying the type (though it is only optional): `Helper.<NullPointerException>printException(new NullPointerException ("D"));` Toxic syntax...
  - You can use `class MyClass<MyClass>` as well as `class MyClass<Object>`. So you can use any "object-type" in the generic declaration you want.
  - `Wash<List> wash = new Wash<ArrayList>();` a generic type cannot be assigned to another direct type unless you are using upper or lower bounds in that statement.
- **Functional Interfaces**
  - Note that generic FIs compile **without** generics as well. (`public void wakeUp(Supplier supplier){...}` compiles.) In this case the the type is assumed to be Object.
  - `BiFunction<Integer,Double,Integer> takeABreak` is not compatible with `(int n, double e) -> (int)(n+e)` even if the primitive/wrappers match at the positions.
- **Primitive Functional Interfaces**
  - The main difference is that these FIs use *primitive* values (for arguments and return values) while the generic types (`Supplier<T>`)  work with the Objects!
  - `XXXSupplier` -> `XXX getAsXXX()` method.
  - `XXXPredicate` -> `boolean test(XXX value)`
  - `XXXConsumer` -> `void accept(XXX value)`
  - `XXXFunction<R>` -> `R apply(XXX value)`
  - `XXXBinaryOperator` -> `xxx(primitive) 	applyAsXXX(xxx left, xxx right)`
  - `XXXUnaryOperator` ->  `xxx(primitive) 	applyAsXXX(xxx oeprand)`
  - `XXXToxxxFunction` -> `xxx(Primitive) applyAsxxx(XXX value)` e.g.: `int applyAsInt(double value)`
    - Note that `XXX value` is a **primitive** type!
  - `ToXXXFunction / ToXXXBiFunction` - `double applyAsDouble(T value)`, `applyAsDouble(T t, U u)`
  - `Bi(Consumer/Function/Predicate)` no such thing as `BiSupplier` (since supplier doesn't take any arguments.)
  - `ObjDoubleConsumer<T>` - Represents an operation that accepts an object-valued and a double-valued argument, and returns no result. --> `accept(T t, double value)` is the method. (**Note:** it is **NOT** `ObjectDoubleConsumer` - it is **only** `Obj`!
  - Note that functions that return primitives can be assigned to generic FIs, as the primitives get autoboxed. This is valid vice versa. You can assign functions to primitive FIs that return generic Objects. (implicit unboxing)
  - Note that the primitive FIs are non-generic! Trying to add any generics to the signature results in compile error.
  - The `forEach` defined in the `Collection` interface requires a `Consumer<T>`. Since the primitive FIs are not related (in terms of inheritance) to the generic FIs, passing a primitive consumer (`DoubleConsumer`) to the `forEach` results in compilation error.
  - **Long can be implicitly cast to double!**
- **Comparator, Comparable**
  - Unlike the `equals()` method, the method in `Comparator` takes the type being compared as the parameters when using generics. (... `implements Comparator<MyClass>` ... `public int compare(MyClass o1, MyClass o2`)
- **List**
  - `list.add(String::new);` - this doesn't work - compile time error. This is a method reference (or lambda) that is used to do deferred execution. At **compile time** it is nothing but a `Supplier<String>` and sicne the `add` function expects a `String` it doesn't compile!
- **Set**
  - Watch out for `Set`s. When printing the content with an iterator the output cannot be determined.
  - `TreeSet` accepting an Object implementing `Comparator`, and using this `Comparator` to compare objects later!

    ```java
    public class Sorted implements Comparable<Sorted>, Comparator<Sorted> {
      /*compare and compareTo implementations*/
    }
    public static void main(String[] args) {
            Sorted s1 = new Sorted(88, "a");
            Sorted s2 = new Sorted(55, "b");
            TreeSet<Sorted> t1 = new TreeSet<>();
            t1.add(s1);
            t1.add(s2); // Sorts according to "compare"!
            // Constructor accepting Comparator!
            TreeSet<Sorted> t2 = new TreeSet<>(s1); 
            t2.add(s1);
            t2.add(s2); // adduing using "compare" method!
            System.out.println(t1 + " " + t2);
        }
    ```

- **Map**
  - `Map` does **NOT** have `add()` method! They have `put`!!! Watch out!!!
  - `Map` does **NOT** have a `contains` function! (`containsKey`, `containsValue`)
  - `TreeMap` (and `TreeSet`) only check it at **runtime**, whether the `Comparable<T>` interface has been implemented. The implementation has to stay in the class declaration signature!
- **Queue**
  - `Queue` has **only** the **remove by object** method, so in the method `remove(1)` Java does autobox there. Since the number 1 is not in the list, Java does not remove anything for the `Queue`. --> The `Collection` interface only declares a `remove(Object o)` method!
  - `ArrayDeque` uses null for a special meaning, so it doesn’t allow it in the data structure.
- **Java 8 in Collections, lambdas**
  - `forEach` takes a `Consumer` parameter, which requires one parameter, hence `() -> "asd"` is incorrect!
  - `System.out::println` is the correct method reference!
  - Watch out! `s.foreach( s -> sout(s))` is not good, as `s` is already declared in the scope (the stream!)
  - `ArrayList::new(n);` --> This is bullshit. Such thing doesn't exist!
- **Misc**
  - Watch out for `char` vs `String` tricks. (`'` instead of `"`)
  - **Uppercase** letters are sorted before **any** lowercase letters. Hence in a `TreeSet` the word "winchester" comes before the word "apple".

#### Functional Programming

- **Streams**
  - `System.out.println(stream.limit(2).map(x -> x + "2"));` --> compiles. Since the stream is not terminated it simply prints the `Object` itself i.e. `java.util.stream.ReferencePipeline$3@4517d9a3`
  - Most of the `Stream` methods have some kind of generic functional interface in them.
    - `anyMatch(Predicate<? super T> predicate)` -> Don't get scared from the lower-bound stuff! Now you can declare the `Predicate` in a separate line:
      - `Predicate<? super String> predicate = s -> s.startsWith("g");`
    - However the actual caller (`Stream`) determines the generic type. If you have a stream of objects and you won't be able to use this predicate!
      - `boolean b1 = objectStream.anyMatch(predicate);` --> `anyMatch(Predicate<? super Object>` cannot be applied to `Predicate<capture<? super String>>`
  - Streams **CANNOT be reused!!!** - watch out!!!
  - Watch out for the **context** - `Stream` does **NOT** define `sum()`, it is only defined in the primitive streams!
  - `findFirst()` is not a reduction because it doesn't look at all the elements in the stream. *Reduction* = look through **all** elements and return an object/primitive.
  - `Stream.iterate(1, x -> x++)` --> This prints 1 all the way! pre-increment has to be used in order to increment the number!!
  - The `count()` method returns `long`.
  - `sort()` (`Collection` interface) vs `sorted()` (`Stream` interface.)
  - The `forEach(Consumer<? super T> action)` is implemented in both `Collection` and `Stream` interfaces.
  - The pipeline still runs if the source doesn’t generate any items and the rest of the pipeline is correct.
  - You can negate a `Predicate` by chaining the `.negate()` function onto it. Not by `! MyClass::PredicateFunct`
  - Regardless of whether the type is a `class` or `interface`, Java uses the `extends` keyword for generics! Hence you can only write `<T extends Collection>` and not `implements Collection`!
  - `tream.of(set, list, queue)` - creates a Stream of Streams! You need to `flatMap` such stuff!
  - `flatMapToInt`, `flatMapToDouble`, `flatMapToLong` work on the **primitives** (while `flatMap` works with Objects). These functions return `IntStream`, `DoubleStream` and `LongStream`
  - The XXXSummaryStatistics class provides getters! (`getAverage()`, `getMax()`, `getMin()`, `getSum()`) and all of these return **primitives!!!**
  - `MapToLong` is not available in the `LongStream` class. (Wouldn't really make sense, heh?)
- **Advanced Streams - DobuleStream, IntStream, LongStream**
  - `average()` method returns `OptionalDouble`. (Doesn't throw even if the stream is empty!)
  - `summaryStatistics()` returns a class of Statistics corresponding to the given class
    - `IntSummaryStatistics` and **NOT** "Integer"!
    - `LongSummaryStatistics`
    - `DoubleSummaryStatistics`
- **Optional**
  - The `boolean isPresnet()` returns `true/false`. The `void	ifPresent(Consumer<? super T> consumer)` does something if the value was present. **is** vs **if** - know the difference!
  - The "primitive optionals" don't have `get` method. They have `getAsLong`, `getAsInt` etc. Also these classes **don't** `extend` `Optipnal<T>` as it is a final class.
  - `Oprional` also has `map`, `filter`, `flatMap` methods!

#### Exceptions

- **Exception class**
  - `Exception()`, `Exception(String message)`, `Exception(String message, Throwable cause)`, `Exception(Throwable cause)` are all valid constructors of the class!
  - a class must inherit from `RuntimeException` or `Error` to be considered an unchecked exception --> inheriting from `Throwable` results in a checked exception!
- **General tips**
  - throw**s** vs throw. Typical case for a compile error! Watch out!
  - Make sure that the `catch(Exception XYZ)` doesn't redefine a local variable, as it leads to compile error.
  - If you have a method that throws `MissingMoneyException` and `MissingFoodException` the caller must (either catch) or declare one of the followings in its signature:
    - `throws Exception`
    - `throws MissingMoneyException`, `MissingFoodException`

      ```java
      public void doIHaveAProblem() throws MissingMoneyException, MissingFoodException {
        System.out.println("No problems"); }
      public static void main(String[] lots) throws XYZ {
        try {
          final Problems p = new Problems();
          p.doIHaveAProblem();
          }
          catch (Exception e) {
            throw e;
          }}
      ```
  - The variables defined in the try-(with-resources) can't be used in the finally block! -> Try block has its own scope!
  - Always check whether the exception we are trying to catch can be thrown from the method! If not compilation error!
- **AutoClosable, Closable**
  - `Closable` may only throw `IOException` while `AutoClosable` can throw `Exception`
  - When working with try-with-resources the `;` after the last resource is *optional* --> watch out which one is the current exception implementing and see if the method signature is correct!
  - `close()` method should be idempotent, which means it is able to be run multiple times without triggering any side-effects. (i.e. no variable changes etc etc.)
  - `return` statement is not allowed in the second expression of an assert statement

- **Assertation**
  - Which **guarantees** an Assertion exception? --> None, unless assertions are enabled!
  - The optional second parameter of an `assert` statement, when used, **must** return a value. (It can be int, string whatever...)

#### LocalDateTime, Strings, Localization

- `LocalDate`, `LocalTime`, `LocalDateTime`, `ZonedDateTime`
  - `LocalDateTime` has a lot of `getXXX` functions such as `getDayOfWeek`, `getDayOfMonth`, `getDayOfYear`, `getMonth()` etc.
  - `LocalDateTime` checks the arguments. Months must be 1-12, days 1-28/31! --> otherwise exception! Negative years are allowed though.
  - `ZonedDateTime a = ZonedDateTime.of(date4, time1, zone);` where `LocalDate.of(2016, 3, 13)` and `LocalTime.of(2, 15);` is valid, even if the date/time won't exist. (We jump forward from 2->3, but it will compile... AND RUN!)
  - To convert these classes into an `Instant` use the `Instant`'s `from()` method. These classes don't have a method like `toInstant()`!!!
  - `int month = Month.MARCH;` is erroneous. It is an ENUM!
  - `Duration duration = Duration.ofDays(1);` `LocalDate result = montyPythonDay.minus(duration);` Since `Duration` implements temporal amount the code compiles, however throws an exception at runtime, because duration represents a "seconds", while you can't subtract seconds from a `LocalDate`
  - `LocalTime`s with nanoseconds are printed like this: 01:02:03.000000004
- **Perios, Duration**
  - `Period`
    - has `getYear`**s** while `LocalDate`/`LocalDateTime` has `getYear` without an "s".
    - With the `Period` you can chain like `Period period = Period.ofYears(1).ofMonths(6).ofDays(3);` With the `Duration` you cannot!
  - `Duration`
    - class has only `getNano, getSeconds, getUnits, get(TemporalUnit unit)` methods. -> Hence no "getMinutes"/"getHours"
    - class has a `ofDays()` method even if days are not stored?!
- **Time spans**
  - Using `ChoronoUnit`'s function: `ChronoUnit.DAYS.between(blackFriday, xmas);`
  - Using `LocalDate`'s function: `blackFriday.until(xmas, ChronoUnit.DAYS)`
- **Formatting**
  - `DateTimeFormatter` has a method `format(TemporalAccessor temporal)` and that's it! No "fromatDate" or "formatTime" methods!
  - `LocalDate`, `LocalDateTime` etc. also have a `format(DateTimeFormatter formatter)` method that can be used to output a `String`
- The class `ChronoUnit` contains the constants like `HOURS`, `MINUTES` etc.
- There is a `DateTimeFormatter` class, but not a `DateFormatter` class.
- Incorrect format pattern - you can't use literal! `.ofPattern("Holiday: yyyy dd MMM");` --> `IllegalArgumentException`
- **Time Shifting**
  - Adding 3 hours to 1.am results in 5a.m. if we are shifting the clock forward.
- **Localization**
  - Locale
    - Oracle defines a *locale* as a geographical, political, or cultural region. Time zones often span multiple locales, hence it is not a locale.
    - A locale can consist of a:
      - language alone (`en`)
      - language and region --> in this case the language **must** come first --> `en_US`
    - `Locale.setDefault(new Locale("EN"));` is correct. Java automatically converts the language to lowercase.
  - `Properties`
    - Implements `Map` and extends `Hashtable`
    - `Properties.getProperty("key")` returns `null` is the given key was not in the dictionary.
    - The class inherits `Map`'s `get` method. Calling this with a key will return an `Object` and not a `String`!
    - `Properties` is the name and not Property!
    - Java requires lowercase letters for the locale in the classname: `Colors_ZH.properties` won't be picked up! ZH should be zh!
  - `ListResourceBundle`
    - Creating a java resource bundle:
      - `public class Flights_en extends ListResourceBundle {` -> Note the extended class!
      - `protected Object[][] getContents` --> Note the whole signature!
    - `protected Object[][] getContents() { return new Object[][] { { "count", count++ } }; }`
      - It is incremented only once, as the resource is only loaded once via `ResourceBundle.getBundle("counter.CountResource");`
    - At least one matching resource bundle must be available at the time of the call to `getBundle()`. If not --> `MissingResourceException`
    - First the exact match (lan,reg) is looked up. If not found, the region is dropped. `.java` have priority over property files.
    - `getString / getObject` throw `RuntimeException` if the key is not found in the given bundle or in its parents!
    - When getting an `Object` a cast is required to assign it to a given variable! (`PropertyCounter obj = (PropertyCounter) rb.getObject("count");`)
    - You must get a Bundle via `Package.Classname`! --> `ResourceBundle.getBundle("Type");` is incorrect!
    -Looking for Bundles:
      - `zn_CH` --> `zn` --> `en_US` (default) --> `en` --> Default bundle (Classname without locale specified e.g. `Car.java`)

#### JDBC
- **Driver, DriverManager**
  - `DriverManager.getConnection(url)` is used to get a connection. DriverManager is already an implementation!
  - The URL **must** contain the database name in the third part. `jdbc:magic:@127.0.0.1:1234` does NOT! If the DB name is "box" it must contain this word!
  - `Class.forName(url)` -  This method is expecting a fully qualified class name of a database driver, not the JDBC URL.
  - `DriverManager` doesn’t implement `Driver`
- **Statement**
  - The first parameter is the ResultSet type. The second parameter is the ResultSet concurrency mode.
  - JDBC code throws a SQLException, which is a checked exception. The code does not handle or declare this exception, and therefore it doesn’t compile.
    - If the exam has a full method descroption (class, main method etc.) then the signatures **must** handle these JDBC exceptions. If only snippets are shown, we can assume that these are handled outside.
  - A `Statement` automatically closes the open `ResultSet` when another SQL statement is run. This means that firs is no longer open by the `println`, and a `SQLException` is thrown because the `ResultSet` is closed.
    - `ResultSet rs = stmt.executeQuery("select count(*) from species");`
    - `int num = stmt.executeUpdate("INSERT INTO species VALUES (3, 'Ant', .05)");`
  - A `Statement` automatically starts in auto-commit mode.
  - When creating the Statement, the code doesn’t specify a result set type. This means it defaults to `TYPE_FORWARD_ONLY`. The `absolute()` method can only be called on scrollable result sets. The code throws a `SQLException`,
- **ResultSet**
  - Executing a `COUNT *` will return an integer that can be accessed via `getInt(1)`
  - You can’t move back before row zero, the cursor is at row zero instead. (`relative(-10)`) Also you can't move beyond #rows+1. It's not like an infinite counter.
- **Misc**
  - `sout(voidFunction())` doesn't compile. --> `beforeFirst()` is void, watch out!
  - `Class.forName(url);` throws a checked exception, hence handling it is necessary!
  - `createStatement(int resultSetType, int resultSetConcurrency)` is the signature, hence it can't check if using enums you are assigning the correct enum to the correct argument position.


#### Concurrency

- **Executor, Executors, ExecutorService, ScheduledExecutorService**
  - `Executor` interface defines a single function `void execute(Runnable command)`.
  - `ExecutorService` implements the interface and adds two `submit` functions. (One for `Runnable` and another for `Callable`). Both return a `Future`! Also `invokeAny` and `invokeAll` are introduced. Both invoke functions operate only on `Callable`s!
  - Note that `Executors` (with an **s** at the end) is the factory class that creates.
  - Executor must be shut down in order not to hang!
  - The correct mehtod names are `scheduleAtFixedRate` and `scheduleWithFixedDelay`. Remember **-Rate** and **WITH-Delay**. Fix-delay always waits until the previous task is finished.
  - `s.shutdown(); System.out.print(classVar.stroke);` --> you don't know if all threads have finished their work, and you might read the variable before!
- **Future**
  - `T get()` throws 2 checked exceptions!!!
- **CyclicBarrier**
  - `cyclicBarrier.await()` throws!
  - A `SingleThreadExecutor` won't be able to trigger a barrier more than once, hence the program might hang!
- **Concurrent Data Structures**
  - `BlockingDeque` is an interface that is implemented by multiple classes: `LinkedBlockingQueue`, `LinkedBlockingDequeue` being one of these.
  - `ConcurrentLinkedDeque` does not support waiting.
  - The collection interface defines a `parallelStream` method but not a "parallel" method!
  - . `ConcurrentSkipListMap` implements the SortedMap interface
- **Fork/Join**
  - `ForkJoinTask` is the abstract parent class of `RecursiveAction` and `RecursiveTask`.
- **Misc**
  - Note that when creating a reduction like `.reduce(0, (c1, c2) -> c1.length() + c2.length(),(s1, s2) -> s1 + s2))` the variable `c1` will be an `int` hence calling the `length()` method on it is illegal.
  - Race-condition: Racing for the given "asset", (e.g.: incrementing it). The outcome of the program depends on who gets it first.
  - Resource-starvation: single active thread is unable to access a shared resource. Live-lock is a special case of this, where two or more threads "starve" by actively trying to acquire the resource.
  - `thread.sleep(1)` throws checked exception!
  - `synchronized {}` is not good. you must lock in an object!

#### IO

- 