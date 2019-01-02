# MongoDB Definitive guide

## The `mongo` Shell

### Login

- The mongo shell is an interactive JavaScript interface to MongoDB.
- **Connect to local instance**
  - Start the local instance with `mongod`
  - Enter the shall on a new tab with `mongo` or `mongo --port 28015`
- **Connect to remote instance**
  - `mongo mongodb://ds153763.mlab.com:53763/` or `mongo --host mongodb://alice@mongodb0.examples.com:28015/?authSource=admin` (shell will prompt for pw)
- **SSH**
  - Append `&ssl=true`

### Commands

- `db` - shows the _current_ db
- `show dbs` - show all the dbs
- `use <db>` - select db
  - You can switch to non-existing databases. When you create a collection the db is also created.
- `show collections` - show all collections
- `show users` - users for _current_ database
- `show roles` - show roles, both user-defined and built-in, for the _current_ database.

### Collection commands

- Find a collection whose name might contain whitespace
  - `db.getCollection("my Collection").find()`
- Prettify the output
  - `db.myCollection.find().pretty()`

### Configure .mongorc.js

- Adding the following js code you can change the "prompt cursor"

```js
host = db.serverStatus().host;
prompt = function() {
  return db + "@" + host + "$ ";
};
```

- `DBQuery.shellBatchSize = 10;` to change the default batch size from 20.

### Scripting

- You can pass the shell JS commands by the `--eval` flag:
  - `mongo test --eval "printjson(db.getCollectionNames())"`
- When writing js script you obviously can't use the shell commands.

```js
conn = new Mongo();
db = conn.getDB("localhost:27020");
cursor = db.myCollection.find();
while (cursor.hasNext()) {
  printjson(cursor.next());
}
```

- Execute via: `load("mytest.js")`or `mongo localhost:27017/test myjsfile.js`.
- The `load()` method accepts relative and absolute paths.

### Datatypes in the shell

- **Date**
  - `Date()` - returns it as string, `new Date()` returns `Date` object.
- **ObjectId**
  - `new ObjectId` - generate new objectId
- **Numbers**
  - The mongo shell treats all numbers **as 64 bit floating-point** values by default.
  - **NumberLong(5)** - to give a 64bit integer
  - **NumberInt(3)** - 32 bit integer
  - **NumberDecimal(10.5)** 128 bit float
- **String**

### Other

- End your line with `(`, `[`, `{`, when pressing enter it will start a new line. (Instead of sending the command.)
- Use `Tab` for autocomplete
- Exit by `quit()` or `<Ctrl-C>`
- `help` to access the help menu.

## CRUD

### Insert

- All write operations in MongoDB are **atomic** on the level of a single document.

- **insertOne**
  - `await db.collection('collName').insertOne({obj});`
  - `insertOne` returns a promise that provides a result. The `result.insertedId` promise contains the `_id` of the newly inserted document.
- **insertMany**
  - `await db.collection('inventory').insertMany([obj1, obj2]);`
  - Returns a promise, and `result.insertedId` returns a promise with the array of `_id`s.
- **insert**
  - Inserts a single document or multiple documents into a collection.

### Query

#### Basics

- **Select all**
  - `db.collection('inventory').find({});`
- **Equality condition**
  - `.find({ status: 'D' })`
  - `.find({status: { $in: ['A', 'D'] }})`
- **AND condition**
  - `.find({status: 'A', qty: { $lt: 30 }});`
- **OR condition**

  - `.find({$or: [{ status: 'A' }, { qty: { $lt: 30 } }]});`

#### Match and query nested documents

- **Match a nested document**
  - `.find({size: { h: 14, w: 21, uom: 'cm' }});`
    - Find the object whose size object matches the specified object.
- **Query on Nested Field**
  - `.find({'size.uom': 'in'});`
  - `.find({'size.h': { $lt: 15 }});`

#### Match and query an array

- **Match and querry array**

  - `.find({tags: ['red', 'blank']});` - **In the specified order!**
  - `.find({tags: { $all: ['red', 'blank'] }});` - Regardless the order
  - `.find({tags: 'red'});` - documents where `tags` is an array that contains the string "red".
  - `.find({dim_cm: { $elemMatch: { $gt: 22, $lt: 30 } }});` - at least one element that is both greater than (`$gt`) 22 and less than (`$lt`) 30
  - `.find({'dim_cm.1': { $gt: 25 }});` - where the second element is greater than 25
  - `.find({tags: { $size: 3 }});` - query by array size

