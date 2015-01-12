/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se.calcare.service;

import it.polimi.se.calcare.auth.AuthRequired;
import it.polimi.se.calcare.entities.Calendar;
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
import javax.ws.rs.QueryParam;
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

    @POST
    @Override
    @Consumes({"application/xml", "application/json"})
    public void create(Calendar entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({"application/xml", "application/json"})
    public void edit(@PathParam("id") Integer id, Calendar entity) {
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
    public Calendar find(@PathParam("id") Integer id) {
        return super.find(id);
    }
    
    @GET
    @AuthRequired
    @Path("me")
    @Produces({"application/json"})
    public Calendar myCalendar(@Context SecurityContext sc) {
        User user = (User) sc.getUserPrincipal();
        return super.find(user.getCalendar().getId());
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
        if(cals.size() > 10){
            cals = cals.subList(0, 10); //let's cut the list to 10 results
        }   
        return cals;
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<Calendar> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
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
