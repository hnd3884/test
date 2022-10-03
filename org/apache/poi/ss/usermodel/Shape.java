package org.apache.poi.ss.usermodel;

public interface Shape
{
    String getShapeName();
    
    Shape getParent();
    
    ChildAnchor getAnchor();
    
    boolean isNoFill();
    
    void setNoFill(final boolean p0);
    
    void setFillColor(final int p0, final int p1, final int p2);
    
    void setLineStyleColor(final int p0, final int p1, final int p2);
}
