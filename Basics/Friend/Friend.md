# Friend keyword

## Friend classes
- C++ provides the friend keyword to enable access to the class' private members for other, external classes. Inside a class, you can indicate that other classes (or functions) will have direct access to protected and private members of the class. When granting access to a class, you must specify that the access is granted for a class using the friend keyword.
- Note that **friend declarations can go in either the public, private, or protected section** of a class--it doesn't matter where they appear. In particular, specifying a friend in the section marked protected doesn't prevent the friend from also accessing private fields.

```cpp
class Classy {
private:
  int data = 5;
  friend class OtherClass; // OtherClass can access private member data!
};
class OtherClass {
public:
  Classy *myFriendClass;
  OtherClass(Classy *classyptr) : myFriendClass(classyptr) {}
  void print() {
    std::cout << "The data is: " << myFriendClass->data << std::endl;
  }
};
int main(int argc, char *argv[]) {
  Classy instanceOfClassy;
  OtherClass otherClassy(&instanceOfClassy);
  otherClassy.print();
  return 0;
}
```

## Friend functions
Similarly, a class can grant access to its internal variables on a more selective basis--for instance, restricting access to only a single function. To do so, the entire function signature must be replicated after the friend specifier, including the return type of the function--and, of course, you'll need to give the scope of the function if it's inside another class:

### Global function accessing class member:
```cpp
class Distance {
private:
  int meter;

public:
  Distance() : meter(0) {}
  // friend function
  friend int addFive(Distance);
};

// friend function definition
int addFive(Distance d) {
  // accessing private data from non-member function
  d.meter += 5;
  return d.meter;
}
int main(int argc, char *argv[]) {
  Distance D;
  std::cout << "Distance: " << addFive(D);
  return 0;
}
```
[1]

### Other class' function accessing class member:
```cpp
class Foo; // Forward declaration of class Foo
class Bar {
private:
  int a;

public:
  Bar() : a(0) {}
  void show(Bar &x, Foo &y);
};

class Foo {
private:
  int b;

public:
  Foo() : b(6) {}
  friend void Bar::show(Bar &x,
                        Foo &y); // declaration of friend from other class
};

// Definition of a member function of Bar; this member is a friend of Foo
void Bar::show(Bar &x, Foo &y) {
  cout << "Show via function member of Bar" << endl;
  cout << "Bar::a = " << x.a << endl;
  cout << "Foo::b = " << y.b << endl;
}

int main() {
  Bar a;
  Foo b;
  a.show(a, b);
}
```
[2]


Sources:
- [1] https://www.programiz.com/cpp-programming/friend-function-class
- [2] https://en.wikipedia.org/wiki/Friend_function
