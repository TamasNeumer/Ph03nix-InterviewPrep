# Data Structures

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
- Each element put into the TreeMap rebalances the tree, so that searching, deletion, and further insertion can always be performed as efficiently as possible: O(log n).
- One main difference between a TreeMap and a HashMap is that, with a TreeMap the order of the keys is preserved when iterating over the whole collection, because the **collection is stored in order**.

**LinkedHashMap**
- This implementation works in the same way as a HashMap, so element retrieval will be O(1) , but it has the added property that iterating over the keys will be in the same order as insertion

**ConcurrentHashMap**
- You should use this implementation if you ever want to share the map instance with many threads. It is thread safe, and has been specifically designed to be able to return read values while the values are being written to the map.

## Sets  
**Sets**
- A set is an unordered collection of objects that does not contain any duplicates.

**HashSet**
- This implementation uses an underlying HashMap, storing the value as the key to the map, and the value is a marker object, signifying that a value is stored.
- For each of the Map implementations visited earlier, there is an equivalent Set implementation: `HashSet`, `TreeSet`, and `LinkedHashSet` — although there is no Set backed by a ConcurrentHashMap.
