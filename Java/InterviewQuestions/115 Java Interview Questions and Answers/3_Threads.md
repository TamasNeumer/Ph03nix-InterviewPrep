# Java Threads

#### 11. What is the difference between processes and threads ?
A process runs independently and isolated of other processes. It cannot directly access shared data in other processes. The resources of the process, e.g. memory and CPU time, are allocated to it via the operating system.

A thread is a so called lightweight process. It has its own call stack, but can access shared data of other threads in the same process. Every thread has its own memory cache. If a thread reads shared data it stores this data in its own memory cache. A thread can re-read the shared data.

#### 12. Explain different ways of creating a thread. Which one would you prefer and why ?
There are three ways that can be used in order for a Thread to be created:

* A class may extend the [Thread](http://docs.oracle.com/javase/7/docs/api/java/lang/Thread.html) class.
* A class may implement the [Runnable](http://docs.oracle.com/javase/7/docs/api/java/lang/Runnable.html) interface.
* An application can use the [Executor](http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Executor.html) framework, in order to create a thread pool.

The Runnable interface is preferred, as it does not require an object to inherit the Thread class. (I.e. You can use lambdas etc.)
Also, the thread pool is very efficient and can be implemented and used very easily.

#### 13. Explain the available thread states in a high-level.
During its execution, a thread can reside in one of the following states:
* New: A new thread begins its life cycle in the new state. It remains in this state until the program starts the thread. It is also referred to as a born thread.
* Runnable: A thread becomes ready to run, but does not necessarily start running immediately.
* Running: The processor is actively executing the thread code.
* Waiting: A thread is in a blocked state waiting for some external processing to finish.
* Sleeping: The thread is forced to sleep.
* Blocked on I/O: Waiting for an I/O operation to complete.
* Blocked on Synchronization: Waiting to acquire a lock.
* Dead: The thread has finished its execution.

#### 14. What is the difference between a synchronized method and a synchronized block ?
In Java programming, each object has a lock. A thread can acquire the lock for an object by using the synchronized keyword. The synchronized keyword can be applied in a method level (coarse grained lock) or block level of code (fine grained lock).

#### 15. How does thread synchronization occurs inside a monitor ? What levels of synchronization can you apply ?
The JVM uses locks in conjunction with monitors. A monitor is basically a guardian that watches over a sequence of synchronized code and ensuring that only one thread at a time executes a synchronized piece of code. Each monitor is associated with an object reference. The thread is not allowed to execute the code until it obtains the lock.

#### 16. What’s a deadlock ?
A condition that occurs when [two processes are waiting for each other to complete](http://www.javacodegeeks.com/2013/01/java-deadlock-example-how-to-analyze-deadlock-situation.html), before proceeding. The result is that both processes wait endlessly.

#### 17. How do you ensure that N threads can access N resources without deadlock ?
A very simple way to avoid deadlock while using N threads is to impose an ordering on the locks and force each thread to follow that ordering. Thus, if all threads lock and unlock the mutexes in the same order, no deadlocks can arise.
