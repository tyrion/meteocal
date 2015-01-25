/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se.calcare.entities;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.eclipse.persistence.oxm.annotations.XmlWriteOnly;

/**
 *
 * @author tyrion
 */
@Entity
@Table(name = "events")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Event.findAll", query = "SELECT e FROM Event e"),
    @NamedQuery(name = "Event.findById", query = "SELECT e FROM Event e WHERE e.id = :id"),
    @NamedQuery(name = "Event.findByName", query = "SELECT e FROM Event e WHERE e.name = :name"),
    @NamedQuery(name = "Event.findByDescription", query = "SELECT e FROM Event e WHERE e.description = :description"),
    @NamedQuery(name = "Event.findByStart", query = "SELECT e FROM Event e WHERE e.start = :start"),
    @NamedQuery(name = "Event.findByEnd", query = "SELECT e FROM Event e WHERE e.end = :end"),
    @NamedQuery(name = "Event.findByLocation", query = "SELECT e FROM Event e WHERE e.location = :location"),
    @NamedQuery(name = "Event.findByPublic1", query = "SELECT e FROM Event e WHERE e.public1 = :public1"),
    @NamedQuery(name = "Event.findByOutdoor", query = "SELECT e FROM Event e WHERE e.outdoor = :outdoor"),
    @NamedQuery(name = "Event.myCalendar", query = "SELECT p.event FROM Participation p WHERE p.calendar = :calendar"),
    @NamedQuery(name = "Event.calendar", query = "SELECT DISTINCT p1.event FROM Participation p1, Participation p2 WHERE p1.calendar.id = :calendar AND p2.calendar.owner = :user AND  (p1.calendar.public1 = TRUE OR p1.event.public1 = TRUE OR p1.event = p2.event)")
})
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "name")
    private String name;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "description")
    private String description;

    @Basic(optional = false)
    @NotNull
    @Column(name = "start")
    @Temporal(TemporalType.TIMESTAMP)
    private Date start;

    @Basic(optional = false)
    @NotNull
    @Column(name = "end")
    @Temporal(TemporalType.TIMESTAMP)
    private Date end;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "location")
    private String location;

    @Basic(optional = false)
    @NotNull
    @Column(name = "public")
    private boolean public1;

    @Basic(optional = false)
    @NotNull
    @Column(name = "outdoor")
    private boolean outdoor;

    @JoinTable(name = "events_forecasts", joinColumns = {
        @JoinColumn(name = "events_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "forecasts_city", referencedColumnName = "city"),
        @JoinColumn(name = "forecasts_dt", referencedColumnName = "dt")})
    @ManyToMany
    private Collection<Forecast> forecastCollection;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "event")
    private Collection<Participation> participationCollection;

    @JoinColumn(name = "creator", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User creator;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "event")
    private Collection<Notification> notificationCollection;

    public Event() {
    }

    public Event(Integer id) {
        this.id = id;
    }

    public Event(String name, String description, Date start, Date end, String location, boolean public1, boolean outdoor) {
        this(start, end, public1);
        this.name = name;
        this.description = description;
        this.location = location;
        this.outdoor = outdoor;
    }

    public Event(Date start, Date end, boolean public1) {
        this.start = start;
        this.end = end;
        this.public1 = public1;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean getPublic1() {
        return public1;
    }

    public boolean isPublic() {
        return public1;
    }

    public void setPublic1(boolean public1) {
        this.public1 = public1;
    }

    public boolean getOutdoor() {
        return outdoor;
    }

    public void setOutdoor(boolean outdoor) {
        this.outdoor = outdoor;
    }

    @XmlElement
    @XmlWriteOnly
    public Collection<Forecast> getForecastCollection() {
        return forecastCollection;
    }

    public void setForecastCollection(Collection<Forecast> forecastCollection) {
        this.forecastCollection = forecastCollection;
    }

    @XmlElement
    @XmlWriteOnly
    public Collection<Participation> getParticipationCollection() {
        return participationCollection;
    }

    public void setParticipationCollection(Collection<Participation> participationCollection) {
        this.participationCollection = participationCollection;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    @XmlTransient
    public Collection<Notification> getNotificationCollection() {
        return notificationCollection;
    }

    public void setNotificationCollection(Collection<Notification> notificationCollection) {
        this.notificationCollection = notificationCollection;
    }

    public Event asPrivate() {
        return new Event(this.start, this.end, false);
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
        if (!(object instanceof Event)) {
            return false;
        }
        Event other = (Event) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Event[ id=" + id + " ]";
    }

    public List<Forecast> getWorstWeather() {
        Forecast worst = null;
        for (Forecast f : this.getForecastCollection()) {
            if (f.isWeatherBad())
                return Arrays.asList(f);
            worst = f;
        }
        return Arrays.asList(worst);
    }

}
