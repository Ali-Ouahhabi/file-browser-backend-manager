/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ali.ouahhabi.dscp.local.mongo.file_manager.api.controllers;

import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Ali Ouahhabi
 */
@RestController
@RequestMapping("${api.prefix}/files")
public class FilesController {

    @RequestMapping("/dowlnload/${file_id}")
    public void download(@PathVariable("file_id") String fileId) {
        throw new org.springframework.web.server.MediaTypeNotSupportedStatusException("not implemented");
    }

    @RequestMapping("/dowlnload")
    public void download(@RequestBody Map<String, String[]> fileId) {
        throw new org.springframework.web.server.MediaTypeNotSupportedStatusException("not implemented");
    }

    @RequestMapping("/upload")
    public void upload(@RequestPart("files") MultipartFile[] files) {
        throw new org.springframework.web.server.MediaTypeNotSupportedStatusException("not implemented");
    }

    @RequestMapping("/rename/${file_id}/${to_name}")
    public void rename(@PathVariable("file_id") String fileId, @PathVariable("to_name") String name) {
        throw new org.springframework.web.server.MediaTypeNotSupportedStatusException("not implemented");
    }

    @RequestMapping("/move/${file_id}")
    public void move(@PathVariable("file_id") String fileId, @RequestPart("path") String[] files) {
        throw new org.springframework.web.server.MediaTypeNotSupportedStatusException("not implemented");
    }

    @RequestMapping("/remove/${file_id}")
    public void remove(@PathVariable("file_id") String fileId) {
        throw new org.springframework.web.server.MediaTypeNotSupportedStatusException("not implemented");
    }
}
