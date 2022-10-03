package org.apache.poi.ss.usermodel.charts;

import org.apache.poi.util.Removal;

@Deprecated
@Removal(version = "4.2")
public interface ChartLegend extends ManuallyPositionable
{
    LegendPosition getPosition();
    
    void setPosition(final LegendPosition p0);
    
    boolean isOverlay();
    
    void setOverlay(final boolean p0);
}
