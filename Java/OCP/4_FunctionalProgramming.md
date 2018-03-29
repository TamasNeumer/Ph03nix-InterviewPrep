# Functional Programming

#### Variables in Lambdas

- Lambda expressions can access static variables, instance variables, effectively final method parameters, and effectively final local variables
- **Supplier**
  - It supplies aka. it returns a value of type `T`.
  - `@FunctionalInterface public class Supplier<T> { public T get();}`
  - Usage:
    - `Supplier<LocalDate> s1 = LocalDate::now;` or `Supplier<LocalDate> s2 = () -> LocalDate.now();` Then `LocalDate d1 = s1.get();` returns the value.
- **Consumer and BiConsumer**
  - Represents an operation that accepts a single input argument and returns no result. Unlike most other functional interfaces, Consumer is expected to operate via side-effects.
  - `@FunctionalInterface public class Consumer<T> { void accept(T t);}`
  - `@FunctionalInterface public class BiConsumer<T, U> {void accept(T t, U u); }`
  - Usage:
    - `Consumer<String> c1 = System.out::println;` then `c1.accept("Annie");`
    - `Map<String, Integer> map = new HashMap<>(); BiConsumer<String, Integer> b1 = Map::put;` then `b1.accept("chicken", 7);`
- **Predicate and BiPredicate**
  - Takes One (or two) arguments and returns a `boolean` value.
  - `@FunctionalInterface public class Predicate<T> { boolean test(T t);}`
  - `@FunctionalInterface public class BiPredicate<T, U> {boolean test(T t, U u); }`
  - Usage:
    - `Predicate<String> p1 = String::isEmpty;` then `System.out.println(p1.test(""));`
    - `BiPredicate<String, String> b1 = String::startsWith;` then `System.out.println(b1.test("chicken", "chick"));`
  - Functional interfaces often have other **default** methods. The `Predicate`for example has the logical `and(Predicate<? super T> other)`, `or(Predicate<? super T> other)` and `negate()` functions. These all return predicates that allow chaining!
    - `Predicate<String> brownEggs = egg.and(brown);` - where `egg` and `brown` are two other predicates.
- **Function and BiFunction**
  - Turning one (or two) parameters into a new one.
  - `@FunctionalInterface public class Function<T, R> { R apply(T t);}`
  - `@FunctionalInterface public class BiFunction<T, U, R> { R apply(T t, U u);`
  - Usage:
    - `Function<String, Integer> f1 = String::length;` then `System.out.println(f1.apply("cluck"));`
    - `BiFunction<String, String, String> b1 = String::concat;` then `System.out.println(b1.apply("baby ", "chick"));`
  - If you need more parameters you can create your own functional interfaces. `interface TriFunction<T,U,V,R> { R apply(T t, U u, V v);}`
  - Default methods include:
    - `andThen(Function<? super R,? extends V> after)` - Returns a composed function that *first applies this function* to its input, and then applies the after function to the result.
    - `compose(Function<? super V,? extends T> before)` - Returns a composed function that *first applies the before function* to its input, and then applies this function to the result.
    - `identity()`
      - Returns a function that always returns its input argument.
- **UnaryOperator and BinaryOperator**
  - A `UnaryOperator` transforms its value into one of the **same type**. (i.e. incrementing)
  - A `BinaryOperator` merges two values into one of the **same type**.
  - They both require the parameters to be the same!
  - This is a functional interface whose functional method is `Function.apply(Object)`.
  - `@FunctionalInterface public class UnaryOperator<T> extends Function<T, T> { }`
  - `@FunctionalInterface public class BinaryOperator<T> extends BiFunction<T, T, T> { }`
  - Usage:
    - `UnaryOperator<String> u1 = String::toUpperCase;` then `UnaryOperator<String> u1 = String::toUpperCase;`
    - `BinaryOperator<String> b1 = String::concat;` then `System.out.println(b1.apply("baby ", "chick"));`

#### Optional

- A container object which may or may not contain a non-null value. If a value is present, `isPresent()` will return `true` and `get()` will return the value.
- An `Optional` is created using a factory. Note that the `of(T t)` throws an exception if `t` is `null`. The `ofNullable()` doesn't. 

    ```java
    public static Optional<Double> average(int... scoes){
      if(scores.length = 0) return Optional.empty();
      int sum = 0;
      for(int score: scores) sum += score;
      return Optional.of((double) sum / scores.length);
    }

    System.out.println(average(90, 100)); // Optional[95.0]
    System.out.println(average()); // Optional.empty
    ```
