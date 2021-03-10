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
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Filters;
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
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.gridfs.GridFsResource;

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
        if (!options.getMetadata().containsKey("name")
                || !options.getMetadata().containsKey("path")
                || !options.getMetadata().containsKey("size")
                || !options.getMetadata().containsKey("type")) {
            throw new Exception("Metadata must include filename path ancestors ");
        }
        return gridFSBucket.uploadFromStream(options.getMetadata().get("name", String.class), in, options);
    }

    public GridFsResource download(GridFSFile file) throws FileNotFoundException, IOException {
        OutputStream output = new FileOutputStream(file.getFilename());
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(file.getId());
        GridFsResource gridFsResource = new GridFsResource(file, gridFSDownloadStream);

        return gridFsResource;
    }

    public GridFSFile findByName(String name) {
        return gridFSBucket.find(eq("metadata.filename", name)).iterator().tryNext();
    }

    public GridFSFile findByID(ObjectId fileId) {
        return gridFSBucket.find(eq("_id", fileId)).iterator().tryNext();

    }
    
    public GridFSFindIterable findByID(ObjectId []fileId) {
        return gridFSBucket.find(Filters.in("_id", fileId));

    }
    
    public GridFSFindIterable findAll(){
        return gridFSBucket.find();
    }

    public GridFSFile findDescendent(String[] path) {
        return gridFSBucket.find(eq("metadata.path", path)).iterator().tryNext();

    }

    public GridFSFindIterable findByPath(String[] path, String name) {
        return gridFSBucket.find(and(eq("metadata.path", path), eq("metadata.name", name)));
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
