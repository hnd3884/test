package org.apache.poi.ss.usermodel;

public interface TableStyle
{
    String getName();
    
    int getIndex();
    
    boolean isBuiltin();
    
    DifferentialStyleProvider getStyle(final TableStyleType p0);
}
