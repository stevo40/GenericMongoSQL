package DBAccess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.bson.Document;

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
public class DBAccessHelpersFileSystem implements DBAccessHelperInterface {

	
	public static String databaseFolder = "c:/users/davisst5/desktop";
	
	
	public static void main(String[] args) throws Exception {
		
		DBAccessHelpersFileSystem dbh = new DBAccessHelpersFileSystem();
		
//		ArrayList<STJSON> st = dbh.getAllSTJSON(null, "breed");//.get(0);
		
		
//		System.out.println(st.get(0).stringArrayFields);
//		System.out.println(st.get(0));
		
//		ArrayList<String> ids = dbh.idsInCollection("breed");
		
//		System.out.println(st);
		
		
//		dbh.indexField("breed.name.en");
		ArrayList<String> ids = dbh.filterToIndexField("breed", "breed.name.en", "Kromfohrländer (ProKro-registered)");
		System.out.println(ids);
		
//		System.out.println(st.get(0).getSchema(1));
		
		ArrayList<String> identifiers = new ArrayList<String>();
		identifiers.add("Kromfohrländer (ProKro-registered)");
		ArrayList<STJSON> collected = dbh.filterBySTJSON(null, "breed", identifiers, "breed.name.en", false);
		System.out.println(collected);
		
	}
	
	
	
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
    	
    	// not used by project, internal method.
    	
    	
    	return null;//dbh.filterBy(database, collection, identifiers, field, isObjectID);
    	
    }
    
    
    public MongoCursor<Document> filterByCursor(MongoDatabase database, String collection, String identifier, String field, boolean isObjectID) throws Exception {
    	
    	// only present in OwnerCaller pullSample method - this is not used - early codebase
    	
    	return null;//return dbh.filterByCursor(database, collection, identifier, field, isObjectID);

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
    	
    	if ("_id".equals(field)) {
    		
    		ArrayList<STJSON> returnDocuments = new ArrayList<STJSON>();
    		
    		STJSON st = getSTJSON(collection, identifiers.get(0));
    		
    		returnDocuments.add(st);
    		
    		return returnDocuments;
    		
    	}
    	
    	ArrayList<String> ids = filterToIndexField(collection, field, identifiers.get(0));
    	
//    	System.out.println(ids);
    	
    	ArrayList<STJSON> returnDocuments = getSTJSONForIds(collection, ids);
    	
    	return returnDocuments;
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
    	
    	// not used by project, internal method.
    	
    	return null;//return dbh.getAll(database, collection);
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
    	
    	// open the list of ids for this collection.
    	ArrayList<String> allIds = idsInCollection(collection);
    	
    	ArrayList<STJSON> returnDocuments = getSTJSONForIds(collection, allIds);
    	
    	return returnDocuments;
    }    
    
    public MongoCursor<Document> getAllCursor (MongoDatabase database, String collection) throws Exception {
    	
    	// only present in GenotypeCountSorter and MongoSlurper
    	
    	return null;//return dbh.getAllCursor(database, collection);
    	
    }
    
    public HashMap<String,String> loadIndexField(String field) throws Exception {
    	
    	HashMap<String,String> returns = new HashMap<String, String>();
    	
    	String[] nameSplit = field.split("\\.");
    	
    	String collection = nameSplit[0];
    	String folder = databaseFolder + "/" + collection + "/";
    	
    	File indexFieldFile = new File(new File(folder), "index." + field + ".txt");
    	
    	if(!indexFieldFile.exists()) {
    		indexField(collection, field);
    	}
    	
    	BufferedReader in = new BufferedReader(new FileReader(indexFieldFile));
    	
    	while (in.ready()) {
    		
    		String[] line = in.readLine().split("\t");
    		
    		if (line.length==2) {
    			returns.put(line[0], line[1]);
    		}
    		
    	}
    	in.close();
    	
    	return returns;
    }
    
    public ArrayList<String> filterToIndexField(String collection, String field, String searchString) throws Exception {
    	
    	ArrayList<String> returns = new ArrayList<String>();
    	
//    	String[] nameSplit = field.split("\\.");
    	
//    	String collection = nameSplit[0];
    	String folder = databaseFolder + "/" + collection + "/";
    	
    	File indexFieldFile = new File(new File(folder), "index." + field + ".txt");
    	
    	if(!indexFieldFile.exists()) {
    		indexField(collection, field);
    	}
    	
    	BufferedReader in = new BufferedReader(new FileReader(indexFieldFile));
    	
    	while (in.ready()) {
    		
    		String[] line = in.readLine().split("\t");
    		
    		if (line.length==2 && searchString.equals(line[1])) {
    			returns.add(line[0]);
    		}
    		
    	}
    	in.close();
    	
    	return returns;
    }
    
    
    
    public void indexField(String collection, String field) throws Exception {
    	
    	String[] nameSplit = field.split("\\.");
    	
//    	String collection = nameSplit[0];
    	String folder = databaseFolder + "/" + collection + "/";
    	
    	File newIndexFile = new File(new File(folder), "index." + field + ".txt");
    	
    	ArrayList<String> idsInCollection = idsInCollection(collection);
    	
    	PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(newIndexFile), "UTF-8") );
    	
    	for (String id:idsInCollection) {
    		
    		STJSON st = STJSONHandlers.fileToSTJSON(folder + id + ".json");
    		
//    		System.out.println(st);
    		
    		String value = "null";
    		
    		STJSON iteratingSTJSON = st;
    		
    		for (int pos = 0; pos<nameSplit.length; pos++) {
    			
    			boolean lastField = (pos==nameSplit.length-1);
    			
    			String subField = nameSplit[pos];
    			
    			if (!lastField) {
    				if (iteratingSTJSON.objectFields.containsKey(subField)) {
    					iteratingSTJSON = iteratingSTJSON.objectFields.get(subField);
    				}
    				else {
    					break;
    				}
    			}
    			else {
    				value = iteratingSTJSON.stringFields.get(subField);
    			}
    			
//    			System.out.println(nameSplit[pos] + "\t" + lastField);
    		}
    		
    		out.println(id + "\t" + value);
    		
//    		break;
    	}
    	
    	out.flush();
    	out.close();
    	
//    	BufferedReader
    }
    
    public ArrayList<String> idsInCollection(String collection) throws Exception {
    	String folder = databaseFolder + "/" + collection + "/";
    	File indexFile = new File(new File(folder), "index.txt");
    	BufferedReader in = new BufferedReader(new FileReader(indexFile));
    	
    	ArrayList<String> returns = new ArrayList<String>();
    	
    	while (in.ready()) {
    		String line = in.readLine();
    		
    		if (line.length()>0) {
    			returns.add(line);
    		}
    	}
    	in.close();
    	
    	return returns;
    }
    
    public STJSON getSTJSON(String collection, String id) throws Exception {
    	
    	String folder = databaseFolder + "/" + collection + "/";
    	
    	String file = folder + id + ".json";
    	
    	STJSON st = STJSONHandlers.fileToSTJSON(file);
    	
    	return st;
    }
    
    public ArrayList<STJSON> getSTJSONForIds(String collection, ArrayList<String> ids) throws Exception {
    	
    	String folder = databaseFolder + "/" + collection + "/";
    	
    	ArrayList<STJSON> returns = new ArrayList<STJSON>();
    	
    	for (String id:ids) {
    	
    		String file = folder + id + ".json";
    	
    		STJSON st = STJSONHandlers.fileToSTJSON(file);
    		
    		returns.add(st);
    	}
    	
    	return returns;
    }
    
    
    
    
    

}
