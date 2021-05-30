package org.ali.ouahhabi.dscp.local.mongo.file_manager.api.daos;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import static com.mongodb.client.model.Filters.*;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;

/**
 *
 * @author Ali Ouahhabi
 * @email ali.ohhb@gmail.com
 */


@Configuration
public class TreeDao {

	private MongoCollection<Document> treesCollection;

	@Autowired
	TreeDao(MongoClient mongoClient, @Value("${spring.mongodb.database}") String database) {
		MongoDatabase myDatabase = mongoClient.getDatabase(database);
		treesCollection = myDatabase.getCollection("trees");
	}
	
	public boolean upsertTree(String tree, String userId) {
		return treesCollection
		.replaceOne(
				eq("userId",userId), 
				new Document("userId",userId).append("tree", tree), 
				new ReplaceOptions().upsert(true)
				).wasAcknowledged();
	}
	
	public String getTree(String userId) {
		Document result= treesCollection.find(eq("userId",userId)).iterator().tryNext();
		if(result != null)
			return result.getString("tree");
		else
			return "null";
	}
}
