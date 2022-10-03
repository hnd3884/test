package com.adventnet.client.components.chart.table;

import java.util.Date;
import com.adventnet.client.components.chart.table.internal.FilterUtil;
import java.util.logging.Level;
import javax.swing.event.TableModelEvent;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.table.TableModel;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.event.TableModelListener;
import org.jfree.data.general.AbstractDataset;

public abstract class AbstractChartModelAdapter extends AbstractDataset implements TableModelListener, MCKDataset
{
    protected GraphData model;
    protected Logger logger;
    private List seriesList;
    private List seriesNames;
    
    private AbstractChartModelAdapter() {
        this.model = null;
        this.logger = Logger.getLogger(this.getClass().getName());
        this.seriesList = null;
        this.seriesNames = null;
    }
    
    public AbstractChartModelAdapter(final GraphData model) {
        this.model = null;
        this.logger = Logger.getLogger(this.getClass().getName());
        this.seriesList = null;
        this.seriesNames = null;
        this.model = model;
        this.model.getData().addTableModelListener(this);
    }
    
    public AbstractChartModelAdapter(final TableModel tbModel) {
        this.model = null;
        this.logger = Logger.getLogger(this.getClass().getName());
        this.seriesList = null;
        this.seriesNames = null;
        final int columnCount = tbModel.getColumnCount();
        final Map axisMap = new HashMap();
        final List tempList = new ArrayList(5);
        for (int i = 0; i < columnCount; ++i) {
            final String columnName = tbModel.getColumnName(i);
            if (columnName.equals("SERIES")) {
                tempList.add(new Integer(i));
            }
            else {
                axisMap.put(columnName, new Integer(i));
            }
        }
        axisMap.put("SERIES", tempList.toArray(new Integer[tempList.size()]));
        this.model = new GraphData(tbModel, axisMap);
        this.model.getData().addTableModelListener(this);
    }
    
    public void tableChanged(final TableModelEvent e) {
        this.fireDatasetChanged();
    }
    
    protected final GraphData getModel() {
        return this.model;
    }
    
    protected final List getSeriesList() {
        if (this.seriesList == null) {
            final TableModel tm = this.getModel().getData();
            final Map axisColumns = this.getModel().getAxisColumns();
            this.logger.logp(Level.INFO, "AbstractChartModelAdapter", "getSeriesList", "axisColumns : {0}", axisColumns);
            final Object seriesCol = axisColumns.get("SERIES");
            this.logger.logp(Level.INFO, "AbstractChartModelAdapter", "getSeriesList", "seriesCol : {0}", seriesCol);
            final int[] seriesColIndex = FilterUtil.getSeriesIndex(tm, seriesCol);
            this.seriesList = FilterUtil.getGroupedTableModel(tm, seriesColIndex);
            this.logger.logp(Level.INFO, "AbstractChartModelAdapter", "getSeriesList", "seriesList : {0}", this.seriesList);
        }
        return this.seriesList;
    }
    
    protected void fireDatasetChanged() {
        this.seriesList = null;
        this.seriesNames = null;
        super.fireDatasetChanged();
    }
    
    protected final String getDisplayName(final int series) {
        final List seriesNms = this.getSeriesNames();
        if (seriesNms.size() > series) {
            return seriesNms.get(series);
        }
        throw new RuntimeException("there is no such series in the index : " + series);
    }
    
    public abstract String getSeriesOrPieName(final int p0);
    
    public final List getSeriesNames() {
        if (this.seriesNames == null) {
            this.seriesNames = new ArrayList();
            for (int len = this.getSeriesList().size(), i = 0; i < len; ++i) {
                final Object[] data = this.seriesList.get(i);
                this.seriesNames.add(data[0].toString());
            }
        }
        return this.seriesNames;
    }
    
    protected Number convertToNumber(final Object ob) {
        if (ob == null) {
            return null;
        }
        if (ob instanceof Number) {
            return (Number)ob;
        }
        if (ob instanceof Date) {
            return new Long(((Date)ob).getTime());
        }
        throw new RuntimeException("Illegal argument excpetion , it needs to be either instance of Number or java.util.Date : " + ob.getClass().getName());
    }
}
