package Utility;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * 
 * 
 * This is some basic code to load in a properties file and not store passwords in git.
 * 
 * @author davisst5
 *
 */

public class Properties {

	/**
	 * hashmap of attributes to values in this properties instance
	 */
	private HashMap<String, String> all = new HashMap<String, String>();
	/**
	 * string of the location this properties file is using for error logging
	 */
	private ArrayList<String> propertiesLocations = new ArrayList<String>();
	
	/**
	 * initialise a properties instance
	 * 
	 * @param propertiesLocation the url of the properties file to load
	 * @throws IOException
	 */
	public void init(String propertiesLocation) throws IOException{
		propertiesLocations.add(propertiesLocation);
		
		// load from given location
		BufferedReader in = new BufferedReader(new FileReader(propertiesLocation));
		
		while (in.ready()){
			String line = in.readLine();
			if (line.startsWith("//") || line.startsWith("%")){
				// ignore line if commented with // or % at the start
				continue;
			}
			
			int equalsIndex = line.indexOf("=");
			if (equalsIndex > 0){
				String key = line.substring(0, line.indexOf("=")).trim();
				String val = line.substring(line.indexOf("=")+1, line.length()).trim();
				
				// if there has been a root key specified already, replace variables of %root% in the value 
				// this is a variable use similar to windows or linux
				if (all.containsKey("root")){
					val = val.replaceAll("%root%", all.get("root"));
				}
				if (all.containsKey("generic")){
					val = val.replaceAll("%generic%", all.get("generic"));
				}
				// add to the hashmap
				all.put(key, val);
				
			}
		}
		in.close();
	}
	
	/**
	 * 
	 * allow access to a value in the hash
	 * 
	 * @param in given string
	 * @return value string
	 * @throws Exception if the key given is not in the properties file
	 */
	public String get(String in) throws Exception{
		if (all.containsKey(in)){
			return all.get(in);
		}
		else {
			throw new Exception("Key " + in + " not found in properties files " + propertiesLocations);
		}
	}
	
}
