/*
 * Copyright (C) 2015 Germano Gabbianelli
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.polimi.se.calcare.service;

import it.polimi.se.calcare.auth.Password;
import it.polimi.se.calcare.entities.Calendar;
import it.polimi.se.calcare.entities.User;
import it.polimi.se.calcare.helpers.JWTHelper;
import it.polimi.se.calcare.helpers.SendMail;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 *
 * @author Germano Gabbianelli
 */
@Stateless
@Path("auth")
public class AuthREST {

    @PersistenceContext(unitName = "it.polimi.se_CalCARE_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @POST
    @Path("login")
    @Produces("application/json")
    public String login(@FormParam("email") String email, @FormParam("password") String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        if (email == null || password == null)
            throw new WebApplicationException(401);
        try {
            User user = em.createNamedQuery("User.findByEmail", User.class)
                    .setParameter("email", email).getSingleResult();
            if (user.isActive() && Password.check(password, user.getPassword()))
                return String.format("{\"token\":\"%s\"}",
                        JWTHelper.encode("user", user));
        } catch (javax.persistence.NoResultException ex) {
        }

        throw new WebApplicationException(401);
    }

    @GET
    @Path("activate")
    public Response activate(@QueryParam("token") String token)
            throws URISyntaxException {

        Integer id = JWTHelper.decode(token, "activate");
        User user = em.createNamedQuery("User.findById", User.class)
                .setParameter("id", id).getSingleResult();

        if (!user.isActive()) {
            user.setActive(true);
            em.persist(user);

            Calendar calendar = new Calendar();
            calendar.setOwner(user);
            em.persist(calendar);
        }

        return Response.seeOther(new java.net.URI("..#/activated")).build();
    }

    @POST
    @Path("reset/request")
    public void requestReset(@Context UriInfo ui,
            @FormParam("email") String email){
        ArrayList<String> receiver=new ArrayList<String>();
        User user = em.createNamedQuery("User.findByEmail", User.class)
                .setParameter("email", email).getSingleResult();
        String resetLink = ui.getBaseUri().resolve("..#/reset?token=")
                + JWTHelper.encode("reset", user);
        receiver.add(email);
        SendMail.Mail(receiver, "[CalCARE] Password reset request",
                String.format("Hey %s, someone requested to reset your "
                        + "password. Click the link to confirm: <a href=\"%s\">%s</a>\n",
                        user.getGivenName(), resetLink, resetLink));
    }

    @POST
    @Path("reset/confirm")
    public void reset(@QueryParam("token") String token,
            @FormParam("password") String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        Integer id = JWTHelper.decode(token, "reset");

        User user = em.createNamedQuery("User.findById", User.class)
                .setParameter("id", id).getSingleResult();

        if (user.isActive())
            user.setPassword(password);
    }
}
