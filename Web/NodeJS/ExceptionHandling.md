# Exception Handling

## Basics

- People sometimes write code like this when they want to handle the asynchronous error by invoking the `callback` function and passing the error as an argument. But they make the mistake of thinking that if they `throw` it from their own callback (the function passed to `doSomeAsynchronousOperation`), then it can be caught in the `catch` block. That's not how `try`/`catch` work with asynchronous functions. Recall that **the whole point of an asynchronous function is that it's invoked some time later, after `myApiFunc` returns**. That means the `try` block has been exited. **The callback is invoked directly by Node, with no try block around it.** So if you use this anti-pattern, you'll end up crashing the program when you `throw` the error. Even in the case of an explicit `async` function that uses an `await` in the `try` block, an error thrown asynchronously won't be caught. Below is an example of an error that won't be caught.

```js
function myApiFunc(callback) {
  /* * This pattern does NOT work! */
  try {
    doSomeAsynchronousOperation(err => {
      if (err) {
        throw err;
      }
      /* continue as normal */
    });
  } catch (ex) {
    callback(ex);
  }
}
```

- You should also be familiar with the four main ways to deliver an error in Node.js:

  - throw the error (making it an exception).
  - pass the error to a callback, a function provided specifically for handling errors and the results of asynchronous operations
  - pass the error to a reject Promise function
  - emit an "error" event on an EventEmitter

- You can throw strings in JavaScript, but if you do it in Node.js, you’ll lose all the stack information and the rest of the properties that are contained in the error object.

```js
/**
 * A custom MyError class
 * @class
 */
class MyError extends Error {
  /**
   * Constructs the MyError class
   * @param {String} message an error message
   * @constructor
   */
  constructor(message) {
    super(message);
    // properly capture stack trace in Node.js
    Error.captureStackTrace(this, this.constructor);
    this.name = this.constructor.name;
  }
}
```

- Using the emitter is also simple

```js
const EventEmitter = require("events");

class Emitter extends EventEmitter {}

const emitter = new Emitter();
const logger = console;

/**
 * Add Error listener
 */
emitter.on("error", err => {
  logger.error("Unexpected error on emitter", err);
});

// test the emitter
emitter.emit("error", new Error("Whoops!"));
// Unexpected error on emitter Error: Whoops!
```

## Operational errors vs. programmer errors

- Operational errors represent run-time problems experienced by correctly-written programs. (i.e. no DB connection, file not found, bad config etc.)
- Programmer errors are bugs in the program. (Passed string where object was expected etc.)
