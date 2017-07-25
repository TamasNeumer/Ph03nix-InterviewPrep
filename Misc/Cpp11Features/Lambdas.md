# Lambdas
## General
- Lambdas provide a way to create simple function objects. The result is a `closure object` that behaves like a function object. Such can bel called using the operator ().
- Lambdas are often used as an argument to functions that take a callable object. In such cases lambdas allow to define the function objects inline.

## Structure of a lambda expression
- [](){}

### Capture List
- ``[]`` is the capture list. By default, variables of the enclosing scope cannot be accessed by a lambda.
- Capturing a variable makes it accessible inside the lambda, either as a copy or as a reference. Captured variables become a part of the lambda; in contrast to function arguments, they do not have to be passed when calling the lambda.
- Objects captured by value in the lambda are by default immutable. This is because the operator() of the generated closure object is const by default. (`operator() const`) Modifying can be allowed by using the keyword mutable, which make the closer object's operator() non-const. (``[a]() mutable {/*..*/})
- [] --> Captures nothing
- [a] --> Captures variable a by value. This also requires that the variable's type be copy-constructible. In Cpp14 you can use `[p = std::move(p)]`
- [&a] --> Capture variable a by reference
- [=] --> Captures everything by value
- [&] --> Captures everything by reference
- ``auto func = [c = 0]() mutable {c++; std::cout << c;} `` --> captures c as mutable
- ``auto func = [c = 0]() mutable -> int {++c; std::cout << c; return c;};``
- [=, &b]() --> overriding the default value capture for variable b.
### Parameter List
- ``()`` is the parameter list, which is almost the same as in regular functions. If the lambda takes no arguments, these parentheses can be omitted.

### Function Body
- ``{}`` is the body, which is the same as in regular functions.

### Return type
- For lambdas with a **single return statement**, or **multiple return statements whose expressions are of the same type**, the compiler can deduce the return type.
- For lambdas with **multiple return statements of different types**, the compiler can't deduce the return type
- If you want you can specify the return type via a trailing:
  - `[]() -> bool { return true; };`
```cpp
// has to be specified explicitely
auto l = [](int value) -> double {
    if (value < 10) {
        return 1;
    } else {
        return 1.5;
    }
};
```
- Lambdas without explicitly specified return types never return references, so if a reference type is desired it must be explicitly specified as well.


## Simple examples
```cpp
// Declare a vector
std::vector<int> vec{ 1, 2, 3, 4, 5 };

// Find a number that's less than a given input (assume this would have been function input)
int threshold = 10;
auto it = std::find_if(vec.begin(), vec.end(), [threshold](int value) { return value < threshold; });

// Generic functor used for comparison
struct islessthan
{
    islessthan(int threshold) : _threshold(threshold) {}
    bool operator()(int value) const
    {
        return value < _threshold;
    }
private:
    int _threshold;
};
```
