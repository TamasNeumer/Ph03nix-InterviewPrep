
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

- “Symbol” value represents a unique identifier.
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

- Symbolic properties do not participate in for..in loop.
- In contrast, `Object.assign` copies both string and symbol properties:


Two objects are equal only if they are the same object.


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
