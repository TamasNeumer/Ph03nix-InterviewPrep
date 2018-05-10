# Hibernate

## Basics

### Hibernate in a nutshell

- **Object-relational mapping** or ORM is the programming technique to map application domain model objects to the relational database tables. Hibernate is java based ORM tool that provides framework for mapping application domain objects to the relational database tables and vice versa.
- Hibernate supports mapping of java classes to database tables and vice versa. It provides features to perform CRUD operations across all the major relational databases.
- Hibernate supports transaction management and makes sure there is no inconsistent data present in the system.
- Hibernate provides a powerful query language (HQL) that is similar to SQL. However, HQL is fully object-oriented and understands concepts like inheritance, polymorphism and association.

### Architecture

- **Database and Database Engine**
  - A database is an organized collection of data. A database engine (or storage engine) is the underlying software component that a database management system (DBMS) uses to create, read, update and delete (CRUD) data from a database.
  - In case of MySQL the default database engine is InnoDB.
- **JNDI, JDBC, JTA**
  - From Java the database can be accessed via Java Database Connectivity (JDBC) API, Java Transaction API (JTA) API, and Java Naming and Directory Interface (JNDI) API. Using these low level APIs have many disadvantages.
- **SessionFactory**
  - Usually an application has a **single** `SessionFactory` instance and threads servicing client requests obtain `Session` instances from this factory.
  - The internal state of a `SessionFactory` is **immutable**. Once it is created this internal state is set. This internal state includes all of the metadata about Object/Relational Mapping.
  - Implementors must be **threadsafe**.
  - It is a good practice to create it when the application starts.
  - A great approach is to wrap the factory into a singleton:
    ```java
    public class SessionUtil {
        private static final SessionUtil instance = new SessionUtil();
        private final SessionFactory factory;
        Logger logger = Logger.getLogger(this.getClass());

        private SessionUtil() {
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .configure()
                    .build();
            factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        }

        public static Session getSession() {
            return getInstance().factory.openSession();
        }

        private static SessionUtil getInstance() {
            return instance;
        }

        public static void doWithSession(Consumer<Session> command) {
            try(Session session = getSession()) {
                Transaction tx = session.beginTransaction();

                command.accept(session);
                if (tx.isActive() &&
                        !tx.getRollbackOnly()) {
                    tx.commit();
                } else {
                    tx.rollback();
                }
            }
        }
    }
    ```
  - The implementation above looks for the standard `hibernate.cfg.xml` file and reads the configuration properties from there.
  - **Methods**
    - `Session openSession()` - DBC connection(s) will be obtained from the configured `ConnectionProvider` as needed to perform requested work.
    - `Session openSession(Connection connection)` - Open a Session, utilizing the specfied JDBC Connection
    - `Session getCurrentSession()` - Obtains the current session. The definition of what exactly "current" means controlled by the `CurrentSessionContext` impl configured for use.
    - `StatelessSession openStatelessSession()` - Open a new stateless session. Does not implement first-level cache nor second-level cache. Collections are ignored by a stateless session. Probably useful if we want to load bulk data into the database and we don't want to fill up the first-level cache memory.
  - Note that the `SessionFactory` requires a connection provider. Hibernate has its own connection pooling algorithm is, however, quite rudimentary. (More on customizing the connection provider comes later.)
- **ConnectionProvider**
  - `ConnectionProvider` is a factory for JDBC connections. It provides abstraction between the application and underlying `javax.sql.DataSource` or `java.sql.DriverManager`. It is not exposed to application, but it can be extended by the developer.
- **Session**
  - `Session` is a **single-threaded**, short-lived object representing a **conversation between the application and the persistent store**. It wraps JDBC `Connection` and works as a factory for `Transaction`. Session holds a mandatory **first-level cache** of persistent objects that are used when navigating the object graph or looking up objects by identifier.
  - It is **NOT** threadsafe!
  - `Session`s should be closed after usage! (`session = sessionFactory.openSession();` should be closed manually after use because it is not managed by an orchestrator that release resource after use.)
  - When you don't close your Hibernate sessions and therefore do not release JDBC connections, you have what is typically called *Connection leak* - i.e. you are "eating up" the connections provided by the pool, without returning them. --> A **try-with-resources** clause is advised while opening connections!
- **Transaction**:
  - Transaction is a single-threaded, short-lived object used by the application to specify atomic units of work. It abstracts the application from the underlying JDBC or JTA transaction. A `Session` might span multiple `Transaction` in some cases.
  ![Architechture](/Java/Hibernate/res/architecture.PNG)
- **Persistance context**
  - Any entity instance in your application appears in one of the three main states in relation to the `Session` persistence context:
    - **Transient**
      - This instance is not, and never was, attached to a `Session`; this instance has no corresponding rows in the database; it’s usually just a new object that you have created to save to the database
    - **Persistent**
      - this instance is associated with a unique `Session` object; upon flushing the `Session` to the database, this entity is guaranteed to have a corresponding consistent record in the database
      - Persistent objects are "tracked" by hibernate. For example if you change an attribute (e.g.: `String name`) of an object that is already persistent, upon committing the transaction, the changes will be synched automatically with the database.
    - **Detached**
      - this instance was once attached to a `Session` (in a persistent state), but now it’s not; an instance enters this state if you `evict()` it from the context, clear or close the `Session`, or put the instance through serialization/deserialization process. Detached objects have a representation in the database, but changes to the object will not be reflected in the database, and vice versa. One reason you might consider doing this would be to read an object out of the database, modify the properties of the object in memory, and then store the results someplace other than your database. (Alternative would be a deep copy.)
    - **Removed** - A removed object is one that’s been marked for deletion in the current transaction. An object is changed to removed state when `Session.delete()` is called for that object reference. (=> Hibernate will remove the object from the DB on the next flush.)
      ![Flow](/Java/Hibernate/res/FLOW.PNG)
  - When the entity instance is in the persistent state, all changes that you make to the mapped fields of this instance will be applied to the corresponding database records and fields upon flushing the `Session`. The persistent instance can be thought of as “online”, whereas the detached instance has gone “offline” and is not monitored for changes. --> This means that when you change fields of a persistent object, you don’t have to call `save`, `update` or any of those methods to get these changes to the database: all you need is to commit the transaction, or flush or close the session, when you’re done with it.

### Hibernate Configuration

