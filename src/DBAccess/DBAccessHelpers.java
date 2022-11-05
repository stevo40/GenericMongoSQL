package DBAccess;

import java.util.ArrayList;
import java.util.HashMap;

import org.bson.Document;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import StringTypedJSON.STJSON;


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
public class DBAccessHelpers {
	
	public static DBAccessHelperInterface dbh = new DBAccessHelpersMongo();
//	public static DBAccessHelperInterface dbh = new DBAccessHelpersFileSystem();

	
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
    public static ArrayList<Document> filterBy(MongoDatabase database, String collection, ArrayList<String> identifiers, String field, boolean isObjectID) throws Exception {
    	
    	// not used by project, internal method.
    	
    	
    	return dbh.filterBy(database, collection, identifiers, field, isObjectID);
    	
    }
    
    
    public static MongoCursor<Document> filterByCursor(MongoDatabase database, String collection, String identifier, String field, boolean isObjectID) throws Exception {
    	
    	// only present in OwnerCaller pullSample method - this is not used - early codebase
    	
    	return dbh.filterByCursor(database, collection, identifier, field, isObjectID);

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
    public static ArrayList<STJSON> filterBySTJSON(MongoDatabase database, String collection, ArrayList<String> identifiers, String field, boolean isObjectID) throws Exception {
    	return dbh.filterBySTJSON(database, collection, identifiers, field, isObjectID);
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
    public static ArrayList<Document> getAll(MongoDatabase database, String collection) throws Exception {
    	
    	// not used by project, internal method.
    	
    	return dbh.getAll(database, collection);
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
    public static ArrayList<STJSON> getAllSTJSON(MongoDatabase database, String collection) throws Exception {
    	return dbh.getAllSTJSON(database, collection);
    }    
    
    public static MongoCursor<Document> getAllCursor (MongoDatabase database, String collection) throws Exception {
    	
    	// only present in GenotypeCountSorter and MongoSlurper
    	
    	return dbh.getAllCursor(database, collection);
    	
    }  

    
    public static HashMap<String, STJSON> toIDHash(ArrayList<STJSON> stjsonList) {
    	
    	HashMap<String, STJSON> returns = new HashMap<String, STJSON>();
    	
    	for (STJSON st:stjsonList) {
    		
    		String id = st.stringFields.get("_id");
    		
    		returns.put(id, st);
    	}
    	
    	
    	return returns;
    }

}
