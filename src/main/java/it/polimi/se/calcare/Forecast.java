/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se.calcare;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author tyrion
 */
@Entity
@Table(name = "forecasts")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Forecast.findAll", query = "SELECT f FROM Forecast f"),
    @NamedQuery(name = "Forecast.findByDt", query = "SELECT f FROM Forecast f WHERE f.forecastPK.dt = :dt"),
    @NamedQuery(name = "Forecast.findByCity", query = "SELECT f FROM Forecast f WHERE f.forecastPK.city = :city"),
    @NamedQuery(name = "Forecast.findByTempMin", query = "SELECT f FROM Forecast f WHERE f.tempMin = :tempMin"),
    @NamedQuery(name = "Forecast.findByTempMax", query = "SELECT f FROM Forecast f WHERE f.tempMax = :tempMax"),
    @NamedQuery(name = "Forecast.findByPressure", query = "SELECT f FROM Forecast f WHERE f.pressure = :pressure"),
    @NamedQuery(name = "Forecast.findByHumidity", query = "SELECT f FROM Forecast f WHERE f.humidity = :humidity")})
public class Forecast implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ForecastPK forecastPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "temp_min")
    private double tempMin;
    @Basic(optional = false)
    @NotNull
    @Column(name = "temp_max")
    private double tempMax;
    @Basic(optional = false)
    @NotNull
    @Column(name = "pressure")
    private double pressure;
    @Basic(optional = false)
    @NotNull
    @Column(name = "humidity")
    private double humidity;
    @ManyToMany(mappedBy = "forecasts")
    private Collection<Event> events;
    @JoinColumn(name = "city", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private City city1;
    @JoinColumn(name = "weather_condition", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private WeatherCondition weatherCondition;

    public Forecast() {
    }

    public Forecast(ForecastPK forecastPK) {
        this.forecastPK = forecastPK;
    }

    public Forecast(ForecastPK forecastPK, double tempMin, double tempMax, double pressure, double humidity) {
        this.forecastPK = forecastPK;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.pressure = pressure;
        this.humidity = humidity;
    }

    public Forecast(Date dt, int city) {
        this.forecastPK = new ForecastPK(dt, city);
    }

    public ForecastPK getForecastPK() {
        return forecastPK;
    }

    public void setForecastPK(ForecastPK forecastPK) {
        this.forecastPK = forecastPK;
    }

    public double getTempMin() {
        return tempMin;
    }

    public void setTempMin(double tempMin) {
        this.tempMin = tempMin;
    }

    public double getTempMax() {
        return tempMax;
    }

    public void setTempMax(double tempMax) {
        this.tempMax = tempMax;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    @XmlTransient
    public Collection<Event> getEvents() {
        return events;
    }

    public void setEvents(Collection<Event> events) {
        this.events = events;
    }

    public City getCity1() {
        return city1;
    }

    public void setCity1(City city1) {
        this.city1 = city1;
    }

    public WeatherCondition getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(WeatherCondition weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (forecastPK != null ? forecastPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Forecast)) {
            return false;
        }
        Forecast other = (Forecast) object;
        if ((this.forecastPK == null && other.forecastPK != null) || (this.forecastPK != null && !this.forecastPK.equals(other.forecastPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "it.polimi.se.calcare.Forecast[ forecastPK=" + forecastPK + " ]";
    }
    
}
