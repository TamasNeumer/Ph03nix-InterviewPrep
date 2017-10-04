# Object orientation

#### Call by value vs call by reference
- Java works with **references** (regarding objects). If you create an object with `new` essentially you end up having a reference to the object.
- When you pass an object to a function, it is **passed by value**, meaning that a copy of this reference is created and you operate on this.
- That's the reason you can't mutate primitive types (and Strings) inside functions. These are passed by value, thus the changes won't be noted outside of the scope.

#### Constructors
- Constructors don't have return type. (just like in C++)
- You can overload constructors and call the other constructor in the current constructor. Here the `this` keyword is not a reference to the instance, but a special keyword!
- Numbers are initialized to 0 by default, object references to null! Or you can specify the default value when defining the class variable. (private int salary = 50;)
- **final** instance variables must be initialized by the end of the constructor.
  - Note: When used on references that refer to mutable objects (e.g. ArrayLists) it is okay to add elements to the array. It merely states, that the reference to the array never changes! (i.e. they can't replace it nor set it to null!)

```java
public Employee(double salary){
  this("", salary); //Calls Employee(String, double)
}
```

- `super()` All constructors in Java must make a call to the Object constructor. This is done with the call `super()`. This has to be the first line in a constructor. The reason for this is so that the object can actually be created on the heap before any additional initialization is performed. If you do not specify the call to `super()` in a constructor the compiler will put it in for you.

#### Inner and nested classes
**Nested Class**
- `private static class NestedClass {...}` --> 	The	class	is private	in	Invoice,	so	only	Invoice	methods	can	access	it.
- `public	static	class	Item` --> Now	anyone	can	construct	Item	objects	by	using	the	qualified	name	`Invoice.Item` Nesting	the	class	just	makes	it	obvious that	the	Item	class	represents	items	in	an	invoice.

**Inner Class**
- `static` modifier dropped.
- A method	of	an	inner	class	can	access	instance	variables	of	its	outer	class.
- Use	a	static	nested	class	when	the	instances	of	the	nested	class	don’tneed	to	know	to	which	instance	of	the	enclosing	class	they	belong.	Use	an	inner	class	only if	this	information	is	important.
```java
public	class	Network	{
				public	class	Member	{
								...
								public	void	leave()	{
												members.remove(this);
								}
				}
				private	ArrayList<Member>	members;
				...
}
```

#### Overriding equals() and hashCode()
- You might create two (custom class) objects with the same arguments, but the `.equals()` function will return `false`. --> You need to override the equals mehtod for the class.
  - Notice that the `hashCode()` method was also overwritten. The contract for that method states that when two objects are equal, their hash values must also be the same. That's why **one must almost always override hashCode() and equals() together**.
  - Pay special attention to the argument type of the equals method. It is Object obj, not Foo obj. If you put the latter in your method, that is **not an override** of the equals method.
  - It's a good practice to make the `equals()` static, thus no child-class might override it. [Link](https://stackoverflow.com/documentation/java/145/object-class-methods-and-constructor/571/equals-method)

```java
public class Foo {
    int field1, field2;
    String field3;

    public Foo(int i, int j, String k) {
        field1 = i;
        field2 = j;
        field3 = k;
    }

    @Override public static boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Foo f = (Foo) obj;
        return field1 == f.field1 && field2 == f.field2 && Objects.equals(field3, f.field3);
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = 31 * hash + this.field1;
        hash = 31 * hash + this.field2;
        hash = 31 * hash + (field3 == null ? 0 : field3.hashCode());
        return hash;
    }

    public static void main(String[] args) {
        Foo foo1 = new Foo(0, 0, "bar");
        Foo foo2 = new Foo(0, 0, "bar");

        System.out.println(foo1.equals(foo2)); // prints true
    }
}
```

#### Clone
- In order to use the `clone()` function the class has to implement the `Cloneable` interface, thus implementing the `clone()` function.
- This means to essentially create a "deep copy" by also copying any of the mutable objects that make up the internal structure of the object being cloned. If this is not implemented correctly the cloned object will not be independent and have the same references to the mutable objects as the object that it was cloned from. This would result in inconsistent behaviour as any changes to those in one would affect the other.

#### getClass() method
- The getClass() method can be used to find the runtime class type of an object.
  - `System.out.println(user.getClass()); //Prints "class User"`
- Generally it's **considered bad to use** finalize() method in applications of any kind and should be avoided.

#### finalize() method
- This is a protected and non-static method of the Object class. This method is used to perform some final operations or clean up operations on an object before it gets removed from the memory.

#### Notes
- 2 types of functions: mutators and accessors
- Garbage collector collects the unused trash.
