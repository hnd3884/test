package com.adventnet.client.components.table.web;

import javax.swing.event.TableModelListener;
import com.adventnet.client.util.web.WebClientUtil;
import com.adventnet.beans.rangenavigator.events.NavigationListener;
import java.util.Iterator;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.DataSet;
import com.adventnet.client.view.web.ViewContext;
import java.util.ArrayList;
import java.util.HashMap;
import com.adventnet.beans.xtable.SortColumn;
import java.util.List;
import java.util.logging.Logger;
import com.adventnet.idioms.tablenavigator.TableNavigatorModel;

public class TableDatasetModel implements TableNavigatorModel
{
    private static final Logger out;
    protected List<List<Object>> tableData;
    protected List<String> columnNames;
    protected Object[] columnTypes;
    protected int[] columnSQLTypes;
    protected String[] dataTypes;
    protected SortColumn[] modelSortColumns;
    protected SortColumn[] viewSortColumns;
    protected long startIndex;
    protected long endIndex;
    protected long totalRecords;
    private long fetchedRecordsCount;
    protected long pageLength;
    protected HashMap totalSum;
    protected HashMap viewSum;
    
    public TableDatasetModel() {
        this.tableData = new ArrayList<List<Object>>();
        this.startIndex = 0L;
        this.endIndex = 0L;
        this.totalRecords = 0L;
        this.fetchedRecordsCount = 0L;
        this.pageLength = 0L;
        this.totalSum = new HashMap();
        this.viewSum = new HashMap();
    }
    
    public void updateModel(final ViewContext viewContext, final DataSet dataSet) throws Exception {
        this.tableData = new ArrayList<List<Object>>();
        final int columnCount = dataSet.getColumnCount();
        this.columnTypes = new Object[columnCount];
        this.columnSQLTypes = new int[columnCount];
        this.columnNames = new ArrayList<String>(columnCount);
        this.dataTypes = new String[columnCount];
        for (int i = 1; i <= columnCount; ++i) {
            this.columnNames.add(dataSet.getColumnName(i));
            this.columnTypes[i - 1] = dataSet.getColumnClassName(i);
            this.columnSQLTypes[i - 1] = ((dataSet.getColumnType(i) == 1111) ? 1 : dataSet.getColumnType(i));
        }
        if (viewContext != null) {
            final DataObject viewDataObject = viewContext.getModel().getViewConfiguration();
            final Row acTableViewRow = viewDataObject.getRow("ACTableViewConfig");
            final DataObject columnConfigDO = TableViewModel.getColumnConfigDO(acTableViewRow.get(4), viewDataObject);
            final Iterator<Row> columnConfigRows = columnConfigDO.getRows("ACColumnConfiguration");
            while (columnConfigRows.hasNext()) {
                final Row columnConfigRow = columnConfigRows.next();
                final String colAlias = (String)columnConfigRow.get("COLUMNALIAS");
                if (colAlias != null && this.columnNames.indexOf(colAlias) != -1) {
                    final int idx = this.columnNames.indexOf(colAlias);
                    final String col_type = (String)columnConfigRow.get("COLUMN_TYPE");
                    this.dataTypes[idx] = ((col_type != null) ? col_type : this.getColType(dataSet, this.columnNames.get(idx)));
                }
            }
        }
        while (dataSet.next()) {
            final List<Object> rowData = this.getRowData(dataSet, columnCount, this.columnNames, this.columnSQLTypes);
            this.tableData.add(rowData);
            ++this.fetchedRecordsCount;
        }
        if (this.startIndex == 1L && this.fetchedRecordsCount == 0L) {
            this.startIndex = 0L;
        }
        this.endIndex = Math.max(this.startIndex - 1L, 0L) + this.fetchedRecordsCount;
    }
    
    private String getColType(final DataSet ds, final String columnAlias) throws Exception {
        String col_type;
        if (ds.isNumber(columnAlias)) {
            col_type = "BIGINT";
        }
        else if (ds.isChar(columnAlias)) {
            col_type = "CHAR";
        }
        else if (ds.isDate(columnAlias)) {
            col_type = "DATE";
        }
        else if (ds.isTime(columnAlias)) {
            col_type = "TIME";
        }
        else if (ds.isTimestamp(columnAlias)) {
            col_type = "TIMESTAMP";
        }
        else if (ds.isBoolean(columnAlias)) {
            col_type = "BOOLEAN";
        }
        else if (ds.isBlob(columnAlias)) {
            col_type = "BLOB";
        }
        else {
            col_type = "CHAR";
        }
        return col_type;
    }
    
    public void addRow(final List<Object> rowData) {
        this.tableData.add(rowData);
    }
    
