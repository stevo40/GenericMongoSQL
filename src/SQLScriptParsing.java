import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * This contains code relevant for turning an SQL script into a useable parse tree.
 * 
 *  TBD: Use ANTLR for better parsing and unexpected values.
 * 
 * 
 * @author davisst5
 *
 */

public class SQLScriptParsing {
	
	
	public static HashMap<String, ArrayList<String>> runSQLParse(String inputString) {
		
		ArrayList<String> tokenised = tokenise(inputString);
		
		HashMap<String, ArrayList<String>> returns = generalParse(tokenised);
		
		return returns;
		
	}

	

	/**
	 * 
	 * Here we are tokenising the string into an array of relevant information.
	 * We can use this step to remove any irrelevant whitespace and formatting
	 * (including comments) 
	 * 
	 * 
	 * @param inputString
	 * @return
	 */
	public static ArrayList<String> tokenise(String inputString) {
		
		// Step 1: Tokenise:
		
		// Remove comments: (I have used the MySQL rather than -- format comments.
		// [#] (# character) . (any character) + (one or more times) + \\n (to and including the end of the line)
		inputString = inputString.replaceAll("[#].+\\n","\n");
		
		// Pad whitespace around the = sign.
		inputString = inputString.replaceAll("="," = ");
		
		//TODO  Currently set for handling "SELECT x,y,z FROM" formats.
		// However, this is useful decoration in the WHERE a=1,b=2 statements.
		inputString = inputString.replaceAll(","," ");
		
		// strip irrelevant newlines (do I need to worry about \r?): 
		inputString = inputString.replaceAll("\n+"," ");
		
		// when tokenised, we need to remove duplicate token separators.  
		inputString = inputString.replaceAll("\s+"," ");
		
		
		// now we have the tokenised string, we can change into array:
		String[] contents = inputString.split(" ");
		
		// and into ArrayList.
		ArrayList<String> tokenised = new ArrayList<String>();
		
		for (int pos = 0; pos<contents.length; pos++) {
			tokenised.add(contents[pos]);
		}
		
		System.out.println(tokenised);
		
		return tokenised;
		
	}
	
	
	/**
	 * 
	 * The generalParse 
	 * 
	 * @param tokenised
	 * @return
	 */
	public static HashMap<String, ArrayList<String>>  generalParse(ArrayList<String> tokenised) {
	
		
		final int PARSE_REQUESTS_MODE = 0;
		final int PARSE_DATABASE_MODE = 1;
		final int PARSE_JOIN_MODE = 2;
		final int PARSE_WHERE_MODE = 3;
		final int PARSE_SHOW_MODE = 4;
		
		int mode = -1;
		
		ArrayList<String> requests = new ArrayList<String>();
		ArrayList<String> tableCollection = new ArrayList<String>();
		ArrayList<String> joinRequests = new ArrayList<String>();
		ArrayList<String> whereRequest = new ArrayList<String>();
		ArrayList<String> showRequest = new ArrayList<String>();
		
		
		for (String str:tokenised) {
			
			if ("SELECT".equals(str)) {
				mode = PARSE_REQUESTS_MODE;
				continue;
			}
			else if ("FROM".equals(str)) {
				mode = PARSE_DATABASE_MODE;
				continue;
			}
			else if ("JOIN".equals(str)) {
				mode = PARSE_JOIN_MODE;
//				continue;
			}
			else if ("WHERE".equals(str)) {
				mode = PARSE_WHERE_MODE;
				continue;
			}
			else if ("SHOW".equals(str)) {
				mode = PARSE_SHOW_MODE;
				continue;
			}
			
			if (mode == PARSE_REQUESTS_MODE) {
				requests.add(str);
				continue;
			}
			else if (mode == PARSE_DATABASE_MODE) {
				tableCollection.add(str);
				continue;
			}
			else if (mode == PARSE_JOIN_MODE) {
				joinRequests.add(str);
				continue;
			}
			else if (mode == PARSE_WHERE_MODE) {
				whereRequest.add(str);
				continue;
			}
			else if (mode == PARSE_SHOW_MODE) {
				showRequest.add(str);
				continue;
			}
			
		}
		
		System.out.println(requests);
		System.out.println(tableCollection);
		System.out.println(joinRequests);
		System.out.println(whereRequest);
		System.out.println(showRequest);
		
		HashMap<String, ArrayList<String>> returns = new HashMap<String, ArrayList<String>>();
		returns.put("requestedFields", requests);
		returns.put("tableCollection", tableCollection);
		returns.put("joinRequests", joinRequests);
		returns.put("whereRequests", whereRequest);
		returns.put("showRequests", showRequest);
		
		return returns;
		
		
	}
	
	
	

	/**
	 * 
	 * This parses the join statements into three separate data structures which are then filled.
	 * 
	 * Because of this, there is no return type. 
	 * 
	 * @param joinRequests Input tokenised string. 
	 * 
	 * @param joinAliases Output database aliases
	 * @param aliasToTable Output alias to database table
	 * @param aliasToRequest Output alias to string to requests.
	 */
	public static void parseJoins(ArrayList<String> joinRequests, ArrayList<String> joinAliases, HashMap<String, String> aliasToTable, HashMap<String, ArrayList<String>> aliasToRequest){
		// new local context for processing join statements:
		System.out.println("joinRequests: " + joinRequests);
		
		String currentAlias = "";
		String currentTable = "";

		ArrayList<String> criteria = new ArrayList<String>();
		boolean criteriaSelectingMode = false;

		for (int pos = 0; pos<joinRequests.size(); pos++) {

			String token = joinRequests.get(pos);

			if ("JOIN".equals(token)) {
				criteriaSelectingMode = false;

				// if we already have details, then we need to save these and find the next details:
				if (!"".equals(currentTable)) {

					joinAliases.add(currentAlias);
					aliasToTable.put(currentAlias, currentTable);
					aliasToRequest.put(currentAlias, criteria);

					// reset values:
					currentAlias = "";
					currentTable = "";
					criteria = new ArrayList<String>();

				}

				if ((pos+1) < joinRequests.size()) {
					currentTable = joinRequests.get(pos+1);
				}
				if ((pos+2) < joinRequests.size()) {
					currentAlias = joinRequests.get(pos+2) + ".";
				}
			}

			if ("ON".equals(token)) {
				criteriaSelectingMode = true;
				continue;
			}

			if (criteriaSelectingMode == true) {
				criteria.add(token);
			}

		}

		// set last join request:
		if (!"".equals(currentTable)) {
			joinAliases.add(currentAlias);
			aliasToTable.put(currentAlias, currentTable);
			aliasToRequest.put(currentAlias, criteria);
		}
	}
	

}
