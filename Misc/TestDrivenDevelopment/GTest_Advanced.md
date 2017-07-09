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


| **Fatal assertion** | **Nonfatal assertion** | **Verifies** |
|:--------------------|:-----------------------|:-------------|
| `ASSERT_THROW(`_statement_, _exception\_type_`);`  | `EXPECT_THROW(`_statement_, _exception\_type_`);`  | _statement_ throws an exception of the given type  |
| `ASSERT_ANY_THROW(`_statement_`);`                | `EXPECT_ANY_THROW(`_statement_`);`                | _statement_ throws an exception of any type        |
| `ASSERT_NO_THROW(`_statement_`);`                 | `EXPECT_NO_THROW(`_statement_`);`                 | _statement_ doesn't throw any exception            |


```cpp
ASSERT_THROW(Foo(5), bar_exception);

EXPECT_NO_THROW({
  int n = 5;
  Bar(&n);
});
```

## Using an Existing Boolean Function

| **Fatal assertion** | **Nonfatal assertion** | **Verifies** |
|:--------------------|:-----------------------|:-------------|
| `ASSERT_PRED1(`_pred1, val1_`);`       | `EXPECT_PRED1(`_pred1, val1_`);` | _pred1(val1)_ returns true |
| `ASSERT_PRED2(`_pred2, val1, val2_`);` | `EXPECT_PRED2(`_pred2, val1, val2_`);` |  _pred2(val1, val2)_ returns true |
|  ...                | ...                    | ...          |

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


| **Fatal assertion** | **Nonfatal assertion** | **Verifies** |
|:--------------------|:-----------------------|:-------------|
| `ASSERT_FLOAT_EQ(`_val1, val2_`);`  | `EXPECT_FLOAT_EQ(`_val1, val2_`);` | the two `float` values are almost equal |
| `ASSERT_DOUBLE_EQ(`_val1, val2_`);` | `EXPECT_DOUBLE_EQ(`_val1, val2_`);` | the two `double` values are almost equal |

By "almost equal", we mean the two values are within 4 ULP's from each other.
The following assertions allow you to choose the acceptable error bound:

| **Fatal assertion** | **Nonfatal assertion** | **Verifies** |
|:--------------------|:-----------------------|:-------------|
| `ASSERT_NEAR(`_val1, val2, abs\_error_`);` | `EXPECT_NEAR`_(val1, val2, abs\_error_`);` | the difference between _val1_ and _val2_ doesn't exceed the given absolute error |

## Printing infromation from Test case
This printer knows how to print built-in C++ types, native arrays, STL
containers, and any type that supports the `<<` operator.  For other
types, it prints the raw bytes in the value and hopes that you the
user can figure it out.

As mentioned earlier, the printer is _extensible_.  That means
you can teach it to do a better job at printing your particular type
than to dump the bytes.  To do that, define `<<` for your type:

```cpp
#include <iostream>

namespace foo {

class Bar { ... };  // We want Google Test to be able to print instances of this.

// It's important that the << operator is defined in the SAME
// namespace that defines Bar.  C++'s look-up rules rely on that.
::std::ostream& operator<<(::std::ostream& os, const Bar& bar) {
  return os << bar.DebugString();  // whatever needed to print bar to os
}

}  // namespace foo
```

Sometimes, this might not be an option: your team may consider it bad
style to have a `<<` operator for `Bar`, or `Bar` may already have a
`<<` operator that doesn't do what you want (and you cannot change
it).  If so, you can instead define a `PrintTo()` function like this:

```cpp
#include <iostream>

namespace foo {

class Bar { ... };

// It's important that PrintTo() is defined in the SAME
// namespace that defines Bar.  C++'s look-up rules rely on that.
void PrintTo(const Bar& bar, ::std::ostream* os) {
  *os << bar.DebugString();  // whatever needed to print bar to os
}

}  // namespace foo
```

If you have defined both `<<` and `PrintTo()`, the latter will be used
when Google Test is concerned.  This allows you to customize how the value
appears in Google Test's output without affecting code that relies on the
behavior of its `<<` operator.

