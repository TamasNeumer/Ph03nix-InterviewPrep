# JVM and basics

#### The compilation process
- Java translates the code into **bytecode** (that is NOT machine code).
- Later the Java Virtual Machine (JVM) compiles this bytecode (via Just In Time) into machine code. The segments that were once compiled are saved. (i.e. if you run a function twice no need to re-compile)
- (+) Portability
- (-) JIT compilation --> Slower than fully compiled languages.
- Compile using `javac MyProg.java` and run using `java MyProg`

#### Random Java facts
- Java doesn't support implicit int-->bool conversion.

#### Java packages
**Basics**
- **A package is a grouping of related types providing access protection and name space management.** Note that types refers to classes, interfaces, enumerations, and annotation types.
- You bundle related classes, interfaces and enumerations, because:
  - You and other programmers can easily determine that these types are related.
  - You and other programmers know where to find types that can provide related functions.
  - The names of your types won't conflict with the type names in other packages because the package creates a new namespace.
  - You can allow types within the package to have unrestricted access to one another yet still restrict access for types outside the package.
- If you do not use a package statement, your type ends up in an unnamed package. Generally speaking, an unnamed package is only for small or temporary applications or when you are just beginning the development process. Otherwise, classes and interfaces belong in named packages.

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


Verwendete Technologien: Java, JPA, Hibernate, Spring, Springboot, JSF, AWS, Elasticsearch, Docker, Maven, Jenkins, Git, Tomcat, JBoss, REST, HTML5, CSS, JavaScript, jQuery etc.

reaktív streamek
meg functional pr
ezek most nagyon menők
és javasoknál ez a mérce
3 fő téma van ami a testert és a devet megkülönbözteti ( jvm , reactive, és framework-ök) + 1 a devops

meg iratkozz fel a dzone-ra , javas újság
a jvm alatt memory mgmt-t értek
tényleg ezeket nem a kisújamból szopom ki
