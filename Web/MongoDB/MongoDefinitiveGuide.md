# MongoDB Definitive guide

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
