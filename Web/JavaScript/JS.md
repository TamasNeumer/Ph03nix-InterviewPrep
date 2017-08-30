https://babeljs.io/learn-es2015/
http://2ality.com/2014/09/es6-modules-final.html

# JavaScript
##  Promises
The Promise object represents the eventual completion (or failure) of an asynchronous operation, and its resulting value. A promise may be created using its constructor. However, most people are consumers of already-created promises returned from functions.

Essentially, a promise is a returned object you attach callbacks to, instead of passing callbacks into a function.

```js
// Old-School
function successCallback(result) {
  console.log("It succeeded with " + result);
}
function failureCallback(error) {
  console.log("It failed with " + error);
}
doSomething(successCallback, failureCallback);

// New School
let promise = doSomething();
promise.then(successCallback, failureCallback)

// Or simply:
doSomething().then(successCallback, failureCallback);
```

A promise can be in one of three states:
- **pending** — The underlying operation has not yet completed, and the promise is pending fulfillment.
- **fulfilled** — The operation has finished, and the promise is fulfilled with a value. This is analogous to returning a value from a synchronous function.
- **rejected** — An error has occurred during the operation, and the promise is rejected with a reason. This is analogous to throwing an error in a synchronous function.

A promise is said to be settled (or resolved) when it is either fulfilled or rejected. Once a promise is settled, it becomes immutable, and its state cannot change. The `then` and `catch` methods of a promise can be used to attach callbacks that execute when it is settled. These callbacks are invoked with the fulfillment value and rejection reason, respectively.

**Promise Chaining**

A common need is to execute two or more asynchronous operations back to back, where each subsequent operation starts when the previous operation succeeds, with the result from the previous step. We accomplish this by creating a promise chain. The key: **the then function returns a new promise, different from the original**
```js
const promise = doSomething();
const promise2 = promise.then(successCallback, failureCallback);

// Or simply:
let promise2 = doSomething().then(successCallback, failureCallback);

// A bit more complex version:
doSomething().then(function(result) {
  return doSomethingElse(result);
})
.then(function(newResult) {
  return doThirdThing(newResult);
})
.then(function(finalResult) {
  console.log('Got the final result: ' + finalResult);
})
.catch(failureCallback);

// Or short with arrow functions:
doSomething()
.then(result => doSomethingElse(result))
.then(newResult => doThirdThing(newResult))
.then(finalResult => {
  console.log(`Got the final result: ${finalResult}`);
})
.catch(failureCallback);
```




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
