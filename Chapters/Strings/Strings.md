# Strings

## Java
- Strings (java.lang.String) are pieces of text stored in your program. Strings are not a primitive data type in Java, however, they are very common in Java programs.
- Strings are **immutable** --> returning new objects if you want to modify --> Don't use `varString += "appendedString";` in loops, as you are creating new variables on each iteration.

#### String memory management
- Like many Java objects, all String instances are created on the heap, even literals. When the JVM finds a String literal that has no equivalent reference in the heap, the JVM creates a corresponding String instance on the heap and it also stores a reference to the newly created String instance in the String pool. Any other references to the same String literal are replaced with the previously created String instance in the heap.
- When we use double quotes to create a String, it first looks for String with same value in the String pool, if found it just returns the reference else it creates a new String in the pool and then returns the reference.
- However using `new` operator, we force String class to create a new String object in heap space. We can use `intern()` method to put it into the pool or refer to other String object from string pool having same value.
- The String pool itself is also created on the heap. (Before Java 7, String literals were stored in the runtime constant pool in the method area of PermGen, that had a fixed size.)
- The following prints true in all 4 cases!

```java
class Strings
{
    public static void main (String[] args)
    {
        String a = "alpha";
        String b = "alpha";
        String c = new String("alpha");

        //All three strings are equivalent
        System.out.println(a.equals(b) && b.equals(c));

        //Although only a and b reference the same heap object
        System.out.println(a == b);
        System.out.println(a != c);
        System.out.println(b != c);

        // changing b this way won't affect a
        b = "betha";
        // Since strings are immutable the b is
        // NEW with a NEW reference. (a!=b) now on!

    }
}
```

#### Comparing strings
- Use the String object's `equals` or `equalsIgnoreCase` methods.
  - **Never use ==, as it only returns true, if the strings are the SAME OBJECTS in the memory!**
  - Test `== null` with the `==` operator.
- Switch statements: As of Java 1.7, it is possible to compare a String variable to literals in a switch statement. Make sure that the String is not `null`, otherwise it will always throw a `NullPointerException`. Values are compared using String.equals, i.e. case sensitive.
- Comparing vs constant values:
  - **When comparing a String to a constant value, you can put the constant value on the left side of equals to ensure that you won't get a NullPointerException if the other String is null.** --> `"baz".equals(foo)`
  - A more readable alternative is to use Objects.equals(), which does a null check on both parameters: Objects.equals(foo, "baz").
- You can `intern` a string, meaning that you create a new reference to it: `String internedStr = strObj.intern();` Since Java 7 the internalized strings do NOT belong anymore to the Permanent Generation area of the JVM, thus it is more safe to use (wrt. overflow). [Link](https://stackoverflow.com/questions/10578984/what-is-string-interning)

#### Finding sub-strings
- `indexOf(str)`, `lastIndexOf(str)`, `indexOf(str, fromIndex)`, lastIndexOf(str, fromIndex)
- `.contains(str)` (Case sensitive)
- `.substring(intFromChar)`, `.substring(intFrom, intTo)`
  - From JDK 7u6 the `substring` method always copies the entire underlying `char[]` array, making the complexity linear compared to the previous constant one but guaranteeing the absence of memory leaks at the same time.

#### Concatenation
- Concatenation with "+"
- int + string --> OK, int implicitly converted to string.
- Use `string.join(", ","Peter", "Mike",..., "Joe")` --> concatenate strings with ", "

#### Splitting
- Use `string.substring(from,to)` to get the char sequence between from and to positions.
- `public String[] split(String regex)`
  - E.g.: `coordinates.split(";")`
  - `String[] words = lineFromInput.split("\\s+"); // one or more space chars`
- The following characters are considered special (aka meta-characters) in regex:
  - `  < > - = ! ( ) [ ] { } \ ^ $ | ? * + . `
  - To split a string based on one of the above delimiters, you need to either escape them using `\\` or use `Pattern.quote()`
  - `String regex = Pattern.quote("|"); String[] arr = s.split(regex);`
- Or you can use the `Stringtokenizer` that splits on `\t \n \r \f` by default.

#### Converting
- `Integer.toString(n)` - to convert to string
- `Integer.parseInt(str)` - to convert to int
  - Double.toString, Double.parseDouble. Same with floats.

#### Joining
- `.join()` --> `String singleString = String.join(" + ", stringArrayVar);`
- `StringJoiner` class: `StringJoiner sj = new StringJoiner(", ", "[", "]"); sj.add("foo"); sj.add("bar"); sj.add("foobar"); System.out.println(sj); // Prints "[foo, bar, foobar]"`

#### Overriding toString() method of a class
```java
@Override
public String toString() {
    return "My name is " + this.name + " and my age is " + this.age;
}

// Now you can write:
System.out.println(person);
```

#### Additional methods worth knowing
- `startsWith(str)`, `endsWith(str)`, `contains(str)`
- `replace(oldSeq, newSeq)`, `replaceAll(regex, replacement)`, `replaceFirst(String regex, String replacement) `
- `toUpperCase()`, `toLowerCase()`
- `trim()` (removing all leading and trailing whitespaces)
- Use `System.out.printf("Hello %s.", var);` for string fromatting.
- Length: `str.length()`
- `string.charAt(n)`7

#### Interview questions
**Difference Between String , StringBuilder And StringBuffer Classes**
- `String`s are **immutable** and stored in the Constant String Pool. Since immutable it is threadsafe.
- `StringBuffer` is **mutable** means one can change the value of the object. The object created through `StringBuffer` is stored in the heap. `StringBuffer`  **has the same methods as** the `StringBuilder`, but each method in `StringBuffer` **is synchronized** that is StringBuffer is thread safe.
  - `StringBuffer` can be converted to the string by using
`toString()` method.
- `StringBuilder` is **not thread safe**, hence faster.

**String length without using .length()**
- Use a for loop and try accessing the current char with the `charAt(i)` method. When throwing exception catch it, and return the counter.

```java
static int i,c,res;
static int length(String s)
{
    try
    {
        for(i=0,c=0;0<=i;i++,c++)
        s.charAt(i);
    }
    catch(Exception e)
    //Array index out of bounds and array index out of range are different exceptions
    {
        System.out.print("length is ");
        // we can not put return  statement in catch
    }
    return c;
}
```

**Recursive palindrome checking**
```java
public static boolean isPalindrome(String str)
{
    return isPalindrome(str,0,str.length()-1);

}
public static boolean isPalindrome(String str,int low, int high)
{  if(high <= low)
    return true;
  else if (str.charAt(low)!= str.charAt(high))
    return false;
  else
    return isPalindrome(str,low+1,high-1);    
}
```
