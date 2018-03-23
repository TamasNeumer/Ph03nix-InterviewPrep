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
    Runnable task1 = () -> System.out.println("Hello Zoo"); Callable<String> task2 = () -> "Monkey";
    Future<?> result1 = service.schedule(task1, 10, TimeUnit.SECONDS); Future<?> result2 = service.schedule(task2, 8, TimeUnit.MINUTES);
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