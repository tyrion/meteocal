/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se.calcare.service;

import com.auth0.jwt.JWTSigner;
import static it.polimi.se.calcare.auth.AuthFilter.SECRET;
import it.polimi.se.calcare.auth.AuthRequired;
import it.polimi.se.calcare.entities.User;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.ParseConversionEvent;

/**
 *
 * @author tyrion
 */
@Stateless
@Path("/users") @Produces("application/json")
public class UserFacadeREST extends AbstractFacade<User> {
    @PersistenceContext(unitName = "it.polimi.se_CalCARE_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    public UserFacadeREST() {
        super(User.class);
    }
    
    private String getActivationToken(User user) {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("activate", user.getId());
        return (new JWTSigner(SECRET)).sign(map);
    }

    @POST
    public Response add(@Context UriInfo ui, User entity) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Because Java.
        entity.setPassword(entity.getPassword());
        super.create(entity);
        em.flush();
        
        String URL = ui.getBaseUri().resolve("auth/activate?token=") +
            getActivationToken(entity);
        
        SendMail.Mail(new String[] { entity.getEmail() },
            "Registration to CalCARE",
            "Welcome to CalCARE, click this link to confirm your account: " +
            String.format("<a href=\"%s\">%s</a>\n", URL, URL));
        return Response
            .status(201)
            .entity(entity)
            .build();
    }

    @AuthRequired
    @PUT
    @Path("me")
    @Consumes({"application/json"})
    public void edit(@Context SecurityContext sc, User entity) {
        super.edit(entity);
    }

    @AuthRequired
    @DELETE
    @Path("me")
    public void remove(@Context SecurityContext sc) {
        User user = (User) sc.getUserPrincipal();
        super.remove(user);
    }

    @AuthRequired
    @GET
    @Path("me")
    @Produces({"application/json"})
    public User get(@Context SecurityContext sc) {
        User user = (User) sc.getUserPrincipal();
        return super.find(user.getId());
    }

    @AuthRequired
    @GET
    @Override
    @Produces({"application/xml", "application/json"})
    public List<User> findAll() {
        return super.findAll();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
