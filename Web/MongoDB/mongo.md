# MongoDB
## General
- Intro
  - MongoDB is an open-source document database and leading NoSQL database. MongoDB is written in C++
  - **Database**  is a physical container for collections. Each database gets its own set of files on the file system. A single MongoDB server typically has multiple databases.
  - **Collection** is a group of MongoDB documents.
  - **Document** is a set of key-value pairs. Documents have dynamic schema. Dynamic schema means that documents in the same collection do not need to have the same set of fields or structure, and common fields in a collection's documents may hold different types of data.
## Short Intro with Example Codes
Creating a Database. Save it as demo_create_mongo_db.js and run it from node. To create a database in MongoDB, start by creating a MongoClient object, then specify a connection URL with the correct ip address and the name of the database you want to create. MongoDB will create the database if it does not exist, and make a connection to it.
**RDBMS**  |  **MongoDB**
--|--
Database  |  Database
Table  |  Collection
Tuple/Row  |  Document
column  |  Field
Table Join  |  Embedded Documents
Primary Key  |  Primary Key (Default key id provided by mongodb itself)

## Advantages of Mongo
- Schema less − MongoDB is a document database in which one collection holds different documents.
- Structure of a single object is clear.
- No complex joins.
- Ease of scale-out − MongoDB is easy to scale
- Conversion/mapping of application objects to database objects not needed.
- Uses internal memory for storing the (windowed) working set, enabling faster access of data.

```js
var MongoClient = require('mongodb').MongoClient;
var url = "mongodb://localhost:27017/mydb2";

// Create Database
MongoClient.connect(url, function(err, db) {
  if (err) throw err;
  console.log("Database created!");
  db.close();
});
```
To create a table in MongoDB, use the createCollection() method:
```js
MongoClient.connect(url, function(err, db) {
  // Create Collection
  db.createCollection("customers", function(err, res) {
    if (err) throw err;
    console.log("Table created!");
    db.close();
  });
});
```
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
```

```js
// Insert many
MongoClient.connect(url, function(err, db) {
  if (err) throw err;
  var myobj = [
    { name: 'John', address: 'Highway 71'},
    { name: 'Peter', address: 'Lowstreet 4'},
    { name: 'Amy', address: 'Apple st 652'},
    { name: 'Hannah', address: 'Mountain 21'},
    { name: 'Michael', address: 'Valley 345'},
    { name: 'Sandy', address: 'Ocean blvd 2'},
    { name: 'Betty', address: 'Green Grass 1'},
    { name: 'Richard', address: 'Sky st 331'},
    { name: 'Susan', address: 'One way 98'},
    { name: 'Vicky', address: 'Yellow Garden 2'},
    { name: 'Ben', address: 'Park Lane 38'},
    { name: 'William', address: 'Central st 954'},
    { name: 'Chuck', address: 'Main Road 989'},
    { name: 'Viola', address: 'Sideway 1633'}
  ];
  db.collection("customers").insertMany(myobj, function(err, res) {
    if (err) throw err;
    console.log("Number of records inserted: " + res.insertedCount);
    db.close();
  });
});
```
Find:  
The findOne() method returns the first occurrence in the selection. The first parameter of the findOne() method is a query object. In this example we use an empty query object, which selects all records in a table (but returns only the first record).
```js
// The findOne() method returns the first occurrence in the selection.
MongoClient.connect(url, function(err, db) {
  if (err) throw err;
  db.collection("customers").findOne({}, function(err, result) {
    if (err) throw err;
    console.log(result.name);
    db.close();
  });
});
```
Select Data:  
To select data from a table in MongoDB, we can also use the find() method.
```js
MongoClient.connect(url, function(err, db) {
  if (err) throw err;
  db.collection("customers").find({}).toArray(function(err, result) {
    if (err) throw err;
    console.log(result);
    console.log(result[2].address);
    db.close();
  });
});
```
When selecting records from a table, you can filter the result by using a query object.
--> Select records with the address "Park Lane 38":
```js
MongoClient.connect(url, function(err, db) {
  if (err) throw err;
  var query = { address: "Park Lane 38" };
  db.collection("customers").find(query).toArray(function(err, result) {
    if (err) throw err;
    console.log(result);
    db.close();
  });
});
```

Querry / Filter with regex:  
```js
MongoClient.connect(url, function(err, db) {
  if (err) throw err;
  var query = { address: /^S/ };
  db.collection("customers").find(query).toArray(function(err, result) {
    if (err) throw err;
    console.log(result);
    db.close();
  });
});
```

Sort:  
```js
MongoClient.connect(url, function(err, db) {
  if (err) throw err;
  var mysortAsc = { name: 1 };
  var mysortDesc = { name: -1 };
  db.collection("customers").find().sort(mysort).toArray(function(err, result) {
    if (err) throw err;
    console.log(result);
    db.close();
  });
});
```

Delete:  
```js
MongoClient.connect(url, function(err, db) {
  if (err) throw err;
  var myquery = { address: 'Mountain 21' };
  db.collection("customers").deleteOne(myquery, function(err, obj) {
    if (err) throw err;
    console.log("1 document deleted");
    db.close();
  });
});
```

Delete table / collection:    
You can delete a table, or collection as it is called in MongoDB, by using the drop() method.  
You can also use the dropCollection() method to delete a table (collection).
The dropCollection() method takes two parameters: the name of the collection and a callback function.
```js
MongoClient.connect(url, function(err, db) {
  if (err) throw err;
  db.collection("customers").drop(function(err, delOK) {
    if (err) throw err;
    if (delOK) console.log("Table deleted");
    db.close();
  });
});

MongoClient.connect(url, function(err, db) {
  if (err) throw err;
  db.dropCollection("customers", function(err, delOK) {
    if (err) throw err;
    if (delOK) console.log("Table deleted");
    db.close();
  });
});
```

Update:  
You can update a record, or document as it is called in MongoDB, by using the updateOne() method.
```js
MongoClient.connect(url, function(err, db) {
  if (err) throw err;
  var myquery = { address: "Valley 345" };
  var newvalues = { name: "Mickey", address: "Canyon 123" };
  db.collection("customers").updateOne(myquery, newvalues, function(err, res) {
    if (err) throw err;
    console.log("1 record updated");
    db.close();
  });
});
```
To update specific fields:
```js
var myquery = { address: "Valley 345" };
var newvalues = { $set: { address: "Canyon 123" } };
```

Limit:  
Use it to limit the number of returned elements
```js
MongoClient.connect(url, function(err, db) {
  if (err) throw err;
  db.collection("customers").find().limit(5).toArray(function(err, result) {
    if (err) throw err;
    console.log(result);
    db.close();
  });
});
```

Join:  
```js
MongoClient.connect(url, function(err, db) {
  if (err) throw err;
  db.collection('orders').aggregate([
    { $lookup:
       {
         from: 'products',
         localField: 'products_id',
         foreignField: 'id',
         as: 'orderdetails'
       }
     }
    ], function(err, res) {
    if (err) throw err;
    console.log(res);
    db.close();
  });
});
```
