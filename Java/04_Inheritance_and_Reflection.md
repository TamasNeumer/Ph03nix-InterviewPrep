# Inheritance and Reflection

## Inheritance
#### Key Points:
1.	A	subclass	can	inherit	or	override	methods	from	the	superclass.
2.	Use	the	super	keyword	to	invoke	a	superclass	method	or	constructor.
3.	A	final	method	cannot	be	overridden;	a	final	class	cannot	be	extended.
4.	An	abstract	method	has	no	implementation;	an	abstract	class	cannot	be
instantiated.
5.	A	protected	member	of	a	subclass	is	accessible	in	a	subclass	method,	but	only
when	applied	to	objects	of	the	same	subclass.
6.	Every	class	is	a	subclass	of	Object	which	provides	methods	toString,
equals,	hashCode,	and	clone.
7.	Each	enumerated	type	is	a	subclass	of	Enum	which	provides	methods	toString,
valueOf,	and	compareTo.8.	The	Class	class	provides	information	about	a	Java	type,	which	can	be	a	class,
array,	interface,	primitive	type,	or	void.
9.	You	can	use	a	Class	object	to	load	resources	that	are	placed	alongside	class	files.
10.	You	can	load	classes	from	locations	other	than	the	class	path	by	using	a	class	loader.
11.	The	reflection	library	enables	programs	to	discover	members	of	arbitrary	objects,
access	variables,	and	invoke	methods.
12.	Proxy	objects	dynamically	implement	arbitrary	interfaces,	routing	all	method
invocations	to	a	handler.

#### Extending a class
- Use the `extend` keyword
- Use the `@override` to annotate that a function is overriding the parent's function.
- Since child classes can't access private members, doing so occurs via the parent's getter: `super.getSalary()`
  - Super is not a reference to an object but a directive to bypass dynamic method lookup
- You can override the return type, while respecting Liskov's principle. (e.g.: Employee is the interface that has a function `Employee getBoss()`, then the manager can override this with `Manager getBoss()`)
- **Dynamic method lookup**

  ```java
  Manager boss = new Manager(...);
  Employee empl = boss;	//	OK	to	assign	to	superclass	variable
  double salary = empl.getSalary();
  ```

- You can cast an class via `(CastToType) classObj`, however before doing so check it with `obj instanceof ClassName`
- `final` methods cannot be overriden
- Abstract classes can have instance variables and constructors.
- **The protected keyword is valid on package level!** Meaning that if a class extends another one with protected members, it can see these members, only if they are in the same package!
- Use `super::function()` to call the parent class version of the function.

#### Object - The cosmic superclass
- Every class in java directly extends the class `object`
- The object class has the following functions, that can be overriden by each class.

**toString()**
- The	Object	class	defines	the	toString	method	to	print	the	class	name	and	the	hash
code.
- An example to overrite the `toString()`

```java
public String toString() {
  return getClass().getName() + "[name=" + name + ",salary=" + salary + "]";
}
```
- By	calling	`getClass().getName()`	instead	of	hardwiring	the	string	"Employee",
this	method	does	the	right	thing	for	subclasses	as	well. (And more roboust against name changes etc.)
- Instead	of	writing	`x.toString()`,	you	can	write	`""	+	x`.	This	expression	even
works	if	x	is	null	or	a	primitive	type	value.

**equals()**
- The equals	method,	as	implemented	in	the	Object	class,	determines	whether	two	object
references	are	identical.
  - For	example,	the	String	class
overrides	equals	to	check	whether	two	strings	consist	of	the	same	characters.
- **!!!Whenever	you	override	the	equals	method,	you	must	provide	a	compatible
hashCode	method	as	well**
- Sample implementation

  ```java
  public class Item{
    private String description;
    private double price;

    public boolean equals(Object otherObject){
      if(this == otherObject) return true;
      if(otherObject == null) return false;
      if(getClass() != otherObject.getClass()) return false;
      Item other = (Item) otherObject;
      return Objects.equals(description, other.description) && price == other.price
    }
  }
  ```
- If	you	have	instance	variables	that	are	arrays,	use	the	static	Arrays.equals
method	to	check	that	the	arrays	have	equal	length	and	the	corresponding	array
elements	are	equal.

**hashCode()**
- A	hash	code	is	an	integer	that	is	derived	from	an	object.
- The	hashCode	and	equals	methods	must	be	compatible:	If	`x.equals(y)`,	then	it
must	be	the	case	that	`x.hashCode()	==	y.hashCode()`.
- Example:

  ```java
  class Item {
    ...
    public int hashCode(){
      return Objects.hash(description, price);
    }
  }
  ```
- If	your	class	has	instance	variables	that	are	arrays,	compute	their	hash	codes	first	with	the
static	Arrays.hashCode	method.
- In	an	interface,	you	can	never	make	a	default	method	that	redefines	one	of	the
methods	in	the	Object	class.

**clone()**
- Rarely necessary!
- The	clone	method	is	declared	as	protected	in	the	Object	class,	so	you	must
override	it	if	you	want	users	of	your	class	to	clone	instances.
- The	Object.clone	method	makes	a	shallow	copy.	It	simply	copies	all	instance
variables	from	the	original	to	the	cloned	object.	That	is	fine	if	the	variables	are	primitive
or	immutable.	But	if	they	aren’t,	then	the	original	and	the	clone	share	mutable	state,	which
can	be	a	problem.
- To deep copy:

```java
public Message clone(){
  Message cloned = new Message(sender, text);
  cloned.recipients = new ArrayList<>(recipients);
  return cloned;
}
```
- You can use the `@SuppressWarnings("unchecked")` to supress warnings.
