# Beginning Hibernate

## Intro

- Database <--> JDBC <--> [Mappings -> Hibernate <- Configuration] <--> ClientCode / POJOs
- Hibernate uses standard Java Database Connectivity (JDBC) database drivers to access the relational database.
- The traditional JDBC code contains loads of "boilerplate" with many try & catch blocs.  Hibernate provides cleaner resource management, which means that you do not have to worry about the actual database connections, nor do you have to have giant try/catch/finally blocks.
- In Hibernate parlance, this is called a mapping. Mappings can be provided either through Java annotations or through an XML mapping file.

## Integrating and Configuring Hibernate

- **Hiberante Configuration**
  - For this example a locally executed MySQL is used. (`src/test/resource/hibernate.cfg.xml`) For our purpose the file was put into the "test" folder, as the tests are going to use the given configuration:
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
- **Persisting an object - example**
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
  - `HikariCP` connection pool is even better though!!!
- **Annotations used in the Java Class**
  - `@Entity` - The class is marked as an entity and hence it must have a no-argument constructor that is visible with at least protected scope.
  - `@Id` - defines a primary key (+not null) in the database. It is NOT auto-incremented by default!
  - `@GeneratedValue(strategy=GenerationType.AUTO)` - enables the `AUTO_INCREMENT` option on the given column.
  - `@Column(nullable = false)` - adds non-null constraint.
  - `@Column(unique = true)` - unique constraint on the column.

## Building a Simple Application

- **Session and Persistance context**
  - Persistence context can be thought of as a container or a first-level cache for all the objects that you loaded or saved to a database during a session.
  - The session is a logical transaction, which boundaries are defined by your application’s business logic. When you work with the database through a persistence context, and all of your entity instances are attached to this context, you should always have a single instance of entity for every database record that you’ve interacted during the session with.
    - `SessionFactory.openSession()` always opens a new session that you have to close once you are done with the operations. `SessionFactory.getCurrentSession()` returns a session bound to a context - you don't need to close this.
    - "Hibernate SessionFactory `getCurrentSession()` method returns the session *bound to the context.*" - But for this to work, we need to configure it in hibernate configuration file like below:
      - `<property name="hibernate.current_session_context_class">thread</property>`
    - Since this session object belongs to the hibernate context, we don’t need to close it. Once the session factory is closed, this session object gets closed.
    - `Session` objects are **NOT** thread safe! -> Don't use in multi-threaded environment! If using a multi-threaded environment open a new session for each request.
    - `StatelessSession` bypasses Hibernate’s event model and interceptors. It’s more like a normal JDBC connection and doesn’t provide any benefits that come from using hibernate framework. It can be beneficial if we are loading bulk data into database and we don’t want hibernate session to hold huge data in first-level cache memory.
  - Any entity instance in your application appears in one of the three main states in relation to the Session persistence context:
    - **Transient**s- this instance is not, and never was, attached to a `Session`; this instance has no corresponding rows in the database; it’s usually just a new object that you have created to save to the database
    - **Persistent** - this instance is associated with a unique `Session` object; upon flushing the `Session` to the database, this entity is guaranteed to have a corresponding consistent record in the database;
    - **Detached** - this instance was once attached to a `Session` (in a persistent state), but now it’s not; an instance enters this state if you `evict()` it from the context, clear or close the `Session`, or put the instance through serialization/deserialization process. Detached objects have a representation in the database, but changes to the object will not be reflected in the database, and vice versa. One reason you might consider doing this would be to read an object out of the database, modify the properties of the object in memory, and then store the results someplace other than your database. (Alternative would be a deep copy.)
    - **Removed** - A removed object is one that’s been marked for deletion in the current transaction. An object is changed to removed state when `Session.delete()` is called for that object reference. (=> Hibernate will remove the object from the DB on the next flush.)
      ![Flow](/Java/Hibernate/res/FLOW.PNG)
  - When the entity instance is in the persistent state, all changes that you make to the mapped fields of this instance will be applied to the corresponding database records and fields upon flushing the `Session`. The persistent instance can be thought of as “online”, whereas the detached instance has gone “offline” and is not monitored for changes. --> This means that when you change fields of a persistent object, you don’t have to call `save`, `update` or any of those methods to get these changes to the database: all you need is to commit the transaction, or flush or close the session, when you’re done with it.
