/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se.calcare.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author tyrion
 */
@Embeddable
public class ForecastPK implements Serializable {
    @Basic(optional = false)
    @NotNull
    @Column(name = "dt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dt;
    @Basic(optional = false)
    @NotNull
    @Column(name = "city")
    private int city;

    public ForecastPK() {
    }

    public ForecastPK(Date dt, int city) {
        this.dt = dt;
        this.city = city;
    }

    public Date getDt() {
        return dt;
    }

    public void setDt(Date dt) {
        this.dt = dt;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (dt != null ? dt.hashCode() : 0);
        hash += (int) city;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ForecastPK)) {
            return false;
        }
        ForecastPK other = (ForecastPK) object;
        if ((this.dt == null && other.dt != null) || (this.dt != null && !this.dt.equals(other.dt))) {
            return false;
        }
        if (this.city != other.city) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.polimi.se.calcare.entities.ForecastPK[ dt=" + dt + ", city=" + city + " ]";
    }
    
}
