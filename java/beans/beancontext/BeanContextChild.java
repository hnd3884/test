package java.beans.beancontext;

import java.beans.VetoableChangeListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

public interface BeanContextChild
{
    void setBeanContext(final BeanContext p0) throws PropertyVetoException;
    
    BeanContext getBeanContext();
    
    void addPropertyChangeListener(final String p0, final PropertyChangeListener p1);
    
    void removePropertyChangeListener(final String p0, final PropertyChangeListener p1);
    
    void addVetoableChangeListener(final String p0, final VetoableChangeListener p1);
    
    void removeVetoableChangeListener(final String p0, final VetoableChangeListener p1);
}
