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

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;
import javax.ejb.Stateless;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author nopesled
 */
@Stateless
public class GetWeather{

    public static URL googleUrlBuilder(String addr) throws Exception
{
    // build a URL
    String s = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    s += addr;
    return new URL(s);
}
    
    public static String getLocation(String addr) throws Exception {
        String location;
        try ( // read from the URL
            Scanner scan = new Scanner(googleUrlBuilder(addr).openStream())) {
             location= new String();
            while (scan.hasNext())
                location += scan.nextLine();
        }
        return (googleJsonDecoder(new JSONObject(location)));
    }
    
    public static HashMap<String, String> getWeatherandCityInfo(Integer cnt, String mode, String loc) throws IOException, JSONException, Exception {
        String str = new String();
        try ( // read from the URL
            Scanner scan = new Scanner(openWeatherUrlBuilder(loc, cnt, mode).openStream())) 
        {
            while (scan.hasNext())
                str += scan.nextLine();
        }
            return openweatherJsonDecoder(new JSONObject(str));
    }

    public static String googleJsonDecoder(JSONObject obj) throws JSONException 
    {
    // get the lat & lng from Google result
    JSONObject res = obj.getJSONArray("results").getJSONObject(0);
    JSONObject loc =
        res.getJSONObject("geometry").getJSONObject("location");
    // nicely format the return value to insert it directly into openweatherUrlDecoder
    return ("lat=" + loc.getDouble("lat") +
                        "&lon=" + loc.getDouble("lng"));
    }
    
    public static URL openWeatherUrlBuilder(String addr, Integer cnt, String mode) throws Exception
    {
        // build a URL
        String s = "http://api.openweathermap.org/data/2.5/forecast/daily?";
        s += addr;
        s += "&cnt=" + cnt.toString();
        s += "&mode=" + mode;
        return new URL(s);
    }
    
    public static HashMap<String, String> openweatherJsonDecoder(JSONObject obj) throws JSONException {
    for (int i=0; i<15; i++){   
    JSONObject list= obj.getJSONArray("list").getJSONObject(i);
    JSONObject weather = list.getJSONArray("weather").getJSONObject(0);
    JSONObject city = obj.getJSONObject("city");
    JSONObject coords = city.getJSONObject("coord");
    //Return formatted (?) Object
    //TODO: Corectly Format Object
    HashMap<String, String> cache = new HashMap<>();
    cache.put("Name", city.getString("name"));
    cache.put("ID_c", city.getString("id"));
    cache.put("Country", city.getString("country"));
    cache.put("Lat", coords.getString("lat"));
    cache.put("Lon", coords.getString("lon"));
    cache.put("Date", list.getString("dt"));
    cache.put("TemperatureMax", list.getJSONObject("temp").getString("max"));
    cache.put("TemperatureMin", list.getJSONObject("temp").getString("min"));
    cache.put("Humidity", list.getString("humidity"));
    cache.put("Pressure", list.getString("pressure"));
    cache.put("ID_w", weather.getString("id"));
    cache.put("Main", weather.getString("main"));
    cache.put("Description", weather.getString("description"));
    cache.put("Icon", weather.getString("icon"));
    return cache;
        }
        return null;
    }
}
