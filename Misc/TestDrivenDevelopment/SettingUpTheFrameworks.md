# Setting up the frameworks

## Google Test
### Getting the Framework (Linux)
- ```git clone https://github.com/google/googletest```
- ```mkdir build```
- ```cd build```
- `cmake -Dgtest_build_samples=ON ..`
- `sudo cp -r ../googletest/include/gtest /usr/local/include`
- `sudo cp googlemock/lib*.a /usr/local/lib`
### Adding it as include (Linux - CMAKE)
Add the following lines to your CMakeLists:
- ```find_package(GTest REQUIRED)```
- Add it to the list of include directories: ```include_directories(${GTEST_INCLUDE_DIR})```
- Include GTest to your file where you want to use it })```#include "gtest/gtest.h"})```

### Basics
- Assertions and Expectations
  - Outcomes: Success, Nonfatal failure, fatal failure (aborts current function)
  - ```ASSERT_*``` versions generate fatal failures when they fail and **abort the current function.**
  - ```EXPECT_*``` versions generate nonfatal failures, which don't abort the current function. --> Prefer
  - **Uses**:
    - ```EXCEPT_*```, use ```ASSERT_*``` when it doesn't make sense to go further in the test function.
    - Since a failed ```ASSERT_*``` returns from the current function immediately, possibly skipping clean-up code that comes after it, it may cause a space leak.
  - **Custom failure messages**
    - Anything that can be streamed to an ostream can be streamed to an assertion macro--in particular, C strings and string objects.
      - e.g.: ```ASSERT_EQ(x.size(), y.size()) << "Vectors x and y are of unequal length";```
  - **Basic Assertions**

  | **Fatal assertion** | **Nonfatal assertion** | **Verifies** |
  |:--------------------|:-----------------------|:-------------|
  | `ASSERT_TRUE(`_condition_`)`;  | `EXPECT_TRUE(`_condition_`)`;   | _condition_ is true |
  | `ASSERT_FALSE(`_condition_`)`; | `EXPECT_FALSE(`_condition_`)`;  | _condition_ is false |

  | **Fatal assertion** | **Nonfatal assertion** | **Verifies** |
  |:--------------------|:-----------------------|:-------------|
  |`ASSERT_EQ(`_val1_`, `_val2_`);`|`EXPECT_EQ(`_val1_`, `_val2_`);`| _val1_ `==` _val2_ |
  |`ASSERT_NE(`_val1_`, `_val2_`);`|`EXPECT_NE(`_val1_`, `_val2_`);`| _val1_ `!=` _val2_ |
  |`ASSERT_LT(`_val1_`, `_val2_`);`|`EXPECT_LT(`_val1_`, `_val2_`);`| _val1_ `<` _val2_ |
  |`ASSERT_LE(`_val1_`, `_val2_`);`|`EXPECT_LE(`_val1_`, `_val2_`);`| _val1_ `<=` _val2_ |
  |`ASSERT_GT(`_val1_`, `_val2_`);`|`EXPECT_GT(`_val1_`, `_val2_`);`| _val1_ `>` _val2_ |
  |`ASSERT_GE(`_val1_`, `_val2_`);`|`EXPECT_GE(`_val1_`, `_val2_`);`| _val1_ `>=` _val2_ |


  - These assertions can work with a user-defined type, but only if you define the corresponding comparison operator (e.g. ==, <, etc).
  - **IMPORTANT**:
    - `ASSERT_EQ()` does **pointer equality on pointers.** If used on two C strings, it tests if they are in the same memory location, not if they have the same value. Therefore, if you want to compare C strings (e.g. `const char*`) by value, use `ASSERT_STREQ()` , which will be described later on. In particular, to assert that a C string is `NULL`, use `ASSERT_STREQ(NULL, c_string)` . However, to compare two `string` objects, you should use `ASSERT_EQ`.

