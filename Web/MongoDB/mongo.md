# MongoDB

## NoSQL vs SQL

- With traditional SQL Databases you have to know in advance what you want to store, create the schema, and fill it with data. If you want to change the schema, you will need to migrate your data, which can be tedious with larger databases.
- NoSQL databases, usually support auto-sharding, meaning that they natively and automatically spread data across an arbitrary number of servers, without requiring the application to even be aware of the composition of the server pool.

## General Intro

- MongoDB is an open-source document database and leading NoSQL database. MongoDB is written in C++
- **Database** is a physical container for collections. Each database gets its own set of files on the file system. A single MongoDB server typically has multiple databases.
- **Collection** is a group of MongoDB documents.
- **Document** stores all relevant data together in single 'document' in JSON, XML, or another format, which can nest values hierarchically. (For MongoDB it is JSON.) each json record must have a unique id member. If not provided, MongoDB automatically assigns one to each record.

| **RDBMS**   | **MongoDB**                                             |
| ----------- | ------------------------------------------------------- |
| Database    | Database                                                |
| Table       | Collection                                              |
| Tuple/Row   | Document                                                |
| column      | Field                                                   |
| Table Join  | Embedded Documents                                      |
| Primary Key | Primary Key (Default key id provided by mongodb itself) |

- Data is stored on the disk, typically at /data/db (it can be overridden)

## Connection pooling (NodeJs Driver)

- Building up a connection for each request is expensive.
- By default the driver is configured to pool 5 connections. Overriding this is simplay as follows:

  ```js
  // This is a global variable we'll use for handing the MongoDB client
  var mongodb;

  // Create the database connection
  MongoClient.connect(url, {  
    poolSize: 10
    // other options can go here
  },function(err, db) {
      assert.equal(null, err);
      mongodb=db;
      }
  );

  // Use the global mongodb variable in routes!
  ```

## Reconnect to database

- `autoReconnect` - defaults to `true` Reconnect on error
- `reconnectTries` - defaults to 30
- `reconnectInterval` - in ms, defaults to `1000`
- Taken the previous example just add the properties to the config object


## Short Intro with Example Codes

To communicate with mongo in node.js use the mongo client library:

```js
var MongoClient = require("mongodb");
```

The mongo client library works in a **non-blocking asynchronous manner**. **For every operation you have to provide a callback function, which is called when te operation completes.**

### Create and Connect to database

Creating a Database. Save it as demo_create_mongo_db.js and run it from node. To create a database in MongoDB, start by creating a MongoClient object, then specify a connection URL with the correct ip address and the name of the database you want to create. MongoDB will create the database if it does not exist, and make a connection to it.  
Db can be reused across operations. Db must be closed at the end of usage.

```js
var MongoClient = require("mongodb").MongoClient;
var url = "mongodb://localhost:27017/mydb";

// Create Database
MongoClient.connect(
  url,
  function(err, db) {
    if (err) throw err;
    console.log("Database created!");
    db.close();
  }
);
```

### Create collection

To create a table in MongoDB, use the createCollection() method:

```js
MongoClient.connect(
  url,
  function(err, db) {
    db.createCollection("customers", function(err, res) {
      if (err) throw err;
      console.log("Collection created!");
      db.close();
    });
  }
);
```

### Insert data into collection

To insert a record into a table in MongoDB, we use the insertOne() method. The first parameter of the insertOne() method is an object containing the name(s) and value(s) of each field in the record you want to insert.

```js
MongoClient.connect(url, function(err, db) {
  // Insert record
  var myobj = { name: "Company Inc", address: "Highway 37" };
  db.collection("customers").insertOne(myobj, function(err, res) {
    if (err) throw err;
    console.log("1 record inserted");
    db.close();
  });
});

res: { result: {ok, n},  // ok is 0 or 1, n is number of insertions
        ps,              // array of inserted items
        insertedCount,   // number of insertions
        insertedIds      // array of ids
      }
```

To insert a bulk of data:

```js
// Insert many
MongoClient.connect(
  url,
  function(err, db) {
    if (err) throw err;
    var myobj = [
      { name: "John", address: "Highway 71" },
      { name: "Peter", address: "Lowstreet 4" },
      { name: "Amy", address: "Apple st 652" }
    ];
    db.collection("customers").insertMany(myobj, function(err, res) {
      if (err) throw err;
      console.log("Number of records inserted: " + res.insertedCount);
      db.close();
    });
  }
);
```

### Find data / query:

The `findOne()` method returns the first occurrence in the selection. The first parameter of the findOne() method is a query object. In this example we use an empty query object, which selects all records in a table (but returns only the first record).

```js
// The findOne() method returns the first occurrence in the selection.
MongoClient.connect(
  url,
  function(err, db) {
    if (err) throw err;
    db.collection("customers").findOne({}, function(err, result) {
      if (err) throw err;
      console.log(result.name);
      db.close();
    });
  }
);
```

