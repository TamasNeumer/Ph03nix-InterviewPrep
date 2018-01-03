# Spring Docs

## The IoC container
#### Container overview
**What is IoC**  
- IoC is also known as dependency injection (DI) --> It is a process whereby objects define their dependencies (via constructor, factory function arguments, or class properties, that are set after instantiation).
-  The container then injects those dependencies when it creates the bean --> hence the *Inversion of Control*
- The **BeanFactory** interface provides an advanced configuration mechanism capable of managing any type of object. **ApplicationContext** is a sub-interface of BeanFactory. --> **ApplicationContext represents the Spring IoC container** and is responsible for instantiating, configuring, and assembling the aforementioned beans.
- **A bean is an object that is instantiated, assembled, and otherwise managed by a Spring IoC container.**

**Configuration**
- There are three ways to **define configuration**, available in Spring 4 by default:
  - XML
  ```XML
  <?xml version="1.0" encoding="UTF-8"?>
  <beans xmlns="http://www.springframework.org/schema/beans" ...>
    <bean id="accountService" class="com.wiley.beginningspring.ch2.AccountServiceImpl">
        <property name="accountDao" ref="accountDao"/>
    </bean>

    <bean id="accountDao" class="com.wiley.beginningspring.ch2.AccountDaoInMemoryImpl">
    </bean>
  </beans>
  ```
  - Java
    - when configuration is Java class, marked with specific annotations.
    - When Spring encounters a class with the `@Configuration` annotation, it looks for bean instance definitions in the class, which are Java methods decorated with the `@Bean` annotation.

  ```java
  @Configuration
  public class Ch2BeanConfiguration {

      @Bean
      public AccountService accountService() {
          AccountServiceImpl bean = new AccountServiceImpl();
          bean.setAccountDao(accountDao());
          return bean;
      }

      @Bean
      public AccountDao accountDao() {
          AccountDaoInMemoryImpl bean = new AccountDaoInMemoryImpl();
          return bean;
      }
  }
  ```
    - **Note that there are (again) 2 options!!!**
      - You use an external configuration class (code snipped above)
      - Or you mark your original POJO with `@Component` or any of its subtypes (Service, Controller etc.) Such classes are considered as candidates for auto-detection when using annotation-based configuration and classpath scanning! (Then in this you can mark factories with `@Bean`)

  - Groovy-based
    - Configuration is file with Groovy code.

-There are two ways for **bean definition**:
  - configuration inside bean definition, when you add beans manually by declaration right in configuration.
    - For xml-config it will be <bean/> tag, for java-based config - method with @Bean annotation and beans {...} construction for Groovy.
  - annotation based bean definition,
    - when you mark bean classes with specific annotations (like @Component, @Service, @Controller etc). This type of config uses classpath scanning.

**Instantiating the container**  
- The location path or paths supplied to an ApplicationContext constructor are actually resource strings that allow the container to load configuration metadata from a variety of external resources such as the local file system, from the Java CLASSPATH, and so on.
  - `ApplicationContext context = new ClassPathXmlApplicationContext("services.xml", "daos.xml");` --> As you see you can use multiple configuration files, each configuring a different classes. Often each individual XML configuration file represents a logical layer or module in your architecture.
  - `ApplicationContext context = new AnnotationConfigApplicationContext(SequenceGeneratorConfiguration.class);` --> passing a Java class configuration. (preferred way)
- Or you can enable the **ComponentScanner** (seen below) or simply use Spring Boot's annotation: `@SpringBootApplication`

  ```java
  @ComponentScan(
        includeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.REGEX,
                        pattern = {"com.apress.springrecipes.sequence.*Dao",
                        "com.apress.springrecipes.sequence.*Service"})
        },
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ANNOTATION,
                        classes = {org.springframework.stereotype.Controller.class}) }
  )
  /*...*/
  ApplicationContext context =
            new AnnotationConfigApplicationContext("com.apress.springrecipes.sequence");
  ```

**Using the container**  
- ApplicationContext maintains a registry of different beans and their dependencies.
- Use `T getBean(String name, Class<T> requiredType)` to retrieve instances of your beans.
  - `PetStoreService service = context.getBean("petStore", PetStoreService.class);`

