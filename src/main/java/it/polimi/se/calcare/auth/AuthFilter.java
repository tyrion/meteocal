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
package it.polimi.se.calcare.auth;

import it.polimi.se.calcare.entities.User;
import it.polimi.se.calcare.helpers.JWTHelper;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Priority;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author Germano Gabbianelli
 */
@Priority(Priorities.AUTHENTICATION)
@Provider
@AuthRequired
public class AuthFilter implements ContainerRequestFilter {

    @PersistenceContext(unitName = "it.polimi.se_CalCARE_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    private static final Pattern HEADER_PATTERN = Pattern.compile("Bearer (.+)");

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String header = requestContext.getHeaderString("Authorization");
        if (header == null)
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);

        Matcher matcher = HEADER_PATTERN.matcher(header);
        matcher.find();

        User user;
        try {
            Integer id = JWTHelper.decode(matcher.group(1), "user", 401);
            user = em.createNamedQuery("User.findById", User.class)
                    .setParameter("id", id).getSingleResult();
        } catch (NoResultException | IllegalStateException ex) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }

        SecurityContext sc = new SecurityContext(user);
        requestContext.setSecurityContext(sc);

        if (!sc.isUserInRole("users"))
            throw new WebApplicationException(Response.Status.FORBIDDEN);
    }
}
