# Data Structures and Java Collections

## Iterable interface
**Iterators**
- One of the root interfaces of the Java collection classes
- Implementing this interface allows an object to be the target of the enhanced for statement (sometimes called the "for-each loop" statement). (Since 1.8 it also allows you to call `forEach​(Consumer<? super T> action)`)

**Fail-fast vs fail-safe**
- Important when dealing with concurrent approaches.
- **Fail-fast iterator** while iterating through the collection, instantly throws `ConcurrentModificationException` if there is structural modification  of the collection. Thus, in the face of concurrent modification, the iterator fails quickly and cleanly, rather than risking arbitrary, non-deterministic behavior at an undetermined time in the future.
  - **In Single threaded environment**: After the creation of the iterator, structure is modified at any time by any method other than iterator's own remove method.
  - **In multi threaded environment**: If one thread is modifying the structure of the collection while other thread is iterating over it.
  - Don't write code that depends on the fail-fast iterator's exception, since the behavior is **never guaranteed**, only throws on a best-effort basis".
  - Detecting change is done via a "mod flag" that is checked before every operation on the iterator.
- **Fail-safe iterator**: makes copy of the internal data structure (object array) and iterates over the copied data structure. So, original data structure remains  structurally unchanged. Hence, no `ConcurrentModificationException` throws by the fail safe iterator.
  - Overhead of maintaining the copied data structure i.e memory.
  - Fail safe iterator does not guarantee that the data being read is the data currently in the original data structure.


| Fail Fast  |  Fail Safe |
|--|---|--|
|Throw ConcurrentModification Exception  | Yes  |  No |
|Clone object  | No  | Yes |
|Memory Overhead  | No  | Yes |
|Examples  | HashMap, Vector, ArrayList, HashSet  | ConcurrentHashMap, CopyOnWriteArrayList |

**Iterator vs Enumerator**
- **Iterator** has 3 methods:
  - `hasNext()`, `next()`, `remove()`
  - Newer and more efficient.
  - Has remove method.
  - Iterator is a **fail-fast** in nature.
  - According to Java API Docs, Iterator is always preferred over the Enumeration.

- **Enumeration** has 2 methods:
  - `hasMoreElements()`, `nextElement()`
  - Enumeration is a legacy interface used to traverse only the legacy classes like Vector, HashTable and Stack.
  - Enumeration is **fail-safe**.

