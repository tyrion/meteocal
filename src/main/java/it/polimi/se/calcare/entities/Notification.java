/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se.calcare.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author tyrion
 */
@Entity
@Table(name = "notifications")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Notification.findAll", query = "SELECT n FROM Notification n"),
    @NamedQuery(name = "Notification.findById", query = "SELECT n FROM Notification n WHERE n.id = :id"),
    @NamedQuery(name = "Notification.findUserNotifs", query = "FROM Notification AS n WHERE n.user.id != :currentUser")})
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @JoinColumn(name = "events_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Event event;

    @JoinColumn(name = "notifications_type", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private NotificationType type;

    @JoinColumn(name = "users_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User user;

    public Notification() {
    }

    public Notification(Event event, NotificationType type, User user) {
        this.event = event;
        this.type = type;
        this.user = user;
    }

    public Notification(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    @XmlTransient
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @XmlElement
    public String getDescription() {
        return String.format(this.type.getDescription(), this.event.getName(),
                this.event.getCreator().getFullName());
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Notification)) {
            return false;
        }
        Notification other = (Notification) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.polimi.se.calcare.entities.Notification[ id=" + id + " ]";
    }

}
