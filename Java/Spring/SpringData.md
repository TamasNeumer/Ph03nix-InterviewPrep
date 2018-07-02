# Spring Data

## CRUD Repository

- **Basics**
  - The `CrudRepository` provides sophisticated CRUD functionality for the entity class that is being managed.
      ```java
      public interface CrudRepository<T, ID extends Serializable>
        extends Repository<T, ID> {
        <S extends T> S save(S entity);
        Optional<T> findById(ID primaryKey);
        Iterable<T> findAll();
        long count();
        void delete(T entity);
        boolean existsById(ID primaryKey);
        // … more functionality omitted.
      }
      ```
  - On top of the `CrudRepository`, there is a `PagingAndSortingRepository` abstraction that adds additional methods to ease paginated access to entities:
    ```java
    public interface PagingAndSortingRepository<T, ID extends Serializable>
      extends CrudRepository<T, ID> {
      Iterable<T> findAll(Sort sort);
      Page<T> findAll(Pageable pageable);
    }
    ```
  - E.g.: `Page<User> users = repository.findAll(new PageRequest(1, 20));`
- **Picking out the required methods only**
  - Typically, your repository interface extends Repository, `CrudRepository`, or `PagingAndSortingRepository`. Alternatively, if you do not want to extend Spring Data interfaces, you can also annotate your repository interface with `@RepositoryDefinition`. Extending `CrudRepository` exposes a complete set of methods to manipulate your entities. If you prefer to be selective about the methods being exposed, copy the methods you want to expose from `CrudRepository` into your domain repository.
    ```java
    @NoRepositoryBean
    interface MyBaseRepository<T, ID extends Serializable> extends Repository<T, ID> {
      Optional<T> findById(ID id);
      <S extends T> S save(S entity);
    }

    interface UserRepository extends MyBaseRepository<User, Long> {
      User findByEmailAddress(EmailAddress emailAddress);
    }
    ```
- **Error handling and Nullability**
  - **Annotations**
    - `@NonNullApi` - Used on the *package level* to declare that the default behavior for parameters and return values is to not accept or produce `null` values.
    - `@NonNull` - Used on a parameter or return value that must not be `null` (not needed on a parameter and return value where `@NonNullApi` applies).
    - `@Nullable` -  Used on a parameter or return value that can be `null`.
  - **Examples**
    - `User getByEmailAddress(EmailAddress emailAddress);`
      - Throws an `EmptyResultDataAccessException` when the query executed does not produce a result. Throws an `IllegalArgumentException` when the `emailAddress` handed to the method is `null`.
    - `@Nullable User findByEmailAddress(@Nullable EmailAddress emailAdress);`
      - Returns `null` when the query executed does not produce a result. Also accepts `null` as the value for `emailAddress`.
    - `Optional<User> findOptionalByEmailAddress(EmailAddress emailAddress);`
      - Returns `Optional.empty()` when the query executed does not produce a result. Throws an `IllegalArgumentException` when the `emailAddress` handed to the method is `null`.
