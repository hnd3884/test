package org.jfree.base.config;

import java.util.Enumeration;
import java.util.Iterator;
import org.jfree.util.Configuration;

public interface ModifiableConfiguration extends Configuration
{
    Iterator findPropertyKeys(final String p0);
    
    Enumeration getConfigProperties();
    
    void setConfigProperty(final String p0, final String p1);
}