- **Transactions**
  - A transaction is a “bundled unit of work” for a database. Changes are committed as a whole, so that no other transaction can see them until the transaction completes.
  - If the transaction is abandoned – that is, `commit()` is not called explicitly – then the transaction’s changes are abandoned and the database is left unmodified.
  - Transactions can be aborted (“rolled back,” with the `Transaction.rollback()` method) such that any changes that have taken place as part of that transaction are discarded.
  - In Hiberante all your persistance (=saving) operations **must** be declared withing a given transaction! -->  Without transaction you can **only retrieve** object from database
- **Persistance operations**
  - It is important to understand from the beginning that all of the methods (`persist`, `save`, `update`, `merge`, `saveOrUpdate`) do **not** immediately result in the corresponding SQL `UPDATE` or `INSERT` statements. The actual saving of data to the database occurs on committing the transaction or flushing the `Session`. The mentioned methods basically manage the state of entity instances by transitioning them between different states along the lifecycle.
  - `session.persist(Object o)`
    - The `persist` method is intended for adding a new entity instance to the persistence context, i.e. transitioning an instance from transient to persistent state.
    - If you try to `persist` a detached instance, the implementation is bound to throw an exception.
    - If the object properties are changed before the transaction is committed or session is flushed, it will also be saved into database.
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

## Identifiers, Relations, Database operations

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
  - **LOAD**
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
      - `PESSIMISTIC_READ` and PESSIMISTIC_WRITE: Both of these obtain a lock immediately on row access.
      - `PESSIMISTIC_FORCE_INCREMENT`: This obtains the lock immediately on row access, and also immediately updates the entity version.
    - It will always return a **“proxy”** (Hibernate term) without hitting the database. In Hibernate, proxy is an object with the given identifier value, its properties are not initialized yet, it just look like a temporary fake object. Properties are only fetched from the database when you actually refer to them / i.e. when you actually use them. If it turns out, that the object with the given ID doesn't exist in the database anymore, it throws an `ObjectNotFoundException`.
    - You should not use a `load()` method unless you are sure that the object exists. If you are not certain, then use one of the `get()` methods. The `load()` methods will **throw an exception** if the unique ID is not found in the database, whereas the `get()` methods will merely return a `null` reference.
- **GET**
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

## An overview of mapping

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

## Mapping with annotations

### Configuration

- Hibernate Configuration
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
            <!--<property name="hbm2ddl.auto">create-drop</property> -->

            <!--  c3po config -->
            <!--hibernate.c3p0.min_size – Minimum number of JDBC connections in the pool. Hibernate default: 1-->
            <property name="hibernate.c3p0.min_size">1</property>
            <!--hibernate.c3p0.max_size – Maximum number of JDBC connections in the pool. Hibernate default: 100-->
            <property name="hibernate.c3p0.max_size">5</property>
            <!--hibernate.c3p0.timeout – When an idle connection is removed from the pool (in second). Hibernate default: 0, never expire.-->
            <property name="hibernate.c3p0.timeout">300</property>
            <!--hibernate.c3p0.max_statements – Number of prepared statements will be cached. Increase performance. Hibernate default: 0 , caching is disable.-->
            <property name="hibernate.c3p0.max_statements">50</property>
            <!--hibernate.c3p0.idle_test_period – idle time in seconds before a connection is automatically validated. Hibernate default: 0-->
            <property name="hibernate.c3p0.idle_test_period">3000</property>
            <mapping class="Entity.Customer"/>
            <mapping class="Entity.Txn"/>
            <mapping class="Entity.Items"/>
            <mapping class="Entity.Cart"/>
            <mapping class="Entity.CartManyToMany"/>
            <mapping class="Entity.ItemManyToMany"/>
        </session-factory>
    </hibernate-configuration>
    ```

### OneToOne

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
    }
    ```
