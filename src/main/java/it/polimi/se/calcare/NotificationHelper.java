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
package it.polimi.se.calcare;

import it.polimi.se.calcare.entities.Event;
import it.polimi.se.calcare.entities.Notification;
import it.polimi.se.calcare.entities.NotificationType;
import it.polimi.se.calcare.entities.User;
import it.polimi.se.calcare.service.SendMail;
import java.net.URI;
import javax.persistence.EntityManager;
import org.owasp.encoder.Encode;

/**
 *
 * @author Germano Gabbianelli
 */
public class NotificationHelper {

    private EntityManager em;

    private final NotificationType type;
    private final Event event;

    public NotificationHelper(EntityManager em, NotificationType.Enum type, Event event) {
        this.em = em;
        this.type = em.createNamedQuery("NotificationType.findById", NotificationType.class)
                .setParameter("id", type.id).getSingleResult();
        this.event = event;
    }

    public Notification sendTo(User user, URI link, Object... args) {
        return sendTo(user, link.toString(), args);
    }

    public Notification sendTo(User user, String link, Object... args) {
        Notification n = new Notification(this.event, this.type, user);
        em.persist(n);

        String body = String.format(this.type.getDescription(), args);
        SendMail.Mail(new String[]{user.getEmail()},
                String.format("CalCARE Notification: %s", this.type.getName()),
                String.format("<a href=\"%s\">%s</a>", link, Encode.forHtml(body)));

        return n;
    }

}
