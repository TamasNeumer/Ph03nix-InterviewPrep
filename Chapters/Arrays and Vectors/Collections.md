# Java Collections
The collections framework in `java.util` provides a number of generic classes for sets of data with functionality that can't be provided by regular arrays.

Collections framework contains interfaces for `Collection<O>`, with main sub-interfaces `List<O> `and `Set<O>`, and mapping collection `Map<K,V>`. Collections are the root interface and are being implemented by many other collection frameworks.




#### Removing items from a List wihin a loop
- It is tricky to remove items from a list while within a loop, this is due to the fact that the index and length of the list gets changed. (Normally if you would do `list.remove(index)` inside the loop, you would skip the next element.) --> Thus the original index[1] is skipped.
- Removing in the enhanced for statement Throws Exception
```java
for (int i = 0; i < fruits.size(); i++) {
    System.out.println (fruits.get(i));
    if ("Apple".equals(fruits.get(i))) {
         fruits.remove(i);
    }     
}
```

- Solution:

```java
fruits.removeIf(p -> "Apple".equals(p));

//OR:
List<String> filteredList =
    fruits.stream().filter(p -> !"Apple".equals(p)).collect(Collectors.toList());
```


#### Collections
