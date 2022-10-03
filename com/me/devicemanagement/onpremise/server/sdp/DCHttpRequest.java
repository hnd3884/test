package com.me.devicemanagement.onpremise.server.sdp;

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Properties;
import java.security.Principal;
import javax.servlet.http.HttpServletRequestWrapper;

public class DCHttpRequest extends HttpServletRequestWrapper
{
    Principal principal;
    Properties properties;
    List roles;
    HttpServletRequest request;
    
    public DCHttpRequest(final HttpServletRequest request, final String loginName, final String domainName, final String role, final Properties properties) {
        super(request);
        this.principal = null;
        this.properties = null;
        this.roles = new ArrayList();
        this.request = null;
        this.request = request;
        this.properties = properties;
        if (domainName != null) {
            request.setAttribute("domainName", (Object)domainName);
        }
        this.principal = new SimplePrincipal(loginName);
        if (role != null) {
            final String[] rs = role.split(";");
            for (int i = 0; i < rs.length; ++i) {
                this.roles.add(rs[i]);
            }
        }
    }
    
    public DCHttpRequest(final HttpServletRequest request, final String loginName, final String domainName, final List role, final Properties properties) {
        super(request);
        this.principal = null;
        this.properties = null;
        this.roles = new ArrayList();
        this.request = null;
        if (domainName != null) {
            request.setAttribute("domainName", (Object)domainName);
        }
        this.request = request;
        this.properties = properties;
        this.principal = new SimplePrincipal(loginName);
        if (role != null) {
            this.roles = role;
        }
    }
    
    public Principal getUserPrincipal() {
        return (this.principal != null) ? this.principal : this.request.getUserPrincipal();
    }
    
    public String getRemoteUser() {
        return (this.principal != null) ? this.principal.getName() : this.request.getRemoteUser();
    }
    
    public boolean isUserInRole(final String role) {
        return this.roles.contains(role) || this.request.isUserInRole(role);
    }
    
    public String getParameter(final String name) {
        if (this.properties != null && this.properties.containsKey(name)) {
            return this.properties.getProperty(name);
        }
        return super.getParameter(name);
    }
    
    public static class SimplePrincipal implements Principal
    {
        String name;
        
        public SimplePrincipal(final String name) {
            this.name = "";
            this.name = name;
        }
        
        @Override
        public String getName() {
            return this.name;
        }
    }
}
