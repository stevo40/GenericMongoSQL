
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import com.mongodb.client.MongoDatabase;

import DBAccess.DBAccessHelpers;
import DBAccess.DBAccessHelpersFileSystem;
import DBAccess.MongoConnectionHandler;
import StringTypedJSON.LanguageObject;
import StringTypedJSON.STJSON;

public class CustomScriptMongoRequest {
	
	public static void main(String[] args) throws Exception {
		
		ArrayList<String> pathConfigs = new ArrayList<String>();
		pathConfigs = TestRequests.testPathConfigs();
		
		PrintWriter out = new PrintWriter(System.out);
		
//		String file = args[0];
//		pathConfigs = loadPathConfigs(file);
//		String fileOut = args[1];
//		out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileOut), "UTF-8") );
		
//		out = new PrintWriter(fileOut);
//		out = new PrintWriter("c:/users/davisst5/desktop/mcd_samplesforallbreeds_cr.txt");
		
		
		
		ArrayList<HashMap<String, String>> results = new ArrayList<HashMap<String,String>>(); 
			
//		results = runPathConfigs(pathConfigs);
//		results = runPathConfigsFileDB(pathConfigs, "c:/users/davisst5/desktop/exportmongo/wp/");
		results = runPathConfigsFileDB(pathConfigs, "c:/users/davisst5/desktop/exportmongo/gscrm/");
		
		if (args.length == 2) {
			results = runPathConfigs(pathConfigs);
		}
		else if (args.length == 3) {
			String localDBPath = args[2];
			results = runPathConfigsFileDB(pathConfigs, localDBPath);
		}
		
		
		

		
		printResults(results, pathConfigs, out);
		
		out.flush();
		out.close();
		
	}
	
	public static ArrayList<String> loadPathConfigs(String file) throws Exception {
		
		ArrayList<String> pathConfigs = new ArrayList<String>();
		
		BufferedReader in = new BufferedReader(new FileReader(file));
		
		while (in.ready()) {
			
			String line = in.readLine().trim();
			
			if (line.length() == 0) {
				continue;
			}
			
			pathConfigs.add(line);
		}
		
		in.close();
		
		return pathConfigs;
	}

	
	public static ArrayList<HashMap<String, String>> runPathConfigs(ArrayList<String> pathConfigs) throws Exception {

		for (String pathConfig:new ArrayList<String>(pathConfigs)) {
			
			if (pathConfig.startsWith("#")) {
				pathConfigs.remove(pathConfig);
			}
			
		}
		
		MongoDatabase database = MongoConnectionHandler.getDatabase(pathConfigs.remove(0));
		
		ArrayList<HashMap<String, String>> results = runMultipleQueries(database, pathConfigs);
		
		return results;
		
	}
	
	public static ArrayList<HashMap<String, String>> runPathConfigsFileDB(ArrayList<String> pathConfigs, String localDBPath) throws Exception {

		for (String pathConfig:new ArrayList<String>(pathConfigs)) {
			if (pathConfig.startsWith("#")) {
				pathConfigs.remove(pathConfig);
			}
		}
		
		MongoDatabase database = null;//ConnectionHandler.getDatabase(pathConfigs.remove(0));
		
		String activeDB = pathConfigs.remove(0);
		
		String fullDBPath = localDBPath;// + activeDB +"/";
		
		DBAccessHelpers.dbh = new DBAccessHelpersFileSystem();
		
		DBAccessHelpersFileSystem.databaseFolder = fullDBPath;
		
		ArrayList<HashMap<String, String>> results = runMultipleQueries(database, pathConfigs);
		
		return results;
		
	}
	

	
	public static ArrayList<HashMap<String, String>> runMultipleQueries(MongoDatabase database, ArrayList<String> pathConfigs) throws Exception {
		
		ArrayList<String> orderOfCommands = new ArrayList<String>();
		
		
		HashMap<String, ArrayList<String>> separatedQueries = separateQueries(pathConfigs, orderOfCommands);
		
//		System.out.println(separatedQueries);
//		System.out.println(orderOfCommands);
		
		ArrayList<HashMap<String, String>> lastLevelOutputs = new ArrayList<HashMap<String,String>>();
		lastLevelOutputs.add(new HashMap<String, String>());
		
		for (String query:orderOfCommands) {
			
//			System.out.println(query);
			
			ArrayList<HashMap<String, String>> thisLevelOutputs = new ArrayList<HashMap<String,String>>();
			
			int iterations = lastLevelOutputs.size();
			
			for (int iteration=0; iteration<iterations; iteration++) {
				
				HashMap<String, String> lastSet = lastLevelOutputs.get(iteration);

				ArrayList<String> individualPathConfigs = separatedQueries.get(query);

				ArrayList<HashMap<String, String>> results = connectionQuery(query, individualPathConfigs, database, lastSet);
				
				for (HashMap<String, String> result : results) {
					result.putAll(lastSet);
					thisLevelOutputs.add(result);
				}

			}
			
			lastLevelOutputs = thisLevelOutputs;
			if (lastLevelOutputs.size()==0) {
				lastLevelOutputs.add(new HashMap<String, String>());
			}
			
		}
		
		return lastLevelOutputs;
		
	}
	
