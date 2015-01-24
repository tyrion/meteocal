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
import it.polimi.se.calcare.entities.NotificationType;
import it.polimi.se.calcare.entities.Participation;
import it.polimi.se.calcare.entities.User;
import it.polimi.se.calcare.helpers.NotificationHelper;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletContext;
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
import javax.ws.rs.core.UriInfo;
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

    private void updateParticipations(User user, EventCreationDTO dto, UriInfo ui) {
        Event event = dto.event;
        URI link = ui.getBaseUri().resolve("..#/events/" + event.getId());

        // Ensure that the creator of the event is not among the invited people.
        dto.invitedPeople.remove(user.getId());

        Set<User> oldUsers = new HashSet<>(em.createQuery(
                "SELECT DISTINCT p.calendar.owner FROM Participation p WHERE p.event = :event",
                User.class).setParameter("event", event).getResultList());

        if (!dto.invitedPeople.isEmpty()) {
            Set<User> newUsers = new HashSet<>(em.createQuery(
                    "SELECT DISTINCT u FROM User u WHERE u.active = TRUE AND u.calendar.id IN :ids",
                    User.class).setParameter("ids", dto.invitedPeople).getResultList());

            Set<User> toInvite = new HashSet<>(newUsers);
            toInvite.removeAll(oldUsers);

            NotificationHelper helper = new NotificationHelper(em, NotificationType.Enum.INVITATION, event);
            for (User u : toInvite) {
                em.persist(new Participation(event, u.getCalendar()));
                helper.sendTo(u, link, event.getName(), user.getFullName());
            }

            oldUsers.removeAll(newUsers);
        }

        if (!oldUsers.remove(user)) {
            em.persist(new Participation(event, user.getCalendar(), true));
        }

        if (!oldUsers.isEmpty())
            em.createQuery("DELETE FROM Participation p WHERE p.calendar.owner IN :users")
                    .setParameter("users", oldUsers).executeUpdate();

    }

    @AuthRequired
    @POST
    @Consumes({"application/xml", "application/json"})
    public void create(@Context SecurityContext sc, @Context UriInfo ui, EventCreationDTO dto) {
        User user = (User) sc.getUserPrincipal();
        Event event = dto.event;
        event.setCreator(user);

        em.persist(event);
        em.flush();

        updateParticipations(user, dto, ui);

        try {
            City city = new GetWeather().cityParser(event.getLocation());
            em.merge(city);
            List<Forecast> toBind = forecastCreator(event.getStart(), event.getEnd(), city);
            event.setForecastCollection(toBind);
        } catch (JSONException | IOException ex) {
            Logger.getLogger(EventFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        em.persist(event);
        em.flush();
    }

    @AuthRequired
    @PUT
    @Path("{id}")
    @Consumes({"application/xml", "application/json"})
    public void edit(@Context SecurityContext sc, @Context UriInfo ui,
            @PathParam("id") Integer id, EventCreationDTO dto) throws IOException, MalformedURLException, JSONException {
        User user = (User) sc.getUserPrincipal();
        Event event = super.find(id);

        em.detach(event);
        dto.event.setParticipationCollection(event.getParticipationCollection());

        if (!event.getCreator().equals(user))
            throw new WebApplicationException(403);

        dto.event.setCreator(user);

        String oldLocation = event.getLocation();
        String newLocation = dto.event.getLocation();

        City city = new GetWeather().cityParser(newLocation);
        if (!(oldLocation.equals(newLocation))) {
            em.merge(city);
        }

        List<Forecast> toBind = forecastCreator(dto.event.getStart(), dto.event.getEnd(), city);
        event.setForecastCollection(toBind);

        em.merge(dto.event);
        em.flush();

        updateParticipations(user, dto, ui);
        em.detach(dto.event);
    }

    @AuthRequired
    @DELETE
    @Path("{id}")
    public void remove(@Context SecurityContext sc, @PathParam("id") Integer id) {
        User user = (User) sc.getUserPrincipal();
        Event event = super.find(id);
        if (event.getCreator().equals(user))
            super.remove(event);
        else
            throw new WebApplicationException(403);
    }

    @AuthRequired
    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public Event find(@Context SecurityContext sc, @PathParam("id") Integer id) {
        User user = (User) sc.getUserPrincipal();
        Event event = super.find(id);
        if (event == null)
            throw new WebApplicationException(404);

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

    private List<Forecast> forecastCreator(Date s, Date e, City city) throws JSONException, IOException {
        DateTime start = new DateTime(s);
        DateTime end = new DateTime(e);
        int cnt = Days.daysBetween(start, end).getDays();
        List<Forecast> toUpdate = new ArrayList<>();

        for (int i = 0; i <= cnt; i++) {
            Forecast forecast = new Forecast(
                    new ForecastPK(start.plusDays(i).toDate(), city.getId()),
                    0, 0, 0, 0);
            toUpdate.add(forecast);
        }
        List<Forecast> toPush = new GetWeather().updateForecast(city, toUpdate);

        for (Forecast item : toPush) {
            em.merge(item);
        }
        em.flush();
        return toPush;

    }

}
