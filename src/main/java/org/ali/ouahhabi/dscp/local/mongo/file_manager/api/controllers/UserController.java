/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ali.ouahhabi.dscp.local.mongo.file_manager.api.controllers;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.security.authentications.models.User;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.security.authentications.models.UserRegister;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.security.authentications.services.RefreshTokenAuthenticationService;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.security.authentications.services.TokenAuthenticationService;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

	@Autowired
	private RefreshTokenAuthenticationService refreshTokenAuthenticationService;
	
	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity register(@RequestBody UserRegister user) {

		try {
			userService.addUser(user);
			User user_ = new User(user.getEmail(), user.getPassword(), null);
			return login(user_);
		} catch (Exception ex) {
			Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
			return ResponseEntity.status(409).body(ex.getMessage());
		}
	}

	@RequestMapping("/login")
	public ResponseEntity login(@RequestBody User user) {
		try {
			return ResponseEntity.ok(userService.authenticate(user));
		} catch (Exception ex) {
			Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
			return ResponseEntity.status(500).body("Internal Server ERROR");
		}
	}

	@RequestMapping("/logout")
	public ResponseEntity logout(HttpServletRequest request) {
		String email = tokenAuthenticationService.getPrincipale(request);
		boolean logout = email != null?userService.logoutUser(email):false;
		if (logout)
			return ResponseEntity.ok("");
		else
			return ResponseEntity.notFound().build();
	}

	@RequestMapping("/refresh")
	public ResponseEntity<String> refresh(HttpServletRequest request) {
		Authentication u = refreshTokenAuthenticationService.getAuthentication(request);
		try {
			return ResponseEntity.ok(userService.authenticate(u));
		} catch (Exception e) {
			Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, e);
			return ResponseEntity.status(409).body(e.getMessage());
		}
	}
}
