# Setting up the frameworks

## Google Test
### Getting the Framework
- ```git clone https://github.com/google/googletest```
- ```mkdir build```
- ```cd build```
- ```cmake -Dgtest_build_samples=ON ..```

### Adding it as include
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
    - ```ASSERT_TRUE()```, ```ASSERT_FALSE()```, ```EXPECT_TRUE()```, ```EXPECT_TUE()```, ```EXPECT_FALSE()```
    - Comparisons:

    | **Fatal assertion** | **Nonfatal assertion** | **Verifies** |
    |:--------------------|:-----------------------|:-------------|
    |`ASSERT_EQ(`_val1_`, `_val2_`);`|`EXPECT_EQ(`_val1_`, `_val2_`);`| _val1_ `==` _val2_ |
    |`ASSERT_NE(`_val1_`, `_val2_`);`|`EXPECT_NE(`_val1_`, `_val2_`);`| _val1_ `!=` _val2_ |
    |`ASSERT_LT(`_val1_`, `_val2_`);`|`EXPECT_LT(`_val1_`, `_val2_`);`| _val1_ `<` _val2_ |
    |`ASSERT_LE(`_val1_`, `_val2_`);`|`EXPECT_LE(`_val1_`, `_val2_`);`| _val1_ `<=` _val2_ |
    |`ASSERT_GT(`_val1_`, `_val2_`);`|`EXPECT_GT(`_val1_`, `_val2_`);`| _val1_ `>` _val2_ |
    |`ASSERT_GE(`_val1_`, `_val2_`);`|`EXPECT_GE(`_val1_`, `_val2_`);`| _val1_ `>=` _val2_ |
