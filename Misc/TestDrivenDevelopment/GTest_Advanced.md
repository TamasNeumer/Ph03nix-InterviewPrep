# Google Test Advanced Guide

## Explicit success and failure
- `SUCCEED()`, `FAIL()`, `ADD_FAILURE()`, `ADD_FAILURE_AT()`
- `FAIL()` generates a fatal failure, while `ADD_FAILURE()` and `ADD_FAILURE_AT()` generate a nonfatal failure.

```cpp
switch(expression) {
  case 1: ... some checks ...
  case 2: ... some other checks
  ...
  default: FAIL() << "We shouldn't get here.";
}
```

## Exception assertations
// TODO ADD TABLE!
```cpp
ASSERT_THROW(Foo(5), bar_exception);

EXPECT_NO_THROW({
  int n = 5;
  Bar(&n);
});
```

## Using an Existing Boolean Function
// TODO Table
This is the easiest way to test boolean functions for their return value:
```cpp
// Returns true iff m and n have no common divisors except 1.
bool MutuallyPrime(int m, int n) { ... }
const int a = 3;
const int b = 4;
const int c = 10;
EXPECT_PRED2(MutuallyPrime, a, b);
EXPECT_PRED2(MutuallyPrime, b, c); // fails
```
## Comparing floating points
Due to round-off errors, it is very unlikely that two floating-points will match exactly. Therefore, `ASSERT_EQ`'s naive comparison usually doesn't work. It's better to compare by a fixed relative error bound, except for values close to 0 due to the loss of precision there.

// TODO Insert table
By "almost equal", we mean the two values are within 4 ULP's from each other.
The following assertions allow you to choose the acceptable error bound:
// TODO INSERT TABLE
