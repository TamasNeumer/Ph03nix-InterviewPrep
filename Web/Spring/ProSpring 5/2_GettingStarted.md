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

#### Summary & Exercises
- In Spring you can configure your beans via XML and Java Annotations.
- Task:
  - Create a Maven project. Create an interface `DAO` that has a method `void saveData(String dataToSave);`
  - Create a `MockDao` class that implements the interface.
  - Create a `Service` class that depends on a DAO instance. (Reference to DAO)
    - Note: You must create getters and setters if the attribute is private!
  - Add `app-context.xml` to the resources and configure it.
    - Note: in `p:dao-ref="mockDao"` it is important that "dao" is the variable name, while DAO was the class name in my case.
  - In the main class use set up the `ApplicationContext`, in this case `ClassPathXmlApplicationContext`
  - Get a Serice instance from the ApplicatioNContext and call the method. See if the injection worked.

```java
public class Main {
    public static void main(String... args){
        ApplicationContext ctx = new ClassPathXmlApplicationContext("app-context.xml");
        Service service = ctx.getBean("service", Service.class);
        service.useDaoToSaveStuff("StuffToSave");

    }
}

public interface DAO {
    void saveData(String dataToSave);
}

public class MockDao implements DAO {
    public void saveData(String dataToSave){
        System.out.println("The data: " + dataToSave + " has been saved using the mock");
    }
}

public class Service {
    DAO dao;

    public void useDaoToSaveStuff(String stuffToSave){
        dao.saveData(stuffToSave);
    }

    public DAO getDao() {
        return dao;
    }

    public void setDao(DAO dao) {
        this.dao = dao;
    }
}
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:p="http://www.springframework.org/schema/p"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd">

    <bean id="mockDao" class="MockDao"/>
    <bean id="service" class="Service" p:dao-ref="mockDao"/>
</beans>

// MAVEN:
<dependencies>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>5.0.4.RELEASE</version>
    </dependency>
</dependencies>
```

```java

@Configuration
public class JavaConfig {
    @Bean
    DAO mockDao(){
        return new MockDao();
    }

    @Bean
    Service service(){
        Service service = new Service();
        service.setDao(mockDao());
        return service;
    }
}
```
