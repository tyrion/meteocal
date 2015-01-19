/*
 * Copyright (C) 2015 Germano Gabbianelli
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
package it.polimi.se.calcare.helpers;

import java.io.StringWriter;
import javax.ws.rs.WebApplicationException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;

/**
 *
 * @author Germano Gabbianelli
 */
public class ObjectGraphHelper {

    public static <T> String render(Object obj, String name, Class<T> entity) {
        try {
            JAXBContext jc = JAXBContextFactory.createContext(
                    new Class[]{entity}, null);

            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
            marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
            marshaller.setProperty(MarshallerProperties.JSON_WRAPPER_AS_ARRAY_NAME, true);
            marshaller.setProperty(MarshallerProperties.OBJECT_GRAPH, name);

            StringWriter sw = new StringWriter();
            marshaller.marshal(obj, sw);
            return sw.getBuffer().toString();

        } catch (JAXBException ex) {
            throw new WebApplicationException(ex, 500);
        }
    }

    public static <T> String render(Object obj, Class<T> entity) {
        return render(obj, "simple", entity);
    }

}
