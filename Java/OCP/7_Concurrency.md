# Concurrency

#### Introducing Threads
- **Terminology**
  - A *thread* is the smallest unit of execution that can be scheduled by the operating system.
  - A *process* is a group of associated threads that execute in the same, shared environment.
  - By *shared environment*, we mean that the threads in the same process share the same memory space and can communicate directly with one another.
  - A *task* is a single unit of work performed by a thread.
- **Distinguishing thread types**
  - A *system thread* is created by the JVM and runs in the background of the application.
  - A *user-defined thread* is one created by the application developer to accomplish a specific task.
- **Understanding Thread Concurrency**
  - The property of executing multiple threads and processes at the same time is referred to as *concurrency*.
  - Operating systems use a *thread scheduler* to determine which threads should be currently executing.
  - A *context switch* is the process of storing a thread’s current state and later restoring the state of the thread to continue execution.
  - A *thread priority* is a numeric value associated with a thread that is taken into consideration by the thread scheduler when determining which threads should currently be executing.
    - If two threads have the same priority, the thread scheduler will arbitrarily choose the one to process first in most situations.
- **Introducing Runnable**
  - `@FunctionalInterface public interface Runnable { void run();}`
  - Don't return a value! The function also doesn't take any arguments.
  - Note that you can also implement a function in a class, it doesn't have to be a lambda necessarily.
- **Creating a thread**
  - First you define the Thread with the corresponding task to be done. Then you start the task by using the `Thread.start()` method.
  - It may be executed immediately or delayed for a significant amount of time. Remember that order of thread execution is not often guaranteed. The exam commonly presents questions in which multiple tasks are started at the same time, and you must determine the result.
  - `(new Thread(new PrintData())).start();` -- passing a class as argument, that implements the `Runnable` interface.
  - On the exam, be careful about cases where a `Thread` or `Runnable` is created but no `start()` method is called. (They might call a `run()` method, that however executes sequentially.)
- **Polling with Sleep**
  - *Polling* is the process of intermittently checking data at some fixed interval.
  - When checking if a given thread is ready definitely apply some sleep!
  - `Thread.sleep()` throws the checked `InterruptedException`, hence the method should declare `throws`!!!

    ```java
    new Thread(() -> {
      for(int i=0; i<500; i++) 
        CheckResults.counter++;
      }).start();
    while(CheckResults.counter<100) {
      System.out.println("Not reached yet");
      Thread.sleep(1000); // 1 SECOND
    }
    ```

#### Creating Threads with the ExecutorService

- **Info**
  - `ExecutorService`, which creates and manages threads for you.
  - The framework includes numerous useful features, such as thread pooling and scheduling, which would be cumbersome for you to implement in every project.
- **Introducing the Single-Thread Executor**
  - Since `ExecutorService` is an interface, how do you obtain an instance of it? The Concurrency API includes the Executors factory class that can be used to create instances of the `ExecutorService` object.

    ```java
    try {
    ExecutorService service = null;
    service = Executors.newSingleThreadExecutor();
    System.out.println("begin");
    service.execute(() -> System.out.println("Printing zoo inventory"));
    service.execute(() -> {for(int i=0; i<3; i++)
      System.out.println("Printing record: "+i);} ); }
    service.execute(() -> System.out.println("Printing zoo inventory"));
    System.out.println("end");
    finally {
      if(service != null) service.shutdown(); }
    ```

  - With a single-thread executor, results are guaranteed to be executed in the order in which they are added to the executor service. 
    - **Important!** This only means that the tasks executed by the ThreadExecutor will happen in a given order. The position of the "end" print is not guaranteed! It can happen that the result is (begin, ... `0 1 2 end` ...) or (... `0 1 end 2` ...).
