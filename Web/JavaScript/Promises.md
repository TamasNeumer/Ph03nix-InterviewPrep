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

```js
const promise = new Promise((resolve, reject) => {
    // Perform some work (possibly asynchronous)
    // ...

    if (/* Work has successfully finished and produced "value" */) {
        resolve(value);
    } else {
        // Something went wrong because of "reason"
        // The reason is traditionally an ERROR OBJECT, although
        // this is not required or enforced.
        let reason = new Error(message);
        reject(reason);

        // Throwing an error also rejects the promise.
        throw reason;
    }
});
```

**Promise Chaining**

A common need is to execute two or more asynchronous operations back to back, where each subsequent operation starts when the previous operation succeeds, with the result from the previous step. We accomplish this by creating a promise chain. The key: **the then function returns a new promise, different from the original**


**The then method of a promise returns a new promise.**

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
Any chained then after a catch will execute its resolve handler using the value resolved from the catch.
```js
const p = new Promise(resolve => {throw 'oh no'});
p.catch(() => 'oh yes').then(console.log.bind(console));  // outputs "oh yes"
```
If there are no catch or reject handlers in the middle of the chain, a catch at the end will capture any rejection in the chain:
```js
p.catch(() => Promise.reject('oh yes'))
  .then(console.log.bind(console))      // won't be called
  .catch(console.error.bind(console));  // outputs "oh yes"
```

**Bulk of promises: promise.all()**  
The `Promise.all()` static method accepts an iterable (e.g. an Array) of promises and returns a new promise, which resolves when all promises in the iterable have resolved, or rejects if at least one of the promises in the iterable have rejected.
```js
// wait "millis" ms, then resolve with "value"
function resolve(value, milliseconds) {
    return new Promise(resolve => setTimeout(() => resolve(value), milliseconds));
}

// wait "millis" ms, then reject with "reason"
function reject(reason, milliseconds) {
    return new Promise((_, reject) => setTimeout(() => reject(reason), milliseconds));
}

Promise.all([
    resolve(1, 5000),
    resolve(2, 6000),
    resolve(3, 7000)    
]).then(values => console.log(values)); // outputs "[1, 2, 3]" after 7 seconds.

Promise.all([
    resolve(1, 5000),
    reject('Error!', 6000),
    resolve(2, 7000)
]).then(values => console.log(values)) // does not output anything
.catch(reason => console.log(reason)); // outputs "Error!" after 6 seconds.
```
