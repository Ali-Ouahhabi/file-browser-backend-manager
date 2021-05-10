/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ali.ouahhabi.dscp.local.mongo.file_manager.api.controllers;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.security.authentications.models.User;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.security.authentications.models.UserRegister;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Ali Ouahhabi
 */
@RestController
@CrossOrigin
@RequestMapping("${api.prefix}/user")
public class UserController {
    
    @Autowired
    UserService userService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity register(@RequestBody UserRegister user) {

        try {
            userService.addUser(user);
            User user_ = new User(user.getEmail() ,user.getPassword(), null);
            return login(user_);
        } catch (Exception ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
            return ResponseEntity.status(409).body(ex.getMessage());
        }
    }

    @RequestMapping("/login")
    public ResponseEntity login(@RequestBody User user) {
                System.out.println("##############################33");
        System.out.println("##############################33");
        System.out.println("##############################33");
        System.out.println("##############################33");
        System.out.println("##############################33");
        try {
            String jwt = userService.authenticate(user);
            HashMap<String, String> body = new HashMap<String, String>();
            body.put("jwt", jwt);
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
            return ResponseEntity.status(500).body("Internal Server ERROR");
        }
    }

    @RequestMapping("/logout")
    public ResponseEntity logout(@RequestBody String email) {
        boolean logout = userService.logoutUser(email);
        if(logout) return ResponseEntity.ok("");
        else return ResponseEntity.notFound().build();
    }
}
