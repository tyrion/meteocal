/*
 * The MIT License
 *
 * Copyright 2015 tyrion.
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
package it.polimi.se.calcare.dto;

import it.polimi.se.calcare.entities.Event;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author tyrion
 */
@XmlRootElement
public class EventCreationDTO {

    public Set<Integer> invitedPeople;
    public Event event;

    public EventCreationDTO() {
    }

    ;

    public EventCreationDTO(Event event, Set<Integer> invitedUsers) {
        this.invitedPeople = invitedUsers;
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Set<Integer> getInvitedPeople() {
        return invitedPeople;
    }

    public void setInvitedPeople(Set<Integer> invitations) {
        this.invitedPeople = invitations;
    }

}