- **Query an array of embedded documents**
  - `.find({'instock.qty': { $lte: 20 }});`
  - `.find({'instock.0.qty': { $lte: 20 }});` - specific array position. Arrays are 0 based!
  - `.find({instock: { $elemMatch: { qty: 5, warehouse: 'A' }}});`
    - Use `$elemMatch` operator to specify multiple criteria on an array of embedded documents
    - `instock` array has at least one embedded document that contains both the field `qty` equal to 5 and the field `warehouse` equal to A.
  - `.find({'instock.qty': { $gt: 10, $lte: 20 }});`
    - matches documents where any document nested in the `instock` array has the `qty` field greater than 10 and any document (but not necessarily the same embedded document) in the array has the `qty` field less than or equal to 20

#### Projections

- With the **exception** of the `_id` field, you cannot combine inclusion and exclusion statements in projection documents.

- **Show specific fields**
  - `.find({}).project({ item: 1, status: 1 });`
- **Suppress \_id**
  - `.project({ item: 1, status: 1, _id: 0 });`
- **Return all but the excluded fields**
  - `.project({ status: 0, instock: 0 });`
- **Return fields of embedded documents**
  - `.project({ item: 1, status: 1, 'size.uom': 1 });`
- **Suppress fields of embedded docs**
  - `.project({ 'size.uom': 0 });`
- **Return specific parts of an array**
  - `.project({ item: 1, status: 1, instock: { $slice: -1 } });`
  - In this case the last element

#### Query for missing elements and null

- `{ item : null }` - **either** contain the `item` field whose value is `null` or that do not contain the `item` field.
- `{ item : { $type: 10 } }` - **only** those where value is BSON Type `Null`
- `{ item : { $exists: false }}` - **only** where item doesn't exist.

#### Iterate a cursor

- `myCursor.forEach(printjson);`
- `myCursor.toArray();` - returns elements as array. **WARNING! It loads all elements into RAM**
- Some drivers allow you to access elements by index in the cursor (`myCursor[1]`), but it is the same as `myCursor.toArray() [1];`

#### Cursor closure, isolation & batches

- By default, the server will automatically close the cursor after **10 minutes of inactivity**, or if client has exhausted the cursor. You can override this the following way: `db.users.find().noCursorTimeout();`. Or you can close it manually either with `cursor.close()` or by exhausting the cursor’s results.
- As a cursor returns documents, other operations may interleave with the query. (You might see the same document twice in the result.) To avoid this refer to "Cursor Snapshot"
- The MongoDB server returns the query results in batches. The amount of data in the batch will not exceed the maximum BSON document size. To override the default size of the batch, see `batchSize()` and `limit()`.
  - `find()` and `aggregate()` operations have an initial **batch size of 101 documents by default**.
  - As you iterate through the cursor and reach the end of the returned batch, if there are more results, cursor.next() will perform a getMore operation to retrieve the next batch. Subsequent `getMore` operations issued against the resulting cursor have no default batch size, so they are **limited only by the 16 megabyte message size**.
- The `db.serverStatus()` method returns a document that includes a `metrics` field. The metrics field contains a `metrics.cursor`field with the following :

  ```json
  {
    "timedOut" : <number>
    "open" : {
        "noTimeout" : <number>,
        "pinned" : <number>,
        "total" : <number>
    }
  }
  ```

### Update documents

#### `updateOne` & `updateMany`

- Updates the **first** document the query finds
- `$set` operator specifying the updated values
- `$currentDate` operator to update the value of a field to the current date

  ```js
  await db.collection("inventory").updateOne(
    { item: "paper" },
    {
      $set: { "size.uom": "cm", status: "P" },
      $currentDate: { lastModified: true }
    }
  );
  ```

- `updateMany` same as `updateOne` but updates **all** documents

#### `replaceOne`

- To replace the entire content of a document except for the \_id field, pass an entirely new document as the second argument to `Collection.replaceOne()`
- In the replacement document, you can omit the `_id` field since the `_id` field is immutable

#### `update`

- Either updates or replaces a single document that match a specified filter or updates all documents that match a specified filter.

