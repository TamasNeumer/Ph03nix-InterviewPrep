
# JavaScript

## Fundamentals

### Importing Scripts to HTML

- `<script src="file.js"/>`
- The following also works, however you **CAN'T** mix the two!

  ```js
    <script>
      alert(1);
    </script>`
  ```

### Strict mode

- Strict mode applies to **entire scripts** or to **individual functions**.
- To invoke strict mode for an entire script, put the exact statement `"use strict";` (or `'use strict';`) before any other statements.
- ECMAScript 2015 introduced JavaScript modules and the entire contents of JavaScript modules are automatically in strict mode, with no statement needed to initiate it.

  ```js
    function strict() {
        // because this is a module --> strict
    }
    export default strict;
  ```

### Variables

- Variables can be defined using `let`, `const`, `var`
- Var is considered obsolete
- In old-school non-strict mode you could also create a variable without declaring its "scope" (`mVar = 5`), however this doesn't work in strict mode.
- Naming:
  - Camel case for normal variables
  - UPPERCASE for constants.

### Data types

- A variable in JavaScript can contain any data. Programming languages that allow such things are called **“dynamically typed”**, meaning that there are data types, but variables are not bound to any of them.
- Hence a variable can store:
  - Number
  - String
    - The new Es6 string template is cool: ```alert( `Hello, ${name}!` )```
  - Boolean
  - null - used for "empty"
  - undefined (`let x; alert(x);` --> undefined)
  - objects (see later)
  - symbols - unique identifier for objects
- You can use the `typeof` operator to get the type of a variable
  - Two forms: `typeof x` or `typeof(x)`.
  - Returns a string with the name of the type, like "string".
  - For `null` returns "object" – that’s an error in the language, it’s not an object in fact.

### Type conversions  

- `String(value)`
  - Addition concatenates as string if **any** of the operands are string! e.g.: `alert( 1 + '2' );`
  - But `alert(2 + 2 + '1' ); // "41" and not "221"`
- `Number(value)`
  - `undefined` -> `NaN`
  - `null` -> `0`
  - `true/false` -> `1/0`
  - `string` -> Whitespaces from the start and the end are removed. Then, if the remaining string is empty, the result is `0`. Otherwise, the number is “read” from the string. An error gives `NaN`.
- `Boolean`
  - `0` / `null` / `""` / `NaN` -> `false`.
  - Other values become `true`.
    - `alert( Boolean("0") ); // true`
    - `alert( Boolean(" ") ); // true`
  
### Operators

- String concatenation
  - Addition concatenates as string if **any** of the operands are string! e.g.: `alert( 1 + '2' );`
  - But `alert(2 + 2 + '1' ); // "41" and not "221"`
- Numeric conversion, unary +
  - If the unary `+` is applied to a (single) non-number it is converted to a number: 
    - `alert( +true ); // 1` and `alert( +"" );   // 0`
    - `alert( +apples + +oranges ); // 5`
- Assignment `=` writes the value and returns it: `let c = 3 - (a = b + 1);` works.
- Remainder `%`
- Exponentiation `**`
- Increment/decrement `++/--`
- Bitwise operations
  - AND `&`
  - OR `|`
  - XOR `^`
  - NOT `~`
  - LEFT SHIFT `<<`
  - RIGHT SHIFT `>>`
  - ZERO-FILL RIGHT SHIFT `>>>`
- In-place operations (`+=`, `-=` etc)
- Comma
  - `for (a = 1, b = 3, c = a * b; a < 10; a++) {`
- Logical operators (`!`, `&&`, `||`)
  - OR is a short circuit operator - seeks the first "true" value
  - AND seeks the first "falsy" value
  - A double NOT `!!` is sometimes used for converting a value to boolean type:
    - `alert( !!"non-empty string" ); // true`
    - `alert( !!null ); // false`

### Comparison

- Just like in Java (`== != && > <` etc.)
- String comparison with `> / <` done lexically (ASCII)
  - `"apple" > "pineapple" → false`
  - `"2" > "12" → true`
