/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ali.ouahhabi.dscp.local.mongo.file_manager.api.daos;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Updates;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.bson.BsonNull;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author Ali Ouahhabi
 * @email ali.ohhb@gmail.com
 */

@Configuration
public class FilesDao {

	private GridFSBucket gridFSBucket;
	private MongoCollection<Document> filesCollection;

	// TODO: for all tryNext ad a catch exception with what to return

	@Autowired
	FilesDao(MongoClient mongoClient, @Value("${spring.mongodb.database}") String database) {
		MongoDatabase myDatabase = mongoClient.getDatabase(database);
		gridFSBucket = GridFSBuckets.create(myDatabase);
		filesCollection = myDatabase.getCollection("fs.files");

	}

	public String initUserFiles(String userId) {
		return filesCollection
				.aggregate(Arrays.asList(new Document("$match", new Document("metadata.userId", userId)),
						new Document("$group",
								new Document("_id", "$metadata.path").append("files",
										new Document("$push", new Document("id", "$_id").append("filename", "$filename")
												.append("uploadDate", "$uploadDate").append("metadata", "$metadata")))),
						new Document("$sort", new Document("_id", 1L)),
						new Document("$group",
								new Document("_id", new BsonNull()).append("all", new Document("$push", "$$ROOT")))))
				.cursor().tryNext().toJson();
	}

	public ObjectId upload(InputStream in, GridFSUploadOptions options) throws Exception {
		if (!options.getMetadata().containsKey("name") || !options.getMetadata().containsKey("path")
				|| !options.getMetadata().containsKey("size") || !options.getMetadata().containsKey("type")) {
			throw new Exception("Metadata must include filename path ancestors ");
		}
		return gridFSBucket.uploadFromStream(options.getMetadata().get("name", String.class), in, options);
	}

	public GridFsResource download(GridFSFile file) throws FileNotFoundException, IOException {
		GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(file.getId());
		GridFsResource gridFsResource = new GridFsResource(file, gridFSDownloadStream);

		return gridFsResource;
	}

	public boolean renameFile(String path, String name, String newName, String userId) {
		Bson filter;
		filter = and(eq("metadata.userId", userId), eq("metadata.name", name), eq("metadata.path", path));
		return filesCollection.updateOne(filter, new Document("$set", new Document("metadata.name", newName)))
				.wasAcknowledged();

	}

	public boolean renameFolder(String path, String newPath, String userId) {
		Bson filter = and(eq("metadata.userId", userId), regex("metadata.path", "^" + path));
		return filesCollection
				.updateMany(filter,
						Arrays.asList(new Document("$set",
								new Document("metadata.path",
										new Document("$replaceOne", new Document("input", "$metadata.path")
												.append("find", path).append("replacement", newPath))))))
				.wasAcknowledged();

	}

	public boolean move(String userId, String name, String from, String to) {
		Bson filter;
		if (name != null) {
			filter = and(eq("metadata.userId", userId), eq("metadata.name", name), regex("metadata.path", "^" + from));
		} else {
			filter = and(eq("metadata.userId", userId), regex("metadata.path", "^" + from));
		}

		return filesCollection
				.updateMany(filter,
						Arrays.asList(
								new Document("$set",
										new Document("metadata.path",
												new Document("$replaceOne", new Document("input", "$metadata.path")
														.append("find", from).append("replacement", to))))))
				.wasAcknowledged();
	}

	public void removeFile(String path, String name, String userId) {
		Bson filter;
		filter = and(eq("metadata.userId", userId), eq("metadata.name", name), eq("metadata.path", path));

		filesCollection.find(filter).projection(include("_id")).forEach((doc) -> {
			gridFSBucket.delete(doc.getObjectId("_id"));
		});
	}

	public void removeFolder(String path, String userId) {
		Bson filter;
		filter = and(eq("metadata.userId", userId), regex("metadata.path", "^" + path));
		filesCollection.find(filter).projection(include("_id")).forEach((doc) -> {
			gridFSBucket.delete(doc.getObjectId("_id"));
		});
		;

	}

	public GridFSFile findByName(String name) {
		return gridFSBucket.find(eq("metadata.filename", name)).iterator().tryNext();
	}

	public GridFSFile findByID(ObjectId fileId, String userId) {
		return gridFSBucket.find(and(eq("_id", fileId), eq("metadata.userId", userId))).iterator().tryNext();

	}

	public GridFSFindIterable findByID(ObjectId[] fileId, String userId) {
		return gridFSBucket.find(and(in("_id", fileId), eq("metadata.userId", userId)));

	}

	public GridFSFindIterable findAll() {
		return gridFSBucket.find();
	}

	public GridFSFile findDescendent(String[] path, String userId) {
		return gridFSBucket.find(and(eq("metadata.path", path), eq("metadata.userId", userId))).iterator().tryNext();

	}

	public GridFSFindIterable findByPath(String path, String userId) {
		return gridFSBucket.find(and(regex("metadata.path", "^" + path), eq("metadata.userId", userId)));
	}

	public GridFSFile findByPathAndName(String path, String name, String userId) {
		return gridFSBucket
				.find(and(eq("metadata.path", path), eq("metadata.name", name), eq("metadata.userId", userId)))
				.iterator().next();
	}

}
