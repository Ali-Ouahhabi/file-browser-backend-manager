/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ali.ouahhabi.dscp.local.mongo.file_manager.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.daos.FilesDao;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.models.FileModel;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.security.core.context.SecurityContextHolder;
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
        String UserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<GridFSUploadOptions> options = List
                .of(metadata)
                .stream()
                .map(m -> {
                    m.setUserId(UserId);
                    return new GridFSUploadOptions().metadata(ow.convertValue(m, Document.class));
                })
                .collect(Collectors.toList());
        List<String> index = new ArrayList<>();
        for (int i = 0; i < options.size(); i++) {
            index.add(filesDao.upload(files[i].getInputStream(), options.get(i)).toHexString());
        }
        index.forEach(System.out::println);
        return index;
    }

    public GridFsResource download(String fileId) throws IOException {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return this.filesDao.download(this.filesDao.findByID(new ObjectId(fileId),userId));
    }

    public File download(String[] fileId) throws IOException {
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ObjectId[] re = new ObjectId[fileId.length];
        List<ObjectId> fids = Stream.of(fileId).map((t) -> {
            return new ObjectId(t); //To change body of generated lambdas, choose Tools | Templates.
        }).collect(Collectors.toList());
        fids.toArray(re);
        GridFSFindIterable files = this.filesDao.findByID(re,userId);

        return zipFiles(files);
    }

    public String findAll() {
    	String UserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String result = this.filesDao.initUserFiles(UserId);
        return result;
    }

    private File zipFiles(GridFSFindIterable files) throws FileNotFoundException, IOException {

        Path tempFile = Files.createTempFile("compressesd", ".zip");

        FileOutputStream fos = new FileOutputStream(tempFile.toFile());
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        GridFsResource fsResource;
        for (GridFSFile srcFile : files) {
            fsResource = this.filesDao.download(srcFile);

            InputStream fis = fsResource.getInputStream();
            try {
                ZipEntry zipEntry = new ZipEntry(srcFile.getFilename());
                zipOut.putNextEntry(zipEntry);
            } catch (Exception e) {
                ZipEntry zipEntry = new ZipEntry("0_"+srcFile.getFilename());
                zipOut.putNextEntry(zipEntry);
            }

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        }
        zipOut.close();
        fos.close();

        return tempFile.toFile();
    }
}