- **Shutting Down a Thread Executor**
  - Once you have finished using a thread executor, it is important that you call the `shutdown()` method. A thread executor creates a non-daemon thread on the  rst task that is executed, so failing to call `shutdown()` will result in your application never terminating.
  - The shutdown process for a thread executor involves first rejecting any new tasks submitted to the thread executor while continuing to execute any previously submitted tasks. During this time, calling `isShutdown()` will return `true`, while `isTerminated()` will return `false`
  - If a new task is submitted to the thread executor while it is shutting down, a `RejectedExecutionException` will be thrown. Once all active tasks have been completed, `isShutdown()` and `isTerminated()` will both return `true`.
  - For the exam, you should be aware that `shutdown()` **does not actually stop any tasks** that have already been submitted to the thread executor.
  - `shutdownNow()` *attempts* to stop all running tasks and discards any that have not been started yet. It is possible to create a thread that will never terminate, so any attempt to interrupt it may be ignored. The function returns a `List<Runnable>` of tasks that were submitted to the thread executor but that were never started.
  - `ExecutorService` interface does not implement `AutoCloseable`, so you cannot use a try-with-resources statement.
  - Remember that failure to shut down a thread executor after at least one thread has been created **will result in the program hanging**.
- **Callable vs Runnable**
  - `Runnable` defines a function `void run()`. A Runnable, does not return a result and cannot throw a checked exception.
  - `Callable`  - A task that returns a result and may throw an exception. Implementors define a single method with no arguments called call (`V call()`).
- **Submitting Tasks**
  - `void execute(Runnable command)` - Executes a `Runnable` task at some point in the future. Note that the return type is `void`, hence "fire and forget" attitude.
  - `Future<?> submit(Runnable task)` - Executes a `Runnable` task at some point in the future and returns a `Future` representing the task
  - `<T> Future<T> submit(Callable<T> task)` - Executes a `Callable` task at some point in the future and returns a `Future` representing the pending results of the task.
  - `<T> List<Future<T>> invokeAll( Collection<? extends Callable<T>> tasks) throws InterruptedException` - Executes the given tasks, synchronously returning the results of all tasks as a Collection of `Future` objects, in the same order they were in the original collection.
  - `<T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException` - Executes the given tasks, synchronously returning the result of one of finished tasks, cancelling any unfinished tasks.
- **Waiting for the Results**
  - `Future<?> future = service.submit(() -> System.out.println("Hello Zoo"));`
  - The Future class includes methods that are useful in determining the state of a task_
    - `boolean isDone()` - Returns `true` if the task was completed, threw an exception, or was cancelled.
    - `boolean isCancelled()` - Returns true if the task was cancelled before it completely normally.
    - `boolean cancel()` - Attempts to cancel execution of the task.
    - `V get()` - Retrieves the result of a task, waiting endlessly if it is not yet available.
    - `V get(long timeout, TimeUnit unit)` - Retrieves the result of a task, waiting the specified amount of time. If the result is not ready by the time the timeout is reached, a checked `TimeoutException` will be thrown.

      ```java
      try {
        service = Executors.newSingleThreadExecutor();
        Future<?> result = service.submit(() -> {
          for(int i=0; i<500; i++) CheckResults.counter++; });
        result.get(10, TimeUnit.SECONDS);
      }  catch (TimeoutException e) {System.out.println("Not reached in time");}
      ```
- **Scheduling Tasks**
  - `ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();`
    - `schedule(Callable<V> callable, long delay, TimeUnit unit)` - Creates and executes a Callable task after the given delay
    - `schedule(Runnable command, long delay, TimeUnit unit)` - Creates and executes a `Runnable` task after the given delay
    - `scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)` - Creates and executes a `Runnable` task after the given initial delay, creating a new task every period value that passes.
    - `scheduleAtFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit)` - Creates and executes a `Runnable` task after the given initial delay and subsequently with the given delay between the termination of one execution and the commencement of the next.

      ```java
      ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
      Runnable task1 = () -> System.out.println("Hello Zoo");
      Callable<String> task2 = () -> "Monkey";
      Future<?> result1 = service.schedule(task1, 10, TimeUnit.SECONDS);
      Future<?> result2 = service.schedule(task2, 8, TimeUnit.MINUTES);
      ```

  - While these tasks are scheduled in the future, the actual execution may be delayed. For example, there may be no threads available to perform the task, at which point they will just wait in the queue. Also, if the ScheduledExecutorService is shut down by the time the scheduled task execution time is reached, they will be discarded.
  - The scheduleAtFixedRate() method creates a new task and submits it to the executor every period, regardless of whether or not the previous task finished. The following example executes a `Runnable` task every minute, following an initial five-minute delay:
    - `service.scheduleAtFixedRate(command,5,1,TimeUnit.MINUTE);`
      - If the task takes longer than the period, the function is called every 5 minutes and adds the tasks to the scheduler! Hance after a while you will end up having a huge pile of tasks...
    - `service.scheduleAtFixedDelay(command,0,2,TimeUnit.MINUTE);`
      - In contrast to the `scheduleAtFixedRate` this only schedules a new task, when the previous task was finished **and** the delay period elapsed!
