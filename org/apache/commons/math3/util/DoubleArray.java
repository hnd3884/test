package org.apache.commons.math3.util;

public interface DoubleArray
{
    int getNumElements();
    
    double getElement(final int p0);
    
    void setElement(final int p0, final double p1);
    
    void addElement(final double p0);
    
    void addElements(final double[] p0);
    
    double addElementRolling(final double p0);
    
    double[] getElements();
    
    void clear();
}
