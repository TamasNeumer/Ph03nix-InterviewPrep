# Concurrency

1. A Runnable describes a task that can be executed asynchronously but does not
return a result.2. An ExecutorService schedules tasks instances for execution.
3. A Callable describes a task that can be executed asynchronously and yields a
result.
4. You can submit one or more Callable instances to an ExecutorService
and combine the results when they are available.
5. When multiple threads operate on shared data without synchronization, the result
is unpredictable.
6. Prefer using parallel algorithms and threadsafe data structures over programming
with locks.
7. Parallel streams and array operations automatically and safely parallelize
computations.
8. A ConcurrentHashMap is a threadsafe hash table that allows atomic update of
entries.
9. You can use AtomicLong for a lock-free shared counter, or use LongAdder if
contention is high.
10. A lock ensures that only one thread at a time executes a critical section.
11. An interruptible task should terminate when the interrupted flag is set or an
InterruptedException occurs.
12. A long-running task should not block the user-interface thread of a program, but
progress and final updates need to occur in the user-interface thread.
13. The Process class lets you execute a command

#### Running tasks
In Java, the Runnable interface describes a task you want to run, perhaps concurrently with others.

```java
public interface Runnable {
  void run();
}
```
Like all methods, the run method is executed in a thread. A thread is a mechanismfor executing a sequence of instructions, usually provided by the operating system. Multiple threads run concurrently, by using separate processors or different time slices on the same processor.

In the Java concurrency library, an executor service schedules and executes tasks, choosing the threads on which to run them.

```java
Runnable task = () -> { ... };
ExecutorService executor = ...;
executor.execute(task);
```

- The call `exec = Executors.newCachedThreadPool();` yields an executor service optimized for programs with many tasks that are short lived or spend most of their time waiting. Each task is executed on an idle thread if
possible, but a new thread is allocated if all threads are busy. There is no bound on the number of concurrent threads.

- The call `exec = Executors.newFixedThreadPool(nthreads);` yield a pool with a fixed number of threads. When you submit a task, it is queued up until a thread becomes available. This is a good choice to use for computationally intensive tasks, or to limit the resource consumption of a service. You can derive the number of threads from the number of available processors, which you obtain as `int processors = Runtime.getRuntime().availableProcessors();`

**Futures**
A `Runnable` carries out a task, but it doesn't yield a value. If you have a task that computes a result, use the `Callable<V>` interface instead. Its `call` method,
unlike the `run` method of the `Runnable` interface, returns a value of type `V`:

```java
ExecutorService executor = Executors.newFixedThreadPool();
Callable<V> task = ...;
Future<V> result = executor.submit(task);
```

The Future interface has the following methods:
- `get()`: The get method blocks until the result is available or until the timeout has been reached. That is, the thread containing the call does not progress until the method returns normally or throws an exception. If the `call` method yields a value, the get method returns that value. If the `call` method throws an exception, the get method throws an `ExecutionException` wrapping the thrown exception. If the timeout has been reached, the get method throws a `TimeoutException`.
- `cancel()`: The cancel method attempts to cancel the task. If the task isn't already running, it won't be scheduled. Otherwise, if mayInterruptIfRunning is true, the thread running the task is interrupted.

**Starting multiple tasks at once**
A task may need to wait for the result of multiple subtasks. Instead of submitting each subtask separately, you can use the invokeAll method, passing a Collection of Callable instances.

```java
String word = ...;
Set<Path> paths = ...;
List<Callable<Long>> tasks = new ArrayList<>();
for (Path p : paths) tasks.add(
  () -> { return number of occurrences of word in p });
List<Future<Long>> results = executor.invokeAll(tasks);
// This call blocks until all tasks have completed
long total = 0;
for (Future<Long> result : results) total += result.get();
```

The `invokeAny` method is like `invokeAll`, but it returns as soon as any one of the submitted tasks has completed normally, without throwing an exception. It then returns the value of its Future. The other tasks are cancelled.

#### Asynchronous Computations
When you have a `Future` object, you need to call `get` to obtain the value, blocking until the value is available. The `CompletableFuture` class implements the `Future` interface, and it provides a second mechanism for obtaining the result. You register a callback that will be invoked (in some thread) with the result once it is available.

```java
CompletableFuture<String> f = ...;
f.thenAccept((String s) -> Process the result s);
```

For example, the HttpClient class can fetch a web page asynchronously

```java
HttpClient client = HttpClient.newHttpClient();
HttpRequest request = HttpRequest.newBuilder(new
URI(urlString)).GET().build();
CompletableFuture<HttpResponse<String>> f =
  client.sendAsync(request, BodyHandler.asString());
CompletableFuture<String> f = CompletableFuture.supplyAsync(
  () -> { String result; Compute the result; return result; },
  executor);

f.whenComplete((s, t) -> {
  if (t == null) { Process the result s; }
  else { Process the Throwable t; }
});
```