    public void addColumn(final List<String> colNames, final List<Object> colTypes) {
        final int columnCount = colNames.size();
        this.columnNames = new ArrayList<String>(columnCount);
        this.columnTypes = new Object[columnCount];
        for (int i = 0; i < columnCount; ++i) {
            this.columnNames.add(colNames.get(i));
            this.columnTypes[i] = colTypes.get(i);
        }
    }
    
    public void createInstance() {
        this.tableData = new ArrayList<List<Object>>();
    }
    
    public String display() {
        return "Started<->" + this.columnNames + "<->" + this.tableData + "<->" + this.tableData.size();
    }
    
    protected List<Object> getRowData(final DataSet ds, final int columnCount, final List<String> columnNames, final int[] columnSQLTypes) throws Exception {
        final List<Object> rowData = this.createRow(columnCount);
        for (int i = 0; i < columnCount; ++i) {
            final Object temp = ds.getValue(ds.findColumn((String)columnNames.get(i)), columnSQLTypes[i]);
            rowData.add(temp);
        }
        return rowData;
    }
    
    protected List<Object> createRow(final int columnCount) throws Exception {
        return new ArrayList<Object>(columnCount);
    }
    
    public long getEndIndex() {
        return this.endIndex;
    }
    
    public long getFetchedRecordsCount() {
        return this.fetchedRecordsCount;
    }
    
    public long getStartIndex() {
        return this.startIndex;
    }
    
    public void showRange(final long fromIndex, final long toIndex) {
        this.startIndex = fromIndex;
        this.endIndex = toIndex;
    }
    
    public long getTotalRecordsCount() {
        return this.totalRecords;
    }
    
    public long getPageLength() {
        return this.pageLength;
    }
    
    public void clearModelSortedColumns() {
        this.modelSortColumns = null;
    }
    
    public void clearViewSortedColumns() {
        this.viewSortColumns = null;
    }
    
    public SortColumn[] getModelSortedColumns() {
        return this.modelSortColumns;
    }
    
    public SortColumn[] getViewSortedColumns() {
        return this.viewSortColumns;
    }
    
    public void sortModel(final SortColumn[] columns) {
    }
    
    public void sortView(final SortColumn[] columns) {
    }
    
    public void addNavigationListener(final NavigationListener navigationListener) {
    }
    
    public void removeNavigationListener(final NavigationListener navigationListener) {
    }
    
    public void setPageLength(final long pagelength) {
        this.pageLength = pagelength;
    }
    
    public int getRowCount() {
        return this.tableData.size();
    }
    
    public int getColumnCount() {
        return this.columnNames.size();
    }
    
    public int getColumnIndex(final String columnName) {
        for (int i = 0; i < this.columnNames.size(); ++i) {
            if (this.columnNames.get(i).equals(columnName)) {
                return i;
            }
        }
        return -1;
    }
    
    public String getColumnName(final int columnIndex) {
        return this.columnNames.get(columnIndex);
    }
    
    public Class<?> getColumnClass(final int columnIndex) {
        final Object type = this.columnTypes[columnIndex];
        if (type instanceof String) {
            try {
                this.columnTypes[columnIndex] = WebClientUtil.loadClass((String)type);
            }
            catch (final ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return (Class)this.columnTypes[columnIndex];
    }
    
    public int getColumnSQLClass(final int columnIndex) {
        return this.columnSQLTypes[columnIndex];
    }
    
    public String getColumnDataType(final int columnIndex) {
        return this.dataTypes[columnIndex];
    }
    
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return false;
    }
    
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        return this.tableData.get(rowIndex).get(columnIndex);
    }
    
    public void setValueAt(final Object value, final int rowIndex, final int columnIndex) {
        final List<Object> rowObject = this.tableData.get(rowIndex);
        rowObject.set(columnIndex, value);
    }
    
    public void addTableModelListener(final TableModelListener l) {
    }
    
    public void removeTableModelListener(final TableModelListener l) {
    }
    
    public void setEndIndex(final long endIndex) {
        this.endIndex = endIndex;
    }
    
    public void setStartIndex(final long startIndex) {
        this.startIndex = startIndex;
    }
    
    public void setTotalRecordsCount(final long recordsCount) {
        this.totalRecords = recordsCount;
    }
    
    public void setModelSortColumns(final SortColumn[] modelSortColumns) {
        this.modelSortColumns = modelSortColumns;
    }
    
    public void setViewSortColumns(final SortColumn[] viewSortColumns) {
        this.viewSortColumns = viewSortColumns;
    }
    
    public HashMap getTotalSumMap() {
        return this.totalSum;
    }
    
    public HashMap getViewSumMap() {
        return this.viewSum;
    }
    
    public void setTotalSumMap(final HashMap map) {
        this.totalSum = map;
    }
    
    public void setViewSumMap(final HashMap map) {
        this.viewSum = map;
    }
    
    static {
        out = Logger.getLogger(TableDatasetModel.class.getName());
    }
}
