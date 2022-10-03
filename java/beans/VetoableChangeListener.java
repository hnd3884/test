package java.beans;

import java.util.EventListener;

public interface VetoableChangeListener extends EventListener
{
    void vetoableChange(final PropertyChangeEvent p0) throws PropertyVetoException;
}
