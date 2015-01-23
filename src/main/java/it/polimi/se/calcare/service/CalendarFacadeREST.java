/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se.calcare.service;

import it.polimi.se.calcare.auth.AuthRequired;
import it.polimi.se.calcare.entities.Calendar;
import it.polimi.se.calcare.entities.Event;
import it.polimi.se.calcare.entities.Forecast;
import it.polimi.se.calcare.entities.User;
import it.polimi.se.calcare.helpers.CryptoHelper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.json.JSONException;
import org.json.JSONObject;

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
        return em.createNamedQuery("Calendar.findById", Calendar.class)
                .setParameter("id", user.getCalendar().getId())
                .getSingleResult();
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
    @Produces({"application/json"})
    public Response export(@Context SecurityContext sc) throws IOException{
        User user = (User) sc.getUserPrincipal();
        List<Event> events = em.createNamedQuery("Event.myCalendar", Event.class)
                .setParameter("calendar", user.getCalendar())
                .getResultList();
        String filename = "myCalendar.meteocal";
        
        CryptoHelper ch = new CryptoHelper();
        String blob = ch.bytesToHex(ch.encrypt(ch.objToBytes(events)));
        
        return Response.ok(blob).header("Content-Disposition", "attachment; filename=" + filename).build();
    }
    
    @POST
    @AuthRequired
    @Path("import")
    public void importCal(@Context SecurityContext sc, String blob){
        User user = (User) sc.getUserPrincipal();
        
        CryptoHelper ch = new CryptoHelper();
        
        //this is because the import form funky format: 
        //we need to cut away the box and only keep the super giant string with the content
        blob = blob.split("application/octet-stream")[1].split("-")[0].trim();
        
        List<Event> events = null;
        try {
            events = (List<Event>) ch.bytesToObj(ch.decrypt(ch.hexToBytes(blob)));
        } catch (ClassNotFoundException | IOException ex) {
            Logger.getLogger(CalendarFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //TODO import the list into db
        for(Event e : events){
            System.out.println(e.getDescription());
        }
        
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
