/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ali.ouahhabi.dscp.local.mongo.file_manager.api.security.authentications.provider;

import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.security.authentications.dao.UserDao;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.security.authentications.UsernamePasswordAuthentication;
import org.ali.ouahhabi.dscp.local.mongo.file_manager.api.security.authentications.details.UserDetailsImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 *
 * @author Ali Ouahhabi
 */
@Component
public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider{
    
    @Autowired
    UserDao userDao;
    
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
         UserDetails user = new UserDetailsImplementation(userDao.getUser(authentication.getName()));
         
         if(passwordEncoder.matches((String)authentication.getCredentials(), user.getPassword())){

             return new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
         }
         else throw new BadCredentialsException("Error");
         
    }

    @Override
    public boolean supports(Class<?> authentication) {
       return UsernamePasswordAuthentication.class.equals(authentication);
    }
    
}
