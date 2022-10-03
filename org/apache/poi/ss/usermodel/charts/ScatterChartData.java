package org.apache.poi.ss.usermodel.charts;

import java.util.List;
import org.apache.poi.util.Removal;

@Deprecated
@Removal(version = "4.2")
public interface ScatterChartData extends ChartData
{
    ScatterChartSeries addSerie(final ChartDataSource<?> p0, final ChartDataSource<? extends Number> p1);
    
    List<? extends ScatterChartSeries> getSeries();
}
