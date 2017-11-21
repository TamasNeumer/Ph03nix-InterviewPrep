# Learnings

## Cpp
- `int main(int argc, char* argv[])` is the correct line!
- Compile using the `g++ -Wall file.cpp -o File`
  - `-Wall` means that all warnings will be shown.
- Using cpp11:
  - g++ -std=c++11 your_file.cpp -o your_program

## Java
#### Creating and compiling java from scratch
- Filename: `ClassName.java`
- public `ClassName` --> important, name has to be the same + public!
- public static void `main` --> with small M
- javac ClassName.java
- java -cp . Composite


- Java doesn't represent boolean values with numbers, thus if(1){...} is wrong!
  - It's **boolean** and **not** bool.
- The bitwise "AND" is "&" (as in C++)
- Java doesn't allow to create ArrayList<E> of primitive types...
  - ArrayList can only reference types, not primitives. Integer is a class, not a primitive, thus you should use Integer instead of int.
    - int -> Integer: `Integer.valueOf(i)`
    - Integer -> int: `i.intValue()`
    - This conversion process is **automatic**, and is called **Autoboxing** and **Unboxing**
- It's not easy to convert int[] to List<Integer>...
  - `List<Integer> list = Arrays.stream(ints).boxed().collect(Collectors.toList());`
- To replace elements in int[] you convert it to stream, and apply a map function with a lambda :-)

  ```java
  int[] arrayReplace(int[] inputArray, int elemToReplace, int substitutionElem) {
      return Arrays.stream(inputArray)
      .map(o -> o == elemToReplace ? substitutionElem : o)
         .toArray();
  }
  ```

  - String class has no `reverse`. However! **StringBuilder:** A mutable sequence of *characters*.
    - `new StringBuilder(myStringVar).reverse().toString()`

- **Anonymous arrays:** `new int[]{3, 6, -2, -5, 7, 3}`
- There is no such thing as `!<`... Gettin tired?
- You can break the outerloop in nested scenario if you add a label to the outerloop e.g.:

```java
public class _08_matrixElementsSum {
    int matrixElementsSum(int[][] matrix) {
        int sum = 0;

        outerloop:
        for (int column = 0; column < matrix[0].length; column++)
        {
            for(int row = 0; row < matrix.length; row++){
                if(matrix[row][column] == 0) continue outerloop;
                sum += matrix[row][column];
            }
        }
        return sum;
    }
}
```

- Filtering arrays:
  - Use streaming, that has a filter() method.
  - It accepts a lambda, and the elements where its value is false are "removed".
  - Collect the stream `toArray()` that is accepts a generator: `<A> A[] toArray(IntFunction<A[]> generator)`
```java
return Arrays.stream(inputArray).filter(currentString ->
                currentString.length() == inputArray[inputArray.length - 1].length()).toArray(String[]::new);
```

- Char shifting
  - `String(char[] value, int offset, int count)`
    - Allocates a new String that contains characters from a subarray of the character array argument.
  - `CharSequence` inteface's `IntStream chars()`
    - Returns a stream of int zero-extending the char values from this sequence.
  - `currentChar - 'a' + 1` shifts the value by one
  - `% 26 ` makes sure that a "z" will be transformed back to "a"
```java
return new String(inputString.chars().map(
                currentChar -> (currentChar - 'a' + 1) % 26 + 'a').toArray(), 0, inputString.length());
```

Math & Logs
- LOGb(n) = LOGe(n) / LOGe(b)

- Java automatically widens int -> double in a division of one of them is double. (100/15.0) --> automatically double result, no need to typecast the 100.

#### Important
**If you are only aiming "to pass the test" you don't think of corner cases as much as you should. Whenever writing a code (i.e. loop ranges, breaking vs not breaking out) think of corner cases istead of hoping that the "current fix" will make the tests pass...**


- Remove each k-th elements
  - `IntStream.range(a,b)` Returns a sequential ordered IntStream from startInclusive (inclusive) to endExclusive (exclusive) by an incremental step of 1. (This will represent our indexes)
  - The list of indexes are filtered, so that only indexes ! %k ==0 remain.
  - Then for each index you take inputArray[i] and put it into an Array.

  ```java
  return IntStream.range(0, inputArray.length).filter(i -> ((i+1) % k) != 0).map(i -> inputArray[i]).toArray();
  ```

- Using back-references in regexp:
  - `inputString.replaceAll("^(\\d*).*", "$1");`
    - "$1" refers to the first back-reference (last mached), $2 would refer to the previously matched etc.
    - If the string starts with a number, the number is captured in the first capture group. Basically then you match the whole string and replace it with the content of the first capturing group. HOWEVER if it starts with another character, then it's a full match and the 1st capturing group is empty, thus you replace the entire string with an empty string.

- Max value of array:
  - `double max = Arrays.stream(array).max().getAsInt();`

- String vs StringBuffer
  - String is immutable, if you try to alter their values, another object gets created, whereas StringBuffer and StringBuilder are mutable so they can change their values.
  - In a loop where you want to add/append use StringBuilder / StringBuffer.

 - `return Arrays.stream(text.split("\\W+")).max((a,b) -> a.length() - b.length()).get();`


#### CORE
- IntStream is a very nice class!
  - `average()` --> returns average
  - `boxed()` --> returns a `Stream<Integer>`.
    - `IntStream i = IntStream.of(1,2,3,4,5,6,7);`
    - `Stream<Integer> o =  i.boxed();`
  - `count()` count of elements in IntStream
  - `distinct()` --> removes duplicate elements and returns IntStream
  - `filter(IntPredicate predicate)` --> Stream that matches predicate
  - `forEach()` --> Performs an action for each element of this stream. (Prallel!)
  - `forEachOrdered(IntConsumer action)` --> forEach in order
  - `max`/`min`
  - `range(int startInclusive, int endExclusive)` --> returns IntStream range
    - **Trick:** `IntStream.range(0,3).forEachOrdered(n -> {...})` Nice for loop on the fly! :-)

- String -> int `Integer.parseInt(str_number)`
- int -> String `Integer.toString(int_number);` or `String.valueOf(int_number)`

- Arrays
  - `Arrays.stream(arr)` --> returns a Stream (IntStream etc). If you want to print use `Arrays.toString(arr)`
  - `Arrays.fill(arr, value)` --> enables you to fill up your array with values.
