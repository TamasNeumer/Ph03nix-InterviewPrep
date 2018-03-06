# Introducing IoC and DI in Spring

## Types of IoC
#### Dependency Pull
- In dependency pull, dependencies are pulled from a registry as required.
- Spring also offers dependency pull as a mechanism for retrieving the components that the framework manages. This is done by the `appContext.getBean("beanName", beanClass.class)` function.

#### Contextualized Dependency Lookup
- In CDL lookup is performed against the container that is managing the resource.
- In CDL the components implement an interface, (e.g. with method 'void performLookup(Container container);') So basically the component is aware of the container.
- When the container is ready to "hand over" the dependencies to the components, it calls the `performLookup` on these. In the implementation of the `performLookup` each component asks the container for its dependencies.

#### Constructor Dependency Injection
- Constructor dependency injection occurs when a component’s dependencies are provided to it in its constructor (or constructors).
- The component declares a constructor or a set of constructors, taking as arguments its dependencies, and the IoC container passes the dependencies to the component when instantiation occurs.
- Constructor-injection enforces the order of initialization and prevents circular dependencies. (*Hence favored lately by the Spring community*)
```java
public ConstructorInjection(Dependency dependency) {
  this.dependency = dependency;
}
```

#### Setter Dependency Injection
- A component’s setters expose the dependencies the IoC container can manage. (i.e. for the dependencies you create setter functions, that are called by the container when injecting instances.)
- An obvious consequence of using setter injection is that an object can be created without its dependencies, and they can be provided later by calling the setter.
- As a rule of thumb use *constructor injection for mandatory* dependencies and setter injection for optionals. Also if the *component is happy to provide its own defaults*, setter injection is usually the best way to accomplish this.
  - (Setter injection is often used with "configuration parameters", e.g. primitive types that are injected to the component to work properly. Such are username, password for the database etc.)
  - Finally setter injection also allows you to swap dependencies for a different implementation on the fly without creating a new instance of the parent component.

#### Field injection
- See at `@Autowired`

#### Conclusion
- The dependency pull must actively obtain a reference to the registry and interact with it to obtain the dependencies, and
using CDL requires your classes to implement a specific interface and look up all dependencies manually.
- You write substantially less code when you are using injection, and the code that you do write is simple and can, in general, be automated by a good IDE.
-  The "Injection" style is the most cleared and hance it is used by Spring.

## IoC in Spring
#### Intro
- Spring uses Dependency Injection (DI)
- Spring’s IoC containe can act as an adapter between its own dependency injection container and external dependency lookup containers.

#### Bean And Bean Factory
- The core of Spring’s dependency injection container is the ``BeanFactory`` interface. BeanFactory is responsible for managing components, including their dependencies as well as their life cycles. In Spring, the term bean is used to refer to any component managed by the container.
- If your application needs DI support your application must create an instance of a class that implements the ``BeanFactory`` interface and configures it with bean and dependency information. Internally, bean configuration is represented by instances of classes that implement the BeanDefinition interface.
- The example below shows the usage of the `DefaultListableBeanFactory`, which is
one of the two main BeanFactory implementations supplied with Spring.
  ```java
  public class XmlConfigWithBeanFactory {
    public static void main(String... args) {
      DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
      XmlBeanDefinitionReader rdr = new XmlBeanDefinitionReader(factory);
      rdr.loadBeanDefinitions(new
        ClassPathResource("spring/xml-bean-factory-config.xml"));
      Oracle oracle = (Oracle) factory.getBean("oracle");
      System.out.println(oracle.defineMeaningOfLife());
    }
  }
  ```

#### ApplicationContext
- In Spring, the ApplicationContext interface is an extension to BeanFactory.
- Provides many configuration options. (XML Config, Annotation config etc.) Which one is better? --> XML is the "traditional" solution and there you can find all of your confguration in one place.

