# Class Inheritance

## Access specifiers and inheritance types
- **Syntax**
```cpp
class DerivedClass : accessSpecifier BaseClass {/*...*/};
```
Access specifiers:
- **Public:** Everything that is aware of Base is also aware of BaseClass' public members. (i.e.: They have access to it.)
- **Protected:** Only BaseClass' children and their children are aware of BaseClass' protected members.
- **Private:** No one but BaseClass is aware of its own protected members.
  - Structs have default public access specifiers while classes have private.
- The `friend` keyword can be added to give access to protected / private members.
  - `friend void printWeight(Animal animal);`
  - `friend class AnimalPrinter;`
  - A common use for a friend function is to overload the operator<< for streaming.
    - `friend std::ostream& operator<<(std::ostream& os, Animal animal);`

Inheritance types:
- **Public:**
  - `class DerivedClass : public BaseClass {/*...*/};`
  - Both protected and private members are inherited by DerivedClass. Public members remain public, while protected members remain protected.
  - In case of structs the default inheritance is public.
  - public inheritance should only be used when/if an instance of the derived class can be substituted for an instance of the base class under any possible circumstance (and still make sense). (Liskov substitution) --> **"IS-A" relationship**
- **Protected:**
  - `class DerivedClass : protected BaseClass {/*...*/};`
  - Both public and protected become protected.
- **Private:**
  - `class DerivedClass : private BaseClass {/*...*/};`
  - Both public and protected members become private.
  - In case of classes, the default inheritance (without access specifier) **is private**
  - Private inheritance describes a **"HAS-A"** relationship.

It's even possible to have a class derive from a struct (or vice versa). In this case, the default inheritance is controlled by the child, so a struct that derives from a class will default to public inheritance, and a class that derives from a struct will have private inheritance by default.

## Inheritance patterns
- Single (A->B)
- Multiple (A, B -> C)
- Hierarchical inheritance (Person -> Student / Employee ; Student -> ITStudent/MathStudent ; Employee -> Driver/Engineer)
- Diamond pattern

## Abstract class and their functions
### Virtual Functions
**A virtual function makes its class a polymorphic base class.** Derived classes can override virtual functions. Virtual functions called through base class pointers/references will be resolved at run-time.
- (Normal) Virtual Functions must have an implementation in the base class.
  - `virtual void f() {/*...*/}`
- Pure Virtual Functions don't have implementation in the base class.
  - `virtual void f() = 0;`
### Abstract class
- A pure virtual function implicitly makes a class abstract.
- Abstract classes can't be instantiated.
- Derived classes need to override/implement the BaseClass' pure virtual functions.

## Constructor and Virtual Destructor
- **Constructor** Base class must have an implemented constructor. (The class might have private members that need to be initialized --> only the BaseClass' constructor will be able to initialize them.)
- **Destructor** Virtual destructors are useful when you can delete an instance of a derived class through a pointer to base class:
```cpp
Base *b = new Derived();
// use b
delete b; // Here's the problem! --> leaking, as derived class part was not destructed
```
If you want to prevent the deletion of an instance through a base class pointer, you can **make the base class destructor protected and nonvirtual**; by doing so, the compiler won't let you call delete on a base class pointer.

**Guideline: A base class destructor should be either public and virtual, or protected and nonvirtual.**

## Initializing base class via derived class
- Once you added a non-deafult constructor to a base class, you have to call it in the child class and instantiate the base class!
  - `DerivedClassCtr() : baseClassNonDefCtr(argPassedToBaseCtr) {/*...*/}`

## Final keyword
- The `final` keyword forbids the class to be inherited.
- `class A final {};`

## The override keyword
- In Cpp11 when overriding the function you can use the `override` keyword.
  - `void foo() const override;`

## Virtual inheritance as a measure to solve the diamond patterns
In the traditional diamond pattern (A->B,C and B,C -> D) B and C both inherit from A, and D inherits from B and C, so there are 2 instances of A in D! This results in ambiguity when you're accessing member of A through D, as the compiler has no way of knowing from which class do you want to access that member (the one which B inherits, or the one that is inherited by C?).

Virtual inheritance solves this problem: Since virtual base resides only in most derived object, there will be only one instance of A in D.

```cpp
struct B : public /*virtual*/ A {};
struct C : public /*virtual*/ A {};
```
