# Node JS Succinctly

## Intro

### What is NodeJS

- **Node.js is an event-driven, single-thread, non-blocking I/O platform for writing applications.**
- **Event driven**
  - This means that the application flow is determined by external actions and it waits for incoming requests.
- **Single threaded**
  - Node.js is single thread; all your applications run on a single thread and it never spawns on other threads.
- **Non-blocking I/O**
  - Every time the application accesses an external resource, for example, to read a file, it doesn’t wait for the file to be completely read. It registers a callback that will be executed when the file is read and in the meantime leaves the execution thread for other tasks. Every time an I/O happens, a callback is registered on a queue and executed when the I/O is completed.

### The event loop

- Remember that Node.js is single thread, so it cannot open a new thread and start to execute the code of the two requests in parallel. It has to wait, or better yet, it puts the event request in a queue and as soon as the previous request is completed it dequeues the next one (whatever it is).
- The event loop is the thing that continues to evaluate the queue in search of new events to execute.
- This is due to the fact that Node is single-thread and if that thread is busy doing something (in this case cycling for nothing), it never returns to the queue to extract the next event:

    ```js
    setInterval(() => console.log('function 1'), 1000)
    setInterval(() => {
      console.log('function 2')
      while (true) { }
      }, 1000)
    console.log('starting')
    ```

### From synchronous to asynchronous

- The first example demonstrates the synchronous variant that blocks the thread while reading the file. Using async callbacks you can make the file reading asynchronous.

    ```js
    // WRONG!
    var fs = require('fs')
    var data = fs.readFileSync('path/to/a/big/file')

    var fs = require('fs')
    fs.readFile('path/to/a/big/file', (err, data) => {
      // do something with data
    })
    ```

### The Node.js runtime environment

- When we run a script using node index.js, Node loads the index.js code and after compiling it, Node executes it from top to bottom, registering the callbacks as needed.
- The script has access to various global objects that are useful for writing our applications. Some of them are:
  - `__dirname` - The name of the folder that the currently executing script resides in.
  - `__filename` - The filename of the script.
  - `console` - Used to print to standard output.
  - `module` - A reference to the current module (more on this later).
  - `require()` - Function used to import a module.

### Event emitters

- To write asynchronous applications, we need the support of tools that enable the callback pattern. In the Node standard library, there is the **EventEmitter** module that exposes the functionality to implement an observer.

    ```js
    // SYNC
    const EventEmitter = require('events').EventEmitter
    let emitter = new EventEmitter()
    emitter.on('newNumber', n => console.log(n * 2))
    for (let i = 0; i < 10; i++) {
        emitter.emit('newNumber', i)
    }

    // ASYNC
    const EventEmitter = require('events').EventEmitter
    let emitter = new EventEmitter()
    emitter.on('newNumber', n => setImmediate(() => console.log(n * 2)))
    for (let i = 0; i < 10; i++) {
       emitter.emit('newNumber', i)
    }
    ```
  - The first line loads the events module and gets the `EventEmitter` function that is used to create a new emitter.
  - The even emitter object has two main methods: `emit` and `on`.
    - `on` is used to **subscribe to a particular event**. It receives an arbitrary event name (`newNumber` in this case) and a callback function that will be executed when the event occurs.
  - Note: There is some confusion around the `setImmediate` and `process.nextTick` functions. They seem similar (and they are) but they have a small, subtle difference. `setImmediate` puts the function in the queue, so if there are other functions in the queue, the `setImmediate` function will be executed after these other functions. `process.nextTick` puts the function at the head of the queue so that it will be executed exactly at the next tick, bypassing other functions in the queue.

### Callback hell

- When you start embedding callbacks inside of other callbacks inside of other callbacks...
- To solve this issue, ECMAScript 6 introduced the concept of **promises**. A promise is a function that will succeed or fail. I used will because the success or failure is not determined at the time of execution, since our code is asynchronous.

## The Node.js Ecosystem

- The require function evaluates the code defined in the specified module and returns the module.exports object.
- Everything defined inside a module is private and doesn’t pollute the global scope except what is assigned to module.exports, and when a module is required, it is cached for better performance.
    ```js
    // file: greeter.js
    module.exports = (who) => { console.log(`Hello ${who}`) }

    // file: otherfile.js
    module.exports = {
        name: 'emanuele',
        surname: 'delbono',
        getFullName: function(){ return `${this.name} ${this.surname}` }
    }
    ```
- In a case where we are requiring a module that is written by us and is not in the node_module folder, we must use the relative path (in this case the module and the main program are in the same folder). If we are requiring a system module or a module downloaded using npm, we simply specify the module name.

### Installing modules

- `npm install express` - This command will download the Express package and all its dependencies in the `node_modules` folder so that they will be available for the scripts in the current folder or any nested folders.
- `npm init` to create projects. This file acts as a sort of project file with some metadata information.
- `npm install express --save` - the dependency information will be stored in the `package.json` file.
- The dependencies in the `package.json` file are usually **split into two groups**: `dependencies` and `devDependencies`. It is a good practice to put the runtime dependencies inside the `dependencies` group and keep the `devDependencies` section for all the packages that are used only during development (for example, for testing, linting code, seeding the database, etc.).
- By default, the script section has one entry dedicated to testing and the default implementation simply echoes a string to the terminal, saying that no test has been configured.

    ```js
    "scripts": { "start": "node ./index.js" },
    ```
- `npm run start` - Node.js will start the index.js file as if we are running directly from the terminal.

a