#### Update behavior

- All write operations in MongoDB are **atomic** on the level of a **single document**.
- `_id` is immutable!
- MongoDB preserves the order of the document fields following write operations except for the following cases:
  - The `_id` field is always the first field in the document.
  - Updates that include `renaming` of field names may result in the reordering of fields in the document.

#### Upsert

- If `updateOne()`, `updateMany()`, or `replaceOne()` include `upsert : true` in the `options` parameter document and no documents match the specified filter, then the operation creates a new document and inserts it.

### Delete documents

#### `deleteMany` / `deleteOne`

- To delete multiple documents. E.g.: `.deleteMany({});`
- Returns a promise that provides a `result` with `result.deletedCount`.
- `deleteOne` only deletes the first match.
  - `.deleteOne({ status: 'D' });`

#### Behavior

- Delete operations **do not drop indexes**, even if deleting all documents from a collection.
- All write operations in MongoDB are atomic on the level of a single document.

## Getting started

### Documents

- _document_: an ordered set of keys with associated values
- `{"greeting": "Hello, world!", "foo": 3}`
- Keys **must** be strings. Avoid `\n`, `.` and `$` in keys.
- You cannot have duplicate keys in a document.

### Collections

- A collection is a group of documents.
- Collections have dynamic schemas. This means that the documents within a single collection can have any number of different “shapes.”

  ```json
  `{"greeting" : "Hello, world!"}``{"foo" : 5}`
  ```

- Empty string (`""`) is invalid name. So is the `null` character (`\0`). The `system` prefix is also reserved.
- If you have created a collection with a name that points to a JS function of mongo, you can still access the collection via `db.getCollection("version");`. (`db.version` would return the function itself) -> anyway, don't use awkward names.

### Databases

- MongoDB groups collections into databases.
- Database name must not be empty and shall contain only alphanumeric ASCII characters. Names are **case sensitive**!
- The following database names are reserved: _admin_, _local_, _config_

### Starting MongoDB

- `mongod` to start the background service -> `mongod` will use the default data directory, `/data/db/` on unix. If directory doesn't exist or there is no write access will fail to start.
- By default listens for socket connections on port `27017`. If in use will fail to start.

### The mongo shell

- The shell is a full-featured JavaScript interpreter, capable of running arbitrary JavaScript programs.
- **Connect to host (mongod service)**
  - `mongo` -> To enter the shell
  - `mongo some-host:30000/myDB` -> create to non/default server
  - `mongo --nodb` -> start without db connection
  - `conn = new Mongo("some-host:30000")` -> reconnect mongo at runtime
- `db.help()`
- `show dbs`
- `db` -> currently used db
- `use otherDb` -> select _otherDb_ to be used
- `show collections` -> show collections in _current_ db
- **Create**
  - `post = {"title" : "My Blog Post", "date" : new Date()}`
  - `db.blog.insert(post)` -> insert into the `blog` collection
  - `db.blog.find()` -> see it listed
- **Update**
  - `post.comments = []`
  - `db.blog.update({title : "My Blog Post"}, post)`
- **Delete**
  - `db.blog.remove({title : "My Blog Post"})`
- **Read**
  - `db.blog.findOne()` --> see more on this later
- You can also pass js script to mongo: `mongo script1.js script2.js script3.js`
- Or you can load the script itself in the interactive shell: `load("script1.js")`

### Creating a .mongorc.js

- If you have frequently-loaded scripts you might want to put them in your _mongorc.js_ ile. (home dir in Unix) This file is run whenever you start up the shell.
- Common use is to "remove" dangerous methods. E.g.: `db.dropDatabase = DB.prototype.dropDatabase = no;` -> will d

  ```js
  var no = function() {
    print("Not on my watch.");
  };

  db.dropDatabase = DB.prototype.dropDatabase = no;
  DBCollection.prototype.drop = no;
  DBCollection.prototype.dropIndex = no;
  ```

- You can disable loading your `.mongorc.js` by using the `--norc` option when starting the shell.

### Data types

- _null_ -> `{"x": null}`
- _boolean_ -> `{"x": true}`
- _number_
  - `{"x": 3.14}` or `{"x": 3}` (8byte float)
  - `{"x": NumberInt("3")}` (4byte signed), `{"x": NumberLong("3")}` (8byte signed)