| **Fatal assertion** | **Nonfatal assertion** | **Verifies** |
|:--------------------|:-----------------------|:-------------|
| `ASSERT_STREQ(`_str1_`, `_str2_`);`    | `EXPECT_STREQ(`_str1_`, `_str_2`);`     | the two C strings have the same content |
| `ASSERT_STRNE(`_str1_`, `_str2_`);`    | `EXPECT_STRNE(`_str1_`, `_str2_`);`     | the two C strings have different content |
| `ASSERT_STRCASEEQ(`_str1_`, `_str2_`);`| `EXPECT_STRCASEEQ(`_str1_`, `_str2_`);` | the two C strings have the same content, ignoring case |
| `ASSERT_STRCASENE(`_str1_`, `_str2_`);`| `EXPECT_STRCASENE(`_str1_`, `_str2_`);` | the two C strings have different content, ignoring case |

  - Note that "CASE" in an assertion name means that case is ignored.
  - **A NULL pointer and an empty string are considered different.**

### Writing Simple Tests
```cpp
TEST(test_case_name, test_name) {
 ... test body ...
}
```
- The first argument is the name of the test case, and the second argument is the test's name within the test case. Both names must be valid C++ identifiers.
- A test's full name consists of its containing test case and its individual name. Tests from different test cases can have the same individual name.

```cpp
// Tests factorial of 0.
TEST(FactorialTest, HandlesZeroInput) {
  EXPECT_EQ(1, Factorial(0));
}

// Tests factorial of positive numbers.
TEST(FactorialTest, HandlesPositiveInput) {
  EXPECT_EQ(1, Factorial(1));
  EXPECT_EQ(2, Factorial(2));
  EXPECT_EQ(6, Factorial(3));
  EXPECT_EQ(40320, Factorial(8));
}
```
- Google Test groups the test results by test cases, so logically-related tests should be in the same test case; in other words, the first argument to their TEST() should be the same.

### Text Fixtures
If you find yourself writing two or more tests that operate on similar data, you can use a test fixture. It allows you to reuse the same configuration of objects for several different tests.

Steps:
1. Derive a class from `::testing::Test` . Start its body with `protected:` or `public:` as we'll want to access fixture members from sub-classes.
1. Inside the class, declare any objects you plan to use.
1. If necessary, write a default constructor or `SetUp()` function to prepare the objects for each test. A common mistake is to spell `SetUp()` as `Setup()` with a small `u` - don't let that happen to you.
1. If necessary, write a destructor or `TearDown()` function to release any resources you allocated in `SetUp()` . To learn when you should use the constructor/destructor and when you should use `SetUp()/TearDown()`, read this [FAQ entry](FAQ.md#should-i-use-the-constructordestructor-of-the-test-fixture-or-the-set-uptear-down-function).
1. If needed, define subroutines for your tests to share.

When using a fixture, use `TEST_F()` instead of `TEST()` as it allows you to access objects and subroutines in the test fixture:
```
TEST_F(test_case_name, test_name) {
 ... test body ...
}
```
- You must first define a test fixture class before using it in a TEST_F(), or you'll get the compiler error "virtual outside class declaration".

For each test defined with `TEST_F()`, Google Test will:
  1. Create a _fresh_ test fixture at runtime
  1. Immediately initialize it via `SetUp()` ,
  1. Run the test
  1. Clean up by calling `TearDown()`
  1. Delete the test fixture.  Note that different tests in the same test case have different test fixture objects, and Google Test always deletes a test fixture before it creates the next one. Google Test does not reuse the same test fixture for multiple tests. Any changes one test makes to the fixture do not affect other tests.

## Run the tests
After defining your tests, you can run them with `RUN_ALL_TESTS()` , which returns `0` if all the tests are successful, or `1` otherwise. Note that `RUN_ALL_TESTS()` runs _all tests_ in your link unit -- they can be from different test cases, or even different source files.

**Important:**
- You must not ignore the return value of `RUN_ALL_TESTS()`, or `gcc` will give you a compiler error. The rationale for this design is that the automated testing service determines whether a test has passed based on its exit code, not on its stdout/stderr output; thus your `main()` function must return the value of `RUN_ALL_TESTS()`.
- Also, you should call `RUN_ALL_TESTS()` only **once**. Calling it more than once conflicts with some advanced Google Test features (e.g. thread-safe death tests) and thus is not supported.

Source:
- https://github.com/google/googletest/blob/master/googletest/docs/Primer.md
