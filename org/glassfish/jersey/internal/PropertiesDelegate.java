package org.glassfish.jersey.internal;

import java.util.Collection;

public interface PropertiesDelegate
{
    Object getProperty(final String p0);
    
    Collection<String> getPropertyNames();
    
    void setProperty(final String p0, final Object p1);
    
    void removeProperty(final String p0);
}
