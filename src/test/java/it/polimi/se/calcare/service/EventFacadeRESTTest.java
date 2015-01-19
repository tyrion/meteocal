/*
 * Copyright (C) 2015 nopesled
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.polimi.se.calcare.service;

import it.polimi.se.calcare.dto.EventCreationDTO;
import it.polimi.se.calcare.entities.Event;
import java.util.List;
import javax.ejb.embeddable.EJBContainer;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
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
public class EventFacadeRESTTest {
    
    public EventFacadeRESTTest() {
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
     * Test of create method, of class EventFacadeREST.
     */
    @Test
    public void testCreate_GenericType() throws Exception {
        System.out.println("create");
        Event entity = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        EventFacadeREST instance = (EventFacadeREST)container.getContext().lookup("java:global/classes/EventFacadeREST");
        instance.create(entity);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of edit method, of class EventFacadeREST.
     */
    @Test
    public void testEdit_GenericType() throws Exception {
        System.out.println("edit");
        Event entity = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        EventFacadeREST instance = (EventFacadeREST)container.getContext().lookup("java:global/classes/EventFacadeREST");
        instance.edit(entity);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of remove method, of class EventFacadeREST.
     */
    @Test
    public void testRemove_GenericType() throws Exception {
        System.out.println("remove");
        Event entity = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        EventFacadeREST instance = (EventFacadeREST)container.getContext().lookup("java:global/classes/EventFacadeREST");
        instance.remove(entity);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of find method, of class EventFacadeREST.
     */
    @Test
    public void testFind_Object() throws Exception {
        System.out.println("find");
        Object id = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        EventFacadeREST instance = (EventFacadeREST)container.getContext().lookup("java:global/classes/EventFacadeREST");
        Event expResult = null;
        Event result = instance.find(id);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findAll method, of class EventFacadeREST.
     */
    @Test
    public void testFindAll() throws Exception {
        System.out.println("findAll");
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        EventFacadeREST instance = (EventFacadeREST)container.getContext().lookup("java:global/classes/EventFacadeREST");
        List<Event> expResult = null;
        List<Event> result = instance.findAll();
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findRange method, of class EventFacadeREST.
     */
    @Test
    public void testFindRange() throws Exception {
        System.out.println("findRange");
        int[] range = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        EventFacadeREST instance = (EventFacadeREST)container.getContext().lookup("java:global/classes/EventFacadeREST");
        List<Event> expResult = null;
        List<Event> result = instance.findRange(range);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of count method, of class EventFacadeREST.
     */
    @Test
    public void testCount() throws Exception {
        System.out.println("count");
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        EventFacadeREST instance = (EventFacadeREST)container.getContext().lookup("java:global/classes/EventFacadeREST");
        int expResult = 0;
        int result = instance.count();
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of create method, of class EventFacadeREST.
     */
    @Test
    public void testCreate_3args() throws Exception {
        System.out.println("create");
        SecurityContext sc = null;
        UriInfo ui = null;
        EventCreationDTO dto = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        EventFacadeREST instance = (EventFacadeREST)container.getContext().lookup("java:global/classes/EventFacadeREST");
        instance.create(sc, ui, dto);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of edit method, of class EventFacadeREST.
     */
    @Test
    public void testEdit_3args() throws Exception {
        System.out.println("edit");
        SecurityContext sc = null;
        Integer id = null;
        Event entity = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        EventFacadeREST instance = (EventFacadeREST)container.getContext().lookup("java:global/classes/EventFacadeREST");
        instance.edit(sc, id, entity);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of remove method, of class EventFacadeREST.
     */
    @Test
    public void testRemove_SecurityContext_Integer() throws Exception {
        System.out.println("remove");
        SecurityContext sc = null;
        Integer id = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        EventFacadeREST instance = (EventFacadeREST)container.getContext().lookup("java:global/classes/EventFacadeREST");
        instance.remove(sc, id);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of find method, of class EventFacadeREST.
     */
    @Test
    public void testFind_SecurityContext_Integer() throws Exception {
        System.out.println("find");
        SecurityContext sc = null;
        Integer id = null;
        EJBContainer container = javax.ejb.embeddable.EJBContainer.createEJBContainer();
        EventFacadeREST instance = (EventFacadeREST)container.getContext().lookup("java:global/classes/EventFacadeREST");
        Event expResult = null;
        Event result = instance.find(sc, id);
        assertEquals(expResult, result);
        container.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
