# High Performance JPA

#### 0. Preface

- The database manual is not only meant for database administrators. Interacting with a database, without knowing how it works, is like driving a racing car without taking any driving lesson. Getting familiar with the SQL standard and the database specific features can make the difference between a high performance application and one that barely crawls.
- There are data access patterns that have proven their effectiveness in many enterprise application scenarios. Martin Fowler’s Patterns of Enterprise Application Architecture2 is a must read for every enterprise application developer.
- JPQL (Java Persistence Querying Language) abstracts the common SQL syntax by subtracting database specific querying features, so it lacks support for Window Functions, Common Table Expressions, Derived tables or PIVOT. As opposed to JPA, jOOQ (Java Object Oriented Query)3 embraces database specific query features, and it provides a type-safe query builder which can protect the application against SQL injection attacks even for dynamic native queries.

  ![Data access](/Java/Hibernate/res/0_DataAccess.PNG)

#### Performance and Scaling

- **Response time and throughput**
  - The transaction response time is measured as the time it takes to complete a transaction, and so it encompasses the following time segments:
    - The database connection acquisition time
    - The time it takes to send all database statements over the wire
    - The execution time for all incoming statements
    - The time it takes for sending the result sets back to the database client
    - The time the transaction is idle due to application-level computations prior to releasing the database connection.
  - **Throughput** is defined as the rate of completing incoming load.
  - Due to contention on database resources and the cost of maintaining coherency across multiple concurrent database sessions, the relative throughput gain follows a curve instead of a straight line. --> Meaning that you can *over-scale* and it would result in a performance decrease, as containing consistency takes up too much resources after a certain point!
- **Database connections boundaries**
  - **Connection management per provider**
    - SQL Server 2016 and MySQL 5.7 use thread-based connection handling.
    - PostgreSQL 9.5 uses one operating system process for each individual connection.
    - On Windows systems, Oracle uses threads, while on Linux, it uses process-based connections. Oracle 12c comes with a thread-based connection model for Linux systems too.
  - **Indexing**
    - An index is an on-disk structure associated with a table or view that speeds retrieval of rows from the table or view. These keys are stored in a structure (B-tree) that enables SQL Server to find the row or rows associated with the key values quickly and efficiently.
  - **Clustered Indexing**
    - Clustered indexes sort and store the data rows in the table or view based on their key values. These are the columns included in the index definition. There can be only one clustered index per table, because the data rows themselves can be stored in only one order. When a table has a clustered index, the table is called a **clustered table**. If a table has no clustered index, its data rows are stored in an unordered structure called a **heap**.
  - **Nonclustered indexing**
    - A nonclustered index contains the nonclustered index key values and each key value entry has a pointer ("*row locator*") to the data row that contains the key value. For a heap, a row locator is a pointer to the row. For a clustered table, the row locator is the clustered index key.
