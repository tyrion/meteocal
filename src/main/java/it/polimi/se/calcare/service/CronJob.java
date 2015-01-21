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
import it.polimi.se.calcare.entities.ForecastPK;
import it.polimi.se.calcare.entities.Participation;
import it.polimi.se.calcare.entities.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Days;
import org.json.JSONException;

/**
 *
 * @author nopesled
 */
@Stateless
public class CronJob {

    @PersistenceContext(unitName = "it.polimi.se_CalCARE_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    @Schedule(dayOfWeek = "*", month = "*", hour = "*/24", dayOfMonth = "*", year = "*", minute = "*", second = "0", persistent = false)
    public void WeatherFetcher() throws IOException, JSONException, Exception {
            List <City> cities = em.createNamedQuery("City.findAll", City.class).getResultList();
            for (City item: cities){
               List <Forecast> toUpdate = (List) item.getForecastCollection();
               List <Forecast> newForecasts=new GetWeather().updateForecast(item, toUpdate);
               
            for (Forecast update: newForecasts) {
                em.merge(update);
                Date tmp = (update.getForecastPK().getDt());
                DateTime date = new DateTime(tmp);
                int cnt = Days.daysBetween(new DateTime(), date).getDays();
                if (cnt==3){
                    
                    //genera mail di warning causa pioggia
                }
                }
            }
            em.flush();
            
    }
}
