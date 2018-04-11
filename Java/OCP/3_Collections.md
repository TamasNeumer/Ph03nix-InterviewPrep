# Generics and Collections

#### Reviewing OCA Collections

- Array to List `Arrays.toList(array)` --> **not resizable**, hence functions as `remove` will throw exception at runtime!
- List to Array `list.toArray()`
- Searching: `Arrays.binarySearch(numbers, 6)`
  - If found the index is returned
  - If not then `-(indexWhereItWouldHaveBeen) -1`
- Sorting: `Collections.sort(list);`
- Removing from Lists: `remove()` is overloaded. One takes the index, another one the object.
- The diamond operator `List<String> names = new ArrayList<>();` This is not limited to one liner operations!

#### Generics

- **Generic classes**
  - The syntax for introducing a generic is to declare a formal type parameter in angle brackets.
      ```java
      public class Crate<T> {
        private T contents;
        public T emptyCrate() {return contents; }
        public void packCrate(T contents) {
           this.contents = contents;
        }
      }
      ```
  - The convention is to use the followings:
    - E for an element
    - K for a map keywordV for a map value
    - N for a number
    - T for a generic data type
    - S, U, V and so forth for multiple generic types
  - You can have multiple parameters in a `class public class SizeLimitedCrate<T, U>`
- **Generic Interfaces**
  - Just like you can have classes, you can have interfaces.
  - `public interface Shippable<T> { void ship(T t);}`
  - Implementing this interface can happen via:
    - Specify generic type in class: `class ShippableRobotCrate implements Shippable<Robot> {`
    - Create a generic class: `class ShippableAbstractCrate<U> implements Shippable<U>`
    - Without generics: `class ShippableCrate implements Shippable { public void ship(Object t) { }}`
      - Gives compiler warning.
- **Generic methods**
  - The method parameter is the generic type `T`. The return type is a `Crate<T>`. Before the return type, we declare the formal type parameter of `<T>`.
    ```java
    public static <T> Crate<T> ship(T t) {
       System.out.println("Preparing " + t);
       return new Crate<T>();
    }
    public static <T> void sink(T t) { }
    public static <T> T identity(T t) { return t; }
    public static T noGood(T t) { return t; } // DOES NOT COMPILE
    ```
- **Before generics**
  - Before generics you could write unchecked code that threw run time exceptions.

    ```java
    public static void main(String[] args) {
      List unicorns = new ArrayList();
      unicorns.add(new Unicorn()); printDragons(unicorns);
    }
    private static void printDragons(List<Dragon> dragons) {
      for (Dragon dragon: dragons) { // ClassCastException System.out.println(dragon);
    }}
    ```