- **Increasing Concurrency with Pools**
  - A *thread pool* is a group of pre-instantiated reusable threads that are available to perform a set of arbitrary tasks.
  - Functions
    - `ExecutorService newSingleThreadExecutor()` - Creates a single-threaded executor that uses a single worker thread operating off an unbounded queue. Results are processed sequentially in the order in which they are submitted.
    - `ScheduledExecutorService newSingleThreadScheduled Executor()` - Creates a single-threaded executor that can schedule commands to run after a given delay or to execute periodically.
    - `ExecutorService newCachedThreadPool()` - Creates a thread pool that creates new threads as needed, but will reuse previously constructed threads when they are available.
    - `ExecutorService newFixedThreadPool(int nThreads)` - Creates a thread pool that reuses a fixed number of threads operating off a shared unbounded queue. In fact, calling `newFixedThreadPool()` with a value of 1 is equivalent to calling `newSingleThreadExecutor()`. Often `Runtime.getRuntime().availableProcessors()` is a good choice for the number of threads.
    - `Scheduled ExecutorService newScheduledThreadPool(int nThreads)` - Creates a thread pool that can schedule commands to run after a given delay or to execute periodically.
  - The difference between a single-thread and a pooled-thread executor is what happens when a task is already running. While a single-thread executor will wait for an available thread to become available before running the next task, a pooled-thread executor can execute the next task concurrently. If the pool runs out of available threads, the task will be queued by the thread executor and wait to be completed.
- **Synchronizing Data Access**
  - The increment operator `++` is not thread-safe.
  - The unexpected result of two tasks executing at the same time is referred to as a *race condition*
  - `java.util.concurrent.atomic` package to help coordinate access to primitive values and object references.
  - *Atomic* is the property of an operation to be carried out as a single unit of execution without any interference by another thread. A thread-safe atomic version of the incre- ment operator would be one that performed the read and write of the variable as a single operation, not allowing any other threads to access the variable during the operation.
  - Classes supported by the API:
    - `AtomicBoolean, AtomicInteger AtomicIntegerArray, AtomicLong, AtomicLongArray, AtomicReference, AtomicReferenceArray`
  - Common atomic methods:
    - `get(), set(), getAndSet(), incrementAndGet(), getAndIncrement(), decrementAndGet(), getAndDecrement()`
  - Example:
  
    ```java
    private AtomicInteger sheepCount = new AtomicInteger(0);
    private void incrementAndReport() {
      System.out.print(sheepCount.incrementAndGet()+" ");
    }
    ```
  - Note that when this script is ran by 10 workers the series printed will always contain 10 (hence no data is lost while counting), **however** the order of the printed numbers is still not consistent.
- **Improving Access with Synchronized Blocks**
  - A *monitor* is a structure that supports mutual exclusion or the property that at most one thread is executing a particular segment of code at a given time.
  - In Java, any `Object` can be used as a monitor, along with the `synchronized` keyword

    ```java
    SheepManager manager = new SheepManager();
    synchronized(manager) {
      // Work to be completed by one thread at a time 
    }
    ```
  - This example is referred to as a *synchronized* block. Each thread that arrives will first check if any threads are in the block. In this manner, a thread “acquires the lock” for the monitor.
  - The correct version using synchronization is below. This code produces the desired `1 2 3 4 5 6 7 8 9 10` output.

    ```java
    private int sheepCount = 0;
    private void incrementAndReport() { 
      synchronized(this) {
        System.out.print((++sheepCount)+" ");
        }
    }
    ```
  - We could have synchronized on any object, so long as it was the same object.

    ```java
    private final Object lock = new Object();
    private void incrementAndReport() {
      synchronized(lock) {
        System.out.print((++sheepCount)+" ");
      }
    }
    ```
