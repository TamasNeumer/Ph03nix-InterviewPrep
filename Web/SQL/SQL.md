# SQL

#### Intro
- SQL = Structured Query Langauge
- RDBMS = Relational Database Management System.
- Usually UPPERCASE
- Semicolon `(;)` at the end.
- Comments: `/* MultiLineComment */` or `--OneLinerComment`
- Strings should be between single quites `'value'`


#### Select
- `*` to select all
- otherwise name columns

```sql
SELECT column1, column2, ...
FROM table_name;
```

- SELECT **DISTINCT** to select only different values.

```sql
SELECT Country FROM Customers;
SELECT DISTINCT Country FROM Customers;
```

- SELECT ... FROM ... **WHERE** to specify a condition
  - Allowed operators in the where clause:
    - `=` (Equal)
    - `<>` (Not equal. Some SQL versions support the != notation)
    - `>` (Greater than)
    - `<` (Less than)
    - `>=` (Greater than or Equal)
    - `<=` (Less than or Equal)
    - `BETWEEN` (Between an inclusive range)
    - `LIKE` (Search for a pattern)
    - `IN` (to specify multiple possible values for a column)
  - Conditions can be combined with `AND`, `OR`, `NOT` operators.

- **ORDER BY (ASC|DESC)**
  - Ascending order by default.

```sql
SELECT * FROM Customers
WHERE Country='Mexico';

SELECT column1 FROM table_name WHERE condition1 AND condition2;
SELECT column1 FROM table_name WHERE condition1 OR condition2;
SELECT column1 FROM table_name NOT condition1

SELECT column1 FROM table_name ORDER BY column2 DESC;
```

- **NULL** is where there was no value specified. Testing for `null` happens via:

```SQL
SELECT column1 FROM table_name WHERE column2 IS NULL;
SELECT column1 FROM table_name WHERE column2 IS NOT NULL;
```

- **TOP, LIMIT, RWONUM**
  - Use to limit the number of returned entries.
  - Not all database systems support the SELECT TOP clause. MySQL supports the LIMIT clause to select a limited number of records, while Oracle uses ROWNUM.

```SQL
SELECT TOP 3 * FROM Customers;
SELECT TOP 3 * FROM Customers WHERE Country='Germany';

SELECT * FROM Customers LIMIT 3;
SELECT * FROM Customers WHERE Country='Germany' LIMIT 3;

SELECT * FROM Customers WHERE ROWNUM <= 3;
SELECT * FROM Customers WHERE Country='Germany' AND ROWNUM <= 3;

SELECT TOP 50 PERCENT * FROM Customers;
```

- **MIN MAX**
  - Select the minimum or maximum element.


```SQL
SELECT MIN(column_name) FROM table_name WHERE condition;
SELECT MAX(column_name) FROM table_name WHERE condition;
```

- **COUNT AVG SUM**
  - Number of elements, avg of elements, sum of elements

```SQL
SELECT COUNT(column_name) FROM table_name WHERE condition;
SELECT AVG(column_name) FROM table_name WHERE condition;
SELECT SUM(column_name) FROM table_name WHERE condition;
```

- **LIKE**
  - The LIKE operator is used **in a WHERE clause** to search for a specified pattern in a column.
  - `%` - The percent sign represents zero, one, or multiple characters
  - `_` - The underscore represents a single character
    - MS Access uses a question mark (?) instead of the underscore `_`.
  - `[abc]` - just like in regex specifies a char list

```SQL
SELECT column1, column2 FROM table_name WHERE column LIKE pattern;
WHERE CustomerName LIKE 'a%' --begins with a
WHERE CustomerName LIKE '%or%' --contains "or"
WHERE CustomerName LIKE '_r%' --r is the second chars
WHERE CustomerName LIKE 'a_%_%' --starts with a, lasts 3 chars
WHERE City LIKE '[a-c]%'; --city starts with a or b or c
WHERE City LIKE '[!bsp]%'; --city NOT starting with b or s or p
WHERE City NOT LIKE '[bsp]%'; --city NOT starting with b or s or p
```

- **IN**
  - The IN operator allows you to specify multiple values in a WHERE clause. The IN operator is a shorthand for multiple OR conditions.

```SQL
SELECT column_name(s) FROM table_name WHERE column_name IN (value1, value2, ...);
SELECT column_name(s) FROM table_name WHERE column_name IN (SELECT STATEMENT);

SELECT * FROM Customers WHERE Country IN ('Germany', 'France', 'UK');
SELECT * FROM Customers WHERE Country IN (SELECT Country FROM Suppliers);
```

- **BETWEEN**
  - The BETWEEN operator selects values within a given range. The values can be numbers, text, or dates.

