# High Performance JPA

## Preface

- The database manual is not only meant for database administrators. Interacting with a database, without knowing how it works, is like driving a racing car without taking any driving lesson. Getting familiar with the SQL standard and the database specific features can make the difference between a high performance application and one that barely crawls.
- There are data access patterns that have proven their effectiveness in many enterprise application scenarios. Martin Fowler’s Patterns of Enterprise Application Architecture 2 is a must read for every enterprise application developer.
- JPQL (Java Persistence Querying Language) abstracts the common SQL syntax by subtracting database specific querying features, so it lacks support for Window Functions, Common Table Expressions, Derived tables or PIVOT. As opposed to JPA, jOOQ (Java Object Oriented Query)3 embraces database specific query features, and it provides a type-safe query builder which can protect the application against SQL injection attacks even for dynamic native queries.

  ![Data access](/Java/Hibernate/res/0_DataAccess.PNG)

## Performance and Scaling

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

## JDBC Connection Management

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

## Batch Updates

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

## Transactions

- **Intro**
  - A transaction is a collection of read and write operations that can either succeed or fail together, as a unit. **All database statements must execute within a transactional context**, even when the database client doesn’t explicitly define its boundaries.
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
      ![DirtyWrite](/Java/Hibernate/res/DirtyRead.PNG)
      - This anomaly is only permitted by the Read Uncommitted isolation level, and, because of the serious impact on data integrity, most database systems offer a higher default isolation level.
      - To prevent dirty reads, the database engine must hide uncommitted changes from all the concurrent transactions (but the one that authored the change). Each transaction is allowed to see its own changes because otherwise the read-your-own-writes consistency guarantee is compromised.
    - **Non-repeatable read**
      - If one transaction reads a database row without applying a shared lock on the newly fetched record, then a concurrent transaction might change this row before the first transaction has ended.
      ![DirtyWrite](/Java/Hibernate/res/NonRepeatableRead.PNG)
      - Most database systems have moved to a Multi-Version Concurrency Control model, and shared locks are no longer mandatory for preventing non-repeatable reads. By verifying the current row version, a transaction can be aborted if a previously fetched record has changed in the meanwhile.
    - **Phantom read**
      - If a transaction makes a business decision based on a set of rows satisfying a given predicate, without predicate locking, a concurrent transaction might insert a record matching that particular predicate.
      ![DirtyWrite](/Java/Hibernate/res/PhantomRead.PNG)
      - Phantom rows can lead a buyer into purchasing a product without being aware of a better offer that was added right after the user has finished fetching the offer list.
    - **Read skew**
      - Read skew is a lesser known anomaly that involves a constraint on more than one database tables. In the following example, the application requires the post and the post_details be updated in sync. Whenever a post record changes, its associated post_details must register the user who made the current modification.
      ![DirtyWrite](/Java/Hibernate/res/ReadSkew.PNG)
      - The first transaction will see an older version of the post row and the latest version of the associated post_details. Because of this read skew, the first transaction will assume that this particular post was updated by Bob, although, in fact, it is an older version updated by Alice.
      - Like with non-repeatable reads, there are two ways to avoid this phenomenon:
        - the first transaction can acquire shared locks on every read, therefore preventing the second transaction from updating these records
        - the first transaction can be aborted upon validating the commit constraints (when using an MVCC implementation of the Repeatable Read or Serializable isolation levels).
    - **Write skew**
      - Like read skew, this phenomenon involves disjoint writes over two different tables that are constrained to be updated as a unit. Whenever the post row changes, the client must update the post_details with the user making the change.
      ![DirtyWrite](/Java/Hibernate/res/WriteSkew.PNG)
      - Both Alice and Bob will select the post and its associated post_details record. If write skew is allowed, Alice and Bob can update these two records separately, therefore breaking the constraint.
      - Prevention: same as above.
    - **Lost update**
      - This phenomenon happens when a transaction reads a row while another transaction modifies it prior to the first transaction to finish. In the following example, Bob’s update is silently overwritten by Alice, who is not aware of the record update.
      ![DirtyWrite](/Java/Hibernate/res/LostUpdate.PNG)
      - This anomaly can have serious consequences on data integrity (a buyer might purchase a product without knowing the price has just changed), especially because it affects Read Committed, the default isolation level in many database systems.
      - With MVCC, the second transaction is allowed to make the change, while the first transaction is aborted when the database engine detects the row version mismatch (during the first transaction commit).
  - **Isolation Levels**
    - **Serializable is the only isolation level to provide a truly ACID transaction** interleaving. But serializability comes at a price as locking introduces contention, which, in turn, limits concurrency and scalability.
    ![DirtyWrite](/Java/Hibernate/res/IsolationLevels.PNG)
    - To read/set these values in JDBC:
      - `int level = connection.getMetaData().getDefaultTransactionIsolation();`
      - `connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);`
    - **Isolation levels used by Database systems:**
      - Read Committed (Oracle, SQL Server, PostgreSQL)
      - Repeatable Read (MySQL)
    - **Prevented phenomena by ReadCommitted**:
      ![DirtyWrite](/Java/Hibernate/res/Phenomenas.PNG)
      - **MYSQL** Although it uses MVCC, InnoDB implements Read Uncommitted so that dirty reads are permitted.
    - **Prevented phenomena by ReadCommitted**:
      ![DirtyRead](/Java/Hibernate/res/PhenomenaDirtyRead.PNG)
      - **MYSQL:** Query-time snapshots are used to isolate statements from other concurrent transactions. When explicitly acquiring shared or exclusive locks or when issuing update or delete statements (which acquire exclusive locks to prevent dirty writes), if the selected rows are filtered by unique search criteria (e.g. primary key), the locks can be applied to the associated index entries.
    - **Repeatable Read**
      ![DirtyRead](/Java/Hibernate/res/PhRepeatableRead.PNG)
      - **MYSQL** Every transaction can only see rows as if they were when the current transaction started. This prevents non-repeatable reads, but it still allows lost updates and write skews.
    - **Serializable**
      ![DirtyRead](/Java/Hibernate/res/PhSerializable.PNG)
      - **MYSQL** The Serializable isolation builds on top of Repeatable Read with the difference that every record that gets selected is protected with a shared lock as well. The locking-based approach allows MySQL to prevent the write skew phenomena, which is prevalent among many Snapshot Isolation implementations.
