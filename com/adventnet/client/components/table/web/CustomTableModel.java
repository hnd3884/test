package com.adventnet.client.components.table.web;

import com.adventnet.beans.rangenavigator.events.NavigationListener;
import javax.swing.event.TableModelListener;
import com.adventnet.client.view.web.ViewContext;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import com.adventnet.beans.xtable.SortColumn;
import com.adventnet.idioms.tablenavigator.TableNavigatorModel;

public class CustomTableModel implements TableNavigatorModel
{
    protected SortColumn[] modelSortColumns;
    protected SortColumn[] viewSortColumns;
    protected HashMap tableData;
    protected HashMap columnNames;
    protected long startIndex;
    protected long endIndex;
    protected int noOfRowsShown;
    protected int noOfColsShown;
    protected long totalRows;
    protected List rangeList;
    protected int totalRecords;
    protected String sortedColumn;
    protected char sortOrder;
    protected long pageLength;
    
    public CustomTableModel() {
    }
    
    public CustomTableModel(final ArrayList rows, final String headers, final ViewContext viewCtx) throws Exception {
        int i = 0;
        int j = 0;
        this.noOfColsShown = headers.split("\\|\\|").length;
        this.noOfRowsShown = rows.size();
        this.tableData = new HashMap(rows.size(), 0.75f);
        this.columnNames = new HashMap(headers.split("\\|\\|").length, 0.75f);
        final String[] header = headers.split("\\|\\|");
        for (int k = 0; k < header.length; ++k) {
            this.columnNames.put(k, header[k]);
        }
        while (i < rows.size()) {
            if (rows.size() > 0 && rows.get(i).split("\\|\\|").length != headers.split("\\|\\|").length) {
                throw new Exception("no of columns in header given does not match with no fo rows given for Rows");
            }
            while (j < rows.get(i).split("\\|\\|").length) {
                final String[] r = rows.get(i).split("\\|\\|");
                this.setValueAt(r[j], i, j);
                ++j;
            }
            j = 0;
            ++i;
        }
        final String fromindex = (String)viewCtx.getURLStateParameter("_FI");
        if (fromindex == null) {
            this.startIndex = Integer.parseInt((String)viewCtx.getStateParameter("_FI"));
        }
        else {
            this.startIndex = Integer.parseInt((String)viewCtx.getURLStateParameter("_FI"));
        }
        final String toindex = (String)viewCtx.getURLStateParameter("_TI");
        if (toindex == null) {
            this.endIndex = Integer.parseInt((String)viewCtx.getStateParameter("_TI"));
        }
        else {
            this.endIndex = Integer.parseInt((String)viewCtx.getURLStateParameter("_TI"));
        }
        this.rangeList = (List)viewCtx.getURLStateParameter("rangeList");
        if (this.rangeList == null) {
            this.rangeList = (List)viewCtx.getStateParameter("rangeList");
        }
        final String totallength = (String)viewCtx.getURLStateParameter("_TL");
        if (totallength == null) {
            if (viewCtx.getStateParameter("_TL") != null) {
                this.totalRows = Integer.parseInt((String)viewCtx.getStateParameter("_TL"));
            }
        }
        else {
            this.totalRows = Integer.parseInt((String)viewCtx.getURLStateParameter("_TL"));
        }
    }
    
    public CustomTableModel(final String[][] rows, final String[] header, final ViewContext viewCtx) throws Exception {
        int i = 0;
        int j = 0;
        if (rows.length > 0 && rows[i].length != header.length) {
            throw new Exception("no of columns in header given does not match with no fo rows given for Rows");
        }
        this.noOfColsShown = header.length;
        this.noOfRowsShown = rows.length;
        this.tableData = new HashMap(rows.length, 0.75f);
        this.columnNames = new HashMap(header.length, 0.75f);
        for (int k = 0; k < header.length; ++k) {
            this.columnNames.put(k, header[k]);
        }
        while (i < rows.length) {
            while (j < rows[i].length) {
                this.setValueAt(rows[i][j], i, j);
                ++j;
            }
            j = 0;
            ++i;
        }
        final String fromindex = (String)viewCtx.getURLStateParameter("_FI");
        if (fromindex == null) {
            this.startIndex = Integer.parseInt((String)viewCtx.getStateParameter("_FI"));
        }
        else {
            this.startIndex = Integer.parseInt((String)viewCtx.getURLStateParameter("_FI"));
        }
        final String toindex = (String)viewCtx.getURLStateParameter("_TI");
        if (toindex == null) {
            this.endIndex = Integer.parseInt((String)viewCtx.getStateParameter("_TI"));
        }
        else {
            this.endIndex = Integer.parseInt((String)viewCtx.getURLStateParameter("_TI"));
        }
        this.rangeList = (List)viewCtx.getURLStateParameter("rangeList");
        if (this.rangeList == null) {
            this.rangeList = (List)viewCtx.getStateParameter("rangeList");
        }
        final String totallength = (String)viewCtx.getURLStateParameter("_TL");
        if (totallength == null) {
            if (viewCtx.getStateParameter("_TL") != null) {
                this.totalRows = Integer.parseInt((String)viewCtx.getStateParameter("_TL"));
            }
        }
        else {
            this.totalRows = Integer.parseInt((String)viewCtx.getURLStateParameter("_TL"));
        }
    }
    