- You **should** check if the `Optional` has a value with `isPresent()` and if so then get its value via `get()`. Otherwise you might get `java.util.NoSuchElementException`! However this is not considered a nice coding practice. Instead use the `ifPresent()`, `orElse()` etc. methods.
- A common factory pattern is: `Optional o = Optional.ofNullable(value);`. Returns an `Optional` describing the specified value, if non-null, otherwise returns an empty `Optional`.
- Other methods are: `orElse(T other)`, `	orElseGet(Supplier<? extends T> other)`, `orElseThrow(Supplier<? extends X> exceptionSupplier)`. These return the contained value, and if it is `null` execute the "else" function part.
- Usage:

  ```java
  Optional<Double> opt = average(90, 100);
  opt.ifPresent(System.out::println);

  Optional<Double> opt = average();
  System.out.println(opt.orElse(Double.NaN));
  System.out.println(opt.orElseGet(() -> Math.random()));
  System.out.println(opt.orElseThrow(() -> new IllegalStateException()));

  // DOES NOT COMPILE. Expects Supplier type Double!
  System.out.println(opt.orElseGet(() -> new IllegalStateException()));

  ```

#### Using Streams

- **Intro**
  - A stream in Java is a sequence of data. A stream pipeline is the operations that run on a stream to produce a result.
  - In Java, the Stream interface is in the `java.util.stream` package.
  - Remember that unless an intermediary operations tells otherwise, the streams are **processed vertically**, meaning that a given element goes through the entire pipeline. Intermediary operations that prevent this are `sort` for example, as this waits for all the elements before sorting them and passing over.
- **Creating Streams**
  - `Stream<String> empty = Stream.empty();`
  - `Stream<Integer> fromArray = Stream.of(1, 2, 3);`
  - `Collections.stream()`
    - `List<String> list = Arrays.asList("a", "b", "c");` and then `Stream<String> fromList = list.stream();`
  - `generate`, `iterate`
    - Both generate an infinite stream, but iterate re-uses the last computation value.
    - `Stream<Double> randoms = Stream.generate(Math::random);`
    - `Stream<Integer> oddNumbers = Stream.iterate(1, n -> n + 2);`
  - `Stream.generate(() -> "Elsa")`
  - Just keep in mind that it isn’t worth working in parallel for small streams.
- **Common terminal operations**
  - *Reductions* are a special type of terminal operation where all of the contents of the stream are combined into a single primitive or `Object`.
    - `long count()` - Returns the number of elements in the stream. For infinite stream it hangs.
      - `System.out.println(s.count());`
    - `Optional<T> min(<? super T> comparator), Optional<T> max(<? super T> comparator)`
      - `Optional<String> min = s.min((s1, s2) -> s1.length()—s2.length());`
  - **Other terminal operations**
    - `Optional<T> findAny(), Optional<T> findFirst()`
      - Since Java generates only the amount of stream you need, the infinite stream needs to generate only one element.
      - Not reduction, as they return a value based on the stream but do not reduce the entire stream into one value.
      - `s.findAny().ifPresent(System.out::println)`
      - `findFirst()` explicitly finds the first element of the stream, while `findAny()` can pick any random element. (Most likely the first element, but not guaranteed!)
    - `boolean anyMatch(Predicate <? super T> predicate), boolean allMatch(Predicate <? super T> predicate), boolean noneMatch(Predicate <? super T> predicate)`
      - `noneMatch` and `allMatch` would run forever on infinite streams. `anyMatch` may also run, if the predicate returns false for all elements.
      - `System.out.println(list.stream().anyMatch(pred));` (Usage same with the other two, as you need to pass a predicate.)
    - `void forEach(Consumer<? super T> action)`
      - This is the only terminal operation with a return type of `void`
      - Streams cannot use a traditional for loop to run because they don’t implement the `Iterable` interface.
    - `reduce()`
      - The reduce() method combines a stream into a single object. There are many overloaded signatures.
      - `T reduce(T identity, BinaryOperator<T> accumulator)`
      - `Optional<T> reduce(BinaryOperator<T> accumulator)`
      - `<U> U reduce(U identity, BiFunction<U,? super T,U> accumulator, BinaryOperator<U> combiner)`
      - Examples:
        - `String word = stream.reduce("", (s, c) -> s + c);` or `String word = stream.reduce("", String::concat);`
        - `BinaryOperator<Integer> op = stream.reduce(1, (a, b) -> a*b)` --> `threeElements.reduce(op).ifPresent(System.out::print)`
        - `empty.reduce(op).ifPresent(System.out::print);` no output!
      - When you don’t specify an identity, an `Optional` is returned because there might not be any data.
    - `collect()`
      - The `collect()` method is a special type of reduction called a *mutable reduction*.
      - It is more efficient than a regular reduction because we use the same mutable object while accumulating. Common mutable objects include `StringBuilder` and `ArrayList`.
      - `<R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner)`
        - Used to soecify how the collection should be done. 
          - `StringBuilder word = stream.collect(StringBuilder::new, StringBuilder::append, StringBuilder:append)`
            - The first parameter is a Supplier that creates the object that will store the results as we collect data.
            - The second parameter is a BiConsumer, which takes two parameters and doesn’t return anything. It is responsible for adding one more element to the data collection. In this exam- ple, it appends the next String to the StringBuilder.
            - The final parameter is another BiConsumer. It is responsible for taking two data collections and merging them. This is useful when we are processing in parallel.
          - `TreeSet<String> set = stream.collect(TreeSet::new, TreeSet::add, TreeSet::addAll);` would be another exmaple.
          - `TreeSet<String> set = stream.collect(Collectors.toCollection(TreeSet::new));`
      - `<R,A> R collect(Collector<? super T, A,R> collector)`
