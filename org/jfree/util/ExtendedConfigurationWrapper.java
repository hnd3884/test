package org.jfree.util;

import java.util.Enumeration;
import java.util.Iterator;

public class ExtendedConfigurationWrapper implements ExtendedConfiguration
{
    private Configuration parent;
    
    public ExtendedConfigurationWrapper(final Configuration parent) {
        if (parent == null) {
            throw new NullPointerException("Parent given must not be null");
        }
        this.parent = parent;
    }
    
    public Iterator findPropertyKeys(final String prefix) {
        return this.parent.findPropertyKeys(prefix);
    }
    
    public boolean getBoolProperty(final String name) {
        return this.getBoolProperty(name, false);
    }
    
    public boolean getBoolProperty(final String name, final boolean defaultValue) {
        return "true".equals(this.parent.getConfigProperty(name, String.valueOf(defaultValue)));
    }
    
    public Enumeration getConfigProperties() {
        return this.parent.getConfigProperties();
    }
    
    public String getConfigProperty(final String key) {
        return this.parent.getConfigProperty(key);
    }
    
    public String getConfigProperty(final String key, final String defaultValue) {
        return this.parent.getConfigProperty(key, defaultValue);
    }
    
    public int getIntProperty(final String name) {
        return this.getIntProperty(name, 0);
    }
    
    public int getIntProperty(final String name, final int defaultValue) {
        final String retval = this.parent.getConfigProperty(name);
        if (retval == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(retval);
        }
        catch (final Exception ex) {
            return defaultValue;
        }
    }
    
    public boolean isPropertySet(final String name) {
        return this.parent.getConfigProperty(name) != null;
    }
}
