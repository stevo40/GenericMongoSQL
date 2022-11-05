# GenericMongoSQL
MongoDB Collection Join using SQL and custom query language

Main projects goals due to development issues with MongoDB.
1. Detype the data into String and Object Hashes and Arrays only. I don't want to figure out the type of an object before using it or finding it.
2. Uuids were used to link between object-oriented object store in different collections. This makes it feasible for users to perform JOIN queries without learning a new Query language which does not JOIN.