## Death Tests
Since these precondition checks cause the processes to die, we call such tests death tests. More generally, any test that checks that a program terminates (except by throwing an exception) in an expected fashion is also a death test.


| **Fatal assertion** | **Nonfatal assertion** | **Verifies** |
|:--------------------|:-----------------------|:-------------|
| `ASSERT_DEATH(`_statement, regex_`);` | `EXPECT_DEATH(`_statement, regex_`);` | _statement_ crashes with the given error |
| `ASSERT_DEATH_IF_SUPPORTED(`_statement, regex_`);` | `EXPECT_DEATH_IF_SUPPORTED(`_statement, regex_`);` | if death tests are supported, verifies that _statement_ crashes with the given error; otherwise verifies nothing |
| `ASSERT_EXIT(`_statement, predicate, regex_`);` | `EXPECT_EXIT(`_statement, predicate, regex_`);` |_statement_ exits with the given error and its exit code matches _predicate_ |

where statement is a statement that is expected to cause the process to die, predicate is a function or function object that evaluates an integer exit status, and regex is a regular expression that the stderr output of statement is expected to match. Note that statement can be any valid statement (including compound statement) and doesn't have to be an expression.

**Note:** We use the word "crash" here to mean that the process
terminates with a _non-zero_ exit status code.  There are two
possibilities: either the process has called `exit()` or `_exit()`
with a non-zero value, or it may be killed by a signal.

This means that if _statement_ terminates the process with a 0 exit
code, it is _not_ considered a crash by `EXPECT_DEATH`.  Use
`EXPECT_EXIT` instead if this is the case, or if you want to restrict
the exit code more precisely.

```cpp
TEST(MyDeathTest, Foo) {
  // This death test uses a compound statement.
  ASSERT_DEATH({ int n = 5; Foo(&n); }, "Error on line .* of Foo()");
}
TEST(MyDeathTest, NormalExit) {
  EXPECT_EXIT(NormalExit(), ::testing::ExitedWithCode(0), "Success");
}
TEST(MyDeathTest, KillMyself) {
  EXPECT_EXIT(KillMyself(), ::testing::KilledBySignal(SIGKILL), "Sending myself unblockable signal");
}
```

verifies that:

  * calling `Foo(5)` causes the process to die with the given error message,
  * calling `NormalExit()` causes the process to print `"Success"` to stderr and exit with exit code 0, and
  * calling `KillMyself()` kills the process with signal `SIGKILL`.

