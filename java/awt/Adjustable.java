package java.awt;

import java.awt.event.AdjustmentListener;

public interface Adjustable
{
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    public static final int NO_ORIENTATION = 2;
    
    int getOrientation();
    
    void setMinimum(final int p0);
    
    int getMinimum();
    
    void setMaximum(final int p0);
    
    int getMaximum();
    
    void setUnitIncrement(final int p0);
    
    int getUnitIncrement();
    
    void setBlockIncrement(final int p0);
    
    int getBlockIncrement();
    
    void setVisibleAmount(final int p0);
    
    int getVisibleAmount();
    
    void setValue(final int p0);
    
    int getValue();
    
    void addAdjustmentListener(final AdjustmentListener p0);
    
    void removeAdjustmentListener(final AdjustmentListener p0);
}
