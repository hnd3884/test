package org.apache.poi.ss.usermodel.charts;

import org.apache.poi.ss.usermodel.Chart;
import org.apache.poi.util.Removal;

@Deprecated
@Removal(version = "4.2")
public interface ChartData
{
    void fillChart(final Chart p0, final ChartAxis... p1);
}