- **Synchronizing Methods**
  - We can add the `synchronized` modifier to any instance method to synchronize automatically on the object itself.
  - `private synchronized void incrementAndReport()`
  - When used on `static` methods the class object is used for locking. (`synchronized(SheepManager.class)`)

#### Using Concurrent Collections

- **Understanding Memory Consistency Errors**
  - A *memory consistency error* occurs when two threads have inconsistent views of what should be the same data.
  - Normally you can't remove elements using the enhanced for-loop. (`for(String key: foodData.keySet())`) If you want to do so, you get a `ConcurrentModificationException` exception, unless the underlying map is a `ConcurrentHashMap`.
    - `ConcurrentHashMap` is ordering read/write access such that all access to the class is consistent. In this code snippet, the iterator created by `keySet()` is updated as soon as an object is removed from the `Map`.
- **Understanding Blocking Queues**
  - `LinkedBlockingQueue` and `LinkedBlockingDeque`
  - New concurrent methods:
    - `offer(E e, long timeout, TimeUnit unit)` - Adds item to the queue waiting the specified time, returning false if time elapses before space is available.
    - `poll(long timeout, TimeUnit unit)` - Retrieves and removes an item from the queue, waiting the specified time, returning `null` if the time elapses before the item is available.
- **Understanding SkipList Collections**
  - The `SkipList` classes, `ConcurrentSkipListSet` and `ConcurrentSkipListMap`, are concurrent versions of their sorted counterparts, `TreeSet` and `TreeMap`, respectively.
- **Understanding CopyOnWrite Collections**
  - `CopyOnWriteArrayList` and `CopyOnWriteArraySet`
  - These classes copy all of their elements to a new underlying structure anytime an element is added, modified, or removed from the collection. By a modified element, we mean that the **reference** in the collection is changed.
  - The following snippet prints `4 3 52 Size: 6`. Despite adding elements to the array while iterating over it, only those elements in the collection at the time the `for()` loop was created were accessed. Alternatively, if we had used a regular `ArrayList` object, a `ConcurrentModificationException` would have been thrown at runtime. The `CopyOnWrite` classes can use a lot of memory, since a new collection structure needs be allocated anytime the collection is modi ed. They are commonly used in multi-threaded environment situations where reads are far more common than writes.

    ```java
    List<Integer> list = new CopyOnWriteArrayList<>(Arrays.asList(4,3,52));
    for(Integer item: list) {
      System.out.print(item+" ");
      list.add(9);
    }
    System.out.println();
    System.out.println("Size: "+list.size());
    ```
- **Obtaining Synchronized Collections**
  - The Concurrency API also includes methods for obtaining synchronized versions of existing non-concurrent collection objects.
  - `synchronizedCollection(Collection<T> c), synchronizedList(List<T> list), synchronizedMap(Map<K,V> m), synchronizedNavigableMap(NavigableMap<K,V> m), synchronizedNavigableSet(NavigableSet<T> s), synchronizedSet(Set<T> s), synchronizedSortedMap(SortedMap<K,V> m), synchronizedSortedSet(SortedSet<T> s)`
  - While these methods synchronize access to the data elements, such as the `get()` and `set()` methods, they do not synchronize access on any iterators that you may create from the synchronized collection.

    ```java
    List<Integer> list = Collections.synchronizedList( new ArrayList<>(Arrays.asList(4,3,52)));
    synchronized(list) {
      for(int data: list)
        System.out.print(data+" ");
    }
    ```

  - Unlike the concurrent collections, the synchronized collections also throw an exception if they are modi ed within an iterator by a single thread.

#### Working with Parallel Streams

