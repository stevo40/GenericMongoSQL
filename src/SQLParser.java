import java.util.ArrayList;
import java.util.HashMap;

import com.mongodb.client.MongoDatabase;

import DBAccess.DBAccessHelpers;
import DBAccess.DBAccessHelpersFileSystem;
import StringTypedJSON.STJSON;

public class SQLParser {
	
	public static MongoDatabase currentDatabase = null; 
	
	
	
	public static void main(String[] args) throws Exception {
		
		setFilePathDatabase("c:/users/davisst5/desktop/exportmongo/", "gscrm");
		
		// basic sql query we want to be able to parse:
//		String test = "SELECT a,b FROM Collection c JOIN Alphabet A ON c.j = C.j JOIN Alphabet A ON c.j = C.j WHERE a = 0";
		
		
		
		//This is the existing script structure which we have a parser for:
//		!collection:user:lastName:Mathlin:false
//		user._id
//		user.firstName
//		user.lastName
//		String test = "SELECT u._id, u.firstName, u.lastName FROM user u WHERE lastName=Mathlin";
		
		
		
//		pathConfigs.add("animal.passNumber");
//		pathConfigs.add("animal.analyses.genotypeSet");
//		pathConfigs.add("!collection:animal:passNumber:870027408083:false");
		

//		String test = "SELECT passNumber FROM animal a";
//		String test = "SELECT passNumber, analyses FROM animal WHERE passNumber=870027408083";
//		String test = "SELECT a.passNumber, a.products[0].productCode, a.tags FROM animal a WHERE a.passNumber=870027408083";
//		String test = "SELECT a.passNumber, a.analyses.genotypeSet FROM animal a WHERE a.passNumber=870027408083";
		String test = "SELECT a.passNumber, a.analyses.genotypeSet, g.sample_call_rate FROM animal a WHERE a.passNumber=870027408083 JOIN genotypeResultSet g ON g._id=id(a.analyses.genotypeSet)";
		
		
		
//		String test = "SELECT passNumber FROM animal a WHERE a._id=5b221272349aabfa06bb9059";
		
		System.out.println(test);
		
		HashMap<String, ArrayList<String>> mostlyParsed = SQLScriptParsing.runSQLParse(test);
		
		initialQuery(mostlyParsed);
		
		
	}
	
	// Utility method sets us up to use the filepathdatabase - More of a DBAccessHelpers method really. 
	
	private static void setFilePathDatabase(String localDBPath, String activeDB) {
		
		currentDatabase = null;
		
//		String activeDB = pathConfigs.remove(0);
		String fullDBPath = localDBPath + activeDB +"/";
		
		DBAccessHelpers.dbh = new DBAccessHelpersFileSystem();
		
		DBAccessHelpersFileSystem.databaseFolder = fullDBPath;
		
	}
	
	
	
	/**
	 * 
	 * At this point we can run our first stage SELECT statement before any JOINs
	 * 
	 * @param queryData This is the tokenised data separated into relevant sections using the basicParse method.
	 * 
	 * @return Per row query results.
	 *  
	 * @throws Exception
	 */
	