	public static HashMap<String, ArrayList<String>> separateQueries(ArrayList<String> pathConfigs, ArrayList<String> orderOfCommands) throws Exception {
		
		HashMap<String, ArrayList<String>> sortedConfigs = new HashMap<String, ArrayList<String>>();
		
		String activeCollection = "";
		
		for (int pos = 0; pos<pathConfigs.size(); pos++) {
			
			String pathConfig = pathConfigs.get(pos);
			
			if (pathConfig.startsWith("!collection")) {
				activeCollection = pathConfig;
				sortedConfigs.put(activeCollection, new ArrayList<String>());
				orderOfCommands.add(activeCollection);
				continue;
			}
			
			sortedConfigs.get(activeCollection).add(pathConfig);
			
		}
		
		return sortedConfigs;
	}
	

	public static ArrayList<HashMap<String, String>> genericRequest(MongoDatabase database, String queryCollection, String queryField, String queryValue, boolean isObjectId, ArrayList<String> pathConfigs, HashMap<String, String> currentResults) throws Exception {

		ArrayList<HashMap<String, String>> results = new ArrayList<HashMap<String,String>>();
		
		ArrayList<STJSON> collectionSTJSONArray = new ArrayList<STJSON>();
		
		if ("*".equals(queryField)) {
			collectionSTJSONArray = DBAccessHelpers.getAllSTJSON(database, queryCollection);
			
		}
		else {
			// get the animal objects for the pass number.
			ArrayList<String> searchValues = new ArrayList<String>();
			
			if ("null".equals(queryValue) || queryValue == null) {
				return results;
			}
			
			searchValues.add(queryValue);
			collectionSTJSONArray = DBAccessHelpers.filterBySTJSON(database, queryCollection, searchValues, queryField, isObjectId);
		}
		
//		System.out.println(collectionSTJSONArray);
		
		
//		int pathConfigPrefixRemoval = queryCollection.length() + 1;
		
		for (STJSON currentST : collectionSTJSONArray) {
			
			flattenSTJSON(currentST);
			
//			System.out.println(currentSTJSON);
			
			HashMap<String, String> outputValues = new HashMap<String, String>();
			
			HashMap<String, STJSON> tempSTJSONObjects = new HashMap<String, STJSON>();
			
			// find objects in config.
			// find fields of objects.
			
			for (int pathConfigPos=0; pathConfigPos<pathConfigs.size(); pathConfigPos++) {
				
				String pathConfig = pathConfigs.get(pathConfigPos);
				
				String destinationPath = ""+pathConfig;
				
				pathConfig = pathConfig.replaceAll("^" + queryCollection + "\\.", "");
				
				boolean lang = false;
				
				if (pathConfig.endsWith(":lang")) {
					pathConfig = pathConfig.split(":")[0];
					lang = true;
				}
				
				String[] pathConfigSplit = pathConfig.split("\\.");
				
//				System.out.println(pathConfig + "\t" + pathConfigSplit.length);
				
				if (pathConfigSplit.length==1) {
					
					if ("*".equals(pathConfig)) {
						for (String stringFieldName:currentST.stringFields.keySet()) {
							String outputKey = destinationPath.substring(0, destinationPath.length()-1) + stringFieldName;
							String outputValue = currentST.stringFields.get(stringFieldName);
							outputValues.put(outputKey, outputValue);
//							System.out.println("output:\t" + outputKey + "\t" + outputValue);
						}
					}
					else if ("asobject".equals(pathConfig)) {
						outputValues.put(destinationPath, currentST.toString());
					}
					else if ("asjson".equals(pathConfig)) {
						outputValues.put(destinationPath, currentST.toJson());
					}
					else if ("aspretty".equals(pathConfig)) {
						outputValues.put(destinationPath, currentST.toPretty());
					}
					else {
						outputValues.put(destinationPath, currentST.stringFields.get(pathConfig));
//						System.out.println("output:\t" + destinationPath + "\t" + outputValues.get(destinationPath));
					}
				}
				else {
					
					// subobjects required.
					
					
					
					String intermediaryPath = "";
					boolean nullTree = false;

					STJSON lastBs = currentST;
					
					
					
					
					for (int pos = 0; pos<pathConfigSplit.length;pos++) {
						
						if (!"".equals(intermediaryPath)) {
							intermediaryPath += ".";
						}
						
						String lastPathLocation = pathConfigSplit[pos]; 
						
						intermediaryPath += lastPathLocation;
						
						if (pos<pathConfigSplit.length-1) {
							
							if (nullTree == true) {
								continue;
							}
							
							if (!tempSTJSONObjects.containsKey(intermediaryPath)) {
								
								STJSON object = lastBs.objectFields.get(lastPathLocation);
								
								flattenSTJSON(object);
								
//								System.out.println(intermediaryPath + "\t:\t" + object);
								
//								System.out.println(object);
								
								if (object == null) {
									nullTree = true;
								}
								else {
									tempSTJSONObjects.put(intermediaryPath, object);
								}
							}
							lastBs = tempSTJSONObjects.get(intermediaryPath);
						}
						else {
							// last value in config:
							
							String value = null;
							
							if (!nullTree) {
								
								if (lang) {
									STJSON langObject = lastBs.objectFields.get(lastPathLocation);
									if (langObject != null) {
										value = LanguageObject.getTextField(langObject);
										outputValues.put(destinationPath, value);
									}
								}
								else if ("*".equals(lastPathLocation)) {
									for (String stringFieldName:lastBs.stringFields.keySet()) {
										String outputKey = destinationPath.substring(0, destinationPath.length()-1) + stringFieldName;
										String outputValue = lastBs.stringFields.get(stringFieldName);
										outputValues.put(outputKey, outputValue);
//										System.out.println("output:\t" + outputKey + "\t" + outputValue);
									}
								}
								else if ("asobject".equals(lastPathLocation)) {
									outputValues.put(destinationPath, lastBs.toString());
								}
								else if ("asjson".equals(lastPathLocation)) {
									outputValues.put(destinationPath, lastBs.toJson());
								}
								else if ("aspretty".equals(lastPathLocation)) {
									outputValues.put(destinationPath, lastBs.toPretty());
								}
								else {
									outputValues.put(destinationPath, lastBs.stringFields.get(lastPathLocation));
//									System.out.println("output:\t" + destinationPath + "\t" + outputValues.get(destinationPath));
								}
								
								
//								value = lastBs.stringFields.get(lastPathLocation);
								
							}
							
//							outputValues.put(destinationPath, value);
							
						}
						
						
						
					}
					
					
					
					
				}
				
			}
			
//			System.out.println(outputValues);
			
			results.add(outputValues);
			
		}
		
		
		return results;
	}
	