- **Bounds**
  - A *bounded parameter type* is a generic type that specifies a bound for the generic.
  - A *wildcard generic type* is an unknown generic type represented with a question mark (?).
  - **Unbounded Wildcards**
    - An unbounded wildcard represents any data type. You use `?` when you want to specify that any type is OK with you.
    - Note that Java won't let you to assign a `List<String>` reference to a `List<Object>` (you would be later able to put `Integer`s into the list of `String`s.) This is **not** the case with arrays. At arrays you can play around with the references and get RunTimeException later.
    - The following code would not compile if we had `List<Objects>` in `printList` argument. (And even if `String` is an `Object`...)
      ```java
      public static void printList(List<?> list) {
         for (Object x: list) System.out.println(x);
      }
      public static void main(String[] args) {
        List<String> keywords = new ArrayList<>();
        keywords.add("java");
        printList(keywords);
      }
      ```
  - **Upper-Bounded Wildcards**
    - As seen before generic type can't use subclasses: `ArrayList<Number> list = new ArrayList<Integer>(); // DOES NOT COMPILE`
    - Instead we need to use a wildcard: `List<? extends Number> list = new ArrayList<Integer>();`
    - The upper-bounded wildcard says that any class that extends Number or Number itself can be used as the formal parameter type.
    - When using such upper bounded wildchars note that the lists are **immutable**. This is because Java doesn't know what you want to add. (Integer, Long, Double etc.), hence `list.add(new Integer(6))` will result in compile error.
    - Use the keyword `extends` even if we are talking about interfaces, and `Number` was an interface.
  - **Lower-bounded Wildcards**
    - As we saw before we can't add any elements to the generic list. Here comes the rescue.
    - `public static void addSound(List<? super String> list)`
    - With a lower bound, we are telling Java that the list will be a list of `String` objects or a list of some objects that are a superclass of `String`. Either way, it is safe to add a `String` to that list.
    - The following won't compile. Line 3 references a List that could be `List<IOException>` or `List<Exception>` or `List<Object>`. Line 4 does not compile because we could have a `List<IOException>` and an Exception object wouldn’t  t in there.
      ```java
      List<? super IOException> exceptions = new ArrayList<Exception>();
      exceptions.add(new Exception()); // DOES NOT COMPILE
      ```
  - **Putting all together**
    - Remember, that in `List<? super A>` you can store `A`s and any parent classes. In `List<? extends B>` you can store `B` and any subclass.
    - `List<?> list6 = new ArrayList<? extends A>();` **won't** compile as the compiler needs to know the exact type when initialising the `ArrayList`.
    - `<T> <? extends T> method2(List<? extends T> list)` --> in a method you **must** specify the return type.
    - `<B extends A>` says that you want to use `B` as a type parameter just for this method and that it needs to extend the `A` class. Coincidentally, `B` is also the name of a class. Within the scope of the method, `B` can represent classes `A`, `B`, or `C`, because all extend the A class. Since `B` no longer refers to the `B` class in the method, you can’t instantiate it.
      ```java
      <B extends A> B method3(List<B> list) {
         return new B(); // DOES NOT COMPILE
      }
      ```
    - A wildcard must have a `?` in it.

#### Collections

- Note that `List`, `Set`, `Queue` are part of the `Collection` interface, however `Map` is totally separate.
- **Common Collection methods**
  - `boolean add(E element)` - In Lists the return is usually true, while at Sets it is false, if the set already contained the entry.
  - `boolean remove(Object object)` - `true` if a match was found and removed.
  - `boolean isEmpty(), int size()`
  - `void clear()`
  - `boolean contains(Object object)`
- **The List interface**
  - **ArrayList**
    - resizable
    - O(1) lookup via `get(index)`
  - **LinkedList**
    - O(n) lookup.
    - Better if you add elements frequently and you have a large array.
  - If you need a stack, use an `ArrayDeque` instead
  - List methods
    - `void add(E element)`
    - `void add(int index, E element)`
    - `E get(int index)`
    - `int indexOf(Object o)`
    - `int lastIndexOf(Object o)`
    - `void remove(int index)`
    - `E set(int index, E e)` -- Replaces element at index and returns original
  - The two oldchool foreach implementations. Note the type <String>
    ```java
    Iterator iter = list.iterator();
    while(iter.hasNext()) {
      String string = (String) iter.next(); // Explicit cast!
      System.out.println(string);
    }

    Iterator<String> iter = list.iterator();
    while(iter.hasNext()) {
      String string = iter.next(); // no cast required!
      System.out.println(string);
    }
    ```
  - **Set**
    - **HashSet**
      - Looping through and printing the elements happens in **arbitrary** order!
    - **TreeSet**
      - Implements the **NavigableSet** interface
        - `E lower(E e)` (greatest element that is smaller than e) `<`
        - `E floor(E e)` smallest element that is `<=` e
        - `E ceiling(E e)` Returns smallest element that is `>=` e
        - `E higher(E e)` Returns smallest element that is `>` e
        - All of these return **null** if entry was not found!
      - Stores elements in a tree structure - sorted order!
      - `O(log n)` is the adding and checking in return...
      - TreeSet uses the `Comparable` interface (?)
