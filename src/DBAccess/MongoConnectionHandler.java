package DBAccess;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import Utility.Properties;

public class MongoConnectionHandler {

	public static Properties props = null;

	public static MongoClient mongoClient = null;


	public static MongoClient getClient() throws Exception {

		if (mongoClient != null) {
			return mongoClient;
		}


		if (props == null) {
			props = new Properties();
			props.init("mongoproperties.txt");
		}

		String connectionString = props.get("connectionString");

		TrustManager[] trustManagers=new TrustManager[] {
				new X509TrustManager() {
					public X509Certificate[] getAcceptedIssuers() { return null; }
					public void checkClientTrusted(X509Certificate[] certs, String t) { }
					public void checkServerTrusted(X509Certificate[] certs, String t) { }
				}
		};

		SSLContext sslContext=SSLContext.getInstance("TLS");
		sslContext.init(null,trustManagers,new SecureRandom());

		// Note: the order is of importance as "sslEnabled()" (wrongly) sets the 
		// socket factory to default
		MongoClientOptions options=MongoClientOptions.builder().
				sslEnabled(true).
				sslInvalidHostNameAllowed(true).
				socketFactory(sslContext.getSocketFactory()).
				build();

		MongoClientURI mcu = new MongoClientURI(connectionString);
		MongoCredential credential = mcu.getCredentials();
		List<String> hosts = mcu.getHosts();
		String uri = hosts.get(0);
		
		List<MongoCredential> credentialList = new ArrayList<MongoCredential>();
		
		credentialList.add(credential);
		ServerAddress sa = new ServerAddress(uri);
		
//		mongoClient = new MongoClient(new MongoClientURI(connectionString));
		mongoClient = new MongoClient(sa, credentialList, options);
		
		return mongoClient;
	}

	// default database
	public static MongoDatabase getDatabase() throws Exception {
		return getDatabase("medisapiens");
	}


	public static MongoDatabase getDatabase(String database) throws Exception {

		MongoClient mongoClient = getClient();

		MongoDatabase mongoDatabase = mongoClient.getDatabase(database);

		return mongoDatabase;
	}

}
