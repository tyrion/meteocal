/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.se.calcare.auth;

import it.polimi.se.calcare.entities.User;
import java.security.Principal;

/**
 *
 * @author tyrion
 */
public class SecurityContext implements javax.ws.rs.core.SecurityContext {
    
    private final User user;
    
    public SecurityContext(User user) {
        this.user = user;
    }

    @Override
    public Principal getUserPrincipal() {
        return this.user;
    }

    @Override
    public boolean isUserInRole(String role) {
        return this.user != null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public String getAuthenticationScheme() {
        return "JWT";
    }
    
}
