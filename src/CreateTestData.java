import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import StringTypedJSON.STJSON;

public class CreateTestData {
	
	HashMap<String,ArrayList<Object>> createdObjects = new HashMap<String,ArrayList<Object>>();

	public static void main(String[] args) throws Exception {
		
		ArrayList<ArrayList<String>> configs = loadConfigs("c:/users/davisst5/Desktop/test_json_051122.txt");
		
		processConfig(configs.get(0));

	}
	
	
	/**
	 * 
	 * processConfig
	 * 
	 * @param config
	 * @return
	 */
	public static HashMap<String, ArrayList<STJSON>> processConfig(ArrayList<String> config){
		
		Random random = new Random(123);
		
		HashMap<String, ArrayList<STJSON>> returns = new HashMap<String, ArrayList<STJSON>>();
		
		ArrayList<STJSON> potentials = new ArrayList<STJSON>();
		
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
			
			if (numrequired < 1 || !"cast".equals(task)) {
				return returns;
			}
			
			
			
			String[] columnsLine = config.get(1).split("\t");
			String[] requirementsLine = config.get(2).split("\t");
			
			HashMap<String, ArrayList<String>> dataPerColumn = new HashMap<String, ArrayList<String>>();
			
			
			
			for (int columnPos = 0; columnPos<columnsLine.length; columnPos++) {
				String fieldName = columnsLine[columnPos];
				String requirements	= requirementsLine[columnPos];
				System.out.println(fieldName + "\t" + requirements);
				
				ArrayList<String> dataForColumn = new ArrayList<String>();
				
				String[] requirementsSplit = requirements.split("\s");
				
				if (requirementsSplit.length == 1) {
					// options list:
					//TODO is the requirements "options"?
					
					ArrayList<String> dataInConfig = new ArrayList<String>();
					
					for (int configLinePos = 3; configLinePos<config.size(); configLinePos++) {
						String[] optionsLine = config.get(configLinePos).split("\t");
						String potentialValue = optionsLine[columnPos];
						
						if (!"".equals(potentialValue)) {
							dataInConfig.add(potentialValue);
						}
					}
					
					for (int i = 0; i<numrequired; i++) {
						dataForColumn.add(dataInConfig.get(random.nextInt(dataInConfig.size())));
					}
					
					System.out.println(dataInConfig);
					
				}
				

			}


			
		}
		
		
		returns.put("", potentials);
		
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
	

}
