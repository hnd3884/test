package org.jfree.util;

import java.util.Enumeration;
import java.util.Set;
import java.util.Collections;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.Properties;

public class DefaultConfiguration extends Properties implements Configuration
{
    public Iterator findPropertyKeys(final String prefix) {
        final TreeSet collector = new TreeSet();
        final Enumeration enum1 = this.keys();
        while (enum1.hasMoreElements()) {
            final String key = enum1.nextElement();
            if (key.startsWith(prefix) && !collector.contains(key)) {
                collector.add(key);
            }
        }
        return Collections.unmodifiableSet((Set<?>)collector).iterator();
    }
    
    public Enumeration getConfigProperties() {
        return this.keys();
    }
    
    public String getConfigProperty(final String key) {
        return this.getProperty(key);
    }
    
    public String getConfigProperty(final String key, final String defaultValue) {
        return this.getProperty(key, defaultValue);
    }
}
