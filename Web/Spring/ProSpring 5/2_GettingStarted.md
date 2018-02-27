# Getting Started
#### Building the Sample Hello World Application
- Book 27-31 is advised to read.
- ``ApplicationContext``: this interface is used by Spring for storing all the environmental information with regard to an application being managed by Spring.
- `ApplicationContext ctx = new ClassPathXmlApplicationContext
("spring/app-context.xml");` --> the application configuration information is loaded from the file spring/app-context.xml in the project’s classpath.
- In this case the word "Bean" refers to an instance of a class.
- `MessageRenderer mr = ctx.getBean("renderer", MessageRenderer.class);` --> obtains the MessageRenderer instances by using the ApplicationContext.getBean() method
- In the config file:
  - `<bean id="provider"
class="com.apress.prospring5.ch2.decoupled.HelloWorldMessageProvider"/>` --> declare the bean with the ID provider and the corresponding implementation class
  - Then the renderer bean is declared, with the corresponding implementation class. Remember that this bean depends on the MessageProvider interface for getting the message to render. To inform Spring about the DI requirement, we use the **p** namespace attribute.
  - `<bean id="renderer"
class="com.apress.prospring5.ch2.decoupled.StandardOutMessageRenderer"p:messageProvider-ref="provider"/>`
    - The tag attribute p:messageProviderref="provider" tells Spring that the bean’s property, *messageProvider*, should be injected with another bean. The bean to be injected into the property should reference a bean with the ID *provider*. When Spring sees this definition, it will instantiate the class, look up the bean’s property named *messageProvider*, and inject it with the bean instance with the ID *provider*.

- Starting with Spring 3.0, XML configuration files are no longer necessary when developing a Spring application. They can be replaced with annotations and configuration classes.
  - Configuration classes are Java classes annotated with ``@Configuration`` that contain bean definitions (methods annotated with ``@Bean``) or are configured themselves to identify bean definitions in the application by annotating them with ``@ComponentScanning``. (More on ComponentScanning later...)
  - Note that in this case we use the `AnnotationConfigApplicationContext`:
  - `ApplicationContext ctx = new AnnotationConfigApplicationContext(HelloWorldConfiguration.class);`

- **Most importantly** note that the classes remained "POJOs" (Plain Old Java Objects) meaning that they have **no** reference to Spring and are completely oblivious to its existence.