#### Bean overview
- Within the container itself, these bean definitions are represented as ``BeanDefinition`` objects, which contain (among other information) the following metadata
  - A package-qualified class name: typically the actual implementation class of the bean being defined.
  - Bean behavioral configuration elements (scope, lifecycle callbacks [initialization, descruction], DI properties, etc.).
  - References to other beans that are needed for the bean to do its work (collaborators / dependencies)
  - Other configuration settings to set in the newly created object.

**Naming beans**
- Every bean has one or more identifiers. These identifiers must be unique within the container that hosts the bean.
- If no name or id is supplied explicitly, the container generates a unique name for that bean.
- **The convention is to use** the standard Java convention for instance field names when naming beans. That is, bean names start with a lowercase letter, and are **camel-cased from** then on.

**Instantiating beans**
- A bean definition essentially is a recipe for creating one or more objects. The container looks at the recipe for a named bean when asked, and uses the configuration metadata encapsulated by that bean definition to create (or acquire) an actual object.
- Instantiating via static method of the class:
  ```XML
  <bean id="clientService"
      class="examples.ClientService"
      factory-method="createInstance"/>
  ```
- Instantiation using another bean's method:

  ```XML
  <bean id="serviceLocator" class="examples.DefaultServiceLocator">
      <!-- inject any dependencies required by this locator bean -->
  </bean>

  <bean id="clientService"
      factory-bean="serviceLocator"
      factory-method="createClientServiceInstance"/>

  <bean id="accountService"
      factory-bean="serviceLocator"
      factory-method="createAccountServiceInstance"/>
  ```

  ```java
  public class DefaultServiceLocator {
      private static ClientService clientService = new ClientServiceImpl();
      private static AccountService accountService = new AccountServiceImpl();

      public ClientService createClientServiceInstance() {
          return clientService;
      }

      public AccountService createAccountServiceInstance() {
          return accountService;
      }
  }
  ```

#### Dependencies
**DI**
- Dependency injection (DI) is a process whereby objects define their dependencies, that is, the other objects they work with, only through constructor arguments, arguments to a factory method, or properties that are set on the object instance after it is constructed or returned from a factory method. The container then injects those dependencies when it creates the bean.

**DI Versions**
- DI Versions:
  - Constructor / static factory function based
  - Setter based
- Rule of thumb:
  - Since you can mix constructor-based and setter-based DI, it is a good rule of thumb to use constructors for mandatory dependencies and setter methods or configuration methods for optional dependencies. Note that use of the @Required annotation on a setter method can be used to make the property a required dependency.
  - The Spring team generally advocates constructor injection as it enables one to implement application components as immutable objects and to ensure that required dependencies are not null.
- Spring detects circular dependencies.

**Injecting via @Autowired**
- To use automatic injecton use the `@Autowired` keyword. (If there is only one implementation it works.)
- Constructors that take other class references as args are autowired.
- You can make certain components optional with `@Autowired(required=false)`
- To resolve ambiguity use the `@Primary` to annotate one of the implementations or use qualifier to express what do you want to inject. (`@Qualifier("datePrefixGenerator")`)
- If you don't have ComponentScan on, but you want to include other Config's benas you can use `@Import(PrefixConfiguration.class)` and use the registered bean names (`@Value("#{datePrefixGenerator}")`)

**Injecting primitive types**
- @Value("${some.property:defaultvalue}")'
- In XML: `<property name="driverClassName" value="com.mysql.jdbc.Driver"/>` or `p:driverClassName="com.mysql.jdbc.Driver"`

**Beans depending on each other**
- The `depends-on` attribute can explicitly force one or more beans to be initialized before the bean using this element is initialized.
  - `<bean id="beanOne" class="ExampleBean" depends-on="manager"/>`
  - `@DependsOn(value = { "beanTwo", "beanThree" })`

**Lazy-initialization**
- Spring uses **eager** initialization, i.e. all beans are created once found by the app context. To make it lazy do the following:
  - `<bean id="lazy" class="com.foo.ExpensiveToCreateBean" lazy-init="true"/>`
  - `@Lazy` (mark a COMPONENT) lazy. --> if another class depends on such a lazy-initialized class, you mark the dependency with `@Lazy`. For example: `private @Autowired @Lazy Address address;` The Address class will only be initialized once a function is called on the object. Havent been for the `@Lazy` annotation int he dependency, the Adress would have been initialized at startup as well.

