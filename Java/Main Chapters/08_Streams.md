# Streams

1. Iterators imply a specific traversal strategy and prohibit efficient concurrent execution.
2. You can create streams from collections, arrays, generators, or iterators.
3. Use filter to select elements and map to transform elements.
4. Other operations for transforming streams include limit, distinct, and sorted.
5. To obtain a result from a stream, use a reduction operator such as count, max, min, findFirst, or findAny. Some of these methods return an Optional
value.
6. The Optional type is intended as a safe alternative to working with null values. To use it safely, take advantage of the ifPresent and orElse
methods.
7. You can collect stream results in collections, arrays, strings, or maps.
8. The groupingBy and partitioningBy methods of the Collectors class
allow you to split the contents of a stream into groups, and to obtain a result for each group.
9. There are specialized streams for the primitive types int, long, and double.
10. Parallel streams automatically parallelize stream operations.

#### Intro
- A stream is a **sequence of elements** that **does not save the elements or modify the original source.**
- Stream operations are either **intermediate** or **terminal**.
  - Intermediate operations return a stream so we can chain multiple intermediate operations without using semicolons.
  - Terminal operations are either void or return a non-stream result.
- Simply changing `stream` into `parallelStream` allows the stream library to do the filtering and counting in parallel.
- Note that **a Stream generally does not have to be closed**. It is only required to close streams that operate on IO channels. Most Stream types don't operate on resources and therefore don't require closing.
  - In this context "closing" as like "closing a file". The stream MUST have a terminal operation!
- The Stream interface extends `AutoCloseable`. Streams can be closed by calling the close method or by using try-with-resource statements.
  - An example use case where a Stream should be closed is when you create a Stream of lines from a file:

  ```java
  try (`Stream<String>` lines = Files.lines(Paths.get("somePath"))) {
    lines.forEach(System.out::println);
  }
  ```

- Streams vs Containers
  - While some actions can be performed on both Containers and Streams, they ultimately serve different purposes and support different operations. Containers are more focused on how the elements are stored and how those elements can be accessed efficiently. A Stream, on the other hand, doesn't provide direct access and manipulation to its elements; it is more dedicated to the group of objects as a collective entity and performing operations on that entity as a whole. Stream and Collection are separate high-level abstractions for these differing purposes.

**Stream workflor**
1. Create a stream.
2. Specify intermediate operations for transforming the initial stream into others, possibly in multiple steps.
3. Apply a terminal operation to produce a result. This operation forces the execution of the lazy operations that precede it. Afterwards, the stream can no longer be used.

#### 1 Stream Creation
**Creating streams**
- `static <T> Stream<T> of(T... values)`
  - The implementation of the of method in the standard library actually delegates to the `Arrays.stream()` stream method in the Arrays class.
  - `Stream<String> song = Stream.of("gently", "down", "the", "stream")`
- `Arrays.stream(array, from, to)`
    - `Arrays.stream(new int[] {1, 2, 3})`
- `static <T> Stream<T> iterate(T seed, UnaryOperator<T> f)`
  - this method returns an infinite (emphasis added) sequential ordered Stream produced by iterative application of a function f to an initial element seed.

  ```java
  List<BigDecimal> nums =
  Stream.iterate(BigDecimal.ONE, n -> n.add(BigDecimal.ONE) )
  .limit(10)
  .collect(Collectors.toList());
  System.out.println(nums);
  // prints [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
  ```

- `static <T> Stream<T> generate(Supplier<T> s)`
  - This method produces a sequential, unordered stream by repeatedly invoking the Supplier (no arg -> non-void return, as Math.random).

    ```java
    long count = Stream.generate(Math::random)
    .limit(10)
    .forEach(System.out::println);
    ```

- There are three child interfaces of Stream specifically for working with primitives: `IntStream` , `LongStream` , and `DoubleStream` .
  - `IntStream.range(1, 4).forEach(System.out::println);`
  - `IntStream.rangeClosed(1, 4).forEach(System.out::println);`
- Lastly there is the `Collection.stream()`
  - `Stream<String> sorted = list.stream().sorted();`

