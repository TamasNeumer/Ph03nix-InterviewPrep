# Beginning Hibernate

#### Intro

- Database <--> JDBC <--> [Mappings -> Hibernate <- Configuration] <--> ClientCode / POJOs
- Hibernate uses standard Java Database Connectivity (JDBC) database drivers to access the relational database.
- The traditional JDBC code contains loads of "boilerplate" with many try & catch blocs.  Hibernate provides cleaner resource management, which means that you do not have to worry about the actual database connections, nor do you have to have giant try/catch/finally blocks.
- In Hibernate parlance, this is called a mapping. Mappings can be provided either through Java annotations or through an XML mapping file.

#### Integrating and Configuring Hibernate

- **Hiberante Configuration**
  - For this example a locally executed MySQL is used. (`src/test/resource/ hibernate.cfg.xml`):
    ```xml
    <?xml version="1.0"?>
    <!DOCTYPE hibernate-configuration PUBLIC
            "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
            "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
    <hibernate-configuration>
        <session-factory>
            <!--  Database connection settings  -->
            <property name="connection.driver_class">com.mysql.jdbc.Driver</property>
            <property name="connection.url">jdbc:mysql://localhost:3306/hibernate</property>
            <property name="connection.username">root</property>
            <property name="connection.password">master</property>
            <property name="dialect">org.hibernate.dialect.MySQL57Dialect</property>
            <!--  Echo all executed SQL to stdout  -->
            <property name="show_sql">true</property>
            <!--  Drop and re-create the database schema on startup  -->
            <property name="hbm2ddl.auto">create-drop</property>
            <mapping class="chapter01.hibernate.Message"/>
        </session-factory>

    </hibernate-configuration>
    ```
  - Note that the server is configured with a "default" schema of "hibernate" and the server can be accessed via -u "root" and -p "master"
- **Getting into the Hibernate API**
  - Construct a `SessionFactory` and then use the `SessionFactory` to retrieve short-lived `Session` objects through which updates, or reads, are performed.
    ```java
    StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .configure()
                    .build();
    factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    /*...*/
    Message message = new Message("Hello, world");
    try (Session session = factory.openSession()) {
        Transaction tx = session.beginTransaction();
        session.persist(message);
        tx.commit();
    }
    ```
- **Connection pooling**
  - Creating these connections is very expensive.
  - Since JDBC connection management is so expensive, you can pool the connections, which can open connections ahead of time --> "Connection Pooling"
  - Hibernate is designed to use a connection pool by default, an internal implementation. However, Hibernate's built-in connection pooling isn't designed for production use. 
  - In Production "C3PO" is often used with Hibernate!
  - First, we need to add c3p0 and Hibernate's C3P0 connection provider as dependencies in the pom.xml.
    ```xml
    <dependencies>
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-c3p0</artifactId>
            <version>[5.0.0,5.9.9)</version>
        </dependency>
        <dependency>
            <groupId>com.mchange</groupId>
            <artifactId>c3p0</artifactId>
            <version>0.9.5.2</version>
        </dependency>
    </dependencies>
    ```
  - Next, we need to change the Hibernate configuration to tell it to use c3p0. To do this, all we need to do is add any c3p0 configuration property to `hibernate.cfg.xml`.
    - `<property name="c3p0.timeout">10</property>`
  - If you're using Hibernate in a Java EE context – in a web application, for example – then you'll want to configure Hibernate to use JNDI. JNDI connection pools are managed by the container (and thus controlled by the deployer), which is generally the “right way” to manage resources in a distributed environment.
- **Annotations used in the Java Class**
  - `@Entity` - The class is marked as an entity and hence it must have a no-argument constructor that is visible with at least protected scope.
  - `@Id` - defines a primary key (+not null) in the database. It is NOT auto-incremented by default!
  - `@GeneratedValue(strategy=GenerationType.AUTO)` - enables the `AUTO_INCREMENT` option on the given column.
  - `@Column(nullable = false)` - adds non-null constraint.
  - `@Column(unique = true)` - unique constraint on the column.

#### Building a Simple Application

