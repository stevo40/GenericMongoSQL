package StringTypedJSON;

import java.util.ArrayList;
import java.util.HashMap;

import org.bson.Document;
import Utility.*;

/**
 * 
 * This acts as a untyped replacement for Document which will tell you the schema of the object by printing it.
 * It has hashes of strings, objects, arrays of strings, and arrays of objects.
 * This means we save a lot of time spent casting objects to the correct type.. Which is present but not really known.
 * 
 * @author davisst5
 *
 */

public class STJSON {

	/**
	 * The string->string field data:
	 */
	public HashMap<String, String> stringFields = new HashMap<String, String>();
	/**
	 * The string->STJSON field data:
	 */
	public HashMap<String, STJSON> objectFields = new HashMap<String, STJSON>();
	
	/**
	 * The string->ArrayList<String> data:
	 */
	public HashMap<String, ArrayList<STJSON>> objectArrayFields = new HashMap<String, ArrayList<STJSON>>();
	
	/**
	 * The string->ArrayList<STJSON> data:
	 */
	public HashMap<String, ArrayList<String>> stringArrayFields = new HashMap<String, ArrayList<String>>();
	

	
	
	/**
	 * 
	 * Display the schema and the containing data:
	 * 
	 */
	public String toString() {
		
		return toString(0);
//		String returns = "";
//		
//		if (stringFields.size()>0) {
//			returns +="stringhash:\n";
//			returns +=stringFields.toString();
//			returns +="\n";
//		}
//		if (objectFields.size()>0) {
//			returns +="objecthash:\n";
//			returns +=objectFields.toString();
//			returns +="\n";
//		}
//		if (stringArrayFields.size()>0) {
//			returns +="stringsArray:\n";
//			returns +=stringArrayFields.toString();
//			returns +="\n";
//		}
//		if (objectArrayFields.size()>0) {
//			returns +="objectsArray:\n";
//			returns +=objectArrayFields.toString();
//			returns +="\n";
//		}
//		
//		return returns;
	}
	
	
	/**
	 * 
	 * Display just the schema - ie, which fields are in which storage location.
	 * 
	 */
	public String getSchema(int tabs) {
		String returns = "";
		
		String currentHeading = new String(new char[tabs]).replace("\0", "\t");
		String indentHeading = new String(new char[tabs+1]).replace("\0", "\t");
		
		if (stringFields.size()>0) {
			
			returns +=currentHeading + "stringhashfields:\n";
			
			for (String field:stringFields.keySet()) {
				returns +=indentHeading + field;
				returns +="\n";
			}
			
		}
		if (objectFields.size()>0) {
			returns +=currentHeading + "objecthashfields:\n";
			
			for (String field:objectFields.keySet()) {
				returns +=indentHeading + field;
				returns +="\n";
				returns +=objectFields.get(field).getSchema(tabs +2);
				returns +="\n";
			}
		}
		if (stringArrayFields.size()>0) {
			returns +=currentHeading + "stringArrayFields:\n";
			
			for (String field:stringArrayFields.keySet()) {
				returns +=indentHeading + field + " ("+ stringArrayFields.get(field).size() + " items)\n";
			}
		}
		if (objectArrayFields.size()>0) {
			returns +=currentHeading + "objectarrayfields:\n";
			
			for (String field:objectArrayFields.keySet()) {
				returns +=indentHeading + field  + " ("+ objectArrayFields.get(field).size() + " items)\n";
				returns +="\n";
				ArrayList<STJSON> array = objectArrayFields.get(field);
				if (array.size()>0) {
					returns +=array.get(0).getSchema(tabs +2);
					returns +="\n";
				}
			}
		}
		
		return returns;
	}
	
	
	/**
	 * 
	 * Tab formatted toString method:
	 * 
	 */
	public String toString(int tabs) {
		String returns = "";
		
		String currentHeading = new String(new char[tabs]).replace("\0", "\t");
		String indentHeading = new String(new char[tabs+1]).replace("\0", "\t");
		
		if (stringFields.size()>0) {
			
			returns +=currentHeading + "stringhashfields:\n";
			
			for (String field:stringFields.keySet()) {
				returns +=indentHeading + field +" = " + stringFields.get(field);
				returns +="\n";
			}
			
		}
		if (objectFields.size()>0) {
			returns +=currentHeading + "objecthashfields:\n";
			
			for (String field:objectFields.keySet()) {
				returns +=indentHeading + field;
				returns +="\n";
				returns +=objectFields.get(field).toString(tabs +2);
				returns +="\n";
			}
		}
		if (stringArrayFields.size()>0) {
			returns +=currentHeading + "stringArrayFields:\n";
			
			for (String field:stringArrayFields.keySet()) {
				
				ArrayList<String> internalArray = stringArrayFields.get(field);
				returns +=indentHeading + field + " ("+ internalArray.size() + " items)\n";
				for (int pos = 0; pos<internalArray.size(); pos++) {
					String arrayValue = internalArray.get(pos);
					returns +=indentHeading + "\t" + pos + ":" + "\t"+ arrayValue +",\n";
				}
//				returns +="\n";
			}
		}
		if (objectArrayFields.size()>0) {
			returns +=currentHeading + "objectarrayfields:\n";
			
			for (String field:objectArrayFields.keySet()) {
				returns +=indentHeading + field  + " ("+ objectArrayFields.get(field).size() + " items)\n";
				returns +="\n";
				ArrayList<STJSON> array = objectArrayFields.get(field);
				if (array.size()>0) {
					for (int pos = 0; pos<array.size(); pos++) {
						STJSON subSTJSON = array.get(pos);
						returns +=indentHeading + pos + ":{";
						returns +=subSTJSON.toString(tabs +2);
						returns +=indentHeading + "}\n";
//						returns +="\n";
					}
				}
			}
		}
		
		return returns;
	}
	