- _string_ -> `{"x": "asd"}`
- _date_ -> `{"x": new Date()}` -> stored as milliseconds since the epoch. Timezone needs to be stored in other variable!
- _regexp_ -> `{"x":/foobar/i}`
- _array_ -> `{"x": ["a","b"]}`
- _embedded document_ -> `{"x":{"foo":"bar"}}`
- _object id_ -> `{"x" : ObjectId()}` (12byte ID)
- _binary data_ -> Binary data is a string of arbitrary bytes.
- _code_ -> `{"x" : function() { /* ... */ }}`

### Dates

- In JavaScript, the Date class is used for MongoDB’s date type. When creating a new `Date` object, always call `new Date(...)`, not just `Date(...)`. Calling the constructor as a function (that is, not including `new`) returns a string representation of the date, not an actual `Date` object. This is not MongoDB’s choice; it is how JavaScript works. If you are not careful to always use the Date constructor, you can end up with a mishmash of strings and dates.

### Arrays

- An array can store different object types: `{"things" : ["pie", 3.14]}`
- One of the great things about arrays in documents is that MongoDB “understands” their structure and knows how to reach inside of arrays to perform operations on their contents. This allows us to query on arrays and build indexes using their contents. For instance, in the previous example, MongoDB can query for all documents where 3.14 is an element of the "things" array.

### Embedded documents

```js
{
  "name" : "John Doe",
  "address" : {
    "street" : "123 Park Street",
    "city" : "Anytown",
    "state" : "NY"
  }
}
```

- In a relational database, the previous document would probably be modeled as two separate rows in two different tables (one for “people” and one for “addresses”).
- When used properly, embedded documents can provide a more natural representation of information. The flip side of this is that there can be more data repetition with MongoDB. Suppose“addresses” were a separate table in a relational database and we needed to fix a typo in an address. When we did a join with “people” and “addresses,” we’d get the updated address for everyone who shares it. With MongoDB, we’d need to fix the typo in each person’s document.

### \_id and ObjectIds

- Every document stored in MongoDB must have an "`_id`" key. The "`_id`" key’s value can be any type, but it defaults to an `ObjectId`.
- The first four bytes of an `ObjectId` are a timestamp in seconds since the epoch. -> It will sort _roughly_ in chronological order.
- This id is usually generated on client side. (Mongo driver)

## Creating, Updating, and Deleting Documents

### Inserting and Saving

- **Batch insert**
  - `db.foo.batchInsert([{"_id" : 0}, {"_id" : 1}, {"_id" : 2}])`
  - Works only on a single collection.
  - Message size is max 48MB, is you are sending more the driver will split it up.
  - **If halfway through the insert you get an error, half of the data remains written to the database (?!)** You can use `continueOnError` to make it through though. (This option is not supported by the shell, only by the drivers.)
  - All documents must be under **16MB**
- **Removing documents**
  - `db.foo.remove()` -> remove all, `db.mailing.list.remove({"opt-out": true})` -> remove where opt-out value is `true`.
  - To remove entire collection use _drop_ -> `db.foo.drop()`
- **Updating**
  - Updating is atomic.
  - Try to use the `_id` when updating. If your id is not unique, and you are overwriting the entire document, if the matcher matches two documents it will throw an error, as you are trying to insert (via the update) the same document with the same `_id`. As more documents cannot coexist with the same `id` an error will be produced.
    - `db.users.update({"name" : "joe"}, joe);` -> avoid! Doesn't work if more than one document matched!

### Modifiers

- **`$inc`**
  - Increments the value. Note that `_id` can't be incremented, it can be only updated by using whole-document replacement.
  - `db.analytics.update({"url" : "www.example.com"}, {"$inc" : {"pageviews" : 1}})` -**`$set`**
  - Sets values to the field or adds field if it does not yet exist.
  - `db.users.update({"_id" : ObjectId("4b253b067525f35f94b60a31")}, {"$set" : {"favorite book" : "War and Peace"}})`
  - Using this you can also change the value type (i.e. from string to array)
- **`$unset`**
  - Remove the key
  - Common mistake people make is trying to update/set without using the `$` modifiers. E.g.: `db.coll.update(criteria, {"foo" : "bar"})`. This won't work.