    public void setValueAt(final Object value, final int rowIndex, final int columnIndex) {
        if (this.tableData.containsKey(rowIndex)) {
            final HashMap rowObject = this.tableData.get(rowIndex);
            rowObject.put(columnIndex, value);
            this.tableData.put(rowIndex, rowObject);
        }
        else {
            final HashMap rowObject = new HashMap();
            rowObject.put(columnIndex, value);
            this.tableData.put(rowIndex, rowObject);
        }
    }
    
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final HashMap rowObject = this.tableData.get(rowIndex);
        return rowObject.get(columnIndex);
    }
    
    public int getColumnCount() {
        return this.noOfColsShown;
    }
    
    public int getRowCount() {
        return this.noOfRowsShown;
    }
    
    public String getColumnName(final int columnIndex) {
        return this.columnNames.get(columnIndex);
    }
    
    public long getTotalRows() {
        return this.totalRows;
    }
    
    public long getStartIndex() {
        return this.startIndex;
    }
    
    public long getEndIndex() {
        return this.endIndex;
    }
    
    public void setStartIndex(final long si) {
        this.startIndex = si;
    }
    
    public void setEndIndex(final long ei) {
        this.endIndex = ei;
    }
    
    public void setRangeList(final List ls) {
        this.rangeList = ls;
    }
    
    public List getRangeList() {
        return this.rangeList;
    }
    
    public void setTotalRecords(final int total) {
        this.totalRecords = total;
    }
    
    public int getTotalRecords() {
        return this.totalRecords;
    }
    
    public String getSortedColumn() {
        return this.sortedColumn;
    }
    
    public void setSortedColumn(final String name) {
        this.sortedColumn = name;
    }
    
    public char getSortOrder() {
        return this.sortOrder;
    }
    
    public void setSortOrder(final char order) {
        this.sortOrder = order;
    }
    
    public long getTotalRecordsCount() {
        return this.totalRecords;
    }
    
    public void setTotalRecordsCount(final int total) {
        this.totalRecords = total;
    }
    
    public SortColumn[] getModelSortedColumns() {
        return this.modelSortColumns;
    }
    
    public void setModelSortColumns(final SortColumn[] modelSortColumns) {
        this.modelSortColumns = modelSortColumns;
    }
    
    public int getColumnIndex(final String columnName) {
        for (int i = 0; i < this.columnNames.size(); ++i) {
            if (this.columnNames.get(i).equals(columnName)) {
                return i;
            }
        }
        return -1;
    }
    
    public long getPageLength() {
        return this.pageLength;
    }
    
    public void showRange(final long fromIndex, final long toIndex) {
        this.startIndex = fromIndex;
        this.endIndex = toIndex;
    }
    
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return false;
    }
    
    public Class getColumnClass(final int columnIndex) {
        return null;
    }
    
    public int getColumnSQLClass(final int columnIndex) {
        return 12;
    }
    
    public String getColSQLClass(final int index) {
        return "CHAR";
    }
    
    public void addTableModelListener(final TableModelListener l) {
    }
    
    public void removeTableModelListener(final TableModelListener l) {
    }
    
    public SortColumn[] getViewSortedColumns() {
        return null;
    }
    
    public void sortModel(final SortColumn[] columns) {
    }
    
    public void sortView(final SortColumn[] columns) {
    }
    
    public void setViewSortColumns(final SortColumn[] viewSortColumns) {
    }
    
    public void clearViewSortedColumns() {
    }
    
    public void addNavigationListener(final NavigationListener navigationListener) {
    }
    
    public void removeNavigationListener(final NavigationListener navigationListener) {
    }
    
    public void setPageLength(final long param) {
        this.pageLength = param;
    }
}
