# Streams

1. Iterators imply a specific traversal strategy and prohibit efficient concurrent execution.
2. You can create streams from collections, arrays, generators, or iterators.
3. Use filter to select elements and map to transform elements.
4. Other operations for transforming streams include limit, distinct, andsorted.
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
- Stream operations are either intermediate or terminal. Intermediate operations return a stream so we can chain multiple intermediate operations without using semicolons. Terminal operations are either void or return a non-stream result.
- Simply changing `stream` into `parallelStream` allows the stream library to do the filtering and counting in parallel.

**Stream workflor**
1. Create a stream.
2. Specify intermediate operations for transforming the initial stream into others, possibly in multiple steps.
3. Apply a terminal operation to produce a result. This operation forces the execution of the lazy operations that precede it. Afterwards, the stream can no longer be used.

#### 1 Stream Creation
- You can turn any collection into a stream with the stream method of the Collection interface. Just use `Stream.of()` to create a stream from a bunch of object references.
  - `Stream<String> song = Stream.of("gently", "down", "the",
"stream")`
  - `Arrays.stream(array, from, to)`
    - `Arrays.stream(new int[] {1, 2, 3})`
- Creating streams of primitve types happens via the `IntStream`, `DoubleStream`, or as above.
  - `IntStream.range(1, 4).forEach(System.out::println);`

#### 2 Intermediate Operations
Intermediate operations will only be executed when a terminal operation is present. Also the order in which you chain these operations is important. Note that usually chained operations (such as filter or sort) are performed vertically, while sort or sorted are performed horizontally. [Link](http://winterbe.com/posts/2014/07/31/java8-stream-tutorial-examples/)

- filter
  - `Stream<String> longWords = words.stream().filter(w -> w.length()>= 12);`
  - The argument of filter is a `Predicate<T>`—that is, a function from T to
boolean.
- map
  - Use the map method and pass the function that carries out the transformation.
  - `words.stream().map(String::toLowerCase);`
- flatMap
  -  Map is kinda limited because every object can only be mapped to exactly one other object.But what if we want to transform one object into multiple others or none at all?
  - FlatMap transforms each element of the stream into a stream of other objects. So each object will be transformed into zero, one or multiple other objects backed by streams. The contents of those streams will then be placed into the returned stream of the flatMap operation.
  - Used to flatten multiple arrays into one.
- skip(n)
  - Discard first n elements
- limit(n)
  - Only consider the first n elements
- distinct
  - The distinct method returns a stream that yields elements from the original
stream, in the same order, except that duplicates are suppressed.
- sorted
  - For sorting a stream, there are several variations of the sorted method. One works for streams of Comparable elements, and another accepts a Comparator.
- peek
  - The peek method yields another stream with the same elements as the
original, but a function is invoked every time an element is retrieved.
    ```java
    Object[] powers = Stream.iterate(1.0, p -> p * 2)
    .peek(e -> System.out.println("Fetching " + e))
    .limit(20).toArray();
    ```



#### 3. Reductions
Java 8 streams cannot be reused.As soon as you call any terminal operation the stream is closed.

- max / min
  - These methods return an Optional<T> value that either wraps
the answer or indicates that there is none (because the stream happened to be empty).
- average()
- forEach()
- collect()
  - **Collect is an extremely useful terminal operation** to transform the elements of the stream into a different kind of result, e.g. a List, Set or Map. (It has many methods, feel free to look into the Java docs.)
  - `List<String> result = stream.collect(Collectors.toList());`
  - `TreeSet<String> result =
stream.collect(Collectors.toCollection(TreeSet::new));`
  - **Very nice** collection to map below. (Result: age 23: [Peter, Pamela] etc.)
- anyMatch()
  - anyMatch returns true as soon as the predicate applies to the given input element.

```java
Map<Integer, List<Person>> personsByAge = persons
    .stream()
    .collect(Collectors.groupingBy(p -> p.age));
```

  - reduce()
    - The `reduction` operation combines all elements of the stream into a single result. There are three forms:
      1. The reduce method accepts a BinaryOperator accumulator function.
        - `.reduce((p1, p2) -> p1.age > p2.age ? p1 : p2)`
      2. Accepts both an identity value and a BinaryOperator accumulator
    ```java
    Person result =
    persons
        .stream()
        .reduce(new Person("", 0), (p1, p2) -> {
            p1.age += p2.age;
            p1.name += p2.name;
            return p1;
        });  
    ```
      3. Accepts three parameters: an identity value, a BiFunction accumulator and a combiner function of type BinaryOperator... 
