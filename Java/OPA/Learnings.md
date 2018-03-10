# Learnings
- Protected = this class, **package**, classes that extend me.
  - Hence: public < protected < default < private
- Watch out for functional interfaces and lambdas!
- The following is valid:
  ```java
  String course = //this is a comment
              "eJava";
  ```
- Arguments
  - Unlike the declaration of multiple variables, which can be preceded by a single occurrence of their data type, each and every method argument must be preceded by its type.
  - `void myMethod(String s1, s2, int val2)` --> INCORRECT!!!
  - If varargs is used to define method parameters, **it must be the last one**.
- The loop variable in the enhanced for loop refers to a copy of the array or list element.
- If StackOverflow error is caught you **CAN** use the catch to print. (No crash of JVM)
- If you initialize a variable within an if or else-if construct, the compiler can’t be sure whether these conditions will evaluate to true, resulting in no initialization of the local variable.
- **Statements like "ARE GARBAGE COLLECTED" are FALSE** as you never know for sure when these are GC-d.
- StringBuilder has **NO** concat method!
- StringBuilder(5*20) creates a SB **with the capacity** of 100!
- The elements of an ArrayList can’t be added to a higher position if lower positions are available.
- `new Diary().setPageCount(200);` --> valid line even if not assigned to anything.
- `do while` --> **without** braces **only** one line allowed.
- It **will compile** if you cast unrelated classes. Only at runtime it throws exception. Casting = "i know what im doing"
  - `Roamable var = (Roamable)new Phone();` (Unrelated classes)
- A base class can use reference variables and objects of its derived classes.