```SQL
SELECT column_name(s) FROM table_name WHERE column_name BETWEEN value1 AND value2;

SELECT * FROM Products WHERE Price BETWEEN 10 AND 20;
```

**SQL Aliases**
- SQL aliases are used to give a table, or a column in a table, a temporary name.
- Requires "[ ]" if the alias contains space.

```sql
SELECT column_name AS alias_name FROM table_name;

SELECT CustomerID as ID, CustomerName AS Customer FROM Customers;

SELECT CustomerName, Address + ', ' + PostalCode + ' ' + City + ', ' +
Country AS Address FROM Customers;
```

#### Joins
- **SQL Join**
  - A JOIN clause is used to combine rows from two or more tables, based on a related column between them.

- **Inner Join**
  - Returns records that have matching values in both tables. (A AND B)
  - Note: The INNER JOIN keyword selects all rows from both tables as long as there is a match between the columns. If there are records in the "Orders" table that do not have matches in "Customers", these orders will not be shown!
  - table_orders: orderID, customerID. table_customers: customerID, customer name. --> list orderID with customerName!

```sql
SELECT column_name(s) FROM table1 INNER JOIN table2 ON table1.column_name = table2.column_name;

SELECT Orders.OrderID, Customers.CustomerName FROM Orders
INNER JOIN Customers ON Orders.CustomerID = Customers.CustomerID;
```

- **LEFT Join**
  - The LEFT JOIN keyword returns all records from the left table (table1), and the matched records from the right table (table2). The result is NULL from the right side, if there is no match.
  - Full list of customers. If customers didn't order anything there is "null" in the order column. If a customer ordered multiple times, his name is listed multiple times, with the corresponding orderIDs.

```sql
SELECT column_name(s) FROM table1
LEFT JOIN table2 ON table1.column_name = table2.column_name;

SELECT Customers.CustomerName, Orders.OrderID FROM Customers
LEFT JOIN Orders ON Customers.CustomerID=Orders.CustomerID
ORDER BY Customers.CustomerName;
```

- **RIGHT Join**
  - The RIGHT JOIN keyword returns all records from the right table (table2), and the matched records from the left table (table1). The result is NULL from the left side, when there is no match.
  - List all orders with the name of who ordered. (in this case no null as all orders should have a buyer.)

```sql
SELECT column_name(s) FROM table1
RIGHT JOIN table2 ON table1.column_name = table2.column_name;

SELECT Orders.OrderID, Employees.LastName, Employees.FirstName
FROM Orders RIGHT JOIN Employees ON Orders.EmployeeID = Employees.EmployeeID
ORDER BY Orders.OrderID;
```

- **FULL Join**
  - The FULL OUTER JOIN keyword return all records when there is a match in either left (table1) or right (table2) table records.
  - List all orders with the name of who ordered. (in this case no null as all orders should have a buyer.)
  - Thus both columns can have null.

```sql
SELECT column_name(s) FROM table1
FULL OUTER JOIN table2 ON table1.column_name = table2.column_name;

SELECT Customers.CustomerName, Orders.OrderID FROM Customers
FULL OUTER JOIN Orders ON Customers.CustomerID=Orders.CustomerID
ORDER BY Customers.CustomerName;
```

- **SELF Join**
  - A self JOIN is a regular join, but the table is joined with itself.
  - Select people from the same city (but not same ID)

```sql
SELECT column_name(s) FROM table1 T1, table1 T2 WHERE condition;

SELECT A.CustomerName AS CustomerName1, B.CustomerName AS CustomerName2, A.City
FROM Customers A, Customers B
WHERE A.CustomerID <> B.CustomerID
AND A.City = B.City
ORDER BY A.City;
```

#### Other fancy stuff
  - **UNION**
    - The `UNION` operator is used to combine the result-set of two or more SELECT statements.
      - Each SELECT statement within UNION must have the same number of columns
      - The columns must also have similar data types
      - The columns in each SELECT statement must also be in the same order.
    - The UNION operator selects only distinct values by default. To allow duplicate values, use `UNION ALL`.
    - E.g. Union --> List cities of customers + suppliers

```sql
SELECT column_name(s) FROM table1 UNION SELECT column_name(s) FROM table2;
SELECT column_name(s) FROM table1 UNION ALL SELECT column_name(s) FROM table2;

SELECT City FROM Customers UNION SELECT City FROM Suppliers ORDER BY City;
SELECT City FROM Customers UNION ALL SELECT City FROM Suppliers ORDER BY City;
```

- **GROUP BY**
  - The GROUP BY statement is often used with aggregate functions (COUNT, MAX, MIN, SUM, AVG) to group the result-set by one or more columns.
  - In the example it sums the number of users per country.
