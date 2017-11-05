# Maven

#### Introduction
**What is Maven**  
- Apache Maven is a software project management and comprehension tool. Based on the concept of a project object model (POM), Maven can manage a project's build, moreover it can also run reports, generate a web site, and facilitate communication among members of a working team.
- Contains:
  - PM tool with POM
  - Set of standards
  - Project life cycle
  - Dependency management system
  - Logic for executing plugin goals at life-cycle phases

**Maven's default configuration**  
- Without customization, source code is assumed to be in `${basedir}/src/main/java` and resources are assumed
to be in `${basedir}/src/main/resources`. Tests are assumed to be in `${basedir}/src/
test`, and a project is assumed to produce a `JAR` file. Maven assumes that you want the compile bytecode
to `${basedir}/target/classes` and then create a distributable JAR file in `${basedir}/
target`.
- Maven has plugins for everything from compiling Java code, to generating reports, to deploying to an application server.

**Maven vs Ant**  
*Apache Ant*  
- Ant doesn’t have formal conventions like a common project directory structure or default behavior.
You have to tell Ant exactly where to find the source and where to put the output. Informal
conventions have emerged over time, but they haven’t been codified into the product.
- Ant is procedural. You have to tell Ant exactly what to do and when to do it. You have to tell it
to compile, then copy, then compress.
- Ant doesn’t have a lifecycle. You have to define goals and goal dependencies. You have to attach
a sequence of tasks to each goal manually.

*Apache Maven*
- Maven has conventions. It knows where your source code is because you followed the convention.
Maven’s Compiler plugin put the bytecode in target/classes, and it produces a JAR file in
target.
- Maven is declarative. All you had to do was create a pom.xml file and put your source in the
default directory. Maven took care of the rest.
- Maven has a lifecycle which was invoked when you executed mvn install. This command
told Maven to execute a series of sequential lifecycle phases until it reached the install lifecycle
phase. As a side effect of this journey through the lifecycle, Maven executed a number of default
plugin goals which did things like compile and create a JAR.

