
https://babeljs.io/learn-es2015/
http://2ality.com/2014/09/es6-modules-final.html

# JavaScript
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
