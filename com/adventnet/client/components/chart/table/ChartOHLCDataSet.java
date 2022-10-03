package com.adventnet.client.components.chart.table;

import javax.swing.table.TableModel;
import org.jfree.data.xy.OHLCDataset;

public class ChartOHLCDataSet extends CustomXYDataset implements OHLCDataset
{
    int openIDX;
    int highIDX;
    int lowIDX;
    int closeIDX;
    int volumeIDX;
    int cc;
    TableModel tm;
    
    public ChartOHLCDataSet(final TableModel tm) {
        super(tm, true, 0, -1);
        this.openIDX = 1;
        this.highIDX = 2;
        this.lowIDX = 3;
        this.closeIDX = 4;
        this.volumeIDX = 5;
        this.cc = -1;
        this.tm = null;
        this.tm = tm;
        this.cc = tm.getColumnCount();
    }
    
    private Number getNumber(final int series, final int item, final int colIdx) {
        if (this.cc > colIdx) {
            return this.convertToNumber(this.tm.getValueAt(item, colIdx));
        }
        return new Double(0.0);
    }
    
    private double getDoubleValue(final int series, final int item, final int colIdx) {
        double result = Double.NaN;
        final Number number = this.getNumber(series, item, colIdx);
        if (number != null) {
            result = number.doubleValue();
        }
        return result;
    }
    
    public Number getHigh(final int series, final int item) {
        return this.getNumber(series, item, this.highIDX);
    }
    
    public double getHighValue(final int series, final int item) {
        return this.getDoubleValue(series, item, this.highIDX);
    }
    
    public Number getLow(final int series, final int item) {
        return this.getNumber(series, item, this.lowIDX);
    }
    
    public double getLowValue(final int series, final int item) {
        return this.getDoubleValue(series, item, this.lowIDX);
    }
    
    public Number getOpen(final int series, final int item) {
        return this.getNumber(series, item, this.openIDX);
    }
    
    public double getOpenValue(final int series, final int item) {
        return this.getDoubleValue(series, item, this.openIDX);
    }
    
    public Number getClose(final int series, final int item) {
        return this.getNumber(series, item, this.closeIDX);
    }
    
    public double getCloseValue(final int series, final int item) {
        return this.getDoubleValue(series, item, this.closeIDX);
    }
    
    public Number getVolume(final int series, final int item) {
        return this.getNumber(series, item, this.volumeIDX);
    }
    
    public double getVolumeValue(final int series, final int item) {
        return this.getDoubleValue(series, item, this.volumeIDX);
    }
    
    @Override
    public int getSeriesCount() {
        return 1;
    }
    
    @Override
    public Comparable getSeriesKey(final int param) {
        return "Series1";
    }
    
    @Override
    public int indexOf(final Comparable seriesKey) {
        return 0;
    }
    
    @Override
    public String getSeriesName(final int series) {
        return "Series1";
    }
    
    @Override
    public Number getY(final int series, final int item) {
        return this.getClose(series, item);
    }
    
    @Override
    public double getYValue(final int series, final int item) {
        return this.getCloseValue(series, item);
    }
}
