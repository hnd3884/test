package javax.swing;

import javax.swing.event.ChangeListener;

public interface BoundedRangeModel
{
    int getMinimum();
    
    void setMinimum(final int p0);
    
    int getMaximum();
    
    void setMaximum(final int p0);
    
    int getValue();
    
    void setValue(final int p0);
    
    void setValueIsAdjusting(final boolean p0);
    
    boolean getValueIsAdjusting();
    
    int getExtent();
    
    void setExtent(final int p0);
    
    void setRangeProperties(final int p0, final int p1, final int p2, final int p3, final boolean p4);
    
    void addChangeListener(final ChangeListener p0);
    
    void removeChangeListener(final ChangeListener p0);
}
