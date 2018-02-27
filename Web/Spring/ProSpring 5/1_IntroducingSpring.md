# Introducing Spring
## Main Features of Spring
#### Inversion Of Control = Dependency Injection
- The Spring Framework is an application framework and inversion of control container (IoC) for the Java platform.
- Consider an example in which class Foo depends on an instance of class Bar to perform some kind of processing. Traditionally, Foo creates an instance of Bar by using the new operator or obtains one from some kind of factory class. Using the IoC approach, an instance of Bar (or a subclass) is provided to Foo at runtime by some external process.

  ```java
  // Old-Style
  class OldFoo {
    /*...*/
    Bar myBar = new Bar();
    bar.callBarFunction();
    /*...*/
  }

  // New Style
  class NewFoo {
    // The Bar instance is injected by the external IoC container at runtime!
    Bar myBar;
    bar.callBarFunction();
  }
  ```

- A few advantages of DI:
  - *Reduced glue code*: The (spring) container automatically manages the injection of instances into their places. No code needs to be written by the developer.
  - *Simplified application configuration*: The injection configuration is done in simple ways. (Via XML configuration file, or Java Annotations).
  - *Testability*: Dependencies can be easily swapped with "Mocks" in order to test the business logic. For example if the Database is not yet ready, simply create a "Dummy" class that implements the same interface as the real DAO, and use that to test your business logic.

#### Aspect Oriented Programming
- AOP provides the ability to implement crosscutting logic - that is, logic that applies to many parts of your application - in a single place and to have that logic applied across your application automatically. (Example: Logger, transaction
management)

#### Spring expression language
- Expression Language (EL) is a technology to allow an application to manipulate Java objects at runtime. Spring has its own EL: Spring Expression Language

#### Validation
- Spring provides `ValidationUtils` and other classes to endorse testing and validating your business logic.

#### Spring Boot
- Setting up the basis of an application is a cumbersome job. Configuration files for the project must be created, and additional tools (like an application server) must be installed and configured. Spring Boot (http://projects.spring.io/spring-boot/) is a Spring project that makes it easy to create stand-alone, production-grade Spring-based applications that you can just run. Spring Boot comes with out-of-the-box configurations for different types of Spring applications that are packed in starter packages.

#### Understanding Spring Packaging
- Spring modules are simply JAR files that package the required code for that module. After you understand
the purpose of each module, you can select the modules required in your project and include them in
your code.

**Module**  |  **Description**
--|--
aop  |  This module contains all the classes you need to use Spring’s AOP features within your application. You also need to include this JAR in your application if you plan to use other features in Spring that use AOP, such as declarative transaction management.
aspects  |  This module contains all the classes for advanced integration with the AspectJ AOP library.
beans  |  This module contains all the classes for supporting Spring’s manipulation of Spring beans. Most of the classes here support Spring’s bean factory implementation. For example, the classes required for processing the Spring XML configuration file and Java annotations are packed into this module.
beans-groovy  |  This module contains Groovy classes for supporting Spring’s manipulation of Spring beans.
context  |  This module contains classes that provide many extensions to Spring Core. You will find that all classes need to use Spring’s ApplicationContext feature, along with classes for Enterprise JavaBeans (EJB), Java Naming and Directory Interface (JNDI), and Java Management Extensions (JMX) integration.
context-indexer  |  This module contains an indexer implementation that provides access to the candidates that are defined in META-INF/spring.components. The core class CandidateComponentsIndex is not meant to be used externally.
context-support  |  This module contains further extensions to the spring-context module. On the user-interface side, there are classes for mail support and integration with templating engines such as Velocity, FreeMarker, and JasperReports.
core  |  This is the main module that you will need for every Spring application. In this JAR file, you will find all the classes that are shared among all other Spring modules (for example, classes for accessing configuration files). Also, in this JAR, you will find selections of extremely useful utility classes that are used throughout the Spring codebase and that you can use in your own application.
expression  |  This module contains all support classes for Spring Expression Language (SpEL).
instrument  |  This module includes Spring’s instrumentation agent for JVM bootstrapping. This JAR file is required for using load-time weaving with AspectJ in a Spring application.
dbc  |  This module includes all classes for JDBC support. You will need this module for all applications that require database access. Classes for supporting data sources, JDBC data types, JDBC templates, native JDBC connections, and so on, are packed in this module.
jms  |  This module includes all classes for JMS support.
messaging  |  This module contains key abstractions taken from the Spring Integration project to serve as a foundation for message-based applications and adds support for STOMP messages.
orm  |  This module extends Spring’s standard JDBC feature set with support for popular ORM tools including Hibernate, JDO, JPA, and the data mapper iBATIS. Many of the classes in this JAR depend on classes contained in the spring-jdbc JAR file, so you definitely need to include that in your application as well.
oxm  |  This module provides support for Object/XML Mapping (OXM). Classes for the abstraction of XML marshalling and unmarshalling and support for popular tools such as Castor, JAXB, XMLBeans, and XStream are packed into this module.
test  |  Spring provides a set of mock classes to aid in testing your applications, and many of these mock classes are used within the Spring test suite, so they are well tested and make testing your applications much simpler. Certainly we have found great use for the mock HttpServletRequest and HttpServletResponse classes in unit tests for our web applications. On the other hand, Spring provides a tight integration with the JUnit unit-testing framework, and many classes that support the development of JUnit test cases are provided in this module; for example, SpringJUnit4ClassRunner provides a simple way to bootstrap the Spring ApplicationContext in a unit test environment.
tx  |  This module provides all classes for supporting Spring’s transaction infrastructure.You will find classes from the transaction abstraction layer to support the Java Transaction API (JTA) and integration with application servers from major vendors.
web  |  This module contains the core classes for using Spring in your web applications, including classes for loading an ApplicationContext feature automatically, file upload support classes, and a bunch of useful classes for performing repetitive tasks such as parsing integer values from the query string.
web-reactive  |  This module contains core interfaces and classes for Spring Web Reactive model.
web-mvc  |  This module contains all the classes for Spring’s own MVC framework. If you are using a separate MVC framework for your application, you won’t need any of the classes from this JAR file.
websocket  |  This module provides support for JSR-356 (Java API for WebSocket).
