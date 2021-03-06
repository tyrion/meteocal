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
import it.polimi.se.calcare.entities.ForecastPK;
import it.polimi.se.calcare.entities.WeatherCondition;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
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
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.Days;
import org.json.JSONArray;

/**
 *
 * @author nopesled
 */
public class GetWeather {
    
    Double lat,lon;

    public static URL googleUrlBuilder(String addr) throws MalformedURLException, UnsupportedEncodingException {
        // build a URL
        String s = "https://maps.googleapis.com/maps/api/geocode/json?address=";
        s += URLEncoder.encode(addr, "utf-8");
        return new URL(s);
    }

    public City cityParser(String addr) throws MalformedURLException, IOException, JSONException {
        City newCity=new City();
        //set the address of the city for the input stream for the google URL Builder 
        InputStream google = googleUrlBuilder(addr).openStream();
        //varibable to call the generic json builder
        JSONObject googleJSON = new JSONObject(jsonBuilder(google));
        //Call the Decoder for the google JSON
        googleJsonDecoder(googleJSON, newCity);
        //Build the Openeather URL
        InputStream openWeather = openWeatherUrlBuilder(newCity.getName()+","+newCity.getCountry()).openStream();
        //Create the JSON for Openweather parsing
        JSONObject owJSON = new JSONObject(jsonBuilder(openWeather));
        //Decode the JSON and create the City into the DB
        return (openweatherJsonDecoderCity(owJSON, newCity));
    }

    public List<Forecast> updateForecast(City city, List<Forecast> forecasts) throws MalformedURLException, JSONException, IOException {
        /*
         this method is responsible for the update of the forecast
         it takes an arraylist of forecasts of THE SAME CITY
         */
        List<Forecast> newForecasts = new ArrayList<>();
        
        if (forecasts.isEmpty()) return newForecasts;
  
        //Build the Openeather URL
        InputStream openWeather = openWeatherUrlBuilder(city.getName()+","+city.getCountry()).openStream();

        //Create the JSON for Openweather parsing
        JSONObject owJSON = new JSONObject(jsonBuilder(openWeather));

        for (Forecast item : forecasts) {
            Date tmp = (item.getForecastPK().getDt());
            DateTime date = new DateTime(tmp);
            int cnt = Days.daysBetween(new DateTime(), date).getDays();
            if ((cnt>=0) && (cnt <= 16)) {
                //Decode the JSON and update the forecast information
                Forecast f = openweatherJsonDecoderWeather(owJSON, item, cnt);
                newForecasts.add(f);
            }
        }
        return newForecasts;
    }

    public String jsonBuilder(InputStream urlBuilder) {
        String str = new String();
        try ( // read from the URL
                Scanner scan = new Scanner(urlBuilder)) {
            while (scan.hasNext()) {
                str += scan.nextLine();
            }
        }
        return str;
    }

    public void googleJsonDecoder(JSONObject obj, City newCity) throws JSONException {
        // get the lat & lng from Google result
        JSONObject res = obj.getJSONArray("results").getJSONObject(0);
        JSONArray addr = res.getJSONArray("address_components");
       for (int i = 0; i < addr.length(); i++) {
             JSONObject item=addr.getJSONObject(i);
             switch(item.getString("types")){
                case ("[\"locality\",\"political\"]") : newCity.setName(item.getString("short_name")); break;
                case ("[\"country\",\"political\"]"): newCity.setCountry(item.getString("short_name")); break;
                default: break;
             }
            }
        JSONObject loc
                = res.getJSONObject("geometry").getJSONObject("location");
        
        newCity.setLat(loc.getDouble("lat"));
        newCity.setLon(loc.getDouble("lng"));
        // nicely format the return value to insert it directly into openweatherUrlDecoder
        //return openWeatherUrlPreBuilder(newCity.getLat(), newCity.getLon());
    }

    public static URL openWeatherUrlBuilder(String addr) throws MalformedURLException, UnsupportedEncodingException {
        // build a URL
        String s = "http://api.openweathermap.org/data/2.5/forecast/daily?";
        s += "q=";
        s += URLEncoder.encode(addr, "utf-8");
        s += "&cnt=16";
        s += "&mode=json";
        return new URL(s);
    }

    public City openweatherJsonDecoderCity(JSONObject obj, City newCity) throws JSONException {
        JSONObject city = obj.getJSONObject("city");
        newCity.setId(city.getInt("id"));
        return newCity;
    }

    public Forecast openweatherJsonDecoderWeather(JSONObject obj, Forecast forecast, int day) throws JSONException {

        JSONObject list = obj.getJSONArray("list").getJSONObject(day);
        JSONObject weather = list.getJSONArray("weather").getJSONObject(0);

        forecast.setHumidity(list.getDouble("humidity"));
        forecast.setPressure(list.getDouble("pressure"));
        forecast.setTempMax(list.getJSONObject("temp").getDouble("max"));
        forecast.setTempMin(list.getJSONObject("temp").getDouble("min"));
        forecast.setWeatherCondition(new WeatherCondition(weather.getInt("id")));
        return forecast;
    }

    Forecast nextSunnyDay(City city, Date dt) throws JSONException, MalformedURLException, UnsupportedEncodingException, IOException {
        //Build the Openeather URL
        InputStream openWeather = openWeatherUrlBuilder(city.getName()+","+city.getCountry()).openStream();
        //Create the JSON for Openweather parsing
        JSONObject owJSON = new JSONObject(jsonBuilder(openWeather));
        //Decode the JSON and create the City into the DB
        return sunnyFinder(owJSON, new Forecast(), dt, city.getId());
    }

    public Forecast sunnyFinder(JSONObject obj, Forecast forecast, Date dt, Integer id) throws JSONException{
        JSONObject list;
        forecast.setForecastPK(new ForecastPK(dt, id));
        for (int i=1; i<16; i++){
            list = obj.getJSONArray("list").getJSONObject(i);
            JSONObject weather = list.getJSONArray("weather").getJSONObject(0);
            forecast.setWeatherCondition(new WeatherCondition(weather.getInt("id")));
            if (forecast.isWeatherBad()==false)
                return forecast;
        }
        return null;
    }
}

