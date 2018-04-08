# JDBC

#### Introducing Relational Databases and SQL
- **Basics**
  - A *relational database* is a database that is organized into tables, which consist of rows and columns.
  - There are two main ways to access a relational database from Java:
    - **Java Database Connectivity Language (JDBC)**: Accesses data as rows and columns.
    - **Java Persistence API (JPA)**: Accesses data through Java objects using a concept called object-relational mapping (ORM).
  - A relational database is accessed through *Structured Query Language (SQL)*.
- **Identifying the Structure of a Relational Database**
  - Each table has a *primary key*, which gives us a unique way to reference each row.
  - Tables have *rows* and *columns* that store data, kind of like an excel sheet.
  - *Relational database*, hence the relation between the tables is important.
- **Writing Basic SQL Statements**
  - Four type of statements: `SELECT, INSERT, UPDATE, DELETE`
  - Unlike Java, SQL keywords are **case insensitive**.
    - Capitals for SQL keywords
    - Underscores `_` to separate words in column names.
  - Inserting example:
    - `INSERT INTO species VALUES (3, 'Asian Elephant', 7.5);`
  - Select example:
    - `SELECT * FROM SPECIES WHERE ID = 3;`
  - Update example:
    - `UPDATE SPECIES SET NUM_ACRES = NUM_ACRES + .5 WHERE NAME = 'Asian Elephant';`
  - Delete example:
    - `DELETE FROM SPECIES WHERE NAME = 'Asian Elephant';`

#### Introducing the Interfaces of JDBC

- All database classes are in the `package java.sql`
- The four interfaces:
  - `Driver`: Knows how to get a connection to the database
  - `Connection`: Knows how to communicate with the database
  - `Statement`: Knows how to run the SQL
  - `ResultSet`: Knows what was returned by a SELECT query
- The standard (in production not recommended) way of usage would be something like that:

    ```java
    public static void main(String[] args) throws SQLException {
      String url = "jdbc:derby:zoo";
      try (Connection conn = DriverManager.getConnection(url);
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery("select name from animal")) {
        while (rs.next())
          System.out.println(rs.getString(1));
        }
    }
    ```

#### Connecting to a Database

- **Building a JDBC URL**
  - `jdbc:postgres://localhost:5432/zoo`
    - Protocol (in this case jdbc)
    - Product/vendor name (postgres)
    - Database Specific Connection Details
- **Getting a Database Connection**
  - There are two main ways to get a Connection: `DriverManager` or `DataSource`.
    - Do not use a `DriverManager` in code someone is paying you to write!
  - The `DriverManager` class is in the JDK, as it is an API that comes with Java. It uses the factory pattern, which means that you call a `static` method to get a `Connection`.
  - `Connection conn = DriverManager.getConnection("jdbc:derby:zoo");`
    - Don't forget to execute your code with the specified driver library! `java -cp "<java_home>/db/lib/ derby.jar:." TestConnect.`
    - The class is a **vendor implementation** of `Connection`. If printed to the output: `org.apache.derby.impl.jdbc.EmbedConnection40@1372082959`.
  - Unless the exam specifies a command line, you can assume that the correct JDBC driver JAR is in the classpath.
  - In some earlier code you might see explicit checks for the presence of the driver libraries. Something like `ClassNotFoundException { Class.forName("org.postgresql.Driver"); /* Connection creation */ }`
    - Notice the outer exception. Interesting syntax.
    - It is because the previous JDBC versions required this explicit search.

#### Obtaining a Statement
- **Obraining the statement**
  - `Statement stmt = conn.createStatement();`
  - `ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);`
- **Choosing result type**
  - By default, a ResultSet is in `TYPE_FORWARD_ONLY` mode. This is what you need most of the time. You can go through the data once in the order in which it was retrieved.
  - `TYPE_SCROLL_INSENSITIVE`: you have a static view of what the ResultSet looked like when you did the query
  - `TYPE_SCROLL_SENSITIVE`:  you would see the latest data when scrolling through the `ResultSet`, even if in the meantime the data was changed.
  - Both allow you to go through the data in any order. You can go both forward and backward. You can even go to a specific spot in the data. Think of this like scrolling in a browser.
  - Most databases and database drivers don’t actually support the `TYPE_SCROLL_SENSITIVE` mode.
- **Choosing a ResultSet Concurrency Mode**
  - By default, a ResultSet is in `CONCUR_READ_ONLY` mode. It means that you can’t update the result set.
  - `CONCUR_UPDATABLE` lets you to modify the table via the result. (Usually not supported.)

#### Executing a Statement

- **executeUpdate, executeQuery, execute**
  - `int executeUpdate()` returns the number of rows that were inserted, deleted, or changed.
    - `int result = stmt.executeUpdate("insert into species values(10, 'Deer', 3)");`
    - `result = stmt.executeUpdate("update species set name = '' where name = 'None'");`
  - `ResultSet executeQuery()` returns the result set containing the matching entries.
    - `ResultSet rs = stmt.executeQuery("select * from species");`
  - `boolean execute()` - can run either a query or an update.
    - If sql is a SELECT, the boolean is `true` and we can get the ResultSet (`ResultSet rs = stmt.getResultSet();`). If it is not a SELECT, we can get the number of rows updated (`int result = stmt.getUpdateCount();`).
  - If you pass the wrong string for an update (`int result = stmt.executeUpdate("select * from animal");`) you get a runtime exception.

