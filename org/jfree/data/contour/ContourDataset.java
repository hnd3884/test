package org.jfree.data.contour;

import org.jfree.data.Range;
import org.jfree.data.xy.XYZDataset;

public interface ContourDataset extends XYZDataset
{
    double getMinZValue();
    
    double getMaxZValue();
    
    Number[] getXValues();
    
    Number[] getYValues();
    
    Number[] getZValues();
    
    int[] indexX();
    
    int[] getXIndices();
    
    Range getZValueRange(final Range p0, final Range p1);
    
    boolean isDateAxis(final int p0);
}
