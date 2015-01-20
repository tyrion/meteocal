/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se.calcare.service;

import it.polimi.se.calcare.auth.AuthRequired;
import it.polimi.se.calcare.entities.Calendar;
import it.polimi.se.calcare.entities.Event;
import it.polimi.se.calcare.entities.User;
import it.polimi.se.calcare.helpers.CryptoHelper;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
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
    public List<Event> find(@Context SecurityContext sc, @PathParam("id") Integer id) {
        User user = (User) sc.getUserPrincipal();
        return em.createNamedQuery("Event.calendar", Event.class)
                .setParameter("user", user)
                .setParameter("calendar", id)
                .getResultList();
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
        return em.createNamedQuery("Event.myCalendar", Event.class)
                .setParameter("calendar", user.getCalendar())
                .getResultList();
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
        String blob = null;
        try {
            blob = ch.bytesToHex(ch.encrypt(ch.objToBytes(events)));
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(CalendarFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.ok(blob).header("Content-Disposition", "attachment; filename=" + filename).build();
    }
    
    @POST
    @AuthRequired
    @Path("import")
    @Produces({"application/json"})
    public void importCal(@Context SecurityContext sc, String blob) throws IOException{
        User user = (User) sc.getUserPrincipal();
        
        CryptoHelper ch = new CryptoHelper();
        
        List<Event> events = null;
        try {
            events = (List<Event>) ch.bytesToObj(ch.decrypt(ch.hexToBytes(blob)));
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(CalendarFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(CalendarFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //TODO import the list
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
