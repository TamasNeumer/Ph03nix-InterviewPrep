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
- **Multiple data sources**
  - Sometimes you have multiple data sources in a project and you need to tell spring where to find the data when using a given query. Here comes the `@EnableJpaRepositories` in play. For more info: [Link](https://stackoverflow.com/questions/45663025/spring-data-jpa-multiple-enablejparepositories)
- **Query methods**
  - **Query Creation**
    - The query builder mechanism built into Spring Data repository infrastructure is useful for building constraining queries over entities of the repository. The mechanism strips the prefixes `find…By`, `read…By`, `query…By`, `count…By`, and `get…By` from the method and starts parsing the rest of it. The introducing clause can contain further expressions, such as a `Distinct` to set a distinct flag on the query to be created. However, the first `By` acts as delimiter to indicate the start of the actual criteria. At a very basic level, you can define conditions on entity properties and concatenate them with And and Or. The following example shows how to create a number of queries:
    - In addition to query methods, query derivation for both count and delete queries is available. In such cases Spring automatically generates the function implementations for you (just based on the function name.)
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
      - **Static Pagination**
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
          - **Since Spring Data 2.0 the `Page` class doesn't contain methods for retrieving the Content. TODO: Research how we can extract data from paged results!**
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