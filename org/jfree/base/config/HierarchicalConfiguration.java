package org.jfree.base.config;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Set;
import java.util.Collections;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.TreeSet;
import org.jfree.util.Configuration;
import java.util.Properties;

public class HierarchicalConfiguration implements ModifiableConfiguration
{
    private Properties configuration;
    private transient Configuration parentConfiguration;
    
    public HierarchicalConfiguration() {
        this.configuration = new Properties();
    }
    
    public HierarchicalConfiguration(final Configuration parentConfiguration) {
        this();
        this.parentConfiguration = parentConfiguration;
    }
    
    private void collectPropertyKeys(final String prefix, final Configuration config, final TreeSet collector) {
        final Enumeration enum1 = config.getConfigProperties();
        while (enum1.hasMoreElements()) {
            final String key = enum1.nextElement();
            if (key.startsWith(prefix) && !collector.contains(key)) {
                collector.add(key);
            }
        }
        if (config instanceof HierarchicalConfiguration) {
            final HierarchicalConfiguration hconfig = (HierarchicalConfiguration)config;
            if (hconfig.parentConfiguration != null) {
                this.collectPropertyKeys(prefix, hconfig.parentConfiguration, collector);
            }
        }
    }
    
    protected void configurationLoaded() {
    }
    
    public Iterator findPropertyKeys(final String prefix) {
        final TreeSet keys = new TreeSet();
        this.collectPropertyKeys(prefix, this, keys);
        return Collections.unmodifiableSet((Set<?>)keys).iterator();
    }
    
    public Enumeration getConfigProperties() {
        return this.configuration.keys();
    }
    
    public String getConfigProperty(final String key) {
        return this.getConfigProperty(key, null);
    }
    
    public String getConfigProperty(final String key, final String defaultValue) {
        String value = this.configuration.getProperty(key);
        if (value == null) {
            if (this.isRootConfig()) {
                value = defaultValue;
            }
            else {
                value = this.parentConfiguration.getConfigProperty(key, defaultValue);
            }
        }
        return value;
    }
    
    protected Properties getConfiguration() {
        return this.configuration;
    }
    
    protected Configuration getParentConfig() {
        return this.parentConfiguration;
    }
    
    public void insertConfiguration(final HierarchicalConfiguration config) {
        config.setParentConfig(this.getParentConfig());
        this.setParentConfig(config);
    }
    
    public boolean isLocallyDefined(final String key) {
        return this.configuration.containsKey(key);
    }
    
    protected boolean isParentSaved() {
        return true;
    }
    
    private boolean isRootConfig() {
        return this.parentConfiguration == null;
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        final boolean readParent = in.readBoolean();
        if (readParent) {
            this.parentConfiguration = (ModifiableConfiguration)in.readObject();
        }
        else {
            this.parentConfiguration = null;
        }
        this.configurationLoaded();
    }
    
    public void setConfigProperty(final String key, final String value) {
        if (key == null) {
            throw new NullPointerException();
        }
        if (value == null) {
            this.configuration.remove(key);
        }
        else {
            this.configuration.setProperty(key, value);
        }
    }
    
    protected void setParentConfig(final Configuration config) {
        if (this.parentConfiguration == this) {
            throw new IllegalArgumentException("Cannot add myself as parent configuration.");
        }
        this.parentConfiguration = config;
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if (!this.isParentSaved()) {
            out.writeBoolean(false);
        }
        else {
            out.writeBoolean(true);
            out.writeObject(this.parentConfiguration);
        }
    }
}
