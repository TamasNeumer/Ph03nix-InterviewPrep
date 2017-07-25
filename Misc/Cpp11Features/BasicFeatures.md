# Basic C++11 features
## auto
-	You can’t use auto for function parameters or member variables or declare arrays with auto.
- Auto is especially useful while working with templates:
```cpp
template <typename X, typename Y>
void doStuff(X x, Y y)
{
  auto result = x*y;
}
```
-	Const and volatile specifiers are removed, (if deep copy)
  - Use ``const auto`` or ``volatile auto``
- Const and volatile **not** removed if using reference.
  - `auto& var = otherConstVar`

## decltype
- Figures our the type of the expression
```cpp
std::cout << typeid(decltype(i+1.0)).name() << std::endl;
```
- Useful when working with templates --> define return type by `decltype`.
  - `decltype(x*y) DoSomething(X x, Y y){/*...*/}`

## Variadic templates
- The basic idea of a class template is that the template parameter gets substituted by a type at **compile** time
- It is often useful to define classes or structures that have a variable number and type of data members which are defined at compile time.
```cpp
template<typename T, typename ... Rest>
struct DataStructure<T, Rest ...>
{
    DataStructure(const T& first, const Rest& ... rest)
        : first(first)
        , rest(rest...)
    {}

    T first;
    DataStructure<Rest ... > rest;
};
```
- This is now sufficient for us to create arbitrary data structures, like ``DataStructure<int, float, std::string> data(1, 2.1, "hello").``
- To understand this better, we can work through an example: suppose you have a declaration DataStructure<int, float> data. The declaration first matches against the specialisation, yielding a structure with int first and DataStructure<float> rest data members. The rest definition again matches this specialisation, creating its own float first and DataStructure<> rest members. Finally this last rest matches against the base-case defintion, producing an empty structure.
```
DataStructure<int, float>
   -> int first
   -> DataStructure<float> rest
         -> float first
         -> DataStructure<> rest
              -> (empty)
```
- Now we have the data structure, but its not terribly useful yet as we cannot easily access the individual data elements (for example to access the last member of DataStructure<int, float, std::string> data we would have to use data.rest.rest.first, which is not exactly user-friendly). So we add a get method to it (only needed in the specialisation as the base-case structure has no data to get):
```cpp
template<typename T, typename ... Rest>
struct DataStructure<T, Rest ...>
{
    ...
    template<size_t idx>
    auto get()
    {
        return GetHelper<idx, DataStructure<T,Rest...>>::get(*this);
    }
    ...
};
```
- To work through an example, suppose we have DataStructure<int, float> data and we need data.get<1>(). This invokes GetHelper<1, DataStructure<int, float>>::get(data) (the 2nd specialisation), which in turn invokes GetHelper<0, DataStructure<float>>::get(data.rest), which finally returns (by the 1st specialisation as now idx is 0) data.rest.first.
- The whole stuff:
```cpp
#include <iostream>

template<size_t idx, typename T>
struct GetHelper;

template<typename ... T>
struct DataStructure
{
};

template<typename T, typename ... Rest>
struct DataStructure<T, Rest ...>
{
    DataStructure(const T& first, const Rest& ... rest)
        : first(first)
        , rest(rest...)
    {}

    T first;
    DataStructure<Rest ... > rest;

    template<size_t idx>
    auto get()
    {
        return GetHelper<idx, DataStructure<T,Rest...>>::get(*this);
    }
};

template<typename T, typename ... Rest>
struct GetHelper<0, DataStructure<T, Rest ... >>
{
    static T get(DataStructure<T, Rest...>& data)
    {
        return data.first;
    }
};

template<size_t idx, typename T, typename ... Rest>
struct GetHelper<idx, DataStructure<T, Rest ... >>
{
    static auto get(DataStructure<T, Rest...>& data)
    {
        return GetHelper<idx-1, DataStructure<Rest ...>>::get(data.rest);
    }
};

int main()
{
    DataStructure<int, float, std::string> data(1, 2.1, "Hello");

    std::cout << data.get<0>() << std::endl;
    std::cout << data.get<1>() << std::endl;
    std::cout << data.get<2>() << std::endl;

    return 0;
}
```