- **Session and Persistance context**
  - Persistence context can be thought of as a container or a first-level cache for all the objects that you loaded or saved to a database during a session.
  - The session is a logical transaction, which boundaries are defined by your application’s business logic. When you work with the database through a persistence context, and all of your entity instances are attached to this context, you should always have a single instance of entity for every database record that you’ve interacted during the session with.
  - Any entity instance in your application appears in one of the three main states in relation to the Session persistence context:
    - **Transient** - this instance is not, and never was, attached to a `Session`; this instance has no corresponding rows in the database; it’s usually just a new object that you have created to save to the database
    - **Persistent** - this instance is associated with a unique `Session` object; upon flushing the `Session` to the database, this entity is guaranteed to have a corresponding consistent record in the database;
    - **Detached** - this instance was once attached to a `Session` (in a persistent state), but now it’s not; an instance enters this state if you evict it from the context, clear or close the `Session`, or put the instance through serialization/deserialization process.
    - **Removed** - A removed object is one that’s been marked for deletion in the current transaction. An object is changed to removed state when `Session.delete()` is called for that object reference.
      ![Flow](http://www.baeldung.com/wp-content/uploads/2016/07/2016-07-11_13-38-11-1024x551.png)
- **Transactions**
  - A transaction is a “bundled unit of work” for a database. Changes are committed as a whole, so that no other transaction can see them until the transaction completes.
  - If the transaction is abandoned – that is, `commit()` is not called explicitly – then the transaction’s changes are abandoned and the database is left unmodified.
  - Transactions can be aborted (“rolled back,” with the `Transaction.rollback()` method) such that any changes that have taken place as part of that transaction are discarded.
- **Ranking Object**
  - A ranking is submitted by an *observer*. Given a *subject* the *observer* estimates the subject's programming *skill* in a given language and gives a certain *score*.
  - While creating the class the followings are important to note:
    - `@Column` - Is used to specify the mapped column for a persistent property or field.
    - `@ManyToOne`, `@OneToMany` - many to one (or one to many) relationship from the current class towards another class.
      ```java
      @ManyToOne
      Person subject;
      @ManyToOne
      Person observer;
      @ManyToOne
      Skill skill;
      @Column
      Integer ranking;
      ```
- **A simple query**
  - Select data from the table created from the `Person` entity (which may or may not have a table name of “person”), aliased to “p,” and limited to objects whose “name” attribute is equal to a named parameter (helpfully called “name”). It also specifies the reference type of the query (with `Person.class`) to cut down on typecasting and potential errors of incorrect return types.
  - If we have five People with that name in our database, an exception will be thrown; we could fix this by using `query.setMaxResults(1)`, and returning the first (and only) entry in `query.list()`, but the right way to fix it is to figure out how to be very specific in returning the right Person. (-> i.e. searching them in a unique way). If no result is found, a signal value – `null` – will be returned.
    ```java
    private Person findPerson(Session session, String name) {
        Query<Person> query = session.createQuery("from Person p where p.name=:name",
                Person.class);
        query.setParameter("name", name);
        Person person = query.uniqueResult();
        return person;
    }
    ```
  - What happens if we already have multiple rankings saved for a given Person and we want to calculate the average? Things to note:
    - Hibernate takes care of writing all of the SQL for us, and we can use the objects “naturally.” --> We are **not** using JOINs in the query!
    - The data is not ready until we actually commit the query!
    ```java
    /**...**/
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
- **Updating data**
  - Imagine that we want to change an attribute of an object, that is already persisted to the database. We fetch the object with a query and then:
    ```java
    Ranking ranking = query.uniqueResult();
    assertNotNull(ranking, "Could not find matching ranking");
    ranking.setRanking(9);
    tx.commit();
    ```
  - Hibernate watches the data model, and when something is changed, it automatically updates the database to reflect the changes. It generally does this by using a proxied object. When you change the data values in the object, the proxy records the change so that the transaction knows to write the data to the database on transaction commit.
  - The transaction commits the update to the database so that other sessions.
- **Removing data**
  - Find ranking -> which will give us a Ranking in *persistent* state. Then we remove it via the session and commit the change.
    ```java
    session.delete(ranking);
    tx.commit();
    ```
