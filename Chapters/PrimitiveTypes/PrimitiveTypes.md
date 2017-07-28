# Primitive Types

## Bitwise operation tricks
- Find the right-most (1) bit: `y = x & ~(x-1)`
  - x-1 flips all bits until the right-most 1: x = 10100, x-1 = 10011.
  - Negating it results in 01100, making the numbers before the right-most 1 flip.
  - AND operation on these numbers thus yield 00100.
- Remove the right-most (1) bit: `y = x & (x-1)`
  - Subtracting 1 from the number flips the bits up to the first right-most 1, thus AND with this number sets these bits to 0.

# Examples
1. Count the number of "1s" in the binary representation of a number.
2. Compute the party of a number. (Parity if the number of "1"s is odd, 0 otherwise.) --> Do better than the brute-force O(n)!
