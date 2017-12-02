## Java arrays
#### Creating and initializing arrays

```Java
int[]   numbers1 = new int[3];                 // Array for 3 int values, default value is 0
int[]   numbers2 = { 1, 2, 3 };                // Array literal of 3 int values
int[]   numbers3 = new int[] { 1, 2, 3 };      // Array of 3 int values initialized
int[][] numbers4 = { { 1, 2 }, { 3, 4, 5 } };  // Jagged array literal
int[][] numbers5 = new int[5][];               // Jagged array, one dimension 5 long
int[][] numbers6 = new int[5][4];              // Multidimensional array: 5x4
```
- An array is a data structure that holds a fixed number of primitive values or references to object instances.
- The **size** of an array is **fixed** at runtime when initialized. It cannot be changed after initialization. If the size must be mutable at runtime, a Collection class such as ArrayList should be used instead.
  - `array = { 1, 2, 3, 4 };` --> Error! `array = new int[] { 4, 5, 6 };` --> OK.
- When declaring arrays using the new Type[length] constructor, each element will be initialized with the following default values:
  - 0 for primitive numerical types: byte, short, int, long, float, and double.
  - '\u0000' (null character) for the char type.
  - false for the boolean type.
  - null for reference types.
- Multi dimensional arrays: inner arrays do not need to be of the same length, or even defined: `int[][] a = { {1}, {2, 3}, null };`
- Generic arrays are not allowed (`private T[] a;`)

#### Methods and Properties
- `arrayVar.length` returns the actual size of the array and not the number of array elements which were assigned a value.
- `arrayListVar.size()` returns the number of array elements which were assigned a value.
- `Arrays.fill()`
  - `Arrays.fill(arrayVar, "abc");` fill an array with the same value after initialization after initialization
  - `Arrays.fill(array8, 1, 2, "aaa");`  // Placing "aaa" from index 1 to 2.
- `Arrays.setAll(T[] array, IntFunction<? extends T> generator)` & `Arrays.parallelSetAll()`
  - `Arrays.setAll(array, i -> i);` The array becomes { 0, 1, 2, 3, 4 }.
- `Arrays.asList()` create a list from the array.
  - `List<String> stringList = Arrays.asList(stringArray);`
  - This method returns List, which is an instance of Arrays$ArrayList(static inner class of Arrays) and not java.util.ArrayList.
  - This list is backed by (a view of) the original array, meaning that any changes to the list will change the array and vice versa. However, changes to the list that would change its size (and hence the array length) will throw an exception.
  - To create a copy of the list that can grow in size:
    - `List<String> stringList = new ArrayList<String>(Arrays.asList(stringArray));`
    - `List<String> stringList = new ArrayList<>(Arrays.asList(stringArray));` (Java SE7+ Diamond notation --> Type inferrence)
  - This **doesn't work for POD types**. In order to convert a primitive array to a List, first of all, convert the primitive array to an array of the corresponding wrapper type (i.e. call Arrays.asList on an Integer[] instead of an int[])

    ```java
    int[] arr = {1, 2, 3};
    System.out.println(Arrays.asList(arr).contains(1)); //--> False
    Integer[] arr2 = {1, 2, 3};
    System.out.println(Arrays.asList(arr2).contains(1)); //--> True
    ```
    - Explanation: Autoboxing only happens for a single element, not for arrays of primitives. When you pass an array of primitives (int[] in your case) to Arrays.asList, it creates a List<int[]> with a single element - the array itself. Therefore contains(3) returns false. contains(array) would return true.

    - Note that every primitive type in Java has an equivalent wrapper class: class:
      - byte has Byte
      - short has Short
      - int has Integer
      - long has Long
      - boolean has Boolean
      - char has Character
      - float has Float
      - double has Double
  - Converting ArrayList<String> to 'String[]' in Java:
    - `String[] strings = list.stream().toArray(String[]::new);`
- `Arrays.stream(arr);`
  - Such streams are list-backed iterables.
  - Converting an array of primitives to Stream using Arrays.stream() will transform the array to a primitive specialization of Stream:
  ```java
  Stream<Integer> intStream = Stream.of(1, 2, 3);
  Stream<String> stringStream = Stream.of("1", "2", "3");
  Stream<Double> doubleStream = Stream.of(new Double[]{1.0, 2.0});
  ```
- `Arrays.sort(arrayVarName);`
  - `Arrays.sort(names, 0, names.length, Collections.reverseOrder());`
  - Sorting an array of objects --> all elements must implement either `Comparable` or `Comparator` interface
  - Furthermore, they must be mutually comparable as well, for example `e1.compareTo(e2)` must not throw a `ClassCastException` for any elements e1 and e2 in the array.
- Find elements
  - `Arrays.binarySearch(arrayVar, "ValToSearch");` --> sorted array only!
  - `Arrays.asList(arrayVar).indexOf("A");`  --> NON POD!
- Contains element
  - `boolean isPresent = Arrays.asList(strings).contains("A");`
- Compare two Arrays
  - `Arrays.deepEquals(aObject, bObject);`
- Copy (deep)
  - Java works with references wrt. objects: `int[] numbers = primes` --> now you have two vars pointing to the same object in memory!
  - `int[] b = a.clone();` (Since arrays are Objects in Java, you can use Object.clone().)
  - `int[] b = Arrays.copyOf(a, a.length);`
