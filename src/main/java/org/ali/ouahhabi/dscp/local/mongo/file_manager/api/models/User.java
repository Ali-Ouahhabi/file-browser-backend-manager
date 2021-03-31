/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ali.ouahhabi.dscp.local.mongo.file_manager.api.models;

/**
 *
 * @author Ali Ouahhabi
 */
public class User {

    private String email;
    private String role;
    private String password;

    public User(String email, String role, String password) {
        this.email = email;
        this.role = role;
        this.password = password;
    } 

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    } 

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
}