- **Durability**
  - Durability ensures that all committed transaction changes become permanent.
  - When a transaction is committed, the database persists all current changes in an append-only, sequential data structure commonly known as the *redo log*.
  - **MYSQL** All the redo log entries associated with a single transaction are stored in the mini transaction buffer and flushed at once into the global redo buffer. The global buffer is flushed to disk during commit. By default, there are two log files which are used alternatively.
- **Read Only Transactions**
  - **Intro**
    - The JDBC Connection defines the `setReadOnly(boolean readOnly)` method which can be used to hint the driver to apply some database optimizations for the upcoming read-only transactions. This method shouldn’t be called in the middle of a transaction because the database system cannot turn a read-write transaction into a read-only one (a transaction must start as read-only from the very beginning).
    - **MYSQL** If a modifying statement is executed when the `Connection` is set to read-only, the JDBC driver throws an exception. InnoDB can optimize read-only transactions because it can skip the transaction ID generation as it’s not required for read-only transactions.
  - **Read-only transaction routing**
    - Setting up a database replication environment is useful for both high-availability (a Slave can replace a crashing Master) and traffic splitting. In a Master-Slave replication topology, the Master node accepts both read-write and read-only transactions, while Slave nodes only take read-only traffic.
    - **MYSQL** The `com.mysql.jdbc.ReplicationDriver` supports transaction routing on a Master-Slave topology, the decision being made on the `Connection` read-only status basis.
    ![DirtyRead](/Java/Hibernate/res/TransactionRouting.PNG)
