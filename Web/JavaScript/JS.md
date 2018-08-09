
# JavaScript

## Fundamentals

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
- 

### Importing Scripts to HTML

- `<script src="file.js"/>`
- The following also works, however you **CAN'T** mix the two!

  ```js
    <script>
      alert(1);
    </script>`
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
