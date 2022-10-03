package com.adventnet.client.components.chart.table;

import java.util.Map;
import javax.swing.table.TableModel;

public class GraphData
{
    private TableModel data;
    private Map axisModelType;
    private Map axisColumns;
    public static final String X_COLUMN = "X";
    public static final String Y_COLUMN = "Y";
    public static final String Z_COLUMN = "Z";
    public static final String SERIES_COLUMN = "SERIES";
    public static final String X_START_COLUMN = "X_START";
    public static final String X_END_COLUMN = "X_END";
    public static final String Y_START_COLUMN = "Y_START";
    public static final String Y_END_COLUMN = "Y_END";
    
    public GraphData(final TableModel data, final Map axisColumns) {
        this.data = null;
        this.axisModelType = null;
        this.axisColumns = null;
        this.data = data;
        this.axisColumns = axisColumns;
    }
    
    private GraphData() {
        this.data = null;
        this.axisModelType = null;
        this.axisColumns = null;
    }
    
    public TableModel getData() {
        return this.data;
    }
    
    public void setData(final TableModel data) {
        this.data = data;
    }
    
    public Map getAxisColumns() {
        return this.axisColumns;
    }
    
    public void setAxisColumns(final Map axisColumns) {
        this.axisColumns = axisColumns;
    }
}