	public static ArrayList<HashMap<String, String>> initialQuery(HashMap<String, ArrayList<String>> queryData) throws Exception {

		// Results per row goes here:
		ArrayList<HashMap<String, String>> resultsPerRow = new ArrayList<HashMap<String,String>>();
		
		// relevant data passed in from query:
		ArrayList<String> initialTableSelectName = queryData.get("tableCollection");
		ArrayList<String> requestedFields = queryData.get("requestedFields");
		ArrayList<String> whereRequests = queryData.get("whereRequests");
		ArrayList<String> joinRequests = queryData.get("joinRequests");
		
		
		
		// Prep for initial SELECT:
		
		// what table/collection:
		String queryCollection = initialTableSelectName.get(0);
		
		// named context, for filtering the database name from the query:
		String alias = "";
		
		if (initialTableSelectName.size()>1) {
			alias = initialTableSelectName.get(1) + ".";
		}
		
		// row response destination:
		ArrayList<STJSON> response = new ArrayList<STJSON>();
		
		// to begin with, we need the parameters from the SELECT statement:
		response = individualQuery(whereRequests, alias, currentDatabase, queryCollection, new HashMap<String, String>());
		
		// STJSON to Array of hashmaps for relevant query fields in response.
		STJSONResultsHandler.extractFieldsFromResults(response, requestedFields, alias, resultsPerRow);
		
		
		
		//Parse out the join information:
		
		ArrayList<String> joinAliases = new ArrayList<String>();
		HashMap<String, String> aliasToTable = new HashMap<String, String>();
		HashMap<String, ArrayList<String>> aliasToRequest = new HashMap<String, ArrayList<String>>();
		
		// do the parsing of the joins:
		SQLScriptParsing.parseJoins(joinRequests, joinAliases, aliasToTable, aliasToRequest);
		
		//TODO  run the join request for the tables.
		System.out.println(joinAliases);
		System.out.println(aliasToTable);
		System.out.println(aliasToRequest);
		
		
		
		
		
		for (HashMap<String,String> values: resultsPerRow) {

			// this keeps everything together:
			HashMap<String, String> currentValues = new HashMap<String, String>();
			currentValues.putAll(values);
			
			

			for (String joinAlias:joinAliases) {

				ArrayList<String> criteria = aliasToRequest.get(joinAlias);
				String joinTable = aliasToTable.get(joinAlias);

				// this provides the response queries.
				ArrayList<STJSON> joinResponse = individualQuery(criteria, joinAlias, currentDatabase, joinTable, currentValues);

				
				ArrayList<HashMap<String, String>> parsedResultsFromResponse = new ArrayList<HashMap<String,String>>();
				
				STJSONResultsHandler.extractFieldsFromResults(joinResponse, requestedFields, joinAlias, parsedResultsFromResponse);
				
				
				// TODO  need to parse and merge this into the existing queries somehow.
				// So for example, we have the results from Query 1, need to multiply response rows for Query 2, etc.
				
				
				System.out.println(parsedResultsFromResponse);

			}

		}
		
		
		
		
		
		
		
//		System.out.println(response);
//		System.out.println(response.size());
		
		return resultsPerRow;
		
	}

	
	
	/**
	 * 
	 * This performs a single query given the filter request 
	 * 
	 * @param filterRequests
	 * @param alias If we are referencing the request parameters, we need to know here.
	 * @param currentDatabase
	 * @param queryCollection
	 * @return
	 * @throws Exception
	 */
	public static ArrayList<STJSON> individualQuery(ArrayList<String> filterRequests, String alias, MongoDatabase currentDatabase, String queryCollection, HashMap<String, String> existingParameters) throws Exception {
		
		ArrayList<STJSON> response = new ArrayList<STJSON>();
		
		if (filterRequests.size()==0) {
			// base case, no where statements to restrict the query:
			response = DBAccessHelpers.getAllSTJSON(currentDatabase, queryCollection);
		}
		else 
		{
			// This is the start of the initial query.
			
			// TODO  How can I generalise this to work with the JOIN process. 
			
			String queryField = "";
			boolean isObjectId = false;
			String queryValue = null;
			
			
			// base case, assume one equals parameter to begin with in [inputField, =, matchValue]
			
			if (filterRequests.contains("=")) {
				
				String inputField = filterRequests.get(0);
				
				// Handle named context for databases:
				
	//			System.out.println(alias + "\t" + inputField);
				if (inputField.startsWith(alias)) {
					inputField = inputField.replace(alias, "");
				}
				
				
				queryField = inputField;
				
				String expectationField = filterRequests.get(2);
				
				
				// handle id variables:
				// TODO  test with mongo
				if (expectationField.startsWith("id(")) {
					expectationField = expectationField.replace("id(", "");
					expectationField = expectationField.replace(")", "");
					isObjectId = true;
				}
				
				
				// Replace variables taken from previous queries
				// IF we have a previously requested parameter.
				// I think MySQL handles this without needing to request the parameter, we need it.
				if (existingParameters.containsKey(expectationField)){
					expectationField = existingParameters.get(expectationField);
				}
				
				queryValue = expectationField;
				
				
			}
			
			
			//TODO Implement caching strategy for table/request pairs.
			
	
			// this starts a filtered query.
			
			ArrayList<String> searchValues = new ArrayList<String>();
			
			if ("null".equals(queryValue) || queryValue == null) {
				return response;
			}
			searchValues.add(queryValue);
			
			
			response = DBAccessHelpers.filterBySTJSON(currentDatabase, queryCollection, searchValues, queryField, isObjectId);
		}
		
		return response;
	}
	

	
		
}
