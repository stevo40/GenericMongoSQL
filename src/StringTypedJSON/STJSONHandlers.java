package StringTypedJSON;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import org.bson.Document;

public class STJSONHandlers {


    public static STJSON fileToSTJSON(String fileIn) throws Exception {
    	
    	BufferedReader in = new BufferedReader(new FileReader(fileIn));
    	
    	StringBuffer buff = new StringBuffer();
    	
    	while (in.ready()) {
    		String line = in.readLine();
    		buff.append(line);
    	}
    	in.close();
    	
//    	System.out.println("file opened");
    	
    	Document doc = Document.parse(buff.toString());
    	
//    	System.out.println("json parsed");
    	
    	STJSON st = process(doc);
    	
//    	System.out.println("STJSON created");
    	
//    	System.out.println(st.getSchema(1));
//    	System.out.println(st);
    	
    	return st;
    }
    
    
    
    
    

	/**
	 * 
	 * The document to STJSON conversion. The Document BSON object is variably typed which means lots of bugs when retrieving. This simplifies the process. 
	 * 
	 * @param doc Document
	 * @return STJSON object.
	 */
	public static STJSON process(Document doc) {
		
		ArrayList<String> keys = new ArrayList<String>(doc.keySet());
		
		STJSON st = new STJSON();
		
		for (String key:keys) {
			
			try {

				Object object = doc.get(key);

				if (object instanceof ArrayList) {
					ArrayList underList = (ArrayList)object;
					
					if (underList.size()>0) {
						Object objectInList = underList.get(0);
						
						if (objectInList instanceof Document) {
							ArrayList<Document> documentsList = new ArrayList<Document>(underList);
							ArrayList<STJSON> objectClones = processObjectArrayList(documentsList);
							st.objectArrayFields.put(key,objectClones);
						}
						else {
							ArrayList<String> strings = processArrayList(underList);
							st.stringArrayFields.put(key,strings);
						}
					}

				}
				else if (object instanceof Document) {
					Document newDoc = (Document)object;
					STJSON newSTJSON = process(newDoc);
					
					st.objectFields.put(key, newSTJSON);
				}
				else {
					st.stringFields.put(key, "" + doc.get(key));
				}

			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return st;
		

	}
	
	/**
	 * Process a sublist of objects:
	 * 
	 * @param documents ArrayList of Documents.
	 * @return ArrayList of STJSON objects.
	 */
	public static ArrayList<STJSON> processObjectArrayList(ArrayList<Document> documents){
		
		ArrayList<STJSON> objects = new ArrayList<STJSON>();
		
		for (Document document: documents) {
			STJSON processed = process(document);
			objects.add(processed);
		}
		
		return objects;
		
	}
	
	
	/**
	 * Process a list of non-document things which can be stored as strings:
	 * 
	 * @param documents ArrayList of Documents.
	 * @return ArrayList of STJSON objects.
	 */
	public static ArrayList<String> processArrayList(ArrayList data){
		
		ArrayList<String> strings = new ArrayList<String>();
		
		for (int pos = 0; pos<data.size(); pos++) {
			strings.add("" + data.get(pos));
		}
		
		return strings;
		
	}
    
    
    
    
}
