package org.apache.poi.ss.usermodel.charts;

import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.Removal;

@Deprecated
@Removal(version = "4.2")
public interface ChartSeries
{
    void setTitle(final String p0);
    
    void setTitle(final CellReference p0);
    
    String getTitleString();
    
    CellReference getTitleCellReference();
    
    TitleType getTitleType();
}
