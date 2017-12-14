# Spring

#### 1. Spring Into Action
- Spring avoids (as much as possible) littering your application code with its API . Spring almost never forces you to implement a Spring-specific interface or extend a Spring-specific class.
- With **DI** , objects are given their dependencies at creation time by some third party that coordinates each object in the system. (=> You expect an interface and the concrete implementation is given to you at run-time.)
- In Spring, there are many ways to wire components together, but a common approach has always been via XML . The next listing shows a simple Spring configuration file, knights.xml, that wires a BraveKnight , a SlayDragonQuest , and a PrintStream together.

```XML
<bean id="knight" class="com.springinaction.knights.BraveKnight">
    <constructor-arg ref="quest" />
</bean>

<bean id="quest" class="com.springinaction.knights.SlayDragonQuest">
    <constructor-arg value="#{T(System).out}" />
</bean>
```

- **AOP** (Aspect Oriented Programming) enables you to capture functionality that’s used throughout your application in reusable components.
- With AOP , you can then cover your core application with layers of functionality. These layers can be applied declaratively throughout your application in a flexible manner without your core application even knowing they exist. This is a powerful concept, because it keeps the security, transaction, and logging concerns from littering the application’s core business logic.

```XML
<bean id="minstrel" class="com.springinaction.knights.Minstrel">
    <constructor-arg value="#{T(System).out}" />
</bean>

<aop:config>
  <aop:aspect ref="minstrel">
    <aop:pointcut id="embark"
      expression="execution(* *.embarkOnQuest(..))"/>
    <aop:before pointcut-ref="embark"
      method="singBeforeQuest"/>
    <aop:after pointcut-ref="embark"
      method="singAfterQuest"/>
  </aop:aspect>
</aop:config>
```

- Minstrel can be applied to BraveKnight without BraveKnight needing to explicitly call on it. In fact, BraveKnight remains completely unaware of Minstrel ’s existence.

- **Templating:** Spring seeks to eliminate boilerplate code by encapsulating it in templates. Spring’s JdbcTemplate makes it possible to perform database operations without all the ceremony required by traditional JDBC.


#### 2. Writing Beans
- When it comes to expressing a bean wiring specification, Spring is incredibly flexible, offering three primary wiring mechanisms:
  - Explicit configuration in XML
  - Explicit configuration in Java
  - Implicit bean discovery and automatic wiring
- The author recommends: Auto > Java > XML. --> Use XML only if the code you want to use has no equivalent in JavaConfig.
- As a general rule, he favors constructor injection for hard dependencies and property injection for any optional dependencies.

- Normally your beans are named as your class, but with camelCase. You can overwrite this by: `@Component("lonelyHeartsClub")`

**Enabling Autowiring**
- Enabling Autowiring and component search:
  - In order to let Spring find your components and services annotate these classes with `@Component` or `@Service` etc. Also to enable component scanning you have to have a Config class.
    - **`@Configuration` "Indicates that a class declares one or more @Bean methods and may be processed by the Spring container to generate bean definitions and service requests for those beans at runtime"**

  ```java
  @Configuration
  @ComponentScan
  public class CDPlayerConfig {
  }
  ```
  - In XML it would look like this: `<context:component-scan base-package="soundsystem"/>`
  - However new you still have to annotate the class where you want to DI: `@ContextConfiguration(classes=CDPlayerConfig.class)`
  - To specify different base packages to scan you can `@ComponentScan(basePackages={"soundsystem", "video"})`

**DI and @Autowired**
- The `@Autowired` annotation makes sure that your dependencies are injected.
- There are 3 cases of DI:
  - via public class property
  - via setter
  - via constructor
