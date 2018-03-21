# Functional Programming

#### Variables in Lambdas

- Lambda expressions can access static variables, instance variables, effectively final method parameters, and effectively final local variables
- **Supplier**
  - It supplies aka. it returns a value.
  - `@FunctionalInterface public class Supplier<T> { public T get();}`
  - Usage:
    - `Supplier<LocalDate> s1 = LocalDate::now;` or `Supplier<LocalDate> s2 = () -> LocalDate.now();` Then `LocalDate d1 = s1.get();` returns the value.
- **Consumer and BiConsumer**
  - You use a Consumer when you want to do something with a parameter but not return anything.
  - `@FunctionalInterface public class Consumer<T> { void accept(T t);}`
  - `@FunctionalInterface public class BiConsumer<T, U> {void accept(T t, U u); }`
  - Usage:
    - `Consumer<String> c1 = System.out::println;` then `c1.accept("Annie");`
    - `Map<String, Integer> map = new HashMap<>(); BiConsumer<String, Integer> b1 = map::put;` then `b1.accept("chicken", 7);`
- **Predicate and BiPredicate**
  - Takes One (or two) arguments and returns a `boolean` value.
  - `@FunctionalInterface public class Predicate<T> { boolean test(T t);}`
  - `@FunctionalInterface public class BiPredicate<T, U> {boolean test(T t, U u); }`
  - Usage:
    - `Predicate<String> p1 = String::isEmpty;` then `System.out.println(p1.test(""));`
    - `BiPredicate<String, String> b1 = String::startsWith;` then `System.out.println(b1.test("chicken", "chick"));`
  - Functional interfaces often hve other default methods. The `Predicate`for example has the logical `and` or `negate` functions.
    - `Predicate<String> brownEggs = egg.and(brown);` - where `egg` and `brown` are two other predicates.
- **Function and BiFunction**
  - Turning one (or two) parameters into a new one.
  - `@FunctionalInterface public class Function<T, R> { R apply(T t);}`
  - `@FunctionalInterface public class BiFunction<T, U, R> { R apply(T t, U u);`
  - Usage:
    - `Function<String, Integer> f1 = String::length;` then `System.out.println(f1.apply("cluck"));`
    - `BiFunction<String, String, String> b1 = String::concat;` then `System.out.println(b1.apply("baby ", "chick"));`
  - If you need more parameters you can create your own functional interfaces. `interface TriFunction<T,U,V,R> { R apply(T t, U u, V v);}`
- **UnaryOperator and BinaryOperator**
  - A `UnaryOperator` transforms its value into one of the same type. (i.e. incrementing)
  - A `BinaryOperator` merges two values into one of the same type.
  - They both require the parameters to be the same!
  - `@FunctionalInterface public class UnaryOperator<T> extends Function<T, T> { }`
  - `@FunctionalInterface public class BinaryOperator<T> extends BiFunction<T, T, T> { }`
  - Usage:
    - `UnaryOperator<String> u1 = String::toUpperCase;` then `UnaryOperator<String> u1 = String::toUpperCase;`
    - `BinaryOperator<String> b1 = String::concat;` then `System.out.println(b1.apply("baby ", "chick"));`

#### Optional

- An `Optional` is created using a factory.

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
- You **should** check if the `Optional` has a value with `isPresent()` and if so then get its value via `get()`. Otherwise you might get `java.util.NoSuchElementException`!
- A common factory pattern is: `Optional o = Optional.ofNullable(value);`. Here if the value was 0 the optional will be `null`
- Other methods are: `orElse(T other)`, `orElseGet(Supplier s)`, `orElseThrow(Supplier s`
- Usage:

  ```java
  Optional<Double> opt = average(90, 100);
  opt.ifPresent(System.out::println);

  Optional<Double> opt = average();
  System.out.println(opt.orElse(Double.NaN));
  System.out.println(opt.orElseGet(() -> Math.random()));
  System.out.println(opt.orElseThrow(() -> new IllegalStateException()));

  System.out.println(opt.orElseGet(() -> new IllegalStateException())); // DOES NOT COMPILE. Expects Supplier type Double!
  ```