- **Common Intermediate Operations**
  - `Stream<T> filter(Predicate<? super T> predicate)`
    - `s.filter(x -> x.startsWith("m")).forEach(System.out::print);`
  - `Stream<T> distinct()`
    - `s.distinct().forEach(System.out::print);`
  - `Stream<T> limit(int maxSize), Stream<T> skip(int n)`
    - The first one limits the number of elements in the stream
    - The second one skips the first n elements in the pipeline.
    - For `Stream<Integer> s = Stream.iterate(1, n -> n + 1); s.skip(5).limit(2).forEach(System.out::print);` will print 67.
  - `<R> Stream<R> map(Function<? super T, ? extends R> mapper)`
    - `s.map(String::length).forEach(System.out::print);`
  - `<R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper)`
    - Helpful when combining list of streams.
    - `Stream<List<String>> animals = Stream.of(zero, one, two);`
    - `animals.flatMap(l -> l.stream()).forEach(System.out::println);` --> prints the content of each ArrayList (zero, one, two)
  - `Stream<T> sorted(), Stream<T> sorted()`
    - `s.sorted(Comparator.reverseOrder()).forEach(System.out::print);` or `s.sorted().forEach(System.out::print);`
  - `Stream<T> peek(Consumer<? super T> action)`
    - The most common use for `peek()` is to output the contents of the stream as it goes by.
    - `long count = stream.filter(s -> s.startsWith("g")).peek(System.out::println).count();` --> prints the element(s) that go by
    - Normally you should not modify the data that is passing through the `peak` method, however Java doesn't prevent you doing so.
      - `good.peek(l -> builder.append(l)).map(List::size).forEach(System.out::print);`
        - Assuming that good contains a `List<Integer>` and `List<String>` the peek adds the values to an "external" `StringBuilder`.
      - `bad.peek(l -> l.remove(0)).map(List::size).forEach(System.out::print);`
        - This one removes elements from the passed lists. Not good!
- **Working with Primitives**
  - The primitive streams know how to perform certain common operations automatically. Hence the `IntStream` has a function such as `sum` that allows you to calculate the sum of the elements in the stream directly.
    - `OptionalDouble avg = intStream.average();` is also a nice exmaple.
  - `IntStream`: Used for the primitive types int, short, byte, and char.
  - `LongStream`: Used for the primitive type long
  - `DoubleStream`: Used for the primitive types double and float.
  - **Creating Primitive Streams**
    - `DoubleStream empty = DoubleStream.empty();`
    - `DoubleStream oneValue = DoubleStream.of(3.14);`
    - `DoubleStream fractions = DoubleStream.iterate(.5, d -> d / 2);`
    - The `range` function is pretty cool
      - `IntStream range = IntStream.range(1, 6);` --> not including 6!
      - `IntStream rangeClosed = IntStream.rangeClosed(1, 5);` --> including 5!
    - Mapping a Stream of Strings to IntStream: `IntStream intStream = objStream.mapToInt(s -> s.length())`
    - The `sum()` method does not return an optional.
  - **Optional with Primitive Streams**
    - `OptionalDouble optional = stream.average();`
    - `System.out.println(optional.getAsDouble());`
  - **Statistics**
    - Problem: You can only calculate `min` or `max` once in a stream, as these are terminal operations. But what if you need both?

      ```java
      private static int range(IntStream ints) {
        IntSummaryStatistics stats = ints.summaryStatistics();
        if (stats.getCount() == 0) throw new RuntimeException();
          return stats.getMax()—stats.getMin();
      }
      ```
- **Booleans**
  - `BooleanSupplier` is a separate type. It has one method to implement: `boolean getAsBoolean()`

#### Working with Advanced Stream Pipeline Concepts

- **Linking Streams with Underlying DataTypes**
  - Remember that streams are lazily evaluated. Hence if you create a Stream from a List that contains two elements. Then add an element to the list, and **only then** start using the stream, then the list will contain 3 elements! Meaning that Streams are kind of like a "view" on the given collection.
