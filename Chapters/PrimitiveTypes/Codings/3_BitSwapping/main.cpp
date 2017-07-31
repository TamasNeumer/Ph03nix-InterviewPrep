/* Copyright (C) 2017 Tamas Neumer - All Rights Reserved*/
#include "gtest/gtest.h"
#include <iostream>

long swapbits(long x, int i, int j) {
  // 1) Only flip bits if the differ!
  // 2) Swapping means FLIPPING bits in this case!
  // 1^1 --> 0, 0^1--> 1 : Thus we will use XOR!
  if (((x >> i) & 1) != ((x >> j) & 1)) {
    unsigned long bitmask = (1 << i) | (1 << j);
    x ^= bitmask;
  }
  return x;
}

class SwapTest : public ::testing::Test {
protected:
  virtual void SetUp() {}
  virtual void TearDown() {}
};

TEST_F(SwapTest, testForSwaps) {
  ASSERT_EQ(1, swapbits(2, 1, 0));
  ASSERT_EQ(3, swapbits(5, 1, 2));
  ASSERT_EQ(7, swapbits(11, 3, 2));
}

int main(int argc, char *argv[]) {
  testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