#### Getting Data from a ResultSet
- **Reading a ResultSet**
  - The following demonstrates how you can retrieve data:

    ```java
    Map<Integer, String> idToNameMap = new HashMap<>();
    ResultSet rs = stmt.executeQuery("select id, name from species");
    while(rs.next()) {
      int id = rs.getInt("id");
      String name = rs.getString("name");
      idToNameMap.put(id, name);
    }
    System.out.println(idToNameMap); // {1=African Elephant, 2=Zebra}
    ```
  - There is another way to access the columns. You can use an index instead of a column name. (`int id = rs.getInt(1);` etc)
    - **Remember that JDBC starts counting with one rather than zero.**
  - Attempting to access a column that does not exist throws a `SQLException`, as does getting data from a ResultSet when it isn’t pointing at a valid row.
  - Also you **must** call `rs.next()` otherwise the result set cursor is still pointing to a location before the first row.
- **Getting Data for a Column**
  - Remember the following functions for the exam. Note that they return primitives!
    - `getBoolean, getDate, getDouble, getInt, getLong, getObject, getString, getTime, getTime`
    - `getFloat, getByte` (don't need to know for exam)
    - There is no `getChar`
  - Note that the returned dates are `java.sql.Date`, so you need to convert it to the standard `LocalDate` type!
    - `java.sql.Date sqlDate = rs.getDate(1); LocalDate localDate = sqlDate.toLocalDate();` -- date
    - `java.sql.Time sqlTime = rs.getTime(1); LocalTime localTime = sqlTime.toLocalTime();` -- time
    - `java.sql.Timestamp sqlTimeStamp = rs.getTimestamp(1); LocalDateTime localDateTime = sqlTimeStamp.toLocalDateTime();` - datetime
  - `getObject` method can return any type. For primitives it uses a wrapper class!
    - `Object idField = rs.getObject("id"); int id = (Integer) idField;`
- **Scrolling ResultSet**
  - `previous()` does the exact opposite of `next()`
  - The `first()` and `last()` methods return a `boolean` for whether they were successful at finding a row.
  - The `beforeFirst()` and `afterLast()` methods have a return type of `void`, since it is always possible to get to a spot that doesn’t have data.
  - The ideas is that you set the pointer to `afterLast`, call `previous` while checking the boolean result and then retrieve the data.
  - `absolute(int rowNr)` takes the pointer to a given row. The 0-th row, is the "header", the 1st one is the 1st row containing the values.
    - A negative number means to start counting from the end of the ResultSet rather than from the beginning. --> -1 points to the last row, -2 to the one before the last etc.
  - `relative(int moveCount)` method that moves forward or backward the requested number of rows. (Accepts positive as well as negative integers.)

#### Closing Database Resources

- You need to close the connection otherwise you have a resource leak.
- Strategies
  - Enclose the creation in a try-with-resources block
  - Or close them manually. First comes the `ResultSet`, then the `Statement`, and last the `Connection`.
    - Closing a `Connection` also closes the `Statement` and `ResultSet`.
    - Closing a `Statement` also closes the `ResultSet`.
    - JDBC automatically closes a `ResultSet` when you run another SQL statement from the same `Statement`.

#### Dealing with Exceptions

- The `e.getMessage()` method returns a human-readable message as to what went wrong.
- The `e.getSQLState()` method returns a code as to what went wrong.
- The `e.getErrorCode()` is a database-specific code.

#### Learnings

- The `Driver`, `Connection`, `Statement`, and `ResultSet` interfaces are part of the JDK. The JDK has a concrete implementation for `Driver` but the `Connection` and `Statement` must be implemented by the given vendor.
- A JDBC URL has three parts.
  - The first part is the string jdbc.
  - The second part is the vendor/product name.
  - The third part is database specific, but it includes a database name. The location, such as IP address and port, is **optional**.
- Starting with JDBC 4.0, driver implementations were required to provide the name of the class implementing Driver in a file named `java.sql.Driver` in the directory `METAINF/service` Hence the file `META-INF/service/java.sql.Driver` is mandatory.
- A Connection is created using a static method on DriverManager. It does not use a constructor!
- `DriverManager.getConnection()` throws a `SQLException` when the driver cannot be found, while `Class.forName()` throws a `ClassNotFoundException` if the driver was not found.
- When a `Statement` is requested with an unsupported mode, the JDBC driver will downgrade the request to one that is supported.
- In a `ResultSet`, columns are indexed starting with 1, not 0.
- By default, a `Statement` is not scrollable. The first call to `previous()` throws a `SQLException` because the `ResultSet` type is `TYPE_FORWARD_ONLY`.