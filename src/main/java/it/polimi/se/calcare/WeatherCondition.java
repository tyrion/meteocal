/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se.calcare;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author tyrion
 */
@Entity
@Table(name = "weather_conditions")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "WeatherCondition.findAll", query = "SELECT w FROM WeatherCondition w"),
    @NamedQuery(name = "WeatherCondition.findById", query = "SELECT w FROM WeatherCondition w WHERE w.id = :id"),
    @NamedQuery(name = "WeatherCondition.findByName", query = "SELECT w FROM WeatherCondition w WHERE w.name = :name"),
    @NamedQuery(name = "WeatherCondition.findByDescription", query = "SELECT w FROM WeatherCondition w WHERE w.description = :description"),
    @NamedQuery(name = "WeatherCondition.findByIcon", query = "SELECT w FROM WeatherCondition w WHERE w.icon = :icon")})
public class WeatherCondition implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
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
    @Size(min = 1, max = 255)
    @Column(name = "icon")
    private String icon;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "weatherCondition")
    private Collection<Forecast> forecastCollection;

    public WeatherCondition() {
    }

    public WeatherCondition(Integer id) {
        this.id = id;
    }

    public WeatherCondition(Integer id, String name, String description, String icon) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @XmlTransient
    public Collection<Forecast> getForecastCollection() {
        return forecastCollection;
    }

    public void setForecastCollection(Collection<Forecast> forecastCollection) {
        this.forecastCollection = forecastCollection;
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
        if (!(object instanceof WeatherCondition)) {
            return false;
        }
        WeatherCondition other = (WeatherCondition) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.polimi.se.calcare.WeatherCondition[ id=" + id + " ]";
    }
    
}
