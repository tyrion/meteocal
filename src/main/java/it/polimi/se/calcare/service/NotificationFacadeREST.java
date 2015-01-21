/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se.calcare.service;

import it.polimi.se.calcare.auth.AuthRequired;
import it.polimi.se.calcare.entities.Event;
import it.polimi.se.calcare.entities.Notification;
import it.polimi.se.calcare.entities.User;
import java.util.List;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author tyrion
 */
@Stateless
@Path("notifications")
public class NotificationFacadeREST extends AbstractFacade<Notification> {
    @PersistenceContext(unitName = "it.polimi.se_CalCARE_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    public NotificationFacadeREST() {
        super(Notification.class);
    }
    
    @GET
    @AuthRequired
    @Produces({"application/json"})
    public List<Notification> findAll(@Context SecurityContext sc) {
        User user = (User) sc.getUserPrincipal();
        return em.createNamedQuery("Notification.findUserNotifs", Notification.class)
                .setParameter("currentUser", user.getId())
                .getResultList();
    }
    
    @AuthRequired
    @DELETE
    @Path("{id}")
    public void remove(@Context SecurityContext sc, @PathParam("id") Integer id) {
        User user = (User) sc.getUserPrincipal();
        Notification n = super.find(id);
        if (!n.getUser().equals(user))
            throw new WebApplicationException(403);
        super.remove(n);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
