/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se.calcare.service;

import it.polimi.se.calcare.entities.Participation;
import it.polimi.se.calcare.entities.ParticipationPK;
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
import javax.ws.rs.core.PathSegment;

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
    public void edit(@PathParam("id") PathSegment id, Participation entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") PathSegment id) {
        it.polimi.se.calcare.entities.ParticipationPK key = getPrimaryKey(id);
        super.remove(super.find(key));
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public Participation find(@PathParam("id") PathSegment id) {
        it.polimi.se.calcare.entities.ParticipationPK key = getPrimaryKey(id);
        return super.find(key);
    }

    @GET
    @Override
    @Produces({"application/xml", "application/json"})
    public List<Participation> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<Participation> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
