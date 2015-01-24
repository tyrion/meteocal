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

import it.polimi.se.calcare.helpers.SendMail;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author nopesled
 */
public class ContextRootListener implements ServletContextListener {

    @Override
    public void contextDestroyed(final ServletContextEvent event) {
       // Do nothing
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        SendMail.setContextPath(event.getServletContext().getContextPath() );
    }

}