**Boxed streams = stream from primitives**  
- The following woN'T work (compile error):
  - `IntStream.of(3, 1, 4, 1, 5, 9).collect(Collectors.toList());
- Alternatives
  - Using the `boxed()` method
    - `List<Integer> ints = IntStream.of(3, 1, 4, 1, 5, 9).boxed().collect(Collectors.toList());`
    - `boxed()` will convert the ints to Integers
  - Using the `mapToObj()`
    - List<Integer> ints = IntStream.of(3, 1, 4, 1, 5, 9).mapToObj(Integer::valueOf).collect(Collectors.toList())

#### 2 Intermediate Operations
Intermediate operations will only be executed when a terminal operation is present. Also the order in which you chain these operations is important. Note that usually chained operations (such as filter or sort) are performed vertically, while sort or sorted are performed horizontally. [Link](http://winterbe.com/posts/2014/07/31/java8-stream-tutorial-examples/)

- `filter()`
  - `Stream<String> longWords = words.stream().filter(w -> w.length()>= 12);`
  - The argument of filter is a `Predicate<T>`—that is, a function from T to
boolean.
- `map()` & `flatMap()`
  - Both the map and the flatMap methods on Stream take a Function as an argument.
  - `<R> Stream<R> map(Function<? super T,? extends R> mapper)`
    - A Function takes a single input and transforms it into a single output. In the case of `map`, a single input of type `T` is transformed into a single output of type `R`.
  -  `map()` is kinda limited because every object can only be mapped to exactly one other object. (`customers.stream().map(Customer::getName).forEach(System.out::println)`)
    - If Customer has a member `List<Integer> orders` and we are mapping the customers to their list of orders we would get a `Stream<List<Integer>>`. This is where `flatMap()` comes in.
  - `flatMap()`
    - `<R> Stream<R> flatMap(Function<? super T,? extends Stream<? extends R>> mapper)`
    - For each generic argument `T` , the function produces a `Stream<R>` rather than just an `R`. The `flatMap` method then “flattens” the resulting stream by removing each element from the individual streams and adding them to the output.
    - `customers.stream().flatMap(customer -> customer.getOrders().stream()) .forEach(System.out::println);`
  - The result of the flatMap operation is to produce a Stream<Order> , which has been flattened so you **don’t need to worry about the nested streams** any more.
- `skip(n)`
  - Discard first n elements
- `limit(n)`
  - Only consider the first n elements
- `distinct()`
  - The distinct method returns a stream that yields elements from the original
stream, in the same order, except that duplicates are suppressed.
- `sorted(Comparator)`
  - For sorting a stream, there are several variations of the sorted method. One works for streams of Comparable elements, and another accepts a Comparator.
- **peek()**  
  - You can use `peek()` as an intermediate opertaion to display the current value.

  ```java
  public int sumDoublesDivisibleBy3(int start, int end) {
    return IntStream.rangeClosed(start, end)
      .peek(n -> System.out.printf("original: %d%n", n))
      .map(n -> n * 2)
      .peek(n -> System.out.printf("doubled : %d%n", n))
      .filter(n -> n % 3 == 0)
      .peek(n -> System.out.printf("filtered: %d%n", n))
      .sum();
  }
  ```

#### 3. Reductions
Java 8 streams cannot be reused. As soon as you call any terminal operation the stream is closed. The primitive streams IntStream , LongStream , and DoubleStream have several reduction operations built into the API:

**Easy reductions**
-  These methods return an `Optional<T>` value that either wraps the answer or indicates that there is none (because the stream happened to be empty).
- `max()` / `min()`
  - Either only on primitives or on objects that implement `Comparator`.
  - OR: The Stream interface has max(Comparator) and min(Comparator) , where the comparators are used to determine the max or min element.
- `average()` / `sum()`
  - On primitives only
- `count()`
- `forEach()`
- `anyMatch(predicate)`, `allMatch(predicate)`, `noneMatch(predicate)`

```java
public boolean isPrime(int num) {
  int limit = (int) (Math.sqrt(num) + 1);
  return num == 2 || num > 1 && IntStream.range(2, limit)
    .noneMatch(divisor -> num % divisor == 0);
}
```

**reduce()**  
 - The `reduction` operation combines all elements of the stream into a single result. There are three forms:
   1. The reduce method accepts a `BinaryOperator` accumulator function. (BinaryOperator = 2input 1 output, all same type)
     - `int sum = IntStream.rangeClosed(1, 10).reduce((x, y) -> x + y).orElse(0); // Returns 55`
     - Chaining the `orElse` method to it indicates that if there are no elements in the stream, the return value should be zero.
     - The value returned by the binary operator becomes the value of x (i.e., the accumulator) on the next iteration, while y takes on each value in the stream.
     - **Identity:** The demonstrations used in this recipe referred to the first argument as an initial value for the accumulator, even though the method signature called it `identity` . The word `identity` means that you should supply a value to the binary operator that, when combined with any other value, returns the other value. For addition, the `identity` is zero. For multiplication, the `identity` is 1. For string concatenation, the `identity` is the empty string. E.g.: `int sum = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).reduce(0, Integer::sum);`
   2. Accepts both an identity value and a BinaryOperator accumulator (seen above)
   3. Accepts three parameters: an identity value, a BiFunction accumulator and a combiner function of type BinaryOperator.
    - Identity value for `putAll` is `new HashMap<Integer, Book>()`
    - Accumulate a single book into Map using `put`
    - Combine multiple Map s using `putAll`

  ```java
  HashMap<Integer, Book> bookMap = books.stream()
    .reduce(new HashMap<Integer, Book>(),
        (map, book) -> {
        map.put(book.getId(), book);
        return map;
        },
        (map1, map2) -> {
        map1.putAll(map2);
        return map1;
        });
  ```

