/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se.calcare.entities;

import com.auth0.jwt.internal.com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author tyrion
 */
@Entity
@Table(name = "participations")
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
@NamedQueries({
    @NamedQuery(name = "Participation.findAll", query = "SELECT p FROM Participation p"),
    @NamedQuery(name = "Participation.findByEvent", query = "SELECT p FROM Participation p WHERE p.participationPK.event = :event"),
    @NamedQuery(name = "Participation.findByCalendarsId", query = "SELECT p FROM Participation p WHERE p.participationPK.calendarsId = :calendarsId"),
    @NamedQuery(name = "Participation.findByAccepted", query = "SELECT p FROM Participation p WHERE p.accepted = :accepted"),
    @NamedQuery(name = "Participation.forEvent", query = "FROM Participation p WHERE p.calendar = :calendar AND p.event = :event"),
    @NamedQuery(name = "Participation.findByAcceptedForEvent", query = "SELECT p FROM Participation p WHERE p.accepted = :accepted AND p.event = :event"),
})
public class Participation implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected ParticipationPK participationPK;

    @Column(name = "accepted")
    private Boolean accepted;

    @JsonIgnore
    @JoinColumn(name = "calendars_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Calendar calendar;

    @JoinColumn(name = "event", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Event event;

    public Participation() {
    }

    public Participation(ParticipationPK participationPK) {
        this.participationPK = participationPK;
    }

    public Participation(int event, int calendarsId) {
        this.participationPK = new ParticipationPK(event, calendarsId);
    }

    public Participation(int event, int calendarsId, Boolean accepted) {
        this(event, calendarsId);
        this.accepted = accepted;
    }

    public Participation(Event event, Calendar calendar, Boolean accepted) {
        this(event.getId(), calendar.getId(), accepted);
        this.event = event;
        this.calendar = calendar;
    }

    public Participation(Event event, Calendar calendar) {
        this(event, calendar, null);
    }

    public ParticipationPK getParticipationPK() {
        return participationPK;
    }

    public void setParticipationPK(ParticipationPK participationPK) {
        this.participationPK = participationPK;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    @XmlTransient
    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    @XmlTransient
    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @JsonIgnore
    @XmlElement
    public User getUser() {
        return this.calendar.getOwner();
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (participationPK != null ? participationPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Participation)) {
            return false;
        }
        Participation other = (Participation) object;
        if ((this.participationPK == null && other.participationPK != null) || (this.participationPK != null && !this.participationPK.equals(other.participationPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.polimi.se.calcare.entities.Participation[ participationPK=" + participationPK + " ]";
    }

}