## Regular Expression Syntax
On POSIX systems (e.g. Linux, Cygwin, and Mac), Google Test uses the
[POSIX extended regular expression](http://www.opengroup.org/onlinepubs/009695399/basedefs/xbd_chap09.html#tag_09_04)
syntax in death tests. To learn about this syntax, you may want to read this [Wikipedia entry](http://en.wikipedia.org/wiki/Regular_expression#POSIX_Extended_Regular_Expressions).

On Windows, Google Test uses its own simple regular expression
implementation. It lacks many features you can find in POSIX extended
regular expressions.  For example, we don't support union (`"x|y"`),
grouping (`"(xy)"`), brackets (`"[xy]"`), and repetition count
(`"x{5,7}"`), among others. Below is what we do support (Letter `A` denotes a
literal character, period (`.`), or a single `\\` escape sequence; `x`
and `y` denote regular expressions.):

| `c` | matches any literal character `c` |
|:----|:----------------------------------|
| `\\d` | matches any decimal digit         |
| `\\D` | matches any character that's not a decimal digit |
| `\\f` | matches `\f`                      |
| `\\n` | matches `\n`                      |
| `\\r` | matches `\r`                      |
| `\\s` | matches any ASCII whitespace, including `\n` |
| `\\S` | matches any character that's not a whitespace |
| `\\t` | matches `\t`                      |
| `\\v` | matches `\v`                      |
| `\\w` | matches any letter, `_`, or decimal digit |
| `\\W` | matches any character that `\\w` doesn't match |
| `\\c` | matches any literal character `c`, which must be a punctuation |
| `\\.` | matches the `.` character         |
| `.` | matches any single character except `\n` |
| `A?` | matches 0 or 1 occurrences of `A` |
| `A*` | matches 0 or many occurrences of `A` |
| `A+` | matches 1 or many occurrences of `A` |
| `^` | matches the beginning of a string (not that of each line) |
| `$` | matches the end of a string (not that of each line) |
| `xy` | matches `x` followed by `y`       |

## Adding Traces to Assertions ##
If a test sub-routine is called from several places, when an assertion
inside it fails, it can be hard to tell which invocation of the
sub-routine the failure is from.  You can alleviate this problem using
extra logging or custom failure messages, but that usually clutters up
your tests. A better solution is to use the `SCOPED_TRACE` macro:

| `SCOPED_TRACE(`_message_`);` |
|:-----------------------------|
where _message_ can be anything streamable to `std::ostream`. This
macro will cause the current file name, line number, and the given
message to be added in every failure message. The effect will be
undone when the control leaves the current lexical scope.

```cpp
void Sub1(int n) {
   EXPECT_EQ(1, Bar(n));
   EXPECT_EQ(2, Bar(n + 1));
 }

 TEST(FooTest, Bar) {
   {
     SCOPED_TRACE("A");  // This trace point will be included in
                         // every failure in this scope.
     Sub1(1);
   }
   // Now it won't.
   Sub1(9);
 }
```

```cpp
path/to/foo_test.cc:11: Failure
Value of: Bar(n)
Expected: 1
Actual: 2
 Trace:
path/to/foo_test.cc:17: A

path/to/foo_test.cc:12: Failure
Value of: Bar(n + 1)
Expected: 2
Actual: 3
```

## Logging additional infromation:

In your test code, you can call `RecordProperty("key", value)` to log
additional information, where `value` can be either a string or an `int`. The _last_ value recorded for a key will be emitted to the XML output
if you specify one. For example, the test

```cpp
TEST_F(WidgetUsageTest, MinAndMaxWidgets) {
  RecordProperty("MaximumWidgets", ComputeMaxUsage());
  RecordProperty("MinimumWidgets", ComputeMinUsage());
}
```

will output XML like this:

```cpp
...
  <testcase name="MinAndMaxWidgets" status="run" time="6" classname="WidgetUsageTest"
            MaximumWidgets="12"
            MinimumWidgets="9" />
...
```

# Sharing Resources Between Tests in the Same Test Case #



Google Test creates a new test fixture object for each test in order to make
tests independent and easier to debug. However, sometimes tests use resources
that are expensive to set up, making the one-copy-per-test model prohibitively
expensive.

If the tests don't change the resource, there's no harm in them sharing a
single resource copy. So, in addition to per-test set-up/tear-down, Google Test
also supports per-test-case set-up/tear-down. To use it:

  1. In your test fixture class (say `FooTest` ), define as `static` some member variables to hold the shared resources.
  1. In the same test fixture class, define a `static void SetUpTestCase()` function (remember not to spell it as **`SetupTestCase`** with a small `u`!) to set up the shared resources and a `static void TearDownTestCase()` function to tear them down.

That's it! Google Test automatically calls `SetUpTestCase()` before running the
_first test_ in the `FooTest` test case (i.e. before creating the first
`FooTest` object), and calls `TearDownTestCase()` after running the _last test_
in it (i.e. after deleting the last `FooTest` object). In between, the tests
can use the shared resources.

Remember that the test order is undefined, so your code can't depend on a test
preceding or following another. Also, the tests must either not modify the
state of any shared resource, or, if they do modify the state, they must
restore the state to its original value before passing control to the next
test.

Here's an example of per-test-case set-up and tear-down:
```cpp
class FooTest : public ::testing::Test {
 protected:
  // Per-test-case set-up.
  // Called before the first test in this test case.
  // Can be omitted if not needed.
  static void SetUpTestCase() {
    shared_resource_ = new ...;
  }

  // Per-test-case tear-down.
  // Called after the last test in this test case.
  // Can be omitted if not needed.
  static void TearDownTestCase() {
    delete shared_resource_;
    shared_resource_ = NULL;
  }

  // You can define per-test set-up and tear-down logic as usual.
  virtual void SetUp() { ... }
  virtual void TearDown() { ... }

  // Some expensive resource shared by all tests.
  static T* shared_resource_;
};

T* FooTest::shared_resource_ = NULL;

TEST_F(FooTest, Test1) {
  ... you can refer to shared_resource here ...
}
TEST_F(FooTest, Test2) {
  ... you can refer to shared_resource here ...
}
```

# Global Set-Up and Tear-Down #

Just as you can do set-up and tear-down at the test level and the test case
level, you can also do it at the test program level. Here's how.

First, you subclass the `::testing::Environment` class to define a test
environment, which knows how to set-up and tear-down:

```cpp
class Environment {
 public:
  virtual ~Environment() {}
  // Override this to define how to set up the environment.
  virtual void SetUp() {}
  // Override this to define how to tear down the environment.
  virtual void TearDown() {}
};
```

Then, you register an instance of your environment class with Google Test by
calling the `::testing::AddGlobalTestEnvironment()` function:

```cpp
Environment* AddGlobalTestEnvironment(Environment* env);
```

Now, when `RUN_ALL_TESTS()` is called, it first calls the `SetUp()` method of
the environment object, then runs the tests if there was no fatal failures, and
finally calls `TearDown()` of the environment object.
Note that Google Test takes ownership of the registered environment objects. Therefore do not delete them by yourself.

## Value Parametrized Tests

What happens when you have a function that you want to test with different values? (e.g.: Boolean function, that you want to test with true/false values:)
```cpp
void TestFooHelper(bool flag_value) {
  flag = flag_value;
  // A code to test foo().
}

TEST(MyCodeTest, TestFoo) {
  TestFooHelper(false);
  TestFooHelper(true);
}
```

But this setup has serious drawbacks. First, when a test assertion fails in your tests, it becomes unclear what value of the parameter caused it to fail. You can stream a clarifying message into your `EXPECT`/`ASSERT` statements, but it you'll have to do it with all of them. Second, you have to add one such helper function per test. What if you have ten tests? Twenty? A hundred?

Value-parameterized tests will let you write your test only once and then easily instantiate and run it with an arbitrary number of parameter values.

Here are some other situations when value-parameterized tests come handy:

  * You want to test different implementations of an OO interface.
  * You want to test your code over various inputs (a.k.a. data-driven testing). This feature is easy to abuse, so please exercise your good sense when doing it!

### How to write them?
To write value-parameterized tests, first you should define a fixture
class.  It must be derived from both `::testing::Test` and
`::testing::WithParamInterface<T>` (the latter is a pure interface),
where `T` is the type of your parameter values.  For convenience, you
can just derive the fixture class from `::testing::TestWithParam<T>`,
which itself is derived from both `::testing::Test` and
`::testing::WithParamInterface<T>`. `T` can be any copyable type. If
it's a raw pointer, you are responsible for managing the lifespan of
the pointed values.

```cpp
class FooTest : public ::testing::TestWithParam<const char*> {
  // You can implement all the usual fixture class members here.
  // To access the test parameter, call GetParam() from class
  // TestWithParam<T>.
};

// Or, when you want to add parameters to a pre-existing fixture class:
class BaseTest : public ::testing::Test {
  ...
};
class BarTest : public BaseTest,
                public ::testing::WithParamInterface<const char*> {
  ...
};
```

Then, use the `TEST_P` macro to define as many test patterns using
this fixture as you want.  The `_P` suffix is for "parameterized" or
"pattern", whichever you prefer to think.

```cpp
TEST_P(FooTest, DoesBlah) {
  // Inside a test, access the test parameter with the GetParam() method
  // of the TestWithParam<T> class:
  EXPECT_TRUE(foo.Blah(GetParam()));
  ...
}

TEST_P(FooTest, HasBlahBlah) {
  ...
}
```

Finally, you can use `INSTANTIATE_TEST_CASE_P` to instantiate the test
case with any set of parameters you want. Google Test defines a number of
functions for generating test parameters. They return what we call
(surprise!) _parameter generators_. Here is a summary of them,
which are all in the `testing` namespace:

| `Range(begin, end[, step])` | Yields values `{begin, begin+step, begin+step+step, ...}`. The values do not include `end`. `step` defaults to 1. |
|:----------------------------|:------------------------------------------------------------------------------------------------------------------|
| `Values(v1, v2, ..., vN)`   | Yields values `{v1, v2, ..., vN}`.                                                                                |
| `ValuesIn(container)` and `ValuesIn(begin, end)` | Yields values from a C-style array, an STL-style container, or an iterator range `[begin, end)`. `container`, `begin`, and `end` can be expressions whose values are determined at run time.  |
| `Bool()`                    | Yields sequence `{false, true}`.                                                                                  |
| `Combine(g1, g2, ..., gN)`  | Yields all combinations (the Cartesian product for the math savvy) of the values generated by the `N` generators. This is only available if your system provides the `<tr1/tuple>` header. If you are sure your system does, and Google Test disagrees, you can override it by defining `GTEST_HAS_TR1_TUPLE=1`. See comments in [include/gtest/internal/gtest-port.h](../include/gtest/internal/gtest-port.h) for more information. |

The following statement will instantiate tests from the `FooTest` test case
each with parameter values `"meeny"`, `"miny"`, and `"moe"`.

```cpp
INSTANTIATE_TEST_CASE_P(InstantiationName,
                        FooTest,
                        ::testing::Values("meeny", "miny", "moe"));
```


To distinguish different instances of the pattern (yes, you can
instantiate it more than once), the first argument to
`INSTANTIATE_TEST_CASE_P` is a prefix that will be added to the actual
test case name. Remember to pick unique prefixes for different
instantiations. The tests from the instantiation above will have these
names:

  * `InstantiationName/FooTest.DoesBlah/0` for `"meeny"`
  * `InstantiationName/FooTest.DoesBlah/1` for `"miny"`
  * `InstantiationName/FooTest.DoesBlah/2` for `"moe"`
  * `InstantiationName/FooTest.HasBlahBlah/0` for `"meeny"`
  * `InstantiationName/FooTest.HasBlahBlah/1` for `"miny"`
  * `InstantiationName/FooTest.HasBlahBlah/2` for `"moe"`

This statement will instantiate all tests from `FooTest` again, each with parameter values `"cat"` and `"dog"`:

```cpp
const char* pets[] = {"cat", "dog"};
INSTANTIATE_TEST_CASE_P(AnotherInstantiationName, FooTest,
                        ::testing::ValuesIn(pets));
```

## Typed Tests - same logic for different types
While you can write one `TEST` or `TEST_F` for each type you want to test (and you may even factor the test logic into a function template   you want _m_ tests over _n_ types, you'll end up writing _m\*n_ `TEST`s.


First, define a fixture class template.  It should be parameterized
by a type.  Remember to derive it from `::testing::Test`:

```cpp
template <typename T>
class FooTest : public ::testing::Test {
 public:
  ...
  typedef std::list<T> List;
  static T shared_;
  T value_;
};
```

Next, associate a list of types with the test case, which will be repeated for each type in the list:

```cpp
typedef ::testing::Types<char, int, unsigned int> MyTypes;
TYPED_TEST_CASE(FooTest, MyTypes);
```

The `typedef` is necessary for the `TYPED_TEST_CASE` macro to parse correctly.  Otherwise the compiler will think that each comma in the type list introduces a new macro argument.

Then, use `TYPED_TEST()` instead of `TEST_F()` to define a typed test for this test case.  You can repeat this as many times as you want:

```cpp
TYPED_TEST(FooTest, DoesBlah) {
  // Inside a test, refer to the special name TypeParam to get the type
  // parameter.  Since we are inside a derived class template, C++ requires
  // us to visit the members of FooTest via 'this'.
  TypeParam n = this->value_;

  // To visit static members of the fixture, add the 'TestFixture::'
  // prefix.
  n += TestFixture::shared_;

  // To refer to typedefs in the fixture, add the 'typename TestFixture::'
  // prefix.  The 'typename' is required to satisfy the compiler.
  typename TestFixture::List values;
  values.push_back(n);
  ...
}

TYPED_TEST(FooTest, HasPropertyA) { ... }
```

## Testing Private Code

If you change your software's internal implementation, your tests should not break as long as the change is not observable by users. Therefore, per the _black-box testing principle_, most of the time you should test your code through its public interfaces.

If you still find yourself needing to test internal implementation code, consider if there's a better design that wouldn't require you to do so. If you absolutely have to test non-public interface code though, you can. There are two cases to consider:

  * Static functions (_not_ the same as static member functions!) or unnamed namespaces, and
  * Private or protected class members

### Static code
However, a better approach is to move the private code into the foo::internal namespace, where foo is the namespace your project normally uses, and put the private declarations in a *-internal.h file. Your production .cc files and your tests are allowed to include this internal header, but your clients are not. This way, you can fully test your internal implementation without leaking it to your clients.

### Private Class Members
Another way to test private members is to refactor them into an implementation class, which is then declared in a `*-internal.h` file. Your clients aren't allowed to include this header but your tests can. Such is called the Pimpl (Private Implementation) idiom.

Or, you can declare an individual test as a friend of your class by adding this line in the class body:


```cpp
FRIEND_TEST(TestCaseName, TestName);
```

For example,
```cpp
// foo.h
#include "gtest/gtest_prod.h"

// Defines FRIEND_TEST.
class Foo {
  ...
 private:
  FRIEND_TEST(FooTest, BarReturnsZeroOnNull);
  int Bar(void* x);
};

// foo_test.cc
...
TEST(FooTest, BarReturnsZeroOnNull) {
  Foo foo;
  EXPECT_EQ(0, foo.Bar(NULL));
  // Uses Foo's private member Bar().
}
```


Pay special attention when your class is defined in a namespace, as you should
define your test fixtures and tests in the same namespace if you want them to
be friends of your class. For example, if the code to be tested looks like:

```cpp
namespace my_namespace {

class Foo {
  friend class FooTest;
  FRIEND_TEST(FooTest, Bar);
  FRIEND_TEST(FooTest, Baz);
  ...
  definition of the class Foo
  ...
};

}  // namespace my_namespace
```

Your test code should be something like:

```cpp
namespace my_namespace {
class FooTest : public ::testing::Test {
 protected:
  ...
};

TEST_F(FooTest, Bar) { ... }
TEST_F(FooTest, Baz) { ... }

}  // namespace my_namespace
```

## Running Tests - Advanced Version
### Listing Tests

Sometimes it is necessary to list the available tests in a program before running them so that a filter may be applied if needed. Including the flag `--gtest_list_tests` overrides all other flags and lists tests in the following format:
```cpp
TestCase1.
  TestName1
  TestName2
TestCase2.
  TestName
```

### Selecting Tests To Running

The format of a filter is a '`:`'-separated list of wildcard patterns (called
the positive patterns) optionally followed by a '`-`' and another
'`:`'-separated pattern list (called the negative patterns). A test matches the
filter if and only if it matches any of the positive patterns but does not
match any of the negative patterns.

A pattern may contain `'*'` (matches any string) or `'?'` (matches any single
character). For convenience, the filter `'*-NegativePatterns'` can be also
written as `'-NegativePatterns'`.

For example:

  * `./foo_test` Has no flag, and thus runs all its tests.
  * `./foo_test --gtest_filter=*` Also runs everything, due to the single match-everything `*` value.
  * `./foo_test --gtest_filter=FooTest.*` Runs everything in test case `FooTest`.
  * `./foo_test --gtest_filter=*Null*:*Constructor*` Runs any test whose full name contains either `"Null"` or `"Constructor"`.
  * `./foo_test --gtest_filter=-*DeathTest.*` Runs all non-death tests.
  * `./foo_test --gtest_filter=FooTest.*-FooTest.Bar` Runs everything in test case `FooTest` except `FooTest.Bar`.