- **Queue**
  - FIFO implementation.
  - An `ArrayDeque` is a “pure” double-ended queue, it stores its elements in a resizable array.
  - `ArrazDeque` is more efficient as a `LinkedList`
  - Methods
    - **Only queue**
      - **Throwing**
        - `boolean add(E e)` - Adds an element to the back of the queue and returns true or throws an  `NullPointerException` if the specified element is `null`
        - `void push(E e)` - Adds an element to the front ("end") of the queue. `NullPointerException` if the specified element is null
        - `E element()` - Returns next element or throws `NoSuchElementException` if this deque is empty
        - `E remove()` - Removes and returns next element or throws `NoSuchElementException` if this deque is empty
        - `E pop()` - Removes and returns next element or throws `NoSuchElementException` if this deque is empty
      - **Non-throwing**
        - `E poll()` - Retrieves and removes the head of the queue represented by this deque (in other words, the first element of this deque), or returns `null` if this deque is empty.
        - `E peek()` - Retrieves, but does not remove, the head of the queue represented by this deque, or returns `null` if this deque is empty.
        - `boolean offer(E e)` - Adds an element to the back of the queue and returns whether successful.
- **Map**
  - **HashMap**
  - **LinkedHashMap**
  - **TreeHashMap**
  - **Hashtable** (old and thread-safe. with small `t`!)
  - **Methods**
    - `V get(Object key)` - Returns the value mapped by key or `null` if none is mapped.
    - `V put(K key, V value)` - Returns the value mapped by key or `null` if none is mapped.
    - `V remove(Object key)` - Removes and returns value mapped to key. Returns `null` if none.
    - `boolean containsKey(Object key)`
    - `boolean containsValue(Object)`
    - `Set<K> keySet()`
    - `Collection<V> values()`
- **Notes**
  - The data structures that involve sorting do not allow `nulls`. (We can’t compare a null and a String.) --> This means that `TreeSet` cannot contain `null` elements. It also means that `TreeMap` cannot contain `null` keys. Null **values** are OK.
  - You can’t put `null` in an `ArrayDeque` because methods like `poll()` use `null` as a special return value to indicate that the collection is empty.
  - Finally, `Hashtable` doesn’t allow `null` keys or values.

#### Comparator vs Comparable

- **Strings**
  - Remember that numbers sort before letters and uppercase letters sort before lowercase letters. Other than that according to the unicode character mapping.
- **Comparable**
  - Package: `java.lang`
  - Method name: **compareTo**
  - `public interface Comparable<T> { public int compareTo(T o);}`
    - A proper way would be to call the `String` class' `compareTo(String other)` method on a string member: `public int compareTo(Duck d) { return name.compareTo(d.name); }`
  - **Rules**
    - The number zero is returned when the current object is equal to the argument to `compareTo()`.
    - A number less than zero is returned when the current object is smaller than the argument to `compareTo()`.
    - A number greater than zero is returned when the current object is larger than the argument to `compareTo()`
  - You are strongly encouraged to make your `Comparable` classes consistent with equals because not all collection classes behave predictably if the `compareTo()` and `equals()` methods are not consistent.
- **Comparator**
  - Package: `java.util`
  - Method name: **compare**
  - Sometimes you want to sort an object that did not implement Comparable, or you want to sort objects in different ways at different times.
    ```java
    Comparator<Duck> byWeight = new Comparator<Duck>() {
      public int compare(Duck d1, Duck d2) { return d1.getWeight()—d2.getWeight();}
    };
    // or simply
    Comparator<Duck> byWeight = (d1, d2) -> d1.getWeight()—d2.getWeight();

    // Comparator chaining!
    public class ChainingComparator implements Comparator<Squirrel> {
       public int compare(Squirrel s1, Squirrel s2) {
          Comparator<Squirrel> c = Comparator.comparing(s -> s.getSpecies());
          c = c.thenComparingInt(s -> s.getWeight());
          return c.compare(s1, s2);
      }
    }
    ```

#### Searching and sorting

