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

## Filesystem and Streams

- The fs module is a sort of wrapper around the standard POSIX functions and exposes a lot of methods to access and work with the filesystem. Most of them are in two flavors: the **default async version** and the **sync version**. For FileIO we have seen the async/sync examples before.

- Writing a file is somewhat similar:

    ```js
    // Writing

    const fs = require('fs')
    fs.writeFile('/path/to/file', data, (err) => { 
      // check error
    })

    // Watching

    const watcher = fs.watch('/path/to/folder')
    watcher.on('change', function(event, filename) {
      console.log(`${event} on file ${filename}`)
    })
    ```

- The path module also offers cool OS independent features.

    ```js
    const path = require('path')
    const fullPath = path.join('/path/to/folder', 'README.md')
    const fullPath2 = path.join(__dirname, 'README.md')
    ```

- The previously seen file readers were not very efficient as they only return once the complete file is read. --> Solution: Streams! They **emit events when a chunk of data is available** so that the consumers can start using it. Two main events are `data` and `end`, which are raised when a chunk of data is ready to be used and when the stream has finished, respectively.

    ```js
    const fs = require('fs');
    const http = require('http');
    const server = http.createServer((request, response) => {
      response.writeHead(200, {'Content-Type': 'text/html'});
      var stream = fs.createReadStream('./index.html');
      stream.pipe(response);
    });
    server.listen(8000);
    ```
  - Since the response object is a stream, we can pipe the readable stream to response so that the server can start serving chunks of index.html as soon as they are available.
- Writable streams are the counterpart of readable ones. They can be created with the function createWriteStream and they are streams on which we can pipe something. For example, we can use a writable stream to copy a file:

    ```js
    const fs = require('fs');
    var sourceFile = fs.createReadStream('path/to/source.txt');
    var destinationFile = fs.createWriteStream('path/to/dest.txt');
    sourceFile.on('data', function(chunk) {
      destinationFile.write(chunk);
    });
    ```

## Writing Web Applications

### HTTP
- The `request` object contains all the information about the request, so we could use it to decide what kind of response we have to send to the client.
- The `response` object is used to build the response. We are using it to add the status code (200) and the Content-Type header.

    ```js
    const http = require('http')
    const server = http.createServer((request, response) => {
        response.writeHead(200, { 'Content-Type': 'text/html' })
        if (request.url === '/about')
            { response.write('<h1>About Node.js</h1>')
        } else {
            response.write('<h1>Hello from Node.js/h1>')
        }
        response.end();
    })
    server.listen(8000)
    ```

### Express

- `npm install express`
    ```js
    const express = require('express')
    const app = express()
    app.get('/', (req, res) => { res.send('Hello World!') });
    app.put('/users/:id', (req, res) => { // update the user with specified id });
    app.get('/users', (req, res) => { 
      const users = [{id: 1, name: 'Emanuele'}, {id: 2, name: 'Tessa'}]
      res.send(users)
    })
    app.listen(8000, () = > { console.log('Example app listening on port 8000!'); });
    ```
- On an `app` object, we attach the various routes specifying the method used.
- Notice the path variable. Inside the request we will find the value of `id` inside `req.params.id`.
- Tip: Even if `res.send` converts the object in JSON format, response has a JSON method that explicitly specifies the content type to `application/json`.
- If we need to specify a particular status code, we can use `res.status(code)` where code is the actual status code that we want. By default, it’s 200. Note that the `send` and `json` methods are overloaded so that you can send the content and the status code at the same time.
  - `res.json(200, users) res.send(200, users)`