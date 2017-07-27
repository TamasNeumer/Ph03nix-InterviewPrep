/* Copyright (C) 2017 Tamas Neumer - All Rights Reserved*/
#include "gtest/gtest.h"
#include <iostream>

int NumberOfOnes(unsigned int number) {
  int counter = 0;
  while (number) {
    counter += number & 0x1;
    number >>= 1;
  }
  return counter;
}

class BitTest : public ::testing::Test {
protected:
  virtual void SetUp() {}
  virtual void TearDown() {}
};

TEST_F(BitTest, testForZero) { ASSERT_EQ(0, NumberOfOnes(0)); }
TEST_F(BitTest, testForOtherNumbers) {
  ASSERT_EQ(1, NumberOfOnes(1));
  ASSERT_EQ(1, NumberOfOnes(2));
  ASSERT_EQ(2, NumberOfOnes(3));
  ASSERT_EQ(1, NumberOfOnes(4));
  ASSERT_EQ(2, NumberOfOnes(5));
  ASSERT_EQ(2, NumberOfOnes(6));
  ASSERT_EQ(3, NumberOfOnes(7));
  ASSERT_EQ(1, NumberOfOnes(8));
}

int main(int argc, char *argv[]) {
  testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
