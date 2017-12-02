class Solution {
    public int hammingDistance(int x, int y) {
       return countBitsInNumber(x ^ y);
    }


    public int countBitsInNumber(int x) {
        int numberOfBits = 0;
        while (x != 0) {
            if ((x & 0x1) == 1) {
                numberOfBits++;
            }
            x >>= 1;
        }
        return numberOfBits;
    }
}