- **Multiple data sources**
  - Sometimes you have multiple data sources in a project and you need to tell spring where to find the data when using a given query. Here comes the `@EnableJpaRepositories` in play. For more info: [Link](https://stackoverflow.com/questions/45663025/spring-data-jpa-multiple-enablejparepositories)
- **Defining Query Methods**
  - **Query Creation**
    - The query builder mechanism built into Spring Data repository infrastructure is useful for building constraining queries over entities of the repository. The mechanism strips the prefixes `find…By`, `read…By`, `query…By`, `count…By`, and `get…By` from the method and starts parsing the rest of it. The introducing clause can contain further expressions, such as a `Distinct` to set a distinct flag on the query to be created. However, the first `By` acts as delimiter to indicate the start of the actual criteria. At a very basic level, you can define conditions on entity properties and concatenate them with And and Or. The following example shows how to create a number of queries:
    - In addition to query methods, query derivation for both count and delete queries is available. In such cases Spring automatically generates the function implementations for you (just based on the function name.)
    - **More on query keywords and query creation: [Link](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation)**
        ```java
        interface PersonRepository extends Repository<User, Long> {

          List<Person> findByEmailAddressAndLastname(EmailAddress emailAddress, String lastname);

          // Enables the distinct flag for the query
          List<Person> findDistinctPeopleByLastnameOrFirstname(String lastname, String firstname);
          List<Person> findPeopleDistinctByLastnameOrFirstname(String lastname, String firstname);

          // Enabling ignoring case for an individual property
          List<Person> findByLastnameIgnoreCase(String lastname);
          // Enabling ignoring case for all suitable properties
          List<Person> findByLastnameAndFirstnameAllIgnoreCase(String lastname, String firstname);

          // Enabling static ORDER BY for a query
          List<Person> findByLastnameOrderByFirstnameAsc(String lastname);
          List<Person> findByLastnameOrderByFirstnameDesc(String lastname);
        }
        ```
  - **Property Expressions**
    - You can also define constraints by traversing nested properties.
      - `List<Person> findByAddressZipCode(ZipCode zipCode);`
    - Assume a `Person` has an `Address` with a `ZipCode`. In that case, the method creates the property traversal x`.address.zipCode`. The resolution algorithm starts by interpreting the entire part (`AddressZipCode`) as the property and checks the domain class for a property with that name (uncapitalized). If the algorithm succeeds, it uses that property. If not, the algorithm splits up the source at the camel case parts from the right side into a head and a tail and tries to find the corresponding property — in our example, `AddressZip` and `Code`. If the algorithm finds a property with that head, it takes the tail and continues building the tree down from there, splitting the tail up in the way just described. If the first split does not match, the algorithm moves the split point to the left (`Address`, `ZipCode`) and continues.
    - Although this should work for most cases, it is possible for the algorithm to select the wrong property. Suppose the `Person` class has an `addressZip` property as well. The algorithm would match in the first split round already, choose the wrong property, and fail (as the type of `addressZip` probably has no `code` property). To resolve this ambiguity you can use `\_` inside your method name to manually define traversal points. So our method name would be as follows:
      - `List<Person> findByAddress_ZipCode(ZipCode zipCode);`
  - **Pagination, Slicing, Sorting**
    - **Paging**
      - This is the most convenient way to implement pagination in a web application. It only needs to get the page and the number of result per page for a query. Instead of using `Repository` or `CrudRepository`, you should use `PaginationAndSortingRepository`, which accepts an object of the "Pageable" type.
      - The `Pageable` object needs the number of a page and the number of the element per page.
      - The result is a "page" that has the element from specific rows of the whole result set. The response also includes the metadata of the total element of the specific query you sent, as well as total pages.
      - We can paginate the query results of our database queries by following these steps:
        - Obtain the `Pageable` object that specifies the information of the requested page.
          ```java
          /* MANUALLY */
          Pageable p = new PageRequest(0, 10); // first page
          Pageable p = new PageRequest(1, 10, Sort.Direction.ASC, "title", "description"); // second page using sorting
          Pageable p = new PageRequest(1, 10,
                      new Sort(Sort.Direction.DESC, "description")
                        .and(new Sort(Sort.Direction.ASC, "title"));
              );
          ```
        - Use this `Pagable` in our function:
          ```java
          @Repository
          public interface SomethingRepository extends PaginationAndSortingRepository<Something, Long> {
            @Query("Select s from  Something s "
                    + "join s.somethingelse as se "
                    + "where se.id = :somethingelseid ")
            Page<Something> findBySomethingElseId(@Param("somethingelseid") long somethingelseid,
                                                                                Pageable pageable);
          ```
        - As seen above you can use the JPA's `@Query` annotation to bind a query to a given function!
        - **Since Spring Data 2.0 the `Page` class doesn't contain methods for retrieving the Content. TODO: Research how we can extract data from paged results!**
    - **Sorting**
      ```java
      // Valid Sort expression pointing to property in domain model.
      repo.findByAndSort("lannister", new Sort("firstname"));
      // Valid Sort expression pointing to property in domain model.
      repo.findByAndSort("stark", new Sort("LENGTH(firstname)"));
      // Valid Sort containing explicitly unsafe Order.
      repo.findByAndSort("targaryen", JpaSort.unsafe("LENGTH(firstname)"));
      // Valid Sort containing explicitly unsafe Order.
      repo.findByAsArrayAndSort("bolton", new Sort("fn_len"));
      ```
  - **Limiting the results**
    - The results of query methods can be limited by using the `first` or `top` keywords, which can be used interchangeably. An optional numeric value can be appended to `top` or `first` to specify the maximum result size to be returned. If the number is left out, a result size of `1` is assumed.
      ```java
      User findFirstByOrderByLastnameAsc();
      User findTopByOrderByAgeDesc();
      Page<User> queryFirst10ByLastname(String lastname, Pageable pageable);
      Slice<User> findTop3ByLastname(String lastname, Pageable pageable);
      List<User> findFirst10ByLastname(String lastname, Sort sort);
      List<User> findTop10ByLastname(String lastname, Pageable pageable);
      ```
  - **Streaming results**
    ```java
    @Query("select u from User u")
    Stream<User> findAllByCustomQueryAndStream();

    Stream<User> readAllByFirstnameNotNull();

    @Query("select u from User u")
    Stream<User> streamAllPaged(Pageable pageable);

    /*Usage*/
    try (Stream<User> stream = repository.findAllByCustomQueryAndStream()) {
      stream.forEach(…);
    }
    ```
<<<<<<< HEAD
  - **Querydsl Extension**
    - Querydsl is a framework that enables the construction of statically typed SQL-like queries through its fluent API.
    - The Interface:
      ```java
      public interface QuerydslPredicateExecutor<T> {
        Optional<T> findById(Predicate predicate);
        Iterable<T> findAll(Predicate predicate);
        long count(Predicate predicate);
        boolean exists(Predicate predicate);
      }
      ```
    - Usage:
      ```java
      interface UserRepository extends CrudRepository<User, Long>, QuerydslPredicateExecutor<User> {
        Predicate predicate = user.firstname.equalsIgnoreCase("dave")
          .and(user.lastname.startsWithIgnoreCase("mathews"));
        userRepository.findAll(predicate);
      }
      ```
- **Web Support**
  - Enabling Spring Data web support
    ```java
    @Configuration
    @EnableWebMvc
    @EnableSpringDataWebSupport
    class WebConfiguration {}
    ```
  - he configuration shown in the previous section registers a few basic components:
    - A `DomainClassConverter` to let Spring MVC resolve instances of repository-managed domain classes from request parameters or path variables.
      ```java
      @Controller
      @RequestMapping("/users")
      class UserController {

        @RequestMapping("/{id}")
        String showUserForm(@PathVariable("id") User user, Model model) {

          model.addAttribute("user", user);
          return "userForm";
        }
      }
      ```
      - As you can see, the method receives a `User` instance directly, and no further lookup is necessary. The instance can be resolved by letting Spring MVC convert the path variable into the `id` type of the domain class first and eventually access the instance through calling `findById(…)` on the repository instance registered for the domain type.
    - `HandlerMethodArgumentResolver` implementations to let Spring MVC resolve `Pageable` and `Sort` instances from request parameters.
      ```java
      @Controller
      @RequestMapping("/users")
      class UserController {
        private final UserRepository repository;

        UserController(UserRepository repository) {
          this.repository = repository;
        }

        @RequestMapping
        String showUsers(Model model, Pageable pageable) {
          model.addAttribute("users", repository.findAll(pageable));
          return "users";
        }
      }
      ```
      - The preceding method signature causes Spring MVC try to derive a Pageable instance from the request parameters by using the following default configuration:
        - `page` Page you want to retrieve. 0-indexed and defaults to 0.
        - `size` Size of the page you want to retrieve. Defaults to 20.
        - `sort` Properties that should be sorted by in the format property,property(,ASC|DESC). Default sort direction is ascending.
  - **Hypermedia Support for Pageables**
    - Spring HATEOAS ships with a representation model class (`PagedResources`) that allows enriching the content of a `Page` instance with the necessary `Page` metadata as well as links to let the clients easily navigate the pages.
    - The conversion of a `Page` to a `PagedResources` is done by an implementation of the Spring HATEOAS `ResourceAssembler` interface, called the `PagedResourcesAssembler`.
      ```java
      @Controller
      class PersonController {
        @Autowired PersonRepository repository;
        @RequestMapping(value = "/persons", method = RequestMethod.GET)
        HttpEntity<PagedResources<Person>> persons(Pageable pageable,
          PagedResourcesAssembler assembler) {
            Page<Person> persons = repository.findAll(pageable);
            return new ResponseEntity<>(assembler.toResources(persons), HttpStatus.OK);
        }
      }
      ```
      - The content of the `Page` becomes the content of the `PagedResources` instance.
      - The `PagedResources` object gets a PageMetadata instance attached, and it is populated with information from the Page and the underlying `PageRequest`.
      - The `PagedResources` may get prev and next links attached, depending on the page’s state. The links point to the URI to which the method maps. The pagination parameters added to the method match the setup of the `PageableHandlerMethodArgumentResolver` to make sure the links can be resolved later.
      ```json
      { "links" : [ { "rel" : "next",
                      "href" : "http://localhost:8080/persons?page=1&size=20 }
        ],
        "content" : [
          … // 20 Person instances rendered here
        ],
        "pageMetadata" : {
          "size" : 20,
          "totalElements" : 30,
          "totalPages" : 2,
          "number" : 0
        }
      }
      ```

## JPA Repository

- **Setup**
  - **Note that on Spring Boot it's all done automatically, you don't need the code below!**
    ```java
    @Configuration
    @EnableJpaRepositories
    @EnableTransactionManagement
    class ApplicationConfig {

      @Bean
      public DataSource dataSource() {

        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        return builder.setType(EmbeddedDatabaseType.HSQL).build();
      }

      @Bean
      public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.acme.domain");
        factory.setDataSource(dataSource());
        return factory;
      }

      @Bean
      public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {

        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory);
        return txManager;
      }
    }
    ```
  - The preceding configuration class sets up an embedded HSQL database by using the EmbeddedDatabaseBuilder API of spring-jdbc. Spring Data then sets up an EntityManagerFactory and uses Hibernate as the sample persistence provider. The last infrastructure component declared here is the JpaTransactionManager. Finally, the example activates Spring Data JPA repositories by using the @EnableJpaRepositories annotation, which essentially carries the same attributes as the XML namespace. If no base package is configured, it uses the one in which the configuration class resides.
- **Persisting Entities**
  - Saving an entity can be performed with the `CrudRepository.save(…)` method. It persists or merges the given entity by using the underlying JPA `EntityManager`. If the entity has not yet been persisted, Spring Data JPA saves the entity with a call to the `entityManager.persist(…)` method. Otherwise, it calls the `entityManager.merge(…)` method.
- **Query Lookup Strategies**
  - See [Table](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation)
- **JPA Named Queries**
    ```java
    public interface UserRepository extends JpaRepository<User, Long> {
      @Query("select u from User u where u.firstname like %?1")
      List<User> findByFirstnameEndsWith(String firstname);
    }
    ```
- **JPA Native Named Queries**
    ```java
    public interface UserRepository extends JpaRepository<User, Long> {
      @Query(value = "SELECT * FROM USERS WHERE LASTNAME = ?1",
        countQuery = "SELECT count(*) FROM USERS WHERE LASTNAME = ?1",
        nativeQuery = true)
      Page<User> findByLastname(String lastname, Pageable pageable);
    }
    ```
- **Using named parameters**
    ```java
    public interface UserRepository extends JpaRepository<User, Long> {

      @Query("select u from User u where u.firstname = :firstname or u.lastname = :lastname")
      User findByLastnameOrFirstname(@Param("lastname") String lastname,
                                    @Param("firstname") String firstname);
    }
    ```
// TODO ...
=======
- **Error handling and Nullability**
  - **Annotations**
    - `@NonNullApi` - Used on the *package level* to declare that the default behavior for parameters and return values is to not accept or produce `null` values.
    - `@NonNull` - Used on a parameter or return value that must not be `null` (not needed on a parameter and return value where `@NonNullApi` applies).
    - `@Nullable` -  Used on a parameter or return value that can be `null`.
  - **Examples**
    - `User getByEmailAddress(EmailAddress emailAddress);`
      - Throws an `EmptyResultDataAccessException` when the query executed does not produce a result. Throws an `IllegalArgumentException` when the `emailAddress` handed to the method is `null`.
    - `@Nullable User findByEmailAddress(@Nullable EmailAddress emailAdress);`
      - Returns `null` when the query executed does not produce a result. Also accepts `null` as the value for `emailAddress`.
    - `Optional<User> findOptionalByEmailAddress(EmailAddress emailAddress);`
      - Returns `Optional.empty()` when the query executed does not produce a result. Throws an `IllegalArgumentException` when the `emailAddress` handed to the method is `null`.
>>>>>>> d6ec70ba444ba2c19492763c764a513b212d6907
