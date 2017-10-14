# Learnings

## Cpp
- `int main(int argc, char* argv[])` is the correct line!
- Compile using the `g++ -Wall file.cpp -o File`
  - `-Wall` means that all warnings will be shown.

## Java
- Java doesn't represent boolean values with numbers, thus if(1){...} is wrong!
  - It's **boolean** and **not** bool.
- The bitwise "AND" is "&" (as in C++)
- Java doesn't allow to create ArrayList<E> of primitive types...
  - ArrayList can only reference types, not primitives. Integer is a class, not a primitive, thus you should use Integer instead of int.
    - int -> Integer: `Integer.valueOf(i)`
    - Integer -> int: `i.intValue()`
    - This conversion process is **automatic**, and is called **Autoboxing** and **Unboxing**
- It's not easy to convert int[] to ArrayList<Integer>...
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
  - It accepts a lambda, and the elements where it's value is false are "removed".
  - Collect the stream `toArray()` that is accepts a generator: `<A> A[] toArray(IntFunction<A[]> generator)`
```java
return Arrays.stream(inputArray).filter(currentString ->
                currentString.length() == inputArray[inputArray.length - 1].length()).toArray(String[]::new);
```

## Recap
- 07 Intro
