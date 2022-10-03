package javax.swing.event;

import javax.swing.SwingUtilities;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;

public final class SwingPropertyChangeSupport extends PropertyChangeSupport
{
    static final long serialVersionUID = 7162625831330845068L;
    private final boolean notifyOnEDT;
    
    public SwingPropertyChangeSupport(final Object o) {
        this(o, false);
    }
    
    public SwingPropertyChangeSupport(final Object o, final boolean notifyOnEDT) {
        super(o);
        this.notifyOnEDT = notifyOnEDT;
    }
    
    @Override
    public void firePropertyChange(final PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent == null) {
            throw new NullPointerException();
        }
        if (!this.isNotifyOnEDT() || SwingUtilities.isEventDispatchThread()) {
            super.firePropertyChange(propertyChangeEvent);
        }
        else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    SwingPropertyChangeSupport.this.firePropertyChange(propertyChangeEvent);
                }
            });
        }
    }
    
    public final boolean isNotifyOnEDT() {
        return this.notifyOnEDT;
    }
}
