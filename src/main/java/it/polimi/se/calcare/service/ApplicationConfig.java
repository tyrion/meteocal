/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se.calcare.service;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author tyrion
 */
@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(it.polimi.se.calcare.service.CalendarFacadeREST.class);
        resources.add(it.polimi.se.calcare.service.CityFacadeREST.class);
        resources.add(it.polimi.se.calcare.service.EventFacadeREST.class);
        resources.add(it.polimi.se.calcare.service.ForecastFacadeREST.class);
        resources.add(it.polimi.se.calcare.service.NotificationFacadeREST.class);
        resources.add(it.polimi.se.calcare.service.NotificationTypeFacadeREST.class);
        resources.add(it.polimi.se.calcare.service.ParticipationFacadeREST.class);
        resources.add(it.polimi.se.calcare.service.UserFacadeREST.class);
        resources.add(it.polimi.se.calcare.service.WeatherConditionFacadeREST.class);
    }
    
}
