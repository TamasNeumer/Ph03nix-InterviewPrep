# Spring Recipes

## 1. Core Tasks

### Definition

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

### Instantiation

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

### DAO Pattern

- You have an interface that is implemented by the DAO classes.

    ```java
    @Component("sequenceDao")
    public class SequenceDaoImpl implements SequenceDao {
      //... impl ...
    }
    ```

- `@Component` is a general-purpose annotation to decorate POJOs for Spring detection, whereas `@Repository`, `@Service`, and `@Controller` are specializations of @Component for more specific cases of POJOs associated with the persistence, service, and presentation layers.

### Filtering Bean Scanning

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
  - The following component scan includes all classes in `com.apress.springrecipes.sequence` whose name contains the word `Dao` or `Service` and excludes the classes with the `@Controller` annotation.

### Autowire POJO Fields with the @Autowired Annotation

- The `@Autowired` annotation can also be applied to a property of an array type to have Spring autowire all the matching beans. For example, you can annotate a `PrefixGenerator[]` property with `@Autowired`.
- In a similar way, you can apply the @Autowired annotation to a type-safe collection. Spring can read the type information of this collection and autowire all the beans whose type is compatible.
- If Spring notices that the @Autowired annotation is applied to a type-safe java.util.Map with strings as the keys, it will add all the beans of the compatible type, with the bean names as the keys, to this map.
    ```java
    @Component
    public class SequenceService {
      @Autowired
      private SequenceDao sequenceDao;

      //Or:
      @Autowired
      private PrefixGenerator[] prefixGenerators;

      //Or:
      @Autowired
      private List<PrefixGenerator> prefixGenerators;

      // Or
      @Autowired
      private Map<String, PrefixGenerator> prefixGenerators;

      public void setSequenceDao(SequenceDao sequenceDao) {
        this.sequenceDao = sequenceDao;
      }
    }
    ```

- By default, all the properties with `@Autowired` are required. When Spring can’t find a matching bean to wire, it will throw an exception. If you want a certain property to be optional, set the required attribute of `@Autowired` to false. Then, when Spring can’t find a matching bean, it will leave this property unset.

    ```java
    @Autowired(required=false)
    public void setPrefixGenerator(PrefixGenerator prefixGenerator) {
      this.prefixGenerator = prefixGenerator;
    }
    ```

- You can also apply the `@Autowired` keyword on the constructor, however since Spring 4.3 this is redundant.

### Resolve Autowire Ambiguity with Annotations

- The `@Primary` annotation gives preference to a bean when multiple candidates are qualified to autowire a single-valued dependency.
- Or you can do with the `@Qualifier` annotation. Once you’ve done this, Spring attempts to find a bean with that name in the IoC container and wire it into the property.
    ```java
    @Autowired
    @Qualifier("datePrefixGenerator")
    private PrefixGenerator prefixGenerator;
    ```
  - You can use the `@Qualifier` for method arguments as well.

### Resolve POJO References from Multiple Locations

- You can have multiple Configuration classes. In this case you tell the Container which configuration classes to use:

    ```java
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext
      (PrefixConfiguration.class, SequenceGeneratorConfiguration.class);
    ```

- Another alternative is to use the `@Import` annotation so Spring makes the POJOs from one configuration file available in another.

    ```java
    @Configuration
    @Import(PrefixConfiguration.class)
    public class SequenceConfiguration {
      @Value("#{datePrefixGenerator}")
      private PrefixGenerator prefixGenerator;
      //...
    }
    ```

### POJO scope with @Scope

- Four possible scopes:
  - **singleton** - Creates a single bean instance per Spring IoC container
  - **prototype** - Creates a new bean instance each time when requested
  - **request** - Creates a single bean instance per HTTP request; valid only in the context of a web application
  - **session** - Creates a single bean instance per HTTP session; valid only in the context of a web application
  - **globalSession** - Creates a single bean instance per global HTTP session; valid only in the context of a portal application

    ```java
    @Component
    @Scope("prototype")
    public class ShoppingCart { ... }
    ```

### Data from external files

- Spring offers the `@PropertySource` annotation as a facility to load the contents of a .properties file (i.e., key-value pairs) to set up bean properties.

    ```java
    @Configuration
    @PropertySource("classpath:discounts.properties")
    @ComponentScan("com.apress.springrecipes.shop")
    public class ShopConfiguration {
      @Value("${endofyear.discount:0}")
      private double specialEndofyearDiscountField;
    }
    ```

- `file:c:/shop/banner.txt` - you can give a system-wide URI
- If the resource is located in a particular package, you can specify the absolute path from the classpath root.
  - `classpath:com/apress/springrecipes/shop/banner.txt`

### Initialization & Life Cycle Hooks

- By default, Spring performs eager initialization on all POJOs. This means POJOs are initialized at startup. Spring can also delay the creation of a bean up until the point it’s required—a process called lazy initialization—with the `@Lazy` annotation. 
- As an application’s POJOs grow, so does the number of POJO initializations. This can create race conditions if POJOs reference one another and are spread out in different Java configuration classes. Spring can also ensure the initialization of certain beans before others with the `@DependsOn` annotation. (Annotation can be applied to )

    ```java
    @Bean
    @DependsOn("datePrefixGenerator")
    public SequenceGenerator sequenceGenerator() {
      SequenceGenerator sequence= new SequenceGenerator();
      sequence.setInitial(100000);
      sequence.setSuffix("A");
      return sequence;
    }
    ```

- `@PostConstruct` can be used to initialize files if they don't exist etc.
- `@PreDestroy` can be used to clean up resources at the end of the bean life-cycle.
  - `writer.close()`