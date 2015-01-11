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
import it.polimi.se.calcare.entities.Forecast;
import it.polimi.se.calcare.entities.WeatherCondition;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import javax.ejb.Stateless;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Days;
/**
 *
 * @author nopesled
 */
@Stateless
public class GetWeather{

    @PersistenceContext(unitName = "it.polimi.se_CalCARE_war_1.0-SNAPSHOTPU")
    private EntityManager em;
    
    public static URL googleUrlBuilder(String addr) throws Exception
{
    // build a URL
    String s = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    s += addr;
    return new URL(s);
}
    
    public int createCity(String addr) throws Exception {
        //set the address of the city for the input stream for the google URL Builder 
        InputStream google=googleUrlBuilder(addr).openStream();
        
        //varibable to call the generic json builder
        JSONObject googleJSON = new JSONObject(jsonBuilder(google));
        
        //Call the Decoder for the google JSON
        String location = googleJsonDecoder(googleJSON);
        
        //Build the Openeather URL
        InputStream openWeather = openWeatherUrlBuilder(location).openStream();
        
        //Create the JSON for Openweather parsing
        JSONObject owJSON = new JSONObject(jsonBuilder(openWeather));
        
        //Decode the JSON and create the City into the DB
        return (openweatherJsonDecoderCity(owJSON));
    }
    
    public void updateForecast(ArrayList<Forecast> forecasts) throws Exception{
        /*
                this method is responsible for the update of the forecast
                it takes nothing as input but grab from the DB the list of cities
                from which derive all the event that have place in that city and 
                update the relative forecast object
        */
        for (Forecast item : forecasts) {
            Date tmp=(item.getForecastPK().getDt());
            DateTime date= new DateTime(tmp);
            int cnt= Days.daysBetween(date, new DateTime()).getDays();
            if ((cnt) > 16) continue; 
                else{        
            Double lat=item.getCity1().getLat();
            Double lon=item.getCity1().getLon();
            
            //Format lat & lon for the URLBuilder
            String coords=openWeatherUrlPreBuilder(lat, lon);
            
            //Build the Openeather URL
            InputStream openWeather = openWeatherUrlBuilder(coords).openStream();
        
            //Create the JSON for Openweather parsing
            JSONObject owJSON = new JSONObject(jsonBuilder(openWeather));
        
            //Decode the JSON and update the forecast information
            openweatherJsonDecoderWeather(owJSON, item, cnt);
            }
            }
    } 
    
    public String jsonBuilder(InputStream urlBuilder){
        String str = new String();
        try ( // read from the URL
            Scanner scan = new Scanner(urlBuilder)) 
        {
            while (scan.hasNext())
                str += scan.nextLine();
        }
        return str;
    }

    public static String googleJsonDecoder(JSONObject obj) throws JSONException 
    {
    // get the lat & lng from Google result
    JSONObject res = obj.getJSONArray("results").getJSONObject(0);
    JSONObject loc =
        res.getJSONObject("geometry").getJSONObject("location");
    // nicely format the return value to insert it directly into openweatherUrlDecoder
        return openWeatherUrlPreBuilder(loc.getDouble("lat"), loc.getDouble("lng"));
    }
    
    public static String openWeatherUrlPreBuilder(Double lat, Double lon){
        return ("lat=" + lat.toString() +
                        "&lon=" + lon.toString());
    }
    
    public static URL openWeatherUrlBuilder(String addr) throws Exception
    {
        // build a URL
        String s = "http://api.openweathermap.org/data/2.5/forecast/daily?";
        s += addr;
        s += "&cnt=16";
        s += "&mode=json";
        return new URL(s);
    }
    
    
    public int openweatherJsonDecoderCity(JSONObject obj) throws JSONException {
    JSONObject city = obj.getJSONObject("city");
    JSONObject coords = city.getJSONObject("coord");
    List<City> cities = em.createNamedQuery("City.findAll", City.class).getResultList();
    City new_city=new City(
            city.getInt("id"), 
            city.getString("name"), 
            city.getString("country"), 
            Double.parseDouble(coords.getString("lat")), 
            Double.parseDouble(coords.getString("lon"))
                                );
    if (cities.contains(new_city)) return new_city.getId();
    em.persist(city);
    return city.getInt("id");
    }
    
    public void openweatherJsonDecoderWeather(JSONObject obj, Forecast forecast, int day) throws JSONException {
        
    JSONObject list= obj.getJSONArray("list").getJSONObject(day);
    JSONObject weather = list.getJSONArray("weather").getJSONObject(0);
    
    forecast.setHumidity(list.getDouble("humidity"));
    forecast.setPressure(list.getDouble("pressure"));
    forecast.setTempMax(list.getJSONObject("temp").getDouble("max"));
    forecast.setTempMin(list.getJSONObject("temp").getDouble("min"));
    forecast.setWeatherCondition(new WeatherCondition(weather.getInt("id")));
    em.persist(forecast);
    }
}
