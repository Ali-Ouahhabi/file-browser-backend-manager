/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ali.ouahhabi.dscp.local.mongo.file_manager.api.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.models.FileModel;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.services.FilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Ali Ouahhabi
 */
@RestController
@CrossOrigin
@RequestMapping("${api.prefix}/files")
public class FilesController {

    private final FilesService filesService;

    @Autowired
    public FilesController(FilesService filesService) {
        this.filesService = filesService;
    }

    @GetMapping("/list")
    public Map<String, String> listAll() {
        return this.filesService.findAll();
    }

    @RequestMapping("/download/{file_id}")
    public ResponseEntity<InputStreamResource> download(@PathVariable("file_id") String fileId) {
        try {

            GridFsResource resp = this.filesService.download(fileId);

            return ResponseEntity
                    .ok()
                    .contentType(
                            MediaType
                                    .valueOf(
                                            resp
                                                    .getOptions()
                                                    .getMetadata()
                                                    .get("type", String.class)
                                    )
                    )
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resp.getFilename() + "\""
                    )
                    .body(
                            new InputStreamResource(
                                    resp.getInputStream()
                            )
                    );

        } catch (IOException ex) {

            Logger.getLogger(FilesController.class.getName()).log(Level.SEVERE, null, ex);

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(value = "/dowlnload", method = RequestMethod.POST )
    public  ResponseEntity download(@RequestBody String [] fileId) {
        try {

            File resp = this.filesService.download(fileId);

            return ResponseEntity
                    .ok()
                    .contentType(
                            MediaType.valueOf("application/zip")
                    )
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resp.getName() + "\""
                    )
                    .body(
                            new InputStreamResource(
                                    new FileInputStream(resp)
                            )
                    );

        } catch (IOException ex) {

            Logger.getLogger(FilesController.class.getName()).log(Level.SEVERE, null, ex);

            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }    }

    @RequestMapping(value = "/uploads", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //, consumes = )
    public ResponseEntity upload(
            @RequestPart(value = "files") MultipartFile[] files,
            @RequestPart(value = "metadata") FileModel[] metadata) throws IOException {
            System.out.println("@@@@@@@@@@@@@@@@@@@@222");
            System.out.println("@@@@@@@@@@@@@@@@@@@@222");
            System.out.println("@@@@@@@@@@@@@@@@@@@@222");
        try {
            return new ResponseEntity<>(this.filesService.upload(files, metadata), HttpStatus.OK);
        } catch (Exception ex) {
            Logger.getLogger(FilesController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping("/rename/{file_id}/{to_name}")
    public void rename(@PathVariable("file_id") String fileId, @PathVariable("to_name") String name) {
        throw new org.springframework.web.server.MediaTypeNotSupportedStatusException("not implemented");
    }

    @RequestMapping(value = "/move/{file_id}", method = RequestMethod.PUT)
    public void move(@PathVariable("file_id") String fileId, @RequestPart("path") String[] files) {
        throw new org.springframework.web.server.MediaTypeNotSupportedStatusException("not implemented");
    }

    @RequestMapping(value = "/remove/{file_id}", method = RequestMethod.PUT)
    public void remove(@PathVariable("file_id") String fileId) {
        throw new org.springframework.web.server.MediaTypeNotSupportedStatusException("not implemented");
    }

}