- When compared values belong to different types, they are converted to **numbers**. (**NOT equality check though! Equality rule doesn't convert!**)
- **A strict equality operator === checks the equality without type conversion.** There also exists a “strict non-equality” operator `!==`, as an analogy for `!=`.
- `null == undefined → true` but `null === undefined → false`
- Values null/undefined are converted to a number: `null` becomes `0`, while undefined becomes `NaN`.
- Be careful when using comparisons like `>` or `<` with variables that can occasionally be null/undefined. Making a separate check for null/undefined is a good idea.

### Interactions

- `alert(str)` -> pop up window in browser
- `result = prompt("text/question", initialInputFieldValue);`
- `confirm("str");` -> ok/cancel, returns boolean

### Conditional operators: if, '?'

- Recap:
  - A number 0, an empty string "", null, undefined and NaN become false. Because of that they are called “falsy” values.
  - Other values become true, so they are called “truthy”.
- Same as in Java. (Ternary as well)

### Loops: while and for

- Same as Java
- You can add **labels** to loops, just like in Java:

  ```js
  outer: for (let i = 0; i < 3; i++) {
    for (let j = 0; j < 3; j++) {
      let input = prompt(`Value at coords (${i},${j})`, '');
      if (!input) break outer; // (*)
    }
  }
  ```

### Switch

- Similar to Java
- Used `===` **strict** equality!

### Functions

- Declaration:

  ```js
  function showMessage(arg1, arg2 = "default value", arg3 = calledIfNotDefined()) {
    alert( 'Hello everyone!' );
  }
  ```

- Watch out for shadowing (function re-declaring variable with "`let`")
- If a parameter is not provided, then its value becomes `undefined`.
- A function with an empty return or without it returns `undefined`
- **NEVER** add new line between return and value!

```js
return; // <-- implicit ; at end of line!
   (some + long + expression + or + whatever * f(a) + f(b))
```

- Javascript has 5 data types that are passed by value: `Boolean, null, undefined, String, Number`. We’ll call these **primitive types**.
- Javascript has 3 data types that are passed by reference: `Array, Function, Object`. These are all technically Objects, so we’ll refer to them collectively as `Objects`.

### Function expressions and arrows

- In JS functions are kind of like values - you can assign these to variables. I.e. `let xyz = `
- A **Function Expression** is created when the execution reaches it and is usable from then on. (`let myfunc = ...`)
- A **Function Declaration** is usable in the whole script/code block.
- A Function Declaration is only visible inside the code block in which it resides.
- You can pass functions to functions as arguments - these serve as your **callback functions**.
- Arrow syntax: `let func = (arg1, arg2, ...argN) => expression`
  - If no argument is given parentheses should be empty.
  - Multi line --> Use curly braces and return.
  - `...argN`is a **variadic argument** representing an array.

## Code quality

### Debugging, Unit testing, Transpilers

- `Cmd + Opt + I` on mac
- `debugger;` to trigger a breakpoint in the code
- Use automatic linters
- `//`, `/* */`

- Unit testing with mocha:
  - `Mocha` – the core framework: it provides common testing functions including describe and it and the main function that runs tests.
  - `Chai` – the library with many assertions. It allows to use a lot of different assertions, for now we need only assert.equal.
  - `Sinon` – a library to spy over functions, emulate built-in functions and more, we’ll need it much later.

    ```js
    describe("pow", function() {
      it("raises to n-th power", function() {
        assert.equal(pow(2, 3), 8);
      });
    });
    ```

- Transpiler
  - Babel is a **transpiler**. It rewrites modern JavaScript code into the previous standard. Modern project build system like webpack or brunch provide means to run transpiler automatically on every code change, so that doesn’t involve any time loss from our side.

## Objects: the basics

### Objects

- An object can be created with figure brackets `{…}` with an optional list of properties. A property is a “key: value” pair, where `key` is a string (also called a “property name”), and `value` can be anything.

  ```js
  let user = new Object(); // "object constructor" syntax
  let user = {};  // "object literal" syntax

  let user = {     // an object
    name: "John",  // by key "name" store value "John"
    age: 30        // by key "age" store value 30
    "likes birds": true  // multiword property name must be quoted
  };

  // Adding new property
  user.isAdmin = true;

  // Deleting property
  delete user.age;

  // multi word property -> use bracket to access!
  // dot doesn't work!
  user["likes birds"] = true;

  // Using square brackets you can get props at runtime!
  // if doesn't exist it is created!
  let key = "likes birds";
  user[key] = true;
  let bag = {
    [fruit]: 5, // the name of the property is taken from the variable fruit
  };
  ```

- For object property names there is no restriction - you can use even the reserved keywords
- In a constructor you often would have `name : name` and stuff like that where you repeat yourself.

    ```js
    function makeUser(name, age) {
      return {
        name, // same as name: name
        age   // same as age: age
        // ...
      };
    }

    let user = {
      name,  // same as name:name
      age: 30
    };
    ```

- Check property for existence:
  - `user.noSuchProperty === undefined`
  - `"key" in object`
- `for(key in object)`
  - integer properties are sorted, others appear in creation order
- The equality `==` and strict equality `===` operators for objects work exactly the same. -> **they check for the equality of the reference**
- Shallow copy

  ```js
  // copies all properties from permissions1 and permissions2 into user
  Object.assign(user, permissions1, permissions2);

  // clone to empty object
  let clone = Object.assign({}, user);
  ```

- For deep copy use a working implementation of it from the JavaScript library `lodash`, the method is called `_.cloneDeep(obj)`.

### Garbage collection

- Any other value is considered reachable if it’s reachable from a root by a reference or by a chain of references.
- JS is clever enough to recognize "islands" i.e. set of objects referencing each other but not reachable from the outside --> it gets GC-d.
- "mark and sweep" algorithm

### Symbols type

- Symbol is a primitive type for unique identifiers.
- `let id = Symbol();` - creation
- `let id = Symbol("id");` - symbol with description
- Symbols dont auto-convert to string implicitly. Use `id.toString()`.
- **Symbols allow us to create “hidden” properties of an object, that no other part of code can occasionally access or overwrite.**

  ```js
  let user = { name: "John" };
  let id = Symbol("id");

  user[id] = "ID Value";
  alert( user[id] ); // we can access the data using the symbol as the key
  ```

- Now if there is another JS library that uses "id" as a field, there would be  a conflict if the field wouldn't be a symbol. But since it is, the two libraries' "id" symbol can co-exist.
- However if you DO want that different parts of your app can see the same symbol, you need to create symbols in the **global symbol registry**.
  - Use: `Symbol.for(key)` - creates a global symbol for a name
  - `Symbol.keyFor(sym)` returns the name for a global symbol
- Symbolic properties do not participate in for..in loop.
- In contrast, `Object.assign` copies both string and symbol properties:
- There are many system symbols used by JavaScript which are accessible as Symbol.*. We can use them to alter some built-in behaviors.

### Object methods, "this"

- Just as you can add a field to an object, you can add a function to it as well:
  - `user.sayHi = function() {alert("Hello!");};`
  - Or using the mehthod shorthand:

    ```js
    let user = {
      sayHi() { // same as "sayHi: function()"
        alert("Hello");
      }
    };
    ```

- To access the object, a method can use the `this` keyword.
- The value of `this` is defined at run-time.
- However watch out for errors:

  ```js
  let user = {
    name: "John",
    hi() { alert(this.name); }
  }

  // split getting and calling the method in two lines
  let hi = user.hi;
  hi(); // Error, because this is undefined 
  ```

  - Here `hi = user.hi` puts the function into the variable, and then on the last line it is completely standalone, and so there’s no `this`.
  - **To make `user.hi()` calls work, JavaScript uses a trick – the dot '.' returns not a function, but a value of the special Reference Type.**
  - The value of Reference Type is a three-value combination (base, name, strict), where:
    - `base` is the object.
    - `name` is the property.
    - `strict` is true if use strict is in effect.
  - The result of a property access `user.hi` is not a function, but a value of Reference Type. For `user.hi` in strict mode it is:
    - `(user, "hi", true)`
  - When parentheses `()` are called on the Reference Type, they receive the full information about the object and its method, and can set the right `this` (`=user` in this case).
  - Any other operation like assignment `hi = user.hi` discards the reference type as a whole, takes the value of `user.hi` (a function) and passes it on. So any further operation “loses” `this`.
  - **So, as the result, the value of this is only passed the right way if the function is called directly using a dot `obj.method()` or square brackets `obj[method]()` syntax (they do the same here).**

### Arrow functions and this

- If we reference `this` from such a function, it’s taken from the outer “normal” function.

  ```js
  let user = {
    firstName: "Ilya",
    sayHi() {
      let arrow = () => alert(this.firstName);
      arrow();
    }
  };

  user.sayHi(); // Ilya
  ```

### Object to primitive conversion

- When an object is used in the context where a primitive is required, for instance, in an alert or mathematical operations, it’s converted to a primitive value using the `ToPrimitive` algorithm (specification).
- To do the conversion, JavaScript tries to find and call three object methods:
  - Call `obj[Symbol.toPrimitive](hint)` if the method exists,
  - Otherwise if hint is "string" try `obj.toString()` and `obj.valueOf()`, whatever exists.
  - Otherwise if hint is "number" or "default" try `obj.valueOf()` and `obj.toString()`, whatever exists.
- The hint can be of 3 types: "string", "number", "default"
- Example:

    ```js
    let user = {
      name: "John",
      money: 1000,

      [Symbol.toPrimitive](hint) {
        alert(`hint: ${hint}`);
        return hint == "string" ? `{name: "${this.name}"}` : this.money;
      }
    };

    // conversions demo:
    alert(user); // hint: string -> {name: "John"}
    alert(+user); // hint: number -> 1000
    alert(user + 500); // hint: default -> 1500
    ```

### Constructor, operator "new"

```js
function User(name) {
  // this = {};  (implicitly)

  // add properties to this
  this.name = name;
  this.isAdmin = false;

  // return this;  (implicitly)
}

let user = new User("Jack");
```

- Steps in the background:
  - A new empty object is created and assigned to this.
  - The function body executes. Usually it modifies this, adds new properties to it.
  - The value of this is returned.

- You can add return statement but:
  - If return is called with object, then it is returned instead of this.
  - If return is called with a primitive, it’s ignored.
- You can omit the parentheses if there is no argument

## Data types in JS

### Methods of primitives

- Primitives are still primitive. A single value, as desired.
- The language allows access to methods and properties of strings, numbers, booleans and symbols.
- When this happens, a special **“object wrapper” is created that provides the extra functionality, and then is destroyed**.
  - For example `myStr.toUpperCase()` works, but know the "price" that had to be payed in the background.
  - The JavaScript engine **highly optimizes** this process. It may even skip the creation of the extra object at all. But it must still adhere to the specification and behave as if it creates one.

### Numbers

- All numbers in JavaScript are stored in 64-bit format IEEE-754, also known as **“double precision”**.
- You can write numbers as `1e9`, `7.3e9`, or `1e-6`.
- Hex/Bin/Oct: `0xff` / `0b11` / `0o3777`
- Two dots to call a method
  - Please note that two dots in `123456..toString(36)` is not a typo. If we want to call a method directly on a number, like `toString` in the example above, then we need to place two dots `..` after it. If we placed a single dot: `123456.toString(36)`, then there would be an error, because JavaScript syntax implies the decimal part after the first dot. And if we place one more dot, then JavaScript knows that the decimal part is empty and now goes the method.
- Methods on numbers:
  - `toString(base)`
  - `Math.floor/ceil/round/trunc`
  - `toFixed(n)` -> rounds the number to n digits after the point and returns a **string representation** of the result. We can convert it to a number using the unary plus or a Number() call: `+num.toFixed(5)`.

### Strings

- The internal format for strings is always **UTF-16**, it is not tied to the page encoding.

  ```js
  let single = 'single-quoted';
  let double = "double-quoted";

  let backticks = `backticks`;

  function sum(a, b) {
    return a + b;
  }

  alert(`1 + 2 = ${sum(1, 2)}.`); // 1 + 2 = 3.

  // Backtics allow multi-line strings

  let guestList = `Guests:
  * John
  * Pete
  * Mary
  `;
  ```

- Please note that `str.length` is a numeric property, not a function.
- Accessing chars with `str[0]` (modern way, returns `undefined` if char not found, `str.charAt(0)` (older, returns empty string if char not found)
- Iterate over chars: `for (let char of "Hello")`
- Strings are **IMMUTABLE** - Strings can’t be changed in JavaScript. It is impossible to change a character. --> create new string by concatenating
- `toLowerCase`, `toUpperCase`
- `str.IndexOf(substr, fromPos)`, `str.lastIndexOf(pos)`
  - In some code ninjas might write `if(~str.indexOf("Widget"))` to find something.
- `str.includes(substr, pos)` returns true/false (instead of index)
- `str.startsWith(substr)`, `str.endsWith(substr)`

## Advanced working with functions

### Rest parameters and spread operator

- A function **can be called with any number of arguments, no matter how it is defined**. There will be no error because of “excessive” arguments. But of course in the result only the first two will be counted (if the function expects two args).
- Rest parameters = `...args` -> gather all args to an array. This must be at the end i.e. last argument.
- `arguments` contains all arguments by their index:

  ```js
  function showName() {
    alert( arguments.length );
    alert( arguments[0] );
    alert( arguments[1] );
  }
  ```

- Arrow functions dont have `arguments`
- **Spread operator** -> passing an array to a function as separate parameters:

  ```js
  let arr = [3, 5, 1];
  alert( Math.max(...arr) );
  ```

### Closure

- In JavaScript, every running function, code block, and the script as a whole have an associated object known as the **Lexical Environment**.
- The Lexical Environment object consists of two parts:
  - **Environment Record** – an object that has all local variables as its properties (and some other information like the value of this).
  - A **reference to the outer lexical environment**, usually the one associated with the code lexically right outside of it (outside of the current curly brackets)
- “Lexical Environment” is a specification object. We can’t get this object in our code and manipulate it directly.
- Function Declarations are special. Unlike `let` variables, they are processed **not** when the execution reaches them, **but when a Lexical Environment is created**. For the global Lexical Environment, it means the moment when the script is started.
- When code wants to access a variable – it is first searched for in the inner Lexical Environment, then in the outer one, then the more outer one and so on until the end of the chain.
- Look at the following example. Here the `count` is always incremented!

  ```js
  function makeCounter() {
    let count = 0;

    return function() {
      return count++; // has access to the outer counter
    };
  }

  let counter = makeCounter();

  alert( counter() ); // 0
  alert( counter() ); // 1
  alert( counter() ); // 2
  ```

  - Can we somehow reset the counter from the code that doesn’t belong to makeCounter? E.g. after alert calls in the example above. --> **There is no way.** The counter is a local function variable, we can’t access it from the outside.
  - If we call makeCounter() multiple times – it returns many counter functions. Are they independent or do they share the same count? --> For every call to `makeCounter()` a new function Lexical Environment is created, with its own `counter`g. So the resulting counter functions are independent.

- For GC the lexical scope also works!!!

  ```js
  function f() {
    let value = 123;

    function g() { alert(value); }

    return g;
  }

  let g = f(); // g is reachable, and keeps the outer lexical environment in memory
  ```

- **V8 optimizations**

  ```js
  function f() {
    let value = Math.random();

    function g() {
      debugger; // in console: type alert( value ); No such variable!
    }

    return g;
  }

  let g = f();
  g();

  // OR

  let value = "Surprise!";

  function f() {
    let value = "the closest value";

    function g() {
      debugger; // in console: type alert( value ); Surprise!
    }

    return g;
  }

  let g = f();
  g();
  ```

  - As you could see – there is no such variable! In theory, it should be accessible, but the engine optimized it out.

### The old var

- `var` has no **BLOCK** scope. It has functions scope though!
- If a code block is inside a function, then var becomes a function-level variable.

  ```js
  if (true) {
    var test = true; // use "var" instead of "let"
  }

  alert(test); // true, the variable lives after if

  // function
  function sayHi() {
    var phrase = "Hello"; // local variable, "var" instead of "let"

    alert(phrase); // Hello
  }

  sayHi();

  alert(phrase); // Error, phrase is not defined
  ```

- `var` declarations are processed when the function starts (or script starts for globals). People also call such behavior **“hoisting”** (raising), because all `var` are “hoisted” (raised) to the top of the function.
- **Declarations are hoisted, but assignments are not.** - i.e. `var myVar = "asd"` -> the "hoisted" `myVar` won't contain the value.

### Global object

- In a browser it is named “window”, for Node.JS it is “global”, for other environments it may have another name.
- Usually, it’s not a good idea to use it, but here are some examples you can meet.

### Function object, Named Function Expression (NFE)

- In JavaScript, functions are objects, and they have properties.
  - `sayHi.name`, where  `function sayHi() {alert("Hi");}` or `let sayHi = function({alert("Hi");}` will print "sayHi".
  - `myFunc.length` -> returns the number of function parameters. Can be used with "variadic" args.
- Functions themselves can have properties:

  ```js
  function sayHi() {
    alert("Hi");

    // let's count how many times we run
    sayHi.counter++;
  }

  sayHi.counter = 0; // initial value

  sayHi(); // Hi
  sayHi(); // Hi

  alert( `Called ${sayHi.counter} times` ); // Called 2 times
  ```

- A property assigned to a function like `sayHi.counter = 0` does not define a local variable `counter` inside it. In other words, a property `counter` and a variable `let counter` are two unrelated things.

- **NFE**
  - If the function is declared as a Function Expression (not in the main code flow), and it carries the name, then it is called a Named Function Expression. The name can be used inside to reference itself, for recursive calls or such.
  - It allows the function to reference itself internally.
  - It is not visible outside of the function.

  ```js
  let sayHi = function func(who) {
    if (who) {
      alert(`Hello, ${who}`);
    } else {
      func("Guest"); // use func to re-call itself
    }
  };
  ```

### New function syntax

- `let func = new Function ([arg1[, arg2[, ...argN]],] functionBody)`

  ```js
  let sum = new Function('a', 'b', 'return a + b');
  alert( sum(1, 2) ); // 3
  ```

  - The major difference from other ways we’ve seen is that the **function is created literally from a string, that is passed at run time**.
  - You can use this to dynamically receive functions and execute it.
- When a function is created using new Function, its `[[Environment]]` references not the current Lexical Environment, but instead the global one.

### Scheduling: setTimeout and setInterval

- `setTimeout` allows to run a function once after the interval of time.
  - `let timerId = setTimeout(func|code, delay[, arg1, arg2...])`
  - `clearTimeout(timerId);` - to clear it
- `setInterval` allows to run a function regularly with the interval between the runs.
  - `let timerId = setInterval(() => alert('tick'), 2000);`
  - `setTimeout(() => { clearInterval(timerId); alert('stop'); }, 5000);`

```js
function sayHi(phrase, who) {
  alert( phrase + ', ' + who );
}
setTimeout(sayHi, 1000, "Hello", "John"); // Hello, John
setTimeout(() => alert('Hello'), 1000);
let timerId = setTimeout("alert('Hello')", 1000); // even from string

clearTimeout(timerId);
```

- **Recursive setTimeout**
  - allows you to (dynamically if wanted) change the delay.
  ```js
  /** instead of:
  let timerId = setInterval(() => alert('tick'), 2000);
  */

  let timerId = setTimeout(function tick() {
    alert('tick');
    timerId = setTimeout(tick, 2000); // (*)
  }, 2000);
  ```

- Note that the `setInterval` **starts** the method in given intervals. If they take longer they may even overlap. With the recursive way, if re-added at the very end, you control the time **between** the end of the current and the start of the next execution.

- Using `setTimeout(funct, 0)` is useful to "schedule" tasks that should be excuted immediately. For example: splitting heavy weight tasks into smaller tasks and schedule them. This way you don't block with the process of the entire task:

  ```js
  let i = 0;
  let start = Date.now();

  function count() {

    // move the scheduling at the beginning
    if (i < 1e9 - 1e6) {
      setTimeout(count, 0); // schedule the new call
    }

    do {
      i++;
    } while (i % 1e6 != 0);

    if (i == 1e9) {
      alert("Done in " + (Date.now() - start) + 'ms');
    }

  }

  count();
  ```



https://babeljs.io/learn-es2015/
http://2ality.com/2014/09/es6-modules-final.html


**`let` keyword**
  - let allows you to declare variables that are **limited in scope to the block**, statement, or expression on which it is used. This is unlike the var keyword, which defines a variable globally, or locally to an entire function regardless of block scope.
    ```js
      function varTest() {
      var x = 1;
      if (true) {
        var x = 2;  // same variable!
        console.log(x);  // 2
      }
      console.log(x);  // 2
    }

    function letTest() {
      let x = 1;
      if (true) {
        let x = 2;  // different variable
        console.log(x);  // 2
      }
      console.log(x);  // 1
    }
    ```


## Binding
The ``bind()`` method creates a new function that, when called, has its this keyword set to the provided value, with a given sequence of arguments preceding any provided when the new function is called.

The simplest use of bind() is to make a function that, no matter how it is called, is called with a particular **this** value.

```js
this.x = 9;    // this refers to global "window" object here in the browser
var module = {
  x: 81,
  getX: function() { return this.x; }
};

module.getX(); // 81

var retrieveX = module.getX;
retrieveX();   
// returns 9 - The function gets invoked at the global scope

// Create a new function with 'this' bound to module
// New programmers might confuse the
// global var x with module's property x
var boundGetX = retrieveX.bind(module);
boundGetX(); // 81
```
- As you can see the `this` keyword is not bound to the class.
- In the first function call you call ``module.getX()`` and thus `this` refers to the module.
- When you create the variable `retrieveX` its value is a function. A function that returns ``this.x``. (`var retrieveX = function(){return this.x}`) However in this context, the this doesn't refer to the class anymore but to the main window.

- Solution:
  - bind the "this" concept to the function. In this case we bound the module, thus when calling the function, the `this.x` will evaluate to `module.x`

## Object.assign()
The ``Object.assign()`` method is used to copy the values of all enumerable own properties from one or more source objects to a target object. It will return the target object. `Object.assign(target, ...sources)`

The ``Object.assign()`` method only copies enumerable and own properties from a source object to a target object. It uses [[Get]] on the source and [[Set]] on the target, so it will invoke getters and setters. Therefore it assigns properties versus just copying or defining new properties.

```js
// Normal copy (assignment)
var obj = { a: 1 };
var copy = Object.assign({}, obj);
console.log(copy); // { a: 1 }

let obj1 = { a: 0 , b: { c: 0}};
let obj2 = Object.assign({}, obj1);
console.log(JSON.stringify(obj2)); // { a: 0, b: { c: 0}}

obj1.a = 1;
console.log(JSON.stringify(obj1)); // { a: 1, b: { c: 0}}
console.log(JSON.stringify(obj2)); // { a: 0, b: { c: 0}}

obj2.a = 2;
console.log(JSON.stringify(obj1)); // { a: 1, b: { c: 0}}
console.log(JSON.stringify(obj2)); // { a: 2, b: { c: 0}}

// Whoops !!!!
obj2.b.c = 3;
console.log(JSON.stringify(obj1)); // { a: 1, b: { c: 3}}
console.log(JSON.stringify(obj2)); // { a: 2, b: { c: 3}}

// Deep Clone
obj1 = { a: 0 , b: { c: 0}};
let obj3 = JSON.parse(JSON.stringify(obj1));
obj1.a = 4;
obj1.b.c = 4;
console.log(JSON.stringify(obj3)); // { a: 0, b: { c: 0}}
```

## Object spread syntax
Spread syntax allows an iterable such as an array expression to be expanded in places where zero or more arguments or elements are expected, or an object expression to be expanded in places where zero or more key-value pairs are expected. Syntax:
- For function calls: ``myFunction(...iterableObj);``
- For array literals: `[...iterableObj, 4, 5, 6];`

```js
function myFunction(v, w, x, y, z) { }
var args = [0, 1];
myFunction(-1, ...args, 2, ...[3]);

var parts = ['shoulders', 'knees'];
var lyrics = ['head', ...parts, 'and', 'toes'];
```