- **Creating Parallel Streams**
  - **parallel()**
    - Creates a parallel stream form an existing stream.
    - `Stream<Integer> parallelStream = stream.parallel();`
  - **parallelStream()**
    - Create a parallel stream right at the beginning (instead of a normal stream)
    - `Stream<Integer> parallelStream2 = Arrays.asList(1,2,3,4,5,6).parallelStream();`
    - The output stream of:
      - `Stream.concat(Stream s1, Stream s2)` is parallel if either s1 or s2 is parallel.
      - `flatMap()` creates a new stream that is not parallel by default, regardless of whether the underlying elements were parallel.
  - **Processing Tasks in Parallel**
    - `Arrays.asList(1,2,3,4,5,6).parallelStream().forEach(s -> System.out.print(s+" "));`
      - With a parallel stream, the `forEach()` operation is applied across multiple elements of the stream concurrently, hence the output is not in the correct order.
      - `forEachOrdered` guarantees that the `forEach` is executed in the correct order.
    - Parallel streams tend to achieve the most improvement when the number of elements in the stream is significantly large.
    - It strongly recommended that you avoid stateful operations when using parallel streams, so as to remove any potential data side effects.
    - So as a recommendation - **parallel stream operations should be stateless**.
  - **Processing Parallel Reductions**
    - Reduction operations on parallel streams are referred to as *parallel reductions*.
    - Note that when using parallel streams the method `findAny` is not predictable, as the the JVM selects the first thread to finish the task and retrieves its data. --> You can see that with parallel streams, the results of `findAny()` are no longer predictable.
    - Any stream operation that is based on order, including `findFirst()`, `limit()`, or `skip()`, may actually perform more slowly in a parallel environment.
    - Creating unordered streams
      - `Arrays.asList(1,2,3,4,5,6).stream().unordered();`
      - Tells the JVM that if an order-based stream operation is applied, the order can be ignored.
    - **Combining Results with reduce()**
      - Recall that first parameter to the `reduce()` method is called the *identity*, the second parameter is called the *accumulator*, and the third parameter is called the *combiner*.
      - In order for the `reduce()` to work correctly with parallel streams the followings have to be true:
        - The *identity* must be defined such that for all elements in the stream u, `combiner.apply(identity, u)` is equal to u.
        - The *accumulator* operator op must be associative and stateless such that `(a op b) op c` is.
        - The *combiner* operator must also be associative and stateless and compatible with the identity, such that for all `u` and `t` `combiner.apply(u,accumulator.apply(identity,t))` is equal to `accumulator.apply(u,t)`.
      - If you follow these principles when building your reduce() arguments, then the operations can be performed using a parallel stream and the results will be ordered as they would be with a serial stream.
      - Examples:
        - `.reduce(0,(a,b) -> (a-b))); // NOT AN ASSOCIATIVE ACCUMULATOR`
        - `.reduce("X",String::concat)); // NOT TRULY AN IDENTITY VALUE`
    - **Combing Results with collect()**
      - `SortedSet<String> set = stream.collect(ConcurrentSkipListSet::new, Set::add, Set::addAll);`
      - Requirements for parallel reductions with collect:
        - The stream is parallel.
        - The parameter of the collect operation has the `Collector.Characteristics.CONCURRENT` characteristic.
        - Either the stream is unordered, or the collector has the characteristic `Collector.Characteristics.UNORDERED`.
      - Any class that implements the Collector interface includes a characteristics() method that returns a set of available attributes for the collector.
      - The Collectors class includes two sets of methods for retrieving collectors that are both UNORDERED and CONCURRENT, `Collectors.toConcurrentMap()` and `Collectors.groupingByConcurrent()`.

        ```java
        Stream<String> ohMy = Stream.of("lions", "tigers", "bears").parallel();
        ConcurrentMap<Integer, String> map = ohMy
          .collect(Collectors.toConcurrentMap(String::length, k -> k, (s1, s2) -> s1 + "," + s2));
        System.out.println(map); // {5=lions,bears, 6=tigers}
        System.out.println(map.getClass()); // java.util.concurrent.ConcurrentHashMap

        // OR

        Stream<String> ohMy = Stream.of("lions", "tigers", "bears").parallel();
        ConcurrentMap<Integer, List<String>> map = ohMy.collect(Collectors.groupingByConcurrent(String::length));
        System.out.println(map); // {5=[lions, bears], 6=[tigers]}
        ```

#### Managing Concurrent Processes