- Constructor DI is preferred, in this case you don't even have to use the `@Autowire` annotation.
- If you have multiple implementation you MUST tell spring which implementation to inject.
  - `@Qualifier("greetingServiceImpl")` is one option. In the constructor it would look like: `ConstructorInjectedController(@Qualifier("constructorGreetingService") GreetingService greetingService)`
    - Note that changing the class names will make qualifiers inefective. Hence it is advised to use aliases
  - `@Primary` -> making an implementation the primary implementation

    ```XML
    <bean id="iceCream"
    class="com.desserteater.IceCream"
    primary="true" />
    ```

  - Using profies, e.g.: `@Profile("de")`, in this case you have to specifiy the used profile in the `application.properties` spring.profiles.active=de.
    - (Note that you can use `@Profile` both on function and method level.)
    - In XML Format:

    ```XML
    <beans profile="dev">
      <jdbc:embedded-database id="dataSource">
        <jdbc:script location="classpath:schema.sql" />
        <jdbc:script location="classpath:test-data.sql" />
      </jdbc:embedded-database>
    </beans>

    <context-param>
      <param-name>spring.profiles.default</param-name>
      <param-value>dev</param-value>
    </context-param>
    ```

- You can make autowiring optional. `@Autowired(required=false)` Spring will attempt to perform autowiring; but if there are no matching beans, it will leave the bean unwired.
- `@Autowired` is spring specific, if you don't like it you can use `@Inject`

**Manual wiring using Java**
- The key to creating a JavaConfig class is to annotate it with `@Configuration`.
- To declare a bean in JavaConfig, you write a method that creates an instance of the desired type and annotate it with `@Bean`. The `@Bean` annotation tells Spring that this method will return an object that should
be registered as a bean in the Spring application context.

  ```java
  @Bean
  // or @Bean(name="lonelyHeartsClubBand")
  public CompactDisc sgtPeppers() {
    return new SgtPeppers();
  }
  ```

- The simplest way to wire up beans in JavaConfig is to refer to the referenced bean’s method. It appears that the CompactDisc is provided by calling sgtPeppers , but that’s not exactly true. Because the sgtPeppers() method is annotated with `@Bean`, Spring will intercept any calls to it and ensure that the bean produced by that method is returned rather than allowing it to be invoked again. By default, all beans in Spring are singletons, and there’s no reason you need to create a duplicate instance for the second CDPlayer bean.

```java
@Bean
public CDPlayer cdPlayer() {
  return new CDPlayer(sgtPeppers());
}
```

- Or simpl name the arg to camelCaseClassName, and it will get autowired. (In constructor as well as in setter.)

```java
@Bean
public CDPlayer cdPlayer(CompactDisc compactDisc) {
  return new CDPlayer(compactDisc);
}

@Bean
public CDPlayer cdPlayer(CompactDisc compactDisc) {
  CDPlayer cdPlayer = new CDPlayer(compactDisc);
  cdPlayer.setCompactDisc(compactDisc);
  return cdPlayer;
}
```

**Manual wiring using XML**
- Put the XML in the resources folder. New -> XML configuration -> Spring configuration.
- For XML configuration, that means creating an XML file rooted with a `<beans>` element.

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
      http://www.springframework.org/schema/beans/spring-beans.xsd
      http://www.springframework.org/schema/context">
      <!-- configuration details go here -->
  </beans>
  ```

- Declare a bean like: `<bean class="soundsystem.SgtPeppers" />`
  - When Spring sees this <bean> element, it will create a SgtPeppers bean for you by calling its default constructor.
- In order to inject the SgtPeppers bean to CDPlayer:

  ```java
  <bean id="cdPlayer" class="soundsystem.CDPlayer">
    <constructor-arg ref="compactDisc" />
  </bean>
  ```

  Or you use the c-namespace notation:

  ```java
  <bean id="cdPlayer" class="soundsystem.CDPlayer"
    c:cd-ref="compactDisc" />
  ```

    - c --> c-namespace prefix
    - cd --> constructor argument name (or refer to parameter position e.g.: `_0` --> `c:_0-ref="compactDisc"`)
    - ref --> injecting a bean reference
    - "" --> the ID of the bean to inject

- If you have a constructor that takes string arguments you can do the following:

  ```XML
  <bean id="compactDisc"
    class="soundsystem.BlankDisc">
    <constructor-arg value="Sgt. Pepper's Lonely Hearts Club Band" />
    <constructor-arg value="The Beatles" />
  </bean>

  <bean id="compactDisc"
    class="soundsystem.BlankDisc"
    c:_0="Sgt. Pepper's Lonely Hearts Club Band"
    c:_1="The Beatles" />
  ```

- You can pass `null` as argument by: `<constructor-arg><null/></constructor-arg>`
- Or you inject collection values or collection of references:

  ```XML
  <list>
    <value>Sgt. Pepper's Lonely Hearts Club Band</value>
    <value>With a Little Help from My Friends</value>
    <value>Lucy in the Sky with Diamonds</value>
    <value>Getting Better</value>
    <value>Fixing a Hole</value>
    <!-- ...other tracks omitted for brevity... -->
  </list>

  <constructor-arg>
    <list>
    <ref bean="sgtPeppers" />
    <ref bean="whiteAlbum" />
    <ref bean="hardDaysNight" />
    <ref bean="revolver" />
    ...
    </list>
