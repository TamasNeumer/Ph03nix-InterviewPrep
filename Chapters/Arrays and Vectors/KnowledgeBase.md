# Arrays and Vectors

## C++ Vector (std::vector)
### General Info
- Can change size dynamically --> Thus allocated on the **heap**
- The data is stored continuously, **except for std::vector<bool>**
- Accessing is constant in time O(1)
### Functions & Features
- Initializing:
  - `std::vector<int> myVec {1,2,3};` --> [1,2,3]
  - `std::vector<int> myVec(4);` --> [0,0,0,0]
  - `std::vector<int> myvec(2,3)` --> [3,3]
  - Copy assignment `v1 = v2` and copy ctor `std::vector<int> v(v2);`
  - Move construction / assignment `std::vector<int> v(std::move(v2)); //
  std::vector<int> v = std::move(v2);`
  - Iterator move-construction: `std::vector<int> v(std::make_move_iterator(v2.begin()),
    std::make_move_iterator(v2.end());`
  - `assign()` can be used to re-initialize the vector after construction
- Accessing members via subscript operator `[]`, or the member function `at()`.
  - `[]` doesn't perform any boundary checking, while `at()`throws a
[std::out_of_range](http://en.cppreference.com/w/cpp/error/out_of_range) exception.
Boundary checking however has a price in performance! (Don't use it if you know that
you remain inside the range e.g.: Looping through with for-loop until vector.size())
  - Accessing is constant in time.
  - `front()` and `back()` allow access to front and last elements.
    - Undefined behavior (Segmentation fault) if calling in on an empty
   vector --> check with `empty()` first.
  - `data()` The data() method returns a pointer to the raw memory used by the std::vector
    - `int a* = myVec.data();`
  - `begin()` and `end()` return iterators to the first and last element.
- Finding elements via `std::find()` and `std::find_if()`
  - The function std::find, defined in the <algorithm> header, can be used to find an element in a std::vector.
  - std::find uses the operator== to compare elements for equality. It returns an iterator to the first element in the range that compares equal to the value. If the element in question is not found, std::find returns std::vector::end (or std::vector::cend if the vector is const).
    - `std::vector<int>::iterator it = std::find(v.begin(), v.end(), 4);
std::vector<int>::difference_type index = std::distance(v.begin(), it);`
  - std::find_if accepts a third argument which is a function object or function pointer to a predicate function
    - `auto it = std::find_if(v.begin(), v.end(), [](int val){return val % 2 == 0;});`
  - Downside of `std::find()` is that for large vectors it is inefficient.
    1. Sort the vector using `std::sort(v.bein(), v.end())`
    2. Use std::lower_bound() that does a binary search on the sorted vector. (`std::vector<int>::iterator it = std::lower_bound(v.begin(), v.end(), 42);`)
    - If the requested value is not part of the vector, `std::lower_bound()` will return an iterator to the first element that is greater than the requested value. This behavior allows us to insert a new element at its right place in an already sorted vector.
    - If you need to insert a new element after the last element of the searched value, you should use the function `std::upper_bound()`
  - To find the largest or smallest element stored in a vector, you can use the methods `std::max_element` and `std::min_element`
```cpp
int maxElementIndex = std::max_element(v.begin(),v.end()) - v.begin();
int maxElement = *std::max_element(v.begin(), v.end());
auto minmax = std::minmax_element(v.begin(), v.end());
std::cout << "minimum element: " << *minmax.first << '\n';
std::cout << "maximum element: " << *minmax.second << '\n';
```
- Removing Elements
  - `pop_back()` --> deletes last element
  - `clear()` --> deleting all elements
    - Deleting all elements using v.clear() does not free up memory (capacity() of the vector remains unchanged).
    - v.shrink_to_fit(); --> frees up unused vector capacity.
A std::vector automatically increases its capacity upon insertion as needed, but it never reduces its capacity after element removal.
  - `erase(_iterator1_, _iterator2_)` --> erasing element **by index** (1 arg) or by range (2args)
    - For a vector deleting an element which is not the last element, all elements beyond the deleted element have to be copied or moved to fill the gap
    - erasing doesn't change the capacity, only the size
  - To erase **by value** the *erase-remove* idiom is used.
    - First `std::remove` moves some elements to the end of the vector, and then `erase()` chops them off.
  - Deleting elements by condition: `std::remove_if(startIt,endIt, condition)`
```cpp
// Remove by value: erase-remove idiom
std::vector<int> v{ 1, 1, 2, 2, 3, 3 };
int value_to_remove = 2;
v.erase(std::remove(v.begin(), v.end(), value_to_remove), v.end());

// Remove by "expression"
std::vector<int> v{ 1, 2, 3, 4, 5, 6 };
v.erase(std::remove_if(v.begin(), v.end(),
     [](auto& element){return element > 3;} ), v.end());
```
- Reserving space for elements
  - Using 1000 times `push_back()` is inefficient. You can manually reserve capacity by` reserve( N )` function (it changes vector capacity to N). --> `v_good.reserve( 10000 );`
- Iterating over a vector:
  - Range based `for(const auto& elem : myvec)`
  - Iterator `for(auto it = std::begin(v); it != std::end(v); ++it)`
    - You can use constant iterators to enforce const correctness `cbegin()`, `cend()`
  - for_each with "function" `std::for_each(std::begin(v), std::end(v), fun);` where fun has the signature void fun(int const& value).
  - Index: `for(std::size_t i = 0; i < v.size(); ++i)`
    - **Use size_t** --> std::size_t is the type of any sizeof expression and as is guaranteed to be able to express the maximum size of any object (including any array) in C++.
- Inserting into a vector
  - `push_back(p)` --> p is **copied** to the vector
    - std::vector does not have a `push_front()` member function due to performance reasons. (Would move all elements) --> use std::list<int>
  - `emplace_back()` --> constructing element in place
      - `std::vector<Point> v; v.emplace_back(10.0, 2.0);`
  - `insert(posIt, value)` --> insert (**by copy**) the value to the position pointed by the iterator
  - `emplace(posIt, value)` --> in place insertion
  - Insert slices of another vector: `v.insert(v.begin()+2, v2.begin(), v2.end());` or use the standard functions `a.insert(std::end(a), std::begin(b), std::end(b));`
- Matrices with Vectors:
  - `std::vector<std::vector<int> > matrix(3, std::vector<int>(4));` or via initializer list. (In the case of a 2x2 matrix: {{1,2},{3,4}} )


### Compatibility with C Arrays
- `int* p = v.data();` should work fine, however if you change the vector (i.e resize it) the data is moved and thus you have an invalid pointer.
- Converting C arrays to Vectors:
```cpp
int values[5] = { 1, 2, 3, 4, 5 }; // source array
std::vector<int> v(std::begin(values), std::end(values)); // copy array to new vector
```

### std::vector<bool> - the Exception
- Since bits aren't addressable in C++, this means that several requirements on vector are not placed on vector<bool>
  - The data stored is not required to be contiguous, so a `vector<bool>` can't be passed to a C API which expects a bool array.
  - `at()`, operator `[]`, and dereferencing of iterators do not return a reference to `bool`. Rather they return a proxy object that (imperfectly) simulates a reference to a `bool` by overloading its assignment operators.
- In the traditional version (e.g. `std::vector<char> v{true,false};`) would have allocated two bytes of memory. [00000001] [00000000]. Meanwhile the optimized C++ version does this in 1 byte. [00000010]

## C++ Array (std::array)
- Initialization:
  - Almost same as vectors, but type takes 2 args:
    - `std::array<int, 3> a{ 0, 1, 2 };`
    - `std::array<int, 3> a2(a);` // copy
    - `std::array<int, 3> a = std::array<int, 3>{ 0, 1, 2 }; `// move
- Accessing elements
  - `[]` and `at(index)` --> same as vector
  - `front() back()` --> same
  - `data()` --> same
- Important differences:
  - Array's size is Fixed. --> Must be a compile time constant, or you can dynamically allocate it, but in this case you must **delete** it via `delete [] arrayName`. Once allocated cannot grow / shrink.