```sql
SELECT COUNT(CustomerID), Country FROM Customers GROUP BY Country;
```

- **HAVING**
  - The HAVING clause was added to SQL because the WHERE keyword could not be used with aggregate functions. (Group by etc.)

```sql
SELECT column_name(s) FROM table_name
WHERE condition GROUP BY column_name(s)
HAVING condition ORDER BY column_name(s);

SELECT COUNT(CustomerID), Country FROM Customers GROUP BY Country
HAVING COUNT(CustomerID) > 5;
```

- **EXISTS**
  - The EXISTS operator is used to test for the existence of any record in a subquery.
  - Selects those suppliers that have products under 20$.

```sql
SELECT column_name(s) FROM table_name
WHERE EXISTS (SELECT column_name FROM table_name WHERE condition);

SELECT SupplierName FROM Suppliers
WHERE EXISTS (SELECT ProductName FROM Products WHERE SupplierId = Suppliers.supplierId AND Price < 20);
```

- **ANY, ALL**
  - The ANY and ALL operators are used with a WHERE or HAVING clause.
  - he ANY operator returns true if any of the subquery values meet the condition.
  - The ALL operator returns true if all of the subquery values meet the condition.

```sql
SELECT column_name(s) FROM table_name WHERE column_name operator ANY
(SELECT column_name FROM table_name WHERE condition);
SELECT column_name(s) FROM table_name WHERE column_name operator ALL
(SELECT column_name FROM table_name WHERE condition);

SELECT ProductName FROM Products
WHERE ProductID = ANY (SELECT ProductID FROM OrderDetails WHERE Quantity = 10);
SELECT ProductName FROM Products
WHERE ProductID = ALL (SELECT ProductID FROM OrderDetails WHERE Quantity = 10);
```

#### Insert
- Name the columns and the corresponding values (unspecified fields will be `null`)
- Name all values in the order of the columns.

```SQL
INSERT INTO table_name (column1, column2) VALUES (value1, value2);
INSERT INTO table_name VALUES (value1, value2);
```
- **SELECT INTO**
  - The SELECT INTO statement copies data from one table into a new table.
  - `SELECT * INTO CustomersBackup2017 FROM Customers;`

- **INSERT INTO SELECT**
  - The INSERT INTO SELECT statement copies data from one table and inserts it into another table.
    - INSERT INTO SELECT requires that data types in source and target tables match
    - The existing records in the target table are unaffected

```sql
SELECT * INTO newtable [IN externaldb] FROM oldtable WHERE condition;
SELECT * INTO CustomersBackup2017 FROM Customers;

INSERT INTO table2 SELECT * FROM table1 WHERE condition;
```

- **IFNULL(Mysql) ISNULL(SQLServer) COALESCE(MSaccess) NVL(Oracle)**
  - `IFNULL()` function lets you return an alternative value if an expression is NULL:
    - `SELECT ProductName, UnitPrice * (UnitsInStock + IFNULL(UnitsOnOrder, 0)) FROM Products`


#### Update
- The UPDATE statement is used to modify the existing records in a table.
- It is the WHERE clause that determines how many records that will be updated.
  - ID = 1 --> Will update only one (if ID is unique...)
  - Country='Mexico' --> Will update all entries where country was Mexico.
  - If you omit the where clause **all** records will be updated!!!

```SQL
UPDATE table_name SET column1 = value1, column2 = value2 WHERE condition;
```

#### Delete
- The DELETE statement is used to delete existing records in a table.
- The WHERE clause specifies which record(s) that should be deleted. If you omit the WHERE clause, **all** records in the table will be deleted!
- Use rather an active/archived value to indicate. Once deleted you can never retrieve values.

```SQL
DELETE FROM table_name WHERE condition;
DELETE * FROM table_name;
```

#### Database operations
- **CREATE DATABASE**
  - The CREATE DATABASE statement is used to create a new SQL database.
- **DROP DATABASE**
  - The DROP DATABASE statement is used to drop an existing SQL database.
- **CREATE TABLE**
  - The CREATE TABLE statement is used to create a new table in a database.
- **DROP TABLE**
  - The DROP TABLE statement is used to drop an existing table in a database.
- **ALTER TABLE**
  - The ALTER TABLE statement is used to add, delete, or modify columns in an existing table.


```sql
CREATE DATABASE databasename;
DROP DATABASE databasename;

CREATE DATABASE testDB;
DROP DATABASE testDB;

CREATE TABLE table_name (column1 datatype, column2 datatype);
DROP TABLE table_name;

CREATE TABLE Persons (PersonID int, LastName varchar(255));
DROP TABLE Shippers;

ALTER TABLE table_name ADD column_name datatype;
ALTER TABLE table_name DROP COLUMN column_name;
```

