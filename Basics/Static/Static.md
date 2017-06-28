# Static

## Preface: Internal vs External linkage
A translation unit refers to an implementation (.c/.cpp) file and all header (.h/.hpp) files it includes. If an object or function inside such a translation unit has internal linkage, then that specific symbol is only visible to the linker within that translation unit. If an object or function has external linkage, the linker can also see it when processing other translation units. **The static keyword, when used in the global namespace, forces a symbol to have internal linkage. The extern keyword results in a symbol having external linkage.** [1]

## What does static mean?
Static members of a class are not associated with the objects of the class: they are independent objects with **static storage duration**.
- **Static storage duration**: The storage for the object is allocated when the program begins and deallocated when the program ends. Only one instance of the object exists. All objects declared at namespace scope (including global namespace) have this storage duration, plus those declared with static or extern. [2]

## Static variable inside a function
A static duration variable (also called a “static variable”) is one that retains its value even after the scope in which it has been created has been exited! Static duration variables are only created (and initialized) once, and then they are persisted throughout the life of the program.
```cpp
void test(){
  int i = 10;
  while(i--){
    static int s = 5; // defined and declared only once
    int a = 6;        // defined and declared in each loop
    std::cout << s << " " << a << std::endl;
  }
}
```

## Static member valirable
**Unlike normal member variables, static member variables are shared by all objects of the class.**  
When we declare a static member variable inside a class, we’re simply telling the class that a static member variable exists (much like a forward declaration). Because static member variables are not part of the individual class objects (they get initialized when the program starts), **you must explicitly define the static member outside of the class, in the global scope.**  
```cpp
int SomeStruct::static_value = 1;
```
This line serves two purposes: it instantiates the static member variable (just like a global variable), and optionally initializes it. In this case, we’re providing the initialization value 1. **If no initializer is provided (only declaration), C++ initializes the value to 0.**
```cpp
struct SomeStruct {
  public:
	static int static_value;
};

int SomeStruct::static_value = 1;

int main(int argc, char *argv[]) {
	staticOnlyInitializedOnce();
	std::cout << "------------------------------" << std::endl;
	SomeStruct s1;
	SomeStruct s2;

	s2.static_value = 2;

	std::cout << s1.static_value << "\n";
	std::cout << s2.static_value << "\n";
	return 0;
}
```
- Prints 2 for both values.  

There is **one exception where a static member definition line is not required:** when the static member is of type **const integer or const enum.** Those can be initialized directly on the line in which they are declared:
```cpp
struct SomeStruct {
  public:
	   static const int static_value = 4;
};
```
## Static member functions
- Static functions **cannot have –cv qualifier** (A static member function shall not be declared const,  volatile, or const volatile.)
- Static functions are not bound to object instances, thus they have **no *this** pointer.
- Static functions can **only access static member variables**. (This is because they are not bound to objects, thus they can't access non-static member variables.)

```cpp
struct SomeStruct {
public:
  static const int static_value = 5;
  int normal_value = 3;
  static void printValue() {
    std::cout << static_value << std::endl;
    // std::cout << normal_value << std::endl; --> COMPILE ERROR
  }
};
```


Sources:
- [1] http://www.goldsborough.me/c/c++/linker/2016/03/30/19-34-25-internal_and_external_linkage_in_c++/
- [2] http://en.cppreference.com/w/cpp/language/static?ref=driverlayer.com/web
- [3] https://en.wikipedia.org/wiki/Static_variable
- http://www.learncpp.com/cpp-tutorial/811-static-member-variables/
- http://www.cprogramming.com/tutorial/statickeyword.html