**collect()**  
  - **Collect is an extremely useful terminal operation** to transform the elements of the stream into a different kind of result, e.g. a List, Set or Map. (It has many methods, feel free to look into the Java docs.)
  - `List<String> result = stream.collect(Collectors.toList());`
  - `TreeSet<String> result = stream.collect(Collectors.toCollection(TreeSet::new));`
  - **Very nice** collection to map below. (Result: age 23: [Peter, Pamela] etc.)
- anyMatch()
  - anyMatch returns true as soon as the predicate applies to the given input element.
- `groupingBy` similar to SQL. Usually used if you want to group elements into a Map:

  ```java
  Map<BlogPostType, List<BlogPost>> postsPerType = posts.stream()
    .collect(groupingBy(BlogPost::getType));
  ```

- `partitioningBy` splits elements into those that satisfy a
Predicate and those that do not.
- downstream operation: the purpose of a downstream collector is to postprocess the collection of objects produced by an upstream operation, like partitioning or grouping.
  - Operations: `count`, `min/max`, `IntegerStream.sum` etc.

  ```java
  Map<Boolean, List<String>> lengthMap = strings.stream()
  .collect(Collectors.partitioningBy(s -> s.length() % 2 == 0));
  // false: [a, strings, use, a]
  // true: [this, is, long, list, of, to, as, demo]

  Map<Boolean, Long> numberLengthMap = strings.stream()
    .collect(Collectors.partitioningBy(s -> s.length() % 2 == 0,
    Collectors.counting()));
  // false: 4
  // true: 8
  ```

#### Encounter order
- Elements in streams are processed in **encounter order**. For lists it is defined, however for sets it is not. Hence if the stream has no encounter order, then any element may be returned when searching with `.findFirst()` (for example)


#### Lazynes
- Streams are lazy, in that no work is done until the terminal condition is reached, and then each element is processed through the pipeline individually.
- (= An element goes through the entire pipeline first.)

```java
public int multByTwo(int n) {
  System.out.printf("Inside multByTwo with arg %d%n", n);
  return n * 2;
}
public boolean divByThree(int n) {
  System.out.printf("Inside divByThree with arg %d%n", n);
  return n % 3 == 0;
}
// ...
firstEvenDoubleDivBy3 = IntStream.range(100, 200)
  .map(this::multByTwo)
  .filter(this::divByThree)
  .findFirst();
```
```
Inside multByTwo with arg 100
Inside divByThree with arg 200
Inside multByTwo with arg 101
Inside divByThree with arg 202
Inside multByTwo with arg 102
Inside divByThree with arg 204
First even divisible by 3 is Optional[204]
```