- Notes:
  - The `Txn` class declares the relationship `@OneToOne(mappedBy = "txn")` meaning that the `Customer` class will be the owner of the relationship. Also the table used for `Customer` objects will be responsible to store the foreign key.
  - ``@Cascade(value = CascadeType.SAVE_UPDATE)`` - cascading will be used on save or update, but not on delete!
  - The One-to-One relationship requires some “extra” code because it has a special property of inheriting the foreign
    key from the parent table (`Txn`) and using it as the primary key of the child table (Customer`)`. --> The ``Customer`` class holds the primary key id of `Txn` as its own primary key / id.
  - To prevent Hibernate creating (or looking for in our case) a `txn_txn_id` column, (which would be confusing and a waste of space) we need to
    help Hibernate by letting it know which column is our join column in our one-to-one relationship.
  - To ensure the id generation we used: ``@GenericGenerator(name="gen", strategy="foreign", parameters={@Parameter(name="property", value="txn")})``
    - use “foreign” strategy.
    - Lastly, we need to tell the `@GenericGenerator` where the actual relationship exists.
      In our case, our `@OneToOne` relationship exists via the `txn` object, so we point it to that object via the use of the `@Parameter` annotation.
  - If you use `strategy="AUTO"`, Hibernate will generate a table called `hibernate_sequence` to provide the next number for the ID sequence. If you are using a pre-defined mysql database this is not the desired behavior.
    - When using Hibernate v 4.0 and Generation Type as `AUTO`, specifically for MySql, Hibernate would choose the `IDENTITY` strategy (and thus use the `AUTO_INCREMENT` feature) for generating IDs for the table in question.
    - Starting with version 5.0 when Generation Type is selected as `AUTO`, Hibernate uses `SequenceStyleGenerator` regardless of the database. In case of MySql Hibernate emulates a sequence using a table and is why you are seeing the `hibernate_sequence` table. MySql doesn't support the standard sequence type natively.

### OneToMany

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

        @OneToMany(mappedBy="cart")
        private Set<Items> items;
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

        public Items(String itemId, double total, int qty, Cart c){
            this.itemId=itemId;
            this.itemTotal=total;
            this.quantity=qty;
            this.cart=c;
        }
    }
    ```
- Notes:
  - When working with ORM and doing `OneToMany` or `ManyToMany` relationships you introduce **circular dependencies**. In our case the `Cart` class has a `Set<Items>` reference, while the `Item` class has a `Cart` reference. When you want to generate `toString` or `hashCode` using Project Lombok or IntelliJ's auto-generator the generated functions will contain circular dependencies as well!
    - For example the generated `hashCode` method contains the following line: `result = result * PRIME + ($items == null ? 43 : $items.hashCode());`. While evaluating this line the `items.hashCode()` triggers a circular call and results in a `StackOverFlowError`.
    - The generated `toString` method contains `items=" + this.getItems()` which also triggers a circular dependency.
    - **Solution**
      - Exclude the circular dependency.
      - In lombok:
        - `@EqualsAndHashCode(exclude="items")`
        - `@ToString(exclude = "items")`

### ManyToMany

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
- Things to note:
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

## Searches and Queries

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

## Advanced Queries Using Criteria

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

#### Adding Logging to Hibernate
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

## Weaker stuff - review and deeper research needed!!!!

#### Creating Hibernate Mappings with Annotations

- **Annotations Advantages / Disadvantages**
  - Recompiling the application upon change. (keep in mind as a con)
  - Annotations are right in the source-code and are more intuitive.
