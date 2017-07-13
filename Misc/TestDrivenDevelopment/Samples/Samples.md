# GTest Samples (From GoogleTest Github Repo)

## Sample 1
Sample 1 is a collection if simple tests containing `EXPECT_EQ, EXPECT_GT, EXPECT_FALSE` assertations. Nothing fancy here. Just a warm-up.

## Sample 2
Demonstrates `EXPECT_STREQ` that is used for comparing strings.

## Sample 3
- Code:
  - Demonstrates a templatized queue. (Interesting for a job interview.) Also nice function passing as argument in the Map function.
- Tests:
  - Use of `Fixtures`. `virtual void SetUp() / TearDown()`
  - The testing of `Map` is interesting: The TEST_F only calls a MapTester (part of the Fixture class), that contains the actual assertations. (I'm however not sure if it was necessary...)

## Sample 4
Very simple counter example with a single test. Step back after Sample 3. :-(

## Sample 5
- Introduces inheritance with regard to the fixture classes. Cool!
- We use time limit for tests (5sec)
- We start using the TestCase(...) << "Error message" operation, where we provide additional information.

## Sample 6
- Testing the interface. (yay!)
- `TYPED_TEST_CASE` Types Tests enable us to test the interface with the same function, but with both implementations.
- Example for type-parameterized tests.