- **`$inc`**

  - Atomically increment a value (in the example increment by 50)
  - `db.games.update({"game" : "pinball", "user" : "joe"},{"$inc" : {"score" : 50}})`
  - Can only be used on numeric values

### Array modifiers

- **`$push`**

  - Add elements to the end of an array (if exists), otherwise create it.
  - `db.blog.posts.update({"title" : "A blog post"}, {"$push" : {"comments" : {"name" : "joe", "email" : "joe@example.com", "content" : "nice post."}}})`
    - Add a new comment to the posts collection, where the title was "A blog post"
  - Combine with the `$each` to insert multiple elements within the same push:
    - `db.stock.ticker.update({"_id" : "GOOG"}, {"$push" : {"hourly" : {"$each" : [562.776, 562.790, 559.123]}}})`
  - Combine with `$slice` to limit the number of elements. (The last N elements will be present in the array, where N was specified by the `$slice`)
  - Combine with `$sort` to sort the elements. The following code will sort all of the objects in the array by their "rating" field and then keep the first 10. Note that you must include "`$each`"; you cannot just "`$slice`" or "`$sort`" an array with "\$push".

    ```json
    db.movies.find(
      { "genre": "horror" },
      {
        "$push": {
          "top10": {
            "$each": [
              { "name": "Nightmare on Elm Street", "rating": 6.6 },
              { "name": "Saw", "rating": 4.3 }
            ],
            "$slice": -10,
            "$sort": { "rating": -1 }
          }
        }
      }
    )
    ```

- **`$ne`**
  - "Not element" -> i.e. push an element into an array, only if it's not already there
  - `db.papers.update({"authors cited" : {"$ne" : "Richie"}},{$push : {"authors cited" : "Richie"}})`
- **`$addToSet`**

  - You can also use "`$addToSet`" in conjunction with "`$each`" to add multiple unique values, which cannot be done with the "`$ne`"/"`$push`" combination. Here we find our guy with it's objectId and add multiple e-mail addresses but only if these were not there yet

    ```json
    db.users.update(
      { "_id": ObjectId("4b2d75476cc613d5ee930164") },
      {
        "$addToSet": { "emails": { "$each": ["joe@php.net", "joe@example.com", "joe@python.org"] } }
      }
    )
    ```

- **`$pop`**
  - `{"$pop" : {"key" : 1}}` removes the last element, `{"$pop" : {"key" : 1}}` removes from beginning.
- **`$pull`**
  - Used to remove elemnts based on a criteria
  - `db.lists.update({}, {"$pull" : {"todo" : "laundry"}})` -> remove the laundry element from our todo list.
- **Positional array modifications**
  - Arrays are indexed from 0 -> `db.blog.update({"post" : post_id}, {"$inc" : {"comments.0.votes" : 1}})` -> increments the vote counter of the first array element.
  - Often we don't know the index -> We use the `$` positional operator, that figures out which element of the array the query document matched and updates that element.
  - `db.blog.update({"comments.author" : "John"}, {"$set" : {"comments.$.author" : "Jim"}})`
  - The positional operator **updates only the first match**.

### Array storage on the disk

- When you start inserting documents into MongoDB, it puts each document right next to the previous one on disk. Thus, if a document gets bigger, it will no longer fit in the space it was originally written to and will be moved to another part of the collection.
- As of this writing **(2013)**, MongoDB is **not** great at reusing empty space, so moving documents around a lot can result in large swaths of empty data file. If you have a lot of empty space, you’ll start seeing messages that look like this in the logs: `extent a:7f18dc00 was empty, skipping ahead`. The message itself is harmless, but it indicates that you have fragmentation and may wish to perform a compact.
- If your schema requires lots of moves or lots of churn through inserts and deletes, you can improve disk reuse by using the `usePowerOf2Sizes` option.
  - `db.runCommand({"collMod" : collectionName, "usePowerOf2Sizes" : true})`

### Upserts / Updates

- If found updated normally, if not created
- `db.users.update({"rep" : 25}, {"$inc" : {"rep" : 3}}, true)`
  - The third `true` parameter sets the upsert mode.
  - Note that if didn't exist, rep is created with 28! And again and again if you run this multiple times.
