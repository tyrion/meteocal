/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se.calcare.service;

import com.auth0.jwt.internal.com.fasterxml.jackson.databind.ObjectMapper;
import it.polimi.se.calcare.auth.AuthRequired;
import it.polimi.se.calcare.entities.Calendar;
import it.polimi.se.calcare.entities.Event;
import it.polimi.se.calcare.entities.Participation;
import it.polimi.se.calcare.entities.User;
import it.polimi.se.calcare.helpers.JWTHelper;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.glassfish.jersey.media.multipart.FormDataParam;

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
    public Set<Event> find(@Context SecurityContext sc,
            @PathParam("id") Integer id) {
        User user = (User) sc.getUserPrincipal();
        // public events and if public calendar also private events NOT in common
        int userCal = user.getCalendar().getId();
        List<Event> events = em.createNativeQuery(
                "SELECT DISTINCT e.* FROM events e "
                + "INNER JOIN participations p1 ON (e.id = p1.event) "
                + "INNER JOIN calendars c ON (p1.calendars_id = c.id) "
                + "LEFT OUTER JOIN participations p2 ON (p1.event = p2.event AND p2.calendars_id = ?) "
                + "WHERE c.id = ? AND (e.public = 1 OR (c.public = 1 AND p2.event IS NULL))",
                Event.class).setParameter(1, userCal).setParameter(2, id)
                .getResultList();
        List<Event> commonPrivate = em.createNativeQuery(
                "SELECT DISTINCT e.* FROM events e "
                + "INNER JOIN participations p1 ON (e.id = p1.event) "
                + "INNER JOIN participations p2 ON (p1.event = p2.event) "
                + "WHERE p1.calendars_id = ? AND p2.calendars_id = ? "
                + "AND e.public = 0", Event.class).setParameter(1, id)
                .setParameter(2, userCal).getResultList();

        Set<Event> result = new HashSet<>(commonPrivate);
        for (Event e : events)
            result.add(e.isPublic() ? e : e.asPrivate());
        return result;
    }

    @GET
    @AuthRequired
    @Path("mine")
    @Produces({"application/json"})
    public Calendar myCalendar(@Context SecurityContext sc) {
        User user = (User) sc.getUserPrincipal();
        return user.getCalendar();
    }

    @GET
    @AuthRequired
    @Path("me")
    @Produces({"application/json"})
    public List<Event> myEvents(@Context SecurityContext sc) {
        User user = (User) sc.getUserPrincipal();
        List<Event> events = em.createNamedQuery("Event.myCalendar", Event.class)
                .setParameter("calendar", user.getCalendar())
                .getResultList();
        for (Event e : events)
            e.setForecastCollection(e.getWorstWeather());
        return events;
    }

    @GET
    @AuthRequired
    @Path("export")
    @Produces({"application/vnd.calcare.signed-calendar"})
    public Response exportLOL(@Context SecurityContext sc) {
        User user = (User) sc.getUserPrincipal();
        String data = JWTHelper.encode("calendar", user.getCalendar());
        return Response.ok(data).header("Content-Disposition",
                "attachment; filename=Calendar.meteocal").build();
    }

    private static final String importEventsQuery = "INSERT INTO events "
            + "(`id`, `name`, description, `start`, `end`, location, `public`,"
            + "outdoor, creator) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ON "
            + "DUPLICATE KEY UPDATE `name`=VALUES(`name`), description="
            + "VALUES(description), `start`=VALUES(`start`), "
            + "`end`=VALUES(`end`), location=VALUES(location), `public`="
            + "VALUES(`public`), outdoor=VALUES(outdoor), creator=VALUES(creator)";

    private static String streamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @POST
    @Path("import")
    @AuthRequired
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public void importcalendar(@Context SecurityContext sc,
            @FormDataParam("file") InputStream stream) throws Exception {
        User user = (User) sc.getUserPrincipal();
        String data = streamToString(stream);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> decoded = JWTHelper.decode(data);
        Calendar importedCal = objectMapper.convertValue(decoded.get("calendar"), Calendar.class);

        if (!user.equals(importedCal.getOwner()))
            throw new WebApplicationException(Response.Status.FORBIDDEN);

        Collection<Participation> ps = importedCal.getParticipations();
        Map<Event, Participation> participations = ps.stream()
                .collect(Collectors.toMap(p -> p.getEvent(), p -> p));

        List<Event> events = ps.stream().map(p -> p.getEvent())
                .filter(e -> user.equals(e.getCreator()))
                .collect(Collectors.toList());

        if (events != null) {
            Connection connection = em.unwrap(Connection.class);
            PreparedStatement stmt = connection.prepareStatement(importEventsQuery);

            for (Event e : events) {
                stmt.setInt(1, e.getId());
                stmt.setString(2, e.getName());
                stmt.setString(3, e.getDescription());
                stmt.setDate(4, new java.sql.Date(e.getStart().getTime()));
                stmt.setDate(5, new java.sql.Date(e.getEnd().getTime()));
                stmt.setString(6, e.getLocation());
                stmt.setBoolean(7, e.isPublic());
                stmt.setBoolean(8, e.getOutdoor());
                stmt.setInt(9, user.getId());
                stmt.addBatch();
            }
            stmt.executeBatch();

        }

        events = em.createQuery("SELECT e FROM Event e WHERE e.id IN :events", Event.class)
                .setParameter("events", participations.keySet().stream()
                        .map(e -> e.getId()).collect(Collectors.toList())).getResultList();
        events.stream().map(e -> participations.get(e)).forEach(p -> em.merge(p));
    }

    @GET
    @AuthRequired
    @Produces({"application/xml", "application/json"})
    public List<Calendar> search(@Context SecurityContext sc, @QueryParam("search") String search) {
        User user = (User) sc.getUserPrincipal();
        List<Calendar> cals = getEntityManager().createNamedQuery("Calendar.search", Calendar.class)
                .setParameter("search", '%' + search + '%')
                .setParameter("currentUser", user.getId()) //delete the current user from results
                .getResultList();
        if (cals.size() > 10) {
            cals = cals.subList(0, 10); //let's cut the list to 10 results
        }

        //let's set a null the partecipationsList - we shouldn't return it to other users
        for (Calendar c : cals) {
            c.setParticipations(null);
        }
        return cals;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
