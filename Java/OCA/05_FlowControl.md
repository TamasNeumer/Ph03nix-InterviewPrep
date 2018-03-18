# FlowControl

#### if, if-else, tenary constructs
- This condition must always evaluate to a boolean or a Boolean value.
- A variable of any type can be (re)assigned a value in an expression used in an if
- You can’t define the else part for an if construct, skipping the if code block. --> You **must** add body to the if clause, ** only if** you have an else clause!!! (It can be empty even, but you have to add it!)
  - `if((score=score+10) > 110);` is also valid.
- Without braces **only** one line of code is part of the `if`'s body! The second line will execute just like any normal line of code!
  ```java
  String name = "Lion";
  int score = 100;
  if (name.equals("Lion"))
      score = 200;
      name = "Larry"; // executes anyway
  else  // won't compile! (no if found for else clause)
      ...
  ```
- In some cases they give an assignment in the if clause. The value is first assigned and then evaluated. In the following case the if executes.
  ```java
  boolean allow = false;
  if (allow = true)
    ....
  ```
- **Watch out for if/else indentation!** It might seem that a given else clause belongs to another if clause...
```java
int score = 110;
if (score > 200)
    if (score <400)
        if (score > 300)
            System.out.println(1);
        else
            System.out.println(2);
else // belongs to the second if!!!
    System.out.println(3);
```
- You can use a ternary operator, `?:`, to define a ternary construct.
  - Parentheses are optional in the ternary clause.
  - You **must** do assignment: `int discount = (bill > 2000)? getSpecDisc(): getRegDisc();`
    - Otherwise the code is not a statement and won't compile.
    - The methods must return a value of course!
  - Can't include blocks (`{}`) --> won't compile
  - You can nest ternary operators.
    - `int discount = (bill > 1000)? (qty > 11)? 10 : 9 : 5;`

#### switch statement
- When control of the code enters the label matching in the switch construct, **it’ll execute all the code until it encounters a break statement or it reaches the end of the switch statement**.
- When a String object is passed as an argument to a switch construct, it doesn’t
compare the object references; it compares the object values using the equals
- Accepted types:
  - `char byte short int String Integer Short Byte Character enum boolean Boolean`
  - **Double, double, Float, float, long, Long NOT accepted!** --> won't compile
  - Passing **null** results in `NullPointerException`
- Case values
  - Case values should be compile time constants!
    - The sum of 2 final and initialised variables is also compile time constant!
  - `null` is not allowed
- You can define multiple case labels that execute the same block:
  ```java
  switch (score) {
      case 100:
      case 50 :
      case 10 : System.out.println("Average score");
      break;
      /*...*/
    }
  ```
- In the absence of the break statement, control will fall through the remaining code and execute the code corresponding to all the remaining cases that follow that matching case.

#### The for loop
-  A for loop can declare and initialise multiple variables in its initialisation block, but the **variables it declares should be of the same type**.
- The termination condition is evaluated once for each iteration before executing the statements defined within the body of the loop. The for loop terminates when the termination condition evaluates to false.
- Usually, you’d use this block to manipulate the value of the variable that you used to specify the termination condition. Code defined in this block executes after all the code defined in the body of the for loop. You can define multiple statements in the update clause, including calls to other methods.
- All three parts of a for statement—that is, initialization block, termination condition, and update clause—are optional. But you must specify that you aren’t including a section by just including a semicolon.
  - `for(; a < 5; ++a)`
  - `for(int a = 10; ; ++a)`
  - `for(int a = 10; a > 5; )`
  - The semicolon **MUST** be there!
- Enhanced for loop `for (String val : myList)`
  - Manipulation of elements:
    - If you’re iterating through an array of primitive values, manipulation of the loop variable will never change the value of the array being iterated because the primitive values are passed by value to the loop variable in an enhanced for loop.
    - When you iterate through a collection of objects, the value of the collection is passed by reference to the loop variable. Therefore, if the value of the loop variable is manipulated by executing methods on it, the modified value will be reflected in the collection of objects being iterated.
    - The enhanced for loop can’t be used to initialize an array and modify its elements.
    - The enhanced for loop can’t be used to delete the elements of a collection.
    - The enhanced for loop can’t be used to iterate over multiple collections or arrays in the same loop.

#### While, do while
- the `while` loops checks its condition before evaluating its loop body, and the `do-while` loop checks its condition after executing the statements defined in its loop body.
- The while loop accepts arguments of type `boolean` or `Boolean`.
- Don’t forget to use a semicolon (;) to end the do-while loop after specifying its condition. Even some experienced programmers overlook this step!
```java
boolean exitSelected = false;
do {
    /*...*/
} while (exitSelected == false);
```

#### Break and continue
- The break statement is used to exit—or break out of—the for, for-each, do, and do-while loops, as well as switch constructs.
- The continue statement can be used to skip the remaining steps in the current iteration and start with the next loop iteration.
- **Labeled statements**
  - In Java, you can add labels to the following types of statements
    - A code block defined using {}
    - All looping statements (for, enhanced for, while, do-while)
    - Conditional constructs (if and switch statements)
    - Expressions
    - Assignments
    - return statements
    - try blocks
    - throws statements
  - You can’t add labels to declarations.
  ```java
  String[] programmers = {"Outer", "Inner"};
  outer:
  for (String outer : programmers) {
      for (String inner : programmers) {
          if (inner.equals("Inner"))
            break outer;
          System.out.print(inner + ":");
      }
  }
  ```

#### Key takeaways
- Watch out for `while(a++ < 5)` the value is incremented at least once. Also if the content of the loop modifies `a=15`, the increment is still executed (before checking the inequality) hence the final value is 16 in this case.
- `if (a++ > 10)` --> postfix operator, the value is only incremented if the.
- It is only a **compile time constant** if you assign a value to the variable at the definition.
  - `final int a = 5;` --> ok
  - `final int a; a = 5:` --> **NOT** OK!
- In a switch case the **default** can be anywhere!
- This code compiles well. The `for` loop belongs to the if-clause. There is no identation though.
```java
boolean myVal = false;
if (myVal=true)
for (int i = 0; i < 2; i++) System.out.println(i);
else System.out.println("else");
```
