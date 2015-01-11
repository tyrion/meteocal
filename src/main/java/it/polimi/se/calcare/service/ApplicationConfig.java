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
@javax.ws.rs.ApplicationPath("api")
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
        resources.add(it.polimi.se.calcare.auth.AuthFilter.class);
        resources.add(it.polimi.se.calcare.service.AuthREST.class);
        resources.add(it.polimi.se.calcare.service.CalendarFacadeREST.class);
        resources.add(it.polimi.se.calcare.service.CityFacadeREST.class);
        resources.add(it.polimi.se.calcare.service.EventFacadeREST.class);
        resources.add(it.polimi.se.calcare.service.ForecastFacadeREST.class);
        resources.add(it.polimi.se.calcare.service.NotificationFacadeREST.class);
        resources.add(it.polimi.se.calcare.service.NotificationTypeFacadeREST.class);
        resources.add(it.polimi.se.calcare.service.ParticipationFacadeREST.class);
        resources.add(it.polimi.se.calcare.service.UserFacadeREST.class);
        resources.add(it.polimi.se.calcare.service.WeatherConditionFacadeREST.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.ClassNotFoundExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.ConversionExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.DatabaseExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.EntityExistsExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.EntityNotFoundExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.IOExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.IllegalAccessExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.IllegalArgumentExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.IllegalStateExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.InvocationTargetExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.JAXBExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.JPARSConfigurationExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.JPARSExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.MalformedURLExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.NamingExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.NoResultExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.NoSuchMethodExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.NonUniqueResultExceptionExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.OptimisticLockExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.PersistenceExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.PessimisticLockExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.QueryTimeoutExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.RollbackExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.TransactionRequiredExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.exceptions.UnsupportedMediaTypeExceptionMapper.class);
        resources.add(org.eclipse.persistence.jpa.rs.resources.EntityResource.class);
        resources.add(org.eclipse.persistence.jpa.rs.resources.PersistenceResource.class);
        resources.add(org.eclipse.persistence.jpa.rs.resources.PersistenceUnitResource.class);
        resources.add(org.eclipse.persistence.jpa.rs.resources.QueryResource.class);
        resources.add(org.eclipse.persistence.jpa.rs.resources.SingleResultQueryResource.class);
        resources.add(org.eclipse.persistence.jpa.rs.resources.unversioned.EntityResource.class);
        resources.add(org.eclipse.persistence.jpa.rs.resources.unversioned.PersistenceResource.class);
        resources.add(org.eclipse.persistence.jpa.rs.resources.unversioned.PersistenceUnitResource.class);
        resources.add(org.eclipse.persistence.jpa.rs.resources.unversioned.QueryResource.class);
        resources.add(org.eclipse.persistence.jpa.rs.resources.unversioned.SingleResultQueryResource.class);
    }
    
}
