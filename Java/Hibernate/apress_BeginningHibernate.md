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
            <!-- Enable quoting vars! -->
            <property name="hibernate.globally_quoted_identifiers">true</property>
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
    - `SessionFactory.openSession()` always opens a new session that you have to close once you are done with the operations. `SessionFactory.getCurrentSession()` returns a session bound to a context - you don't need to close this.
  - Any entity instance in your application appears in one of the three main states in relation to the Session persistence context:
    - **Transient**s- this instance is not, and never was, attached to a `Session`; this instance has no corresponding rows in the database; it’s usually just a new object that you have created to save to the database
    - **Persistent** - this instance is associated with a unique `Session` object; upon flushing the `Session` to the database, this entity is guaranteed to have a corresponding consistent record in the database;
    - **Detached** - this instance was once attached to a `Session` (in a persistent state), but now it’s not; an instance enters this state if you `evict()` it from the context, clear or close the `Session`, or put the instance through serialization/deserialization process. Detached objects have a representation in the database, but changes to the object will not be reflected in the database, and vice versa. One reason you might consider doing this would be to read an object out of the database, modify the properties of the object in memory, and then store the results someplace other than your database. (Alternative would be a deep copy.)
    - **Removed** - A removed object is one that’s been marked for deletion in the current transaction. An object is changed to removed state when `Session.delete()` is called for that object reference. (=> Hibernate will remove the object from the DB on the next flush.)
      ![Flow](http://www.baeldung.com/wp-content/uploads/2016/07/2016-07-11_13-38-11-1024x551.png)
  - When the entity instance is in the persistent state, all changes that you make to the mapped fields of this instance will be applied to the corresponding database records and fields upon flushing the `Session`. The persistent instance can be thought of as “online”, whereas the detached instance has gone “offline” and is not monitored for changes. --> This means that when you change fields of a persistent object, you don’t have to call `save`, `update` or any of those methods to get these changes to the database: all you need is to commit the transaction, or flush or close the session, when you’re done with it.
- **Transactions**
  - A transaction is a “bundled unit of work” for a database. Changes are committed as a whole, so that no other transaction can see them until the transaction completes.
  - If the transaction is abandoned – that is, `commit()` is not called explicitly – then the transaction’s changes are abandoned and the database is left unmodified.
  - Transactions can be aborted (“rolled back,” with the `Transaction.rollback()` method) such that any changes that have taken place as part of that transaction are discarded.
  - In Hiberante all your persistance (=saving) operations **must** be declared withing a given transaction! -->  Without transaction you can **only retrieve** object from database
- **Persistance operations**
  - It is important to understand from the beginning that all of the methods (`persist`, `save`, `update`, `merge`, `saveOrUpdate`) do **not** immediately result in the corresponding SQL `UPDATE` or `INSERT` statements. The actual saving of data to the database occurs on committing the transaction or flushing the `Session`. The mentioned methods basically manage the state of entity instances by transitioning them between different states along the lifecycle.
  - `session.persist(Object o)`
    - The persist method is intended for adding a new entity instance to the persistence context, i.e. transitioning an instance from transient to persistent state.
    - If you try to persist a detached instance, the implementation is bound to throw an exception.
  - `session.save(Object o)`
    - Its purpose is basically the same as persist, but it has different implementation details. 
    - The call of save on a detached instance creates a new persistent instance and assigns it a new identifier, which results in a duplicate record in a database upon committing or flushing.
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
  - `session.update(Object o)`
    - it acts upon passed object (its return type is void); the update method transitions the passed object from detached to persistent state;
    - this method throws an exception if you pass it a transient entity.
  - `saveOrUpdate(Object o)`
    - The main difference of saveOrUpdate method is that it does not throw exception when applied to a transient instance; instead, it makes this transient instance persistent.
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
    - Note that opening a transaction and closing it is kind of "redundant" here (?!) as we get the same results without it.
      - When dirty reads are not acceptable, then a transaction for the select is appropriate. Most of the time, with that kind of scenario, the select will be done in conjunction with some data modification that requires full consistency for a point in time.
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

#### Identifiers, Relations, Database operations

- **Identifiers**
  - `GeneratedValue`, which tells Hibernate that it is responsible for assigning and maintaining the identifier.
  - There are five different generation possibilities: identity, sequence, table, auto, and none.
    - In *Auto* generation the persistence provider will determine values based on the type of the primary key attribute.
    - *Identity* generation relies on a natural table sequencing. This type of generation relies on the IdentityGenerator which expects values generated by an identity column in the database, meaning they are auto-incremented.
    - The *sequence* mechanism depends on the database’s ability to create table sequences (which tends to limit it to PostgreSQL, Oracle, and a few others). This generator uses sequences if they’re supported by our database, and switches to table generation if they aren’t.
    - The *TableGenerator* uses an underlying database table that holds segments of identifier generation values.
      ```java
      @Id
      @GeneratedValue(strategy = GenerationType.TABLE, 
        generator = "table-generator")
      @TableGenerator(name = "table-generator", 
        table = "dep_ids", 
        pkColumnName = "seq_id", 
        valueColumnName = "seq_value")
      private long depId;
      ```
    - *None* relies on manual assignment of an identifier.
- **MappedBy Annotation**
  - `mappedBy` signals hibernate that the key for the (1-n, n-1) relationship is on the other side. E.g.: `@OneToOne(mappedBy = "email")` here you tell Hibernate that the reference to the current (Email) class is stored in a class variable named `email` in the other (Message) class. If you were to set `message.email = emailInstance;` and persist this, then Hibernate would automatically assign `this.message = message` too.
  - In conclusion `mappedBy` defines the "owner" in a given relation.
- **Updating tables II**
  - If you save the given entity hibernate assigns the id immediately:
    ```java
    obj = new SimpleObject();
    obj.setKey("sl");
    obj.setValue(10L);

    session.save(obj);
    assertNotNull(obj.getId());
    // we should have an id now, set by Session.save()
    id = obj.getId();
    ```
  - If you were to save the same object twice however, then you would end up creating two rows in the database!
- **Retrieving/Saving objects within same/different session**
  - Loading
    - Requesting a persistent object again from the same Hibernate session returns the same Java instance of a class, which means that you can compare the objects using the standard Java == equality syntax. If, however, you request a persistent object from more than one Hibernate session, Hibernate will provide distinct instances from each session, and the == operator will return `false` if you compare these object instances.
  - Saving
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
- **Loading Entities**
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
    - NONE: Uses no row-level locking, and uses a cached object if available; this is the Hibernate default.
    - READ: Prevents other SELECT queries from reading data that is in the middle of a transaction (and thus possibly invalid) until it is committed.
    - UPGRADE: Uses the SELECT FOR UPDATE SQL syntax to lock the data until the transaction is finished.
    - UPGRADE_NOWAIT: Uses the NOWAIT keyword (for Oracle), which returns an error immediately if there is another thread using that row; otherwise this is similar to UPGRADE.
    - UPGRADE_SKIPLOCKED: Skips locks for rows already locked by other updates, but otherwise this is similar to UPGRADE.
    - OPTIMISTIC: This mode assumes that updates will not experience contention. The entity’s contents will be verified near the transaction’s end.
    - OPTIMISTIC_FORCE_INCREMENT: This is like OPTIMISTIC, except it forces the version of the object to be incremented near the transaction’s end.
    - PESSIMISTIC_READ and PESSIMISTIC_WRITE: Both of these obtain a lock immediately on row access.
    - PESSIMISTIC_FORCE_INCREMENT: This obtains the lock immediately on row access, and also immediately updates the entity version.
  - You should not use a `load()` method unless you are sure that the object exists. If you are not certain, then use one of the `get()` methods. The `load()` methods will throw an exception if the unique ID is not found in the database, whereas the `get()` methods will merely return a null reference.
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
- **Merging entities**
  - Merging is performed when you desire to have a detached entity changed to persistent state again, with the detached entity’s changes migrated to (or overriding) the database. Merging is the inverse of `refresh()`, which overrides the detached entity’s values with the values from the database.
    ```java
    Object merge(Object object)
    Object merge(String entityName, Object object)

    /*Usage*/
    Transaction tx = session.beginTransaction();
    session.merge(so);
    tx.commit();
    ```
- **Refreshing entities**
  - Hibernate provides a mechanism to refresh persistent objects from their database representation. These methods will reload the properties of the object from the database, overwriting them; thus, as stated, `refresh()` is the inverse of `merge()`.
    ```java
    public void refresh(Object object)
    public void refresh(Object object, LockMode lockMode)
    ```
- **Updating entries**
  - Hibernate automatically persists changes made to persistent objects into the database.
  - The `void flush()` method forces Hibernate to flush the session.
  - You can determine if the session is dirty with the `boolean isDirty()` method.
  - You can also instruct Hibernate to use a flushing mode for the session with the `void setHibernateFlushMode(FlushMode flushMode)` method. The `FlushMode getHibernateFlushMode()` method returns the flush mode for the current session.
  - Flush modes:
    - ALWAYS: Every query flushes the session before the query is executed. This is going to be very slow.
    - AUTO: Hibernate manages the query flushing to guarantee that the data returned by a query is up to date.
    - COMMIT: Hibernate flushes the session on transaction commits.
    - MANUAL: Your application needs to manage the session flushing with the flush() method. Hibernate never flushes the session itself.
- **Deleting entries**
  - `void delete(Object object)`
  - This method takes a persistent object as an argument. The argument can also be a transient object with the identifier set to the ID of the object that needs to be erased.
  - If you set the cascade attribute to `delete` or `all`, the delete will be cascaded to all of the associated objects.
  - Hibernate also supports **bulk deletes**, where your application executes a DELETE HQL statement against the database. These are very useful for deleting more than one object at a time because each object does not need to be loaded into memory just to be deleted:
    - `session.createQuery("delete from User").executeUpdate();`
  - Bulk deletes **do not cause cascade** operations to be carried out. If cascade behavior is needed, you will need to carry out the appropriate deletions yourself, or use the session’s `delete()` method.
- **Cascading operations**
  - The cascade types supported by the Java Persistence Architecture are as follows:
    - PERSIST, MERGE, REFRESH, REMOVE, DETACH, ALL
  - It’s worth pointing out that Hibernate has its own configuration options for cascading, which represent a superset of these.
    - `CascadeType.PERSIST` means that `save()` or  operations cascade to related entities.
    - `CascadeType.MERGE` means that related entities are merged into managed state when the owning entity is merged.
    - `CascadeType.REFRESH` does the same thing for the `refresh()` operation.
    - `CascadeType.REMOVE` removes all related entities association with this setting when the owning entity is deleted.
    - `CascadeType.DETACH` detaches all related entities if a manual detach were to occur.
    - `CascadeType.ALL` is shorthand for all of the cascade operations.
  - To include only refreshes and merges in the cascade operation for a one-to-one relationship, you might see the following:
    ```java
    @OneToOne(cascade={CascadeType.REFRESH, CascadeType.MERGE})
    EntityType otherSide;
    ```
- **Orphan removal**
  - orphanRemoval is an entirely ORM-specific thing. It marks "child" entity to be removed when it's no longer referenced from the "parent" entity, e.g. when you remove the child entity from the corresponding collection of the parent entity. (In contrast ON DELETE CASCADE is a database-specific thing, it deletes the "child" row in the database when the "parent" row is deleted.)
  - Imagine that you have a `Library` (object) with 3 books (`List<Book>`). Now if orphan removal is turned on, removing an object via `library.getBooks().remove(0);` will mark it as an orphan (as the parent "abandoned it), and hence when the transaction is **committed**, the orphan will be deleted by the ORM.
    ```java
    @OneToMany(orphanRemoval = true, mappedBy = "library")
    List<Book> books = new ArrayList<>();
    ```
- **Lazy loading**
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
  - 

#### An overview of mapping

- (Learn the SQL basics first (w3schools) first...)
- **Primary keys**
  - Even if your table has been created without a primary key, Hibernate will require you to specify one
  - While Hibernate will not let you omit the primary key, it will permit you to form the primary key from a collection of columns.
- **One-to-One**
  - At its simplest, the properties of both classes are maintained in the same table. --> (ID, Username, Email) even if Email is a separate POJO.
  - Alternatively, the entities can be maintained in distinct tables with identical primary keys, or with a key maintained from one of the entities into the other. (ID, Username, EmailID (unique)) --> it is easy to modify this into a ManyToOne relationship by removing the unique quantifier.
- **OneToMany, ManyToOne**
  - Simple foreign key with no additional constrains.
  - Alternatively a link table can be created (just like with ManyToMany) using a unique constrain on one side.
- **ManyToMany**
  - A link table without any constrains is applied between two tables.
  - As stated earlier in this chapter (in the section entitled “Primary Keys,” of all things), Hibernate demands that a primary key be used to identify entities. The choice of a surrogate key, a key chosen from the business data, and/or a compound primary key can be made via configuration. --> Either the Column1+Column2 has to be unique, or you have to add an additional primary key!

#### Mapping with annotations

- **Creating Hibernate Mappings with Annotations**
  - Recompiling the application upon change. (keep in mind as a con)
  - Annotations are right in the source-code and are more intuitive.
- **JPA2 persistance annotation**
  - Hibernate uses reflection at runtime to read the annotations and apply the mapping information.
  - `@Entity` - The `@Entity` annotation marks this class as an entity bean, so it must have a no-argument constructor that is visible with at least protected scope. Other JPA 2 rules for an entity bean class are (a) that the class must not be final, and (b) that the entity bean class must be concrete.
  - `@Id` defining the primary key.
    - If the annotation is applied to a field, then field access will be used.
    - If, instead, the annotation is applied to the accessor (`getXYZ()`) for the field, as shown in, then property access will be used.
  - By default, the `@Id` annotation *will not create a primary key generation strategy*, which means that you, as the code’s author, need to determine what valid primary keys are.
- `@GeneratedValue` - decide the generation strategy.
  - AUTO: Hibernate decides which generator type to use, based on the database’s support for primary key generation.
  - IDENTITY: The database is responsible for determining and assigning the next primary key.
  - SEQUENCE: Some databases support a SEQUENCE column type. It is similar to the use of an identity column type, except that a sequence is independent of any particular table and can therefore be used by multiple tables.
    ```java
    @Id
    @SequenceGenerator(name="seq1",sequenceName="HIB_SEQ")
    @GeneratedValue(strategy=SEQUENCE,generator="seq1")
    int id;
    ```
    - Here, a sequence-generation annotation named `seq1` has been declared. This refers to the database sequence object called `HIB_SEQ`. The name `seq1` is then referenced as the generator attribute of the `@ GeneratedValue` annotation.
  - TABLE: This type keeps a separate table with the primary key values. `@TableGenerator` manipulates a standard database table to obtain its primary key values, instead of using a vendor-specific sequence object, it is guaranteed to be portable between database platforms.
- **Compound Primary Keys with @Id, @IdClass, or @EmbeddedId**
  - When the primary key consists of multiple columns, you need to take a different strategy to group these together in a way that allows the persistence engine to manipulate the key values as a single object. You must create a class to represent this primary key. It will not require a primary key of its own, of course, but it must be a public class, must have a default constructor, must be serializable, and must implement `hashCode()` and `equals()` methods to allow the Hibernate code to test for primary key collisions.
  - Your three strategies for using this primary key class once it has been created are as follows:
    - Mark it as `@Embeddable` and add to your entity class a normal property for it, marked with `@Id`.
      ```java
      @Embeddable
      public class ISBN implements Serializable {
      /*No arg constructor, getters, setters, equals, hashCode*/
      }

      @Entity
      public class CPKBook {
      @Id
      ISBN id;
      /*...*/
      }
      ```
    - Add to your entity class a normal property for it, marked with `@EmbeddableId`.
      ```java
      @Entity
      public class EmbeddedPKBook {
        @EmbeddedId
        EmbeddedISBN id;
        @Column
        String name;
        static class EmbeddedISBN implements Serializable {
        // looks fundamentally the same as the ISBN class
        }
      }
      ```
    - Add properties to your entity class for all of its fields, mark them with `@Id`, and mark your entity class with `@IdClass`, supplying the class of your primary key class.
       ```java
      @Entity
      @IdClass(IdClassBook.EmbeddedISBN.class)
      public class IdClassBook {
        @Id
        int group;
        @Id
        int publisher;
        @Id
        int title;
        @Id
        int checkdigit;
        String name;
        /*...*/
      }
      static class EmbeddedISBN implements Serializable {
        // identical to EmbeddedISBN from Listing 6-8
      }
      ```
  - ` @Embeddable` - annotation over a class defines that, it does not have independent existence. (E.g. the class "UserDetails" might be marked as embeddable, while the class "User" holds a reference to it.)
- **Database Table Mapping with @Table and @SecondaryTable**


