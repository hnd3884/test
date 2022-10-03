package org.jfree.base.config;

import java.util.Vector;
import java.util.Enumeration;

public class SystemPropertyConfiguration extends HierarchicalConfiguration
{
    public Enumeration getConfigProperties() {
        try {
            return System.getProperties().keys();
        }
        catch (final SecurityException ex) {
            return new Vector().elements();
        }
    }
    
    public String getConfigProperty(final String key, final String defaultValue) {
        try {
            final String value = System.getProperty(key);
            if (value != null) {
                return value;
            }
        }
        catch (final SecurityException ex) {}
        return super.getConfigProperty(key, defaultValue);
    }
    
    public boolean isLocallyDefined(final String key) {
        try {
            return System.getProperties().containsKey(key);
        }
        catch (final SecurityException ex) {
            return false;
        }
    }
    
    public void setConfigProperty(final String key, final String value) {
        throw new UnsupportedOperationException("The SystemPropertyConfiguration is readOnly");
    }
}