#### Converting Streams to Collections
- Use the `toList`, `toSet`, or `toCollection` methods in the `Collectors` utility class.
- Collectors perform a “mutable reduction operation” that accumulates elements into a result container.
- The `collect` method in Stream has two overloaded versions:
  - `<R,A> R collect(Collector<? super T,A,R> collector)`
  - `<R> R collect(Supplier<R> supplier, BiConsumer<R,? super T> accumulator, BiConsumer<R,R> combiner)`
- The Collectors class also contains a method to create an array of objects:
  - `Object[] toArray()`
  - `<A> A[] toArray(IntFunction<A[]> generator)`
- As you see the `Function.identity()` is the same as ` b -> b`

```java
List<String> superHeroes =
  Stream.of("Mr. Furious", "The Blue Raja", "The Shoveler", "The Bowler", "Invisible Boy", "The Spleen", "The Sphinx")
  .collect(Collectors.toList());

List<String> actors =
  Stream.of("Hank Azaria", "Janeane Garofalo", "William H. Macy",
  "Paul Reubens", "Ben Stiller", "Kel Mitchell", "Wes Studi")
  .collect(Collectors.toCollection(LinkedList::new));

String[] wannabes =
  Stream.of("The Waffler", "Reverse Psychologist", "PMS Avenger")
  .toArray(String[]::new);

Map<String, String> actorMap = actors.stream()
  .collect(Collectors.toMap(Actor::getName, Actor::getRole));

/*THE FOLLWOING TWO ARE THE SAME:*/
Map<Integer, Book> bookMap = books.stream()
  .collect(Collectors.toMap(Book::getId, b -> b));

bookMap = books.stream()
  .collect(Collectors.toMap(Book::getId, Function.identity()));
```


#### Problems
**Streaming stings**
- Strings are not part of the Collection, hence they don't implement the .stream() operation nor have Iterator.
- The String class implements the `CharSequence`
interface, and that interface contains two new methods (`codePoints` and `chars` that produce an `IntStream`.
- Checking if string is palindrome

```java
public boolean isPalindrome(String s) {
  String forward = s.toLowerCase().codePoints()
    .filter(Character::isLetterOrDigit)
    .collect(StringBuilder::new,
    StringBuilder::appendCodePoint,
    StringBuilder::append)
    .toString();
  String backward = new StringBuilder(forward).reverse().toString();
  return forward.equals(backward);
}
```
- The interesting part is in the collect method, whose signature is `<R> R collect(Supplier<R> supplier, BiConsumer<R,? super T> accumulator, BiConsumer<R,R> combiner)`
  - A Supplier, which produces the resulting reduced object, in this case a `StringBuilder`.
  - A `BiConsumer` used to accumulate each element of the stream into the resulting data structure; this example uses the `appendCodePoint` method.
  - A `BiConsumer` representing a combiner, which is a “non-interfering, stateless
function” for combining two values that must be compatible with the accumula‐
tor; in this case, the `append` method.

**Finding first element for condition**

- Use `findFirst()`
```java
Optional<Integer> firstEvenGT10 = Stream.of(3, 1, 4, 1, 5, 9, 2, 6, 5)
  .filter(n -> n > 10)
  .filter(n -> n % 2 == 0)
  .findFirst();

// Prints Optional.empty
```

**Creating frequency map fo words**
- Workflow:
  - `collect()` collects the elements of streams into a container
  - The `groupingBy(classifier, downstream)` collector allows the collection of Stream elements into a Map by classifying each element in a group and performing a downstream operation on the elements classified in the same group.
    - classifier is simply the identity function, which returns the element as-is
    - the downstream operation counts the number of equal elements, using counting()

```java
Stream.of("apple", "orange", "banana", "apple")
      .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
      .entrySet()
      .forEach(System.out::println);
```
