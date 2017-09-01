
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