- **JPA2 persistance annotation**
  - Hibernate uses reflection at runtime to read the annotations and apply the mapping information.
  - `@Entity` - The `@Entity` annotation marks this class as an entity bean, so it must have a no-argument constructor that is visible with at least protected scope. Other JPA 2 rules for an entity bean class are (a) that the class must not be final, and (b) that the entity bean class must be concrete. By default, table names are derived from the entity names. Therefore, given a class `Book` with a simple `@Entity` annotation, the table name would be “`book`”, adjusted for the database’s configuration. If the entity name is changed (by providing a different name in the @Entity annotation, such as `@Entity(“BookThing”)`), the new name will be used for the table name.
  - `@Table` - The table name can be customized further, and other database-related attributes can be configured via the `@Table` annotation.
  - `@SecondaryTable` annotation provides a way to model an entity bean that is persisted across several different database tables.
  - `@Id` defining the primary key.
    - If the annotation is applied to a field, then field access will be used.
    - If, instead, the annotation is applied to the accessor (`getXYZ()`) for the field, as shown in, then property access will be used.
  - By default, the `@Id` annotation *will not create a primary key generation strategy*, which means that you, as the code’s author, need to determine what valid primary keys are.
- `@GeneratedValue` - decide the generation strategy.
  - `AUTO`: Hibernate decides which generator type to use, based on the database’s support for primary key generation.
  - `IDENTITY`: The database is responsible for determining and assigning the next primary key.
  - `SEQUENCE`: Some databases support a `SEQUENCE` column type. It is similar to the use of an identity column type, except that a sequence is independent of any particular table and can therefore be used by multiple tables.
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
    - An embeddable entity must be composed entirely of basic fields and attributes. An embeddable entity can only use the `@Basic`, `@Column`, `@Lob`, `@Temporal`, and `@Enumerated` annotations.
    - It cannot maintain its own primary key with the `@Id` tag because its primary key is the primary key of the enclosing entity.
- **Database Table Mapping with @Table and @SecondaryTable**
  - The `@Table` annotation provides four attributes, allowing you to override the name of the table, its catalog, and its schema, and to enforce unique constraints on columns in the table. Typically, you would only provide a substitute table name thus: `@Table(name="ORDER_HISTORY")`.
  - The @SecondaryTable annotation provides a way to model an entity bean that is persisted across several different database tables.
- `@Basic`
  - The first attribute is named `optional` and takes a `boolean`. Defaulting to `true`, this can be set to `false` to provide a hint to schema generation that the associated column should be created `NOT NULL`. The second is named `fetch` and takes a member of the enumeration `FetchType`. This is `EAGER` by default, but can be set to `LAZY` to permit loading on access of the value.
- `@Transient`
  - The `@Transient` annotation does not have any attributes—you just add it to the instance variable or the getter method as appropriate for the entity bean’s property access strategy.
- `@Column`
  - `name` - name of the column
  - `length` - size of the column (e.g. for strings)
  - `nullable`
  - `unique`
    ```java
    @Column(name="working_title",length=200,nullable=false)
    String title;
    ```
  - `table` is used when the owning entity has been mapped across one or more secondary tables. By default, the value is assumed to be drawn from the primary table, but the name of one of the secondary tables can be substituted here.
  - `insertable` defaults to true, but if set to false, the annotated field will be omitted from insert statements generated by Hibernate (i.e., it won’t be persisted).
  - `updatable` defaults to true, but if set to false, the annotated field will be omitted from update statements generated by Hibernate (i.e., it won’t be altered once it has been persisted).
  - `columnDefinition` can be set to an appropriate DDL fragment to be used when generating the column in the database. This can only be used during schema generation from the annotated entity, and should be avoided if possible, since it is likely to reduce the portability of your application between database dialects.
  - `precision` permits the precision of decimal numeric columns to be specified for schema generation, and will be ignored when a nondecimal value is persisted.
  - `scale` permits the scale of decimal numeric columns to be specified for schema generation and will be ignored where a nondecimal value is persisted. The value given represents the number of places after the decimal point.