- **Working with checked exceptions**
  - The Supplier interface does not allow checked exceptions. There are two approaches to get around this problem. 
  - a) Turn it into unchecked exceptions.

    ```java
    Supplier<List<String>> s = () -> {
      try {eturn ExceptionCaseStudy.create(); }
      catch (IOException e) {throw new RuntimeException(e); }};
    ```

  - b) Create a wrapper method

    ```java
    private static List<String> createSafe() {
      try {return ExceptionCaseStudy.create(); }
      catch (IOException e) {throw new RuntimeException(e); }}
    Supplier<List<String>> s2 = ExceptionCaseStudyHelper::createSafe;
    ```

- **Collecting results**
  - **Basics**
    - Notice how the predefined collectors are in the Collector**s** class rather than the Collector class.
    - `String result = ohMy.collect(Collectors.joining(", "));`
    - `Double result = ohMy.collect(Collectors.averagingInt(String::length));`
    - It is very important to pass the Collector to the collect method. It exists to help collect elements. A Collector doesn’t do anything on its own.
  - **Collecting into Maps**
    - `Map<String, Integer> map = ohMy.collect(Collectors.toMap(s -> s, String::length));`
    - Let's analyze the following snippet

      ```java
      Stream<String> ohMy = Stream.of("lions", "tigers", "bears");
      Map<Integer, String> map = ohMy.collect(
        Collectors.toMap(String::length, k -> k, (s1, s2) -> s1 + "," + s2));
      System.out.println(map); // {5=lions,bears, 6=tigers}
      System.out.println(map.getClass()); // class. java.util.HashMap
      ```

    - The key will be the length of the string. The value will be the string itself. However we need to tell the compiler what to do when two strings are equal! (Otherwise runtime exception is thrown!)
    - It so happens that the Map returned is a `HashMap`. This behavior is not guaranteed. Suppose that we want to mandate that the code return a `TreeMap` instead. Then we must pass `TreeMap::new` as the fourth argument!
  - **Collecting Using Grouping, Partitioning, and Mapping**
    - The `groupingBy()` collector tells `collect()` that it should group all of the elements of the stream into lists, organizing them by the function provided.
      - `Map<Integer, List<String>> map = ohMy.collect(Collectors.groupingBy(String::length));` -- {5=[lions, bears], 6=[tigers]}
      - `Map<Integer, Set<String>> map = ohMy.collect(Collectors.groupingBy(String::length, Collectors.toSet()));` -- {5=[lions, bears], 6=[tigers]}
      - `TreeMap<Integer, List<String>> map = ohMy.collect(Collectors.groupingBy(String::length, TreeMap::new, Collectors.toList()));`
      - `Map<Integer, Long> map = ohMy.collect(Collectors.groupingBy(String::length, Collectors.counting()));` -- {5=2, 6=1}
    - Partitioning is like splitting a list into two parts.
      - `Map<Boolean, List<String>> map = ohMy.collect(Collectors.partitioningBy(s -> s.length() <= 5));` -- {false=[tigers], true=[lions, bears]}
      - `Map<Boolean, List<String>> map = ohMy.collect(Collectors.partitioningBy(s -> s.length() <= 7));` -- {false=[], true=[lions, tigers, bears]}
        - Notice that there are still two keys! (false and true, even if false is empty)
      - Unlike groupingBy(), **we cannot change the type of Map that gets returned**.
      - Partitions only give `Boolean` as key!

#### Learnings

- When generating streams `iterate` uses the output of the previous accumulation. Think of creating a series of 1 2 3 4... On the other hand `generate` doesn't rely on the result of the previous iteration. Both return infinite streams!
- The `Collectors.joining()` function joins the stream elements to a string, or (if specified explicitly) does so using the default delimiter.
- If no terminal operation is specified the stream never executes. Hence `System.out.println(stream.limit(2).map(x -> x + "2"));` prints something like `java.util.stream.ReferencePipeline$3@4517d9a3`
- `noneMatch(predicate)` terminates if the predicate evaluates to `false`. In this case the function returns `false`. Given an infinite stream and a predicate that always evaluates to true it hangs.
- Watch out! Trying to reuse a stream will result in a `RuntimeException`!!!
- `sum()` is a terminal reduction operation, however it is **only available** in the primitive streams, but not in the default `Stream` class.
- The `sum()` method returns an `int` rather than an `OptionalInt` because the sum of an empty list is zero.
- The `average()` method returns an `OptionalDouble`, (even in an `IntStream`) because the average can be a fractal.
- `ds.mapToInt(x -> x);` - converting a double to int this way would require an explicit cast in the lambda!
- Partitions only give `Boolean` as key.
- The `partitioningBy()` operation always returns a map with two `Boolean` keys, even if there are no corresponding values. By contrast, `groupingBy()` returns only keys that are actually needed.