A `CompletableFuture` can complete in two ways: either **with a result**, or with an **uncaught exception**. In order to handle both cases, use the `whenComplete`
method.

The `isDone` method tells you whether a Future object has been completed (normally or with an exception).

In a usual scenario you start chaining callbacks after each other. Also if you have to handle exceptions the code gets even more dirty. The `CompletableFuture` class solves this problem by providing a mechanismfor composing asynchronous tasks into a processing pipeline.

For example, suppose we want to extract all links from a web page in order to build a web crawler. Let's say we have a method `public void CompletableFuture<String> readPage(URI url)` that yields the text of a web page when it becomes available. If the method `public static List<URI> getLinks(String page)` yields the URIs in an HTML page, you can schedule it to be called when the page is available:

```java
CompletableFuture<String> contents = readPage(url);
CompletableFuture<List<URI>> links =
  contents.thenApply(Parser::getLinks);
```

The `thenApply` method doesn’t block either. It returns another future. When the first future has completed, its result is fed to the `getLinks` method, and the return value of that method becomes the final result.

Now we could dig down and deeper into the methods and params but for now it was enough. :-)

Remember - if you have long running actions such as loading sites on user inputs and you want to maintain a responsible GUI use the `Runnable`:

```java
read.setOnAction(event -> { // Good—long-running action in separate thread
  Runnable task = () -> {
    Scanner in = new Scanner(url.openStream());
    while (in.hasNextLine()) {
    String line = in.nextLine();
    ...
  }
}
executor.execute(task);
});
```

#### Thread safety
**Visibility**
Why wouldn't it be visible? Modern compilers, virtual machines, and processors perform many optimizations. These optimizations assume that the code is sequential unless explicitly told otherwise.

The code `while (!done) i++;` running on thread `II.` will be optimized to `if (!done) while (true) i++;`, as the thread doesn't realize that the value of `done` can change (e.g.: by another thread) **Declaring a variable volatile solves the issue of visibility.**

**Race condition**
You have non-atomic operations, and 2 threads overwrite each other with wrong values. **Solution: Use atomic operatos or locks**

**Strategies for Safe Concurrency**
- Don't share. Count separately on local vars in threads and sum the values at the end.
- Use immutable objects. (i.e. Immutable Classes)
- Locking

#### Parallel algorithms
- Streams can be parallelized via `parallelStream()`
- Parallel Array Operations
  - `Arrays.parallelSetAll` set all elements in array to value in parallelSetAll
  - `Arrays.parallelSort` sort an array of primitive values or objects
    - `Arrays.parallelSort(words, Comparator.comparing(String::length));`

#### Threadsafe data structures
The collections in the java.util.concurrent package have been cleverly implemented so that multiple threads can access them without blocking each other, provided they access different parts.

**Concurrent Hash Maps**
NON thread safe:

```java
ConcurrentHashMap<String, Long> map = new ConcurrentHashMap<>();
...
Long oldValue = map.get(word);
Long newValue = oldValue == null ? 1 : oldValue + 1;
map.put(word, newValue); // Error—might not replace oldValue
```

- Use the `compute` method. The compute method is atomic—no other thread can mutate the map entry while
the computation is in progress. There are also variants `computeIfPresent` and `computeIfAbsent` that only
compute a new value when there is already an old one, or when there isn’t yet one.
  ```java
  map.compute(word, (k, v) -> v == null ? 1 : v + 1);
  ```
- Use the `merge` method hen a key is added for the first time.

```java
map.merge(word, 1L, (existingValue, newValue) -> existingValue +
newValue);
```

**Blocking queue**
One commonly used tool for coordinating work between tasks is a blocking queue. Producer tasks insert items into the queue, and consumer tasks retrieve them. The
queue lets you safely hand over data from one task to another. When you try to add an element and the queue is currently full, or you try to remove an element when the queue is empty, the operation blocks. In this way, the queue balances the workload. If the producer tasks run slower than the consumer tasks, the consumers block while waiting for the results. If the producers run faster, the queue fills up until the consumers catch up.

#### Locks
There are a number of classes in the java.util.concurrent.atomic package that use safe and efficient machine-level instructions to guarantee atomicity of operations on integers, long and boolean values, object references, and arrays thereof.
- `long id = nextNumber.incrementAndGet();`

It is guaranteed that the correct value is computed and returned, even if multiple threads access the same instance concurrently.

```java
public static AtomicLong largest = new AtomicLong();
// In some thread...
largest.set(Math.max(largest.get(), observed)); // Error—race condition!
```
- This update is not atomic. Instead, call `updateAndGet` with a lambda expression for updating the variable. In our example, we can call.
  - `largest.updateAndGet(x -> Math.max(x, observed));` or `largest.accumulateAndGet(observed, Math::max);`


If you anticipate high contention, you should simply use a LongAdder instead of an AtomicLong.

