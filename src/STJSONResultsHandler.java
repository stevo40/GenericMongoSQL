import java.util.ArrayList;
import java.util.HashMap;

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
												String alias, ArrayList<HashMap<String, String>> resultsPerRow) {
		
		// over all rows in response:
		
		for (STJSON st:response) {
			
			// values in a single row:
			
			HashMap<String, String> valuesForRow = new HashMap<String, String>();
			
			
			
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
						
						
						// check in stringFields
						
						if (currentNode.stringFields.containsKey(searchPart)) {
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

}