- **Scaling up and scaling out**
  - Scaling vertically (scaling up) means adding resources to a single machine. Increasing the number of available machines is called horizontal scaling (scaling out).
    - Facebook is known for [scaling horizontally](https://www.facebook.com/note.php?note_id=409881258919) while StackOverflow [scales vertically](https://stackexchange.com/performance)
  - **Master-Slave Replication**
    - The master "replicates" itself to multiple slaves. Slaves can handle incoming *read* requests, but all write requests go into the master first. Once processed, the master copies the new changes to the slaves.
    - The Slave nodes are eventual consistent as they might lag behind the Master. In case the Master node crashes, a cluster-wide voting process must elect the new Master (usually the node with the most recent update record) from the list of all available Slaves.
    - Most database systems allow one synchronous Slave node, at the price of increasing transaction response time (the Master has to block waiting for the synchronous Slave node to acknowledge the update). - Also called *hot standby* topology.
  - **Multi-Master replication**
    - In a Multi-Master replication scheme, all nodes are equal and can accept both read-only and read/write transactions. (Slower response time though, since severs must be in sync.)
  - **Sharding**
    - Sharding means distributing data across multiple nodes so each instance contains only a subset of the overall data. Each shard must be self-contained because a user transaction can only use data from within a single shard.
    - By reducing data size per node, indexes will also require less space, and they can better fit into main memory. With less data to query, the transaction response time can also get shorter too.
    - MySQL Cluster offers automatic sharding, so data is evenly distributed (using a primary key hashing function) over multiple commodity hardware machines.

#### JDBC Connection Management

- **JDBC Intro**
  - The JDBC (Java Database Connectivity) API provides a common interface for communicating to a database server. All the networking logic and the database specific communication protocol are hidden away behind the vendor-independent JDBC API.
  - The `java.sql.Driver` is the main entry point for interacting with the JDBC API, defining the implementation version details and providing access to a database connection.
  ![Data access](/Java/Hibernate/res/1_JDBC.PNG)
  - To communicate to a database server, a Java program must first obtain a ``java.sql.Connection``. Although the ``java.sql.Driver`` is the actual database connection provider, it’s more convenient to use the ``java.sql.DriverManager`` since it can also resolve the JDBC driver associated with the current database connection URL.
  - Since JDBC 4.0, the Service Provider Interfaces mechanism can automatically discover all the available drivers in the current application class-path.
- **DriverManager**
  - Methods to retrieve a connection:
      ```java
      public static Connection getConnection(
        String url, Properties info) throws SQLException;
      public static Connection getConnection(
        String url, String user, String password) throws SQLException;
      public static Connection getConnection(
        String url) throws SQLException;
      ```
  - Every time the ``getConnection()`` method is called, the ``DriverManager`` will request a new **physical connection** from the underlying ``Driver``.
- **DataSource**
  - **Basics**
    - In a typical enterprise application, the user request throughput is greater than the available database connection capacity. As long as the connection acquisition time is tolerable (from the end-user perspective), the user request can wait for a database connection to become available. The middle layer acts as a database connection buffer that can mitigate user request traffic spikes by increasing request response time, without depleting database connections or discarding incoming traffic.
    - For this reason, instead of serving physical database connections, the application server provides only **logical connections (proxies or handles)**, so it can intercept and register how the client API interacts with the connection object.
    - If the ``DriverManager`` is a physical connection factory, the ``javax.sql.DataSource`` interface is a logical connection provider.
      - `public Connection getConnection() throws SQLException;`
      - `public Connection getConnection(String username, String password) throws SQLException;`
    - The simplest ``javax.sql.DataSource`` implementation could delegate connection acquisition requests to the underlying ``DriverManager``
    - **Creating connections being a very expensive operation reusing these has many advantages!** [HikariCP](http://brettwooldridge.github.io/HikariCP/) is a great connection pool provider!
  - **Why is pooling so much faster?**
    - The connection acquisition in a connection pool environment:
      1. When a connection is being requested, the pool looks for unallocated connections
      1. If the pool finds a free one, it handles it to the client
      1. If there is no free connection, the pool tries to grow to its maximum allowed size
      1. If the pool already reached its maximum size, it will retry several times before giving up with a connection acquisition failure exception
      1. When the client closes the logical connection, the connection is released and returns to the pool without closing the underlying physical connection.
    - The connection pool doesn’t return the physical connection to the client, but instead it offers a proxy or a handle. When a connection is in use, the pool changes its state to *allocated* to prevent two concurrent threads from using the same database connection. The proxy intercepts the connection close method call, and it notifies the pool to change the connection state to *unallocated*.
  - **Queuing theory capacity planning**
    - `L (average number of requests in the system) = λ (average arrival rate) * W (average time a request spends in a system)`
      - ``50 req/sec * 0.1 avg time = 5 connection requests`` in the system on average --> A pool size of 5 can accommodate the average incoming traffic without having to enqueue any connection request.
      - The theory can be made more complex with adding the departure rate, but it's only theory...
  - **Practical database connection provisioning**
    - Unfortunately, many connection pooling solutions only offer limited support for monitoring and failover strategies, and that was the main reason for building [FlexyPool](https://github.com/vladmihalcea/flexy-pool)
    - FlexyPool Metrics:
      - concurrent connection requests
      - concurrent connections
      - maximum pool size
      - connection acquisition time
      - overall connection acquisition time
      - retry attempts
      - overflow pool size
        - How much the pool size can grow over the maximum size until timing out the connection acquisition request
      - connection lease time
        - The duration between the moment a connection is acquired and the time it gets released
    - The library provides nice tools to monitor the connection pool so that later you can adjust the pool size.

#### Batch Updates

- **Batching Statements**
  - Sending multiple statements in a single request reduces the number of database roundtrips, therefore decreasing transaction response time.
  - For executing static SQL statements, JDBC defines the ``Statement`` interface and batching multiple DML statements is as straightforward as the following code snippet:
    ```sql
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

#### Statement Caching

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