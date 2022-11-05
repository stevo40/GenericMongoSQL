package DBAccess;

import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import StringTypedJSON.STJSON;

public interface DBAccessHelperInterface {
	
	public ArrayList<Document> filterBy(MongoDatabase database, String collection, ArrayList<String> identifiers, String field, boolean isObjectID) throws Exception;
	
	public MongoCursor<Document> filterByCursor(MongoDatabase database, String collection, String identifier, String field, boolean isObjectID) throws Exception;
	
	public ArrayList<STJSON> filterBySTJSON(MongoDatabase database, String collection, ArrayList<String> identifiers, String field, boolean isObjectID) throws Exception;
	
	public ArrayList<Document> getAll(MongoDatabase database, String collection) throws Exception;
	
	public ArrayList<STJSON> getAllSTJSON(MongoDatabase database, String collection) throws Exception;
	
	public MongoCursor<Document> getAllCursor (MongoDatabase database, String collection) throws Exception;
	
}
