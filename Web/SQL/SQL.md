# SQL

#### Intro
- SQL = Structured Query Langauge
- RDBMS = Relational Database Management System.
- Usually UPPERCASE
- Semicolon `(;)` at the end.
- Comments: `/* MultiLineComment */` or `--OneLinerComment`


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
