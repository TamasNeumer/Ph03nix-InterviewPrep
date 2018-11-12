# Promises, async/await

## Callbacks

Many actions in JavaScript are asynchronous. So is the `loadScript(src)` function:

```js
function loadScript(src) {
  let script = document.createElement("script");
  script.src = src;
  document.head.append(script);
}
```

Due to this calling an function that was "imported" using `loadScript` won't work.

```js
loadScript("/my/script.js"); // the script has "function newFunction() {…}"
newFunction(); // no such function! -> loadScript is running parallel, and didn't finish yet!
```

Let's add a callbacks to `loadScript` and use the loaded functionality in the callback!

```js
function loadScript(src, callback) {
  let script = document.createElement("script");
  script.src = src;

  script.onload = () => callback(null, script);
  script.onerror = () => callback(new Error(`Script load error for ${src}`));

  document.head.append(script);
}

loadScript("https://cdnjs.cloudflare.com/ajax/libs/lodash.js/3.2.0/lodash.js", function(
  error,
  script
) {
  if (error) {
    // handle error
  } else {
    // script loaded successfully
  }
});
```

That’s called a “callback-based” style of asynchronous programming. A function that does something asynchronously should provide a `callback` argument where we put the function to run after it’s complete. It’s called the “error-first callback” style, as we are calling the error callback first.

The only problem arises when you use multiple async functions in the callbacks. ("Callback hell")

```js
loadScript("1.js", function(error, script) {
  if (error) {
    handleError(error);
  } else {
    // ...
    loadScript("2.js", function(error, script) {
      if (error) {
        handleError(error);
      } else {
        // ...
        loadScript("3.js", function(error, script) {
          if (error) {
            handleError(error);
          } else {
            // ...continue after all scripts are loaded (*)
          }
        });
      }
    });
  }
});
```

## Promises

A promise is a special JavaScript object that links the “producing code” and the “consuming code” together.The Promise object represents the eventual completion (or failure) of an asynchronous operation, and its resulting value. A promise may be created using its constructor. However, most people are consumers of already-created promises returned from functions.

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
promise.then(successCallback, failureCallback);

// Or simply:
doSomething().then(successCallback, failureCallback);
```

A promise object has two internal properties:

- `state` — initially “pending”, then changes to either “fulfilled” or “rejected”
- `result` — an arbitrary value of your choosing, initially `undefined`.

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

When the executor finishes the job, it should call one of the functions that it gets as arguments:

- `resolve(myValue)` — to indicate that the job finished successfully:
  - sets `state` to "`fulfilled`",
  - sets `result` to `myValue`.
- `reject(myError)` — to indicate that an error occurred:
  - sets `state` to "`rejected`",
  - sets `result` to `myError`.

The executor should call only one resolve or reject. The promise’s state change is **final**. All further calls of `resolve` and `reject` are ignored. It is also recommended to use `Error` objects (or objects that inherit from `Error`).

Now that we have created the Promise in the previous snippet, we can attach "consumers" with `.then()` and `.catch()`. The syntax is the following:

```js
promise.then(
  function(result) {
    /* handle a successful result */
  },
  function(error) {
    /* handle an error */
  }
);
```

You will often use `.catch(f)` which is a complete analog of `.then(null, f)`. Same for `.then(f)` which is analog for `.then(f, null)`

Handlers of `.then`/`.catch` are **always asynchronous**! Even when the Promise is immediately resolved, code which occurs on lines below your `.then`/`.catch` may still execute first! (The JavaScript engine has an internal execution queue which gets all `.then`/`.catch` handlers. But it only looks into that queue when the current (code block) execution is finished.)

Rewriting the older code:

```js
function loadScript(src) {
  return new Promise(function(resolve, reject) {
    let script = document.createElement("script");
    script.src = src;

    script.onload = () => resolve(script);
    script.onerror = () => reject(new Error("Script load error: " + src));

    document.head.append(script);
  });
}

let promise = loadScript("https://cdnjs.cloudflare.com/ajax/libs/lodash.js/3.2.0/lodash.js");

promise.then(
  script => alert(`${script.src} is loaded!`),
  error => alert(`Error: ${error.message}`)
);

promise.then(script => alert("One more handler to do something else!"));
```

## Promise Chaining

A common need is to execute two or more asynchronous operations back to back, where each subsequent operation starts when the previous operation succeeds, with the result from the previous step. We accomplish this by creating a promise chain. The key: **the then function returns a new promise, different from the original**

```js
const promise = doSomething();
const promise2 = promise.then(successCallback, failureCallback);

// Or simply:
let promise2 = doSomething().then(successCallback, failureCallback);

// A bit more complex version:
doSomething()
  .then(function(result) {
    return doSomethingElse(result);
  })
  .then(function(newResult) {
    return doThirdThing(newResult);
  })
  .then(function(finalResult) {
    console.log("Got the final result: " + finalResult);
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

Any chained `.then` after a `.catch` will execute its resolve handler using the value resolved from the `.catch`.

```js
const p = new Promise(resolve => {
  throw "oh no";
});
p.catch(() => "oh yes").then(console.log.bind(console)); // outputs "oh yes"
```

If there are no `.catch` or reject handlers in the middle of the chain, a `.catch` at the end will capture any rejection in the chain:

```js
p.catch(() => Promise.reject("oh yes"))
  .then(console.log.bind(console)) // won't be called
  .catch(console.error.bind(console)); // outputs "oh yes"
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

Promise.all([resolve(1, 5000), resolve(2, 6000), resolve(3, 7000)]).then(values =>
  console.log(values)
); // outputs "[1, 2, 3]" after 7 seconds.

Promise.all([resolve(1, 5000), reject("Error!", 6000), resolve(2, 7000)])
  .then(values => console.log(values)) // does not output anything
  .catch(reason => console.log(reason)); // outputs "Error!" after 6 seconds.
```
