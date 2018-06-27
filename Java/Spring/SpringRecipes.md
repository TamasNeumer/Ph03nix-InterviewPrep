# Spring Recipes

## 1. Core Tasks

### 1.1 Use Configuration file to configure beans

#### Definition

- Annotate the given class with `@Configuration`
- Add public methods that create POJO instances and annotate these with `@Bean`

    ```java
    @Configuration
    public class SequenceGeneratorConfiguration {

      @Bean
      public SequenceGenerator sequenceGenerator() {
      SequenceGenerator seqgen = new SequenceGenerator();
      // Set fields
      return seqgen;
      }
    }
    ```

- When Spring encounters a class with the `@Configuration` annotation, it looks for bean instance definitions in the class, which are Java methods decorated with the `@Bean` annotation. The Java methods create and return a bean instance.
- If not specified the bean will be named the class name with lower-first letter. ("camel case") -> "sequenceGeneratorConfiguration". Alternatively you can specify the name `@Bean(name="mys1")`

#### Instantiation

- Spring provides two types of IoC container implementations. The basic one is called a *bean factory*. The more advanced one is called an *application context*, which is compatible with the bean factory.
- Using the **application context** is recommended unless resources are low (e.g.: applet/mobile)
- `ApplicationContext` is an interface, instantiate with `AnnotationConfigApplicationContext` (as recommended)

    ```java
    // Registering our config
    ApplicationContext context = new AnnotationConfigApplicationContext
      (SequenceGeneratorConfiguration.class);

    // Instantiating objects
    //- with casting
    SequenceGenerator generator = (SequenceGenerator) context.getBean("sequenceGenerator");
    //-without casting
    SequenceGenerator generator = context.getBean("sequenceGenerator",SequenceGenerator.class);
    //-If there is only a single bean for the class:
    SequenceGenerator generator = context.getBean(SequenceGenerator.class);
    ```

#### DAO Pattern

- You have an interface that is implemented by the DAO classes.

    ```java
    @Component("sequenceDao")
    public class SequenceDaoImpl implements SequenceDao {
      //... impl ...
    }
    ```

- `@Component` is a general-purpose annotation to decorate POJOs for Spring detection, whereas `@Repository`, `@Service`, and `@Controller` are specializations of @Component for more specific cases of POJOs associated with the persistence, service, and presentation layers.

#### Filtering Bean Scanning

- By default, Spring detects all classes decorated with `@Configuration`, `@Bean`, `@Component`, `@Repository`, `@Service`, and `@Controller` annotations, among others.
- **Scanning every package can slow down the startup process unnecessarily.**
- Spring supports four types of filter expressions. The **annotation** and **assignable** types are to specify an annotation type and a class/interface for filtering. The **regex** and **aspectj** types allow you to specify a regular expression and an AspectJ pointcut expression for matching the classes.

    ```java
    @ComponentScan(
      includeFilters = {
        @ComponentScan.Filter(
          type = FilterType.REGEX,
          pattern = { "com.apress.springrecipes.sequence.*Dao",
          "com.apress.springrecipes.sequence.*Service"})
      },
      excludeFilters = {
        @ComponentScan.Filter(
          type = FilterType.ANNOTATION,
          classes = {org.springframework.stereotype.Controller.class})
      }
    )
    ```
  - The following component scan includes all classes in `com.apress.springrecipes.sequence` whose name contains the word `Dao` or `Service` and excludes the classes with the `@Controller` annotation