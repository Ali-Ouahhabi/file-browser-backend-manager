/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ali.ouahhabi.dscp.local.mongo.file_manager.api.services;

import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.security.authentications.dao.UserDao;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.security.authentications.models.User;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.security.authentications.models.UserRegister;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.controllers.UserController;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.security.authentications.UsernamePasswordAuthentication;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.security.authentications.services.RefreshTokenAuthenticationService;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.security.authentications.services.TokenAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author Ali Ouahhabi
 */
@Service
public class UserService {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private TokenAuthenticationService tokenAuthenticationService;
    
    @Autowired
    private RefreshTokenAuthenticationService refreshTokenAuthenticationService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserDao userDao;

    public User getUser(String email) throws Exception{
        User user =  userDao.getUser(email);
        if(user!=null) return user;
        else throw new Exception("user unfound");
    }
    
    public boolean addUser(UserRegister user) throws Exception{
        UserRegister user_ = new UserRegister(user);
        user_.setPassword(passwordEncoder.encode(user.getPassword()));
        user_.setRole("USER");
        return userDao.addUser(user_);
    }
    
    public HashMap<String,String> authenticate(User user) throws Exception{
        Authentication upa = new UsernamePasswordAuthentication(user.getEmail(), user.getPassword());
        upa = authenticationManager.authenticate(upa);
        String jwt = tokenAuthenticationService.generateToken(upa); 
        String refresh = refreshTokenAuthenticationService.generateToken(upa);
        if(userDao.createUserSession(user.getEmail(), refresh)){
        	HashMap<String,String> resp = new HashMap<String, String>(2);
        	resp.put("jwt", jwt);
        	resp.put("refresh", refresh);
            return resp;
        }else throw new Exception("Server Error");
    }
    
    public String authenticate(Authentication user) throws Exception{
    	return tokenAuthenticationService.generateToken(user); 
    } 
    
    public boolean logoutUser(HttpServletRequest request) {
    	String email = tokenAuthenticationService.getPrincipale(request);
        return email != null?userDao.deleteUserSessions(email):false;
    }
    
    public String refresh(HttpServletRequest request) throws Exception {
		return this.authenticate(
				refreshTokenAuthenticationService.getAuthentication(request));
		
	}
    
}
