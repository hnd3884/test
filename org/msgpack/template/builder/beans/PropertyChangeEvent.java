package org.msgpack.template.builder.beans;

import java.util.EventObject;

public class PropertyChangeEvent extends EventObject
{
    private static final long serialVersionUID = 7042693688939648123L;
    String propertyName;
    Object oldValue;
    Object newValue;
    Object propagationId;
    
    public PropertyChangeEvent(final Object source, final String propertyName, final Object oldValue, final Object newValue) {
        super(source);
        this.propertyName = propertyName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
    
    public String getPropertyName() {
        return this.propertyName;
    }
    
    public void setPropagationId(final Object propagationId) {
        this.propagationId = propagationId;
    }
    
    public Object getPropagationId() {
        return this.propagationId;
    }
    
    public Object getOldValue() {
        return this.oldValue;
    }
    
    public Object getNewValue() {
        return this.newValue;
    }
}
