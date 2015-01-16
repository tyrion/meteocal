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
import static it.polimi.se.calcare.service.GetWeather.googleUrlBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
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
    /*
    @Test
    public void testCreateCity() throws IOException, MalformedURLException, JSONException{
        System.out.println("createCity");
        String addr = "PiazzaLeonardoDaVinci,Milano,IT";
        GetWeather instance = new GetWeather();
        City expResult = null;
        City result = instance.createCity(addr);
        assertEquals(expResult, result);
        
    }
*/
    /**
     * Test of updateForecast method, of class GetWeather.
     */
    /*
    @Test
    public void testUpdateForecast() throws Exception {
        System.out.println("updateForecast");
        List<Forecast> forecasts = null;
        GetWeather instance = new GetWeather();
        List<Forecast> expResult = null;
        List<Forecast> result = instance.updateForecast(forecasts);
        assertEquals(expResult, result);
    }
*/
    /**
     * Test of jsonBuilder method, of class GetWeather.
     */
    /*
    @Test
    public void testJsonBuilder() throws MalformedURLException, IOException {
        System.out.println("jsonBuilder");
        GetWeather instance = new GetWeather();
        InputStream urlBuilder = googleUrlBuilder("PiazzaLeonardoDaVinci,Milano,IT").openStream();
        String expResult = "";
        String result = instance.jsonBuilder(urlBuilder);
        assertEquals(expResult, result);
    }
*/
    /**
     * Test of googleJsonDecoder method, of class GetWeather.
     */
    /*
    @Test
    public void testGoogleJsonDecoder() throws Exception {
        System.out.println("googleJsonDecoder");
        JSONObject obj = null;
        String expResult = "";
        String result = GetWeather.googleJsonDecoder(obj);
        assertEquals(expResult, result);
    }
*/
    /**
     * Test of openWeatherUrlPreBuilder method, of class GetWeather.
     */
    @Test
    public void testOpenWeatherUrlPreBuilder() {
        System.out.println("openWeatherUrlPreBuilder");
        Double lat = 0.0;
        Double lon = 0.0;
        String expResult = "lat=0.0&lon=0.0";
        String result = GetWeather.openWeatherUrlPreBuilder(lat, lon);
        assertEquals(expResult, result);
    }

    /**
     * Test of openWeatherUrlBuilder method, of class GetWeather.
     */
    @Test
    public void testOpenWeatherUrlBuilder() throws Exception {
        System.out.println("openWeatherUrlBuilder");
        String addr = "lat=0.0&lon=0.0";
        URL expResult = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?lat=0.0&lon=0.0&cnt=16&mode=json");
        URL result = GetWeather.openWeatherUrlBuilder(addr);
        assertEquals(expResult, result);
    }

    /**
     * Test of openweatherJsonDecoderCity method, of class GetWeather.
     */
    /*
    @Test
    public void testOpenweatherJsonDecoderCity() throws Exception {
        System.out.println("openweatherJsonDecoderCity");
        JSONObject obj = null;
        GetWeather instance = new GetWeather();
        City expResult = null;
        City result = instance.openweatherJsonDecoderCity(obj);
        assertEquals(expResult, result);
    }
*/
    /**
     * Test of openweatherJsonDecoderWeather method, of class GetWeather.
     */
    /*
    @Test
    public void testOpenweatherJsonDecoderWeather() throws Exception {
        System.out.println("openweatherJsonDecoderWeather");
        JSONObject obj = null;
        Forecast forecast = null;
        int day = 0;
        GetWeather instance = new GetWeather();
        Forecast expResult = null;
        Forecast result = instance.openweatherJsonDecoderWeather(obj, forecast, day);
        assertEquals(expResult, result);
    }
    */
}