- **Transaction boundaries**
  - **Separating DAO from usage**
    - By default, every `Connection` **starts in auto-commit mode**, each statement being executed in a separate transaction. Unfortunately, it doesn’t work for multi-statement transactions as it moves atomicity boundaries from the logical unit of work to each individual statement.
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

## Why JPA and Hibernate Matter

- **JPA vs Hibernate**
  - JPA is only a specification. It describes the interfaces that the client operates with and the standard object-relational mapping metadata (Java annotations or XML descriptors).
  - If JPA is the interface, Hibernate is one implementation and implementation details always matter from a performance perspective.
- **Schema ownership**
  - lthough, theoretically, both the database and the Domain Model could drive the schema evolution, for practical reasons, the schema belongs to the database. (An enterprise system might be too large to fit into a single application, so it’s not uncommon to split in into multiple subsystems, each one serving a specific goal.)
  - **The distributed commit log**
    - For very large enterprise systems, where data is split among different providers (relational database systems, caches, Hadoop, Spark), it’s no longer possible to rely on the relational database to integrate all disparate subsystems.
    - In this case, Apache Kafka offers a fault-tolerant and scalable append-only log structure, which every participating subsystem can read and write concurrently.
  - Even if the data access framework can auto-generate the database schema, the schema must be migrated incrementally and all changes need to be traceable in the VCS (Version Control System) as well. Along with table structure, indexes and triggers, the database schema is therefore accompanying the Domain Model source code itself. A tool like **Flywaydb** can automate the database schema migration, and the system can be deployed continuously, whether it’s a test or a production environment.
- **Write-based optimizations**
  - JPA shifts the developer mindset from SQL statements to entity state transitions. An entity can be in one of the following states:
    ![DirtyRead](/Java/Hibernate/res/JPAStates.PNG)
  - The Persistence Context captures entity state changes, and, during flushing, it translates them to SQL statements. The JPA `EntityManager` and the Hibernate `Session` (which includes additional methods for moving an entity from one state to the other) interfaces are gateways towards the underlying Persistence Context, and they define all the entity state transition operations.
    ![DirtyRead](/Java/Hibernate/res/EntityStateTransition.PNG)
  - Hibernate uses `PreparedStatement`(s) exclusively, so not only it protect against SQL injection, but the data access layer can better take advantage of server-side and client-side statement caching as well.
- **Read-based optimizations**
  - In the JDBC use case, the associations must be manually resolved (= writing JOINs), while JPA does it automatically (based on the entity schema).
  - **Prefer projections for read-only views**
    - Although it is very convenient to fetch entities along with all their associated relationships, it’s better to take into consideration the performance impact as well. As previously explained, fetching too much data is not suitable because it increases the transaction response time.
    - As a rule of thumb, fetching entities is suitable when the logical transaction requires modifying them, even if that will only happen in a successive web request. With this is mind, it is much easier to reason on which fetching mechanism to employ for a given business logic use case.
  - **Secondary cache**
    - Although the second-level cache can mitigate the entity fetching performance issues, it requires a distributed caching implementation, which might not elude the networking penalties anyway.

## Mapping Types and Identifyers

- **Intro**
  - JPA uses three main Object-Relational mapping elements: **type**, **embeddable** and **entity**.
  - Although it’s common practice to map all database columns, this is not a strict requirement. Sometimes it’s more practical to use a root entity and several sub-entities, so each business case fetches just as much info as needed (while still benefiting from entity state management).
  - An embeddable type groups multiple properties in a single reusable component.
    ```java
    @Embeddable
    public class Change {
      @Column(name = "changed_on")
      private Date changedOn;

      @Column(name = "created_by")
      private String changedBy;
    }
    ```
  - The composition association, defined by UML, is the perfect analogy for the relationship between an entity and an embeddable. When an entity includes an embeddable type, all its properties become part of the owner entity. Lacking an identifier, the embeddable object cannot be managed by a Persistence Context, and its state is controlled by its parent entity.