</constructor-arg>
  ```

- If you want to **inject into the setter**:
  - cdPlayer doesn't require args at Bean definition.
  - Then in the properties you specify the member value.

```java
<bean id="cdPlayer" class="soundsystem.CDPlayer">
  <property name="compactDisc" ref="compactDisc" />
</bean>

//p-namespace
<bean id="cdPlayer" class="soundsystem.CDPlayer"
  p:compactDisc-ref="compactDisc" />

// Injecting literals:
<property name="artist" value="The Beatles" />
<property name="tracks">
<list>
  <value>Getting Better</value>
  <value>Fixing a Hole</value>
</list>
</property>

// Optionally use util:list ti create a bean and inject that as property
<util:list id="trackList">
  <value>Getting Better</value>
  <value>Fixing a Hole</value>
  <!-- ...other tracks omitted for brevity... -->
</util:list>

<bean id="compactDisc"
  class="soundsystem.BlankDisc"
  p:title="Sgt. Pepper's Lonely Hearts Club Band"
  p:artist="The Beatles"
  p:tracks-ref="trackList" />
```

**Importing configuration**
- `@Import({CDPlayerConfig.class, CDConfig.class})` (Java)
- `@ImportResource("classpath:cd-config.xml")` (XML)

#### 3. Advanced Wiring
**Conditional beans**
- ``@Conditional`` annotation that can be applied.
- The class given to ``@Conditional`` can be any type that implements the Condition interface. As you can see, it’s a straightforward interface to implement, requiring only that you provide an implementation for the ``matches()`` method. If the ``matches()`` method returns true, then the`` @Conditional``-annotated beans are created. If ``matches()`` returns false, then those beans aren’t created.

```java
@Bean
@Conditional(MagicExistsCondition.class)
  public MagicBean magicBean() {
  return new MagicBean();
}

