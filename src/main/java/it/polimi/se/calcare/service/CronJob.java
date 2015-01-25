/*
 * The MIT License
 *
 * Copyright 2015 nopesled.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package it.polimi.se.calcare.service;

import it.polimi.se.calcare.entities.City;
import it.polimi.se.calcare.entities.Event;
import it.polimi.se.calcare.entities.Forecast;
import it.polimi.se.calcare.entities.NotificationType;
import it.polimi.se.calcare.entities.Participation;
import it.polimi.se.calcare.helpers.NotificationHelper;
import it.polimi.se.calcare.helpers.SendMail;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONException;

/**
 *
 * @author nopesled
 */
@Stateless
public class CronJob {
    
    @PersistenceContext(unitName = "it.polimi.se_CalCARE_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    @Schedule(dayOfWeek = "*", month = "*", hour = "*/12", dayOfMonth = "*", year = "*", minute = "*", second = "*", persistent = false)
    public void weatherFetcher() throws IOException, JSONException, Exception {
        List <City> cities = em.createNamedQuery("City.findAll", City.class).getResultList();
        List <Forecast> newForecasts;

        for (City c: cities){
           newForecasts = new GetWeather().updateForecast(c, (List<Forecast>) c.getForecastCollection());               

            for (Forecast f: newForecasts) {
                em.merge(f);
                Date forecastDate = (f.getForecastPK().getDt());
                DateTime forecastDateTime = new DateTime(forecastDate);
                Long now = new Date().getTime();
                //int cnt = Days.daysBetween(new DateTime(), date).getDays();
                if (f.isWeatherBad()) {
                    //hours of difference between the forecast and the current moment
                    Long difference = (forecastDateTime.minus(now)).getMillis()/(1000*60*60);
                    DateTime sunnyDayDt = new DateTime(new GetWeather().nextSunnyDay(new DateTime(now), f.getCity1(), forecastDate).getForecastPK().getDt());

                    for (Event event: f.getEventCollection()){ 
                        //sendMail(event, difference, sunnyDayDt);
                    }
                }
            }
        }
        em.flush();
    }
    
    public void sendMail(Event e, Long difference, DateTime sunnyDayDt){
        NotificationHelper helper;
        
        //happens in three days? - if we are in the 12 hours range
        if (e.getOutdoor() && (difference - 24*3) < 12){
            if (sunnyDayDt != null){
                helper = new NotificationHelper(em, NotificationType.Enum.BAD_WEATHER, e);
                helper.sendTo(e.getCreator(),
                        composeEventLink(e),
                        e.getName(),
                        DateTimeFormat.forPattern("d MMMM yyyy").print(sunnyDayDt)
                );
            } else {
                helper = new NotificationHelper(em, NotificationType.Enum.BAD_WEATHER_WITHOUT_CHOICE, e);
                helper.sendTo(e.getCreator(), " ", e.getName());
            }
        } else if(e.getOutdoor() && (difference - 24*1) < 12) {
            //happens in 12 or 24 hours
            helper = new NotificationHelper(em, NotificationType.Enum.BAD_WEATHER_ONE_DAY, e);
            
            //should warn all the participants
            List<Participation> participations = em.createNamedQuery("Participation.findByAcceptedForEvent", Participation.class)
                .setParameter("event", e)
                .setParameter("accepted", true)
                .getResultList();
            
            for (Participation p: participations){
                helper.sendTo(p.getUser(),
                        "", //because no link goes here
                        e.getName()
                );
            }
        }
    }
    
    private String composeEventLink(Event e){
        return "http://localhost:8080" + SendMail.getContextPath() + "/#/events/" + e.getId();
    }
}
