/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ali.ouahhabi.dscp.local.mongo.file_manager.api.models;

import org.bson.codecs.pojo.annotations.BsonProperty;

/**
 *
 * @author Ali Ouahhabi
 * @email ali.ohhb@gmail.com
 */


public class FileModel {

    @BsonProperty(value = "path")
    private String path;
    @BsonProperty(value = "lastModified")
    private Long lastModified;
    @BsonProperty(value = "lastModifiedDate")
    private String lastModifiedDate;
    @BsonProperty(value = "name")
    private String name;
    @BsonProperty(value = "size")
    private Long size;
    @BsonProperty(value = "type")
    private String type;
    @BsonProperty(value = "userId")
    private String userId;

    public FileModel() {
    }

    public FileModel(String path, Long lastModified, String lastModifiedDate, String name, Long size, String type) {
        this.path = path;
        this.lastModified = lastModified;
        this.lastModifiedDate = lastModifiedDate;
        this.name = name;
        this.size = size;
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getLastModified() {
        return lastModified;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