- `$setOnInsert` - only sets the value of a field when the document is being inserted.
  - `db.users.update({}, {"$setOnInsert" : {"createdAt" : new Date()}}, true)`
- `save` is a shell function that lets you insert a document if it doesn’t exist and update it if it does.
- Updates, by default, **update only the first document found** that matches the criteria.
- To modify all of the documents matching the criteria, you can pass `true` as the fourth parameter to update.
  - `db.users.update({"birthday" : "10/13/1978"},{"$set" : {"gift" : "Happy Birthday!"}}, false, true)`
- `findAndModify` -> can **return** the item and update it in a single operation. It returns the **pre-update** state of the object.

  - `findAndModify` can either have `update` or `remove` key.

  ```json
  db.runCommand({"findAndModify" : "processes",
      "query" : {"status" : "READY"},
      "sort" : {"priority" : -1},
      "update" : {"$set" : {"status" : "RUNNING"}})
  ```

### Write concern

- The two basic write concerns are _acknowledged_ or _unacknowledged_ writes.
- Acknowledged writes are the default: you get a response that tells you whether or not
  the database successfully processed your write. Unacknowledged writes do not return
  any response, so you do not know if the write succeeded or not.
- In general, **applications should stick with acknowledged writes**.
- For low-value data (e.g., logs or bulk data loading), you may not want to wait for a response you don’t care about. In these situations, use unacknowledged writes.
- The shell does not actually support write concerns in the same way that the client libraries do: it does unacknowledged writes and then checks that the last operation was successful before drawing the prompt. Thus, if you do a series of invalid operations on a collection, finishing with a valid operation, the shell will not complain.
  - `db.foo.insert({"_id" : 1}); db.foo.insert({"_id" : 1}); db.foo.count()`
  - This will pass, even if inserting twice with same \_id didn't work out!

## Querying

### Find

- **Intro**
  - `db.collectionName.find()` - if no criteria given all results returned (as a `Cursor`)
  - `db.users.find({"age" : 27})` - adding a key/value pair to query.
- **Which key-value paris to return**
  - `db.users.find({}, {"username" : 1, "email" : 1})` -> default `_id` and these two fields are returned.
  - `db.users.find({}, {"username" : 1, "_id" : 0})` -> you can disable returning a field by setting it to `0`.

### Query Criteria

- **Query Conditionals**
  - "`$lt`", "`$lte`", "`$gt`", "`$gte`" - (lower than, lower than equals etc.)
    - `db.users.find({"age" : {"$gte" : 18, "$lte" : 30}})`
    - `db.users.find({"registered" : {"$lt" : new Date("01/01/2007")}})`
      - When matching date you don't want to use equals, as this would match to millisecond precision.
  - `$ne` -> "not equal"
    - `db.users.find({"username" : {"$ne" : "joe"}})`
- **OR querries**
  - `db.raffle.find({"ticket_no" : {"$in" : [725, 542, 390]}})`
  - `db.raffle.find({"ticket_no" : {"$nin" : [725, 542, 390]}})`
  - `db.raffle.find({"$or" : [{"ticket_no" : 725}, {"winner" : true}]})`
- **`$not`**
  - _metaconditional_: it can be applied on top of any other criteria
  - `db.users.find({"id_num" : {"$not" : {"$mod" : [5, 1]}}})` - find numbers where % 5 does NOT return 1.

### Type-Specific Queries

- **null**
  - `null` not only matches itself but also matches “does not exist.”
    - `db.c.find({"z" : null})` will return every element where the key `z` was not defined at all, and elements where it was set explicitly to `null`
    - `db.c.find({"z" : {"$in" : [null], "$exists" : true}})` -> to only deliver results where `null` was assigned to the key use the `$exists`
- **RegExp**
  - MongoDB uses the Perl Compatible Regular Expression (PCRE)

### Querying Arrays

- **`$all`**
  - If you need to match arrays by more than one element, you can use "`$all`".
  - `db.food.find({fruit : {$all : ["apple", "banana"]}})` -> find docs where fruit both contains apple and banana
  - You can also query by **exact match** using the entire array.
    - `db.food.find({"fruit" : ["apple", "banana"]})`
  - `db.food.find({"fruit.2" : "peach"})` -> find by a specific index
- **`$size`**
  - `db.food.find({"fruit" : {"$size" : 3}})`
- **`$slice`**
