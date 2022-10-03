package org.glassfish.jersey.servlet;

import java.util.Enumeration;
import java.util.Collections;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import org.glassfish.jersey.internal.PropertiesDelegate;

class ServletPropertiesDelegate implements PropertiesDelegate
{
    private final HttpServletRequest request;
    
    public ServletPropertiesDelegate(final HttpServletRequest request) {
        this.request = request;
    }
    
    public Object getProperty(final String name) {
        return this.request.getAttribute(name);
    }
    
    public Collection<String> getPropertyNames() {
        return (Collection<String>)Collections.list((Enumeration<Object>)this.request.getAttributeNames());
    }
    
    public void setProperty(final String name, final Object object) {
        this.request.setAttribute(name, object);
    }
    
    public void removeProperty(final String name) {
        this.request.removeAttribute(name);
    }
}
