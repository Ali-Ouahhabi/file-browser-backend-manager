/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ali.ouahhabi.dscp.local.mongo.file_manager.api.models;

import org.springframework.web.bind.annotation.ModelAttribute;

/**
 *
 * @author Ali Ouahhabi
 */
public class FileSet {
    FileModel []files;

    public FileSet() {
    }

    public FileSet(FileModel[] files) {
        this.files = files;
    }

    
    public FileModel[] getFiles() {
        return files;
    }

    public void setFiles(FileModel[] files) {
        this.files = files;
    }
    
}
