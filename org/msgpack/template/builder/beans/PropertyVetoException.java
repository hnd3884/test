package org.msgpack.template.builder.beans;

public class PropertyVetoException extends Exception
{
    private static final long serialVersionUID = 129596057694162164L;
    private final PropertyChangeEvent evt;
    
    public PropertyVetoException(final String message, final PropertyChangeEvent event) {
        super(message);
        this.evt = event;
    }
    
    public PropertyChangeEvent getPropertyChangeEvent() {
        return this.evt;
    }
}
