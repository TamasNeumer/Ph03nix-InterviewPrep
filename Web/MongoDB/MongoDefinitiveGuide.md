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