- **Modelling Entity Relationships**
  - **OneToOne**
    - You should give some thought to using the embedded technique described previously before using the `@OneToOne` annotation.
    - Advantage might be the ability to easily convert it to ManyToOne or OneToMany
    - Parameters: `targetEntity, cascade, fetch, optional, orphanRemoval, mappedBy`
  - **ManyToOne, OneToMany**
    - Cascading:
      - `ALL` requires all operations to be cascaded to dependent entities. this is the same as including `MERGE`, `PERSIST`, `REFRESH`, `DETACH`, and `REMOVE`.
      - `MERGE` cascades updates to the entity’s state in the database (i.e., `UPDATE`...).
      - `PERSIST` cascades the initial storing of the entity’s state in the database (i.e., `INSERT`...).
      - `REFRESH` cascades the updating of the entity’s state from the database (i.e., `SELECT`...).
      - `DETACH` cascades the removal of the entity from the managed persistence context.
      - `REMOVE` cascades deletion of the entity from the database (i.e., `DELETE`...).
      - if no cascade type is specified, no operations will be cascaded through the association.
    - **ManyToMany**
      - `mappedBy` is the field that owns the relationship—this is only required if the association is bidirectional. If an entity provides this attribute, then the other end of the association is the owner of the association, and the attribute must name a field or property of that entity.
      - `targetEntity` is the entity class that is the target of the association. Again, this may be inferred from the generic or array declaration, and only needs to be specified if this is not possible.
      - `cascade` indicates the cascade behavior of the association, which defaults to none.
      - `fetch` indicates the fetch behavior of the association, which defaults to LAZY.
        ```java
        @ManyToMany(cascade = ALL)
        Set<Author> authors;
        /*...*/
        @ManyToMany(mappedBy = "authors")
        Set<Book> books;

        /*Specifying link table*/
        @ManyToMany(cascade = ALL)
        @JoinTable(
                name="Books_to_Author",
                joinColumns={@JoinColumn(name="book_ident")},
                inverseJoinColumns={@JoinColumn(name="author_ident")}
        )
        Set<Authors> authors;
        ```
- **Collection Ordering**
  - An ordered collection can be persisted in hibernate or Jpa 2 using the `@OrderColumn` annotation to maintain the order of the collection.
  - You can also order the collection at retrieval time by means of the `@OrderBy` annotation.
- **Mapping Inheritance**
  - Single table (`SINGLE_TABLE`)
    - One table for each class hierarchy
    - When following this strategy, you will need to ensure that columns are appropriately renamed when any field or property names collide in the hierarchy.
    - The single-table approach can be messy, leading to many columns in the table that aren’t used in every row, as well as a rapidly horizontally growing table.
  - Joined Table (`JOINED`)
    - Here a discriminator column is used, but the fields of the various derived types are stored in distinct tables.
    - It is easiest to maintain your database when using the joined-table approach. If fields are added or removed from any class in the class hierarchy, only one database table needs to be altered to reflect the changes. In addition, adding new classes to the class hierarchy only requires that a new table be added, eliminating the performance problems of adding database columns to large data sets. 
  - Table-per-class (`TABLE_PER_CLASS`)
    - all of the fields of each type in the inheritance hierarchy are stored in distinct tables.
    - With the table-per-class approach, a change to a column in a parent class requires that the column change be made in all child tables.
- **Other JPA annotations**
  - `@Temporal(TemporalType.Time)` - The annotation accepts a single value attribute from the javax.persistence.TemporalType enumeration.
  - `@ElementCollection` - JPA 2 introduced an `@ElementCollection` annotation for mapping collections of basic or embeddable classes. You can use the `@ElementCollection` annotation to simplify your mappings.
  - `@Lob` for large objects. (E.g.: text strings.)

#### JPA Integration adn a Lifecycle Events