	public static ArrayList<HashMap<String, String>> connectionQuery(String queryString, ArrayList<String> pathConfigs, MongoDatabase database, HashMap<String, String> currentResults) throws Exception {
		
//		System.out.println(queryString);
		
		if (queryString.startsWith("!collection")) {
			
			String[] moreCreds = queryString.split(":");
			
//			user:_id:$owner:true
			
			String newQueryCollection = "";
			String newQueryField = "";
			String newQueryValue = "";
			boolean newIsObjectId = false;
			
			for (int pos = 1; pos<moreCreds.length; pos++) {
				
				if (pos == 1) {
					newQueryCollection = moreCreds[pos];
					newQueryCollection = variableReplace(newQueryCollection, currentResults);
				}
				else if (pos == 2) {
					newQueryField = moreCreds[pos];
					newQueryField = variableReplace(newQueryField, currentResults);
				}
				else if (pos == 3) {
					newQueryValue = moreCreds[pos];
//					System.out.println(currentResults);
					newQueryValue = variableReplace(newQueryValue, currentResults);
//					System.out.println(newQueryValue);
				}
				else if (pos == 4) {
					String newIsObjectIdString = moreCreds[pos];
					
					newIsObjectIdString = variableReplace(newIsObjectIdString, currentResults);
					
					if ("true".equalsIgnoreCase(newIsObjectIdString)) {
						newIsObjectId = true;
					}
				}
				
			}
			
//			System.out.println(pathConfigs);
			
			ArrayList<HashMap<String, String>> dataValues = genericRequest(database, newQueryCollection, newQueryField, newQueryValue, newIsObjectId, pathConfigs, currentResults);
			
			return dataValues;
		}
		
		return new ArrayList<HashMap<String, String>>();
	}
	
	
	public static String variableReplace(String label, HashMap<String, String> currentResults) throws Exception {
		
		if (label.startsWith("$")) {
			label = label.substring(1);
			label = label.replaceAll("/", ".");
			
			if (currentResults.containsKey(label)) {
				return currentResults.get(label);
			}
		}
		
		return label;
		
	}


