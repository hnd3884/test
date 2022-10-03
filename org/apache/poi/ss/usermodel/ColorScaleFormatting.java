package org.apache.poi.ss.usermodel;

public interface ColorScaleFormatting
{
    int getNumControlPoints();
    
    void setNumControlPoints(final int p0);
    
    Color[] getColors();
    
    void setColors(final Color[] p0);
    
    ConditionalFormattingThreshold[] getThresholds();
    
    void setThresholds(final ConditionalFormattingThreshold[] p0);
    
    ConditionalFormattingThreshold createThreshold();
}