- **Connection Pooling**
  - **Configuration**
    - As stated above Hibernate is shipped with a primitive connection pool, but it is not intended to be used in real production.
    - Currently [HikariCP](https://github.com/brettwooldridge/HikariCP) provides the best performance.
    - Maven dependencies:
      ```xml
        <dependency>
            <groupId>com.zaxxer</groupId>
            <artifactId>HikariCP</artifactId>
            <version>3.1.0</version>
        </dependency>
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-hikaricp</artifactId>
            <version>5.2.10.Final</version>
        </dependency>
      ```
    - `hibernate.cfg.xml` configuration:
      ```xml
      <?xml version="1.0"?>
      <!DOCTYPE hibernate-configuration PUBLIC
              "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
              "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
      <hibernate-configuration>
          <session-factory>
              <!--  Database connection settings  -->
              <property name="connection.driver_class">com.mysql.jdbc.Driver</property>
              <property name="connection.url">jdbc:mysql://localhost:3306/hibernate?useSSL=false</property>
              <property name="dialect">org.hibernate.dialect.MySQL57Dialect</property>
              <property name="show_sql">true</property>
              <property name="hibernate.generate_statistics">true</property>
              <!--  Drop and re-create the database schema on startup  -->
              <!--<property name="hbm2ddl.auto">create-drop</property> -->
              <property name="hibernate.connection.provider_class">org.hibernate.hikaricp.internal.HikariCPConnectionProvider</property>
              <property name="hibernate.hikari.dataSource.url">jdbc:mysql://localhost:3306/hibernate</property>
              <property name="hibernate.hikari.dataSource.user">root</property>
              <property name="hibernate.hikari.dataSource.password">master</property>
              <property name="hibernate.hikari.maximumPoolSize">10</property>

              <mapping class="Entity.Customer"/>
              <!-- Other mapped entities -->
          </session-factory>
      </hibernate-configuration>
      ```
  - **Pool Size**
    - Pool Size does not necessarily improve performance! [Watch Oracle Video](http://www.dailymotion.com/video/x2s8uec)
    - If we didn't have I/O or Networking overhead to achieve the best performance we simply should have set `#Connection = #Cores` (= number of threads)
    - But since we have I/O and Networking overhead and while using the disk the thread is "blocked" we can add extra threads, so that the CPU can do something meaningful in this time.
    - Don't be tricked into thinking, "SSDs are faster and therefore I can have more threads". That is exactly 180 degrees backwards. Faster, no seeks, no rotational delays means less blocking and therefore fewer threads [closer to core count] will perform better than more threads.
    - Network is similar to disc.
    - **The formula:**
      - The formula below is provided by the PostgreSQL project as a starting point, but we believe it will be largely applicable across databases. You should test your application, i.e. simulate expected load, and try different pool settings around this starting point:
      - `connections = ((core_count * 2) + effective_spindle_count)`
        - Core count **should not** include hyperThreading threads, even if hyperthreading is enabled.
        - Effective spindle count is zero if the active data set is fully cached, and approaches the actual number of spindles as the cache hit rate falls. There hasn't been any analysis so far regarding how well the formula works with SSDs...
      - Guess what that means? Your little 4-Core i7 server with one hard disk should be running a connection pool of: 9 = ((4 * 2) + 1). Call it 10 as a nice round number.
  - **Maven configuration for hibernate**
    ```xml
    <dependency>
        <groupId>org.hibernate</groupId>
        <artifactId>hibernate-core</artifactId>
        <version>5.2.17.Final</version>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>5.1.39</version>
    </dependency>
    ```
  - **Enabling Logging**
    - Simply add the `slf4j` dependency to the maven configuration.
    ```xml
    <dependency>
          <groupId>org.slf4j</groupId>
          <artifactId>slf4j-log4j12</artifactId>
          <version>1.6.1</version>
    </dependency>
    ```
    - Add the following configuration (`log4j.properties`) to your resources folder:
        ```yml
        # Direct log messages to a log file
        log4j.appender.file=org.apache.log4j.RollingFileAppender
        log4j.appender.file.File=C:\\mkyongapp.log
        log4j.appender.file.MaxFileSize=1MB
        log4j.appender.file.MaxBackupIndex=1
        log4j.appender.file.layout=org.apache.log4j.PatternLayout
        log4j.appender.file.layout.ConversionPattern=%d{ABSOLUTE} %5p %c{1}:%L - %m%n

        # Direct log messages to stdout
        log4j.appender.stdout=org.apache.log4j.ConsoleAppender
        log4j.appender.stdout.Target=System.out
        log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
        log4j.appender.stdout.layout.ConversionPattern=%d{ABSOLUTE} %5p %c{1}:%L - %m%n

        # Root logger option
        log4j.rootLogger=INFO, file, stdout

        # Log everything. Good for troubleshooting
        log4j.logger.org.hibernate=INFO

        # Log all JDBC parameters
        log4j.logger.org.hibernate.type=ALL
        ```
    - Optionally you can add `<property name="hibernate.generate_statistics">true</property>` to the configuration file to enable live statistics, that will be logged. Once done so you can add statistics to your transactions:
      ```java
      /*...*/
      SessionStatistics sessionStats = session.getStatistics();
      Statistics stats = sessionFactory.getStatistics();
      /*...*/
      logger.info("getEntityCount- "+sessionStats.getEntityCount());
      logger.info("openCount- "+stats.getSessionOpenCount());
      logger.info("getEntityInsertCount- "+stats.getEntityInsertCount());
      /*...*/
      ```

## Reducing boilerplate using Project Lombok

### Intro & Install

- **What is Lombok?**
  - Project Lombok enables the user to add annotations to the code and thus reduce the usual boilerplate.
- **Enabling Project Lombok**
  - Add the dependency to the maven configuration:
    ```xml
    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
    </dependency>
    ```
  - Eclipse can use Lombok out of the box, while IntelliJ needs some configuration.
- **IntelliJ Configuration**
  - File - Settings/Preferences - Build. Execution, Deployment - Compiler - Enable annotation processing
  - File - Settings/Preferences - Plugins - Browse repositories - Search for "Lombok" - Install - Restart

### Useful Lombok Annotations

- `@NonNull`
  - You can use `@NonNull` on the parameter of a method or constructor to have lombok generate a null-check statement for you.
    ```java
    /*Instead of*/
    if (person == null) {
        throw new NullPointerException("person");
    }
    /*Simply use*/
    public NonNullExample(@NonNull Person person) { /*...*/ }
    ```
- `@Getter, @Setter`
  - You can annotate any field with `@Getter` and/or `@Setter`, to let lombok generate the default getter/setter automatically.
  - A default getter simply returns the field, and is named `getFoo` if the field is called `foo` (or `isFoo` if the field's type is `boolean`). A default setter is named `setFoo` if the field is called foo, returns `void`, and takes 1 parameter of the same type as the field. It simply sets the field to this value.
  - Access level is `public` by default, however you can set it via `@Setter(AccessLevel.PROTECTED)` (The access levels are `PUBLIC`, `PROTECTED`, `PACKAGE`, and `PRIVATE`)
  - Using the `AccessLevel.NONE` access level simply generates nothing. It's useful only in combination with `@Data` or a class-wide `@Getter` or `@Setter`.
- `@ToString`
  - Generates a `toString()` method. By default, it'll print your class name, along with each field, in order, separated by commas.
  - If you want to skip some fields, you can name them in the `exclude` parameter. - `@ToString(exclude="id")`. As we will see later it is useful in order to avoid circular dependency in the generated methods, while using relation mapping.
  - Alternatively, you can specify exactly which fields you wish to be used by naming them in the `of` parameter.
  - By setting `callSuper` to true, you can include the output of the superclass implementation of toString to the output. - `@ToString(callSuper=true)`
- `@EqualsAndHashCode`
  - Generates implementations of the `equals(Object other)` and `hashCode()` methods. Example: `@EqualsAndHashCode(exclude={"id", "shape"})`, `@EqualsAndHashCode(callSuper=true)`
  - Usage of `exclude` and `of` are similar to the `@ToString`
  - Not setting `callSuper` to true when you extend another class generates a warning, because unless the superclass has no (equality-important) fields, lombok cannot generate an implementation for you that takes into account the fields declared by your superclasses.
  - All fields marked as `transient` will not be considered for `hashCode` and `equals`.
- `@NoArgsConstructor, @RequiredArgsConstructor, @AllArgsConstructor`
  - `@NoArgsConstructor` will generate a constructor with no parameters. If this is not possible (because of final fields), a compiler error will result instead, unless `@NoArgsConstructor(force = true)` is used, then all final fields are initialized with `0` / `false` / `null`.
  - `@RequiredArgsConstructor` generates a constructor with 1 parameter for each field that requires special handling. All non-initialized `final` fields get a parameter, as well as any fields that are marked as `@NonNull` that aren't initialized where they are declared. For those fields marked with `@NonNull`, an explicit null check is also generated.
  - `@AllArgsConstructor` generates a constructor with 1 parameter for each field in your class. Fields marked with `@NonNull` result in null checks on those parameters.
  - **Static fields are skipped by these annotations!**
    ```java
    @RequiredArgsConstructor(staticName = "of")
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public class ConstructorExample<T> {
      private int x, y;
      @NonNull private T description;

      @NoArgsConstructor
      public static class NoArgsExample {
        @NonNull private String field;
      }
    }

    /*Generated code:*/
    public class ConstructorExample<T> {
      private int x, y;
      @NonNull private T description;

      private ConstructorExample(T description) {
        if (description == null) throw new NullPointerException("description");
        this.description = description;
      }

      public static <T> ConstructorExample<T> of(T description) {
        return new ConstructorExample<T>(description);
      }

      @java.beans.ConstructorProperties({"x", "y", "description"})
      protected ConstructorExample(int x, int y, T description) {
        if (description == null) throw new NullPointerException("description");
        this.x = x;
        this.y = y;
        this.description = description;
      }

      public static class NoArgsExample {
        @NonNull private String field;

        public NoArgsExample() {
        }
      }
    }
    ```
  - `@Data`
    - `@Data` is a convenient shortcut annotation that bundles the features of `@ToString`, `@EqualsAndHashCode`, `@Getter` / `@Setter` and `@RequiredArgsConstructor` together.
    - The parameters of these annotations (such as `callSuper`, `includeFieldNames` and `exclude`) cannot be set with `@Data`. If you need to set non-default values for any of these parameters, just add those annotations explicitly; `@Data` is smart enough to defer to those annotations.
    - No constructor will be generated if any explicitly written constructors already exist!
    - All `static` fields will be skipped entirely (not considered for any of the generated methods, and no setter/getter will be made for them).
    - Example: `@Data public class DataExample {/*...*/}`
  - `@Value`
    - `@Value` is the immutable variant of `@Data`; all fields are made private and final by default, and setters are not generated.
  - `@Builder`
    - The `@Builder` annotation produces complex builder APIs for your classes.
    - `@Builder` lets you automatically produce the code required to have your class be instantiable with code such as: `Person.builder().name("Adam Savage").city("San Francisco").job("Mythbusters").job("Unchained Reaction").build();`
    - For more info: [Link](https://projectlombok.org/features/Builder)
  - `@Log`
    - You put the variant of `@Log` on your class (whichever one applies to the logging system you use); you then have a static final log field, initialized to the name of your class, which you can then use to write log statements.
    - `@Log4j` Creates `private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LogExample.class);`
    - `@Log4j2` Creates `private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(LogExample.class);`
    - `@Slf4j` Creates `private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LogExample.class);`
      ```java
      @Slf4j
      public class LogExampleOther {
        public static void main(String... args) {
          log.error("Something else is wrong here");
        }
      }
      ```

## Table and Type Annotations

### Basic Annotations

- `@Entity`
  - Specifies that the class is an entity. (Class is to be handled by ORM)
  - Optional argument: `(name = "alternativeName")` Defaults to the unqualified name of the entity class. This name is used to refer to the entity in queries.
  - Basically you need to mark all your classes with `@Entity` to take them into ORM.
  - An entity class **must have a public or protected no-arg constructor**, and it can have other constructors as well!
  - Every non-static and non-transient property of an entity bean is considered persistent unless you specify `@Transient`.
- `@Table`
  - Specifies the primary table for the annotated entity.
  - `(name = alternativeTableName)` (Optional) The name of the table.
  - `(schema = optionalSchemaName)` (Optional) The schema of the table.
  - If you already have a table in your DB where you want to map the entity, use this annotation.
- `@Column`
  - Is used to specify a mapped column for a persistent property or field.
  - `String name` (Optional) The name of the column.
  - `boolean nullable` (Optional) Whether the database column is nullable.
  - `boolean unique` (Optional) Whether the property is a unique key.
  - `int length` (Optional) The column length.
  - Any many [other](https://docs.oracle.com/javaee/7/api/javax/persistence/Column.html)

### ID Generation Strategies

- **Primary key**
  - The JPA specification requires that every entity must have a primary key. From the JPA perspective, an `@Id` attribute is used to define how an identifier is generated.
  - `@Id`
    - Specifies the primary key of an entity. The field or property to which the Id annotation is applied should be one of the following types: any Java primitive type; any primitive wrapper type; String; java.util.Date; java.sql.Date; java.math.BigDecimal; java.math.BigInteger.
- **Identity**
  - Supported by MySQL (`AUTO_INCREMENT`) - and for MySQL **it is the only reasonable generation type**
  - The only drawback is that the newly assigned value can only be known after executing the actual insert statement. Because Hibernate separates the id generation from the actual entity insert statement, entities using the identity generator may not participate in JDBC batch updates.
  - It is not advisable to use it when the application is deployed in a cluster of servers because each server generates an ID and it might conflict with the generation on the other server.
  - `@GeneratedValue(strategy = GenerationType.IDENTITY)`
  - To truly see the difference between Identity and Sequence we have to enable batching:
    ```xml
    <property name="hibernate.jdbc.batch_size">30</property>
    <property name="hibernate.order_inserts">true</property>
    <property name="hibernate.order_updates">true</property>
    <property name="hibernate.jdbc.batch_versioned_data">true</property>
    ```
  - Once done so the following insertion generates the SQL output:
    ```java
    for (int i = 0; i < batchSize; i++) {
      entityManager.persist(new Post());
    }
    /*Commit*/

    /*OUTOUT*/
    INSERT INTO post (id) VALUES (DEFAULT)
    INSERT INTO post (id) VALUES (DEFAULT)
    ```
- **Sequence**
  - `@GeneratedValue(strategy = GenerationType.SEQUENCE)`
  - **NOT** supported by MySQL!
  - A sequence is a database object that generates consecutive numbers.
  - Advantages over identity:
    - the same sequence can be used to populate multiple columns, even across tables
    - values may be preallocated to improve performance
    - allowing incremental steps, sequences can benefit from application-level optimization techniques
    - A SEQUENCE doesn’t restrict Hibernate JDBC batching nor inheritance models
  - When executing the code above using SEQUENCE generation the result if the following:
    ```java
    CALL NEXT VALUE FOR hibernate_sequence
    CALL NEXT VALUE FOR hibernate_sequence

    DEBUG - Flush is triggered at commit-time

    INSERT INTO post (id) VALUES (1, 2)
    ```
    - Here the driver could insert the data in a single batch, while in the previous version two inserts were required.
- **Table**
  - **NEVER USE TABLE because of performance reasons!**
  - Because of the mismatch between the identifier generator and the transactional write-behind cache, JPA offers an alternative sequence-like generator that works even when sequences are not natively supported. A database table is used to hold the latest sequence value and row-level locking is employed to prevent two concurrent connections from acquiring the same identifier value.
  - `@GeneratedValue(strategy=GenerationType.TABLE)`
  - Meanwhile the table generator benefits from JDBC batching, every table sequence update incurs three steps:
    - the lock statement is executed to ensure that the same sequence value is not allocated for two concurrent transactions
    - the current value is incremented in the data access layer
    - the new value is saved back to the database and the secondary transaction is committed to release the row-level lock.
  - This makes the `TABLE` approach not quite efficient, as it contains significant performance overhead.
- **AUTO**
  - Decides the identifier generation strategy based on the current database dialect.
  - In Hibernate 5 **DON'T USE AUTO FOR MYSQL!** Hibernate picks the `TABLE` generator instead of `IDENTITY` when the underlying database does not support sequences.
- **UUID**
  - Fixed-size non-numerical keys (e.g. `CHAR`, `VARCHAR`) are less efficient than numerical ones (e.g. `INTEGER`, `BIGINT`) both for joining (a simple key performs better than a compound one) or indexing (the more compact the data type, the less memory space is required by an associated index).
  - Surrogate keys are generated independently of the current row data, so table column constraints may evolve with time (changing a user birthday or email address). The surrogate key can be generated by a numerical sequence generator (e.g. a database identity column or a sequence), or it can be constructed by a pseudorandom number generator (e.g GUID or UUID).
  - A `UUID` takes 128 bits, which is four times more than an `INTEGER` and twice as as `BIGINT`
  - Requiring less space and being more index-friendly, **numerical sequences are preferred over UUID keys**.
  - **MYQL** The UUID must be stored in a `BINARY(16)` column type.
    ```java
    @Entity @Table(name = "post")
    public class Post {
      @Id
      @Column(columnDefinition = "BINARY(16)")
      @GeneratedValue(generator = "uuid2")
      @GenericGenerator(name = "uuid2", strategy = "uuid2")
      private UUID id;
    }
    ```

### Putting things together

- In the example below we have a simple POJO Java class that is annotated and now ready to be persisted to the database:
    ```java
    @Entity
    @Table(name = "EMPLOYEE")
    @Data
    public class Employee {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "emp_id")
        private long id;

        @Column(name = "emp_name")
        private String name;

        @Column(name = "emp_salary")
        private double salary;
    }
    ```

## Mapping Components

### @Embedded, @Embeddable

- **Intro**
  - Imagine that you want to create classes that are "Composites" meaning that they contain references to other classes, however for efficiency reasons you want to store the composite class in a single table. `@Embedded` and `@Embeddable` allow exactly this feature.
- `@Embeddable`
  - The `@Embeddable` annotation over a class defines that, it does not have independent existence. (E.g. the class "`UserDetails`" might be marked as `@Embeddable`, while the class "`User`" holds a reference to it.)
  - An `@Embeddable` entity must be composed entirely of basic fields and attributes. An `@Embeddable` entity can only use the `@Basic`, `@Column`, `@Lob`, `@Temporal`, and `@Enumerated` annotations.
  - It cannot maintain its own primary key with the `@Id` tag because its primary key is the primary key of the enclosing entity.
- `@Embedded`
  - The `@Embedded` annotation is used to express that the class field is an `@Embeddable` type.
- **Example**
    ```java
    @Embeddable
    @Data
    public class EmbeddedContact {
        @Column
        String name;
        @Column
        String address;
        @Column
        String phone;
    }

    @Entity
    @Data
    public class OrderWithEmbeddedContact {
        @Id
        @GeneratedValue
        Long id;
        @Embedded
        EmbeddedContact weekdayContact;
        @Embedded
        @AttributeOverrides({
                @AttributeOverride(name = "name", column = @Column(name = "holidayname")),
                @AttributeOverride(name = "address", column = @Column(name = "holidayaddress")),
                @AttributeOverride(name = "phone", column = @Column(name = "holidayphone")),
        })
        EmbeddedContact holidayContact;
    }
    ```
  - In the example above you have two `@Embedded EmbeddedContact` fields meaning that Hibernate would try to map each of the object's attributes to the same table columns.
    ![DirtyRead](/Java/Hibernate/res/EmbeddedContact.PNG)
- **Reference to the Parent within the @Embeddable class**
  - In Hibernate Annotation, while using embeddable object we can assign parent class reference in embeddable class. `@Parent` annotation is used to assign the parent reference. We can call this reference through the parent class.
  - In the above example the `EmbeddedContact` class will have a reference to its parent class. `@Parent OrderWithEmbeddedContact parentRef;`

### @ElementCollection

- **Intro**
  - JPA 2.0 defines an `ElementCollection` mapping. It is meant to handle several non-standard relationship mappings. An `ElementCollection` can be used to define a one-to-many relationship to an `Embeddable` object, or a `Basic` value (such as a collection of Strings). An `ElementCollection` can also be used in combination with a `Map` to define relationships where the key can be any type of object, and the value is an `Embeddable` object or a `Basic` value.
  - This is **not** a typical usage of `Embeddable` objects as the objects are not embedded in the source object's table, but stored in a separate collection table. This is similar to a `OneToMany`, except the target object is an `Embeddable` instead of an Entity.
  - **Advantage:** This allows collections of simple objects to be easily defined, without requiring the simple objects to define an `Id` or `ManyToOne` inverse mapping.
  - The limitations of using an `ElementCollection` instead of a `OneToMany` is that the target objects cannot be queried, persisted, merged independently of their parent object. There is no cascade option on an `ElementCollection`, the target objects are always persisted, merged, removed with their parent.
- **Implementation**
    ```java
    @ElementCollection(targetClass=Address.class,fetch=FetchType.EAGER)
            @JoinTable (name = "Address", joinColumns = @JoinColumn(name="Customer_ID"))
            private Set<Address> contacts;
    ```
  - The generated table for the `Address` class will have a "Customer_ID" column in which the parent's primary key is stored. Other than that each property will have its own column.

## Mapping Inheritance

### Single table

- **Intro**
  - The single table inheritance is the **default** JPA strategy, funneling a whole inheritance Domain Model hierarchy into a single database table.
  - Annotation: `@Inheritance` or `@Inheritance(strategy = InheritanceType.SINGLE_TABÖE)`
- **Implementation**
  - The parent class simply receives one of the two above mentioned annotations and defines the column in which the "Class Types" are stored with the `@DiscriminatorColumn` annotation.
    ```java
    @Data
    @NoArgsConstructor
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    @DiscriminatorColumn(name = "disc_type")
    @Entity
    public class Disc {
        @Id
        @GeneratedValue
        Long id;
        @Column(nullable = false)
        String name;
        @Column(nullable = false)
        int price;
    }
    ```
  - As you can see on the results all classes are saved in the same table.
   ![DirtyRead](/Java/Hibernate/res/SingleTable.PNG)
- **Performance**
  - Since only one table is used for storing entities, both read and write operations are fast.
  - Because all subclass properties are collocated in a single table, `NOT NULL` constraints are not allowed for columns belonging to subclasses. Being automatically inherited by all subclasses, the base class properties may be non-nullable. From a data integrity perspective, this limitation defeats the purpose of Consistency (guaranteed by the ACID properties).

### Join table

- **Intro**
  - The join table inheritance resembles the Domain Model class diagram since each class is mapped to an individual table. The subclass tables have a foreign key column referencing the base class table primary key.
- **Implementation**
  - Compared to the previous scenario simply change the strategy to `strategy = InheritanceType.JOINED`
  - As seen on the result below the DISC table now only contains the `DISC` specific information. All the other tables contain the "extra" class-specific information, with an `id` as the foreign key.
  ![DirtyRead](/Java/Hibernate/res/JoinTableMerge.PNG)
- **Performance**
  - Unlike single table inheritance, the joined table strategy allows nullable subclass property columns.
  - When writing data, Hibernate requires two insert statements for each subclass entity, so there’s a performance impact compared to single table inheritance. The index memory footprint also increases because instead of a single table primary key, the database must index the base class and all subclasses primary keys.
  - The table takes up less space because we don't have that many `NULL` values.

### Table-per-class

- **Intro**
  - This strategy creates tables for every type in the hierarchy, so we end up with a table for Disc, AudioDisc, VideoDisc.
  - Changes in the hierarchy have to be reflected across each table, and polymorphic queries have to span multiple tables as well.
- **Implementation**
  - `@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)`
  ![DirtyRead](/Java/Hibernate/res/TablePerClass.PNG)
- **Performance**
  - While write operations are faster than in the joined table strategy, the read operations are only efficient when querying against the actual subclass entities. Polymorphic queries can have a considerable performance impact because Hibernate must select all subclass tables and use `UNION ALL` to build the whole inheritance tree result set. As a rule of thumb, the more subclass tables, the least efficient the polymorphic queries will get.

### Mapped Superclass

- **Intro**
  - What happens if our superclass contains common attributes, but is abstract?
  - Everything in common among the various subclasses can be located in the superclass, which is annotated normally, except that it receives a `@MappedSuperclass` annotation instead of being marked with `@Entity`.
  - Being an abstract class the entity is leaved out of the database. Note that the class is note annotated with `@Enttity` anymore, instead it got the `@MappedSuperclass` annotation!
- **Implementation**
    ```java
    @Data
    @NoArgsConstructor
    @MappedSuperclass
    public class Disc {
        @Id
        @GeneratedValue
        Long id;
        @Column(nullable = false)
        String name;
        @Column(nullable = false)
        int price;
    }
    ```
  ![DirtyRead](/Java/Hibernate/res/MappedSuperClass.PNG)
  - **Performance**
    - Although polymorphic queries and associations are no longer permitted, the `@MappedSuperclass` yields very efficient read and write operations. Like single and table-per-class inheritance, write operations require a single insert statement and reading only needs to select from one table only.

### Summary

- All the aforementioned inheritance mapping models require trading something in order to accommodate the impedance mismatch between the relational database system and the object-oriented Domain Model.
- The default single table inheritance performs the best in terms of reading and writing data, but it forces the application developer to overcome the column nullability limitation. This strategy is useful when the database can provide support for trigger procedures and the number of subclasses is relatively small.
- The join table is worth considering when the number of subclasses is higher and the data access layer doesn’t require polymorphic queries. When the number of subclass tables is large, polymorphic queries will require many joins, and fetching such a result set will have an impact on application performance. This issue can be mitigated by restricting the result set (e.g. pagination), but that only applies to queries and not to `@OneToMany` or `@ManyToMany` associations. On the other hand, polymorphic `@ManyToOne` and `@OneToOne` associations are fine since, in spite of joining multiple tables, the result set can have at most one record only.
- Table-per-class is the least effective when it comes to polymorphic queries or associations. If each subclass is stored in a separate database table, the `@MappedSuperclass` Domain Model inheritance is often a better alternative anyway.

## Mapping Relationships

### @ManyToOne

- **Intro**
  - When using a `@ManyToOne` association, the underlying foreign key is controlled by the child-side, no matter the association is unidirectional or bidirectional.
- **Code**
  - Executed SQL:
      ```sql
        CREATE TABLE `Cart` (
          `cart_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
          `total` decimal(10,0) NOT NULL,
          `name` varchar(10) DEFAULT NULL,
          PRIMARY KEY (`cart_id`)
        ) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

        CREATE TABLE `Items` (
          `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
          `cart_id` int(11) unsigned NOT NULL,
          `item_id` varchar(10) NOT NULL,
          `item_total` decimal(10,0) NOT NULL,
          `quantity` int(3) NOT NULL,
          PRIMARY KEY (`id`),
          KEY `cart_id` (`cart_id`),
          CONSTRAINT `items_ibfk_1` FOREIGN KEY (`cart_id`) REFERENCES `Cart` (`cart_id`)
        ) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
      ```
  - Java Code:
      ```java
      @Entity
      @Table(name="CART")
      @Data
      @EqualsAndHashCode(exclude="items")
      @ToString(exclude = "items")
      public class Cart {

          @Id
          @GeneratedValue(strategy=GenerationType.IDENTITY)
          @Column(name="cart_id")
          private long id;

          @Column(name="total")
          private double total;

          @Column(name="name")
          private String name;
      }

      @Entity
      Data
      @NoArgsConstructor
      @Table(name="Items")
      public class Items {

          @Id
          @GeneratedValue(strategy = GenerationType.IDENTITY)
          @Column(name = "id")
          private long id;

          @Column(name = "item_id")
          private String itemId;

          @Column(name = "item_total")
          private double itemTotal;

          @Column(name = "quantity")
          private int quantity;

          @ManyToOne
          @JoinColumn(name = "cart_id", nullable = false)
          private Cart cart;
      }
      ```
  - Notes:
    - In this scenario the `Cart` class doesn't necessarily have to posses a `Set<Items>`. If we remove this line it would still work!
    - Because the `@ManyToOne` association controls the foreign key directly, the automatically generated DML statements are very efficient.
    - When working with ORM and doing `OneToMany` or `ManyToMany` relationships you introduce **circular dependencies**. In our case the `Cart` class has a `Set<Items>` reference, while the `Item` class has a `Cart` reference. When you want to generate `toString` or `hashCode` using Project Lombok or IntelliJ's auto-generator the generated functions will contain circular dependencies as well!
      - For example the generated `hashCode` method contains the following line: `result = result * PRIME + ($items == null ? 43 : $items.hashCode());`. While evaluating this line the `items.hashCode()` triggers a circular call and results in a `StackOverFlowError`.
      - The generated `toString` method contains `items=" + this.getItems()` which also triggers a circular dependency.
      - **Solution**
        - Exclude the circular dependency.
        - In lombok:
          - `@EqualsAndHashCode(exclude="items")`
          - `@ToString(exclude = "items")`
    - Fetching mode:
      - Once fetched an Item do you want the Cart to be fatched as well? If not, then use the `@ManyToOne(fetch = FetchType.LAZY)` annotation.
      - The **default** fetch type is `EAGER`

### @OneToMany

#### Bidirectional @OneToMany

- **Code**
  - Take the same SQL table.
  - Add the following modification to the `Cart` class:
    ```java
    @OneToMany(mappedBy="cart")
    Set<Items> items;
    ```
- **Notes**
  - In a bidirectional association, only one side can control the underlying table relationship. For the bidirectional `@OneToMany` mapping, it’s the child-side `@ManyToOne` association in charge of keeping the foreign key column value in sync with the in-memory Persistence Context. This is the reason why the bidirectional `@OneToMany` relationship must define the mappedBy attribute, indicating that it only mirrors the `@ManyToOne` child-side mapping.
  - The bidirectional `@OneToMany` association generates efficient DML statements because the `@ManyToOne` mapping is in charge of the table relationship. Because it simplifies data access operations as well, the bidirectional `@OneToMany` association is worth considering when the size of the child records is relatively low.

#### Unidirectional @OneToMany

- **Intro**
  - The unidirectional `@OneToMany` association is very tempting because the mapping is simpler than its bidirectional counterpart. Because there is only one side to take into consideration, there’s no need for helper methods and the mapping doesn’t feature a mappedBy attribute either.
- **Code**
  - In this case you would remove the `Cart` reference from the `Item` class and probably change the association to `@OneToMany(cascade = CascadeType.ALL)`
- **Notes**
  - Unfortunately, in spite its simplicity, the unidirectional `@OneToMany` association is less efficient than the unidirectional `@ManyToOne` mapping or the bidirectional `@OneToMany` association. Because there is no `@ManyToOne` side to control this relationship, Hibernate uses a separate junction table to manage the association between a parent row and its child records. (If the tables are generated by hibernate)
  - The unidirectional `@OneToMany` relationship is less efficient both for reading data (three joins are required instead of two), as for adding (two tables must be written instead of one) or removing (entries are removed and added back again) child entries.

#### @OneToMany with @JoinColumn

- **Intro**
  - JPA 2.0 added support for mapping the @OneToMany association with a @JoinColumn so that it can map the one-to-many table relationship. With the @JoinColumn, the @OneToMany association controls the child table foreign key so there is no need for a junction table.
- **Code**
    ```java
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "item_id")
    private List<Item> items = new ArrayList<>();
    ```
- **Notes**
  - Although it’s an improvement over the regular `@OneToMany` mapping, in practice, it’s still not as efficient as a regular bidirectional `@OneToMany` association.

### @OneToOne

#### Bidirectional @OneToOne

- **Code**
  - Executed SQL to set up tables:
      ```sql
      DROP TABLE IF EXISTS `Customer`;
      DROP TABLE IF EXISTS `Transaction`;
      DROP TABLE IF EXISTS `Cart`;
      DROP TABLE IF EXISTS `Items`;
      -- Create Transaction Table
        CREATE TABLE `Transaction` (
          `txn_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
          `txn_date` date NOT NULL,
          `txn_total` decimal(10,0) NOT NULL,
          PRIMARY KEY (`txn_id`)
        ) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
        -- Create Customer table
        CREATE TABLE `Customer` (
          `txn_id` int(11) unsigned NOT NULL,
          `cust_name` varchar(20) NOT NULL DEFAULT '',
          `cust_email` varchar(20) DEFAULT NULL,
          `cust_address` varchar(50) NOT NULL DEFAULT '',
          PRIMARY KEY (`txn_id`),
          CONSTRAINT `customer_ibfk_1` FOREIGN KEY (`txn_id`) REFERENCES `Transaction` (`txn_id`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
      ```
  - Java Code:
      ```java
      @Entity
      @Data
      @Table(name="Customer")
      public class Customer {

          @Id
          @Column(name="txn_id", unique=true, nullable=false)
          @GeneratedValue(generator="gen")
          @GenericGenerator(name="gen", strategy="foreign", parameters={@Parameter(name="property", value="txn")})
          private long id;

          @Column(name = "cust_name")
          private String name;

          @Column(name = "cust_email")
          private String email;

          @Column(name = "cust_address")
          private String address;

          @OneToOne
          @PrimaryKeyJoinColumn
          private Txn txn;
      }

      @Entity
      @Data
      @Table(name = "Transaction")
      public class Txn {
          @Id
          @GeneratedValue(strategy = GenerationType.IDENTITY)
          @Column(name = "txn_id")
          private long id;

          @Column(name = "txn_date")
          private Date date;

          @Column(name = "txn_total")
          private double total;

          @OneToOne(mappedBy = "txn")
          @Cascade(value = CascadeType.SAVE_UPDATE)
          private Customer customer;

          //Bidirectional Relationship -> SETTER for Customer manually!!!
      }
      ```
- **Notes**:
  - The `Txn` class declares the relationship `@OneToOne(mappedBy = "txn")` meaning that the `Customer` class will be the owner of the relationship. Also the table used for `Customer` objects will be responsible to store the foreign key.
  - ``@Cascade(value = CascadeType.SAVE_UPDATE)`` - cascading will be used on save or update, but not on delete!
  - The One-to-One relationship requires some “extra” code because it has a special property of inheriting the foreign
    key from the parent table (`Txn`) and using it as the primary key of the child table (Customer). --> The ``Customer`` class holds the primary key id of `Txn` as its own primary key / id.
  - To prevent Hibernate creating (or looking for in our case) a `txn_txn_id` column, (which would be confusing and a waste of space) we need to
    help Hibernate by letting it know which column is our join column in our one-to-one relationship.
  - To ensure the id generation we used: ``@GenericGenerator(name="gen", strategy="foreign", parameters={@Parameter(name="property", value="txn")})``
    - use “foreign” strategy.
    - Lastly, we need to tell the `@GenericGenerator` where the actual relationship exists.
      In our case, our `@OneToOne` relationship exists via the `txn` object, so we point it to that object via the use of the `@Parameter` annotation.
  - If you use `strategy="AUTO"`, Hibernate will generate a table called `hibernate_sequence` to provide the next number for the ID sequence. If you are using a pre-defined mysql database this is not the desired behavior.
    - When using Hibernate v 4.0 and Generation Type as `AUTO`, specifically for MySql, Hibernate would choose the `IDENTITY` strategy (and thus use the `AUTO_INCREMENT` feature) for generating IDs for the table in question.
    - Starting with version 5.0 when Generation Type is selected as `AUTO`, Hibernate uses `SequenceStyleGenerator` regardless of the database. In case of MySql Hibernate emulates a sequence using a table and is why you are seeing the `hibernate_sequence` table. MySql doesn't support the standard sequence type natively.
  - If you don't want to use the primary key as a foreign key, then you could simply write:
    ```java
    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Txn txn;
    ```
  - Sicne JPA 2.0 you dont necessarily need this complex generic generator stuff. **You could simply write `@MapsId`**

#### Unidirectional @OneToOne

- **Notes**
  - In this case only one of the two classes has a reference to the other.
- **Code**
    ```java
    @OneToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    ```

### @ManyToMany

### ManyToMany

#### Unidirectional @ManyToMany

- **Code**
  - Executed SQL:
      ```sql
      DROP TABLE IF EXISTS `Cart_Items`;
      DROP TABLE IF EXISTS `CartManyToMany`;
      DROP TABLE IF EXISTS `ItemManyToMany`;

      CREATE TABLE `CartManyToMany` (
        `cart_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
        `cart_total` decimal(10,0) NOT NULL,
        PRIMARY KEY (`cart_id`)
      ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

      CREATE TABLE `ItemManyToMany` (
        `item_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
        `item_desc` varchar(20) NOT NULL,
        `item_price` decimal(10,0) NOT NULL,
        PRIMARY KEY (`item_id`)
      ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

      CREATE TABLE `CartManyToMany_ItemsManyToMany` (
        `cart_id` int(11) unsigned NOT NULL,
        `item_id` int(11) unsigned NOT NULL,
        PRIMARY KEY (`cart_id`,`item_id`),
        CONSTRAINT `fk_cart` FOREIGN KEY (`cart_id`) REFERENCES `CartManyToMany` (`cart_id`),
        CONSTRAINT `fk_item` FOREIGN KEY (`item_id`) REFERENCES `ItemManyToMany` (`item_id`)
      ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
      ```
  - Java Code:
      ```java
      @Entity
      @Table(name = "CartManyToMany")
      @Data
      @ToString(exclude = "items")
      @EqualsAndHashCode(exclude = "items")
      public class CartManyToMany {

          @Id
          @GeneratedValue(strategy = GenerationType.IDENTITY)
          @Column(name = "cart_id")
          private long id;

          @Column(name = "cart_total")
          private double total;

          @ManyToMany(targetEntity = ItemManyToMany.class, cascade = { CascadeType.ALL })
          @JoinTable(name = "CartManyToMany_ItemsManyToMany",
                  joinColumns = { @JoinColumn(name = "cart_id") },
                  inverseJoinColumns = { @JoinColumn(name = "item_id") })
          private Set<ItemManyToMany> items;
      }

      @Entity
      @Data
      @Table(name = "ItemManyToMany")
      public class ItemManyToMany {
          @Id
          @Column(name="item_id")
          @GeneratedValue(strategy=GenerationType.IDENTITY)
          private long id;

          @Column(name = "item_price")
          private double price;

          @Column(name = "item_desc")
          private String description;
      }
      ```
- **Notes**:
  - Only one of the classes (`CartManyToMany`) has a Collection to the other class (`Set<IntemManyToMany>`).
  - Using annotation this class defines all attributes of the relatin!
    - `@ManyToMany`
      - `targetEntity` - the other class that is used in the mapping
      - `cascade` - cascade type
    - `@JoinTable`
      - `name` - name of the table that is used for ManyToMany mappings.
      - `joinColumns` - The foreign key columns of the join table which reference the primary table of the entity that **does own** the association.
        - Which is the owning side in ManyToMany relationship?
          - In the case of ManytoMany relationships in bidirectional scenario the Owner of the relationship can be selected arbitrarily, but having in mind the purpose you should select the entity that makes more sense to retrieve first or the one that is more used according to your purpose.
      - `inverseJoinColumns` - The foreign key columns of the join table which reference the primary table of the entity that does **not** own the association.

#### Bidirectional @ManyToMany

- **Code**
  - Same as above, except that `Item` also get a collection of `Cart`
    ```java
    @ManyToMany(mappedBy = "items")
    private List<CartManyToMany> carts = new ArrayList<>();
    ```

## Collection Mapping

### Mapping a Set

- Can be done using the `@ElementCollection` as seen previously. The solution below has two separate entities and uses a `ManyToOne` relationship.
    ```java
    @Entity
    @Data
    @NoArgsConstructor
    public class Book1 {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        int id;
        String title;
        @OneToMany(cascade = CascadeType.ALL, mappedBy = "book")
        Set<Chapter1> chapters = new HashSet<>();
        @ElementCollection(fetch = FetchType.EAGER)
        @Column(name = "review")
        Set<String> reviews = new HashSet<>();
    }

    @Entity
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    @EqualsAndHashCode(exclude = "book")
    @ToString(exclude = "book")
    public class Chapter1 {
        @Getter
        @Setter
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        int id;
        @Getter
        @Setter
        String title;
        @Getter
        @Setter
        String content;
        @ManyToOne(optional = false)
        @Getter
        @Setter
        Book1 book;
    }
    ```
  - The `Chapter1` code doesn't use the `@Data` annotation because of the generated `toString` and `hashCode` methods would contain circular dependencies.
- Another option would be to use an `Embeddable`. In this case the class is not an entity anymore, hence no `Id` column.
    ```java
    @Embeddable
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public class Chapter1Embedded {
        @Getter
        @Setter
        String title;
        @Getter
        @Setter
        String content;
    }

    @Entity
    @Data
    @NoArgsConstructor
    public class Book1Embedded {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        int id;
        String title;
        @ElementCollection
        Set<Chapter1Embedded> chapters = new HashSet<>();
        @ElementCollection
        @Column(name="review")
        Set<String> reviews = new HashSet<>();
    }
    ```
  - In this case `Chapter1Embedded` is no longer an entity.
    ![ListMapping](/Java/Hibernate/res/BookEmbedded.PNG)

### Mapping a List

- The `@OrderColumn` annotation provides the capability to preserve the order of a `List`. To add order to our collection, we add the annotation and ordering definition; Hibernate automatically maintains the order of the `List`.
    ```java
    @Entity
    @NoArgsConstructor
    @Data
    public class Book3 {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        int id;
        @Getter
        @Setter
        String title;
        @ElementCollection
        @OrderColumn(columnDefinition = "int", name = "order_column")
        @Column(name = "review")
        List<String> reviews = new ArrayList<>();
    }
    ```
- If auto-table generation is enabled Hibernate generates a table where the List elements are stored. Note the `order_column` which stores the order of the elements.
    ![ListMapping](/Java/Hibernate/res/ListMapping.PNG)
- Storing an array would be the same as storing a `List`.

### Mapping a Map

- The code:
    ```java
    @Entity
    @NoArgsConstructor
    @Data
    public class Book5 {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        int id;
        @Getter
        @Setter
        String title;
        @ElementCollection(targetClass = String.class)
        @Column(name = "reference")
        @MapKeyColumn(name = "topic")
        Map<String, String> topicMap = new HashMap<>();
    }
    ```
- If done correctly, hibernate generates a new table called `Book5_reviews` where the individual map elements are stored.
    ![ListMapping](/Java/Hibernate/res/MappingMap.PNG)

## Transactions

### Intro

- A transaction is a collection of read and write operations that can either succeed or fail together, as a unit. **All database statements must execute within a transactional context**, even when the database client doesn’t explicitly define its boundaries.
- In computer science, **ACID (Atomicity, Consistency, Isolation, Durability)** of database transactions intended to guarantee validity even in the event of errors, power failures, etc. In the context of databases, a sequence of database operations that satisfies the ACID properties, and thus can be perceived as a single logical operation on the data, is called a transaction.

### ACID

- **Atomicity**
  - Atomicity is the property of grouping multiple operations into an all-or-nothing unit of work, which can succeed only if all individual operations succeed.
  - **MYSQL**
    - The undo log is stored in the rollback segment of the system tablespace.
- **Consistency**
  - A modifying transaction can be seen like a state transformation, moving the database from one valid state to another.
    - Ensuring that constrains are followed. E.g.: column types, column length, nullability, foreign key constraints, unique key constraints, custom check constraints
  - **MYSQL**
    - Traditionally, MySQL constraints are not strictly enforced, and the database engine replaces invalid values with predefined defaults:
      - out of range numeric values are set to either 0 or the maximum possible value
      - `String` values are trimmed to the maximum length
      - Incorrect data values are permitted (e.g. 2015-02-30)
      - `NOT NULL` constraints are only enforced for single `INSERT` statements. For multi-row inserts, 0 replaces a null numeric values, and the empty value is used for a null String.
    - Since the 5.0.2 version, strict constraints are possible if the database engine is configured to use a custom sql mode:
      - `SET GLOBAL sql_mode='POSTGRESQL,STRICT_ALL_TABLES';`
    - Because the sql_mode resets on server startup, it’s better to set it up in the MySQL configuration file: `[mysqld] sql_mode = POSTGRESQL,STRICT_ALL_TABLES`
- **Isolation**
  - **Concurrency control**
    - **Two-phase locking**
      - Locking on lower-levels (e.g. rows) can offer better concurrency as it reduces the likelihood of contention. Because each lock takes resources, holding multiple lower-level locks can add up, so the database might decide to substitute multiple lower-level locks into a single upper-level one. This process is called lock escalation and it trades off concurrency for database resources.
      - Each database system comes with its own lock hierarchy but the most common types
        - shared (read) lock, preventing a record from being written while allowing concurrent reads
        - exclusive (write) lock, disallowing both read and write operations
      - The 2PL protocol splits a transaction in two sections:
        - expanding phase (locks are acquired and no lock is released)
        - shrinking phase (all locks are released and no other lock is further acquired).
      ![Statement Lifecycle](/Java/Hibernate/res/TwoPhaseLock.PNG)
          - both Alice and Bob select a post record, both acquiring a shared lock on this record
          - when Bob attempts to update the post entry, his statement is blocked by the Lock Manager because Alice is still holding a shared lock on this database row
          - only after Alice’s transaction ends and all locks are being released,Bob can resume his update operation
          - Bob’s update will generate a lock upgrade, so the shared lock is replaced by an exclusive lock, which will prevent any other concurrent read or write operation
          - Alice starts a new transaction and issues a select query for the same post entry, but the statement is blocked by the Lock Manager since Bob owns an exclusive lock on this record
          - after Bob’s transaction is committed, all locks are released and Alice’s query can be resumed, so she will get the latest value of this database record.
    - **Multi-Version Concurrency Control**
      - The promise of MVCC is that readers don’t block writers and writers don’t block readers. The only source of contention comes from writers blocking other concurrent writers, which otherwise would compromise transaction rollback and atomicity.
      - **MYSQL**
        - The InnoDB storage engine offers support for ACID transactions and uses MVCC for controlling access to shared resources. The InnoDB MVCC implementation is very similar to Oracle, and previous versions of database rows are stored in the rollback segment as well.
  - **Phenomena**
    - **Intro**
      - As the incoming traffic grows, the price for strict data integrity becomes too high, and this is the primary reason for having multiple isolation levels. Relaxing serializability guarantees may generate data integrity anomalies, which are also referred as *phenomena*.
      - Choosing a certain isolation level is a trade-off between increasing concurrency and acknowledging the possible anomalies that might occur.
      - Scalability is undermined by contention and coherency costs. The lower the isolation level, the less locking (or multi-version transaction abortions), and the more scalable the application will get. But a lower isolation level allows more phenomena, and the data integrity responsibility is shifted from the database side to the application logic, which must ensure that it takes all measures to prevent or mitigate any such data anomaly.
    - **Dirty Write**
      - A dirty write happens when two concurrent transactions are allowed to modify the same row at the same time. As previously mentioned, all changes are applied against the actual database object structures, which means that the **second transaction simply overwrites the first transaction pending change**.
      ![DirtyWrite](/Java/Hibernate/res/DirtyWrite.PNG)
      - If the two transactions commit, one transaction will silently overwrite the other transaction, causing a lost update. Another problem arises when the first transaction wants to roll back.
      - Although the SQL standard doesn’t mention this phenomenon, even the lowest isolation level (Read Uncommitted) is able to prevent it.
    - **Dirty Read**
      - A dirty read happens when a transaction is allowed to read the uncommitted changes of some other concurrent transaction.
      ![DirtyRead](/Java/Hibernate/res/DirtyRead.PNG)
      - This anomaly is only permitted by the Read Uncommitted isolation level, and, because of the serious impact on data integrity, most database systems offer a higher default isolation level.
      - To prevent dirty reads, the database engine must hide uncommitted changes from all the concurrent transactions (but the one that authored the change). Each transaction is allowed to see its own changes because otherwise the read-your-own-writes consistency guarantee is compromised.
    - **Non-repeatable read**
      - If one transaction reads a database row without applying a shared lock on the newly fetched record, then a concurrent transaction might change this row before the first transaction has ended.
      ![NonRepeatableRead](/Java/Hibernate/res/NonRepeatableRead.PNG)
      - Most database systems have moved to a Multi-Version Concurrency Control model, and shared locks are no longer mandatory for preventing non-repeatable reads. By verifying the current row version, a transaction can be aborted if a previously fetched record has changed in the meanwhile.
    - **Phantom read**
      - If a transaction makes a business decision based on a set of rows satisfying a given predicate, without predicate locking, a concurrent transaction might insert a record matching that particular predicate.
      ![PhantomRead](/Java/Hibernate/res/PhantomRead.PNG)
      - Phantom rows can lead a buyer into purchasing a product without being aware of a better offer that was added right after the user has finished fetching the offer list.
      - Phantom reads are not a problem on MySQL.
    - **Read skew**
      - Read skew is a lesser known anomaly that involves a constraint on more than one database tables. In the following example, the application requires the post and the post_details be updated in sync. Whenever a post record changes, its associated post_details must register the user who made the current modification.
      ![ReadSkew](/Java/Hibernate/res/ReadSkew.PNG)
      - The first transaction will see an older version of the post row and the latest version of the associated post_details. Because of this read skew, the first transaction will assume that this particular post was updated by Bob, although, in fact, it is an older version updated by Alice.
      - Like with non-repeatable reads, there are two ways to avoid this phenomenon:
        - the first transaction can acquire shared locks on every read, therefore preventing the second transaction from updating these records
        - the first transaction can be aborted upon validating the commit constraints (when using an MVCC implementation of the Repeatable Read or Serializable isolation levels).
      - On Mysql `READ UNCOMMITTED` and `READ COMMITTED` permit Read Skew, while `REPEATABLE READ` and `SERIALIZABLE` isolation levels don't.
    - **Write skew**
      - Like read skew, this phenomenon involves disjoint writes over two different tables that are constrained to be updated as a unit. Whenever the post row changes, the client must update the post_details with the user making the change.
      ![DirtyWrite](/Java/Hibernate/res/WriteSkew.PNG)
      - Both Alice and Bob will select the post and its associated post_details record. If write skew is allowed, Alice and Bob can update these two records separately, therefore breaking the constraint.
      - Prevention: same as above.
      - On MySQL only the `SERIALIZABLE` isolation level forbids write skew!
    - **Lost update**
      - This phenomenon happens when a transaction reads a row while another transaction modifies it prior to the first transaction to finish. In the following example, Bob’s update is silently overwritten by Alice, who is not aware of the record update.
      ![DirtyWrite](/Java/Hibernate/res/LostUpdate.PNG)
      - This anomaly can have serious consequences on data integrity (a buyer might purchase a product without knowing the price has just changed), especially because it affects Read Committed, the default isolation level in many database systems.
      - With MVCC, the second transaction is allowed to make the change, while the first transaction is aborted when the database engine detects the row version mismatch (during the first transaction commit).
  - **Isolation Levels**
    - **Serializable is the only isolation level to provide a truly ACID transaction** interleaving. But serializability comes at a price as locking introduces contention, which, in turn, limits concurrency and scalability.

      ![IsolationLevels](/Java/Hibernate/res/IsolationLevels.PNG)
    - To read/set these values in JDBC:
      - `int level = connection.getMetaData().getDefaultTransactionIsolation();`
      - `connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);`
    - **Isolation levels used by Database systems:**
      - Read Committed (Oracle, SQL Server, PostgreSQL)
      - Repeatable Read (MySQL)
    - **Prevented phenomena by Read Uncommitted**:

      ![Phenomens](/Java/Hibernate/res/Phenomenas.PNG)
      - **MYSQL** Although it uses MVCC, InnoDB implements Read Uncommitted so that dirty reads are permitted.
    - **Prevented phenomena by Read Committed**:

      ![PhenomenaDirtyRead](/Java/Hibernate/res/PhenomenaDirtyRead.PNG)
      - **MYSQL:** Query-time snapshots are used to isolate statements from other concurrent transactions. When explicitly acquiring shared or exclusive locks or when issuing update or delete statements (which acquire exclusive locks to prevent dirty writes), if the selected rows are filtered by unique search criteria (e.g. primary key), the locks can be applied to the associated index entries.
    - **Repeatable Read**

      ![RepeatableRead](/Java/Hibernate/res/PhRepeatableRead.PNG)
      - **MYSQL** Every transaction can only see rows as if they were when the current transaction started. This prevents non-repeatable reads, but it still allows lost updates and write skews.
    - **Serializable**

      ![Serlializable](/Java/Hibernate/res/PhSerializable.PNG)
      - **MYSQL** The Serializable isolation builds on top of Repeatable Read with the difference that every record that gets selected is protected with a shared lock as well. The locking-based approach allows MySQL to prevent the write skew phenomena, which is prevalent among many Snapshot Isolation implementations.
- **Durability**
  - Durability ensures that all committed transaction changes become permanent.
  - When a transaction is committed, the database persists all current changes in an append-only, sequential data structure commonly known as the *redo log*.
  - **MYSQL** All the redo log entries associated with a single transaction are stored in the mini transaction buffer and flushed at once into the global redo buffer. The global buffer is flushed to disk during commit. By default, there are two log files which are used alternatively.

### Read Only Transactions

- **Intro**
  - The JDBC Connection defines the `setReadOnly(boolean readOnly)` method which can be used to hint the driver to apply some database optimizations for the upcoming read-only transactions. This method shouldn’t be called in the middle of a transaction because the database system cannot turn a read-write transaction into a read-only one (a transaction must start as read-only from the very beginning).
  - **MYSQL** If a modifying statement is executed when the `Connection` is set to read-only, the JDBC driver throws an exception. InnoDB can optimize read-only transactions because it can skip the transaction ID generation as it’s not required for read-only transactions.
- **Read-only transaction routing**
  - Setting up a database replication environment is useful for both high-availability (a Slave can replace a crashing Master) and traffic splitting. In a Master-Slave replication topology, the Master node accepts both read-write and read-only transactions, while Slave nodes only take read-only traffic.
  - **MYSQL** The `com.mysql.jdbc.ReplicationDriver` supports transaction routing on a Master-Slave topology, the decision being made on the `Connection` read-only status basis.

    ![TransactionRouting](/Java/Hibernate/res/TransactionRouting.PNG)

#### Transaction Boundaries

- **Separating DAO from usage**
  - By default, every **JDBC (!)** `Connection` **starts in auto-commit mode**, each statement being executed in a separate transaction. Unfortunately, it doesn’t work for multi-statement transactions as it moves atomicity boundaries from the logical unit of work to each individual statement.
    - (**Hibernate Connections are NOT auto-committing by default.**)
  - In the following example, a sum of money is transferred between two bank accounts. The balance must always be consistent, so if an account gets debited, the other one must always be credited with the same amount of money.
    ```java
    try(Connection connection = dataSource.getConnection(); PreparedStatement transferStatement = connection.prepareStatement(
            "UPDATE account SET balance = ? WHERE id = ?"
        )) {
        transferStatement.setLong(1, Math.negateExact(cents));
        transferStatement.setLong(2, fromAccountId);
        transferStatement.executeUpdate();
        transferStatement.setLong(1, cents);
        transferStatement.setLong(2, toAccountId);
        transferStatement.executeUpdate();
    }
    ```
  - **Because of the auto-commit mode**, if the second statement failed, only those particular changes can be rolled back, **the first statement being already committed cannot be reverted anymore**.
  - The correct version:
    ```java
    try(Connection connection = dataSource.getConnection()) { connection.setAutoCommit(false);
    try(PreparedStatement transferStatement = connection.prepareStatement(
            "UPDATE account SET balance = ? WHERE id = ?"
        )) {
            transferStatement.setLong(1, Math.negateExact(cents));
            transferStatement.setLong(2, fromAccountId);
            transferStatement.executeUpdate();
            transferStatement.setLong(1, cents);
            transferStatement.setLong(2, toAccountId);
            transferStatement.executeUpdate();
    connection.commit(); } catch (SQLException e) { connection.rollback();
    throw e; }
    }
    ```
  - This is however shitty code - violating Single Responsibility Principle. One way to extract the transaction management logic is to use the Template method pattern:
    ```java
    public void transact(Consumer<Connection> callback) {
      Connection connection = null;
        try {
            connection = dataSource.getConnection();
            callback.accept(connection);
            connection.commit();
        } catch (Exception e) {
          if (connection != null) {
            try {
              connection.rollback();
            } catch (SQLException ex) {
              throw new DataAccessException(e);
            }
          }
          throw (e instanceof DataAccessException ?
            (DataAccessException) e : new DataAccessException(e));
        } finally {
          if(connection != null) {
            try { connection.close();
          } catch (SQLException e) {
            throw new DataAccessException(e);
          }
        }
      }
    }
    ```
  - **Transactions should never be abandoned on failure**, and it’s mandatory to initiate a transaction rollback (to allow the database to revert any uncommitted changes and release any lock as soon as possible).
    ```java
    transact((Connection connection) -> {
    try(PreparedStatement transferStatement = connection.prepareStatement(
            "UPDATE account SET balance = ? WHERE id = ?"
        )) {
            transferStatement.setLong(1, Math.negateExact(cents));
            transferStatement.setLong(2, fromAccountId);
            transferStatement.executeUpdate();
            transferStatement.setLong(1, cents);
            transferStatement.setLong(2, toAccountId);
            transferStatement.executeUpdate();
    } catch (SQLException e) {
    throw new DataAccessException(e);
    } });
    ```
- **Application-level transactions**
  - **Intro**
    - A logical transaction may be composed of multiple web requests, including user think time, for which reason it can be visualized as a long conversation. (Alice and Bob are working on Motius's ticket manager, both opening the same ticket making some changes and then overwriting each others changes.) --> A logical transaction may be composed of multiple web requests, including user think time, for which reason it can be visualized as a long conversation.
      - Spanning a database transaction over multiple web requests is prohibitive since locks would be held during user think time, therefore hurting scalability.
    - HTTP is stateless by nature and, for very good reasons, stateless applications are easier to scale than stateful ones. But application-level transactions cannot be stateless as otherwise newer requests would not continue from where the previous request was left. Preserving state across multiple web requests allows building a conversational context, providing application-level repeatable reads guarantees. In the next diagram, Alice uses a stateful conversational context, but, in the absence of a record versioning system, it’s still possible to lose updates.
    ![DirtyRead](/Java/Hibernate/res/Stateful.PNG)
    - Without Alice to notice, the batch process resets the product quantity. Thinking the product version hasn’t changed, Alice attempts to purchase one item which decreases the previous product quantity by one. In the end, Alice has simply overwritten the batch processor modification and data integrity has been compromised. --> To prevent lost updates, a concurrency control mechanism becomes mandatory.
  - **Pessimic Locking**
    - Most database systems already offer the possibility of **manually requesting shared or exclusive locks**. This concurrency control is said to be pessimistic because it assumes that conflicts are bound to happen, and so they must be prevented accordingly.
    - Acquiring locks on critical records can prevent non-repeatable reads, lost updates, as well as read and write skew phenomena.
  - **Optimistic Locking**
    - Optimistic locking doesn’t incur any locking at all. A much better name would be optimistic concurrency control since it uses a totally different approach to managing conflicts than pessimistic locking.
    - Each database row must have an associated version, which is locally incremented by the logical transaction. Every modifying SQL statement (update or delete) uses the previously loaded version as an assumption that the row hasn’t been changed in the meanwhile.
    - Because even the lowest isolation level can prevent write-write conflicts, only one transaction is allowed to update a row version at any given time. Since the database already offers monotonic updates, the row versions can also be incremented monotonically, and the application can detect when an updating record has become stale. The optimistic locking concurrency algorithm looks like this:
    ![DirtyRead](/Java/Hibernate/res/OptimisticLocking.PNG)
      - when a client reads a particular row, its version comes along with the other fields
      - upon updating a row, the client filters the current record by the version it has previously loaded.
        ```java
        UPDATE product
              SET (quantity, version) = (4, 2)
              WHERE id = 1 AND version = 1
        ```
      - if the statement update count is zero, the version was incremented in the meanwhile, and the current transaction now operates on a stale record version.
      - **Using timestamps to order events is rarely a good idea. System time is not always monotonically incremented, and it can even go backwards, due to network time synchronization.**

## Persisting objects

- **Intro**
  - It is important to understand from the beginning that all of the methods (`persist`, `save`, `update`, `merge`, `saveOrUpdate`) do **not** immediately result in the corresponding SQL `UPDATE` or `INSERT` statements. (Hibernate defaults auto-commit to false!) The actual saving of data to the database occurs on committing the transaction or flushing the `Session`. The mentioned methods basically manage the state of entity instances by transitioning them between different states along the lifecycle.
- **Persistance operations**
  - Summary of the persistance operations:
  ![Flow](/Java/Hibernate/res/FLOW.PNG)
  - `session.persist(Object o)`
    - The `persist` method is intended for adding a **new** entity instance to the persistence context, i.e. transitioning an instance from transient to persistent state. (The `persist` operation **must be used only for new entities**.)
    - If you try to `persist` a detached instance, the implementation is bound to throw an exception.
    - If the object properties are changed before the transaction is committed or session is flushed, it will also be saved into database.
      ```java
      Foo foo = new Foo();
      foo.setValue(4);
      session.persist(foo);
      foo.setValue(5);
      session.commit(); // foo is saved with value of 5!
      ```
    - `persist` doesn’t return anything so we need to use the persisted object to get the generated identifier value
  - `session.save(Object o)`
    - Its purpose is basically the same as `persist`, but it has different implementation details.
    - The call of `save` on a detached instance creates a new persistent instance and assigns it a new identifier, which results in a duplicate record in a database upon committing or flushing.
    - **Important:**
      - Technically you can `save` an entity outside of the transaction boundary, however mapped entities will not be saved causing data inconsistency. It’s very normal to forget flushing the session because it doesn’t throw any exception or warnings.
      - Hibernate `save` method returns the generated id immediately, this is possible because primary object is saved as soon as `save` method is invoked.
      - If there are other objects mapped from the primary object, they **gets saved at the time of committing transaction or when we flush the session**.
  - `session.merge(Object o)`
    - The main intention of the merge method is to update a persistent entity instance with new field values from a detached entity instance. The `merge` method
      - finds an entity instance by id taken from the passed object (either an existing entity instance from the persistence context is retrieved, or a new instance loaded from the database);
      - copies fields from the passed object to this instance;
      - returns newly updated instance
    - Note that the merge method returns an object — it is the mergedPerson object that was loaded into persistence context and updated, not the person object that you passed as an argument.
      ```java
      Person person = new Person();
      person.setName("John");
      session.save(person);

      session.evict(person);
      person.setName("Mary");

      Person mergedPerson = (Person) session.merge(person);
      ```
  - `session.refresh(Object o)`
    - Hibernate provides a mechanism to refresh persistent objects from their database representation. These methods will reload the properties of the object from the database, overwriting them; thus, as stated, `refresh()` is the inverse of `merge()`.
      ```java
        public void refresh(Object object)
        public void refresh(Object object, LockMode lockMode)
      ```
  - `session.update(Object o)`
    - it acts upon passed object (its return type is void) the update method transitions the passed object from detached to persistent state;
    - this method throws an exception if you pass it a transient entity.
  - `saveOrUpdate(Object o)`
    - The main difference of `saveOrUpdate` method is that it does not throw exception when applied to a transient instance; instead, it makes this transient instance persistent.
  - `void delete(Object object)`
    - This method takes a persistent object as an argument. The argument can also be a transient object with the identifier set to the ID of the object that needs to be erased.
    - If you set the cascade attribute to `delete` or `all`, the delete will be cascaded to all of the associated objects.
    - Hibernate also supports **bulk deletes**, where your application executes a DELETE HQL statement against the database. These are very useful for deleting more than one object at a time because each object does not need to be loaded into memory just to be deleted:
      - `session.createQuery("delete from User").executeUpdate();`
    - Bulk deletes **do not cause cascade** operations to be carried out. If cascade behavior is needed, you will need to carry out the appropriate deletions yourself, or use the session’s `delete()` method.

- **Updating data**
  - Imagine that we want to change an attribute of an object, that is already persisted to the database. We fetch the object with a query and then:
    ```java
    Ranking ranking = query.uniqueResult();
    assertNotNull(ranking, "Could not find matching ranking");
    ranking.setRanking(9);
    tx.commit();
    ```
  - Hibernate watches the (persistent) data model, and when something is changed, it automatically updates the database to reflect the changes. It generally does this by using a proxied object. When you change the data values in the object, the proxy records the change so that the transaction knows to write the data to the database on transaction commit. To summarize:
    - All "persistent" objects are tracked within a transaction.
    - In order for the changes to take effect, the transaction must be committed!
- **Removing data**
  - Find ranking -> which will give us a Ranking in *persistent* state. Then we remove it via the session and commit the change.
    ```java
    session.delete(ranking);
    tx.commit();
    ```
- **Notes**
  - If you call `persist` on an object you can see immediately that the database has assigned an ID to it. However when you look into the database you see that the object has not yet been actually saved. Where does this ID come from, if your object is not even saved?
  - The answer is the Isolation context. When calling the `persist` method **inside a transaction**, the changes are not visible for others until the transaction is committed. If you log the SQL statements executed by hibernate you can see that the object is in fact inserted into its table, but as long as the transaction is not committed it won't be visible to you via the MySQL workbench interface.

## Loading objects from the dabase

### Sesstion context

#### Session context while loading/saving

- **Loading**
  - Requesting a persistent object again from the same Hibernate session returns the same Java instance of a class, which means that you can compare the objects using the standard Java `==` equality syntax. If, however, you request a persistent object from more than one Hibernate session, Hibernate will provide distinct instances from each session, and the `==` operator will return `false` if you compare these object instances.
- **Saving**
  - For saving it's similar. If you save an object in session "A", close the session, open a new session, change a field of the object and save it, then hibernate creates a new entry in the database! That is because the session is taking care of tracking "persisted" objects!
  - However if you call `saveOrUpdate()` then even if we are in a new session, the object is updated and no new row is created.
- **Implementing equals()**
  - Taking this into account, if you are comparing objects in two different sessions, you will need to implement the `equals()` method on your Java persistence objects, which you should probably do as a regular occurrence anyway. (Just don’t forget to implement `hashCode()` along with it.)
  - Implementing `equals()` can be interesting. Hibernate wraps the actual object in a proxy (for various performance-enhancing reasons, like loading data on demand), so you need to factor in a class hierarchy for equivalency; it’s also typically more efficient to **use accessors** (`getXYZ()`) in your `equals()` and `hashCode()` methods, as opposed to the actual fields.
  - In the example below we check if all fields if `this` are equal to the field of the other object.
    ```java
    @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof SimpleObject)) return false;

            SimpleObject that = (SimpleObject) o;

            // we prefer the method versions of accessors, because of Hibernate's proxies.
            if (getId() != null ?
              !getId().equals(that.getId()) : that.getId() != null)
                return false;
            if (getKey() != null ?
              !getKey().equals(that.getKey()) : that.getKey() != null)
                return false;
            return getValue() != null ? getValue().equals(that.getValue()) : that.getValue() == null;
        }
    ```

#### Loading and managing entities

- **Session.load()**
  - Each of the following `load()` method requires the object’s primary key as an identifier.
    ```java
    public <T> T load(Class<T> theClass, Serializable id)
    public Object load(String entityName, Serializable id)
    public void load(Object object, Serializable id)
    ```
  - With the more advanced loading methods you can specify the locking mode.
    ```java
    public <T> T load(Class<T> theClass, Serializable id, LockMode lockMode)
    public Object load(String entityName, Serializable id, LockMode lockMode)
    ```
    - `NONE`: Uses no row-level locking, and uses a cached object if available; this is the Hibernate **default**.
    - `READ`: Prevents other `SELECT` queries from reading data that is in the middle of a transaction (and thus possibly invalid) until it is committed.
    - `UPGRADE`: Uses the SELECT FOR UPDATE SQL syntax to lock the data until the transaction is finished.
    - `UPGRADE_NOWAIT`: Uses the NOWAIT keyword (for Oracle), which returns an error immediately if there is another thread using that row; otherwise this is similar to UPGRADE.
    - `UPGRADE_SKIPLOCKED`: Skips locks for rows already locked by other updates, but otherwise this is similar to UPGRADE.
    - `OPTIMISTIC`: This mode assumes that updates will not experience contention. The entity’s contents will be verified near the transaction’s end.
    - `OPTIMISTIC_FORCE_INCREMENT`: This is like OPTIMISTIC, except it forces the version of the object to be incremented near the transaction’s end.
    - `PESSIMISTIC_READ` and `PESSIMISTIC_WRITE`: Both of these obtain a lock immediately on row access.
    - `PESSIMISTIC_FORCE_INCREMENT`: This obtains the lock immediately on row access, and also immediately updates the entity version.
  - `Session.load` will always return a **“proxy”** (Hibernate term) without hitting the database. In Hibernate, proxy is an object with the given identifier value, its properties are not initialized yet, it just look like a temporary fake object. Properties are only fetched from the database when you actually refer to them / i.e. when you actually use them. If it turns out, that the object with the given ID doesn't exist in the database anymore, it throws an `ObjectNotFoundException`.
  - You should not use a `load()` method unless you are sure that the object exists. If you are not certain, then use one of the `get()` methods. The `load()` methods will **throw an exception** if the unique ID is not found in the database, whereas the `get()` methods will merely return a `null` reference.
- **Session.get()**
  - `get()` returns the object by fetching it from database or from hibernate cache.
    ```java
    public <T> T get(Class<T> clazz, Serializable id)
    public Object get(String entityName, Serializable id)
    public <T> T get(Class<T> clazz, Serializable id, LockMode lockMode)
    public Object get(String entityName, Serializable id, LockMode lockMod

    /*Usage*/
    Supplier supplier = session.get(Supplier.class,id);
    if (supplier == null) {
        System.out.println("Supplier not found for id " + id);
    return; }
    ```

#### Lazy loading

- Imagine that you have a huge "web" of objects, i.e. there are a lot of references between the objects. Loading 10k rows from a table would mean loading another 80k from various tables due to the references. To manage this problem, Hibernate provides a facility called lazy loading.
- At lazy loading an entity’s associated entities will be *loaded only when they are directly requested*! So until the object is not actually required Hibernate provides "proxies", and when requested, then these proxies are replaced by the actual objects loaded from the database.
- This only works in active sessions. Hibernate can only access the database via a session. If an entity is detached from the session when we try to access an association (via a proxy or collection wrapper) that has not yet been loaded, Hibernate throws a `LazyInitializationException`.
- `isInitialized(Object proxy), isPropertyInitialized(Object proxy, String propertyName)` - to check if an object is initialized
- `initialize(Object proxy)` force a proxy to be initialized (within a session)
- Certain relationships can be marked as being “lazy,” and they will not be loaded from the database until they are actually required.
- The **default** in Hibernate is that classes (including collections like `Set` and `Map`) should be lazily loaded. Given the following class only `userId` and `username` are loaded.
    ```java
    public class User {
      int userId;
      String username;
      EmailAddress emailAddress;
      Set<Role> roles;
    }
    ```

#### Session flushing

- Flushing the session (`(void) session.flush()`) forces Hibernate to synchronize the in-memory state of the Session with the database (i.e. to write changes to the database). By default, Hibernate will flush changes automatically for you:
  - before some query executions
  - when a transaction is committed
- Allowing to explicitly flush the Session gives finer control that may be required in some circumstances (to get an ID assigned, to control the size of the Session,...).
- Hibernate automatically persists changes made to persistent objects into the database.
- You can determine if the session is dirty with the `boolean isDirty()` method.
- You can also instruct Hibernate to use a flushing mode for the session with the `void setHibernateFlushMode(FlushMode flushMode)` method. The `FlushMode getHibernateFlushMode()` method returns the flush mode for the current session.
- Flush modes:
- `ALWAYS`: Every query flushes the session before the query is executed. This is going to be very slow.
- `AUTO`: Hibernate manages the query flushing to guarantee that the data returned by a query is up to date.
- `COMMIT`: Hibernate flushes the session on transaction commits.
- `MANUAL`: Your application needs to manage the session flushing with the `flush()` method. Hibernate never flushes the session itself.

#### Cascading operations

- Cascading only makes sense only for Parent – Child associations (the Parent entity state transition being cascaded to its Child entities). Cascading from Child to Parent is not very useful and usually, it’s a mapping code smell.
- The cascade types supported by the Java Persistence Architecture are as follows:
  - `PERSIST`, `MERGE`, `REFRESH`, `REMOVE`, `DETACH`, `ALL`
- It’s worth pointing out that Hibernate has its own configuration options for cascading, which represent a superset of these.
  - `CascadeType.PERSIST` means that `save()` or  operations cascade to related entities.
  - `CascadeType.MERGE` means that related entities are merged into managed state when the owning entity is merged.
    - "push this detached entity back into managed status and save its state changes"; the cascading means that all associated entities get pushed back the same way, and the managed-entity handle you get back from .merge() has all managed entities associated with it."
  - `CascadeType.REFRESH` does the same thing for the `refresh()` operation.
  - `CascadeType.REMOVE` removes all related entities association with this setting when the owning entity is deleted.
  - `CascadeType.DETACH` detaches all related entities if a manual detach were to occur.
  - `CascadeType.ALL` is shorthand for **all** of the cascade operations.
    ```java
    @Entity
    public class Post {

      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      @Getter
      private Long id;

      @Getter
      @Setter
      private String name;

      @OneToOne(mappedBy = "post",
              cascade = CascadeType.ALL, orphanRemoval = true)
      @Getter
      private PostDetails details;

      public void addDetails(PostDetails details) {
          this.details = details;
          details.setPost(this);
      }

      public void removeDetails(PostDetails details) {
          if (details != null) {
              details.setPost(null);
          }
          this.details = null;
      }
    }

    @Entity
    public class PostDetails {

        @Id
        @Getter
        private Long id;

        @Column(name = "created_on")
        @Temporal(TemporalType.TIMESTAMP)
        private Date createdOn = new Date();

        @Getter
        @Setter
        private boolean visible;

        @OneToOne
        @MapsId
        private Post post;

        public void setPost(Post post) {
            this.post = post;
        }
    }
    ```
    - **Notes**
      - The setter is **NOT** generated on details. Instead we add our own setter where we set the other side's reference as well. Same for removal! (The **bidirectional associations should always be updated on both sides**, therefore the Parent side should contain the addChild and removeChild combo. These methods ensure we always synchronize both sides of the association, to avoid object or relational data corruption issues.)
      - The `Post` entity plays the Parent role and the `PostDetails` is the Child.
      - Note how the date is saved! `@Temporal(TemporalType.TIMESTAMP)`
      - `@MapsId` creates a `post_id` column in the `PostDetails` that stores the `Post`'s primary key.
      - Note that in the `PostDetails` in the setter method in order to avoid circular dependency via `Post`'s setter we **only** set the `this.post`
      - When using this type of cascading upon deleting the parent (post) the child (postdetails) is also removed from the database.
- To include only refreshes and merges in the cascade operation for a one-to-one relationship, you might see the following:
    ```java
    @OneToOne(cascade={CascadeType.REFRESH, CascadeType.MERGE})
    EntityType otherSide;
    ```

#### Orphan removal

- Orphan removal is an entirely ORM-specific thing. It marks "child" entity to be removed when it's no longer referenced from the "parent" entity, e.g. when you remove the child entity from the corresponding collection of the parent entity. (In contrast ON DELETE CASCADE is a database-specific thing, it deletes the "child" row in the database when the "parent" row is deleted.)
- Imagine that you have a `Library` (object) with 3 books (`List<Book>`). Now if orphan removal is turned on, removing an object via `library.getBooks().remove(0);` will mark it as an orphan (as the parent "abandoned it), and hence when the transaction is **committed**, the orphan will be deleted by the ORM.
    ```java
    @OneToMany(orphanRemoval = true, mappedBy = "library")
    List<Book> books = new ArrayList<>();
    ```

### Hibernate Query Language (HQL) and SQL

#### Simple HQL examples

  ```java
  private Person findPerson(Session session, String name) {
      Query<Person> query = session.createQuery("from Person p where p.name=:name",
              Person.class);
      query.setParameter("name", name);
      Person person = query.uniqueResult();
      return person;
  }
  ```

- Select data from the table associated with the `Person` entity (aliased to “p”) and limited to objects whose “name” attribute is equal to a parameter value. It also specifies the reference type of the query (with `Person.class`) to cut down on typecasting and potential errors of incorrect return types.
- Note that `uniqueResult()` returns a single instance that matches the query, or null if the query returns no results. Throws `NonUniqueResultException` - if there is more than one matching result
- We could fix the exception throwing in case of multiple matches by using `query.setMaxResults(1)`, and returning the first (and only) entry in `query.list()`, but the right way to fix it is to figure out how to be very specific in returning the right `Person`.

- **Simple query with some math**
    ```java
      /**...**/
      Transaction tx = session.beginTransaction();
      Query<Ranking> query = session.createQuery("from Ranking r "
                      + "where r.subject.name=:name "
                      + "and r.skill.name=:skill", Ranking.class);
      query.setParameter("name", "J. C. Smell");
      query.setParameter("skill", "Java");
      IntSummaryStatistics stats = query.list()
              .stream()
              .collect(Collectors.summarizingInt(Ranking::getRanking));
      long count = stats.getCount();
      int average = (int) stats.getAverage();

      tx.commit();
      session.close();
      assertEquals(count, 3);
      assertEquals(average, 7);
      ```
  - Imagine a scenario where the "Ranking" entity represents an assessment of a given person. A `Ranking` entity contains:
    - the person who submitted the evaluation
    - the person who receives the evalutation
    - the name of the programming language that is "ranked"
    - the score
  - For example `Rank(joe, mike, "Java", 5)` means that joe `Person` ranked mike `Person`'s Java skills to be 5 on the scale of 10. In this scenario `Ranking` and `Person` are different entities, stored in different tables, hence using traditional SQL a JOIN would be necessary.
  - What happens if we already have multiple rankings (integer values in a "ranking" field) saved for a given `Person` and we want to calculate the average? Things to note:
    - Hibernate takes care of writing all of the SQL for us, and we can use the objects “naturally.” --> We are **not** using JOINs in the query!
    - When dirty reads are not acceptable, then a transaction for the select is appropriate. Most of the time, with that kind of scenario, the select will be done in conjunction with some data modification that requires full consistency for a point in time. -> hence the transaction around the query.
- **A more advanced query**
  - On the query below we are actually writing a full SQL query with the "select" statement included. The result is but List{["Name1", avgScoreForSkill], ["Name2", avgScore2] ...} ordered by the score. Since the returned data structure doesn't correspond to any of our POJOs, the returned type is declared as `List<Object[]>` To get the best person for the given skill we take the first element and extract the first column value (his name). -> `(String) result.get(0)[0])`
    ```java
    Query<Object[]> query = session.createQuery("select r.subject.name, avg(r.ranking)"
                    + " from Ranking r where "
                    + "r.skill.name=:skill "
                    + "group by r.subject.name "
                    + "order by avg(r.ranking) desc", Object[].class);
            query.setParameter("skill", skill);
            List<Object[]> result = query.list();
            if (result.size() > 0) {
                return findPerson(session, (String) result.get(0)[0]);
            }
    ```

#### Getting deeper into HQL

- **HQL Syntax basics**
  - **Intro**
    - HQL is an object-oriented query language, similar to SQL, but instead of operating on tables and columns, HQL works with persistent objects and their properties.
    - Hibernate’s query facilities do not allow you to alter the database structure.
    - Use HQL (or criteria) whenever possible to avoid database portability hassles, as well as to take advantage of Hibernate’s SQL-generation and caching strategies.
    - Advantage over SQL: It can make use of the relationship information defined in the Hibernate mappings.
  - **Update**
    ```java
    UPDATE [VERSIONED]
      [FROM] path [[AS] alias] [, ...]
      SET property = value [, ...]
      [WHERE logicalExpression]

    Query query=session.createQuery("update Person set creditscore=:creditscore where name=:name");
    query.setInteger("creditscore", 612);
    query.setString("name", "John Q. Public");
    int modifications = query.executeUpdate();
    ```
    - `path` - The fully qualified name of the entity or entities
    - `alias` - used to abbreviate references to specific entities or their properties, and must be used when property names in the query would otherwise be ambiguous.
    - `VERSIONED` - means that the update will update time stamps, if any, that are part of the entity being updated.
    - `FROM` - names of properties of entities listed in the `FROM` path.
  - **Delete**
    ```java
    DELETE
      [FROM] path [[AS] alias]
      [WHERE logicalExpression]

    Query query=session.createQuery("delete from Person where accountstatus=:status");
    query.setString("status", "purged");
    int rowsDeleted=query.executeUpdate();
    ```
  - **Insert**
    ```java
    INSERT
      INTO path ( property [, ...])
      select

    Query query=session.createQuery("insert into purged_users(id, name, status) "+
    "select id, name, status from users where status=:status");
    query.setString("status", "purged");
    int rowsCopied=query.executeUpdate();
    ```
  - **Select**
    ```java
    [SELECT [DISTINCT] property [, ...]]
      FROM path [[AS] alias] [, ...] [FETCH ALL PROPERTIES]
      WHERE logicalExpression
      GROUP BY property [, ...]
      HAVING logicalExpression
      ORDER BY property [ASC | DESC] [, ...]
    ```
    - `FETCH ALL PROPERTIES` - is used, then lazy loading semantics will be ignored, and all the immediate properties of the retrieved object(s) will be actively loaded (this does not apply recursively).
    - Use the select clause if you don't want to fetch all the columns but only certain properties. (= memory efficient)
      - `select product.name from Product product`
      - This result set contains a `List` of `Object` arrays—each array represents one set of properties.
- **Named Queries**
  - Named queries are created via class-level annotations on entities; normally, the queries apply to the entity in whose source file they occur, but there’s no absolute requirement for this to be true.
  - Named queries are created with the `@NamedQueries` annotation, which contains an array of `@NamedQuery` sets; each has a query and a name.
  - **One effect of the hierarchy we use here is that Lombok is no longer as usable as it has been** - Lombok works by analyzing Java source code. It does not walk a hierarchy; while this would be very useful, there are some real technical challenges to implementation.
  - Adding a named query is as simple as adding an annotation to one of the entities. (You can add the annotation to any of the entities.)
  - Examples:
    ```java
    @NamedQuery(name = "supplier.findAll", query = "from Supplier s")
    /*Or "bulk" definition: */
    @NamedQueries({
            @NamedQuery(name = "supplier.findAll", query = "from Supplier s"),
            @NamedQuery(name = "supplier.findByName",
                    query = "from Supplier s where s.name=:name"),
    })

    Query query = session.getNamedQuery("supplier.findAll");
    List<Supplier> suppliers = query.list();
    ```
  - Note: SQL specific stuff is case **in-sensitive**, however when referencing actual Java classes the references are **case-sensitive**!
- **Logging and Commenting the Underlying SQL**
  - Hibernate can output the underlying SQL behind your HQL queries into your application’s log file.
  - `show_sql` property. Set this property to true in your `hibernate.cfg.xml` configuration file.
  - Tracing your HQL statements through to the generated SQL can be difficult, so Hibernate provides a commenting facility on the Query object that lets you apply a comment to a specific query. The `Query` interface has a `setComment()` method that takes a String object as an argument, as follows:
    - `public Query setComment(String comment)`
  - You will also need to set a Hibernate property, `hibernate.use_sql_comments`, to true in your Hibernate configuration.
    ```java
    String hql = "from Supplier";
    Query query = session.createQuery(hql);
    query.setComment("My HQL: " + hql);
    List results = query.list();

    Hibernate: /*My HQL: from Supplier*/ select supplier0_.id as id, supplier0_.name ➥ as name2_ from Supplier supplier0_
    ```
- **Logical restrictions (WHERE clause)**
  - Logic operators: `OR, AND, NOT`
  - Equality operators: `=, <>, !=, ^=`
  - Comparison operators: `<, >, <=, >=, like, not like, between, not between`
  - Math operators: `+, -, *, /`
  - Concatenation operator: `||`
  - Cases: Case `when <logical expression> then <unary expression> else <unary expression> end`
  - Collection expressions: `some, exists, all, any`
  - In addition, you may also use the following expressions in the where clause:
    - HQL named parameters:, such as `:date`, `:quantity`
    - JDBC query parameter: `?`
    - Date and time SQL-92 functional operators: `current_time(), current_date(), current_timestamp()`
    - SQL functions (supported by the database): `length(), upper(), lower(), ltrim(), rtrim()`, etc.
- **Named Parameters**
    ```java
    String hql = "from Product where price > :price";
    Query query = session.createQuery(hql);
    query.setDouble("price",25.0);
    List results = query.list();
    ```
  - When the value to be provided will be known only at run time, you can use some of HQL’s object-oriented features to provide objects as values for named parameters. The Query interface has a setEntity() method that takes the name of a parameter and an object.
    ```java
    String supplierHQL = "from Supplier where name='MegaInc'";
    Query supplierQuery = session.createQuery(supplierHQL);
    Supplier supplier = (Supplier) supplierQuery.list().get(0);
    String hql = "from Product as product where product.supplier=:supplier";
    Query query = session.createQuery(hql);
    query.setEntity("supplier",supplier);
    List results = query.list();
    ```
- **Paging through the result set**
  - There are two methods on the `Query` interface for paging: `setFirstResult(int)` (takes an integer that represents the first row in your result set, starting with row 0) and `setMaxResults(int)` (only retrieve a fixed number of objects), just as with the Criteria interface.
  - E.g. Getting the second element of the query:
    ```java
    Query query = session.createQuery("from Product");
    query.setFirstResult(1);
    query.setMaxResults(2);
    List results = query.list();
    displayProductsList(results);
    ```
- **Obtaining unique result**
  - The `uniqueResult()` method on the Query object returns a single object, or `null` if there are zero results.
  - `Product product = (Product) query.uniqueResult();`
- **Ordering results**
  - via `order by`
    - `from Product p order by p.supplier.name asc, p.price asc`
- **Associations**
  - Associations allow you to use more than one class in an HQL query, just as SQL allows you to use joins between tables in a relational database. You add an association to an HQL query with the `join` clause.
  - Hibernate supports five different types of joins: `inner join,cross join,left outer join,right outer join` ,and `full outer join`. If you use `cross join`,just specify both classes in the `from` clause `(from Product p, Supplier s)`.
    - `from Product p inner join p.supplier as s`
- **Aggregate Methods**
  - `avg(property name)`: The average of a property’s value
  - `count(property name or *)`: The number of times a property occurs in the results
  - `max(property name)`: The maximum value of the property values
  - `min(property name)`: The minimum value of the property values
  - `sum(property name)`: The sum total of the property values
- **Bulk Updates and Deletes with HQL**
  - The `Query` interface contains a method called `executeUpdate()` for executing HQL `UPDATE` or `DELETE` statements. The `executeUpdate()` method returns an `int` that contains the number of rows affected by the update or delete.
    - `int rowCount = query.executeUpdate();`
  - **Be careful** when you use bulk delete with objects that are in relationships. Hibernate will not know that you removed the underlying data in the database, and you can get foreign key integrity errors. To get around this, you could set the not-found attribute to ignore on your one-to-many and many-to-one mappings, which will make IDs that are not in the database resolve to null references. The default value for the not-found attribute is exception.
- **Native SQL**
  - One reason to use native SQL is that your database supports some special features through its dialect of SQL that are not supported in HQL.
  - Another reason is that you may want to call stored procedures from your Hibernate application.
  - `public SQLQuery createSQLQuery(String queryString) throws HibernateException`
  - After you pass a string containing the SQL query to the `createSQLQuery()` method, you should associate the SQL result with an existing Hibernate entity, a join, or a scalar result. The SQLQuery interface has `addEntity()`, `addJoin()`, and `addScalar()` methods.
    ```java
    String sql = "select avg(product.price) as avgPrice from Product product";
    SQLQuery query = session.createSQLQuery(sql);
    query.addScalar("avgPrice",Hibernate.DOUBLE);
    List results = query.list();
    ```
  - Or a bit more complex example:
    ```java
    String sql = "select {supplier.*} from Supplier supplier";
    SQLQuery query = session.createSQLQuery(sql);
    query.addEntity("supplier", Supplier.class);
    List results = query.list();
    ```
    - Hibernate modifies the SQL and executes the following command against the database:
      - `select Supplier.id as id0_, Supplier.name as name2_0_ from Supplier supplier`

### Advanced Queries Using Criteria

- **Basics**
  - We can’t use Criteria to run update or delete queries or any DDL statements. It’s only used to fetch the results from the database using more object oriented approach.
  - The Criteria Query API lets you build nested, structured query expressions in Java, providing a **compile-time syntax checking** that is not possible with a query language like HQL or SQL.
  - The Criteria API also includes **query by example** (QBE) functionality. This lets you supply example objects that contain the properties you would like to retrieve instead of having to step-by-step spell out the components of the query. It also includes projection and aggregation methods, including counts.
- **Usage**
    ```java
    Criteria crit = session.createCriteria(Product.class);
    List<Product> results = crit.list();
    ```
  - Lists all `Products` and any objects of derived classes.
- **Restrictions**
  - To retrieve objects that have a property value that equals your restriction, use the `eq()` method on `Restrictions`:
    - `public static SimpleExpression eq(String propertyName, Object value)`
      ```java
      Criteria crit = session.createCriteria(Product.class);
      crit.add(Restrictions.eq("description","Mouse"));
      List<Product> results = crit.list()
      ```
    - To search for products that are **not** equal use the `ne()` method.
    - You **cannot** use the not-equal restriction to retrieve records with a `NULL` value in the database for that property (in sQL, and therefore in hibernate, `NULL` represents the absence of data, and so cannot be compared with data). -> you will have to use the `isNull()` restriction.
      ```java
      Criteria crit = session.createCriteria(Product.class);
      crit.add(Restrictions.isNull("name"));
      List<Product> results = crit.list();
      ```

  - Instead of searching for exact matches, we can retrieve all objects that have a property matching part of a given pattern. To do this, we need to create an SQL `LIKE` clause, with either the `like()` or the `ilike()` method. The `ilike()` method is case-insensitive.
    ```java
    Criteria crit = session.createCriteria(Product.class);
    crit.add(Restrictions.like("name","Mou%"));
    //OR: crit.add(Restrictions.ilike("name","Mou", MatchMode.START));
    List<Product> results = crit.list();
    ```
  - A complex Criteria query can be seen below:
    ```java
    Criteria crit = session.createCriteria(Product.class);
    Criterion priceLessThan = Restrictions.lt("price", 10.0);
    Criterion mouse = Restrictions.ilike("description", "mouse", MatchMode.ANYWHERE);
    LogicalExpression orExp = Restrictions.or(priceLessThan, mouse);
    crit.add(orExp);
    List<Product> results=crit.list();
    ```
  - If we wanted to create an OR expression with more than two different criteria (for example, “price > 25.0 OR name like Mou% OR description not like blocks%”), we would use an `org.hibernate.criterion.Disjunction` object to represent a disjunction.
    ```java
    Criteria crit = session.createCriteria(Product.class);
    Criterion priceLessThan = Restrictions.lt("price", 10.0);
    Criterion mouse = Restrictions.ilike("description", "mouse", MatchMode.ANYWHERE);
    Criterion browser = Restrictions.ilike("description", "browser", MatchMode.ANYWHERE);
    Disjunction disjunction = Restrictions.disjunction();
    disjunction.add(priceLessThan);
    disjunction.add(mouse);
    disjunction.add(browser);
    crit.add(disjunction);
    List<Product> results = crit.list();
    ```
  - The last type of restriction is the SQL restriction `sqlRestriction()`. This restriction allows you to directly specify SI.QL in the Criteria API.
    ```java
    Criteria crit = session.createCriteria(Product.class);
    crit.add(Restrictions.sqlRestriction("{alias}.description like 'Mou%'"));
    List<Product> results = crit.list();
    ```
  - Ordering can be added like: `crit.addOrder(Order.desc("name"));`
- **Projection and aggregates**
  - Instead of working with objects from the result set, you can treat the results from the result set as a set of rows and columns, also known as a projection of the data.
  - To use projections, start by getting the o`rg.hibernate.criterion.Projection` object you need from t`he org.hibernate.criterion.Projections` factory class.
  - After you get a `Projection` object, add it to your `Criteria` object with the `setProjection()` method.
    ```java
    Criteria crit = session.createCriteria(Product.class);
    crit.setProjection(Projections.rowCount());
    List<Long> results = crit.list();
    ```
    - The results list will contain one object, a Long that contains the results of executing the COUNT SQL statement.
  - Other aggregate functions available through the `Projections` factory class include the following::
  - `avg(String propertyName)`: Gives the average of a property’s value
  - `count(String propertyName)`: Counts the number of times a property occurs
  - `countDistinct(String propertyName)`: Counts the number of unique values the property contains
  - `max(String propertyName)`: Calculates the maximum value of the property values
  - `min(String propertyName)`: Calculates the minimum value of the property values
  - `sum(String propertyName)`: Calculates the sum total of the property values
    ```java
    Criteria crit = session.createCriteria(Product.class);
    ProjectionList projList = Projections.projectionList();
    projList.add(Projections.max("price"));
    projList.add(Projections.min("price"));
    projList.add(Projections.avg("price"));
    projList.add(Projections.countDistinct("description"));
    crit.setProjection(projList);
    List<Object[]> results = crit.list();
    ```
- **Query By Example (QBE)**
  - The `org.hibernate.criterion.Example` class contains the QBE functionality.
  - To use QBE, we first need to construct an `Example` object. Then we need to create an instance of the `Example` object, using the static `create()` method on the `Example` class. The `create()` method takes the `Example` object as its argument. You add the `Example` object to a `Criteria` object just like any other `Criterion` object.
    ```java
    Criteria crit = session.createCriteria(Supplier.class);
    Supplier supplier = new Supplier();
    supplier.setName("MegaInc");
    crit.add(Example.create(supplier));
    List<Supplier> results = crit.list();
    ```
  - When Hibernate translates our Example object into an SQL query, **all the properties on our Example objects get examined**.
  - Default is to ignore null-valued properties. --> Hence if you used primitives you **must** tell hibernate to ignore these while searching (since these have non-null values.)
    - `excludeZeros`
    - `excludeProperty()`
    - `excludeNothing()` - compare for null values and zeroes exactly as they appear in the `Example` object.
      ```java
      Criteria prdCrit = session.createCriteria(Product.class);
      Product product = new Product();
      product.setName("M%");
      Example prdExample = Example.create(product);
      prdExample.excludeProperty("price");
      prdExample.enableLike();
      Criteria suppCrit = prdCrit.createCriteria("supplier");
      Supplier supplier = new Supplier();
      supplier.setName("SuperCorp");
      suppCrit.add(Example.create(supplier));
      prdCrit.add(prdExample);
      List<Product> results = prdCrit.list();
      ```

## Hibernate Caching

- **First Level Cache**
  - Hibernate First Level cache is **enabled by default**, there are no configurations needed for this.
  - Hibernate first level cache is **session specific**. --> Within the same session two `load(className.class, 1)` queries will return the same object even if table was changed between the two calls! Hibernate only "hits" the database on the first query, but not on the second! However if you open a new session and issue the query, it will hit the database and reflect the changes!
  - We can use session `session.evict(Object o)` method to remove a single object from the hibernate first level cache.
  - We can use session `clear()` method to clear the cache i.e delete all the objects from the cache.
  - We can use session `contains(Object o)` method to check if an object is present in the hibernate cache or not, if the object is found in cache, it returns true or else it returns false.
  - Since hibernate cache all the objects into session first level cache, while running bulk queries or batch updates it’s necessary to clear the cache at certain intervals to avoid memory issues.
- **Second Level Cache**
  - Once the session is closed, first-level cache is terminated as well. This is actually desirable, as it allows for concurrent sessions to work with entity instances in isolation from each other. On the other hand, second-level cache is **SessionFactory-scoped**, meaning it is shared by all sessions created with the same session factory.
  - Using the secondary cache the process is the following:
    - If an instance is already present in the first-level cache, it is returned from there
    - If an instance is not found in the first-level cache, and the corresponding instance state is cached in the second-level cache, then the data is fetched from there and an instance is assembled and returned
    - Otherwise, the necessary data are loaded from the database and an instance is assembled and returned
  - Hibernate only needs to be provided with an implementation of the org.hibernate.cache.spi.RegionFactory interface which encapsulates all details specific to actual cache providers.
    - We will look into Hibernate `EHCache` that is the most popular Hibernate Second Level Cache provider.
    - Make sure that the `EHCache` version matches your hibernate version!
  - **Caching strategies**
    - **Read Only**: This caching strategy should be used for persistent objects that will always read but never updated. It’s good for reading and caching application configuration and other static data that are never updated. This is the simplest strategy with best performance because there is no overload to check if the object is updated in database or not.
    - **Read Write**: It’s good for persistent objects that can be updated by the hibernate application. However if the data is updated either through backend or other applications, then there is no way hibernate will know about it and data might be stale. So while using this strategy, make sure you are using Hibernate API for updating the data.
    - **Nonrestricted Read Write**: If the application only occasionally needs to update data and strict transaction isolation is not required, a nonstrict-read-write cache might be appropriate.
    - **Transactional**: The transactional cache strategy provides support for fully transactional cache providers such as JBoss TreeCache. Such a cache can only be used in a JTA environment and you must specify hibernate.transaction.manager_lookup_class.
  - In order to enable second level caching we need to introduce the following dependencies:
    ```xml
    <!-- EHCache Core APIs -->
        <dependency>
          <groupId>net.sf.ehcache</groupId>
          <artifactId>ehcache-core</artifactId>
          <version>2.6.11</version>
        </dependency>
        <!-- Hibernate EHCache API -->
        <dependency>
          <groupId>org.hibernate</groupId>
          <artifactId>hibernate-ehcache</artifactId>
          <!-- Make sure to match hib. versioN! -->
          <version>4.3.5.Final</version>
        </dependency>
        <!-- EHCache uses slf4j for logging -->
        <dependency>
          <groupId>org.slf4j</groupId>
          <artifactId>slf4j-simple</artifactId>
          <version>1.7.5</version>
        </dependency>
    ```
- We also need to enable caching in our `hibernate.xml`
    ```xml
    <property name="hibernate.cache.region.factory_class">org.hibernate.cache.ehcache.EhCacheRegionFactory</property>
        <!-- For singleton factory -->
        <!-- <property name="hibernate.cache.region.factory_class">org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory</property>
        -->
        <!-- enable second level cache and query cache -->
        <property name="hibernate.cache.use_second_level_cache">true</property>
        <property name="hibernate.cache.use_query_cache">true</property>
        <property name="net.sf.ehcache.configurationResourceName">/myehcache.xml</property>
    ```
  - `hibernate.cache.region.factory_class` is used to define the Factory class for Second level caching, I am using `org.hibernate.cache.ehcache.EhCacheRegionFactory` for this. If you want the factory class to be singleton, you should use org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory` class.
  - `hibernate.cache.use_second_level_cache` is used to enable the second level cache.
  - `hibernate.cache.use_query_cache` is used to enable the query cache, without it HQL queries results will not be cached.
  - `net.sf.ehcache.configurationResourceName` is used to define the EHCache configuration file location, it’s an optional parameter and if it’s not present EHCache will try to locate `ehcache.xml` file in the application classpath.
- Finally we need to define the `ecache.xml`
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:noNamespaceSchemaLocation="http://ehcache.org/ehcache.xsd" updateCheck="true"
            monitoring="autodetect" dynamicConfig="true">

        <diskStore path="java.io.tmpdir/ehcache" />

        <defaultCache maxEntriesLocalHeap="10000" eternal="false"
                      timeToIdleSeconds="120" timeToLiveSeconds="120" diskSpoolBufferSizeMB="30"
                      maxEntriesLocalDisk="10000000" diskExpiryThreadIntervalSeconds="120"
                      memoryStoreEvictionPolicy="LRU" statistics="true">
            <persistence strategy="localTempSwap" />
        </defaultCache>

        <cache name="employee" maxEntriesLocalHeap="10000" eternal="false"
              timeToIdleSeconds="5" timeToLiveSeconds="10">
            <persistence strategy="localTempSwap" />
        </cache>

        <cache name="org.hibernate.cache.internal.StandardQueryCache"
              maxEntriesLocalHeap="5" eternal="false" timeToLiveSeconds="120">
            <persistence strategy="localTempSwap" />
        </cache>

        <cache name="org.hibernate.cache.spi.UpdateTimestampsCache"
              maxEntriesLocalHeap="5000" eternal="true">
            <persistence strategy="localTempSwap" />
        </cache>
    </ehcache>
    ```
  - `diskStore`: EHCache stores data into memory but when it starts overflowing, it start writing data into file system. We use this property to define the location where EHCache will write the overflown data.
  - `defaultCache`: It’s a mandatory configuration, it is used when an Object need to be cached and there are no caching regions defined for that.
  - `cache name=”employee”`: We use cache element to define the region and it’s configurations. We can define multiple regions and their properties, while defining model beans cache properties, we can also define region with caching strategies. The cache properties are easy to understand and clear with the name.
  - Cache regions `org.hibernate.cache.internal.StandardQueryCache` and `org.hibernate.cache.spi.UpdateTimestampsCache` are defined because EHCache was giving warning to that.
- Lastly we need to annotate our classes:
  - `@Cacheable`
  - `@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)`
  - Hibernate will use a separate cache region to store state of instances for that class. The region name is the fully qualified class name. For example, Foo instances are stored in a cache name `org.baeldung.persistence.model.Foo` in Ehcache.
- Collections are not cached by default, and we need to explicitly mark them as cacheable.
    ```java
        @Cacheable
        @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
        @OneToMany
        private Collection<Bar> bars;
    ```

## Batch Updates

- **Batching Statements**
  - Sending multiple statements in a single request reduces the number of database roundtrips, therefore decreasing transaction response time.
  - For executing static SQL statements, JDBC defines the ``Statement`` interface and batching multiple DML statements is as straightforward as the following code snippet:
    ```java
    statement.addBatch(
      "INSERT INTO post (title, version, id) " +
      "VALUES ('Post no. 1', 0, 1)");
    statement.addBatch(
      "INSERT INTO post_comment (post_id, review, version, id) " +
      "VALUES (1, 'Post comment 1.1', 0, 1)");
    int[] updateCounts = statement.executeBatch();
    ```
  - **Oracle:** For ``Statement`` and ``CallableStatement``, the Oracle JDBC Driver doesn’t actually support batching For anything but ``PreparedStatement``, the driver ignores batching, and each statement is executed separately.
  - **MySQL** Although it implements the JDBC specification, by default, the MySQL JDBC driver doesn’t send the batched statements in a single request. For this purpose, the JDBC driver defines the ``rewriteBatchedStatements`` connection property, so that statements get rewritten into a single String buffer. In order to fetch the auto-generated row keys, the batch must contain insert statements only.
    ```java
    String myConnectionString =
          "jdbc:mysql://localhost:3307/mydb?" +
          "useUnicode=true&characterEncoding=UTF-8";
    try {
        Connection con = DriverManager.getConnection(myConnectionString,"root", "whatever");
        PreparedStatement ps = con.prepareStatement("INSERT INTO jdbc (`name`) VALUES (?)");
        for (int i = 1; i <= 5; i++) {
            ps.setString(1, String.format("Line %d: Lorem ....", i));
            ps.addBatch();
        }
        ps.executeBatch();
    }
    ```
    - This will send **individual** statements.
    - However, if we change the connection string to include ``rewriteBatchedStatements=true``, then JDBC will send one or more multi-row INSERT statements.
    ```java
    String myConnectionString =
            "jdbc:mysql://localhost:3307/mydb?" +
            "useUnicode=true&characterEncoding=UTF-8" +
            "&rewriteBatchedStatements=true";
    ```
    - Such statement rewriting can improve performance in a **0-25%** range!
- **Batching PreparedStatements**
  - **Basics**
    - To address the vulnerabilities of JDBC `Statements` (SQL Injection etc.) JDBC offers the ``PreparedStatement`` interface for binding parameters in a safely manner.
        ```java
        PreparedStatement postStatement = connection.prepareStatement(
        "INSERT INTO Post (title, version, id) " +
        "VALUES (?, ?, ?)");
        postStatement.setString(1, String.format("Post no. %1$d", 1));
        postStatement.setInt(2, 0);
        postStatement.setLong(3, 1);
        postStatement.addBatch();
        postStatement.setString(1, String.format("Post no. %1$d", 2));
        postStatement.setInt(2, 0);
        postStatement.setLong(3, 2);
        postStatement.addBatch();
        int[] updateCounts = postStatement.executeBatch();
        ```
    - The **batch size** as a variable here has to be set correctly. From the book's diagramms it seems that in the "sample measurements" **batch size of ca. 30 and above** were optimal.
    - Compared to the previous ``Statement`` batch insert results, it’s clear that, for the same data load, the ``PreparedStatement`` use case performs just better.
  - **Choosing the right batch size**
    - As a rule of thumb you should always measure the performance improvement for various batch sizes. In practice, **a relatively low value (between 10 and 30) is usually a good choice**.
  - **Bulk operations**
    - For example: `UPDATE post SET version = version + 1;`
    - The bulk alternative is one order of magnitude faster than batching, but, even so, batch updates are more flexible since each row can take a different update logic. Batch updates can also prevent lost updates if the data access logic employs an optimistic locking mechanism.
    - **Note**: Processing too much data in a single transaction can degrade application performance, especially in a highly concurrent environment. In case the bulk updated records conflict with other concurrent transactions, then either the bulk update transaction might have to wait for some row-level locks to be released or other transactions might wait for the bulk updated rows to be committed.
- **Retrieving auto-generated keys**
  - To retrieve the newly created row identifier, the JDBC ``PreparedStatement`` must be instructed to return the auto-generated keys.
    ```java
    PreparedStatement postStatement = connection.prepareStatement(
      "INSERT INTO post (title, version) VALUES (?, ?)",
      Statement.RETURN_GENERATED_KEYS
    );
    ```
  - Many database engines use sequence number generation optimizations to lower the sequence call execution as much as possible. If the number of inserted records is relatively low, then the sequence call overhead (extra database roundtrips) is insignificant.

## Statement Caching

- **Statement lifecycle**
  - **Parser**
    - The Parser checks the SQL statement and ensures its validity.
    - During parsing, the SQL statement is transformed into a database internal representation, called the *syntax tree* (also known as parse tree or query tree).
  - **Optimizer**
    - For a given syntax tree, the database must decide the most efficient data fetching algorithm.
    - The list of access path, chosen by the Optimizer, is assembled into an execution plan.
    - Execution plans are often cached, however there are many challenges.
  - **Execution plan visualization**
    - In MySQL the plan is displayed using `EXPLAIN` or E`XPLAIN EXTENDED`:
      - `EXPLAIN EXTENDED SELECT COUNT(*) FROM post;`
  - **Executor**
    - From the Optimizer, the execution plan goes to the Executor where it is used to fetch the associated data and build the result set.
    ![Statement Lifecycle](/Java/Hibernate/res/2_StatementLifecycle.PNG)
- **Caching performance gain**
  - According to the Book's measurements the caching has increased the performance by **20-55%** (Statements per Minute).
- **Server-side statement caching**
  - **Intro**
    - The statement string value is used as input to a hashing function, and the resulting value becomes the execution plan cache entry key. If the statement string value changes from one execution to the other, the database cannot reuse an already generated execution plan. For this purpose, **dynamic-generated JDBC Statement(s) are not suitable for reusing execution plans**.
    - **Server-side prepared statements** allow the data access logic to reuse the same execution plan for multiple executions. A `PreparedStatement` is always associated with a single SQL statement, and bind parameters are used to vary the runtime execution context. Because `PreparedStatement`(s) take the SQL query at creation time, the database can precompile the associated SQL statement prior to executing it.
    - When it comes to executing the `PreparedStatement`, the driver sends the actual parameter values, and the database can jump to compiling and running the actual execution plan.
    - Because of index selectivity, in the absence of the actual bind parameter values, the Optimizer cannot compile the syntax tree into an execution plan. For prepared statements, the execution plan can either be compiled on every execution or it can be cached and reused. Recompiling the plan can generate the best data access paths for any given bind variable set while paying the price of additional database resources usage. Reusing a plan can spare database resources, but it might not be suitable for every parameter value combination.
  - **Bind-sensitive execution plans**
    - Imagine that we have 100'000 tickets out of which 95% has the status "DONE", 1000 are "TODO" and 4000 are "FAILED".
    - If you search for the "DONE" tickes: `EXPLAIN SELECT * FROM task WHERE status = 'DONE' LIMIT 100;`
      - You see that the the optimizer tends to prefer **sequential scans** over index lookups for high selectivity percentages, to reduce the total number of disk-access roundtrips (especially when data is scattered among multiple data blocks).
    - If you search for the "TODO" tickets: `SQL> EXPLAIN SELECT * FROM task WHERE status = 'TO_DO' LIMIT 100;`
      - You see that the optimizer selects index scanning!
    - So, the **execution plan depends on bind parameter** value selectivity.
  - **MYSQL**
    - MySQL doesn’t cache **execution plans** (don't confuse with query caching!), so every statement execution is optimized for the current bind parameter values, therefore avoiding data skew issues.
    - Because of some unresolved issues, since version 5.0.5, the MySQL JDBC driver only emulates server-side prepared statements. To switch to server-side prepared statements, both the `useServerPrepStmts` and the `cachePrepStmts` connection properties must be set to `true`.
- **Client side statement caching**
  - Not only the database side can benefit from caching statements, but also the JDBC driver can reuse already constructed statement objects. Advantages:
    - reducing client-side statement processing, which, in turn, lowers transaction response time
    - sparing application resources by recycling statement objects along with their associated database-specific metadata.
  - **MYSQL**
    - The client-side statement cache is configured using the following properties:
      - `cachePrepStmts` - enables the client-side statement cache as well as the server-side state- ment validity checking. By default, the statement cache is disabled.
      - `prepStmtCacheSize` - the number of statements cached for each database connection. The default cache size is 25.
      - `prepStmtCacheSqlLimit` - the maximum length of an SQL statement allowed to be cached. The default maximum value is 256.
    - These properties can be set both as connection parameters or at `DataSource` level:
      - `((MysqlDataSource) dataSource).setCachePrepStmts(true);`
      - `((MysqlDataSource) dataSource).setPreparedStatementCacheSize(cacheSize);`
      - `(MysqlDataSource) dataSource).setPreparedStatementCacheSqlLimit(maxLength);`

## ResultSet Fetching

- **Intro**
  - The SQL Standard defines both the result set and the cursor descriptor through the following properties:
    - scrollability (the direction in which the result set can be iterated)
    - sensitivity (when should data be fetched)
    - updatability (available for cursors, it allows the client to modify records while traversing the
    result set)
    - holdability (the result set scope in regard to a transaction lifecycle).
  - **JDBC ResultSet properties**
    - `TYPE_FORWARD_ONLY` - The result set can only be iterated from the first to the last element. This is the default scrollability value.
    - `TYPE_SCROLL_INSENSITIVE` - The result set takes a loading time snapshot which can be iterated both forward and backwards.
    - `TYPE_SCROLL_SENSITIVE` - The result set is fetched on demand, while being iterated without any direction restriction.
    - `CONCUR_READ_ONLY` - The result set is just a static data projection which doesn’t allow row-level manipulation. This is the default changeability value.
    - `CONCUR_UPDATABLE` - The cursor position can be used to update or delete records, or even insert a new one.
    - `CLOSE_CURSORS_AT_COMMIT` - The result set is closed when the current transaction ends.
    - `HOLD_CURSORS_OVER_COMMIT` - The result set remains open even after the current transaction is committed.
- **ResultSet scrollability**
  - The JDBC ResultSet can be traversed using an application-level cursor. The fetching mechanism is therefore hidden behind an iterator API which decouples the application code from the data retrieval strategy. Some database drivers prefetch the whole result set on the client-side, while other implementations retrieve batches of data on a demand basis.
  - By default, the ResultSet uses a **forward-only** application-level cursor, which can be traversed only once, from the first position to last one. JDBC also offers scrollable cursors, therefore allowing the row-level pointer to be positioned freely.
  - The main difference between the two scrollable result sets lays in their **selectivity**.
    - An **insensitive** cursor **offers a static view** over the current result set, so the **data needs to be fetched entirely prior** to being iterated.
    - A **sensitive** cursor allows the **result set to be fetched dynamically**, so it can reflect concurrent changes.
  - **MYSQL**
    - **Only the insensitive scroll type is supported**, even when explicitly specifying a forward-only result set. Because MySQL doesn’t support database cursors, the driver retrieves the whole result set and caches it on the client-side.
- **ResultSet changeability**
  - By default, the result set is just a read-only view of the underlying data projection.
  - For web applications, requests should be as short as possible - an updatable result set is of little use, especially because holding it open (along with the underlying database connection) over multiple requests can really hurt application scalability.
  - **As a rule of thumb, if the current transaction doesn’t require updating selected records, the forward-only and read-only default result set type is the most efficient option.**
- **ResultSet holdability**
  - **MYSQL**
    - The default and the only supported holdability value is `HOLD_CURSORS_OVER_COMMIT`.
    - In a typical enterprise application, database connections are reused from one transaction to another, so **holding a result set after a transaction ends is risky**.
- **Fetching size**
  - The JDBC `ResultSet` acts as an application-level cursor, so whenever the statement is traversed, the result must be transferred from the database to the client. The transfer rate is controlled by the Statement fetch size. - `statement.setFetchSize(fetchSize);`
  - A custom fetch size gives the driver a hint as to the number of rows needed to be retrieved in a single database roundtrip. The default value of 0 leaves each database choose its own driver-specific fetching policy.
  - **MYSQL**
    - Because of the network protocol design consideration, fetching the whole result set is the most efficient data retrieval strategy. The only streaming option requires processing one row at a time, which, for large result sets, it implies many database roundtrips.
- **ResultSet size**
  - **Intro**
    - All too often, unfortunately, **especially with the widespread of ORM tools**, the statement might select more data than necessary. This issue might be caused by selecting too many rows or too many columns, which are later discarded in the data access or the business layer.
    - Tables tend to grow in size (especially if the application gains more traction), and, with time, a moderate result set might easily turn into a performance bottleneck. This issues are often discovered in production systems, long after the application code was shipped.
    - A user interface can accommodate just as much info as the view allows displaying. For this reason, it’s inefficient to fetch a result set entirely if it cannot fit into the user interface. Pagination or dynamic scrolling are common ways of addressing this issue, and partitioning data sets becomes unavoidable. -> Limiting queries can therefore ensure predictable response times and database resource utilization.
  - **Too many rows**
    - Include the row restriction clause in the SQL statement. This way, the Optimizer can better come up with an execution plan that’s optimal for the current result set size (like selecting an index scan instead of a full scan).
      - `SELECT pc.id FROM post_comment LIMIT ?`
    - Configure a maximum row count at the JDBC Statement level. Ideally, the driver can adjust the statement to include the equivalent result set size restriction as an SQL clause, but, most often, it only hints the database engine to use a database cursor instead.
      - `statement.setMaxRows(maxRows);` - Unlike the SQL construct, the JDBC alternative is portable across all driver implementations.
      - **MYSQL:** The `maxRows` attribute is not sent to the database server, so neither the Optimizer nor the Extractor can benefit from this hint. While the JDBC driver would normally fetch all rows, by placing an upper bound on the result set size, the client-side can spare some networking overhead.
    - In practice (Book's tests) SQL level restriction proves to be the optimal strategy for limiting a result set.
  - **Too many columns**
    - This situation is more prevalent among ORM tools, as for populating entities entirely, all columns are needed to be selected. This might pass unnoticed when selecting just a few entities, but, for large result sets, this can turn into a noticeable performance issue.
    - **No Solution?!**

## Implementing Application Server Side Connection Pooling using JNDI

### SQL and TomCat Setup

- **SQL Setup**
    ```sql
    CREATE TABLE `Employee` (
      `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
      `name` varchar(20) DEFAULT NULL,
      `role` varchar(20) DEFAULT NULL,
      `insert_time` datetime DEFAULT NULL,
      PRIMARY KEY (`id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;

    INSERT INTO `Employee` (`id`, `name`, `role`, `insert_time`)
    VALUES
      (3, 'Puppy', 'CEO', now());
    INSERT INTO `Employee` (`id`, `name`, `role`, `insert_time`)
    VALUES
      (14, 'David', 'Developer', now());
    ```
- **TomCat - server.xml in GlobalNamingResources section**
    ```xml
    <Resource name="jdbc/testDatasource"
              global="jdbc/testDatasource"
              auth="Container"
              factory="com.zaxxer.hikari.HikariJNDIFactory"
              type="javax.sql.DataSource"
              driverClassName="com.mysql.jdbc.Driver"
              jdbcUrl="jdbc:mysql://localhost:3306/hibernate?verifyServerCertificate=false&amp;useSSL=true"
              username="root"
              password="master"
              minimumIdle="8" maximumPoolSize="15" connectionTimeout="300000" maxLifetime="1800000" />
    ```
- **TomCat Context.xml ->**
    ```xml
    <ResourceLink name="jdbc/testDatasource"
                global="jdbc/testDatasource"
                auth="Container"
                type="javax.sql.DataSource" />
    ```
- **TomCat jars**
  - You need to manually copy the `HikariCP-3.1.0.jar` and `mysql-connector-java-5.1.45.jar` to the lib folder on the app server!

### Maven Setup

- **Maven Config**
    ```xml
    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
      <modelVersion>4.0.0</modelVersion>
      <groupId>HibernateDataSource</groupId>
      <artifactId>HibernateDataSource</artifactId>
      <version>0.0.1-SNAPSHOT</version>
      <packaging>war</packaging>

      <dependencies>
        <dependency>
          <groupId>org.hibernate</groupId>
          <artifactId>hibernate-core</artifactId>
          <version>5.2.17.Final</version>
        </dependency>
        <dependency>
          <groupId>org.slf4j</groupId>
          <artifactId>slf4j-simple</artifactId>
        </dependency>

        <dependency>
          <groupId>mysql</groupId>
          <artifactId>mysql-connector-java</artifactId>
          <version>5.0.5</version>
          <scope>provided</scope>
        </dependency>

      </dependencies>
      <build>
        <plugins>
          <plugin>
            <artifactId>maven-war-plugin</artifactId>
            <version>2.3</version>
            <configuration>
              <warSourceDirectory>WebContent</warSourceDirectory>
              <failOnMissingWebXml>false</failOnMissingWebXml>
            </configuration>
          </plugin>
          <plugin>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.1</version>
            <configuration>
              <source>1.7</source>
              <target>1.7</target>
            </configuration>
          </plugin>
        </plugins>
        <finalName>${project.artifactId}</finalName>
      </build>
    </project>
    ```
- **Hibernate Config**
    ```xml
      <?xml version="1.0" encoding="UTF-8"?>
      <!DOCTYPE hibernate-configuration PUBLIC
          "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
          "http://hibernate.org/dtd/hibernate-configuration-3.0.dtd">
      <hibernate-configuration>
          <session-factory>
              <property name="hibernate.connection.driver_class">com.mysql.jdbc.Driver</property>
              <property name="hibernate.dialect">org.hibernate.dialect.MySQLDialect</property>
              <property name="hibernate.connection.datasource">java:comp/env/jdbc/testDatasource</property>
              <property name="hibernate.current_session_context_class">thread</property>

              <!-- Mapping with model class containing annotations -->
          <mapping class="com.tamasne.servlet.hibernate.model.Employee"/>
          </session-factory>
      </hibernate-configuration>
    ```

- **Entity**
    ```java
    @Entity
    @Data
    @Table(name="Employee",
        uniqueConstraints={@UniqueConstraint(columnNames={"ID"})})
    public class Employee {

      @Id
      @GeneratedValue(strategy=GenerationType.IDENTITY)
      @Column(name="ID", nullable=false, unique=true, length=11)
      private int id;

      @Column(name="NAME", length=20, nullable=true)
      private String name;

      @Column(name="ROLE", length=20, nullable=true)
      private String role;

      @Column(name="insert_time", nullable=true)
      private Date insertTime;
    }
    ```

- **listener/HibernateSessionFactoryListener.java**
    ```java
    @WebListener
    public class HibernateSessionFactoryListener implements ServletContextListener {

      public final Logger logger = Logger.getLogger(HibernateSessionFactoryListener.class);

        public void contextDestroyed(ServletContextEvent servletContextEvent) {
          SessionFactory sessionFactory = (SessionFactory) servletContextEvent.getServletContext().getAttribute("SessionFactory");
          if(sessionFactory != null && !sessionFactory.isClosed()){
            logger.info("Closing sessionFactory");
            sessionFactory.close();
          }
          logger.info("Released Hibernate sessionFactory resource");
        }

        public void contextInitialized(ServletContextEvent servletContextEvent) {
          Configuration configuration = new Configuration();
          configuration.configure("hibernate.cfg.xml");
          configuration.addAnnotatedClass(Employee.class);
          logger.info("Hibernate Configuration created successfully");

          ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
          logger.info("ServiceRegistry created successfully");
          SessionFactory sessionFactory = configuration
            .buildSessionFactory(serviceRegistry);
          logger.info("SessionFactory created successfully");

          servletContextEvent.getServletContext().setAttribute("SessionFactory", sessionFactory);
          logger.info("Hibernate SessionFactory Configured successfully");
        }

    }
    ```

- **And the Servlet Definition Itself**
    ```java
    @WebServlet("/GetEmployeeByID")
    public class GetEmployeeByID extends HttpServlet {
      private static final long serialVersionUID = 1L;
      public final Logger logger = Logger.getLogger(GetEmployeeByID.class);
      protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int empId = Integer.parseInt(request.getParameter("empId"));
        logger.info("Request Param empId="+empId);
        SessionFactory sessionFactory = (SessionFactory) request.getServletContext().getAttribute("SessionFactory");
        Session session = sessionFactory.getCurrentSession();
        Transaction tx = session.beginTransaction();
        Employee emp = (Employee) session.get(Employee.class, empId);
        tx.commit();
        PrintWriter out = response.getWriter();
            response.setContentType("text/html");
            if(emp != null){
            out.print("<html><body><h2>Employee Details</h2>");
            out.print("<table border=\"1\" cellspacing=10 cellpadding=5>");
            out.print("<th>Employee ID</th>");
            out.print("<th>Employee Name</th>");
            out.print("<th>Employee Role</th>");
                out.print("<tr>");
                out.print("<td>" + empId + "</td>");
                out.print("<td>" + emp.getName() + "</td>");
                out.print("<td>" + emp.getRole() + "</td>");
                out.print("</tr>");
            out.print("</table></body><br/>");
            out.print("</html>");
            }else{
              out.print("<html><body><h2>No Employee Found with ID="+empId+"</h2></body></html>");
            }
      }
    }
    ```
  - Unfortunately for some reason I had to add manually the `configuration.addAnnotatedClass(Employee.class);` to make it work. (Config file was not read.) If everything done correclty this should work.

## Interview Questions

- **What is Hibernate Framework?**
  - Object-relational mapping or ORM is the programming technique to map application domain model objects to the relational database tables. Hibernate is java based ORM tool that provides framework for mapping application domain objects to the relational database tables and vice versa.
  - Hibernate provides reference implementation of Java Persistence API, that makes it a great choice as ORM tool with benefits of loose coupling. We can use Hibernate persistence API for CRUD operations.
- **What is Java Persistence API (JPA)?**
  - Java Persistence API (JPA) provides specification for managing the relational data in applications.
  - JPA specifications is defined with annotations in javax.persistence package. Using JPA annotation helps us in writing implementation independent code.
- **What are the important benefits of using Hibernate Framework?**
  - Eliminate boiler plate code that coems with JDBC
  - XML/JPA annotation configuration
  - HQL
  - Open Source
  - Supports lazy initialization using proxy objects
  - Its cache improves perofrmance
- **Which are the most important interfaces if Hibarenate?**
  - `SessionFactory` (org.hibernate.SessionFactory): SessionFactory is an immutable thread-safe cache of compiled mappings for a single database. We need to initialize SessionFactory once and then we can cache and reuse it. SessionFactory instance is used to get the Session objects for database operations.
  - `Session` (org.hibernate.Session): Session is a single-threaded, short-lived object representing a conversation between the application and the persistent store. It wraps JDBC java.sql.Connection and works as a factory for org.hibernate.Transaction. We should open session only when it’s required and close it as soon as we are done using it. Session object is the interface between java application code and hibernate framework and provide methods for CRUD operations.
  - `Transaction` (org.hibernate.Transaction): Transaction is a single-threaded, short-lived object used by the application to specify atomic units of work. It abstracts the application from the underlying JDBC or JTA transaction. A org.hibernate.Session might span multiple org.hibernate.Transaction in some cases.
- **What is hibernate configuration file?**
  - Hibernate configuration file contains database specific configurations and used to initialize SessionFactory. We provide database credentials or JNDI resource information in the hibernate configuration xml file. Some other important parts of hibernate configuration file is Dialect information, so that hibernate knows the database type and mapping file or class details. (`hiberante.cfg.xmkl`)
- **What is hibernate mapping file?**
  - Hibernate mapping file is used to define the entity bean fields and database table column mappings. We know that JPA annotations can be used for mapping but sometimes XML mapping file comes handy when we are using third party classes and we can’t use annotations.
- **Name the most used JPA annotations!**
  - `@Entity`, `@Table`, `@Id`, `@Embedded`, `@Column`, `@GeneratedValue`, `@Cascade`, `@PrimaryKeyJoinColumn`, and the mappings.
- **What is Hibernate SessionFactory and how to configure it?**
  - SessionFactory is the factory class used to get the Session objects. SessionFactory is responsible to read the hibernate configuration parameters and connect to the database and provide Session objects. Usually an application has a single SessionFactory instance and threads servicing client requests obtain Session instances from this factory.
  - The internal state of a SessionFactory is immutable. Once it is created this internal state is set. This internal state includes all of the metadata about Object/Relational Mapping.
  - SessionFactory also provide methods to get the Class metadata and Statistics instance to get the stats of query executions, second level cache details etc.
- **Hibernate SessionFactory is thread safe?**
  - Internal state of SessionFactory is immutable, so it’s thread safe. Multiple threads can access it simultaneously to get Session instances.
- **What is Hibernate Session and how to get it?**
  - Hibernate Session is the interface between java application layer and hibernate. This is the core interface used to perform database operations. Lifecycle of a session is bound by the beginning and end of a transaction.
  - Session provide methods to perform create, read, update and delete operations for a persistent object. We can execute HQL queries, SQL native queries and create criteria using Session object.
- **Hibernate Session is thread safe?**
  - Hibernate Session object is not thread safe, every thread should get it’s own session instance and close it after it’s work is finished.
- **What is difference between openSession and getCurrentSession?**
  - Hibernate SessionFactory openSession() method always opens a new session, while the other returns the session bound to the context.
- **What is difference between Hibernate Session get() and load() method?**
  - get() loads the data as soon as it’s called whereas load() returns a proxy object and loads data only when it’s actually required, so load() is better because it support lazy loading.
  - Since load() throws exception when data is not found, we should use it only when we know data exists.
  - We should use get() when we want to make sure data exists in the database.
- **What is hibernate caching? Explain Hibernate first level cache?**
  - Hibernate first level cache is associated with the Session object. Hibernate first level cache is enabled by default and there is no way to disable it. However hibernate provides methods through which we can delete selected objects from the cache or clear the cache completely.
- **What are different states of an entity bean?**
  - Transient, Persistent, Detached
- **What is use of Hibernate Session merge() call?**
  - Hibernate merge can be used to update existing values, however this method create a copy from the passed entity object and return it. The returned object is part of persistent context and tracked for any changes, passed object is not tracked.
- **What is difference between Hibernate save(), saveOrUpdate() and persist() methods?**
  - Hibernate save can be used to save entity to database. Problem with save() is that it can be invoked without a transaction and if we have mapping entities, then only the primary object gets saved causing data inconsistencies. Also save returns the generated id immediately.
  - Hibernate persist is similar to save with transaction. I feel it’s better than save because we can’t use it outside the boundary of transaction, so all the object mappings are preserved. Also persist doesn’t return the generated id immediately, so data persistence happens when needed.
  - Hibernate saveOrUpdate results into insert or update queries based on the provided data. If the data is present in the database, update query is executed. We can use saveOrUpdate() without transaction also, but again you will face the issues with mapped objects not getting saved if session is not flushed.
- **What will happen if we don’t have no-args constructor in Entity bean?**
  - `HibernateException`
- **What is difference between sorted collection and ordered collection, which one is better?**
  - When we use (Java's) Collection API sorting algorithms to sort a collection, it’s called sorted list.
  - If we are using Hibernate framework to load collection data from database, we can use it’s Criteria API to use “order by” clause to get ordered list. Below code snippet shows you how to get it.
    - `List<Employee> empList = session.createCriteria(Employee.class).addOrder(Order.desc("id")).list();`
  - Ordered list is better than sorted list because the actual sorting is done at database level, that is fast and doesn’t cause memory issues.
- **What are the collection types in Hibernate?**
  - Bag, Set, List, Array, Map
- **Why we should not make Entity Class final?**
  - Hibernate use proxy classes for lazy loading of data, only when it’s needed. This is done by extending the entity bean, if the entity bean will be final then lazy loading will not be possible, hence low performance.
- **What is HQL and what are it’s benefits?**
  - Supports object oriened queries
- **What is Query Cache in Hibernate?**
  - Hibernate implements a cache region for queries resultset that integrates closely with the hibernate second-level cache.
  - This is an optional feature and requires additional steps in code. This is only useful for queries that are run frequently with the same parameters. First of all we need to configure below property in hibernate configuration file.
- **What is Named SQL Query?**
  - Hibernate provides Named Query that we can define at a central location (e.g. over a class definition using annotations) and use them anywhere in the code. We can created named queries for both HQL and Native SQL.
  - Hibernate Named Query syntax is checked when the hibernate session factory is created, thus making the application fail fast in case of any error in the named queries.
- **What is the benefit of Hibernate Criteria API?**
  - Hibernate provides Criteria API that is more object oriented for querying the database and getting results. We can’t use Criteria to run update or delete queries or any DDL statements. It’s only used to fetch the results from the database using more object oriented approach.
- **What is Hibernate Proxy and how it helps in lazy loading?**
  - Hibernate uses proxy object to support lazy loading. Basically when you load data from tables, hibernate doesn’t load all the mapped objects. As soon as you reference a child or lookup object via getter methods, if the linked entity is not in the session cache, then the proxy code will go to the database and load the linked object. It uses javassist to effectively and dynamically generate sub-classed implementations of your entity objects.
- **How transaction management works in Hibernate?**
  - Transaction management is very easy in hibernate because most of the operations are not permitted outside of a transaction. So after getting the session from SessionFactory, we can call session beginTransaction() to start the transaction. This method returns the Transaction reference that we can use later on to either commit or rollback the transaction.
- **What is cascading and what are different types of cascading?**
  - When we have relationship between entities, then we need to define how the different operations will affect the other entity. This is done by cascading and there are different types of it.
  - ALL, SAVE_UPDATE, DELETE, DETACH, MERGE, PERSIST, REFRESH, REMOVE, LOCK, REPLCIATE
- **How to use application server JNDI DataSource with Hibernate framework?**
  - For web applications, it’s always best to allow servlet container to manage the connection pool. That’s why we define JNDI resource for DataSource and we can use it in the web application. It’s very easy to use in Hibernate, all we need is to remove all the database specific properties and use below property to provide the JNDI DataSource name.
  - `<property name="hibernate.connection.datasource">java:comp/env/jdbc/MyLocalDB</property>`