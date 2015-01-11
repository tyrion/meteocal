/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se.calcare.service;

import it.polimi.se.calcare.entities.City;
import it.polimi.se.calcare.entities.Event;
import it.polimi.se.calcare.entities.Forecast;
import it.polimi.se.calcare.entities.ForecastPK;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.joda.time.DateTime;
import org.joda.time.Days;

/**
 *
 * @author tyrion
 */
@Stateless
@Path("it.polimi.se.calcare.entities.event")
public class EventFacadeREST extends AbstractFacade<Event> {
    @PersistenceContext(unitName = "it.polimi.se_CalCARE_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    public EventFacadeREST() {
        super(Event.class);
    }

    // Forecast[] getWeather(start, end, coords)
    @POST
    @Override
    @Consumes({"application/xml", "application/json"})
    public void create(Event entity) {

        //Create the City in the DB
        int id=cityCreator(entity.getLocation());
        if (id < 0) //Errore nell'inserimento della nuova città
        //Create the forecast(s) associated with the event
        forecastCreator(entity.getLocation(), entity.getStart(), entity.getEnd(), id);
        super.create(entity);
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

    private Date Date(String get) {
        java.util.Date date = new java.util.Date(get);
        return date;
    }

    private int cityCreator(String location) {
         //Create the City in the DB
        try {
            return (new GetWeather().createCity(location));
        } catch (Exception ex) {
            Logger.getLogger(EventFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }    
            return -1;
    }

    private void forecastCreator(String city , Date s, Date e, int id) {
        DateTime start = new DateTime(s);
        DateTime end = new DateTime(e);
        int cnt=Days.daysBetween(start, end).getDays();
        for (int i=0; i<=cnt; i++) {
            Forecast forecast=new Forecast(new ForecastPK(start.plusDays(i).toDate(), id), 0, 0, 0, 0);
            em.persist(forecast);
        }
    }
}
