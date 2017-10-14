## Python Lists [ ]
### General Info
- Lists might contain items of different types, but usually the items all have the same type.
- Lists are a mutable type, i.e. it is possible to change their content.
### Functions and Features
- Instantiation:
  - `a = [1,2,3]`
  - Copy: `b = a` -  here you can use slices of `a`
- Inserting elements
  - `append(value) `– appends a new element to the end of the list.
    - If you append list `b` to list `a` you get an "embedded" list: `[a1 a2 [b1 b2]]`
  - `extend(enumerable) `– extends the list by appending elements from another enumerable.
    - Does the work for two lists (where append "embedded")
  - `+` operator (concatenation)
  - `insert(index, value)` inserts value just before the specified index.
- Accessing elements
  - index(value, [startIndex]) – gets the index of the first occurrence of the input value. If the input value is not in the list a `ValueError` exception is raised. If a second argument is provided, the search is started at that specified index.
  - `pop([index])` – removes and **returns** the item at index. With no argument it removes and returns the last element of the list.
  - Negative indexing allowed [-1] refers to the last element. (Meanwhile first element is [0])
  - Access via slicing: `list[start:end:step]`.
- Removing elements
  - `remove(value)` – removes the first occurrence of the specified value. If the provided value cannot be found, a ValueError is raised.
  - `clear()` – removes all items from the list
  - `del` keyword and slice notation: `del a[::2]` --> every 2nd element
- Sorting
  - `sort()` – sorts the list in numerical and lexicographical order and returns None.
  - if you want to sort by attributes use the key argument.
  - Lists can also be sorted using attrgetter and itemgetter functions from the operator module.
    - Use the attrgetter if you want to sort by attributes of an object,

```py
import datetime
from operator import itemgetter,attrgetter

class Person(object):
    def __init__(self, name, birthday, height):
        self.name = name
        self.birthday = birthday
        self.height = height

    def __repr__(self):
        return self.name

l = [Person("John Cena", datetime.date(1992, 9, 12), 175),
     Person("Chuck Norris", datetime.date(1990, 8, 28), 180),
     Person("Jon Skeet", datetime.date(1991, 7, 6), 185)]

l.sort(key=lambda item: item.name)
# l: [Chuck Norris, John Cena, Jon Skeet]

people = [{'name':'chandan','age':20,'salary':2000},
          {'name':'chetan','age':18,'salary':5000},
          {'name':'guru','age':30,'salary':3000}]
by_age = itemgetter('age')
by_salary = itemgetter('salary')

people.sort(key=by_age) #in-place sorting by age
people.sort(key=by_salary) #in-place sorting by salary

persons = [Person("John Cena", datetime.date(1992, 9, 12), 175),
           Person("Chuck Norris", datetime.date(1990, 8, 28), 180),
           Person("Jon Skeet", datetime.date(1991, 7, 6), 185)]

person.sort(key=attrgetter('name')) #sort by name
by_birthday = attrgetter('birthday')
person.sort(key=by_birthday) #sort by birthday
```
- Iterating:
  - `for item in my_list:`
  - `for (index, item) in enumerate(my_list):`
  - `for i in range(0,len(my_list)):`
- Extra functions:
  - `len()` --> returns length, has O(1) complexity
  - `reverse()` – reverses the list in-place and returns None.
  - `count(value)` – counts the number of occurrences of some value in the list.
  - `*` – replication. (a = [3] --> a * 3 --> [3,3,3])
  - `copy()` – Returns a shallow copy of the list
  - Check for emptiness via `if not lst:`
  - Check if value is in list via `'test' in lst`
  - `zip` returns a list of tuples, where the i-th tuple contains the i-th element from each of the argument sequences or iterables. (2 * 1D vectors --> 1 * 2D Vector)
