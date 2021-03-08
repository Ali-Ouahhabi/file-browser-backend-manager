/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ali.ouahhabi.dscp.local.mongo.file_manager.api.daos;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Ali Ouahhabi
 */
@Configuration
public class FilesDao {

    private GridFSBucket gridFSBucket;

    @Autowired
    FilesDao(MongoClient mongoClient, @Value("${spring.mongodb.database}") String database) {
        MongoDatabase myDatabase = mongoClient.getDatabase(database);
        gridFSBucket = GridFSBuckets.create(myDatabase);
    }

    public ObjectId upload(InputStream in, GridFSUploadOptions options) throws Exception {
        if (!options.getMetadata().containsKey("filename")
                || !options.getMetadata().containsKey("path")
                || !options.getMetadata().containsKey("ancestors")) {
            throw new Exception("Metadata must include filename path ancestors ");
        }
        return gridFSBucket.uploadFromStream(options.getMetadata().get("filename", String.class), in, options);
    }

    public OutputStream download(ObjectId fileId) throws FileNotFoundException, IOException {
        OutputStream output = new FileOutputStream(fileId.toHexString());
        gridFSBucket.downloadToStream(fileId, output);
        output.close();
        return output;
    }

    public void findByName(String name) {
        GridFSFindIterable resp = gridFSBucket.find(eq("metadata.filename", name));

    }

    public void findByID(ObjectId fileId) {
        GridFSFindIterable resp = gridFSBucket.find(eq("_id", fileId));

    }

    public void findDescendent(String[] path) {
        GridFSFindIterable resp = gridFSBucket.find(eq("metadata.ancestors", path));

    }

    public void findByPath(String[] ancestors, String path) {
        GridFSFindIterable resp = gridFSBucket.find(and(eq("metadata.ancestors", ancestors), eq("metadata.path", path)));

    }

    public void rename(ObjectId fileId, String name) {
        gridFSBucket.rename(fileId, "mongodbTutorial");

    }

    public void delete(ObjectId fileId) {
        gridFSBucket.delete(fileId);
    }

    public void move(ObjectId fileId, String[] path) {
        // get fs.file and set upadate
    }
}
