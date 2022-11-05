package DBAccess;

import java.util.ArrayList;

import org.bson.BsonDocument;
import org.bson.BsonObjectId;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import StringTypedJSON.STJSON;
import StringTypedJSON.STJSONHandlers;


/**
 * 
 * The DB access helpers make it easier to filter and pull down data.
 * There are a couple of different object retrieval methods in mongo-java-driver 3.12.5
 * The general technique is to request a Document iterator and iterate through those objects.
 * This is important when you have a large collection, but is a hassle when you have a few documents. 
 * This is a repeated task so we should probably keep all that code somewhere and keep it generic and reusable.     
 * 
 * @author davisst5
 *
 */
public class DBAccessHelpersMongo implements DBAccessHelperInterface {

	
	/**
	 * 
	 * Filter the given database collection in different way. 
	 * 
	 * @param database	The given database.
	 * @param collection	The collection to filter.
	 * @param identifiers	Some things to search for.
	 * @param field	What field are we searching? This can also be of the form "subobjectname.field"
	 * @param isObjectID	If we are providing an object id we need to vary the filter type.
	 * @return An arraylist of documents.
	 * @throws Exception
	 */
    public ArrayList<Document> filterBy(MongoDatabase database, String collection, ArrayList<String> identifiers, String field, boolean isObjectID) throws Exception {
    	
    	MongoCollection<Document> animalCollection = database.getCollection(collection);
    	
    	ArrayList<Document> documents = new ArrayList<Document>();
    	
    	
    	for (String id:identifiers) {

    		BsonDocument filter = new BsonDocument();
    		
    		if (isObjectID) {

    			ObjectId objectId = new ObjectId(id);
    			BsonObjectId bsonObjectId = new BsonObjectId(objectId);

    			filter.append(field, bsonObjectId);
    		}
    		else {
    			filter.append(field, new BsonString(id));
    		}
    		
    		MongoCursor<Document> cursor = animalCollection.find(filter).iterator();

    		try {
    			while (cursor.hasNext()) {

    				Document nextDocument = cursor.next();
    				
    				documents.add(nextDocument);
    				
    			}
    		} finally {
    			cursor.close();
    		}
    	}
    	
    	return documents;
    	
    }
    
    
    public MongoCursor<Document> filterByCursor(MongoDatabase database, String collection, String identifier, String field, boolean isObjectID) throws Exception {
    	
    	MongoCollection<Document> animalCollection = database.getCollection(collection);
    	

    	BsonDocument filter = new BsonDocument();

    	if (isObjectID) {

    		ObjectId objectId = new ObjectId(identifier);
    		BsonObjectId bsonObjectId = new BsonObjectId(objectId);

    		filter.append(field, bsonObjectId);
    	}
    	else {
    		filter.append(field, new BsonString(identifier));
    	}

    	MongoCursor<Document> cursor = animalCollection.find(filter).iterator();

    	return cursor;

    }
    
    
    /**
	 * 
	 * Filter the given database collection in different way.
	 * 
	 * This is exactly the same as the filterBy method with an additional conversion to an array of STJSON format objects. 
	 * 
	 * @param database	The given database.
	 * @param collection	The collection to filter.
	 * @param identifiers	Some things to search for.
	 * @param field	What field are we searching? This can also be of the form "subobjectname.field"
	 * @param isObjectID	If we are providing an object id we need to vary the filter type.
	 * @return An arraylist of documents.
	 * @throws Exception
	 */
    public ArrayList<STJSON> filterBySTJSON(MongoDatabase database, String collection, ArrayList<String> identifiers, String field, boolean isObjectID) throws Exception {
    	ArrayList<Document> docs = filterBy(database, collection, identifiers, field, isObjectID);
    	return STJSONHandlers.processObjectArrayList(docs);
    }
    
    /**
     * 
     * Retrieves all documents in a collection. This might be pretty big and lead to out of memory on large collections as it will try grab everything.
     * 
     * @param database The database
     * @param collection The collection
     * @return All the Document objects in the collection
     * @throws Exception
     */
    public ArrayList<Document> getAll(MongoDatabase database, String collection) throws Exception {
    	
    	MongoCollection<Document> snpCollection = database.getCollection(collection);
    	
    	MongoCursor<Document> cursor = snpCollection.find().iterator();

    	ArrayList<Document> allDocuments = new ArrayList<Document>();
    	
    	try {
    		while (cursor.hasNext()) {

    			Document nextDocument = cursor.next();
    			
    			allDocuments.add(nextDocument);

    			
    		}
    	} finally {
    		cursor.close();
    	}
    	
    	return allDocuments;
    	
    }    
    
    
    /**
     * 
     * Retrieves all documents in a collection. This might be pretty big and lead to out of memory on large collections as it will try grab everything.
     * 
	 * This is exactly the same as the getAll method with an additional conversion to an array of STJSON format objects.
     * 
     * @param database The database
     * @param collection The collection
     * @return All the Document objects in the collection
     * @throws Exception
     */
    public ArrayList<STJSON> getAllSTJSON(MongoDatabase database, String collection) throws Exception {
    	ArrayList<Document> docs = getAll(database, collection);
    	return STJSONHandlers.processObjectArrayList(docs);
    }    
    
    public MongoCursor<Document> getAllCursor (MongoDatabase database, String collection) throws Exception {
    	
    	MongoCollection<Document> snpCollection = database.getCollection(collection);
    	
    	MongoCursor<Document> cursor = snpCollection.find().batchSize(10).iterator();

    	return cursor;
    	
    }  

    
//    public HashMap<String, STJSON> toIDHash(ArrayList<STJSON> stList) {
//    	
//    	HashMap<String, STJSON> returns = new HashMap<String, STJSON>();
//    	
//    	for (STJSON st:stList) {
//    		
//    		String id = st.stringFields.get("_id");
//    		
//    		returns.put(id, st);
//    	}
//    	
//    	
//    	return returns;
//    }

}