	public static void printResults(ArrayList<HashMap<String, String>> results, ArrayList<String> pathConfigs, PrintWriter out) {

		System.out.println(results);
		
		ArrayList<String> resultOrder = new ArrayList<String>();
		
		if (results.size()>0) {
			
			TreeSet<String> allKeys = new TreeSet<String>();
			
			for (HashMap<String,String> resultSet:results) {
				allKeys.addAll(resultSet.keySet());
			}
			
			for (String pathConfig:pathConfigs) {
				
				
				if (pathConfig.startsWith("!")) {
					
				}
				else if (pathConfig.endsWith("*")) {
					String newPrefix = pathConfig.substring(0, pathConfig.length()-1);
					
					// key value:
					{
						String idKey = newPrefix + "_id";
						
						if (allKeys.contains(idKey)) {
							resultOrder.add(idKey);
						}
					}
					
					for (String keyName:allKeys) {
						if (keyName.startsWith(newPrefix)) {
							
							if (keyName.endsWith("_id")) {
								continue;
							}
							
							resultOrder.add(keyName);
						}
					}
				}
				else {
					resultOrder.add(pathConfig);
				}
			}
		}
		
		for (String key:resultOrder){
			out.print("\t" + key);
		}
		out.println();
		
		for (HashMap<String, String> result:results) {
			for (String key:resultOrder){
				out.print("\t" + result.get(key));
			}
			out.println();
		}
		
	}
	
	
	public static void flattenSTJSON(STJSON stInput) {
		
		if (stInput == null) {
			return;
		}
		
		for (String key:stInput.stringArrayFields.keySet()) {
			
			STJSON output = new STJSON();
			
			ArrayList<String> strings = stInput.stringArrayFields.get(key);
			
			for (int pos = 0; pos<strings.size(); pos++) {
				output.stringFields.put("" + pos, strings.get(pos));
			}
			
			stInput.objectFields.put(key, output);
			
//			System.out.println(output);
		}
		
		
		for (String key:stInput.objectArrayFields.keySet()) {
			
			STJSON output = new STJSON();
			
			ArrayList<STJSON> objects = stInput.objectArrayFields.get(key);
			
			for (int pos = 0; pos<objects.size(); pos++) {
				output.objectFields.put("" + pos, objects.get(pos));
			}
			
			stInput.objectFields.put(key, output);
			
//			System.out.println(output.objectFields);
			
		}
		
		
		
		
	}
	
}
