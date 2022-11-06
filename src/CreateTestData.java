import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import StringTypedJSON.STJSON;

public class CreateTestData {
	
	private static ArrayList<String> arrayList;
	public static HashMap<String,ArrayList<STJSON>> createdObjects = new HashMap<String,ArrayList<STJSON>>();
	public static Random random = new Random(123);
	
	public static String outputDir = "testDatabase";
	
	public static void main(String[] args) throws Exception {
		
		ArrayList<ArrayList<String>> configs = loadConfigs("test_json_051122.txt");
		
		for (int i = 0; i< configs.size(); i++) {
			
			createdObjects.putAll(processConfig(configs.get(i)));
			
		}
		
//		System.out.println(createdObjects);
		
		
	}
	
	
	/**
	 * 
	 * processConfig
	 * 
	 * @param config
	 * @return
	 */
	public static HashMap<String, ArrayList<STJSON>> processConfig(ArrayList<String> config) throws Exception {
		
		HashMap<String, ArrayList<STJSON>> returns = new HashMap<String, ArrayList<STJSON>>();
		
//		ArrayList<STJSON> potentials = new ArrayList<STJSON>();
		
		String objectName = "";
		
		if (config.size()>=3) {

			String[] initialLine = config.get(0).split("\t");
			
			objectName = initialLine[0];
			
			String task = initialLine[1];
			
			String numberString = initialLine[2];
			
			int numrequired = 0;
			
			try {
				numrequired = Integer.parseInt(numberString);
			}
			catch (Exception e) {
				
			}
			
			if (numrequired < 1 || !("save".equals(task) || "generate".equals(task))) {
				return returns;
			}
			
			
			
			String[] columnsLine = config.get(1).split("\t");
			String[] requirementsLine = config.get(2).split("\t");
			
			HashMap<String, ArrayList<Object>> dataForColumns = new HashMap<String, ArrayList<Object>>();
			
			HashMap<String, ArrayList<String>> mergeColumns = new HashMap<String, ArrayList<String>>();
			
			// for all columns of configuration:
			
			for (int columnPos = 0; columnPos<columnsLine.length; columnPos++) {
				
				String fieldName = columnsLine[columnPos];
				String requirements	= requirementsLine[columnPos];
				System.out.println(fieldName + "\t" + requirements);
				
				ArrayList<Object> dataForColumn = new ArrayList<Object>();
				
				String[] requirementsSplit = requirements.split("\s");

				// record the columns which need to be merged:
				{
					String[] columnNameSplit = fieldName.split("\s");
					
					if (columnNameSplit.length > 1) {
						String fieldNamePart = columnNameSplit[0];
						if (!mergeColumns.containsKey(fieldNamePart)) {
							mergeColumns.put(fieldNamePart, new ArrayList<String>());
						}
						mergeColumns.get(fieldNamePart).add(fieldName);
					}
				}
				
				if (requirementsSplit.length == 1) {
					// options list:
					//TODO is the requirements "options"?
					
					ArrayList<String> dataInConfig = new ArrayList<String>();
					
					for (int configLinePos = 3; configLinePos<config.size(); configLinePos++) {
						String[] optionsLine = config.get(configLinePos).split("\t");
						
						if (columnPos>=optionsLine.length) {
							continue;
						}
						
						String potentialValue = optionsLine[columnPos];
						
						if (!"".equals(potentialValue)) {
							dataInConfig.add(potentialValue);
						}
					}
					
					for (int i = 0; i<numrequired; i++) {
						dataForColumn.add(dataInConfig.get(random.nextInt(dataInConfig.size())));
					}
					
//					System.out.println(dataInConfig);
//					System.out.println(dataForColumn);
				}
				else if (requirementsSplit.length == 2) {
					
					if ("num".equals(requirementsSplit[0])) {
						String numberRestriction = requirementsSplit[1];
						
						int number = 1;
						
						try {
							number = Integer.parseInt(numberRestriction);
						}
						catch (Exception e) {
							
						}
						
						int ceiling = (int)Math.pow(10,number);
						
						for (int i = 0; i<numrequired; i++) {
							int randomValue = random.nextInt(ceiling);

							String asString = "" + randomValue;
							
							dataForColumn.add(asString);
						}
						
					}
					
					// TODO: add links to other objects.
					if ("link".equals(requirementsSplit[0])) {
						
						ArrayList<STJSON> relevantObjects = createdObjects.get(requirementsSplit[1]);
						
						for (int i = 0; i<numrequired; i++) {
							
							int randomObject = random.nextInt(relevantObjects.size());

							STJSON selectedObject = relevantObjects.get(randomObject);
							
							String idString = selectedObject.stringFields.get("_id");
							
							dataForColumn.add(idString);
						}
					}
					
					if ("use".equals(requirementsSplit[0])) {
						
						ArrayList<STJSON> relevantObjects = createdObjects.get(requirementsSplit[1]);
						
						for (int i = 0; i<numrequired; i++) {
							
							int randomObject = random.nextInt(relevantObjects.size());

							STJSON selectedObject = relevantObjects.get(randomObject);
							
							dataForColumn.add(selectedObject);
						}
					}
					
				}
				
				else if (requirementsSplit.length == 3) {
					
					int requiredObjectsInFieldArray = Integer.parseInt(requirementsSplit[2]);
					

					if ("link".equals(requirementsSplit[0])) {
						
						ArrayList<STJSON> relevantObjects = createdObjects.get(requirementsSplit[1]);
						
						for (int i = 0; i<numrequired; i++) {
							
							ArrayList objects = new ArrayList();
							
							for (int reqi = 0; reqi< requiredObjectsInFieldArray; reqi++) {
								
								int randomObject = random.nextInt(relevantObjects.size());

								STJSON selectedObject = relevantObjects.get(randomObject);

								String idString = selectedObject.stringFields.get("_id");

								objects.add(idString);
							}
							
							dataForColumn.add(objects);
						}
					}
					
					if ("use".equals(requirementsSplit[0])) {
						
						ArrayList<STJSON> relevantObjects = createdObjects.get(requirementsSplit[1]);
						
						for (int i = 0; i<numrequired; i++) {
							
							ArrayList objects = new ArrayList();
							
							for (int reqi = 0; reqi< requiredObjectsInFieldArray; reqi++) {
							
								int randomObject = random.nextInt(relevantObjects.size());
	
								STJSON selectedObject = relevantObjects.get(randomObject);
								objects.add(selectedObject);
								
							}
							
							dataForColumn.add(objects);
						}
					}
					
				}
				
				
				System.out.println(dataForColumn);
				
				dataForColumns.put(fieldName, dataForColumn);
				

			} // columns processed first pass
			
//			System.out.println(dataForColumns);
			
			// handle merged columns:
			if (mergeColumns.size()>0) {
				
				for (String field:mergeColumns.keySet()) {
					
					ArrayList<String> fieldsToMerge = mergeColumns.get(field);
					
//					ArrayList<String> beginnings = null;
					
					String[] array = null;
					
					for (String fieldToMerge:fieldsToMerge) {
						
						ArrayList<Object> valuesForMergeField = dataForColumns.get(fieldToMerge);
						
						if (array == null) {
							array = new String[valuesForMergeField.size()];
						}
						
						for (int i = 0; i< valuesForMergeField.size(); i++) {
							String currentValue = array[i];
							if (currentValue == null) {
								currentValue = (String)valuesForMergeField.get(i);
							}
							else {
								currentValue = currentValue + " " + valuesForMergeField.get(i);
							}
							array[i] = currentValue;
						}
						
						dataForColumns.remove(fieldToMerge);
					}
					
					ArrayList<Object> mergedValuesForField = new ArrayList<Object>();
					
					for (int i = 0; i< array.length; i++) {
						String currentValue = array[i];
						
						mergedValuesForField.add(currentValue);
					}
					
					dataForColumns.put(field, mergedValuesForField);
					
				}
			}
			
			
			


			
//			potentials.put() = dataForColumns;
			
//			System.out.println(dataForColumns);
			
			
			// Convert to stjsons:
			
			ArrayList<STJSON> stjsons = new ArrayList<STJSON>();

			File outputDirFile = new File(outputDir);
			
			File outputDirectoryForObject = new File(outputDirFile, objectName);
			
			PrintWriter listout = new PrintWriter(System.out);
			
			if ("save".equals(task)) {
				
				if (!outputDirectoryForObject.exists()) {
					outputDirectoryForObject.mkdirs();
				}
				
				listout = new PrintWriter(new File(outputDirectoryForObject, "index.txt"));
			}
			
			
			// TODO convert dataForColumns to STJSON:
			for (int i = 0; i<numrequired; i++) {
				STJSON st = new STJSON();
				
				for (String fieldName:dataForColumns.keySet()) {
					
					ArrayList<Object> relevantData = dataForColumns.get(fieldName);
					
					if (relevantData.size()>i) {
						
						Object relevantObject = relevantData.get(i);
						
						if (relevantObject instanceof String) {
							st.stringFields.put(fieldName, (String)relevantObject);
						}
						if (relevantObject instanceof STJSON) {
							st.objectFields.put(fieldName, (STJSON)relevantObject);
						}
						if (relevantObject instanceof ArrayList) {
							ArrayList listObject = (ArrayList)relevantObject;

							if (listObject.size() == 0) {
								// if unknown type, then add a dummy string:
								st.stringArrayFields.put(fieldName, new ArrayList<String>());
							}
							else {
								
								Object firstObject = listObject.get(0);
								
								if (firstObject instanceof String) {
									st.stringArrayFields.put(fieldName, (ArrayList<String>)listObject);
								}
								if (firstObject instanceof STJSON) {
									st.objectArrayFields.put(fieldName, (ArrayList<STJSON>)listObject);
								}
								
							}
						}
						
					}
				}
				
				String id = createHex(24);
				
				st.stringFields.put("_id", id);
				
				stjsons.add(st);
				
				
				if ("save".equals(task)) {
					
					File objectFilename = new File(outputDirectoryForObject, id + ".json");
					
					PrintWriter out = new PrintWriter(objectFilename);
					
					out.print(st.toJson());
					
					out.flush();
					out.close();
					
					listout.println(id);
				}
				
				
				
			}
			
			if ("save".equals(task)) {
				listout.flush();
				listout.close();
			}
			
			
//			System.out.println(stjsons);
			
			returns.put(objectName, stjsons);
			
		}
		
		return returns;
		
	}
	
	
	// Load the config lines into different sections:
	
	public static ArrayList<ArrayList<String>> loadConfigs (String file) throws Exception {
		
		ArrayList<ArrayList<String>> configs = new ArrayList<ArrayList<String>>();
		
		
		BufferedReader in = new BufferedReader(new FileReader(file));
		
		ArrayList<String> currentConfig = new ArrayList<String>();

		while (in.ready()) {

			String line = in.readLine();
			String[] lineSplit = line.split("\t");

			if (lineSplit.length == 0) {
				configs.add(currentConfig);
				currentConfig = new ArrayList<String>();
			}
			else {
				currentConfig.add(line);
			}
		}
		configs.add(currentConfig);

		in.close();
		
		return configs;
	}
	
	
	public static String createHex(int n){
		
		String returns = "";
		
		for (int i = 0; i<n; i++) {
			int value = random.nextInt(16);
			returns+=Integer.toHexString(value);
		}
		
		return returns;
	}
	

}
