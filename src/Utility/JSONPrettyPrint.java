package Utility;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;

import com.mongodb.MongoClient;


public class JSONPrettyPrint {

	public static void main(String[] args) throws Exception {
		
//		Document bson = new Document();
//		
//		Document doc = new Document();
//		doc.put("bob", "bob");
//		String jsonString = doc.toJson();
//		
//		System.out.println(jsonString);
//		
//		Document doc2 = Document.parse(jsonString);
		
		List<String> lines = Files.readAllLines(new File("c:/users/davisst5/desktop/bob.json").toPath());
		
		String json = "";
		
		for (String line:lines) {
			json += line + "\n";
		}
		
		Document doc = Document.parse(json);
		
		prettyPrintBSON(doc);
		
//		String jsonString = doc.toJson();
//		System.out.println(jsonString);
		
//		Bson bson = Filters.gt("a", 10);
		
//		BS bs = BS.process(doc);
//		System.out.println(bs.getSchema(0));
		
	}
	
	
	public static String prettyPrintBSONToString(Document doc) {
		BsonDocument bsonDocument = doc.toBsonDocument(Document.class, MongoClient.getDefaultCodecRegistry());
		JsonWriterSettings.Builder settingsBuilder = JsonWriterSettings.builder().indent(true);
		return bsonDocument.toJson(settingsBuilder.build());
	}
	
	public static void prettyPrintBSON(Document doc) {
		BsonDocument bsonDocument = doc.toBsonDocument(Document.class, MongoClient.getDefaultCodecRegistry());
		JsonWriterSettings.Builder settingsBuilder = JsonWriterSettings.builder().indent(true);
		System.out.println(bsonDocument.toJson(settingsBuilder.build()));
	}
	
	public static void prettyPrintBSONToFile(Document doc, String file) throws Exception {
		BsonDocument bsonDocument = doc.toBsonDocument(Document.class, MongoClient.getDefaultCodecRegistry());
		JsonWriterSettings.Builder settingsBuilder = JsonWriterSettings.builder().indent(true);
		
		PrintWriter out = new PrintWriter(file, "UTF-8");
		out.println(bsonDocument.toJson(settingsBuilder.build()));
		out.flush();
		out.close();
	}

}