public class MagicExistsCondition implements Condition {
  public boolean matches(
          ConditionContext context, AnnotatedTypeMetadata metadata) {
      Environment env = context.getEnvironment();
      return env.containsProperty("magic");
  }
}
```

- From the ``ConditionContext``, you can:
  - Check for the presence of beans, and even dig into bean properties
  - Check for the presence and values of environment variables via the ``Environment``
  - Read and inspect the contents of resources loaded via the ``ResourceLoader``
  - Load and check for the presence of classes via the ``ClassLoader`` returned from ``getClassLoader()``.
- ``AnnotatedTypeMetadata`` offers you a chance to inspect annotations that may also be placed on the ``@Bean`` method.

**Spring Bean Scopes**
- singletons (default) only instance is created in the IoC container.
- Prototype - A new instance is created each time the bean is requested.
- Request - A single instance per http request.
- Session - A single instance per http session.
- Application - scoped to the life-cycle of a ServletContext.
- WebSocket - life-cycle of a web socket


#### Property values
**Property values from config file**  
- Imagine that you want to have a "config" file, in which you store username, pw, dburl etc.
- Create a file under resources (e.g. `datasource.properties`) and fill it with config values (username=tamas, password=password)

  ```java
  @Configuration
  @PropertySource("classpath:datasource.properties")
  public class PropertyConfig {

      // Read out values from the properties file
      @Value("${guru.username}")
      String user;

      @Value("${guru.password}")
      String password;

      @Value("${guru.dburl}")
      String url;
      //...
    }
  ```

**Property values from environment vars**  
- The above mentioned keywords (guru.username) work for environment variables as well. (i.e. if you had a "GURU_USERNAME" environment var, then it would still work!) However probably it is more neat to do it via the `Environment`

  ```java
  @Autowired
  Environment env;
  //...
  String username = env.getProperty("USERNAME");
  ```

- You can specify multiple PropertySources via

  ```java
  @PropertySources({
          @PropertySource("classpath:datasource.properties"),
          @PropertySource("classpath:jms.properties")
  })
  ```

**Property values form Spring Boot's application.properties**
- You can also move your properties to spring boot's default properties file.
- If you are using spring boot then  you can move your property values there and you can start referencing right away!

**Managing Properties with YAML**
- Create a file named `application.yml` and put your stiff in there. Referencing is the same as before, spring picks up the file and parses it for you.

**Managing propertiees with properties files**
- Add `application-de.properties` as a file.
- As profile.active=de --> the part that follows the original config is overwritten by the `application-de.properties`
- You can pull off the same stuff with yml files.

  ```yml
  jms:
    username: JMS Username
    password: samepass
    url: SomeURL

  ---
  spring:
    profiles: de
  jms:
    username: JMS Username $$$ German
  ```

#### Spring Web (MVC)
**Create Project**
- Core --> DevTools
- Web --> Web
- Template --> Thymeleaf
- SQL --> JPA (for Hybernate) and H2

**Recap HTTP Methods**
- **GET** get data (fetch data etc.)
- **POST** post data to server
- **PUT** is a request for an entity to be stored at the supplied URI. If entity exists, it is expected to be updated.
  - POST is a create request, PUT is a create or update request.
- **DELETE** - request to DELETE
- **TRACE** - will echo the received request (can be used to see if request was altered by intermediate servers)
- **CONNECT** - connect TCP/IP channel (for HTTPS)
- **PATCH** - partial modifications to the resources

- Safe methods (don't cause changes on server)
  - GET, HEAD, OPTION, TRACE
- Idempotent (action has only effect once)
  - PUT, DELETE

- Status codes
  - 100 informational in nature
  - 200 successful request (200 okay, 201, created, 204 accepted)
  - 300 series of redirections (301 moved permanently)
  - 400 error (401 not authorized, 404 not found)
  - 500 internal server side error (503 service unavailable)

#### JPA
- `@OneToOne`
- `@OneToMany`(List, Set, Map, SortedSet, SortedMap)
- `@ManyToOne`
- `@ManyToMany` (each has a List or Set reference to the other)

**Unidirectional vs Bidirectional**
- Uni - only one knows about the connection.
- Bi - both know about the connection. Prefer Bidirectional normally!

**Owning Side**
- The owning side holds the foregin key in the database.

**Fetch types**
- Eager vs Lazy
- Default JPA 2.1:
  - OneToMany - Lazy
  - ManyToOne - Eager
  - ManyToMany - Lazy
  - OneToOne - Eager

**Cascade Types**
- How state changes are cascaded from parent objects to child objects
- PERSIST - Save operations will cascade to related entities
- MERGE - related entities are merged when the owning entity is merged
- REFRESH - related entities are refreshed when the owning entity is refreshed.
- REMOVE - removes all related entities when the owning entity is deleted
- DETACH - detaches all related entities if a manual detach occurs
- ALL - applies all the above
- By default NO operatios are cascaded.