**Basic Configuration**
- In the XML Config use the given header:
  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:c="http://www.springframework.org/schema/c"
  xsi:schemaLocation="http://www.springframework.org/schema/beans
  http://www.springframework.org/schema/beans/spring-beans.xsd">
  </beans>
  ```
- To enable component scanning:
  ```xml
  <context:component-scan base-package="com.apress.prospring5.ch3.annotation"/>
  ```
  - You can even specify in which files / types to look for components, but this is a more advanced configuration.
- Having the `component-scan` enabled you can annotate your Java POJOs so that they are picked up by the scanner.
  ```java
  /*...*/
  @Component("provider")
  public class HelloWorldMessageProvider implements MessageProvider {
    /*...*/
  }

  @Service("renderer")
  public class StandardOutMessageRenderer implements MessageRenderer {
    /*...*/
    @Autowired
    public void setMessageProvider(MessageProvider provider) {
      this.messageProvider = provider;
    }
  }
  ```

- To enable component scanning from a java configuration class you have to do the following:
  ```java
  @ComponentScan(basePackages = {"com.apress.prospring5.ch3.annotation"})
  @Configuration
  public class HelloWorldConfiguration {
  }
  ```

**Setter injection**
- To configure setter injection by using XML configuration, you need to specify ``<property>`` tags under the ``<bean>`` tag for each <property> into which you want to inject a dependency.
- You can use the ref attribute to assign a bean reference to a property.
- Since Spring 2.5 you are encouraged to use the p namespace.
  ```xml
  <bean id="renderer"
    class="com.apress.prospring5.ch2.decoupled.StandardOutMessageRenderer">
    <property name="messageProvider" ref="provider"/>
  </bean>

  <!--// With p-namespace!-->
  <bean id="renderer"
  class="com.apress.prospring5.ch2.decoupled.StandardOutMessageRenderer"
  p:messageProvider-ref="provider"/>
  ```
- In the above example when component-scan was enabled we also had setter injection (``setMessageProvider``) . Note the `@Autowired` keyword! The annotation tells the container that a dependency is to be injected via the setter method of the class.
- Spring also supports the `@Inject` and `@Resource` keywords.

**Constructor injection**
- Imagine the following constructor:
  ```java
  public ConfigurableMessageProvider(String message) {
    this.message = message;
  }
  ```
- This can be configured as follows:
  ```xml
  <bean
    class="com.apress.prospring5.ch3.xml.ConfigurableMessageProvider">
  <constructor-arg value="I hope that someone gets my message in a bottle"/>
  </bean>
  ```
  - In this code, instead of using a ``<property>`` tag, we used a ``<constructor-arg>`` tag. Because we are not passing in another bean this time, just a String literal, we use the ``value`` attribute instead of ref to specify the value for the constructor argument. **When you have more than one constructor argument or your class has more than one constructor, you need to give each ``<constructor-arg>`` tag an index attribute to specify the index of the argument, starting at 0**, in the constructor signature.
  - In addition to the p namespace, as of Spring 3.1, you can also use the c namespace, as shown here:
    ```xml
    <bean id="provider"
      class="com.apress.prospring5.ch3.xml.ConfigurableMessageProvider"
      c:message="I hope that someone gets my message in a bottle"/>

    <!--OR:-->
    <bean id="message" class="java.lang.String"
      c:_0="I hope that someone gets my message in a bottle"/>

    <!--ConstructorConfusion-->
    <bean id="constructorConfusion"
      class="com.apress.prospring5.ch3.xml.ConstructorConfusion">
      <constructor-arg type="int">
      <value>90</value>
      </constructor-arg>
    </bean>
    ```
  - In some clases you have multiple constractors with the same name but with different arguments. Then you need to define in which you are asking spring to inject.

**Field Inejction**
- This is done by annotating the class member with the ``@Autowired`` annotation.
- Even if the field is private, but the Spring IoC container does not really care about that; it uses reflection to populate the required dependency.
- However generally Field Injection should be avoided as it makes harder to detect dependencies of a class for the developer etc.

#### Injecting parameters
- By default, not only can the ``<value>`` tag read String values, but it can also
convert these values to any primitive or primitive wrapper class.
- As you can see is possible to define properties on your bean that accept String values, primitive values, or primitive wrapper values and then inject values for these properties by using the ``<value>`` tag.
  ```xml
  <bean id="injectSimpleSpel"
    class="com.apress.prospring5.ch3.xml.InjectSimpleSpel"
    p:name="John Mayer"
    p:age="39"
    p:height="1.92"
    p:programmer="false"
    p:ageInSeconds="1241401112"/>
  ```

- For annotation-style simple value injection, we can apply the ``@Value`` annotation to the bean properties.
  ```java
  @Service("injectSimple")
  public class InjectSimple {
    @Value("John Mayer")
    private String name;
    @Value("39")
    private int age;
    @Value("1.92")
    private float height;
    @Value("false")
  }
  ```

- Injection using ``SpEL``
  - One powerful feature introduced in Spring 3 is the Spring Expression Language (SpEL). SpEL enables you to evaluate an expression dynamically and then use it in Spring’s ``ApplicationContext``. (XML and Annotations)
    ```XML
    <bean id="injectSimpleSpel"
    class="com.apress.prospring5.ch3.xml.InjectSimpleSpel"
    p:name="#{injectSimpleConfig.name}"
    p:age="#{injectSimpleConfig.age + 1}"
    p:height="#{injectSimpleConfig.height}"
    p:programmer="#{injectSimpleConfig.programmer}"
    p:ageInSeconds="#{injectSimpleConfig.ageInSeconds}"/>
    ```
    ```java
    @Value("#{injectSimpleConfig.name}")
    private String name;
    @Value("#{injectSimpleConfig.age + 1}")
    private int age;
    @Value("#{injectSimpleConfig.height}")
    private float height;
    ```
