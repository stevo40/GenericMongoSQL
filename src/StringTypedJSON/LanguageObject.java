package StringTypedJSON;

/**
 * 
 * The language class lets us handle the repeated schema of a text output object with many sub fields of different region objects.
 * This is a repeated function so is stored here. 
 * 
 * @author davisst5
 *
 */

public class LanguageObject {
	

	public static final String ru = "ru";
	public static final String fi = "fi";  
	public static final String en = "en";
	public static final String fr = "fr";
	public static final String nl = "nl";
	public static final String os = "os";
	
	
	public static String current = en;
	

	/**
	 * 
	 * The utility method to get the output text in the currently configured language.
	 * 
	 * @param textObject
	 * @return
	 */
	public static String getTextField(STJSON textObject) {
		
		if (textObject == null) {
			return "";
		}
		
		String text = textObject.stringFields.get(current);
		
		if (text == null || text.equals("null") || text.contentEquals("")) {
			text = textObject.stringFields.get(en);
		}
		
		if (text == null || text.equals("null") || text.contentEquals("")) {
			text = textObject.stringFields.get(fi);
		}
		
		
		return text;
	}
	

}
