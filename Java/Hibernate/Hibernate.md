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
        ```xml
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

## Annotations

#### Basic Annotations

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

#### ID Generation Strategies

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
- **Composite keys**