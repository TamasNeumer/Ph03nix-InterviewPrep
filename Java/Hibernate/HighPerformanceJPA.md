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
