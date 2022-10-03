package java.beans;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.util.Vector;

public class PropertyEditorSupport implements PropertyEditor
{
    private Object value;
    private Object source;
    private Vector<PropertyChangeListener> listeners;
    
    public PropertyEditorSupport() {
        this.setSource(this);
    }
    
    public PropertyEditorSupport(final Object source) {
        if (source == null) {
            throw new NullPointerException();
        }
        this.setSource(source);
    }
    
    public Object getSource() {
        return this.source;
    }
    
    public void setSource(final Object source) {
        this.source = source;
    }
    
    @Override
    public void setValue(final Object value) {
        this.value = value;
        this.firePropertyChange();
    }
    
    @Override
    public Object getValue() {
        return this.value;
    }
    
    @Override
    public boolean isPaintable() {
        return false;
    }
    
    @Override
    public void paintValue(final Graphics graphics, final Rectangle rectangle) {
    }
    
    @Override
    public String getJavaInitializationString() {
        return "???";
    }
    
    @Override
    public String getAsText() {
        return (this.value != null) ? this.value.toString() : null;
    }
    
    @Override
    public void setAsText(final String value) throws IllegalArgumentException {
        if (this.value instanceof String) {
            this.setValue(value);
            return;
        }
        throw new IllegalArgumentException(value);
    }
    
    @Override
    public String[] getTags() {
        return null;
    }
    
    @Override
    public Component getCustomEditor() {
        return null;
    }
    
    @Override
    public boolean supportsCustomEditor() {
        return false;
    }
    
    @Override
    public synchronized void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        if (this.listeners == null) {
            this.listeners = new Vector<PropertyChangeListener>();
        }
        this.listeners.addElement(propertyChangeListener);
    }
    
    @Override
    public synchronized void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
        if (this.listeners == null) {
            return;
        }
        this.listeners.removeElement(propertyChangeListener);
    }
    
    public void firePropertyChange() {
        final Vector<PropertyChangeListener> unsafeClone;
        synchronized (this) {
            if (this.listeners == null) {
                return;
            }
            unsafeClone = this.unsafeClone(this.listeners);
        }
        final PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(this.source, null, null, null);
        for (int i = 0; i < unsafeClone.size(); ++i) {
            ((PropertyChangeListener)unsafeClone.elementAt(i)).propertyChange(propertyChangeEvent);
        }
    }
    
    private <T> Vector<T> unsafeClone(final Vector<T> vector) {
        return (Vector)vector.clone();
    }
}
