/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se.calcare.auth;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import it.polimi.se.calcare.entities.User;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Priority;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author tyrion
 */

@Priority(Priorities.AUTHENTICATION)
@Provider
@AuthRequired
public class AuthFilter implements ContainerRequestFilter {
    
    @PersistenceContext(unitName = "it.polimi.se_CalCARE_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    
    private static final Pattern HEADER_PATTERN = Pattern.compile("Bearer (.+)");
    private static final JWTVerifier jwtVerifier = new JWTVerifier("SECR3T");

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        /* Map<String, Object> map = new HashMap<String, Object>();
        map.put("user", 1);
        System.out.println((new JWTSigner("SECR3T")).sign(map)); */
        
        String header = requestContext.getHeaderString("Authorization");
        if (header == null) {
            unauthorized(requestContext);
            return;
        }
        Map<String, Object> token;
        Matcher matcher = HEADER_PATTERN.matcher(header);
        String payload;
        
        matcher.find();
        
        payload = matcher.group(1);
        
        try {
            token = jwtVerifier.verify(payload);
        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalStateException | IOException | SignatureException | JWTVerifyException ex) {
            Logger.getLogger(AuthFilter.class.getName()).log(Level.SEVERE, null, ex);
            unauthorized(requestContext);
            return;
        }
        Integer id  = (Integer)token.get("user");
        User user = em.createNamedQuery("User.findById", User.class)
            .setParameter("id", id)
            .getSingleResult();
        
        SecurityContext sc = new SecurityContext(user);
        requestContext.setSecurityContext(sc);
        
        if (!sc.isUserInRole("users"))
            unauthorized(requestContext);        
    }
    
    private void unauthorized(ContainerRequestContext requestContext) {
        requestContext.abortWith(Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("Unauthorized")
                    .build());
    }
    
}
