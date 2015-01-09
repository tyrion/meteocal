/*
 * The MIT License
 *
 * Copyright 2015 tyrion.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package it.polimi.se.calcare.service;

import com.auth0.jwt.JWTSigner;
import it.polimi.se.calcare.auth.AuthFilter;
import it.polimi.se.calcare.auth.Password;
import it.polimi.se.calcare.entities.User;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

/**
 *
 * @author tyrion
 */
@Stateless
@Path("auth")
public class AuthREST {
    @PersistenceContext(unitName = "it.polimi.se_CalCARE_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    
    @POST
    @Path("login")
    @Produces("application/json")
    public String login(@FormParam("email") String email, @FormParam("password") String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (email == null || password == null)
            throw new WebApplicationException(401);
        User user;
        try {
            user = em.createNamedQuery("User.findByEmail", User.class)
                .setParameter("email", email)
                .getSingleResult();
            
            if (Password.check(password, user.getPassword())) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("user", user.getId());
                String token = (new JWTSigner(AuthFilter.SECRET)).sign(map);
                return String.format("{\"token\":\"%s\"}", token);
            }
        } catch (javax.persistence.NoResultException ex) {}
        throw new WebApplicationException(401);
    }
}
