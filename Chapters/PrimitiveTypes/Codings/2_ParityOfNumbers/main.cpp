/* Copyright (C) 2017 Tamas Neumer - All Rights Reserved*/
#include "gtest/gtest.h"
#include <iostream>

/*
* Brute-force method. O(n) complexity, where n is the number of bits in x.
*/
short ParityBruteforce(unsigned long x) {

  short parity = 0;
  while (x) {
    parity ^= (x & 0x1);
    x >>= 1;
  }
  return parity;
}

/*
* Idea: Mask out the first "1" from the right, thus skipping the zeros.
* Complexity: O(s), where s is the number of set bits.
*/
short ParityOfNumberII(unsigned long number) {
  short result = 0;
  while (number) {
    result ^= 1;
    number &= number - 1;
  }
  return result;
}

/*
* Idea: XOR is associative and commutative. --> Parity of (11011011) same as
* of [(1101) XOR (1011)]. --> This yields a 4 bit number (1101). The parity if
* (1101) is the same as of [(11) XOR (01)] --> This yields a 2 bit number (10).
* The same process for (10) --> 1 XOR 0 --> Parity is 1! O(logN) complexity.
*/
short PartyOfNumberClever(unsigned long number) {
  number = (number >> 32) ^ number;
  number = (number >> 16) ^ number;
  number = (number >> 8) ^ number;
  number = (number >> 4) ^ number;
  number = (number >> 2) ^ number;
  number = (number >> 1) ^ number;
  return number & 0x1;
}

/*
* Another solution could be to split the 64 bit number into 4 16 bit chunks,
* and then fetch the parity of these 16 bit numbers from a hash. Then the
* 4 results can be siply XORed to yield the result.
*/

class ParityTest : public ::testing::Test {
protected:
  virtual void SetUp() {}
  virtual void TearDown() {}
};

TEST_F(ParityTest, testForZeroBruteForce) { ASSERT_EQ(0, ParityBruteforce(0)); }
TEST_F(ParityTest, testForOtherNumbersBruteForce) {
  ASSERT_EQ(1, ParityBruteforce(1));
  ASSERT_EQ(1, ParityBruteforce(2));
  ASSERT_EQ(0, ParityBruteforce(3));
  ASSERT_EQ(1, ParityBruteforce(4));
  ASSERT_EQ(0, ParityBruteforce(5));
  ASSERT_EQ(0, ParityBruteforce(6));
protected
  ASSERT_EQ(1, ParityBruteforce(7));
  ASSERT_EQ(1, ParityBruteforce(8));
}

TEST_F(ParityTest, testForZeroClever) { ASSERT_EQ(0, PartyOfNumberClever(0)); }
TEST_F(ParityTest, testForOtherNumbersClever) {
  ASSERT_EQ(1, PartyOfNumberClever(1));
  ASSERT_EQ(1, PartyOfNumberClever(2));
  ASSERT_EQ(0, PartyOfNumberClever(3));
  ASSERT_EQ(1, PartyOfNumberClever(4));
  ASSERT_EQ(0, PartyOfNumberClever(5));
  ASSERT_EQ(0, PartyOfNumberClever(6));
  ASSERT_EQ(1, PartyOfNumberClever(7));
  ASSERT_EQ(1, PartyOfNumberClever(8));
}

TEST_F(ParityTest, testForZeroII) { ASSERT_EQ(0, ParityOfNumberII(0)); }
TEST_F(ParityTest, testForOtherII) {
  ASSERT_EQ(1, ParityOfNumberII(1));
  ASSERT_EQ(1, ParityOfNumberII(2));
  ASSERT_EQ(0, ParityOfNumberII(3));
  ASSERT_EQ(1, ParityOfNumberII(4));
  ASSERT_EQ(0, ParityOfNumberII(5));
  ASSERT_EQ(0, ParityOfNumberII(6));
  ASSERT_EQ(1, ParityOfNumberII(7));
  ASSERT_EQ(1, ParityOfNumberII(8));
}

int main(int argc, char *argv[]) {
  testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
