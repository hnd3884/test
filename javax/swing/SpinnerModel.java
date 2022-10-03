package javax.swing;

import javax.swing.event.ChangeListener;

public interface SpinnerModel
{
    Object getValue();
    
    void setValue(final Object p0);
    
    Object getNextValue();
    
    Object getPreviousValue();
    
    void addChangeListener(final ChangeListener p0);
    
    void removeChangeListener(final ChangeListener p0);
}