	/**
	 * 
	 * Tab formatted toString method:
	 * 
	 */
	public String toJson() {
		
		String returns = "";
		
		ArrayList<String> outputs = new ArrayList<String>();
		
		if (stringFields.size()>0) {
			for (String field:stringFields.keySet()) {
				outputs.add("\"" + field + "\":\"" + stringFields.get(field) + "\"");
			}
		}
		if (objectFields.size()>0) {
			
			for (String field:objectFields.keySet()) {
				outputs.add("\"" + field + "\":" + objectFields.get(field).toJson());
			}
		}
		if (stringArrayFields.size()>0) {
			
			for (String field:stringArrayFields.keySet()) {
				
				ArrayList<String> internalArray = stringArrayFields.get(field);
				
				ArrayList<String> forJson = new ArrayList<String>();
				
				for (String str:internalArray) {
					forJson.add("\"" + str + "\"");
				}
				
				String asList = stowJson(forJson);
				
				outputs.add("\"" + field + "\":" + "[" + asList +"]");
			}
				
		}
		if (objectArrayFields.size()>0) {
			for (String field:objectArrayFields.keySet()) {
				
				ArrayList<STJSON> array = objectArrayFields.get(field);
				
				ArrayList<String> jsonList = new ArrayList<String>();
				
				if (array.size()>0) {
					
					for (int pos = 0; pos<array.size(); pos++) {
						STJSON subSTJSON = array.get(pos);
						jsonList.add(subSTJSON.toJson());
					}
				}
				
				outputs.add("\"" + field + "\":" + "[" + stowJson(jsonList) +"]");
				
			}
		}
		
		returns+="{" + stowJson(outputs) + "}";
		
		return returns;
	}
	
	public static String stowJson(ArrayList<String> entries) {
		
		String returns = "";
		
		for (int pos = 0; pos<entries.size(); pos++) {
			
			returns += entries.get(pos);
			
			if (pos <entries.size()-1) {
				returns += ",";
			}
		}
		
//		return "{" + returns + "}";
		
		return returns;
	}
	
	public String toPretty() {
		
		Document doc = Document.parse(toJson());
		
		return JSONPrettyPrint.prettyPrintBSONToString(doc);
		
	}
	
}
