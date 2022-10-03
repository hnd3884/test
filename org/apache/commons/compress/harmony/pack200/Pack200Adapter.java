package org.apache.commons.compress.harmony.pack200;

import java.beans.PropertyChangeListener;
import java.util.TreeMap;
import java.util.SortedMap;
import java.beans.PropertyChangeSupport;

public abstract class Pack200Adapter
{
    protected static final int DEFAULT_BUFFER_SIZE = 8192;
    private final PropertyChangeSupport support;
    private final SortedMap<String, String> properties;
    
    public Pack200Adapter() {
        this.support = new PropertyChangeSupport(this);
        this.properties = new TreeMap<String, String>();
    }
    
    public SortedMap<String, String> properties() {
        return this.properties;
    }
    
    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        this.support.addPropertyChangeListener(listener);
    }
    
    protected void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
        this.support.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        this.support.removePropertyChangeListener(listener);
    }
    
    protected void completed(final double value) {
        this.firePropertyChange("pack.progress", null, String.valueOf((int)(100.0 * value)));
    }
}
