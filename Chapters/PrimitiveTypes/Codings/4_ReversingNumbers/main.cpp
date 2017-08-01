/* Copyright (C) 2017 Tamas Neumer - All Rights Reserved*/
#include "gtest/gtest.h"
#include <iostream>
#include <map>

long swapbits(unsigned long &x, int i, int j) {
  if (((x >> i) & 1) != ((x >> j) & 1)) {
    unsigned long bitmask = (1 << i) | (1 << j);
    x ^= bitmask;
  }
  return x;
}

unsigned long reverseNumberMirror(unsigned long x) {
  // Reversing the number is a swap. (31-0), (30-1) etc.
  // For simplicity with 8 bit numbers here:
  int i = 3;
  int j = 4;
  while (i >= 0) {
    swapbits(x, i, j);
    j++;
    i--;
  }
  return x;
}

// Other solution would be to have 8 bit "batches" precomputed.
// A 32 bit number is 4*8 bits. /y3,y2,y1,y0/ --> reversing this
// is the same as putting y0 in reverse order to the beginnign, and then y1..
std::map<int, int> mymap{{0, 0}, {1, 2}, {3, 3}, {2, 1}};
unsigned long reverseNumberLookup(unsigned long x) {
  unsigned long bitMask = 0x3;
  long wordSize = 2;
  x = (mymap.at(x & bitMask) << (3 * wordSize)) |
      (mymap.at((x >> (wordSize)) & bitMask) << (2 * wordSize)) |
      (mymap.at((x >> (2 * wordSize)) & bitMask) << (1 * wordSize)) |
      (mymap.at((x >> (3 * wordSize)) & bitMask));
  return x;
}

class ReverseTest : public ::testing::Test {
protected:
  virtual void SetUp() {}
  virtual void TearDown() {}
};

TEST_F(ReverseTest, reverseNumberMirrorTest) {
  ASSERT_EQ(128, reverseNumberMirror(1));
  ASSERT_EQ(160, reverseNumberMirror(5));
  ASSERT_EQ(208, reverseNumberMirror(11));
}

TEST_F(ReverseTest, reverseNumberLookup) {
  ASSERT_EQ(128, reverseNumberLookup(1));
  ASSERT_EQ(160, reverseNumberLookup(5));
  ASSERT_EQ(208, reverseNumberLookup(11));
}

int main(int argc, char *argv[]) {
  testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
