/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se.calcare.service;

import it.polimi.se.calcare.auth.AuthRequired;
import it.polimi.se.calcare.entities.Calendar;
import it.polimi.se.calcare.entities.Event;
import it.polimi.se.calcare.entities.Participation;
import it.polimi.se.calcare.entities.User;
import it.polimi.se.calcare.helpers.ObjectGraphHelper;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

/**
 *
 * @author tyrion
 */
@Stateless
@Path("calendars")
public class CalendarFacadeREST extends AbstractFacade<Calendar> {

    @PersistenceContext(unitName = "it.polimi.se_CalCARE_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    public CalendarFacadeREST() {
        super(Calendar.class);
    }

    @AuthRequired
    @PUT
    @Path("me")
    @Consumes({"application/xml", "application/json"})
    public void edit(@Context SecurityContext sc, Calendar entity) {
        User user = (User) sc.getUserPrincipal();
        entity.setId(user.getCalendar().getId());
        super.edit(entity);
    }

    @AuthRequired
    @GET
    @Path("{id}")
    @Produces({"application/json"})
    public String find(@Context SecurityContext sc,
            @PathParam("id") Integer id) {
        User user = (User) sc.getUserPrincipal();

        Calendar calendar = em.createNamedQuery("Calendar.findById", Calendar.class)
                .setParameter("id", id).getSingleResult();
        int userCal = user.getCalendar().getId();

        // public events and if public calendar also private events NOT in common
        List<Participation> ps = em.createNativeQuery(
                "SELECT DISTINCT p1.* FROM events e "
                + "INNER JOIN participations p1 ON (e.id = p1.event) "
                + "INNER JOIN calendars c ON (p1.calendars_id = c.id) "
                + "LEFT OUTER JOIN participations p2 ON (p1.event = p2.event AND p2.calendars_id = ?) "
                + "WHERE c.id = ? AND (e.public = 1 OR (c.public = 1 AND p2.event IS NULL))",
                Participation.class).setParameter(1, userCal).setParameter(2, id)
                .getResultList();
        List<Participation> commonPrivate = em.createNativeQuery(
                "SELECT DISTINCT p1.* FROM events e "
                + "INNER JOIN participations p1 ON (e.id = p1.event) "
                + "INNER JOIN participations p2 ON (p1.event = p2.event) "
                + "WHERE p1.calendars_id = ? AND p2.calendars_id = ? "
                + "AND e.public = 0", Participation.class).setParameter(1, id)
                .setParameter(2, userCal).getResultList();

        Set<Participation> result = new HashSet<>(commonPrivate);
        for (Participation p : ps) {
            Event e = p.getEvent();
            if (e.isPublic())
                p.setEvent(e.asPrivate());
            result.add(p);
            em.detach(p);
        }

        em.detach(calendar);
        calendar.setParticipations(result);

        return ObjectGraphHelper.render(calendar, Calendar.class);
    }

    @GET
    @AuthRequired
    @Path("me")
    @Produces({"application/json"})
    public String myCalendar(@Context SecurityContext sc) {
        User user = (User) sc.getUserPrincipal();
        Calendar calendar = em.createNamedQuery("Calendar.findById", Calendar.class)
                .setParameter("id", user.getCalendar().getId())
                .getSingleResult();
        return ObjectGraphHelper.render(calendar, Calendar.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