To find multiple entries / query use the `find()` method.

```js
MongoClient.connect(
  url,
  function(err, db) {
    if (err) throw err;
    db.collection("customers")
      .find({})
      .toArray(function(err, result) {
        if (err) throw err;
        console.log(result);
        console.log(result[2].address);
        db.close();
      });
  }
);
```

### Filtering data

When selecting records from a table, you can filter the result by using a query object.
--> Select records with the address "Park Lane 38":

```js
MongoClient.connect(
  url,
  function(err, db) {
    if (err) throw err;
    var query = { address: "Park Lane 38" };
    db.collection("customers")
      .find(query)
      .toArray(function(err, result) {
        if (err) throw err;
        console.log(result);
        db.close();
      });
  }
);
```

You can also use regex for Querry / Filter:

```js
MongoClient.connect(
  url,
  function(err, db) {
    if (err) throw err;
    var query = { address: /^S/ };
    db.collection("customers")
      .find(query)
      .toArray(function(err, result) {
        if (err) throw err;
        console.log(result);
        db.close();
      });
  }
);
```

### Sort

You can create your own object, that will be used for sorting the found records.

```js
MongoClient.connect(
  url,
  function(err, db) {
    if (err) throw err;
    var mysortAsc = { name: 1 };
    var mysortDesc = { name: -1 };
    db.collection("customers")
      .find()
      .sort(mysortAsc)
      .toArray(function(err, result) {
        if (err) throw err;
        console.log(result);
        db.close();
      });
  }
);
```

### Deleting entries and collections

`DeleteOne()` will delete the first occurrence of the search.

```js
MongoClient.connect(
  url,
  function(err, db) {
    if (err) throw err;
    var myquery = { address: "Mountain 21" };
    db.collection("customers").deleteOne(myquery, function(err, obj) {
      if (err) throw err;
      console.log("1 document deleted");
      db.close();
    });
  }
);
```

Delete table / collection:  
You can delete a table, or collection as it is called in MongoDB, by using the `drop()` method.  
You can also use the d`ropCollection()` method to delete a table (collection). The `dropCollection()` method takes two parameters: the name of the collection and a callback function.

```js
MongoClient.connect(
  url,
  function(err, db) {
    if (err) throw err;
    db.collection("customers").drop(function(err, delOK) {
      if (err) throw err;
      if (delOK) console.log("Table deleted");
      db.close();
    });
  }
);

MongoClient.connect(
  url,
  function(err, db) {
    if (err) throw err;
    db.dropCollection("customers", function(err, delOK) {
      if (err) throw err;
      if (delOK) console.log("Table deleted");
      db.close();
    });
  }
);
```

### Update

You can update a record, or document as it is called in MongoDB, by using the updateOne() method.

```js
MongoClient.connect(
  url,
  function(err, db) {
    if (err) throw err;
    var myquery = { address: "Valley 345" };
    var newvalues = { name: "Mickey", address: "Canyon 123" };
    db.collection("customers").updateOne(myquery, newvalues, function(err, res) {
      if (err) throw err;
      console.log("1 record updated");
      db.close();
    });
  }
);
```

To update specific fields:

```js
var myquery = { address: "Valley 345" };
var newvalues = { $set: { address: "Canyon 123" } };
```

### Limit

Use it to limit the number of returned elements

```js
MongoClient.connect(
  url,
  function(err, db) {
    if (err) throw err;
    db.collection("customers")
      .find()
      .limit(5)
      .toArray(function(err, result) {
        if (err) throw err;
        console.log(result);
        db.close();
      });
  }
);
```

### Join

MongoDB is not a relational database, but you can perform a left outer join by using the $lookup stage. The$lookup stage lets you specify which collection you want to join with the current collection, and which fields that should match. Consider you have a "orders" collection and a "products" collection:

```js
[{ _id: 1, product_id: 154, status: 1 }][
  ({ _id: 154, name: "Chocolate Heaven" },
  { _id: 155, name: "Tasty Lemons" },
  { _id: 156, name: "Vanilla Dreams" })
];

// Join the matching "products" document(s) to the "orders" collection:
MongoClient.connect(
  url,
  function(err, db) {
    if (err) throw err;
    db.collection("orders").aggregate(
      [
        {
          $lookup: {
            from: "products",
            localField: "products_id",
            foreignField: "id",
            as: "orderdetails"
          }
        }
      ],
      function(err, res) {
        if (err) throw err;
        console.log(res);
        db.close();
      }
    );
  }
);

// Result:
[{ _id: 1, product_id: 154, status: 1, orderdetails: [{ _id: 154, name: "Chocolate Heaven" }] }];
```

## Closing remarks and hints

- Callbacks are carried out out-of-order!!!
- All APIs return promises, if they aren't provided with a callback
