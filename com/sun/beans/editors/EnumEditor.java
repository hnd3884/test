package com.sun.beans.editors;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.beans.PropertyEditor;

public final class EnumEditor implements PropertyEditor
{
    private final List<PropertyChangeListener> listeners;
    private final Class type;
    private final String[] tags;
    private Object value;
    
    public EnumEditor(final Class type) {
        this.listeners = new ArrayList<PropertyChangeListener>();
        final Object[] enumConstants = type.getEnumConstants();
        if (enumConstants == null) {
            throw new IllegalArgumentException("Unsupported " + type);
        }
        this.type = type;
        this.tags = new String[enumConstants.length];
        for (int i = 0; i < enumConstants.length; ++i) {
            this.tags[i] = ((Enum)enumConstants[i]).name();
        }
    }
    
    @Override
    public Object getValue() {
        return this.value;
    }
    
    @Override
    public void setValue(final Object value) {
        if (value != null && !this.type.isInstance(value)) {
            throw new IllegalArgumentException("Unsupported value: " + value);
        }
        final Object value2;
        final PropertyChangeListener[] array;
        synchronized (this.listeners) {
            value2 = this.value;
            this.value = value;
            Label_0083: {
                if (value == null) {
                    if (value2 != null) {
                        break Label_0083;
                    }
                }
                else if (!value.equals(value2)) {
                    break Label_0083;
                }
                return;
            }
            final int size = this.listeners.size();
            if (size == 0) {
                return;
            }
            array = this.listeners.toArray(new PropertyChangeListener[size]);
        }
        final PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(this, null, value2, value);
        final PropertyChangeListener[] array2 = array;
        for (int length = array2.length, i = 0; i < length; ++i) {
            array2[i].propertyChange(propertyChangeEvent);
        }
    }
    
    @Override
    public String getAsText() {
        return (this.value != null) ? ((Enum)this.value).name() : null;
    }
    
    @Override
    public void setAsText(final String s) {
        this.setValue((s != null) ? Enum.valueOf((Class<Enum>)this.type, s) : null);
    }
    
    @Override
    public String[] getTags() {
        return this.tags.clone();
    }
    
    @Override
    public String getJavaInitializationString() {
        final String asText = this.getAsText();
        return (asText != null) ? (this.type.getName() + '.' + asText) : "null";
    }
    
    @Override
    public boolean isPaintable() {
        return false;
    }
    
    @Override
    public void paintValue(final Graphics graphics, final Rectangle rectangle) {
    }
    
    @Override
    public boolean supportsCustomEditor() {
        return false;
    }
    
    @Override
    public Component getCustomEditor() {
        return null;
    }
    
    @Override
    public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        synchronized (this.listeners) {
            this.listeners.add(propertyChangeListener);
        }
    }
    
    @Override
    public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        synchronized (this.listeners) {
            this.listeners.remove(propertyChangeListener);
        }
    }
}