**Method injection, inversion of control**
- Normally you handle dependencies by adding a class as a property to another class. (Delegation) Assume that A class uses B to delegate methods. However if A's scope is `singleton`, but B is `prototype`, the container won't inject a new B instance on every call of B.someFunction(). The container **only creates the singleton bean A once**, and thus only gets one opportunity to set the properties.
- Solution: Inversion of Control: You can make bean A aware of the container by implementing the ApplicationContextAware interface, and by making a getBean("B") call to the container ask for (a typically new) bean B instance every time bean A needs it.

  ```java
  public class CommandManager implements ApplicationContextAware {

      private ApplicationContext applicationContext;

      public Object process(Map commandState) {
          // grab a new instance of the appropriate Command
          Command command = createCommand();
          // set the state on the (hopefully brand new) Command instance
          command.setState(commandState);
          return command.execute();
      }

      protected Command createCommand() {
          // notice the Spring API dependency!
          return this.applicationContext.getBean("command", Command.class);
      }

      public void setApplicationContext(
              ApplicationContext applicationContext) throws BeansException {
          this.applicationContext = applicationContext;
      }
  }
  ```

- Such IoC is disadvantageous, as the POJOs are not "POJOs" anymore - they are aware of their container.
- **SOLUTION: Lookup method injection**
  - Lookup method injection is the ability of the container to override methods on container managed beans, to return the lookup result for another named bean in the container.
  - Instead of creating a new object using the container, you configure the container so that it calls another bean's method (which can be prototype scoped).

  ```xml
  <!-- a stateful bean deployed as a prototype (non-singleton) -->
  <bean id="myCommand" class="fiona.apple.AsyncCommand" scope="prototype">
      <!-- inject dependencies here as required -->
  </bean>

  <!-- commandProcessor uses statefulCommandHelper -->
  <bean id="commandManager" class="fiona.apple.CommandManager">
      <lookup-method name="createCommand" bean="myCommand"/>
  </bean>
  ```

  ```java
  public abstract class CommandManager {

      public Object process(Object commandState) {
          Command command = createCommand();
          command.setState(commandState);
          return command.execute();
      }

      @Lookup("myCommand")
      protected abstract Command createCommand();
  }
  ```

#### Bean scopes
- When you create a bean definition, you create a recipe for creating actual instances of the class defined by that bean definition.
- Scopes
  - Singletons (default) only instance is created in the IoC container.
  - Prototype - A new instance is created each time the bean is requested.
    - Spring does not manage the complete lifecycle of a prototype bean: initialization callbacs are called, but destruction callbacks are not. The client code must clean up prototype-scoped objects and release expensive resources that the prototype bean(s) are holding. (**!!!!**)
  - Request - A single instance per http request.
  - Session - A single instance per http session.
  - Application - scoped to the life-cycle of a ServletContext.
  - WebSocket - life-cycle of a web socket.
- To use scopes simply mark your components with `@Scope("scopeType")`

#### Customizing the nature of beans
**Life-cycle callbacks**
- To interact with the container’s management of the bean lifecycle, you can implement the Spring `InitializingBean` and `DisposableBean` interfaces. The container calls `afterPropertiesSet()` for the former and `destroy()` for the latter to allow the bean to perform certain actions upon initialization and destruction of your beans.
- The `InitializingBean` interface specifies a single method:
  - `void afterPropertiesSet() throws Exception;`
  - It is recommended that you **do not use the InitializingBean interface** because it unnecessarily couples the code to Spring. Instead use the **@PostConstruct annotation**.
- Usage: Annotate the functions you want to call.
  - `@PostConstruct`, `@Predestroy.`
#### Java-based container configuring
**Conditional configuration**
- It is often useful to conditionally enable or disable a complete @Configuration class, or even individual @Bean methods, based on some arbitrary system state.
- Use the `@Profile()` keyword.
  - `@Profile("de")`
  - applocation.properties: `spring.profiles.active=de`