#### Locks
Code that must be executed in its entirety, without interruption, is called a critical section. One can use a lock to implement a critical section:
```java
Lock countLock = new ReentrantLock();
int count; // Shared among multiple threads
...
countLock.lock();
try {
  count++; // Critical section
} finally {
  countLock.unlock(); // Make sure the lock is unlocked
}
```
- Note that, by placing the unlock method into a finally clause, the lock is released if any exception happens in the critical section. Otherwise, the lock would be permanently locked, and no other thread would be able to proceed past it.
- For that reason, application programmers should use locks as a matter of last resort. First try to avoid sharing, by using immutable data or handing off mutable data from
one thread to another. If you must share, use prebuilt threadsafe structures such as a ConcurrentHashMap or a LongAdder. Still, it is useful to know about locks so you can understand how such data structures can be implemented.

**The synchronized Keyword**
- In Java, every object has an intrinsic lock. So it's **not** a prop of the object, which you lock via `obj.intrinsicLock.lock()`. The synchronized keyword is used to lock the intrinsic lock.

```java
synchronized (obj) {
  Critical section
}
```

You can also declare a method as synchronized. Then its body is locked on the receiver parameter `this`.

**Waiting condition**
```java
public synchronized Object take() {
  if (head == null) ... // Now what?
  Node n = head;
  head = n.next;
  return n.value;
}
```
- If the head is null there are no process to fetch from the queue. -> Have to wait until somebody puts there an element. --> **wait()**

```java
public synchronized Object take() throws InterruptedException {
  while (head == null) wait();
  ...
}
```
- Note that the wait method is a method of the Object class. It relates to the lock that is associated with the object.
- The thread is not made runnable when the lock is available.
Instead, it stays deactivated until another thread has called the notifyAll method on the same object.

```java
public synchronized void add(Object newValue) {
  ...
  notifyAll();
}
```

When implementing data structures with blocking methods, the wait, notify, and notifyAll methods are appropriate. But they are not easy to use properly. Application programmers should never have a need to use these methods. Instead, use prebuilt data structures such as LinkedBlockingQueue or ConcurrentHashMap.


#### Threads
**Strting threads**
Starting a thread:
```java
Runnable task = () -> { ... };
Thread thread = new Thread(task);
thread.start();
```
- The static sleep method makes the current thread sleep for a given period: `Thread.sleep(millis);`
- If you want to wait for a thread to finish, call the join method: `thread.join(millis);`

**Thread interrupt**
Each thread has an interrupted status that indicates that someone would like to “interrupt” the thread. A Runnable can check for this status, which is typically done in a loop:
```java
Runnable task = () -> {
  while (more work to do) {
    if (Thread.currentThread().isInterrupted()) return;
    Do more work
  }
};
```
- If the thread is interrupted while it waits or sleeps, it is immediately reactivated—but in this case, the interrupted status is not set. Instead, an InterruptedException is thrown.

**Thread local variables**
Sometimes, you can avoid sharing by giving each thread its own instance, using the `ThreadLocal` helper class.

```java
ublic static final ThreadLocal<NumberFormat> currencyFormat
= ThreadLocal.withInitial(() -> NumberFormat.getCurrencyInstance());

String amountDue = currencyFormat.get().format(total);
```

#### Processes
Start the building process by specifying the command that you want to execute. You can supply a List<String> or simply the strings that make up the command.

Each process has a working directory, which is used to resolve relative directory names. By default, a process has the same working directory as the virtual machine, which is typically the directory from which you launched the java program. You can change it with the directory method:

```java
ProcessBuilder builder = new ProcessBuilder("gcc", "myapp.c");
builder = builder.directory(path.toFile());

Process p = new ProcessBuilder(command).directory(file).start();
```

Next, you will want to specify what should happen to the standard input, output, and error streams of the process. By default, each of them is a pipe that you can access with

```java
OutputStream processIn = p.getOutputStream();
InputStream processOut = p.getInputStream();
InputStream processErr = p.getErrorStream();
```

After you have configured the builder, invoke its start method to start the process. If you configured the input, output, and error streams as pipes, you can now write to
the input stream and read the output and error streams.

```java
Process process = new ProcessBuilder("/bin/ls", "-l")
  .directory(Paths.get("/tmp").toFile())
  .start();
try (Scanner in = new Scanner(process.getInputStream())) {
  while (in.hasNextLine())
  System.out.println(in.nextLine());
}
```

To wait for the process to finish, call `int result = process.waitFor();``or:

```java
long delay = ...;
if (process.waitfor(delay, TimeUnit.SECONDS)) {
  int result = process.exitValue();
  ...
} else {
  process.destroyForcibly();
}
```

Finally, you can receive an asynchronous notification when the process has completed. The call`` process.onExit()`` yields a ``CompletableFuture<Process> ``that you can use to schedule any action.