- The sort method uses the `compareTo()` method to sort. It expects the objects to be sorted to be `Comparable`.
- `Collections.sort(rabbits);` Doesn't compile unless the class is not comparable. Also you can't add such objects to Collections that rely on sorting data. (`TreeSet`)

  ```java
    Set<Rabbit> rabbit = new TreeSet<>(new Comparator<Rabbit>() {
    public int compare(Rabbit r1, Rabbit r2) {
        return r1.id = r2.id; }
    });
    rabbit.add(new Rabbit());
  ```

- As seen above you can provide a comparator externally and then add the objects to the collection.

#### Java 8 additions

- **Method references**
  - Method references are a way to make the code shorter by reducing some of the code that can be inferred and simply mentioning the name of the method.
  - Assume that the `Duck` class has a `static` method `compareByWeight`. Then instead of `Comparator<Duck> byWeight = (d1, d2) -> DuckHelper.compareByWeight(d1, d2);` you could write `Comparator<Duck> byWeight = DuckHelper::compareByWeight;`.
  - The `::` operator tells Java to pass the parameters automatically into compareByWeight.
  - `DuckHelper::compareByWeight` **returns a functional interface** and not an `int`. Remember that `::` is like lambdas, and it is typically used for deferred execution.
  - `Predicate<String> methodRef3 = String::isEmpty;` is another example.
- **`removeIf`**
  - `boolean removeIf(Predicate<? super E> filter)`
  - E.g.: `list.removeIf(s -> s.startsWith("A"));`
- **`replaceAll`**
  - `void replaceAll(UnaryOperator<E> o)`
  - E.g.: `list.replaceAll(x -> x*2);`
- **`forEach`**
  - `cats.forEach(c -> System.out.println(c));` or `cats.forEach(System.out::println);`
- **`merge`**
  - The `merge()` method allows adding logic to the problem of what to choose.
  - Line 18 calls this mapping function, and it sees that “Bus Tour” is longer than “Skyride,” so it leaves the value as “Bus Tour.” Line 19 calls this mapping function again. This time “Tram” is not longer than “Skyride,” so the map is updated.
    ```java
    BiFunction<String, String, String> mapper = (v1, v2)
      -> v1.length() > v2.length() ? v1: v2;
      Map<String, String> favorites = new HashMap<>();
    favorites.put("Jenny", "Bus Tour");
    favorites.put("Tom", "Tram");
    String jenny = favorites.merge("Jenny", "Skyride", mapper); // Bus Tour
    String tom = favorites.merge("Tom", "Skyride", mapper); // Sky ride
    ```
  - The mapping function is used only when there are two actual values to decide between. (and not `null`)

#### Learnings
- **Generics**
  - You can't assign a subclass container to a superclass container (`ArrayList<Number> a = new ArrayList<Integer>();`) --> not good! Use `<? extends Nuber>` for this purpose!
  - When you are **not** using generics, then you can add any object tot he list. However **when looping through** this collection you **must** use Object as in `for (Object o: list)`! (Compile time error!)
  - You can call a generic function `new Hello<String>("hi")` or `new Hello("there")`
  - If you are not using generics, assume that your collections are type of `Object`.
  - The following function signature can be called two ways. `public static <U extends Exception> void printException(U u)` Don't get confused just because of the unnecessary syntax.
    - `Main.<NullPointerException>printException(new NullPointerException ("D"));`
    - `Main.printException(new NullPointerException ("D"));`
  - Watch out for generics shadowing. (When the generic type in a method or class shadows another class.)
  - When using generic types in a `static` method, the generic specification goes before the return type.
- **Collections**
  - The `Map` interface doesn't have an `add` method! (It has `put`!)
  - The `Map`interface doesn't have a `contains()`method.
- **Rest**
  - Watch out for the `Comparator`implementations! In order to have ascending order the first argument has to come first. (`return int1 - int2;`)
  - `forEach` takes a `Consumer` parameter, which requires one parameter in the lambda body!
  - Make sure that the lambda body doesn't redefine variables of the outer scope!