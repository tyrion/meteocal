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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author tyrion
 */
@Stateless
@Path("participations")
public class ParticipationFacadeREST extends AbstractFacade<Participation> {

    @PersistenceContext(unitName = "it.polimi.se_CalCARE_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    public ParticipationFacadeREST() {
        super(Participation.class);
    }

    @AuthRequired
    @PUT
    @Path("{id}")
    @Consumes({"application/json"})
    public void edit(@Context SecurityContext sc,
            @PathParam("id") Integer eventId, Participation changed) {
        User user = (User) sc.getUserPrincipal();
        ParticipationPK key = new ParticipationPK(eventId, user.getCalendar().getId());
        Participation p = super.find(key);
        
        p.setAccepted(changed.getAccepted());
        super.edit(p);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
