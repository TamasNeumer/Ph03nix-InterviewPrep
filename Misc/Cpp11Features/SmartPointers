# Smart Pointers
## General
- Traditional pointers need to be deleted explicitly.
- When a smart pointer goes out of scope (with reference count 0) the owned object is deleted --> destructor is called.
- auto_ptr was the first attempt of C++. This is depricated (Cpp 11, 14) and deleted (Cpp 17). auto_ptr had problems:
  - Couldn't be used with containers. (List, vector etc)
  - Didn't work with arrays.
  - After function call ownership was not given back to caller.
- In order to use smart pointers include the header `<memory>`
- If possible use the `make_unique / make_shared` implementation, as it is more efficinet.
## std::unqiue_ptr
- The dynamic object is owned only by one instance. --> Cannot be copied!
- Transfer of ownership via std::move()
- Passing to function also via std::move() (since no copy!)
- In Cpp11 the preferred way to write factory functions is to return by std::unique_ptr --> it makes the ownership clear.
- std::unique_ptr supports arrays via std::make_unique (cpp14)
```cpp
std::unique_ptr<type> ptr = std::make_unique<type>(); // Cpp14
auto ptr2 = std::unique_ptr<type>(new Type());
auto ptr3 = std::move(ptr); //-> ptr becomes nullptr

std::unique_ptr<type> FactoryFunction()
{
  std::unique_ptr<type> ptr = std::make_unique<type>();
  return ptr;
}

std::unique_ptr<int[]> ptr = std::make_unique<int[]>(14); // Array
```

## std::shared_ptr
  - Share ownership among other shared pointers
  - Sharing implemented using reference counting. When counter reaches 0 owned object is destructed.
  - Copy is allowed --> reference count incremented.
  - Until Cpp 14 std::make_shared didn't support arrays + array destructor had to be explicitly defined,
   however since C++17 it is supported.
  - Shared_ptr can point to a sub-object that it owns.
  - Transfer of ownership allowed, via std::move
  - Don't cast normal pointers to shared_ptr. It is a bad practice and can end in corruption.
```cpp
std::shared_ptr<type> ptr = std::make_shared<type>(/*args*/);
auto ptr2 = ptr; // copy assignment
std::shared_ptr<type> ptr3(ptr); // copy constructing

shared_ptr<int> sh(new int[10], std::default_delete<int[]>()); // Cpp14
std::shared_ptr<int[]> sh(new int[10]); // Cpp17

// Point to sub-object
// Both p2 and p1 own the object of type Foo, but p2 points to its int member x.
struct Foo { int x; };
std::shared_ptr<Foo> p1 = std::make_shared<Foo>();
std::shared_ptr<int> p2(p1, &p1->x);
```

## std::weak_ptr - Sharing with temporary ownership
- Instances of std::weak_ptr can point to objects owned by instances of std::shared_ptr
 while only becoming temporary owners themselves. This means that weak pointers do not alter
 the object's reference count and therefore do not prevent an object's deletion if all of
 the object's shared pointers are reassigned or destroyed.
 - A great example for weak_ptr is a Tree. Here in order to enable deletion, upon running out of scope, the childs point to the
parent via std::weak_ptr. When the root node is reset at the end of main(), the root is destroyed.
Since the only remaining std::shared_ptr references to the child nodes were contained in the root's collection children,
 all child nodes are subsequently destroyed as well.
```cpp
struct TreeNode {
    std::weak_ptr<TreeNode> parent;
    std::vector< std::shared_ptr<TreeNode> > children;
};

int main() {
    std::shared_ptr<TreeNode> root(new TreeNode);

    for (size_t i = 0; i < 100; ++i) {
        std::shared_ptr<TreeNode> child(new TreeNode);
        root->children.push_back(child);
        child->parent = root;
    }

    // Reset the root shared pointer, destroying the root object, and
    // subsequently its child nodes.
    root.reset();
}
```
- Use the `use_count()` function, to get the number of strong references.
- Use the `expired()` to see if the object, that is pointed to by the weak_ptr is still alive.
- casting weak to shared: since std::weak_ptr does not keep its referenced object alive, direct data access through a std::weak_ptr is not possible.
Instead it provides a lock() member function that attempts to retrieve a std::shared_ptr to the referenced object:
```cpp
std::weak_ptr<int> wk;
         std::shared_ptr<int> sp;
         {
             std::shared_ptr<int> sh = std::make_shared<int>(42);
             wk = sh;
             // calling lock will create a shared_ptr to the object referenced by wk
             sp = wk.lock();
             // sh will be destroyed after this point, but sp is still alive
         }
         // sp still keeps the data alive.
```

## Additional Problems and Use-cases
### Using custom deleters to create wrapper for C interface (webasto project)
- In order to delete the `SDL_Surface` struct you need to call the `SDL_FreeSurface()` method.
```cpp
std::unique_ptr<SDL_Surface, void(*)(SDL_Surface*)> a(pointer, SDL_FreeSurface);

// OR:

struct SurfaceDeleter {
    void operator()(SDL_Surface* surf) {
        SDL_FreeSurface(surf);
    }
};
std::unique_ptr<SDL_Surface, SurfaceDeleter> a(pointer, SurfaceDeleter{}); // safe

// OR:
std::shared_ptr<Test> sptr1(new Test[5], [](Test* p){delete[] p;});
```
### Common mistakes
1. Using a shared pointer where an unique pointer suffices
2. Thinking that using shared_ptr makes your program threadsafe
3. Not using make_shared / make_unique to initialize pointer
4. Not using custom deleters while working with arrays
5. Not avoiding cyclic references with std::shared_ptr
6. Not deleting a raw pointer after releasing it via ptr.release()
7. Not using threadsafe lock of weak_ptr:
```cpp
std::shared_ptr<type> ptr = myWeakptr.lock();
if(ptr)
{
// This is threadsafe now.
}
```
