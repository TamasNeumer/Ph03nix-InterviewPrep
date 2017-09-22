# JVM and basics

#### The compilation process
- Java translates the code into **bytecode** (that is NOT machine code).
- Later the Java Virtual Machine (JVM) compiles this bytecode (via Just In Time) into machine code. The segments that were once compiled are saved. (i.e. if you run a function twice no need to re-compile)
- (+) Portability
- (-) JIT compilation --> Slower than fully compiled languages.
- Compile using `javac MyProg.java` and run using `java MyProg`

#### Java facts
- Java doesn't support implicit int-->bool conversion.
