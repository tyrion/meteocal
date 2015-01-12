/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se.calcare.service;

import it.polimi.se.calcare.auth.AuthRequired;
import it.polimi.se.calcare.dto.EventCreationDTO;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
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
        List<Participation> participations = new ArrayList<>();
        for (int id : dto.invitedPeople)
            participations.add(new Participation(event.getId(), id));
        event.setParticipationCollection(participations);
        
        
//        try {
//            int id = cityCreator(event.getLocation());
//            forecastCreator(event.getLocation(), event.getStart(), event.getEnd(), id);
//        } catch (JSONException | IOException ex) {
//            Logger.getLogger(EventFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
    }

    @PUT
    @Path("{id}")
    @Consumes({"application/xml", "application/json"})
    public void edit(@PathParam("id") Integer id, Event entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public Event find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({"application/xml", "application/json"})
    public List<Event> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<Event> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
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

    private int cityCreator(String location) throws JSONException, IOException {
         //Create the City in the DB
        City city = new GetWeather().createCity(location);
        
        try {
            em.persist(city);
            em.flush();
        } catch (EntityExistsException ex) {}
        return city.getId();
    }

    private void forecastCreator(String city , Date s, Date e, int id) throws JSONException, IOException {
        DateTime start = new DateTime(s);
        DateTime end = new DateTime(e);
        int cnt=Days.daysBetween(start, end).getDays();
        List<Forecast> toUpdate= new ArrayList<>();
        for (int i=0; i<=cnt; i++) {
            Forecast forecast=new Forecast(new ForecastPK(start.plusDays(i).toDate(), id), 0, 0, 0, 0);
            toUpdate.add(forecast);
            em.persist(forecast);
        }
        List<Forecast> toPush = new GetWeather().updateForecast(toUpdate);
        
        for (Forecast item : toPush) {
            em.persist(item);
        }
        em.flush();
    }
}
