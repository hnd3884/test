package com.adventnet.client.components.chart.table;

import org.jfree.data.DomainOrder;
import java.util.Map;
import com.adventnet.client.components.chart.table.internal.FilterUtil;
import java.util.List;
import javax.swing.table.TableModel;
import org.jfree.data.xy.XYDataset;

public abstract class AbstractXYModelAdapter extends AbstractChartModelAdapter implements XYDataset
{
    public AbstractXYModelAdapter(final GraphData data) {
        super(data);
    }
    
    public AbstractXYModelAdapter(final TableModel data) {
        super(data);
    }
    
    public int getSeriesCount() {
        final List seriesList = super.getSeriesList();
        if (seriesList != null) {
            return seriesList.size();
        }
        return 0;
    }
    
    public int indexOf(final Comparable seriesKey) {
        final List seriesList = super.getSeriesNames();
        if (seriesList != null) {
            return seriesList.indexOf(seriesKey);
        }
        return -1;
    }
    
    public Comparable getSeriesKey(final int series) {
        final List seriesList = super.getSeriesNames();
        if (seriesList != null) {
            return seriesList.get(series);
        }
        return null;
    }
    
    @Override
    public String getSeriesOrPieName(final int i) {
        final Object seriesCol = super.model.getAxisColumns().get("SERIES");
        if (seriesCol != null) {
            return super.getSeriesNames().get(i);
        }
        return null;
    }
    
    public String getSeriesName(final int series) {
        return super.getDisplayName(series);
    }
    
    public int getItemCount(final int series) {
        final List seriesList = super.getSeriesList();
        if (seriesList != null && seriesList.size() > series) {
            final Object[] data = seriesList.get(series);
            final TableModel tm = (TableModel)data[1];
            return tm.getRowCount();
        }
        return 0;
    }
    
    public double getXValue(final int series, final int item) {
        double result = Double.NaN;
        final Number x = this.getX(series, item);
        if (x != null) {
            result = x.doubleValue();
        }
        return result;
    }
    
    public double getYValue(final int series, final int item) {
        double result = Double.NaN;
        final Number y = this.getY(series, item);
        if (y != null) {
            result = y.doubleValue();
        }
        return result;
    }
    
    Object getValue(final int series, final int item, final String type) {
        final List seriesList = super.getSeriesList();
        if (seriesList != null && seriesList.size() > series) {
            final Object[] data = seriesList.get(series);
            final TableModel tm = (TableModel)data[1];
            final Map axisColumns = super.getModel().getAxisColumns();
            final Object col = axisColumns.get(type);
            final int colIndex = FilterUtil.getIndex(tm, col);
            return tm.getValueAt(item, colIndex);
        }
        return null;
    }
    
    public DomainOrder getDomainOrder() {
        return DomainOrder.ASCENDING;
    }
}
