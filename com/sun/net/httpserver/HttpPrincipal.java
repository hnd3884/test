package com.sun.net.httpserver;

import jdk.Exported;
import java.security.Principal;

@Exported
public class HttpPrincipal implements Principal
{
    private String username;
    private String realm;
    
    public HttpPrincipal(final String username, final String realm) {
        if (username == null || realm == null) {
            throw new NullPointerException();
        }
        this.username = username;
        this.realm = realm;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof HttpPrincipal)) {
            return false;
        }
        final HttpPrincipal httpPrincipal = (HttpPrincipal)o;
        return this.username.equals(httpPrincipal.username) && this.realm.equals(httpPrincipal.realm);
    }
    
    @Override
    public String getName() {
        return this.username;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public String getRealm() {
        return this.realm;
    }
    
    @Override
    public int hashCode() {
        return (this.username + this.realm).hashCode();
    }
    
    @Override
    public String toString() {
        return this.getName();
    }
}
