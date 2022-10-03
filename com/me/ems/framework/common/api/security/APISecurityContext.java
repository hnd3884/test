package com.me.ems.framework.common.api.security;

import java.security.Principal;
import com.me.ems.framework.uac.api.v1.model.User;
import javax.ws.rs.core.SecurityContext;

public class APISecurityContext implements SecurityContext
{
    private User user;
    private String scheme;
    
    public APISecurityContext(final User user, final String scheme) {
        this.user = user;
        this.scheme = scheme;
    }
    
    public Principal getUserPrincipal() {
        return this.user;
    }
    
    public boolean isUserInRole(final String role) {
        return this.user.isUserInRole(role);
    }
    
    public boolean isSecure() {
        return false;
    }
    
    public String getAuthenticationScheme() {
        return null;
    }
}
