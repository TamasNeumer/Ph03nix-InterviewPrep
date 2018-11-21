# MongoSecurity

- `db.myCollection.find( { $where: "this.credits == this.debits" } );` -> Don't use where! It is the most dangerous command!
- By injecting special characters relevant to the target API language, and observing the results, a tester can determine if the application correctly sanitized the input. For example within MongoDB, if a string containing any of the following special characters were passed unsanitized, it would trigger a database error.
  - `' " \ ; { }`

WYV86-Q2XBK-39K2H-7G64D-39Q9G

- Shell tests:
  `db.user.find( {"username": {"$ne": null}, "email": {"$ne": null} } );`
