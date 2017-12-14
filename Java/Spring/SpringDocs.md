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

  
