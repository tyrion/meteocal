/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se.calcare.service;

import it.polimi.se.calcare.auth.AuthRequired;
import it.polimi.se.calcare.entities.Participation;
import it.polimi.se.calcare.entities.ParticipationPK;
import it.polimi.se.calcare.entities.User;
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
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author tyrion
 */
@Stateless
@Path("it.polimi.se.calcare.entities.participation")
public class ParticipationFacadeREST extends AbstractFacade<Participation> {

    @PersistenceContext(unitName = "it.polimi.se_CalCARE_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    private ParticipationPK getPrimaryKey(PathSegment pathSegment) {
        /*
         * pathSemgent represents a URI path segment and any associated matrix parameters.
         * URI path part is supposed to be in form of 'somePath;event=eventValue;calendarsId=calendarsIdValue'.
         * Here 'somePath' is a result of getPath() method invocation and
         * it is ignored in the following code.
         * Matrix parameters are used as field names to build a primary key instance.
         */
        it.polimi.se.calcare.entities.ParticipationPK key = new it.polimi.se.calcare.entities.ParticipationPK();
        javax.ws.rs.core.MultivaluedMap<String, String> map = pathSegment.getMatrixParameters();
        java.util.List<String> event = map.get("event");
        if (event != null && !event.isEmpty()) {
            key.setEvent(new java.lang.Integer(event.get(0)));
        }
        java.util.List<String> calendarsId = map.get("calendarsId");
        if (calendarsId != null && !calendarsId.isEmpty()) {
            key.setCalendarsId(new java.lang.Integer(calendarsId.get(0)));
        }
        return key;
    }

    public ParticipationFacadeREST() {
        super(Participation.class);
    }

    @POST
    @Override
    @Consumes({"application/xml", "application/json"})
    public void create(Participation entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({"application/xml", "application/json"})
    public void edit(@Context SecurityContext sc,
            @PathParam("id") PathSegment id, Participation changed) {
        ParticipationPK key = getPrimaryKey(id);
        User user = (User) sc.getUserPrincipal();
        Participation p = super.find(key);
        if (p.getCalendar().equals(user.getCalendar())) {
            p.setAccepted(changed.getAccepted());
            super.edit(p);
        }
    }

    @AuthRequired
    @DELETE
    @Path("{id}")
    public void remove(@Context SecurityContext sc, @PathParam("id") PathSegment id) {
        ParticipationPK key = getPrimaryKey(id);
        User user = (User) sc.getUserPrincipal();
        Participation p = super.find(key);
        if (p.getCalendar().equals(user.getCalendar()))
            super.remove(p);
        else
            throw new WebApplicationException(403);
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public Participation find(@PathParam("id") PathSegment id) {
        it.polimi.se.calcare.entities.ParticipationPK key = getPrimaryKey(id);
        return super.find(key);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