- **Creating a CyclicBarrier**
  - The `CyclicBarrier` takes in its constructors a limit value, indicating the number of threads to wait for. As each thread  finishes, it calls the `await()` method on the cyclic barrier. Once the specified number of threads have each called `await()`, the barrier is released and all threads can continue. The CyclicBarrier class allows us to perform complex, multi-threaded tasks, while all threads stop and wait at logical barriers.
  - Coordinating a task with a CyclicBarrier requires the object to be `static` or passed to the thread performing the task. We also add a try/catch block in the `performTask()` method, as the `await()` method throws multiple checked exceptions.
    - Output: 4* Removing animals, 4* Cleaning the pen, *** Pen Cleaned!, Adding animals

    ```java
    import java.util.concurrent.*;

    public class LionPenManager {
      private void removeAnimals() {
          System.out.println("Removing animals");
      }
      private void cleanPen() {
        System.out.println("Cleaning the pen");
      }
      private void addAnimals() {
        System.out.println("Adding animals");
      }
      public void performTask(CyclicBarrier c1, CyclicBarrier c2) { 
        try {
          removeAnimals();
          c1.await();
          cleanPen();
          c2.await();
          addAnimals();
        } catch (InterruptedException | BrokenBarrierException e) {
          // Handle checked exceptions here
        }
      }
      public static void main(String[] args) {
        ExecutorService service = null; try {
        service = Executors.newFixedThreadPool(4);
        LionPenManager manager = new LionPenManager();
        CyclicBarrier c1 = new CyclicBarrier(4);
        CyclicBarrier c2 = new CyclicBarrier(4, () -> System.out.println("*** Pen Cleaned!"));
        for(int i=0; i<4; i++)
          service.submit(() -> manager.performTask(c1,c2));
        } finally {
          if(service != null) service.shutdown();
        }
      }
    }
    ```

- **Applying the Fork/Join Framework**
  - Applying the fork/join framework requires us to perform three steps:
    - Create a `ForkJoinTask`.
    - Create the `ForkJoinPool`.
    - Start the `ForkJoinTask`.
  - `RecursiveAction` and `RecursiveTask`, both of which implement the `ForkJoinTask` interface.
  - The first class, `RecursiveAction`, is an abstract class that requires us to implement the `compute()` method, which returns `void`, to perform the bulk of the work.
  - The second class, `RecursiveTask`, is an abstract generic class that requires us to implement the `compute()` method, which returns the generic type, to perform the bulk of the work.

    ```java
    import java.util.*;
    import java.util.concurrent.*;
    public class WeighAnimalAction extends RecursiveAction {
      private int start;
      private int end;
      private Double[] weights;
      public WeighAnimalAction(Double[] weights, int start, int end) { this.start = start;
        this.end = end;
        this.weights = weights;
      }
      protected void compute() { if(end-start <= 3)
        for(int i=start; i<end; i++) {
          weights[i] = (double)new Random().nextInt(100);
          System.out.println("Animal Weighed: "+i);
        } else {
        int middle = start+((end-start)/2);
        System.out.println("[start="+start+",middle="+middle+",end="+end+"]");
        invokeAll(new WeighAnimalAction(weights,start,middle),
          new WeighAnimalAction(weights,middle,end));
        }
      }
    }
    ```

  - Once the task class is de ned, creating the ForkJoinPool and starting the task is quite easy. The following `main()` method performs the task on 10 records and outputs the results. By default, the ForkJoinPool class will use the number of processors to determine how many threads to create.

    ```java
    public static void main(String[] args) {
      Double[] weights = new Double[10];
      ForkJoinTask<?> task = new WeighAnimalAction(weights,0,weights.length);
      ForkJoinPool pool pool = new ForkJoinPool();
      pool.invoke(task);
      // Print results
      System.out.println(); System.out.print("Weights: ");
      Arrays.asList(weights).stream()
        .forEach(d -> System.out.print(d.intValue()+" "));
    }
    ```

  - **Using RecursiveTask**
    - The only differences are the following:
      - `public class WeighAnimalTask extends RecursiveTask<Double>`
      - `return sum;` at the end of the try block.
      - `RecursiveTask<Double> otherTask = new WeighAnimalTask(weights,start,middle);`, `otherTask.fork();` and `return new WeighAnimalTask(weights,middle,end).compute() + otherTask.join();` at the end of the else block.
    - Since the `invokeAll()` method doesn’t return a value, we instead issue a `fork()` and `join()` command to retrieve the recursive data. The `fork()` method instructs the fork/join framework to complete the task in a separate thread, while the `join()` method causes the current thread to wait for the results.
    - The `main()` method then looks like: `Double sum = pool.invoke(task);`
    - For the exam, make sure that `fork()` is called before the current thread begins a subtask and that `join()` is called after it finishes retrieving the results, in order for them to be done in parallel. (`Double otherResult = otherTask.fork().join();` is WRONG!)

  - **Identifying Fork/Join Issues**
    - The class should extend `RecursiveAction` or `RecursiveTask`.
    - If the class extends `RecursiveAction`, then it should override a **protected** `compute()` method that **takes no arguments** and returns `void`. --> `void compute()`
    - If the class extends `RecursiveTask`, then it should override a **protected** `compute()` method that **takes no arguments** and returns a **generic** type listed in the class definition. --> `V compute()`
    - The `invokeAll()` method takes two instances of the fork/join class and does not return a result.
    - The `fork()` method causes a new task to be submitted to the pool and is similar to the thread executor `submit()` method.
    - The `join()` method is called after the `fork()` method and causes the current thread to wait for the results of a subtask.
    - Unlike `fork()`, calling `compute()` within a `compute()` method causes the task to wait for the results of the subtask.
    - The `fork()` method should be called before the current thread performs a `compute()` operation, with `join()` called to read the results afterward.
    - Since `compute()` takes no arguments, the constructor of the class is often used to pass instructions to the task.

