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
import static it.polimi.se.calcare.service.GetWeather.googleUrlBuilder;
import static it.polimi.se.calcare.service.GetWeather.openWeatherUrlBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author nopesled
 */
public class GetWeatherTest {
    
    public GetWeatherTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of googleUrlBuilder method, of class GetWeather.
     */
    @Test
    public void testGoogleUrlBuilder() throws MalformedURLException, UnsupportedEncodingException{
        System.out.println("googleUrlBuilder");
        String addr = "hello,world!";
        String base = "https://maps.googleapis.com/maps/api/geocode/json?address=";
        URL expResult = new URL(base+URLEncoder.encode(addr, "utf-8"));
        URL result = GetWeather.googleUrlBuilder(addr);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of createCity method, of class GetWeather.
     */
    
    @Test
    public void testCreateCity() throws IOException, MalformedURLException, JSONException{
        System.out.println("createCity");
        String addr = "PiazzaLeonardoDaVinci32,Milano,IT";
        GetWeather instance = new GetWeather();
        City expResult = new City(3173435, "Milano", "IT", 45.47783219999999, 9.2274315);
        City result = instance.cityParser(addr);
        assertEquals(expResult, result);
        
    }

    /**
     * Test of updateForecast method, of class GetWeather.
     */
    
   /* @Test
    public void testUpdateForecast() throws JSONException, IOException {
        System.out.println("updateForecast");
        List<Forecast> nullForecasts = new ArrayList<>();
        List<Forecast> forecasts = new ArrayList<>();
        GetWeather instance = new GetWeather();
        List<Forecast> expResult0 = new ArrayList<>();
        List<Forecast> result = instance.updateForecast(new City(), nullForecasts);
        assertEquals(expResult0, result);
    }
    */
    /**
     * Test of googleJsonDecoder method, of class GetWeather.
     */
    
    @Test
    public void testGoogleJsonDecoder() throws Exception {
        System.out.println("googleJsonDecoder");
        City newCity=new City();
        GetWeather instance = new GetWeather();
        String addr = "PiazzaLeonardoDaVinci32,Milano,IT";
        //set the address of the city for the input stream for the google URL Builder 
        InputStream google = googleUrlBuilder(addr).openStream();
        //varibable to call the generic json builder
        JSONObject googleJSON = new JSONObject(instance.jsonBuilder(google));
        //Call the Decoder for the google JSON
        instance.googleJsonDecoder(googleJSON, newCity);
        //Build the Openeather URL
        InputStream openWeather = openWeatherUrlBuilder(newCity.getName()+","+newCity.getCountry()).openStream();
        //Create the JSON for Openweather parsing
        JSONObject owJSON = new JSONObject(instance.jsonBuilder(openWeather));
        
        JSONObject obj = null;
        String expResult = "";
        assertEquals(new City(3173435, "Milano", "IT", 45.47783219999999, 9.2274315), instance.openweatherJsonDecoderCity(owJSON, newCity));
    }

    /**
     * Test of openWeatherUrlBuilder method, of class GetWeather.
     */
    @Test
    public void testOpenWeatherUrlBuilder() throws Exception {
        System.out.println("openWeatherUrlBuilder");
        String addr = "PiazzaLeonardoDaVinci32 Milano IT";
        URL expResult = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=PiazzaLeonardoDaVinci32+Milano+IT&cnt=16&mode=json");
        URL result = GetWeather.openWeatherUrlBuilder(addr);
        assertEquals(expResult, result);
    }
    
}