## Collection
- Useful lists for hierarchy:
  - [1](http://www.falkhausen.de/Java-8/java.util/Collection-Hierarchy.html)
  - [2](http://wiki3.cosc.canterbury.ac.nz/index.php/User:Jenny_Harlow/Design_study/Java_Collections_Framework)

![](res/collection_overview.png)

- The Collection interface just defines a set of methods (behaviour) that each of these Collection subtypes share.
- Note that Maps are not part of the collection interface!
- Do not confuse the Collection interface with the CollectionS utility class.

**Methods to know**
- `add​(E e)` / `addAll​(Collection<? extends E> c)`
- `clear​()`
- `contains​(Object o)` / `containsAll​(Collection<?> c)`
- `equals​(Object o)`
- `hashCode​()`
- `isEmpty​()`
- `iterator()` (inherited)
- `remove(Object o)` / `removeAll​(Collection<?> c)`
- `removeIf​(Predicate<? super E> filter)`
- `size()`
- `stream()`
- `toArray​()`


## Lists and Arrays
**Arrays**
- Fixed in size --> size has to be determined in advance at construction!
- You can access the element of an array directly by its index value. This is known as random access.
- The static `arrayCopy` method on the `System` object enables you to copy all or part of the array to a new array.
- Big O:
  - `append(item)` **O(n)**
  - `delete(index)` **O(n)**
  - `[index]` **O(1)**

```java
final int[] integers = new int[3];
final boolean[] bools = {false, true, true, false};
final String[] strings = new String[]{"one", "two"}

// Copying arrays.
int[] integers = {0, 1, 2, 3, 4};
int[] newIntegersArray = new int[integers.length + 1];
System.arraycopy(integers, 0, newIntegersArray, 0, integers.length);
integers = newIntegersArray;
integers[5] = 5;
```

**Lists**
- Lists are sequential, ordered collections of values of a certain type. In Java, you will generally work with either `LinkedList`s or `ArrayList`s.
- Lists are **unbounded**.
- Whenever you are working with a list, you should always **work to the List interface** where possible.
  - For example, seeding tests with dummy code from the `asList` method on the `Arrays` utility class in the standard Java library returns a list, compliant to the List interface, from the given parameters.

- **List Interface**
  - Finding elements by index or by value:
    - `get​(int index)`
    - `indexOf​(Object o)`
    - `lastIndexOf​(Object o)`
      - Searching for objects is linear in time - O(n)
  - `listIterator​()` - iterator that allows element insertion and replacement, and bidirectional access
  - `add​(int index, E element)` / `remove​(int index)` adding and removing can be done with indexes.
  - `sort​(Comparator<? super E> c)`

- **ArrayLists**
  - Uses array internally to store data
  - If you specify no value, the initial size is ten.
  - Memory space automatically extended, but expensive when dealing with large lists. Thus either allocate a large array in advance if you know you will add/remove much, or use linked lists.
  - Size reallocation is *one way* meaning that array size won't shrink, when the space is not used!

- **Linked Lists**
  - The LinkedList instance contains a reference to the head of the list, represented as an Element . The Element inner class is a recursive data type, with the next field pointing to the next element in the list. This enables you to traverse the list easily, visiting and processing each element in order.
  - Getting element by index takes longer, however inserting is fast. Also when deleting nodes, the "space" shrinks (unlike ArrayLists)

  ```java
  public class SimpleLinkedList<E> {
    private static class Element<E> {
      E value;
      Element<E> next;
    }
    private Element<E> head;
  }
  ```

- **Queue / Dequeue**
  - A Queue is a Java interface that represents a “first in, first out” data structure.
  - Methods:
    - `add`
    - `peek`
    - `remove`
  - A Deque (pronounced “deck”) is an extension of Queue , and allows addition and removal from either end of the data structure.

- **Vectors**
  - Main differences vs ArrayList:
    - Vector is synchronized, while ArrayList is NOT!
    - A Vector defaults to doubling the size of its array, while the ArrayList increases its array size by 50 percent.
    - ArrayList is newer and 20-30% faster.

## Trees
**Trees**
- A tree is a data structure in which an element can be succeeded by a number of different elements, known as *children*.
- A common use of the tree data structure is a *binary tree*, in which each element has a maximum of two children.
- One such implementation of a binary tree is a *binary search* tree, where elements “less than” the value
of a given node are children on the left, and elements “greater than” the value of a given node are children on the right.

```java
public class SimpleTree<E extends Comparable> {
  private E value;
  private SimpleTree<E> left;
  private SimpleTree<E> right;
  ... // constructors, getters and setters omitted
}

public boolean search(final E toFind) {
  if (toFind.equals(value)) {
    return true;
  }
  if (toFind.compareTo(value) < 0 && left != null) {
    return left.search(toFind);
  }
  return right != null && right.search(toFind);
}

public void insert(final E toInsert) {
  if (toInsert.compareTo(value) < 0) {
    if (left == null) {
      left = new SimpleTree<>(toInsert, null, null);
  } else {
    left.insert(toInsert);
  }
  } else {
    if (right == null) {
      right = new SimpleTree<>(toInsert, null, null);
    } else {
      right.insert(toInsert);
    }
  }
}
```

**Balancing trees**
- When just simply adding elements to a tree like one above you can end up with a linked list in practice. --> Balancing the tree is quite important if you want to search it regularly.
- A specific implementation of a binary search tree, called an *AVL Tree*, enforces that, for any node, the difference in depth for each child is at most one. (= a node cannot have a chain of 2 children on left, while no child on the right.)
- When a tree is balanced, searching, inserting, and deleting has the size **O(log n)**.
- Another application of a binary tree is called
a *Binary Heap*, which is a balanced tree with the property that children are “greater than” their parent.
  - Heaps are especially useful for priority queues, or any time you require quick access to the smallest element of a collection.
  - To insert elements into a heap, you start by adding the element at the next available position in the lowest level of the tree. You then compare the inserted value with its parent, and if the new value is lower, the two values swap.

## Maps
**Maps**
- A map, sometimes called a hash, associative array or dictionary, is a key-value store. Elements within the data structure can be queried by the key, which will return the associated value.
- The Map interface
  is part of the Java Collections API, but, unlike List , it **does not implement the Collection interface**.
  - Similar to the List interface, it specifies most common operations for map implementations, such as the data structure size and the ability to read, insert, and delete key-value pairs.

**Map methods**
- `containsKey​(Object key)` / `containsValue​(Object value)`
- `entrySet​()` return a Set view on the map
- `keySet​()` Returns a Set view of the keys contained in this map.
- `put​(K key, V value)` / `putAll​(Map<? extends K,? extends V> m)`
- `remove​(Object key)` / `remove​(Object key, Object value)`
- `values()` Returns a Collection view of the values contained in this map.

**HashMap**
- **Hashing**
  - In the Java programming language, every class implicitly or explicitly provides a hashCode() method, which digests the data stored in an instance of the class into a single hash value (a 32-bit signed integer).
  - Contract of hashing:
    - Given object must consistently report the same hash value
    - If two objects are equal according to the equals(Object) method, then calling the hashCode method on each of the two objects must produce the same integer result.
    - It is **not** required that if two objects are unequal according to the equals(java.lang.Object) method, then calling the hashCode method on each of the two objects must produce distinct integer results. However, the programmer should be aware that producing distinct integer results for unequal objects may improve the performance of hashtables.


- **Internal implementation**
  - Imagine HashMap as an array. Each array element is a **"bucket"** that is used to store `Element`s. Two or more Elements can have the same bucket (if their hashed values are the same). In that case link list structure is used to connect the nodes (via their `next`). Buckets are different in capacity.
  - A single bucket can have more than one nodes, it depends on hashCode() method. The better your hashCode() method is, the better your buckets will be utilized.

    ```java
    static class Entry<K,V> implements Map.Entry<K,V>
     {
         final K key;
         V value;
         Entry<K,V> next;
         final int hash;
         ........
     }
    ```

  - The code implementation of the `put()` method looks like this:

    ```java
    public V put(K key, V value)
    {
        if (key == null)
           return putForNullKey(value);
        int hash = hash(key.hashCode());
        int i = indexFor(hash, table.length);
        for (Entry<K,V> e = table[i]; e != null; e = e.next)
        {
            Object k;
            if (e.hash == hash && ((k = e.key) == key || key.equals(k)))
             {
                 V oldValue = e.value;
                 e.value = value;
                 e.recordAccess(this);
                 return oldValue;
              }
         }
         modCount++;
         addEntry(hash, key, value, i);
         return null;
     }
    ```

    - First, it checks if the key given is null or not. If the given key is null, it will be stored in the zero position, as the hashcode of null will be zero.
    - Otherwise the object's `.hashCode()` is called. In order to get the value within the limits of an array, the hash(key.hashCode()) is called, which performs some shifting operations on the hashcode. (Precisely: hash = key.hashCode % bucketSize, so the `hash()` function basically applies modulo with bucketSize. The value will be stored in that bucket.) --> [GoodDescription](http://www.geeksforgeeks.org/internal-working-of-hashmap-java/)
    - The `indexFor()` method is used to get the exact location to store the Entry object.
    - Then comes the most important part: What happens if two different object has the same hashcode? ( eg : Aa,BB will have the same hashcode) They will be stored in the same bucket. To handle this let's think of the LinkedList in data structure which has the next attribute, that will always point to the next object. The same way the next attribute in the Entry class points to the next object. Using this method, the different objects with the same hashcode will be placed next to each other.
    - In the case of the Collision, the HashMap checks for the value of the next attribute if it is null it inserts the Entry object in that location, if next attribute is not null then it keeps the loop running till next attribute is null then stores the Entry object there.
    - **Preventing duplicates**
      - **HashMap don’t allow duplicate keys, but allows duplicate values. HashMap allows null key also but only once and multiple null values.**
      - All the Entry Objects in the LinkedList will have the same hashcode, but HashMap uses equals() . This method checks the equality, so if key.equals(k) is true, then it will replace the value object inside the Entry class and not the key. So this way it prevents the duplicate key from being inserted.
    - Getting the value is similar to put (hashing, hashing and then iterating until e.key = searchedKey, then returning value.)

- **Performance**
  - An instance of HashMap has two parameters that affect its performance: initial capacity and load factor.
    - **Capacity:** The capacity is the number of buckets in the hash table. (By default 16)
    - **Load factor;** The load factor is a measure of how full the hash table is allowed to get before its capacity is automatically increased. When the number of entries in the hash table exceeds the product of the load factor and the current capacity, the hash table is rehashed (that is, internal data structures are rebuilt) so that the hash table has approximately twice the number of buckets.
    - Default initial capacity of the HashMap takes is 16 and load factor is 0.75f (i.e 75% of current map size). The load factor represents at what level the HashMap capacity should be doubled. For example product of capacity and load factor as 16 * 0.75 = 12. This represents that after storing the 12th key – value pair into the HashMap , its capacity becomes 32. Hence higher load factors  decrease the space overhead but increase the lookup cost.

**TreeMap**
- An alternative Map implementation is the TreeMap, which uses a binary tree data structure to conform to the Map interface.
- Each node in the tree is a key-value pair.
- Each element put into the TreeMap rebalances the tree, so that searching, deletion, and further insertion can always be performed as efficiently as possible: **O(log n)**.
- One main difference between a TreeMap and a HashMap is that, with a TreeMap the order of the keys is preserved when iterating over the whole collection, because the **collection is stored in order**. TreeMap will iterate according to the "natural ordering" of the keys according to their compareTo() method (or an externally supplied Comparator).

**LinkedHashMap**
- This implementation works in the same way as a HashMap, so element retrieval will be O(1) , but it has the added property that iterating over the keys will be in the same order as insertion.

**ConcurrentHashMap**
- You should use this implementation if you ever want to share the map instance with many threads. It is thread safe, and has been specifically designed to be able to return read values while the values are being written to the map.

**WeakhashMap**
- Use it if you are working with large objects and you want them to get cleaned up from the map.
- This data structure cooperates with the garbage collector to remove key/value pairs when the only reference to the key is the one from the hash table entry.

## Sets  
**Sets**
- A set is an unordered collection of objects that does not contain any duplicates.

**HashSet**
- This implementation uses an underlying HashMap, storing the value as the key to the map, and the value is a marker object, signifying that a value is stored.
- For each of the Map implementations visited earlier, there is an equivalent Set implementation: `HashSet`, `TreeSet`, and `LinkedHashSet` — although there is no Set backed by a ConcurrentHashMap.

**Implementation**
-  Uniqueness in Set is achieved through a HashMap in Java. Whenever you create an object of HashSet it will create an object of HashMap as you can see:

```java
public class HashSet<E>
extends AbstractSet<E>
implements Set<E>, Cloneable, java.io.Serializable

{
    private transient HashMap<E,Object> map;
    private static final Object PRESENT = new Object();

    public HashSet() {
        map = new HashMap<>();
    }

    public boolean add(E e) {
        return map.put(e, PRESENT)==null;
    }
    /* Other Stuff*/
}
```

- As we know in HashMap each key is unique . So what we do in the set is that we pass the argument in the add(Elemene E) that is E as a key in the HashMap.
- Note that HashMap's `put` returns the previous value associated with key, or null if there was no mapping for key. So , in HashSet add() method ,  we check the return value of map.put(key,value) method with null value, if so we return true, if not we return false.

**HashTable vs  (interview question)**
- HashTable is synchronized --> use HashMap in single threaded environment
- HashTable does NOT allow null keys, meanwhile HashMap does!
- HashTable uses Enumerator (so does Vector and they are the only 2 btw.). HashMap uses Iterator.
- HashTable's enumerators are NOT fail fast!
- HashTable is slower and uses more resources
- HashTable

**Treeset**
- You use a TreeSet if you want to traverse the set in sorted order.
- The element type of the set must implement the Comparable interface, or you need to supply a Comparator in the constructor.

```java
countries = new TreeSet<>((u, v) ->
u.equals(v) ? 0
: u.equals("USA") ? -1
: v.equals("USA") ? 1
: u.compareTo(v));
```

## Other collections
#### Views
- A collection view is a lightweight object that implements a collection interface, but doesn't store elements.
- You can get views of the keys, values, and entries of a map by calling these methods:
  - `Set<K> keySet()`, `Set<Map.Entry<K, V>> entrySet()`, `Collection<V> values()`
- The collections that are returned are not copies of the map data, but they are connected to the map. If you remove a key or entry from the view, then the entry is also removed from the underlying map.
- Another example is the `Arrays.asList(a)` method.

#### BitSets
- The BitSet class stores a sequence of bits. A bit set packs bits into an array of long values, so it is more efficient to use a bit set than an array of boolean values. Bit sets are **useful for sequences of flag bits or to represent sets of non-negative integers**, where the ith bit is 1 to indicate that i is contained in the set.
- The BitSet class gives you convenient methods for getting and setting individual bits.
- `set(index)` / `clean(index)` / `flip(index)`

#### Enumeration sets
- If you collect sets of enumerated values, use the EnumSet.
- You can use the methods of the Set interface to work with an EnumSet.

```java
enum Weekday { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY };
```

#### Properties
- The Properties class implements a map that can be easily saved and loaded using a plain text format. Such maps are commonly used for storing configuration options for programs.
- With the properties class you can also access platform directories (user.home), os.name, os.version etc.

```java
Properties settings = new Properties();
  settings.put("width", "200");
  settings.put("title", "Hello, World!");
  try (OutputStream out = Files.newOutputStream(path)) {
    settings.store(out, "Program Properties");
}

try (InputStream in = Files.newInputStream(path)) {
  settings.load(in);
}

String title = settings.getProperty("title", "New Document");
// To load:
```
