# Strings

#### Java
- Strings are **immutable** --> returning new objects.
- Concatenation with "+"
- int + string --> OK, int implicitly converted to string.
- Use `string.join(", ","Peter", "Mike",..., "Joe")` --> concatenate strings with ", "
- Split & Substring
  - Use `string.substring(from,to)` to get the char sequence between from and to positions.
  - Or you can split at a character string.split(", ") resulting in a String[].
- `string.equals(otherString)` --> check for equality
  - **Never use ==, as it only returns true, if the strings are the SAME OBJECTS in the memory!**
  - Test == null with the `==` operator.
  - When comparing put the string first --> "London".equals(varLocation) as it works even if varLocation is null.
  - `string.equalsIgnoreCase()` --> compare without regard to letter case.
- `string.compareTo(second)` --> compare them alphabetically. Returns positive int if first comes after second. Negative otherwise.
- `Integer.toString(n)` - to convert to string
- `Integer.parseInt(str)` - to convert to int
  - Double.toString, Double.parseDouble. Same with floats.
- There are a lot of other methods
  - `startsWith(str)`, `endsWith(str)`, `contains(str)`
  - `indexOf(str)`, `lastIndexOf(str)`, `indexOf(str, fromIndex)`, lastIndexOf(str, fromIndex)
  - `replace(oldSeq, newSeq)`
  - `toUpperCase()`, `toLowerCase()`
  - `trim()` (removing all leading and trailing whitespaces)

- Use `System.out.printf("Hello %s.", var);` for string fromatting.
