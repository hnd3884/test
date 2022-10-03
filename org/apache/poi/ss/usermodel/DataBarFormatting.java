package org.apache.poi.ss.usermodel;

public interface DataBarFormatting
{
    boolean isLeftToRight();
    
    void setLeftToRight(final boolean p0);
    
    boolean isIconOnly();
    
    void setIconOnly(final boolean p0);
    
    int getWidthMin();
    
    void setWidthMin(final int p0);
    
    int getWidthMax();
    
    void setWidthMax(final int p0);
    
    Color getColor();
    
    void setColor(final Color p0);
    
    ConditionalFormattingThreshold getMinThreshold();
    
    ConditionalFormattingThreshold getMaxThreshold();
}
