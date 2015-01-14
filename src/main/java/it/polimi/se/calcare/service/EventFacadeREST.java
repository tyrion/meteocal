/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se.calcare.service;

import it.polimi.se.calcare.auth.AuthRequired;
import it.polimi.se.calcare.dto.EventCreationDTO;
import it.polimi.se.calcare.entities.Calendar;
import it.polimi.se.calcare.entities.City;
import it.polimi.se.calcare.entities.Event;
import it.polimi.se.calcare.entities.Forecast;
import it.polimi.se.calcare.entities.ForecastPK;
import it.polimi.se.calcare.entities.Participation;
import it.polimi.se.calcare.entities.ParticipationPK;
import it.polimi.se.calcare.entities.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
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
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.json.JSONException;

/**
 *
 * @author tyrion
 */
@Stateless
@Path("events")
public class EventFacadeREST extends AbstractFacade<Event> {

    @PersistenceContext(unitName = "it.polimi.se_CalCARE_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    public EventFacadeREST() {
        super(Event.class);
    }

    @AuthRequired
    @POST
    @Consumes({"application/xml", "application/json"})
    public void create(@Context SecurityContext sc, EventCreationDTO dto) {
        User user = (User) sc.getUserPrincipal();
        Event event = dto.event;
        event.setCreator(user);

        em.persist(event);
        em.flush();

        // We use a Map to avoid duplicated participations.
        Map<Integer,Participation> participations = new HashMap<>();
        for (int id : dto.invitedPeople)
            participations.put(id, new Participation(event.getId(), id));
        
        // The event creator always participates to the Event.
        Participation userP = new Participation(event.getId(), user.getId(), true);
        participations.put(user.getId(), userP);
        event.setParticipationCollection(participations.values());

//        try {
//            City city = cityCreator(event.getLocation());
//            forecastCreator(event.getLocation(), event.getStart(), event.getEnd(), city);
//        } catch (JSONException | IOException ex) {
//            Logger.getLogger(EventFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
//        }

    }

    @PUT
    @Path("{id}")
    @Consumes({"application/xml", "application/json"})
    public void edit(@Context SecurityContext sc, @PathParam("id") Integer id, Event entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@Context SecurityContext sc, @PathParam("id") Integer id) {
        User user = (User) sc.getUserPrincipal();
        Event event = super.find(id);
        if (event.getCreator().equals(user)) {
            super.remove(event);
        } else {
            throw new WebApplicationException(403);
        }
    }

    @AuthRequired
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public Event find(@Context SecurityContext sc, @PathParam("id") Integer id) {
        User user = (User) sc.getUserPrincipal();
        Event event = super.find(id);
        if (event == null) {
            throw new WebApplicationException(404);
        }

        if (!event.isPublic()) {
            try {
                em.createNamedQuery("Participation.forEvent", Participation.class)
                        .setParameter("calendar", user.getCalendar())
                        .setParameter("event", event)
                        .getSingleResult();
            } catch (NoResultException ex) {
                throw new WebApplicationException(403);
            }
        }
        return event;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    private City cityCreator(String location) throws JSONException, IOException {
        //Create the City in the DB
        City city = new GetWeather().createCity(location);

        try {
            em.persist(city);
            em.flush();
        } catch (PersistenceException ex) {}

        return city;
    }

    private void forecastCreator(String location, Date s, Date e, City city) throws JSONException, IOException {
        DateTime start = new DateTime(s);
        DateTime end = new DateTime(e);
        int cnt = Days.daysBetween(start, end).getDays();
        List<Forecast> toUpdate = new ArrayList<>();
        for (int i = 0; i <= cnt; i++) {
            Forecast forecast = new Forecast(
                    new ForecastPK(start.plusDays(i).toDate(), city.getId()),
                    0, 0, 0, 0);
            toUpdate.add(forecast);
            em.persist(forecast);
        }
        List<Forecast> toPush = new GetWeather().updateForecast(city, toUpdate);

        for (Forecast item : toPush) {
            em.persist(item);
        }
        em.flush();
    }
}
