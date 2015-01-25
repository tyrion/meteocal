/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se.calcare.service;

import java.util.Set;
import javax.ws.rs.core.Application;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

/**
 *
 * @author tyrion
 */
@javax.ws.rs.ApplicationPath("api")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        resources.add(MultiPartFeature.class);
        resources.add(LoggingFilter.class);
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method. It is automatically
     * populated with all resources defined in the project. If required, comment
     * out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(it.polimi.se.calcare.JSONExceptionMapper.class);
        resources.add(it.polimi.se.calcare.auth.AuthFilter.class);
        resources.add(it.polimi.se.calcare.service.AuthREST.class);
        resources.add(it.polimi.se.calcare.service.CalendarFacadeREST.class);
        resources.add(it.polimi.se.calcare.service.EventFacadeREST.class);
        resources.add(it.polimi.se.calcare.service.NotificationFacadeREST.class);
        resources.add(it.polimi.se.calcare.service.ParticipationFacadeREST.class);
        resources.add(it.polimi.se.calcare.service.UserFacadeREST.class);
    }

}
