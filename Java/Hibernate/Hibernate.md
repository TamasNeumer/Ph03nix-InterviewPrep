# Hibernate

#### Intro
- **Object-relational mapping** or ORM is the programming technique to map application domain model objects to the relational database tables. Hibernate is java based ORM tool that provides framework for mapping application domain objects to the relational database tables and vice versa.
- Hibernate supports mapping of java classes to database tables and vice versa. It provides features to perform CRUD operations across all the major relational databases.
- Hibernate supports transaction management and make sure there is no inconsistent data present in the system.
- Configured by XML
- Hibernate provides a powerful query language (HQL) that is similar to SQL. However, HQL is fully object-oriented and understands concepts like inheritance, polymorphism and association.

#### Architecture
![Hibernate Architecture](https://cdn.journaldev.com/wp-content/uploads/2014/05/Hibernate-Architecture-Diagram.jpg "Hibernate architecture")

- **SessionFactory (org.hibernate.SessionFactory)**: SessionFactory is an immutable thread-safe cache of compiled mappings for a single database. We can get instance of org.hibernate.Session using SessionFactory.
- **Session (org.hibernate.Session)**: Session is a single-threaded, short-lived object representing a conversation between the application and the persistent store. It wraps JDBC java.sql.Connection and works as a factory for org.hibernate.Transaction.
- **Persistent objects**: Persistent objects are short-lived, single threaded objects that contains persistent state and business function. These can be ordinary JavaBeans/POJOs. They are associated with exactly one org.hibernate.Session.
- **Transient objects**: Transient objects are persistent classes instances that are not currently associated with a org.hibernate.Session. They may have been instantiated by the application and not yet persisted, or they may have been instantiated by a closed org.hibernate.Session.
- **Transaction (org.hibernate.Transaction)**: Transaction is a single-threaded, short-lived object used by the application to specify atomic units of work. It abstracts the application from the underlying JDBC or JTA transaction. A org.hibernate.Session might span multiple org.hibernate.Transaction in some cases.
- **ConnectionProvider (org.hibernate.connection.ConnectionProvider)**: ConnectionProvider is a factory for JDBC connections. It provides abstraction between the application and underlying javax.sql.DataSource or java.sql.DriverManager. It is not exposed to application, but it can be extended by the developer.
- **TransactionFactory (org.hibernate.TransactionFactory)**: A factory for org.hibernate.Transaction instances.

#### Annotation vs XML Configuration
- You have to tell Hibernate how to map the class properties to tables and rows in the database. You can do this two ways:
  - Use Javax Annotations over the properties in the class (@Entity, etc.)
  - Use an external XML that basically does the same as the annitation based configuration, however does that in an XML. e.g.:

  ```
  <hibernate-mapping>
  	<class name="com.journaldev.hibernate.model.Employee" table="EMPLOYEE">
          <id name="id" type="int">
              <column name="ID" />
              <generator class="increment" />
          </id>
          <property name="name" type="java.lang.String">
              <column name="NAME" />
          </property>
          <property name="role" type="java.lang.String">
              <column name="ROLE" />
          </property>
          <property name="insertTime" type="timestamp">
          	<column name="insert_time" />
          </property>
      </class>
  </hibernate-mapping>
  ```

- Note the followings:
  - You define the table name
  - For each property, you define the property name, java type, and the column name.


- Then you have to configure hibernate to use the above created XML configuration / Annotation based stuff. The configuration file contains:
  - driver_class, connection_url, username, pw etc.
  - If you use XML mapping then: `<mapping resource="employee.hbm.xml" />`
  - If you use Annotation mapping then: `<mapping class="com.journaldev.hibernate.model.Employee1"/>`

- Once done, do the following to save a Java Object in the MySQL DB:
  - Create configuration
  - Create service registry using the above created configuration
  - Create a session factory using the configuration and the serviceRegistry as argument to the function.
  - Create Session using the factory
  - Begin transaction, save object, commit changes.

  ```java
  // Creating Hibernate config using our config.xml
  Configuration configuration = new Configuration();
  configuration.configure("hibernate.cfg.xml");

  // Create Service Regitry
  ServiceRegistry serviceRegistry =
      new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();

  // Create Session Factory
  SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);

  // Create session
  Session session = sessionFactory.getCurrentSession();

  // Begin transaction, save object, commit chnages.
  session.beginTransaction();
	session.save(emp);
	session.getTransaction().commit();
  ```

#### One-to-One relationships
- Set up the database using the sql file from the references.
- When using XML configuration the following line is the most important when configuring the `Txn` class:

  ```
  <one-to-one name="customer" class="com.journaldev.hibernate.model.Customer"
                      cascade="save-update" />
  ```

  - meaning that the `Customer customer` field in the class references a ...Customer object with one-to-one reference.

- When configuring the `Customer` class:

  ```
  <id name="id" type="long">
      <column name="txn_id" />
      <generator class="foreign">
          <param name="property">txn</param>
      </generator>
  </id>
  <one-to-one name="txn" class="com.journaldev.hibernate.model.Txn"
              constrained="true"></one-to-one>
  ```

  - **generator class=”foreign”** is the important part that is used for hibernate foreign key implementation.

- Then you do the same as before.
  - Create the session, you create a ``Txn`` and `Customer` object.
  - Set the references to each other
  - `session.save(txn);`
  - **The example is dumb, as they kinna "share" the primary key.** (Transaction table has its txn_id auto_incremented. Primary key. However the Customer uses a "txn_id" as primary key as well as a foreign key at the same time.)
  - Anyway. **Interesting is that the DB was the one who assigned the id.**


**Using annotations**
- Imporant points:

  ```java
  // CUSTOMER CLASS
  @Id
  @Column(name="txn_id", unique=true, nullable=false)
  @GeneratedValue(generator="gen")
  @GenericGenerator(name="gen", strategy="foreign", parameters={@Parameter(name="property", value="txn")})
  private long id;

  //...
  @OneToOne
  @PrimaryKeyJoinColumn
  private Txn1 txn;
  ```

- Very good description of the foreign key property: [Link](https://howtoprogramwithjava.com/hibernate-onetoone-annotation/)

#### One to Many
- **@OneToMany(mappedBy="cart1") vs @JoinColumn(name="cart_id", nullable=false)**
  - One entity has an array of the other. (Shopping cart has an array of items.)
  - To store this relationship, you want each item to have a shopping card column, where the cart_id is stored.
  - You achive this by giving "mappedBy" to the entity containing the collection, and marking the single entity with @JoinColumn

  ```java
  @ManyToOne
  @JoinColumn(name="cart_id", nullable=false)
  private Cart1 cart1;

  @OneToMany(mappedBy="cart1")
  private Set<Items1> items1;
  ```

#### Many to Many
- In the many to many relation you create a new table that has two columns, both foreign keys.
- Interestingly only the cart had a Set<Items>, the item entity didn't have any reference to the cart at all.
```java
// CART
@ManyToMany(targetEntity = Item1.class, cascade = { CascadeType.ALL })
@JoinTable(name = "CART_ITEMS",
      joinColumns = { @JoinColumn(name = "cart_id") },
      inverseJoinColumns = { @JoinColumn(name = "item_id") })
private Set<Item1> items;

// ITEM1s
@ManyToMany(mappedBy="items") //-> refers to the field of the other class!
private Set<Cart1> carts;
```

#### HQL
HQL is case-insensitive except for java class and variable names. So SeLeCT is the same as sELEct is the same as SELECT, but ``com.journaldev.model.Employee`` is not same as ``com.journaldev.model.EMPLOYEE``.
