package org.apache.poi.xssf.usermodel.charts;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScatterSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScatterStyle;
import org.openxmlformats.schemas.drawingml.x2006.chart.STScatterStyle;
import java.util.Iterator;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScatterChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.ss.usermodel.charts.ChartAxis;
import org.apache.poi.ss.usermodel.Chart;
import org.apache.poi.ss.usermodel.charts.ScatterChartSeries;
import org.apache.poi.ss.usermodel.charts.ChartDataSource;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.util.Removal;
import org.apache.poi.ss.usermodel.charts.ScatterChartData;

@Deprecated
@Removal(version = "4.2")
public class XSSFScatterChartData implements ScatterChartData
{
    private List<Series> series;
    
    public XSSFScatterChartData() {
        this.series = new ArrayList<Series>();
    }
    
    public ScatterChartSeries addSerie(final ChartDataSource<?> xs, final ChartDataSource<? extends Number> ys) {
        if (!ys.isNumeric()) {
            throw new IllegalArgumentException("Y axis data source must be numeric.");
        }
        final int numOfSeries = this.series.size();
        final Series newSerie = new Series(numOfSeries, numOfSeries, xs, ys);
        this.series.add(newSerie);
        return (ScatterChartSeries)newSerie;
    }
    
    public void fillChart(final Chart chart, final ChartAxis... axis) {
        if (!(chart instanceof XSSFChart)) {
            throw new IllegalArgumentException("Chart must be instance of XSSFChart");
        }
        final XSSFChart xssfChart = (XSSFChart)chart;
        final CTPlotArea plotArea = xssfChart.getCTChart().getPlotArea();
        final CTScatterChart scatterChart = plotArea.addNewScatterChart();
        this.addStyle(scatterChart);
        for (final Series s : this.series) {
            s.addToChart(scatterChart);
        }
        for (final ChartAxis ax : axis) {
            scatterChart.addNewAxId().setVal(ax.getId());
        }
    }
    
    public List<? extends Series> getSeries() {
        return this.series;
    }
    
    private void addStyle(final CTScatterChart ctScatterChart) {
        final CTScatterStyle scatterStyle = ctScatterChart.addNewScatterStyle();
        scatterStyle.setVal(STScatterStyle.LINE_MARKER);
    }
    
    static class Series extends AbstractXSSFChartSeries implements ScatterChartSeries
    {
        private int id;
        private int order;
        private ChartDataSource<?> xs;
        private ChartDataSource<? extends Number> ys;
        
        protected Series(final int id, final int order, final ChartDataSource<?> xs, final ChartDataSource<? extends Number> ys) {
            this.id = id;
            this.order = order;
            this.xs = xs;
            this.ys = ys;
        }
        
        public ChartDataSource<?> getXValues() {
            return this.xs;
        }
        
        public ChartDataSource<? extends Number> getYValues() {
            return this.ys;
        }
        
        protected void addToChart(final CTScatterChart ctScatterChart) {
            final CTScatterSer scatterSer = ctScatterChart.addNewSer();
            scatterSer.addNewIdx().setVal((long)this.id);
            scatterSer.addNewOrder().setVal((long)this.order);
            final CTAxDataSource xVal = scatterSer.addNewXVal();
            XSSFChartUtil.buildAxDataSource(xVal, this.xs);
            final CTNumDataSource yVal = scatterSer.addNewYVal();
            XSSFChartUtil.buildNumDataSource(yVal, this.ys);
            if (this.isTitleSet()) {
                scatterSer.setTx(this.getCTSerTx());
            }
        }
    }
}
