/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ali.ouahhabi.dscp.local.mongo.file_manager.api.security.authentications.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 *
 * @author Ali Ouahhabi
 */
@Configuration
public class RefreshTokenAuthenticationService {

    private final String TOKEN_PREFIX = "Bearer";
    private final String HEADER_STRING = "Authorization";
    
    @Value("${refreshTokenExpirationInMs}")
    private long refreshExpirationInMs;
    @Value("${refreshTokenSecret}")
    private String refreshSecret;

    public String generateToken(Authentication authentication) {
        String userPrincipal = (String) authentication.getPrincipal();
        String grantedAuthority = (String) authentication
                .getAuthorities()
                .iterator()
                .next()
                .getAuthority();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpirationInMs);
        return Jwts.builder()
                .setSubject(userPrincipal + ":-:" + grantedAuthority)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, refreshSecret)
                .compact();
    }

    public Authentication getAuthenticationUser(String token) {
        String[] user_authority = Jwts.parser()
                .setSigningKey(refreshSecret)
                .parseClaimsJws(
                        token
                                .replace(TOKEN_PREFIX, "")
                                .trim()
                )
                .getBody()
                .getSubject()
                .split(":-:");
        return new UsernamePasswordAuthenticationToken(user_authority[0], null, List.of(() -> user_authority[1]));
    }
    
    public Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        if (token != null && !token.isEmpty()) return getAuthenticationUser(token);
        return null;
    }
       
}
