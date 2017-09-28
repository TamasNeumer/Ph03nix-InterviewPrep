# JVM and basics

#### [Docs](https://docs.oracle.com/javase/8/docs/api/)

#### The compilation process
- Java translates the code into **bytecode** (that is NOT machine code). Extension is `.class`
- Later the Java Virtual Machine (JVM) compiles this bytecode (via Just In Time) into machine code. The segments that were once compiled are saved. (i.e. if you run a function twice no need to re-compile)
- (+) Portability
- (-) JIT compilation --> Slower than fully compiled languages.
- Compile using `javac MyProg.java` and run using `java MyProg`

#### Hello word analyzed
```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```
- `System` call System class from `java.lang`
- `.` Dot operators provide you access to a classes members1; i.e. its fields (variables) and its methods. In this case, this dot operator allows you to reference the `out` static field within the `System` class.
- `out` this is the name of the static field of PrintStream type within the System class containing the standard output functionality.
- `println` this is the name of a method within the PrintStream class. This method in particular prints the contents of the parameters into the console and inserts a newline after.

#### Random Java facts
- Java doesn't support implicit int-->bool conversion.
- A single application may have multiple classes containing an entry point (main) method. The entry point of the application is determined by the class name passed as an argument to the java command.

#### Java packages
**Basics**
- **A package is a grouping of related types providing access protection and name space management.** Note that types refers to classes, interfaces, enumerations, and annotation types.
- Put your classes in packages.
- Use the name of your domain in reverse --> com.example.corejava
- If you do not use a package statement, your type ends up in an unnamed package. Generally speaking, an unnamed package is only for small or temporary applications or when you are just beginning the development process. Otherwise, classes and interfaces belong in named packages.
- If not defined public/privte you define the var/funct into the package scope!
- Importing do not include headers! It's more like "using namespace" from C++!

**Naming**
- Package names are written in all lower case to avoid conflict with the names of classes or interfaces.
- Companies use their reversed Internet domain name to begin their package names—for example, com.example.mypackage for a package named mypackage created by a programmer at example.com.
- Packages in the Java language itself begin with java. or javax.

**Consuming packages**
- To use a public package member from outside its package, you must do one of the following:
  - Refer to the member by its fully qualified name (`graphics.Rectangle`)
  - Import the package member (`import graphics.Rectangle;`)
  - Import the member's entire package (`import graphics.*;`)

#### Java CLASSPATH
- Classpath is a parameter in the Java Virtual Machine or the Java compiler that specifies the location of user-defined classes and packages.
-  The parameter may be set either on the command-line, or through an environment variable.
- (i.e.) you create your own classes and you put all the compiled mystuff.class into this CLASSPATH for later re-use.
- Your CLASSPATH will contain JAR files (classes that were "archived")




Verwendete Technologien: Java, JPA, Hibernate, Spring, Springboot, JSF, AWS, Elasticsearch, Docker, Maven, Jenkins, Git, Tomcat, JBoss, REST, HTML5, CSS, JavaScript, jQuery etc.

**reaktív** streamek
meg **functional pr**
ezek most nagyon menők
és javasoknál ez a mérce
3 fő téma van ami a testert és a devet megkülönbözteti ( jvm , reactive, és framework-ök) + 1 a devops

meg iratkozz fel a dzone-ra , javas újság
a jvm alatt memory mgmt-t értek
tényleg ezeket nem a kisújamból szopom ki
