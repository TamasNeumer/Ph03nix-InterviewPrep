# OO
#### Call by value vs call by reference
- Java works with **references** (regarding objects). If you create an object with `new` essentially you end up having a reference to the object.
- When you pass an object to a function, it is **passed by value**, meaning that a copy of this reference is created and you operate on this.
- That's the reason you can't mutate primitive types (and Strings) inside functions. These are passed by value, thus the changes won't be noted outside of the scope.

#### Constructors
- Constructors don't have return type. (just like in C++)
- You can overload constructors and call the other constructor in the current constructor. Here the this keyword is not a reference to the instance, but a special keyword!

```java
public Employee(double salary){
  this("", salary); //Calls Employee(String, double)
}
```

#### Notes
- 2 types of functions: mutators and accessors
- Garbage collector collects the unused trash.
