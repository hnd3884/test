package org.apache.poi.xssf.usermodel.charts;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.STMarkerStyle;
import java.util.Iterator;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.ss.usermodel.charts.ChartAxis;
import org.apache.poi.ss.usermodel.Chart;
import org.apache.poi.ss.usermodel.charts.LineChartSeries;
import org.apache.poi.ss.usermodel.charts.ChartDataSource;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.util.Removal;
import org.apache.poi.ss.usermodel.charts.LineChartData;

@Deprecated
@Removal(version = "4.2")
public class XSSFLineChartData implements LineChartData
{
    private List<Series> series;
    
    public XSSFLineChartData() {
        this.series = new ArrayList<Series>();
    }
    
    public LineChartSeries addSeries(final ChartDataSource<?> categoryAxisData, final ChartDataSource<? extends Number> values) {
        if (!values.isNumeric()) {
            throw new IllegalArgumentException("Value data source must be numeric.");
        }
        final int numOfSeries = this.series.size();
        final Series newSeries = new Series(numOfSeries, numOfSeries, categoryAxisData, values);
        this.series.add(newSeries);
        return (LineChartSeries)newSeries;
    }
    
    public List<? extends LineChartSeries> getSeries() {
        return (List<? extends LineChartSeries>)this.series;
    }
    
    public void fillChart(final Chart chart, final ChartAxis... axis) {
        if (!(chart instanceof XSSFChart)) {
            throw new IllegalArgumentException("Chart must be instance of XSSFChart");
        }
        final XSSFChart xssfChart = (XSSFChart)chart;
        final CTPlotArea plotArea = xssfChart.getCTChart().getPlotArea();
        final CTLineChart lineChart = plotArea.addNewLineChart();
        lineChart.addNewVaryColors().setVal(false);
        for (final Series s : this.series) {
            s.addToChart(lineChart);
        }
        for (final ChartAxis ax : axis) {
            lineChart.addNewAxId().setVal(ax.getId());
        }
    }
    
    static class Series extends AbstractXSSFChartSeries implements LineChartSeries
    {
        private int id;
        private int order;
        private ChartDataSource<?> categories;
        private ChartDataSource<? extends Number> values;
        
        protected Series(final int id, final int order, final ChartDataSource<?> categories, final ChartDataSource<? extends Number> values) {
            this.id = id;
            this.order = order;
            this.categories = categories;
            this.values = values;
        }
        
        public ChartDataSource<?> getCategoryAxisData() {
            return this.categories;
        }
        
        public ChartDataSource<? extends Number> getValues() {
            return this.values;
        }
        
        protected void addToChart(final CTLineChart ctLineChart) {
            final CTLineSer ctLineSer = ctLineChart.addNewSer();
            ctLineSer.addNewIdx().setVal((long)this.id);
            ctLineSer.addNewOrder().setVal((long)this.order);
            ctLineSer.addNewMarker().addNewSymbol().setVal(STMarkerStyle.NONE);
            final CTAxDataSource catDS = ctLineSer.addNewCat();
            XSSFChartUtil.buildAxDataSource(catDS, this.categories);
            final CTNumDataSource valueDS = ctLineSer.addNewVal();
            XSSFChartUtil.buildNumDataSource(valueDS, this.values);
            if (this.isTitleSet()) {
                ctLineSer.setTx(this.getCTSerTx());
            }
        }
    }
}
