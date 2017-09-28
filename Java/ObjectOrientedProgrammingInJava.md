# OO
#### Call by value vs call by reference
- Java works with **references** (regarding objects). If you create an object with `new` essentially you end up having a reference to the object.
- When you pass an object to a function, it is **passed by value**, meaning that a copy of this reference is created and you operate on this.
- That's the reason you can't mutate primitive types (and Strings) inside functions. These are passed by value, thus the changes won't be noted outside of the scope.

#### Constructors
- Constructors don't have return type. (just like in C++)
- You can overload constructors and call the other constructor in the current constructor. Here the this keyword is not a reference to the instance, but a special keyword!
- Numbers are initialized to 0 by default, object references to null! Or you can specify the default value when defining the class variable. (private int salary = 50;)
- **final** instance variables must be initialized by the end of the constructor.
  - Note: When used on references that refer to mutable objects (e.g. ArrayLists) it is okay to add elements to the array. It merely states, that the reference to the array never changes! (i.e. they can't replace it nor set it to null!)

```java
public Employee(double salary){
  this("", salary); //Calls Employee(String, double)
}
```
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


#### Notes
- 2 types of functions: mutators and accessors
- Garbage collector collects the unused trash.