- **Types**
  - **Primitive Types**
    ![DirtyRead](/Java/Hibernate/res/PrimitiveTypes.PNG)
    - From one database system to another, the boolean type can be represented either as a BIT, BYTE, BOOLEAN or CHAR database type, so defines four Type(s) to resolve the boolean primitive type.
    - **Only non-nullable database columns can be mapped to Java primitives!**
  - **String Types**
    ![DirtyRead](/Java/Hibernate/res/StringTypes.PNG)
  - **Date and Time types**
    ![DirtyRead](/Java/Hibernate/res/DateTime.PNG)
  - **Numeric Types**
    ![DirtyRead](/Java/Hibernate/res/NumericType.PNG)
  - **Binary Types**
    ![DirtyRead](/Java/Hibernate/res/BinaryTypes.PNG)
  - **UUID Types**
    ![DirtyRead](/Java/Hibernate/res/UUID.PNG)
  - **Other Types**
    ![DirtyRead](/Java/Hibernate/res/OtherTypes.PNG)
  - **Custom types**
    - See [here](https://vladmihalcea.com/how-to-implement-a-custom-basic-type-using-hibernate-usertype/)
- **Identifiers**
  - **UUID**
    - Fixed-size non-numerical keys (e.g. `CHAR`, `VARCHAR`) are less efficient than numerical ones (e.g. `INTEGER`, `BIGINT`) both for joining (a simple key performs better than a compound one) or indexing (the more compact the data type, the less memory space is required by an associated index).
    - Surrogate keys are generated independently of the current row data, so table column constraints may evolve with time (changing a user birthday or email address). The surrogate key can be generated by a numerical sequence generator (e.g. a database identity column or a sequence), or it can be constructed by a pseudorandom number generator (e.g GUID or UUID).
    - A `UUID` takes 128 bits, which is four times more than an `INTEGER` and twice as as `BIGINT`
    - Requiring less space and being more index-friendly, **numerical sequences are preferred over UUID keys**.
    - **MYQL** The UUID must be stored in a `BINARY(16)` column type.
      ```java
      @Entity @Table(name = "post") 
      public class Post {
        @Id @Column(columnDefinition = "BINARY(16)")
        @GeneratedValue(generator = "uuid2")
        @GenericGenerator(name = "uuid2", strategy = "uuid2")
        private UUID id;
      }
      ```
  - **Numerical Identifiers**
    - JPA defines the GenerationType9 enumeration for all supported identifier generator types:
      - `IDENTITY` is for mapping the entity identifier to a database identity column
      - `SEQUENCE` allocates identifiers by calling a given database sequence
      - `TABLE` is for relational databases that don’t support sequences (e.g MySQL 5.7), the table generator emulating a database sequence by using a separate table
      - `AUTO` decides the identifier generation strategy based on the current database dialect.
    - **Identity generator**
      - The only drawback is that the newly assigned value can only be known after executing the actual insert statement.
      - `@GeneratedValue(strategy = GenerationType.IDENTITY)`
    - **Sequence generator**
      - A sequence is a database object that generates consecutive numbers.
      - Advantages over identity:
        - the same sequence can be used to populate multiple columns, even across tables
        - values may be pre-allocated to improve performance
        - allowing incremental steps, sequences can benefit from application-level optimization techniques
      - `@GeneratedValue(strategy=GenerationType.SEQUENCE)`
      - When executing the `persist()` method, Hibernate calls the associated database sequence and fetches an identifier for the newly persisted entity. The actual insert statement is postponed until flush-time, which **allows Hibernate to take advantage of JDBC batching**.
    - **Table**
      - A database table is used to hold the latest sequence value and row-level locking is employed to prevent two concurrent connections from acquiring the same identifier value.
      - `@GeneratedValue(strategy=GenerationType.TABLE)`
      - Unlike identity columns and sequences, which can increment the sequence in a single request, the table generator entails a significant performance overhead.

## Relationships

- **Types**
  - `@ManyToOne` represents the child-side (where the foreign key resides) in a database one-to- many table relationship
  - `@OneToMany` is associated with the parent-side of a one-to-many table relationship
  - `@ElementCollection` defines a one-to-many association between an entity and multiple value types (basic or embeddable)
  - `@OneToOne` is used for both the child-side and the parent-side in a one-to-one table relationship
  - `@ManyToMany` mirrors a many-to-many table relationship.
- **Mapping large collections**
  - When handling large data sets, it’s good practice to limit the result set size, both for UI (to increase responsiveness) or batch processing tasks (to avoid long running transactions). Just because JPA offers supports collection mapping, it doesn’t mean they are mandatory for every domain model mapping. Until there’s a clear understanding of the number of child records (or if there’s even a need to fetch child entities entirely), it’s better to post pone the collection mapping decision. For high-performance systems, a data access query is often a much more flexible alternative anyway.
- **(Unidirectional) ManyToOne**
  - When using a @ManyToOne association, the underlying foreign key is controlled by the child-side, no matter the association is unidirectional or bidirectional.
  - In the example the `PostComment` class has a reference to `Post`:
    ```java
    @ManyToOne
    @JoinColumn(name = "post_id") private Post post;
    ```
- **OneToMany**
  - **Bidirectional @OneToMany**
    - The bidirectional `@OneToMany` association has a matching `@ManyToOne` child-side mapping that controls the underlying one-to-many table relationship.
      ```java
      @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true) private List<PostComment> comments = new ArrayList<>();
      ```
    - One of the major advantages of using a bidirectional association is that entity state transitions can be cascaded from the parent entity to its children. --> I.e. if a post has multiple comments upon calling the `persist` on the `Post` object, the `Comments` are also persisted to the DB.
  - **Unidirectional @OneToMany**
    ```java
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostComment> comments = new ArrayList<>();
    ```
    - Unfortunately, in spite its simplicity, the **unidirectional @OneToMany association is less efficient than the unidirectional @ManyToOne** mapping or the bidirectional `@OneToMany` association.
    - Hibernate uses a separate junction table to manage the association between a parent row and its child records.
    ![DirtyRead](/Java/Hibernate/res/OneToManyTable.PNG)
  - **@ElementCollection**
    - To represent collections of basic types (e.g. `String`, `int`, `BigDecimal`) or embeddable types, the `@ElementCollection` must be used instead.
      ```java
      @ElementCollection
      private List<String> comments = new ArrayList<>();
      ```
    - When it comes to adding or removing child records, the `@ElementCollection` behaves like a unidirectional `@OneToMany` relationship, annotated with `CascadeType.ALL` and orphanRemoval.
    ![DirtyRead](/Java/Hibernate/res/ElementCollection.PNG)
    - Unfortunately, the remove operation uses the same logic as the unidirectional `@OneToMany` association, so when removing the first collection element **Hibernate deletes all the associated child-side records and re-inserts the in-memory ones back into the database table**
  - **@OneToMany with @JoinColumn**
    - With the `@JoinColumn`, the `@OneToMany` association controls the child table foreign key so there is no need for a junction table.
    ```java
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "post_id")
    private List<PostComment> comments = new ArrayList<>();
    ```
    - **Although it’s an improvement over the regular @OneToMany mapping, in practice, it’s still not as efficient as a regular bidirectional @OneToMany association.**
- **OneToOne**
  - **Unidirectional @OneToOne**
      ```java
      @OneToOne
      @JoinColumn(name = "post_id")
      private Post post;
      ```
      ![DirtyRead](/Java/Hibernate/res/OneToOneTable.PNG)
  - **Bidirectional @OneToTone**
    ```java
    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PostDetails details;
    ```
- **ManyToMany**
  - **Unidirectional @ManyToMany**
    ```java
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE } )
    @JoinTable(name = "post_tag",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();
    ```
  - **Bidirectional @ManyToMany**
    ```java
    @ManyToMany(mappedBy = "tags")
    private List<Post> posts = new ArrayList<>();
    ```

## Inheritance