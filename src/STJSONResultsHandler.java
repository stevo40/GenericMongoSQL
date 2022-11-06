import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeSet;

import StringTypedJSON.STJSON;

public class STJSONResultsHandler {


	/**
	 * 
	 * extractFieldsFromResults:
	 * 
	 *  We have our STJSON objects, but need to iterate through those to find the relevant fields. 
	 *  
	 * @param response This is the list of STJSON objects from the database response.
	 * @param requestedFields These are the fields we want using an xpath variant to navigate the tree.
	 * @param alias This is the alias of the database to filter on.
	 * @param resultsPerRow This is where we put the required 
	 */
	
	public static void extractFieldsFromResults(ArrayList<STJSON> response, ArrayList<String> requestedFields,
												String alias, ArrayList<HashMap<String, String>> resultsPerRow,
												HashMap<String, String> originalValues) {
		
		// over all rows in response:
		
		for (STJSON st:response) {
			
			// values in a single row:
			
			HashMap<String, String> valuesForRow = new HashMap<String, String>();
			
			valuesForRow.putAll(originalValues);
			
			
			
			// loop over the requested fields:
			
			for (String requestedName:requestedFields) {
				
				
				
				String searchName = requestedName;
				
				
				// handle alias:
				if (searchName.startsWith(alias)) {
					searchName = searchName.replace(alias, "");
				}
				else {
					continue;
				}
				
				
//				System.out.println(searchName);
				
				String[] objectNameSplit = searchName.split("\\.");
				
//				System.out.println("Object name parts: " + objectNameSplit.length);
				
				// TODO  map the objects to reuse on different requests. 
//				String currentLevel = "";
				
				STJSON currentNode = st;
				
				String detectedValue = null;
				
				// tokenise the search request and go through the differnet parts
				// following the objects in the tree using the currentNode 
				
				for (int pos = 0; pos < objectNameSplit.length; pos++) {
					
					String searchPart = objectNameSplit[pos];
					
//					System.out.println(searchPart);
					
					if (pos == objectNameSplit.length-1) {
						
						// last node - return the last string value at position:
						
						
						
						if ("*".equals(searchPart)) {
							
							String newLabel = "" + alias;
							
							for (int relabelPos = 0; relabelPos < pos; relabelPos++) {
								if (relabelPos>0) {
									newLabel = newLabel + ".";
								}
								newLabel = newLabel + objectNameSplit[relabelPos];
							}
							
							if (!"".equals(newLabel)) {
								newLabel = newLabel + ".";
							}
							
							for (String stringDataKey:currentNode.stringFields.keySet()) {
								valuesForRow.put(newLabel + stringDataKey, currentNode.stringFields.get(stringDataKey));
							}
							
							for (String stringDataKey:currentNode.stringArrayFields.keySet()) {
								valuesForRow.put(newLabel + stringDataKey, "STRINGARRAY");
							}
							
							for (String stringDataKey:currentNode.objectArrayFields.keySet()) {
								valuesForRow.put(newLabel + stringDataKey, "OBJECTARRAY");
							}
							
							for (String stringDataKey:currentNode.objectFields.keySet()) {
								valuesForRow.put(newLabel + stringDataKey, "OBJECT");
							}
							
						}
						else if (currentNode.stringFields.containsKey(searchPart)) {
							// check in stringFields
							
							detectedValue = currentNode.stringFields.get(searchPart);
						}
						
//						currentNode.objectFields;
						// show json of object.
						else if (currentNode.objectFields.containsKey(searchPart)) {
							
							// get the object from the objectFields
							STJSON potentialValue = currentNode.objectFields.get(searchPart);
							// then convert object to json as string
							if (potentialValue != null) {
								detectedValue = potentialValue.toJson();
							}
						}
						
						else if (searchPart.contains("[")) {
							
							// if label[123] then we can find the relevant node:
							
							String[] searchPartSplit = searchPart.replace("]","").split("\\[");
							
							int requestedPos = -1;
							
							if (searchPartSplit.length>1) {
								
								// test for numeric part:
								try {
									requestedPos = Integer.parseInt(searchPartSplit[1]); 
								}
								catch (Exception e) {
									// not important.
								}
								
							}
							
							String newSearch = searchPartSplit[0];
							
//							System.out.println(newSearch + "\t" + requestedPos);
							
							if (requestedPos>-1) {
								
								// position is valid number.
								
								if (currentNode.stringArrayFields.containsKey(newSearch)) {
									
									ArrayList<String> array = currentNode.stringArrayFields.get(newSearch);
									
									if (requestedPos<array.size()) {
										detectedValue = array.get(requestedPos);
									}
									
								}
								else if (currentNode.objectArrayFields.containsKey(newSearch)) {
									
									ArrayList<STJSON> array = currentNode.objectArrayFields.get(newSearch);
									
									if (requestedPos<array.size()) {
										STJSON obj = array.get(requestedPos);
										
										if (obj != null) {
											detectedValue = obj.toJson();
										}
									}
									
								}
								
							}
							
							// end obj[123] code.
							
						}
						
						else if (currentNode.stringArrayFields.containsKey(searchPart)) {
							
							// print the string array:
							
							detectedValue = "" + currentNode.stringArrayFields.get(searchPart);
						}
						
//						else if (currentNode.objectArrayFields.containsKey(searchPart)) {
//							
////							currentNode.objectArrayFields;
//							// probably not great to return that
//							
//							value = "" + currentNode.stringArrayFields.get(searchPart);
//						}
						

						
						
					}
					else {
						
						// position is not the first in the list.

						
						if (currentNode.objectFields.containsKey(searchPart)) {
							
//							currentNode.objectFields;
							
							currentNode = currentNode.objectFields.get(searchPart);
						}
						
						else if (searchPart.contains("[")) {
							
							// okay if specific object:
//							currentNode.objectArrayFields;
							
							
							String[] searchPartSplit = searchPart.replace("]","").split("\\[");
							
							int requestedPos = -1;
							
							if (searchPartSplit.length>1) {
								
								// test for numeric part:
								try {
									requestedPos = Integer.parseInt(searchPartSplit[1]); 
								}
								catch (Exception e) {
									// not important.
								}
								
							}
							
							String newSearch = searchPartSplit[0];
							
//							System.out.println(newSearch + "\t" + requestedPos);
							
							if (requestedPos>-1) {
								
								// position is valid number.
								
								if (currentNode.objectArrayFields.containsKey(newSearch)) {
									
									ArrayList<STJSON> array = currentNode.objectArrayFields.get(newSearch);
									
									if (requestedPos<array.size()) {
										STJSON obj = array.get(requestedPos);
										
										if (obj != null) {
											currentNode = obj;
										}
										else {
											break;
										}
									}
									
								}
								
							}
							
						}
						
						// probably void if following, as these do not have subnodes.
//						currentNode.stringArrayFields;
//						currentNode.stringFields;
						
						
					}
					
					
					
					
				}
				
				
				
//				System.out.println(searchName + "\t" + detectedValue);
				
				
				
				valuesForRow.put(requestedName, detectedValue);
				
			}
			
			resultsPerRow.add(valuesForRow);
			
			System.out.println(resultsPerRow);
			
		}
		
	}
	
	
	public static void printResultToFile(String fileName, ArrayList<HashMap<String,String>> dataRows, ArrayList<String> requests) throws Exception {
		
		PrintWriter out = new PrintWriter(fileName);
		
		
		// Find requests which were not specifically requested.
		
		ArrayList<String> newParams = new ArrayList<String>();
		
		for (HashMap<String,String> dataForRow:dataRows) {
			
			for (String key:dataForRow.keySet()) {
				if (requests.contains(key) || newParams.contains(key)) {
					// already got.
				}
				else {
					// parameter was not specifically requested:
					newParams.add(key);
				}
			}
		}
		
		//TODO We should be able to put these in order next to the * request parameters somehow.

		// in the meantime, just sort these new parameters alphabetically.
		Collections.sort(newParams);
		
		
		// and add after 
		ArrayList<String> allParams = new ArrayList<String>(requests);
		allParams.addAll(newParams);
		
		
		{
			boolean nextCols = false;
		
			for (String key:allParams) {
				
				if (nextCols) {
					out.print("\t");
				}
				
				out.print(key);
				
				nextCols = true;
			}
			
			out.println();
		}
		
		
		for (HashMap<String,String> dataForRow:dataRows) {
			
			boolean nextCols = false;
			
			for (String key:allParams) {
				
				if (nextCols) {
					out.print("\t");
				}
				
//				if (dataForRow.containsKey(key)) {
				
				out.print(dataForRow.get(key));
//				}
				
				nextCols = true;
				
			}
			out.println();
		}
		
		
		
		
		
		out.flush();
		out.close();
		
	}
	
	

}