#### Installing Maven
**Download Maven**
- [Download](https://maven.apache.org/download.cgi) and [Install](https://maven.apache.org/install.html) maven.
  - Unix
    - (sudo apt-get install maven) OR:
    - Download, extract `/user/local/...`
    - `sudo ln -s apache-maven-3.5.2 maven`
    - `export PATH=/usr/local/maven/bin:$PATH`
    - `export PATH=/usr/local/maven/bin:${PATH}`
  - Windows
    - `set PATH="c:\Program Files\apache-maven-3.5.2\bin";%PATH%`
    - Add `"C:\Program Files\apache-maven-3.0.5\bin;` to environment vars.
- Test success
  - `mvn -v`

#### Simple project
**Building the first project**  
- To start a new Maven project, use the Maven Archetype plugin from the command line.
  - `mvn archetype:generate`
    - Specify `groupID`, `artifactID` etc.
  - "archetype:generate" is called a Maven goal. When executing the command a project is created with the standard Maven project structure. `arc
hetype` is the identifier of a plugin and `generate` is the identifier of a goal
- To build the project run `mvn install`
  - You’ve just created, compiled, tested, packaged, and installed the simplest possible Maven project. To prove to yourself that this program works, run it from the command line:
  - `java -cp target/simple-1.0-SNAPSHOT.jar org.sonatype.mavenbook.App` (where org.sonatype.mavenbook was the package name)

**Core concepts: plugins and goals**  
  - A Maven **Plugin is a collection of one or more goals**. Examples of Maven plugins can be simple core plugins like the Jar plugin, which contains goals for creating JAR files, Compiler plugin, which contains goals for compiling source code and unit tests, or the Surefire plugin, which contains goals for executing unit tests and generating reports.
  - A goal is a specific task that may be executed as a standalone goal or along with other goals as part of a larger build. **A goal is a “unit of work” in Maven.** Examples of goals include the compile goal in the Compiler plugin, which compiles all of the source code for a project, or the test goal of the Surefire plugin, which can execute unit tests.
  - By itself Maven doesn't know how to compile or test, but it delegates these tasks to plugins!

**Maven lifecycle**
- Plugin goals can be attached to a lifecycle phase. As Maven moves through the phases in a lifecycle, it
will execute the goals attached to each particular phase.
- In the previous example (`mvn install`) the following lifecycle steps have been executed.
  - `resources:resources`
    - This goal copies all of the resources from src/main/resources and any other configured resource directories to the output directory.
  - `compiler:compile`
    - This goal compiles all of the source code from src/main/java or any other configured source directories to the output directory.
  - `resources:testResources`
    - etc etc etc.
  - `compiler:testCompile`
  - `surefire:test`
  - `jar:jar`
- To summarize, when we executed mvn install, Maven executes all phases up to the install phase, and
in the process of stepping through the lifecycle phases it executes all goals bound to each phase. You could actually achieve the same results by executing the same goals:

```
mvn resources:resources \
  compiler:compile \
  resources:testResources \
  compiler:testCompile \
  surefire:test \
  jar:jar \
  install:install
```

**Maven coordinates**
- Maven coordinates define a set of identifiers which can be used to uniquely identify a project, a dependency, or a plugin in a Maven POM
- These combined identifiers make up a project’s coordinates:
  ```
  <groupId>testGroupId</groupId>
  <artifactId>testArtifactID</artifactId>
  <version>1.0-SNAPSHOT</version>
  <packaging>jar</packaging>
  ```
- `groupID` ->  unique identifier of the organization or group that created the project. The convention for group identifiers is that they begin with the reverse domain name of the organization that creates the project.
- `artifactId` -> unique base name of the primary artifact being generated by this project. The primary artifact for a project is typically a JAR file.
- `packaging` -> indicates the package type to be used by this artifact (e.g. JAR, WAR, EAR, etc.).
- `version` -> version of the artifact generated by the project
  - What is a SNAPSHOT version?
    - The SNAPSHOT value refers to the 'latest' code along a development branch, and provides no guarantee the code is stable or unchanging. In other words, a SNAPSHOT version is the 'development' version before the final 'release' version.
- `packaging`
  - The type of project, defaulting to jar, describing the packaged output produced by a project. A project with packaging jar produces a JAR archive; a project with packaging war produces a web application.

**Dependency Management with Maven**
- You can simply add dependencies in the `<dependencies>` section.
- Support for transitive dependencies is one of Maven’s most powerful features. (i.e. If you depend on other maven project those dependencies will be added automatically)
- Maven also works out conflicts with dependencies.
- View your dependencies with `mvn dependency:resolve` or view the tree with `mvn dependency:tree`

- Test-scoped dependency
  - A test-scoped dependency is a dependency that is available on the classpath only during test compilation and test execution
  - `<scope>test</scope>` to mark it as a test-scope dependency!


#### Customizing a Maven project
- Adding informative stuff
  - License infomation
  - Organization infomation
  - Developer list (Email, timezone etc.)
- Adding build plugins. (`<build><plugins><plugin>...</...>`)
- Add dependencies
- Add java classes (the code you write)
- Add resources
- `mvn install`



#### Working with Maven
- Creating project
```shell
mvn -B archetype:generate \
  -DarchetypeGroupId=org.apache.maven.archetypes \
  -DgroupId=com.mycompany.app \
  -DartifactId=my-app
```
- Compiling project
  - Compiled files will be under `${basedir}/target/classes`
```
mvn compile
```
- Running unit tests
```
mvn test
```
- Creating JAR file
  - After that you'll want to install the artifact you've generated (the JAR file) in your local repository (`${user.home}/.m2/repository` is the default location).
```
mvn package
mvn install
```
- You can also create documentation using Maven:
```
mvn archetype:generate \
  -DarchetypeGroupId=org.apache.maven.archetypes \
  -DarchetypeArtifactId=maven-archetype-site \
  -DgroupId=com.mycompany.app \
  -DartifactId=my-app-site
```

#### POM
**POM basics**
- POM is based on a standard folder structure.
  - (src, target, test etc.)
- POM contains many project details

  - `name` -> display name used for the project
  - `url` -> project's site can be found
  - `description` -> basic description
  - `model` -> describes mavn version used
- Combination of GroupID, ArtifactID and Version should be unique! --> "Coordinates of the project."
- Plugins
  - A plugin is a collection of one or more goals. A goal is a "unit of work" in Maven.
  - Basic plugins, JAR plug-in, Compiler plug-in, Surefire plug-in
  - On the [Plugins website](https://maven.apache.org/plugins/) you find plug-ins for different categories. E.g.: compiling, reporting (documentation generation) etc.
- Adding resources
  - E.g.: Add a test file to simulate sample input
  - Add files under `test/resources`


**Effective POM**
- The effective-pom goal is used to make visible the POM that results from the application of interpolation, inheritance and active profiles. It provides a useful way of removing the guesswork about just what ends up in the POM that Maven uses to build your project. It will iterate over all projects in the current build session, printing the effective POM for each.
  - `mvn help:effective-pom`




#### Understanding Maven
**Maven lifecycle**
- Generate
- Execute Maven with lifecycle phase
- Install a Maven artifact
- Run app

**Maven Install**
- If you go into a maven project (project containing a POM file) if you execute `mvn install` you will see that maven executes commands for several lifecycle phases such as fetching resources, compiling, testing, and then creating jar files.

**Maven Repositories**
- Online maven repository `search.maven.org`
- Local maven repository (Home/.m2)
