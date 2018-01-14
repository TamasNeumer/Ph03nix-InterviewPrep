#### Sorting
- ... `implements Comparable<CLASSNAME>` --> Don't forget the CLASSNAME!

- Converting int[] array to list<Integer>
  - Since `Arrays.asList()` doesn't deal with Autoboxing you have to do it manually using the ``intList.add(ints[index]);``
  - You could use Integer[] and then it would work.

- Reading lines from file into Stream:

  ```
  try(Stream<String> stream = Files.lines(Paths.get("..."))){
  stream.forEach(s -> {/*...*/});
  } catch(Excepcion e) {/*...*/};
  ```

- Initializing int arrays:
  - `int[] myArray = new int[5];`
  - `int[] myArray = {1,2,3};`
  - `int[] myArray = new int[]{1,2,3};`
