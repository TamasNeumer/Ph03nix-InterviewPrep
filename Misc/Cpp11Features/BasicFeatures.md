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
