package org.apache.poi.ss.usermodel.charts;

import org.apache.poi.util.Removal;

@Deprecated
@Removal(version = "4.2")
public interface ChartAxisFactory
{
    ValueAxis createValueAxis(final AxisPosition p0);
    
    ChartAxis createCategoryAxis(final AxisPosition p0);
    
    ChartAxis createDateAxis(final AxisPosition p0);
}
