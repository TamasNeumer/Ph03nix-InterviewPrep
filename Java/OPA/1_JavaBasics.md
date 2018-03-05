# Basics
#### Package statement
- A Java class can be explicitly defined in a *named package*; otherwise, it becomes part of a *default package*, which doesn’t have a name.
- Package statement **must** be in the first line and appear only **once** (if declared explicitly). (Except for comments. Comments can be placed before the package declaration.)
- Given a package statement all newly defined classes and interfaces will fall under this package.
- You can use a package to group together a related set of classes and interfaces.
- A package is made of multiple sections that go from the more-generic (left) to the more-specific (right).
- Package names **should** be all lowercase.
- One package declaration per source file!
- The hierarchy of classes and interfaces defined in packages **must** match the hierarchy of the directories in which these classes and interfaces are defined in the code.

#### Import statement
- To use a class or an interface from another package, you must use its fully qualified name, that is, `packageName.anySubpackageName.ClassName`.
- To avoid referring to classes on their full-name you can import these.
- If a package statement is present in a class, the import statement **must** follow the package statement. --> otherwise won't compile. (Comments are allowed though.)
- The import statement doesn’t embed the contents of the imported class in your class, which means that importing more classes doesn’t increase the size of your own class.
- You don’t need an explicit import statement to use members from the [`java.lang`](https://docs.oracle.com/javase/9/docs/api/java/lang/package-summary.html) package. Classes and interfaces in this package are automatically imported in all other Java classes, interfaces, or enums.
- The Java API defines class Date in two commonly used packages: `java.util` and `java.sql` tying to import both will result in a naming-clash and hence won't compile.
- You can import either a single member or all members (classes and interfaces) of a package using the import statement.
  - `import certification.ExamQuestion;` - Imports a single class!
- You can’t import classes from a subpackage by using an asterisk in the import statement.
- This default package is **automatically imported** in the Java classes and interfaces defined within the same directory on your system. However members of a **named package can’t access** classes and interfaces defined in the default package. --> The only way to access classes in the default package is from another class in the default package.
- If present, an import statement must be placed before any class or interface definition.
- `static` import
  - You can import an individual static member of a class or all its static members by using the import static statement.
  - On real projects, avoid overusing static imports; otherwise, the code might become a bit confusing about which imported component comes from which class.

#### Comments
- Comments can come **anywhere** (they are disregarded by the compiler)
- Multi line (`/*...*/`) / single line Comments (`//`)
  - `String name = /* Harry */ "Paul";` --> uncommon but valid!
- Javadoc comments are special comments that start with `/**` and end with `*/` in a Java source file. These comments are processed by Javadoc, a JDK tool, to generate API documentation for your Java source code files.

#### Class declaration
- `class` keyword followed by the class name.
- `public final class Runner extends Person implements Athlete {}`
  - `public` - Access modifier
  - `final` - Non-access modifier
  - `class` - keyword class. (Case-sensitive! Does't work with "Class")
  - `Runner` - name of the class
  - `extends` - keyword extends
  - `Person` - Base class name
  - `implements` - keyword implements
  - `Athlete` - name of implemented interface
- The *state* of the class is defined by its attributes / variables
- The *state* is manipulated by the class' methods.
- A *top-level* class is a class that isn’t defined within any other class. A class that is defined within another class is called a *nested or inner class*.

#### Structure and components of a Java source code file
- An interface is a grouping of related methods and constants.
- Prior Java 8 methods were abstract, but with the `default` keyword an implementation can be provided.
- Since Java 8 interfaces can declare static methods.
- Defining multiple classes / interfaces within same source file:
  - When you define a *public* class or an interface in a Java source file, the names of the class or interface and Java source file **must** match.
  - Also, a source code file *can’t* define *more than one public class* or interface.

#### Executable Java classes versus non-executable Java classes
- In executable Java class, when handed over to the JVM, starts its execution at a particular point in the class — the main method. The `main` method makes a Java class executable.
- The `main` method
  - Must be marked `public`
  - Must be marked `static`
  - Name of the method must be `main`
  - Return type must be `void`
  - The method must accept a method argument of a `String` array or a variable argument (varargs) of type `String`.
    - `String []` and `String ... args` are both correct.
    - The passed argument can have any name as long as the type is correct.
  - The main method can be overloaded (e.g.: `public static void main(int number)`), however only **one** "correct" main method can be defined that conforms the requirements and this will be executed by the JVM.
  - The `java` and `ExecutableName` properties are obviously not passed as arguments to the main method.
- Compile with `javac FileName.java` and run with `java FileName arg1 arg2...`

#### Access modifiers on class members
- Access modifiers can be applied to classes, interfaces, and their members. Using them on local members results in a compile error.
- Four levels of access:
  - `public`
    - Accessible across all packages
  - `protected`
    - Accessible to classes and interfaces defined in the same package
    - Accessible to all *derived* classes, even if they’re defined in separate packages
  - default
    - The default access is also referred to as *package-private*. Variables are only accessible in the **same** package.
  - `private`
    - The members of a class defined using the private access modifier are accessible only to themselves.
- Exam tip: Watch out for invalid combinations of a Java entity and an access modifier.

#### Access modifiers on top-level classes, interfaces and enums
- They can be only public and (default) package-private.

#### Non-access modifiers
- Non-access modifiers change the default behaviour of a Java class and its members.
- Keywords: `abstract, static, final, synchronised, native, strictfp, transient, volatile`
  - In OCA exam only the first 3 are relevant.
- `abstract` modifier
  - `abstract` class can't be instantiated (even if it doesn't contain any abstract methods)
  - The `abstract` keyword before a function indicates a function without a **body**. --> A method with an **empty** body isn’t an abstract method!
  - The Java compiler automatically adds the keyword abstract to the definition of an *interface*. Thus, adding the keyword abstract to the definition of an interface is redundant.
  - The `abstract` keyword can't be applied to variables!
- `final` modifier
  - Can be used with the declaration of a class, variable, or method, but **not** with an interface!
  - `final` class can't be extended by other classes .
  - `final` method cannot be overridden.
  - Interfaces **can"t be marked as final** --> compile error
  - A `final` variable's value can't be reassigned. (Note the word reassigned! A reference to an object can **not** be changed, however the object's underlying structure can be changed! - e.g. adding elements to a `final` list is okay.)
- `static` modifier
  - Can be applied to the declarations of variables, methods, classes, and interfaces.
  - `static` variables belong to the class and not to the instance. Static variables are shared among all instances of the class. You can reference static variables via class names (MyClass.staticVar) or via a given instance, however accessing them via instances should be avoided (due to code clarity).
  - `static` methods aren’t associated with objects and can only use static instance variables of a class. It’s a common practice to use static methods to define utility methods, which are methods that usually manipulate the method parameters to compute and return an appropriate value.
  - Because static variables and methods belong to a class and not to an instance, **you can access them using variables, which are initialized to null**. Watch out for such questions in the exam.
    - `Emp emp = null;` and then `emp.callingStaticMethod()` is fine.
    - You **can** access static variables and methods using a null reference.
  - You **can’t** prefix the definition of a **top-level** class or an interface with the keyword static.
  - You can add the `static` prefix to nested classes and nested interfaces.

#### Features and components of Java
- **Platform independence**
  - Java code can be executed on multiple systems without recompilation. Java code is compiled into bytecode, to be executed by a virtual machine—the Java Virtual Machine (JVM). A JVM is installed on platforms with different OSs like Windows, Mac, or Linux.
- **Object orientation**
  - In real life, state and behavior are tied to an object. Similarly, all Java code is defined within classes, interfaces, or enums. You need to create their objects to use them.
- **Abstraction**
  - Java lets you abstract objects and include only the required properties and behavior in your code.
- **Encapsulation**
  - With Java classes, you can encapsulate the state and behavior of an object. The state or the fields of a class are protected from unwanted access and manipulation.
- **Inheritance**
  - Java enables its classes to inherit other classes and implement interfaces. The inter- faces can inherit other interfaces. This saves you from redefining common code.
- **Polymorphism**
  - The literal meaning of polymorphism is “many forms.” Java enables instances of its classes to exhibit multiple behaviors for the same method calls.
- **Type Safety**
  - In Java, you must declare a variable with its data type before you can use it. This means that you have compile-time checks that ensure you never assign to a variable a value of the wrong type.
- **Garbage collection / memory management**
  -  Java uses garbage collectors for automatic memory management. They reclaim memory from objects that are no longer in use.
- **Multithreading**
  - Java has supported multithreading and concurrency since it was first released—sup- ported by classes and interfaces defined in its core API.
- **Security**
  - Java includes multiple built-in security features (though not all are covered in this exam) to control access to your resources and execution of your programs. Java is type safe and includes garbage collection. It provides secure class loading, and verification ensures execution of legitimate Java code.

#### Key takeaway:
- In the test questions pay attention to case sensitivity. Sometimes they can "slip" a "Class" definition with capital and you simply will think that they are testing your knowledge on completely different stuff.
- If a class has no access modifier the class itself is "default", and hence can be only accessed from classes of the same package.
