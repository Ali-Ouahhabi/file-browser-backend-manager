/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ali.ouahhabi.dscp.local.mongo.file_manager.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.daos.FilesDao;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.models.FileModel;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Ali Ouahhabi
 */
@Service
public class FilesService {

    private final FilesDao filesDao;

    @Autowired
    FilesService(FilesDao filesDao) {
        this.filesDao = filesDao;
    }

    public List<String> upload(MultipartFile[] files, FileModel[] metadata) throws Exception {
        ObjectMapper ow = new ObjectMapper();
        List<GridFSUploadOptions> options = List.of(metadata).stream()
                .map(m -> new GridFSUploadOptions().metadata(ow.convertValue(m, Document.class)))
                .collect(Collectors.toList());
        List<String> index = new ArrayList<>();
        for (int i = 0; i < options.size(); i++) {
            index.add(filesDao.upload(files[i].getInputStream(), options.get(i)).toHexString());
        }
        index.forEach(System.out::println);
        return index;
    }
    
    public GridFsResource download(String fileId) throws IOException {
        return this.filesDao.download(this.filesDao.findByID(new ObjectId(fileId)));
    }
    
    public Map<String,String> findAll(){
        GridFSFindIterable result = this.filesDao.findAll();
        Map<String, String> resp = new HashMap<>();
        result.forEach(f->resp.put(f.getId().asObjectId().getValue().toHexString(), f.getFilename()));
        return resp;
    } 
}
