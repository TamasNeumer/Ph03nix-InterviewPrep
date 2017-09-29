# Streams

#### Baics
- A Stream is a sequence of elements upon which sequential and parallel aggregate operations can be performed. Any given Stream can potentially have an unlimited amount of data flowing through it. As a result, data received from a Stream is processed individually as it arrives, as opposed to performing batch processing on the data altogether. When combined with lambda expressions they provide a concise way to perform operations on sequences of data using a functional approach.

```java
Stream<String> fruitStream = Stream.of("apple", "banana", "pear", "kiwi", "orange");

fruitStream.filter(s -> s.contains("a"))
           .map(String::toUpperCase)
           .sorted()
           .forEach(System.out::println);
```
- Notes
     - The `map()` operation will return a stream with a different generic type if the mapping function returns a type different to its input parameter. For example on a `Stream<String>` calling `.map(String::isEmpty)` returns a `Stream<Boolean>`

- Closing streams
  - Note that **a Stream generally does not have to be closed**. It is only required to close streams that operate on IO channels. Most Stream types don't operate on resources and therefore don't require closing.
  - The Stream interface extends `AutoCloseable`. Streams can be closed by calling the close method or by using try-with-resource statements.
  - An example use case where a Stream should be closed is when you create a Stream of lines from a file:

  ```java
  try (`Stream<String>` lines = Files.lines(Paths.get("somePath"))) {
    lines.forEach(System.out::println);
  }
  ```

- Processing order:
  - In a **sequential mode**, the elements are processed in the order of the source of the Stream. (Only if the Stream is ordered like SortedMap or List)
  - **Parallel mode** allows the use of multiple threads on multiple cores but there is no guarantee of the order in which elements are processed.

- Streams vs Containers
  - While some actions can be performed on both Containers and Streams, they ultimately serve different purposes and support different operations. Containers are more focused on how the elements are stored and how those elements can be accessed efficiently. A Stream, on the other hand, doesn't provide direct access and manipulation to its elements; it is more dedicated to the group of objects as a collective entity and performing operations on that entity as a whole. Stream and Collection are separate high-level abstractions for these differing purposes.

- Consuming Streams
  - A Stream will only be traversed when there is a terminal operation, like count(), collect() or forEach(). Otherwise, no operation on the Stream will be performed.
  - After the terminal operation is performed, the Stream is consumed and cannot be reused.
    - This would be useful for returning a modified view of a live data set without having to collect results into a temporary structure.
    - Workaround: create reusable `Iterable` that delegates to a stream pipeline.

  ```java
  List<String> list = Arrays.asList("FOO", "BAR");
  Iterable<String> iterable = () -> list.stream().map(String::toLowerCase).iterator();
  ```

- Creating a frequency map
```java
Stream.of("apple", "orange", "banana", "apple")
      .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
      .entrySet()
      .forEach(System.out::println);
```
  - Workflow:
    - `collect()` collects the elements of streams into a container
    - The `groupingBy(classifier, downstream)` collector allows the collection of Stream elements into a Map by classifying each element in a group and performing a downstream operation on the elements classified in the same group.
      - classifier is simply the identity function, which returns the element as-is
      - the downstream operation counts the number of equal elements, using counting()

- Infinite streams
  - The limit method of a Stream can be used to limit the number of terms of the Stream that Java processes.

    ```java
      // Generate infinite stream - 1, 2, 3, 4, 5, 6, 7, ...
    IntStream naturalNumbers = IntStream.iterate(1, x -> x + 1);

    // Print out only the first 5 terms
    naturalNumbers.limit(5).forEach(System.out::println);
    ```

-