#### Constrains
- Constraints can be specified when the table is created with the CREATE TABLE statement, or after the table is created with the ALTER TABLE statement.

```sql
CREATE TABLE table_name (column1 datatype constraint);
```

- **NOT NULL** Ensures that a column cannot have a NULL value
- **UNIQUE** Ensures that all values in a column are different
  - Both the UNIQUE and PRIMARY KEY constraints provide a guarantee for uniqueness for a column or set of columns. A PRIMARY KEY constraint automatically has a UNIQUE constraint. However, you can have many UNIQUE constraints per table, but only one PRIMARY KEY constraint per table.
- **PRIMARY KEY** A combination of a NOT NULL and UNIQUE. Uniquely identifies each row in a table. A FOREIGN KEY is a field (or collection of fields) in one table that refers to the PRIMARY KEY in another table. The table containing the foreign key is called the child table, and the table containing the candidate key is called the referenced or parent table.
- **FOREIGN KEY** Uniquely identifies a row/record in another table.
  - A FOREIGN KEY is a key used to link two tables together.
- **CHECK** Ensures that all values in a column satisfies a specific condition
- **DEFAULT** Sets a default value for a column when no value is specified
- **INDEX** Use to create and retrieve data from the database very quickly.
  - Indexes are used to retrieve data from the database very fast. The users cannot see the indexes, they are just used to speed up searches/queries.
  - Note: Updating a table with indexes takes more time than updating a table without (because the indexes also need an update). So, only create indexes on columns that will be frequently searched against.
- **AUTO INCREMENT**
  - Auto-increment allows a unique number to be generated automatically when a new record is inserted into a table.
  - Often this is the primary key field that we would like to be created automatically every time a new record is inserted.

```sql
CREATE TABLE Persons (ID int NOT NULL, Age int, UNIQUE (ID));

CREATE TABLE Persons (ID int NOT NULL, Age int, PRIMARY KEY (ID));

CREATE TABLE Orders (OrderID int NOT NULL, OrderNumber int NOT NULL,
    PersonID int, PRIMARY KEY (OrderID),
    FOREIGN KEY (PersonID) REFERENCES Persons(PersonID));

CREATE TABLE Persons (ID int NOT NULL, Age int, CHECK (Age>=18));
ALTER TABLE Persons ADD CHECK (Age>=18);

CREATE TABLE Orders (ID int NOT NULL, OrderDate date DEFAULT GETDATE());

CREATE INDEX index_name ON table_name (column1, column2, ...);
CREATE INDEX idx_lastname ON Persons (LastName);
CREATE INDEX idx_pname ON Persons (LastName, FirstName); --Index on combination of columns
DROP INDEX index_name ON table_name; --drop index

CREATE TABLE Persons (ID int NOT NULL AUTO_INCREMENT, Age int, PRIMARY KEY (ID));
ALTER TABLE Persons AUTO_INCREMENT=100;
```

#### Dates
- The most difficult part when working with dates is to be sure that the format of the date you are trying to insert, matches the format of the date column in the database.
- As long as your data contains only the date portion, your queries will work as expected. However, if a time portion is involved, it gets more complicated.
- MySQL vs SQL server comes with slightly different types.
- MySQL:
  - DATE - format YYYY-MM-DD
  - DATETIME - format: YYYY-MM-DD HH:MI:SS
  - TIMESTAMP - format: YYYY-MM-DD HH:MI:SS
  - YEAR - format YYYY or YY

#### View
- In SQL, a view is a virtual table based on the result-set of an SQL statement.
- Views are also useful when transitioning from OLD to new DB. You code is littered with references to `T_OLD`. You create T_NEW that contains additional stuff and create a VIEW `T_OLD` that mimics the old table. Thus your stuff keeps working... (?!)

```sql
CREATE VIEW view_name AS SELECT column1, column2, ... FROM table_name WHERE condition;
```

#### SQL Injection
- SQL injection usually occurs when you ask a user for input, like their username/userid, and instead of a name/id, the user gives you an SQL statement that you will unknowingly run on your database.

```sql
txtUserId = getRequestString("UserId");
txtSQL = "SELECT * FROM Users WHERE UserId = " + txtUserId;
SELECT * FROM Users WHERE UserId = 105 OR 1=1;
```

- To protect a web site from SQL injection, you can use SQL parameters. SQL parameters are values that are added to an SQL query at execution time, in a controlled manner.

```sql
txtUserId = getRequestString("UserId");
txtSQL = "SELECT * FROM Users WHERE UserId = @0";
db.Execute(txtSQL,txtUserId);
```
