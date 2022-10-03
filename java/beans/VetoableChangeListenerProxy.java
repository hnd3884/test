package java.beans;

import java.util.EventListenerProxy;

public class VetoableChangeListenerProxy extends EventListenerProxy<VetoableChangeListener> implements VetoableChangeListener
{
    private final String propertyName;
    
    public VetoableChangeListenerProxy(final String propertyName, final VetoableChangeListener vetoableChangeListener) {
        super(vetoableChangeListener);
        this.propertyName = propertyName;
    }
    
    @Override
    public void vetoableChange(final PropertyChangeEvent propertyChangeEvent) throws PropertyVetoException {
        this.getListener().vetoableChange(propertyChangeEvent);
    }
    
    public String getPropertyName() {
        return this.propertyName;
    }
}
