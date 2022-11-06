# GenericMongoSQL
MongoDB Collection Join using SQL and custom query language. Currently data retrieval rather than update and submission.

Main projects goals due to development issues with MongoDB.
1. Detype the data into String and Object Hashes and Arrays only. Figuring out the type of an object before using it or finding it slowed initial development and use of the MongoDB access API. The String Typed JSON solves this problem and permits String, Object hashes and Arrays only.
2. Although Uuids were used to link between object-oriented object store in different collections, creating JOINs of the data was a manual process in Mongo Compass. This application makes it feasible for users to perform JOIN queries with easy to learn script without learning a new Query language which does not technically JOIN between Collections.
 a. I started with a custom language which operates in order of operations (database search, search params, desired keys in objects).
 b. Implemented a basic SQL script form: SELECT * FROM Table t WHERE t.a=b JOIN TableB b on t.a=b.a
3. The Mongo Database was decommisioned to reduce costs. To handle this I downloaded the JSONs to local storage and created a database layer which can happily index fields and recall elements from local storage rather than MongoDB with the exact same integration.


