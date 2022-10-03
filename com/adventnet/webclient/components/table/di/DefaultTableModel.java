package com.adventnet.webclient.components.table.di;

import javax.swing.event.TableModelListener;
import com.adventnet.beans.rangenavigator.events.NavigationListener;
import com.adventnet.beans.xtable.SortColumn;
import java.util.List;
import com.adventnet.idioms.tablenavigator.TableNavigatorModel;

public class DefaultTableModel implements TableNavigatorModel
{
    private List tableData;
    private List columnNames;
    private SortColumn[] modelSortColumns;
    private SortColumn[] viewSortColumns;
    private long startIndex;
    private long endIndex;
    private long totalRecords;
    private long pageLength;
    
    public DefaultTableModel(final List tableData, final List tableColumns) {
        this.startIndex = 0L;
        this.endIndex = 0L;
        this.totalRecords = 0L;
        this.pageLength = 0L;
        this.tableData = tableData;
        this.columnNames = tableColumns;
    }
    
    public long getEndIndex() {
        return this.endIndex;
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
    
    public long getPageLength() {
        return this.pageLength;
    }
    
    public void removeNavigationListener(final NavigationListener navigationListener) {
    }
    
    public void setPageLength(final long param) {
    }
    
    public int getRowCount() {
        return this.tableData.size();
    }
    
    public int getColumnCount() {
        return this.columnNames.size();
    }
    
    public String getColumnName(final int columnIndex) {
        return this.columnNames.get(columnIndex);
    }
    
    public Class getColumnClass(final int columnIndex) {
        return null;
    }
    
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return true;
    }
    
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        return this.tableData.get(rowIndex).get(columnIndex);
    }
    
    public void setValueAt(final Object value, final int rowIndex, final int columnIndex) {
        final List rowObject = this.tableData.get(rowIndex);
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
}