- **Identifying Threading Problems**
  - **Terms**
    - *Liveness* is the ability of an application to be able to execute in a timely manner.
    - Liveness problems, then, are those in which the application becomes unresponsive or in some kind of “stuck” state. For the exam, there are three types of liveness issues with which you should be familiar: deadlock, starvation, and livelock.
  - **Deadlock**
    - *Deadlock* occurs when two or more threads are blocked forever, each waiting on the other.
  - **Starvation**
    - *Starvation* occurs when a single thread is perpetually denied access to a shared resource or lock. The thread is still active, but it is unable to complete its work as a result of other threads constantly taking the resource that they trying to access.
  - **Livelock**
    - *Livelock* occurs when two or more threads are conceptually blocked forever, although they are each still active and trying to complete their task.
    - Example: Each thread notices that they are potentially entering a deadlock state and responds by releasing all of its locked resources. Unfortunately, the lock and unlock process is cyclical, and the two threads are conceptually deadlocked.
  - **Managing Race Conditions**
    - For the exam, you should understand that race conditions lead to invalid data if they are not properly handled. Even the solution where both participants fail to proceed is preferable to one in which invalid data is permitted to enter the system.

#### Learnings

- Both `Runnable` and `Callable` can throw **unchecked** exceptions.
- `scheduleWithFixedDelay()` does not exist in `ExecutorService`. It exists in `ScheduledExecutorService`, hence creating an instance of the latter and assigning it to the superclass `ExecutorService` and then calling the method won't work!
- `scheduleWithFixedDelay()` only supports `Runnable`, but not `Callable`!
- If a task is submitted to a thread executor, and the thread executor does not have any available threads, the call to the task will return immediately with the task being queued internally by the thread executor.
- The `CopyOnWriteArrrayList` class is designed to preserve the original list on iteration, hence adding elements in a loop won't effect the execution.
- The `ConcurrentSkipListSet` class allows while iterating
- `t1.fork().join();` - will work as single threaded!
- Calling `parallel()` on an already parallel is allowed, and it may in fact return the same object. (`s.parallelStream().parallel()` is okay!)
- A deadlock may arise, but not necessarily. Hence 1) the answer can't be given until runtime, 2) a deadlock may appear are correct answers!
- The stream created by `flatMap()` is a new stream that is not parallel by default.
- Dequque's `offerFirst, offerLast, pollFirst, pollLast` methods all throw checked exceptions!
- `DoubleStream.of(3.14159,2.71828).forEach(c -> service.submit(() -> System.out.println(10*c)));` --> even if the stream is single-threaded the execution service may execute the submitted tasks in a different order, hence the output is not predictable. Exception is a **single threaded** executioner.
- `IntStream.iterate(1, i -> 1).limit(9).parallel().forEach(i -> await(cb));` - it is not guaranteed that 9 threads will be allocated by the JVM, so if `cb` is expecting 9 awaits the code might still hang.