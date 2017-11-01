# Collections
The Java collections framework provides implementations of common data
structures. The main things to remember:  

1. The Collection interface provides common methods for all collections, except
for maps which are described by the Map interface.
2. A list is a sequential collection in which each element has an integer index.
3. A set is optimized for efficient containment testing. Java provides HashSet and
TreeSet implementations.
4. For maps, you have the choice between HashMap and TreeMap
implementations. A LinkedHashMap retains insertion order.
5. The Collection interface and Collections class provide many useful
algorithms: set operations, searching, sorting, shuffling, and more.
6. Views provide access to data stored elsewhere using the standard collection
interfaces.

#### Collections class' best methods
- disjoint (compare if two collections don't have common element)
- addAll(c, T... elems) (add all elements to c)
- copy
- replaceAll(list, oldVal, newVal)
- fill
- List<T> nCopies(int n, T o) Yields an immutable list with n copies of o.
- frequency(c,elem)
- binarySearch(list, key)
- sort(list)
- reverse, shuffle

#### Iterators
- Each collection provides a way to iterate through its elements in some order. The Iterable<T> superinterface of Collection defines a method
Iterator<T> iterator() It yields an iterator that you can use to visit all elements.
- For any object c of a class that implements the Iterable<E> interface,
the enhanced for loop is translated to the preceding form.
- `coll.removeIf(e -> e fulfills the condition);`
  - The remove method removes the last element that the iterator has returned, not the element to which the iterator points. You can't call remove twice without an intervening call to next or previous.
- The ListIterator interface is a subinterface of Iterator with methods for
adding an element before the iterator, setting the visited element to a different value,
and for navigating backwards. It is mainly useful for working with linked lists.

#### Sets
- A set can efficiently test whether a value is an element, but it gives up something in
return: It doesn't remember in which order elements were added. Sets are useful
whenever the order doesn't matter.
The Set interface is implemented by the `HashSet` and `TreeSet` classes.

#### Maps
- Maps store associations between keys and values. Call put to add a new association,
or change the value of an existing key:
  - `counts.put("Alice", 1);`
  - `int count = counts.get("Alice");` to get value.
- This example uses a hash map which, as for sets, is usually the better choice if you
don't need to visit the keys in sorted order. If you do, use a TreeMap instead.
- **WeakHashMaps** This data structure
cooperates with the garbage collector to remove key/value pairs when the only
reference to the key is the one from the hash table entry.

#### Views
- A collection view is a lightweight object that implements a collection interface, but doesn't store elements.
- You can get views of the keys, values, and entries of a map by calling these methods:
  - `Set<K> keySet()`, `Set<Map.Entry<K, V>> entrySet()`, `Collection<V> values()`
- The collections that are returned are not copies of the map data, but they are connected to the map. If you remove a key or entry from the view, then the entry is also removed from the underlying map.
- Another example is the `Arrays.asList(a)` method.


#### Properties
- The Properties class implements a map that can be easily saved and loaded using a plain text format. Such maps are commonly used for storing configuration options for programs.

#### BitSets
- The BitSet class stores a sequence of bits. A bit set packs bits into an array of long values, so it is more efficient to use a bit set than an array of boolean values. Bit sets are useful for sequences of flag bits or to represent sets of non-negative integers, where the ith bit is 1 to indicate that i is contained in the set.


#### Stacks, Queues, Deques, and Priority Queues
- There is no Stack interface in the Java collections framework, just a legacy Stack
class from the earliest days of Java that you should avoid. If you need a stack, queue,
or deque and are not concerned about thread safety, use an ArrayDeque.


#### Lists
**Caution:** The List interface provides methods to access the nth element of a list, even though such an access may not be efficient. To indicate that it is, a collection class should implement the RandomAccess interface. This is a tagging interface without methods. For example, ArrayList implements List and RandomAccess, but LinkedList implements only the List interface.