- **Lifecycle Events**
  - Annotations
    - `@PrePersist` - Executes before the data is actually inserted into a database table. It is not used when an object exists in the database and an update occurs.
    - `@PostPersist` - Executes after the data is written to a database table.
    - `@PreUpdate` - Executes when a managed object is updated. This annotation is not used when an object is first persisted to a database.
    - `@PostUpdate` - Executes after an update for managed objects is written to the database.
    - `@PreRemove` - Executes before a managed object’s data is removed from the database.
    - `@PostRemove` - Executes after a managed object’s data is removed from the database.
    - `@PostLoad` - Executes after a managed object’s data has been loaded from the database and the object has been initialized.
  - Usage: Apply the annotations to `public void` ... (`no args`) methods and they will be called automatically.
- **External Entity Listeners**
  - Add the `@EntityListeners({UserAccountListener.class})` annotation to the class.
  - In the other class simply mark the method with the life cycle annotation. For example: `@PrePersist void setPasswordHash(Object o) {...}`
- **Data validation**
  - The first step is to add Hibernate Validator to our project.
    ```java
    dependency>
        <groupId>org.hibernate</groupId>
        <artifactId>hibernate-validator</artifactId>
        <version>5.1.0.Alpha1</version>
    </dependency>
    <!-- these are only necessary if not in a Java EE environment -->
    <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-validator-cdi</artifactId>
            <version>5.1.0.Alpha1</version>
    </dependency>
    <dependency>
            <groupId>javax.el</groupId>
            <artifactId>javax.el-api</artifactId>
            <version>2.2.4</version>
    </dependency>
    <dependency>
        <groupId>org.glassfish.web</groupId>
        <artifactId>javax.el</artifactId>
        <version>2.2.4</version>
    </dependency>
    ```
  - Adding some lombok annotation to the class:
    - `@AllArgsConstructor(access = AccessLevel.PACKAGE)`
    - `@Builder` enables the builder pattern for the given class. E.g.:
      ```java
      ValidatedSimplePerson person=ValidatedSimplePerson.builder()
          .age(15)
          .fname("Johnny")
          .lname("McYoungster").build();
      ```
  - Validations:
    - `@NotNull` almost the same as `@Column(nullable=false)`, but if the prior is applied the validation occurs before persistance, if the latter then the validation occurs in the database, and gives us a database constraint violation rather than a validation failure.
    - `@Min, @Max` for integers to specify min and max values.

#### Using the Session

- `SessionFactory`
  - `SessionFactory` objects are expensive objects; needlessly duplicating them will cause problems quickly, and creating them is a relatively time-consuming process. Ideally, you should have a single `SessionFactory` for each database your application will access.
  - `SessionFactory` objects are **threadsafe**, so it is not necessary to obtain one for each thread.
- `Session`
  - Sessions in Hibernate are **not threadsafe**, so sharing `Session` objects between threads could cause data loss or deadlock.
  - **Caution** - if a hibernate `Session` object throws an exception of any sort, you must discard it and obtain a new one. this prevents data in the session’s cache from becoming inconsistent with the database.
- **Session and Transaction**
  - A transaction is a unit of work guaranteed to behave as if you have exclusive use of the database.
  - If you decide to avoid transactions, you will need to invoke the `flush()` method on the session at appropriate points to ensure that your changes are persisted to the database.
  - Setting session isolation:
    - `session.connection().setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);`
- **Locking modes**
  - `NONE` - Reads from the database only if the object is not available from the caches.
  - `READ` - Reads from the database regardless of the contents of the caches.
  - `UPGRADE` - Obtains a dialect-specific upgrade lock for the data to be accessed (if this is available from your database).
  - `UPGRADE_NOWAIT` - Behaves like `UPGRADE`, but when support is available from the database and dialect, the method will fail with a locking exception immediately. Without this option, or on databases for which it is not supported, the query must wait for a lock to be granted (or for a timeout to occur).
- **Deadlocks**
  - Fortunately, a database management system (DBMS) can detect this situation automatically, at which point the transaction of one or more of the offending processes will be aborted by the database. The resulting deadlock error will be received and handled by Hibernate as a normal `HibernateException`. Now you must roll back your transaction, close the session, and then (optionally) try again.