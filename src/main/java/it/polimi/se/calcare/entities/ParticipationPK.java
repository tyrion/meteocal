/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se.calcare.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author tyrion
 */
@Embeddable
public class ParticipationPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "event")
    private int event;
    @Basic(optional = false)
    @NotNull
    @Column(name = "calendars_id")
    private int calendarsId;

    public ParticipationPK() {
    }

    public ParticipationPK(int event, int calendarsId) {
        this.event = event;
        this.calendarsId = calendarsId;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public int getCalendarsId() {
        return calendarsId;
    }

    public void setCalendarsId(int calendarsId) {
        this.calendarsId = calendarsId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) event;
        hash += (int) calendarsId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ParticipationPK)) {
            return false;
        }
        ParticipationPK other = (ParticipationPK) object;
        if (this.event != other.event) {
            return false;
        }
        if (this.calendarsId != other.calendarsId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.polimi.se.calcare.entities.ParticipationPK[ event=" + event + ", calendarsId=" + calendarsId + " ]";
    }
    
}
