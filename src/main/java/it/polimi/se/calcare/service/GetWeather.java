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
    
    public static String getWeather(String addr) throws IOException, JSONException, Exception {
        String str,loc;
        try ( // read from the URL
            Scanner scan = new Scanner(googleUrlBuilder(addr).openStream())) {
             str= new String();
            while (scan.hasNext())
                str += scan.nextLine();
        }
        loc=(googleJsonDecoder(new JSONObject(str)));
        try ( // read from the URL
            Scanner scan = new Scanner(openWeatherUrlBuilder(loc).openStream())) 
        {
            str = new String();
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
    
    public static URL openWeatherUrlBuilder(String addr) throws Exception
    {
        // build a URL
        String s = "http://api.openweathermap.org/data/2.5/forecast/daily?";
        s += addr;
        s += "&cnt=1&mode=json";
        return new URL(s);
    }
    
    public static String openweatherJsonDecoder(JSONObject obj) throws JSONException {
    JSONObject list= obj.getJSONArray("list").getJSONObject(0);
    JSONObject weather = list.getJSONArray("weather").getJSONObject(0);
    //Return formatted (?) Object
    //TODO: Corectly Format Object
    return ("date=" + list.getInt("dt") +
                        "\nMain:" + weather.getString("main") +
                        "\nDescription:" + weather.getString("description") +
                        "\nIcon:" + weather.getString("icon")
                                );
    }
}
