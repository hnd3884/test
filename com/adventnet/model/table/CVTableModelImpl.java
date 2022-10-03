package com.adventnet.model.table;

import com.adventnet.model.table.update.internal.TableRowAction;
import javax.swing.SwingUtilities;
import com.adventnet.model.table.update.internal.TableModelNotification;
import java.util.Enumeration;
import com.adventnet.persistence.WritableDataObject;
import java.util.HashMap;
import com.adventnet.customview.ViewData;
import java.lang.reflect.UndeclaredThrowableException;
import com.adventnet.model.table.internal.CVTableModelRow;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import com.adventnet.ds.query.Range;
import java.util.Arrays;
import com.adventnet.beans.rangenavigator.events.NavigationListener;
import javax.swing.table.TableModel;
import javax.swing.event.TableModelEvent;
import com.adventnet.ds.query.Table;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.customview.CustomViewException;
import com.adventnet.persistence.Row;
import java.util.logging.Level;
import java.util.Map;
import com.adventnet.beans.rangenavigator.events.NavigationEvent;
import java.util.ArrayList;
import com.adventnet.persistence.DataObject;
import javax.swing.event.TableModelListener;
import java.util.Hashtable;
import java.util.Vector;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import java.util.Properties;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.customview.CustomViewRequest;
import com.adventnet.customview.CustomViewManager;
import java.util.logging.Logger;
import com.adventnet.customview.CustomViewManagerUser;

public class CVTableModelImpl implements CVTableModel, CustomViewManagerUser
{
    private static final String CLASS_NAME;
    private static Logger OUT;
    private static int instanceIdCounter;
    private int instanceId;
    private int currentBaseDataStateId;
    private Integer instanceIdObj;
    private static byte[] lockObj;
    protected transient CustomViewManager cvMgr;
    protected CustomViewRequest cvRequest;
    protected TableModelData tableModelData;
    protected SelectQuery selectQuery;
    protected List tableAliases;
    protected Properties tableAliasesToTableNames;
    protected int tableSize;
    protected Column[] columns;
    protected SortColumn[] sortColumns;
    protected transient com.adventnet.beans.xtable.SortColumn[] xSortColumns;
    protected SortColumn[] viewSortColumns;
    protected transient com.adventnet.beans.xtable.SortColumn[] xViewSortColumns;
    protected boolean viewSortEnabled;
    protected Vector viewSortedData;
    protected RowComparator comparator;
    protected Class[] colClasses;
    protected int[] colTypes;
    protected String[] col_Types;
    protected String[] colNames;
    protected Hashtable dataObjects;
    protected Vector tableData;
    protected Vector keys;
    protected TableModelListener tableModelListener;
    protected long totalCount;
    protected long fetchSize;
    protected long startIndex;
    protected long endIndex;
    protected int columnCount;
    protected int[] keyColumnIndices;
    protected DataObject cvConfigDO;
    protected String cvName;
    protected ArrayList listeners;
    protected ArrayList nListeners;
    NavigationEvent ne;
    private Map sortOrders;
    private static boolean useSwingThread;
    private transient SendFromQueue sfq;
    private ArrayList queue;
    private boolean carryOverCount;
    
    public CVTableModelImpl(final TableModelData tableModelData, final DataObject cvConfigDO, final SelectQuery selectQuery) throws CustomViewException {
        this.tableAliases = new ArrayList();
        this.tableAliasesToTableNames = new Properties();
        this.colClasses = null;
        this.colTypes = null;
        this.col_Types = null;
        this.colNames = null;
        this.dataObjects = new Hashtable();
        this.listeners = new ArrayList();
        this.nListeners = new ArrayList();
        this.ne = new NavigationEvent((Object)this);
        this.sfq = new SendFromQueue();
        this.queue = new ArrayList(100);
        this.carryOverCount = false;
        synchronized (CVTableModelImpl.lockObj) {
            this.instanceId = CVTableModelImpl.instanceIdCounter++;
            this.instanceIdObj = new Integer(this.instanceId);
            CVTableModelImpl.OUT.log(Level.FINER, " Inside CVTableModelImpl[{0}]", this.instanceIdObj);
        }
        this.currentBaseDataStateId = this.instanceId;
        this.tableModelData = tableModelData;
        this.cvConfigDO = cvConfigDO;
        try {
            if (cvConfigDO.containsTable("CustomViewConfiguration")) {
                final Row cvConfigRow = cvConfigDO.getFirstRow("CustomViewConfiguration");
                this.cvName = (String)cvConfigRow.get(2);
            }
            else {
                final Row cvConfigRow = new Row("CustomViewConfiguration");
                cvConfigRow.set(2, (Object)(this.cvName = "CVTableModelImpl_Temp_View"));
                cvConfigDO.addRow(cvConfigRow);
            }
            this.selectQuery = selectQuery;
            this.initTableAliases();
            this.resetData();
        }
        catch (final DataAccessException dae) {
            throw new CustomViewException((Throwable)dae);
        }
        catch (final Exception e) {
            e.printStackTrace();
            throw new CustomViewException(e);
        }
    }
    
    public int getCurrentBaseDataStateId() {
        return this.currentBaseDataStateId;
    }
    
    private void initTableAliases() {
        final List tables = this.selectQuery.getTableList();
        this.tableSize = tables.size();
        for (int i = 0; i < this.tableSize; ++i) {
            final Table selectTable = tables.get(i);
            final String tableName = selectTable.getTableName();
            final String aliasName = selectTable.getTableAlias();
            this.tableAliases.add(aliasName);
            this.tableAliasesToTableNames.setProperty(aliasName, tableName);
        }
    }
    
    @Override
    public void setCustomViewManager(final CustomViewManager cvMgr) {
        this.cvMgr = cvMgr;
        this.sfq = new SendFromQueue();
        this.ne = new NavigationEvent((Object)this);
    }
    
    @Override
    public void setCustomViewRequest(final CustomViewRequest cvRequest) {
        this.cvRequest = cvRequest;
    }
    
    public synchronized void addTableModelListener(final TableModelListener tml) {
        CVTableModelImpl.OUT.log(Level.FINER, " CVTableModelImpl : adding TableModelListener : {0}", tml);
        if (!this.listeners.contains(tml)) {
            this.listeners.add(tml);
        }
        CVTableModelImpl.OUT.log(Level.FINER, " CVTableModelImpl : listeners : {0}", this.listeners);
    }
    
    public synchronized void removeTableModelListener(final TableModelListener tml) {
        CVTableModelImpl.OUT.log(Level.FINER, " CVTableModelImpl : removing TableModelListener : {0}", tml);
        this.listeners.remove(tml);
        CVTableModelImpl.OUT.log(Level.FINER, " CVTableModelImpl : listeners : {0}", this.listeners);
    }
    
    protected void fireHeaderRowEvent() {
        this.updateTableModelData();
        this.fireTableChanged(new TableModelEvent((TableModel)this, -1));
    }
    
    protected void updateTableModelData() {
        this.tableModelData.setTableData(this.tableData);
        this.tableModelData.setTotal(this.totalCount);
        this.tableModelData.setStartIndex(this.startIndex);
        this.tableModelData.setEndIndex(this.endIndex);
    }
    
    protected void fireTableChanged(final TableModelEvent tableModelEvent) {
        this.updateTableModelData();
        for (int size = this.listeners.size(), i = 0; i < size; ++i) {
            this.listeners.get(i).tableChanged(tableModelEvent);
        }
        this.fireNavigationEvent();
    }
    
    protected void fireNavigationEvent() {
        for (int nSize = this.nListeners.size(), i = 0; i < nSize; ++i) {
            this.nListeners.get(i).navigationChanged(this.ne);
        }
    }
    
    protected void resetData() {
        final List columnsList = this.selectQuery.getSelectColumns();
        this.columns = columnsList.toArray(new Column[columnsList.size()]);
        final List sortColumnsList = this.selectQuery.getSortColumns();
        final int size;
        if (sortColumnsList != null && (size = sortColumnsList.size()) > 0) {
            CVTableModelImpl.OUT.log(Level.FINER, " sortColumnsList : {0}", sortColumnsList);
            CVTableModelImpl.OUT.log(Level.FINER, " size : {0}", new Integer(size));
            this.sortColumns = sortColumnsList.toArray(new SortColumn[size]);
            this.xSortColumns = this.getXSortColumns(this.sortColumns);
        }
        if (this.selectQuery.getRange() != null) {
            this.fetchSize = this.selectQuery.getRange().getNumberOfObjects();
        }
        this.tableData = this.tableModelData.getTableData();
        this.keys = this.tableModelData.getKeys();
        this.totalCount = this.tableModelData.getTotal();
        this.startIndex = this.tableModelData.getStartIndex();
        this.endIndex = this.tableModelData.getEndIndex();
        this.colClasses = this.tableModelData.getColumnClasses();
        this.colTypes = this.tableModelData.getColumnSQLTypes();
        this.col_Types = this.tableModelData.getColSQLTypes();
        this.columnCount = this.columns.length;
        this.colNames = getColumnNames(this.columns);
        this.keyColumnIndices = this.tableModelData.getKeyColumnIndices();
        try {
            if (this.cvConfigDO.containsTable("TableViewSortColumn")) {
                this.viewSortColumns = getViewSortColumnsFromDO(this.cvName, this.cvConfigDO);
                this.setSortOrderToSortColumns(this.sortColumns);
                this.xViewSortColumns = this.getXSortColumns(this.viewSortColumns);
            }
        }
        catch (final DataAccessException dae) {
            dae.printStackTrace();
        }
        this.sortInternal();
        this.fireHeaderRowEvent();
    }
    
    private void setSortOrderToSortColumns(final SortColumn[] sortColumnsToUpdate) {
        if (sortColumnsToUpdate == null || this.sortOrders == null) {
            return;
        }
        for (int len = sortColumnsToUpdate.length, i = 0; i < len; ++i) {
            final Column col = sortColumnsToUpdate[i].getColumn();
            final Vector sortOrder = this.sortOrders.get(col);
            if (sortOrder != null) {
                sortColumnsToUpdate[i].setSortOrder(sortOrder);
            }
        }
    }
    
    private com.adventnet.beans.xtable.SortColumn[] getXSortColumns(final SortColumn[] dsSortColumns) {
        if (dsSortColumns == null) {
            return null;
        }
        final int len = dsSortColumns.length;
        final com.adventnet.beans.xtable.SortColumn[] xTableSortColumns = new com.adventnet.beans.xtable.SortColumn[len];
        for (int i = 0; i < len; ++i) {
            xTableSortColumns[i] = new com.adventnet.beans.xtable.SortColumn(this.findIndex(dsSortColumns[i].getColumn()), dsSortColumns[i].isAscending());
        }
        return xTableSortColumns;
    }
    
    protected void updateSelectQuery() {
        if (this.columns != null) {
            for (final Column sc : this.selectQuery.getSelectColumns()) {
                this.selectQuery.removeSelectColumn(sc);
            }
            this.selectQuery.addSelectColumns((List)Arrays.asList(this.columns));
        }
        if (this.sortColumns != null) {
            for (final SortColumn sc2 : this.selectQuery.getSortColumns()) {
                this.selectQuery.removeSortColumn(sc2);
            }
            this.selectQuery.addSortColumns((List)Arrays.asList(this.sortColumns));
        }
        CVTableModelImpl.OUT.finer(" ####cvtmi setting Range.... ");
        CVTableModelImpl.OUT.log(Level.FINER, " startIndex : {0}", new Long(this.startIndex));
        CVTableModelImpl.OUT.log(Level.FINER, " endIndex   : {0}", new Long(this.endIndex));
        CVTableModelImpl.OUT.log(Level.FINER, " fetchSize   : {0}", new Long(this.fetchSize));
        this.selectQuery.setRange(new Range((int)this.startIndex, (int)this.fetchSize));
        this.cvRequest.setSelectQuery(this.selectQuery);
        if (this.viewSortColumns != null) {
            try {
                addViewSortColumnsToDO(this.cvName, this.viewSortColumns, this.cvConfigDO);
            }
            catch (final DataAccessException dae) {
                CVTableModelImpl.OUT.log(Level.FINE, "Exception during addViewSortColumnsToDO", (Throwable)dae);
            }
        }
        CVTableModelImpl.OUT.log(Level.FINER, " ####cvtmi cvConfigDO : {0}", this.cvConfigDO);
        this.cvRequest.setCustomViewConfiguration(this.cvConfigDO);
        CVTableModelImpl.OUT.log(Level.FINER, " ####cvtmi cvRequest : {0}", this.cvRequest);
    }
    
    private static void addViewSortColumnsToDO(final String cvName, final SortColumn[] tableViewSortColumns, final DataObject cvConfigDataObject) throws DataAccessException {
        final Row cvRow = cvConfigDataObject.getRow("CustomViewConfiguration");
        final Iterator rowIterator = cvConfigDataObject.getRows("TableViewSortColumn");
        final List tableColRows = new ArrayList();
        while (rowIterator.hasNext()) {
            tableColRows.add(rowIterator.next());
        }
        for (int i = tableColRows.size() - 1; i > -1; --i) {
            cvConfigDataObject.deleteRow((Row)tableColRows.get(i));
        }
        for (int i = tableViewSortColumns.length - 1; i > -1; --i) {
            final Row tableViewSortColumnRow = new Row("TableViewSortColumn");
            tableViewSortColumnRow.set(1, cvRow.get(1));
            tableViewSortColumnRow.set("TABLEALIAS", (Object)tableViewSortColumns[i].getTableAlias());
            tableViewSortColumnRow.set("COLUMNNAME", (Object)tableViewSortColumns[i].getColumnName());
            tableViewSortColumnRow.set("ASCENDING_ORDER", (Object)tableViewSortColumns[i].isAscending());
            tableViewSortColumnRow.set("SORTINDEX", (Object)new Integer(i));
            cvConfigDataObject.addRow(tableViewSortColumnRow);
        }
    }
    
    private static SortColumn[] getViewSortColumnsFromDO(final String cvName, final DataObject cvConfigDataObject) throws DataAccessException {
        if (!cvConfigDataObject.containsTable("TableViewSortColumn")) {
            return null;
        }
        final Row cvConfigRow = new Row("CustomViewConfiguration");
        cvConfigRow.set(2, (Object)cvName);
        final Iterator rowIterator = cvConfigDataObject.getRows("TableViewSortColumn", cvConfigRow);
        final List listForSorting = new ArrayList();
        while (rowIterator.hasNext()) {
            listForSorting.add(rowIterator.next());
        }
        CVTableModelImpl.OUT.log(Level.FINER, " listForSorting : {0}", listForSorting);
        if (listForSorting.isEmpty()) {
            return null;
        }
        final Comparator viewSortColumnComparator = new Comparator() {
            @Override
            public int compare(final Object o1, final Object o2) {
                final Row r1 = (Row)o1;
                final Row r2 = (Row)o2;
                final int i1 = (int)r1.get(5);
                final int i2 = (int)r2.get(5);
                return i1 - i2;
            }
            
            @Override
            public boolean equals(final Object obj) {
                return false;
            }
        };
        Collections.sort((List<Object>)listForSorting, viewSortColumnComparator);
        CVTableModelImpl.OUT.log(Level.FINER, " listForSorting : {0}", listForSorting);
        final int len = listForSorting.size();
        final SortColumn[] tableViewSortColumns = new SortColumn[len];
        for (int i = 0; i < len; ++i) {
            final Row viewSortColumnRow = listForSorting.get(i);
            tableViewSortColumns[i] = new SortColumn((String)viewSortColumnRow.get(2), (String)viewSortColumnRow.get(3), (boolean)viewSortColumnRow.get("ASCENDING_ORDER"));
        }
        return tableViewSortColumns;
    }
    
    public Class getColumnClass(final int index) {
        return this.colClasses[index];
    }
    
    @Override
    public int getColumnSQLClass(final int index) {
        return this.colTypes[index];
    }
    
    @Override
    public String getColSQLClass(final int index) {
        return this.col_Types[index];
    }
    
    public int getColumnCount() {
        return this.columnCount;
    }
    
    public String getColumnName(final int index) {
        return this.colNames[index];
    }
    
    public static String[] getColumnNames(final Column[] columns) {
        final int len = columns.length;
        final String[] colNames = new String[len];
        for (int i = 0; i < len; ++i) {
            final String aliasName = columns[i].getColumnAlias();
            if (aliasName != null) {
                colNames[i] = aliasName;
            }
            else {
                colNames[i] = columns[i].getColumnName();
            }
        }
        return colNames;
    }
    
    public int getRowCount() {
        return this.tableData.size();
    }
    
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        try {
            final CVTableModelRow tableModelRow = this.getCVTableModelRow(rowIndex);
            if (columnIndex == -999) {
                return tableModelRow.getTableList();
            }
            final List row = tableModelRow.getRowContents();
            return row.get(columnIndex);
        }
        catch (final ArrayIndexOutOfBoundsException aioobe) {
            CVTableModelImpl.OUT.log(Level.FINE, "CVTableModelImpl : ArrayIndexOutOfBoundsException during getValueAt", aioobe);
            return null;
        }
    }
    
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return false;
    }
    
    public synchronized void setValueAt(final Object p0, final int p1, final int p2) {
    }
    
    public long getTotalRecordsCount() {
        return this.totalCount;
    }
    
    public synchronized void refresh() {
        try {
            this.updateSelectQuery();
            if (this.carryOverCount) {
                this.cvRequest.set("TOTAL", new Long(this.totalCount));
            }
            final ViewData data = this.cvMgr.getData(this.cvRequest);
            final CVTableModelImpl model = (CVTableModelImpl)data.getModel();
            this.currentBaseDataStateId = model.getCurrentBaseDataStateId();
            this.tableModelData = model.getModelData();
            this.sortOrders = model.getSortOrders();
            CVTableModelImpl.OUT.log(Level.FINER, "CVTableModelImpl : sortOrders : {0}", this.sortOrders);
            this.resetData();
        }
        catch (final UndeclaredThrowableException ute) {
            ute.printStackTrace();
        }
        catch (final CustomViewException cve) {
            cve.printStackTrace();
        }
    }
    
    public long getStartIndex() {
        return this.startIndex;
    }
    
    public long getEndIndex() {
        return this.endIndex;
    }
    
    public synchronized void showRange(final long startIndex, final long endIndex) {
        if (startIndex == 0L && endIndex == 0L) {
            CVTableModelImpl.OUT.finest("CVTableModelImpl : invalid range startIndex : 0 ; endIndex : 0");
            return;
        }
        if (startIndex == this.startIndex && endIndex == this.endIndex) {
            CVTableModelImpl.OUT.finest("CVTableModelImpl : showRange invoked with the same range values as in model - ignoring.");
            return;
        }
        CVTableModelImpl.OUT.finer(" ##################################################ssssssssssssssssss");
        CVTableModelImpl.OUT.finer(" Entering CVTableModelImpl.showRange...");
        CVTableModelImpl.OUT.log(Level.FINER, " startIndex : {0}", new Long(startIndex));
        CVTableModelImpl.OUT.log(Level.FINER, " endIndex   : {0}", new Long(endIndex));
        CVTableModelImpl.OUT.log(Level.FINER, " fetchSize   : {0}", new Long(this.fetchSize));
        if (startIndex > this.totalCount) {
            CVTableModelImpl.OUT.log(Level.INFO, " Invalid Range : startIndex {0} is greater than totalCount {1}", new Object[] { new Long(startIndex), new Long(this.totalCount) });
            this.fireNavigationEvent();
            return;
        }
        if (startIndex <= 0L || endIndex <= 0L) {
            CVTableModelImpl.OUT.log(Level.INFO, " Invalid Range : negative or 0 values : startIndex {0}, endIndex {1}", new Object[] { new Long(startIndex), new Long(endIndex) });
            this.fireNavigationEvent();
            return;
        }
        if (endIndex < startIndex) {
            CVTableModelImpl.OUT.log(Level.INFO, " Invalid Range : endIndex : {0} is less than startIndex {1}", new Object[] { new Long(endIndex), new Long(startIndex) });
            this.fireNavigationEvent();
            return;
        }
        if (endIndex == this.totalCount) {
            final long newFetchSize = endIndex - startIndex + 1L;
            if (newFetchSize > this.fetchSize) {
                this.fetchSize = newFetchSize;
            }
        }
        else {
            this.fetchSize = endIndex - startIndex + 1L;
        }
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        CVTableModelImpl.OUT.log(Level.FINER, " startIndex : {0}", new Long(startIndex));
        CVTableModelImpl.OUT.log(Level.FINER, " endIndex   : {0}", new Long(endIndex));
        CVTableModelImpl.OUT.log(Level.FINER, " fetchSize   : {0}", new Long(this.fetchSize));
        CVTableModelImpl.OUT.finer(" Invoking CVTableModelImpl.refresh...");
        this.refresh();
        CVTableModelImpl.OUT.finer(" After invoking CVTableModelImpl.refresh...");
        CVTableModelImpl.OUT.finer(" Exiting CVTableModelImpl.showRange...");
        CVTableModelImpl.OUT.finer(" ##################################################");
    }
    
    @Override
    public Column[] getColumns() {
        CVTableModelImpl.OUT.log(Level.FINER, "getColumns called for model : {0}", this);
        return this.columns;
    }
    
    @Override
    public synchronized void setColumns(final Column[] columns) {
        CVTableModelImpl.OUT.log(Level.FINER, "setColumns called for model : {0}", this);
        this.columns = columns;
        this.refresh();
    }
    
    @Override
    public Object getRow(final long rowIndex) {
        CVTableModelImpl.OUT.log(Level.FINER, "getRow called for model : {0}", this);
        final HashMap allValuesForRow = new HashMap();
        final CVTableModelRow tableModelRow = this.getCVTableModelRow((int)rowIndex);
        final List row = tableModelRow.getRowContents();
        for (int i = 0; i < this.columnCount; ++i) {
            allValuesForRow.put(this.columns[i], row.get(i));
        }
        return allValuesForRow;
    }
    
    public CVTableModelRow getCVTableModelRow(final int index) {
        CVTableModelRow tableModelRow = null;
        if (this.viewSortEnabled) {
            tableModelRow = this.viewSortedData.get(index);
        }
        else {
            tableModelRow = this.tableData.get(index);
        }
        return tableModelRow;
    }
    
    @Override
    public DataObject getDataObjectForRow(final int[] rowIndex) throws DataAccessException {
        CVTableModelImpl.OUT.entering("CVTableModelImpl", "getDataObjectForRow", new Object[] { rowIndex });
        final DataObject dataObjForRow = (DataObject)new WritableDataObject();
        final HashMap tableVsAllRows = new HashMap();
        for (int i = 0; i < rowIndex.length; ++i) {
            final HashMap tableVsRows = new HashMap();
            Row row = null;
            final List rowContents = this.getCVTableModelRow(rowIndex[i]).getRowContents();
            CVTableModelImpl.OUT.log(Level.FINER, "getObjectForRow(int) rowContexts obtained for rowIndex {0} ->{1}", new Object[] { new Integer(rowIndex[i]), rowContents });
            String tableAlias = null;
            final List tableAliasesToRemove = new ArrayList();
            for (int j = 0; j < this.columnCount; ++j) {
                tableAlias = this.columns[j].getTableAlias();
                row = tableVsRows.get(tableAlias);
                final String tableName = this.tableAliasesToTableNames.getProperty(tableAlias);
                if (row == null) {
                    row = new Row(tableName, tableAlias);
                    tableVsRows.put(tableAlias, row);
                }
                final Object cellContent = rowContents.get(j);
                if (cellContent == null) {
                    for (int q = 0; q < this.keyColumnIndices.length; ++q) {
                        if (j == this.keyColumnIndices[q]) {
                            tableAliasesToRemove.add(tableAlias);
                        }
                    }
                }
                row.set(this.columns[j].getColumnName(), cellContent);
            }
            for (int s = 0; s < tableAliasesToRemove.size(); ++s) {
                tableVsRows.remove(tableAliasesToRemove.get(s));
            }
            final Enumeration keys = this.tableAliasesToTableNames.keys();
            while (keys.hasMoreElements()) {
                final String key = keys.nextElement();
                HashMap allRows = tableVsAllRows.get(key);
                final Row iRow = tableVsRows.get(key);
                if (iRow == null) {
                    continue;
                }
                if (allRows == null) {
                    allRows = new HashMap();
                    allRows.put(iRow.getPKValues(), "a");
                    dataObjForRow.addRow(iRow);
                    tableVsAllRows.put(key, allRows);
                }
                else {
                    final Object rowIden = iRow.getPKValues();
                    if (allRows.get(rowIden) != null) {
                        continue;
                    }
                    allRows.put(iRow.getPKValues(), "a");
                    dataObjForRow.addRow(iRow);
                }
            }
        }
        CVTableModelImpl.OUT.log(Level.FINER, "getObjectForRow(int[]) constructed the DO, going to return -> {0}", dataObjForRow);
        return dataObjForRow;
    }
    
    private void addRow(final CVTableModelRow cvtmr, final int rowIndex, final long startIndex, final long endIndex, final long total) {
        final List row = cvtmr.getRowContents();
        final List pk = cvtmr.getKey();
        CVTableModelImpl.OUT.log(Level.FINER, "adding ROW : {0}, PK: {1}, at {2}.\n SI : {3}, EI : {4}, T : {5}", new Object[] { row, pk, new Integer(rowIndex), new Long(startIndex), new Long(endIndex), new Long(total) });
        this.tableData.add(rowIndex, cvtmr);
        this.keys.add(rowIndex, pk);
        int affectedRow = rowIndex;
        if (this.viewSortEnabled) {
            affectedRow = this.addRowInViewSortedData(cvtmr);
        }
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.totalCount = total;
        final TableModelEvent tme = new TableModelEvent((TableModel)this, affectedRow, affectedRow, -1, 1);
        this.fireTableChanged(tme);
    }
    
    protected synchronized int addRowInViewSortedData(final CVTableModelRow row) {
        CVTableModelImpl.OUT.log(Level.FINER, " addRowInViewSortedData, row : {0}", row);
        int rowsCount;
        int insertIndex;
        for (rowsCount = this.viewSortedData.size(), insertIndex = 0; insertIndex < rowsCount; ++insertIndex) {
            final int compareVal = this.comparator.compare(row, this.viewSortedData.get(insertIndex));
            if (compareVal < 0) {
                break;
            }
        }
        CVTableModelImpl.OUT.log(Level.FINER, " insertIndex : {0}", new Integer(insertIndex));
        this.viewSortedData.add(insertIndex, row);
        return insertIndex;
    }
    
    private void deleteRow(final int rowIndex, final long startIndex, final long endIndex, final long total) {
        CVTableModelImpl.OUT.log(Level.FINER, "deleting ROW at {0}.\n SI : {1}, EI : {2}, T : {3}", new Object[] { new Integer(rowIndex), new Long(startIndex), new Long(endIndex), new Long(total) });
        final CVTableModelRow cvtmr = this.tableData.remove(rowIndex);
        final List removedRow = cvtmr.getRowContents();
        int affectedRow = rowIndex;
        if (this.viewSortEnabled) {
            CVTableModelImpl.OUT.log(Level.FINER, " removing {0} from viewSortedData", cvtmr);
            final int removedRowIndex = this.viewSortedData.indexOf(cvtmr);
            if (removedRowIndex > -1) {
                this.viewSortedData.remove(removedRowIndex);
                affectedRow = removedRowIndex;
            }
            else {
                CVTableModelImpl.OUT.log(Level.SEVERE, " Client sorting failed - Could not remove {0} from {1} during delete notification", new Object[] { cvtmr, this.viewSortedData });
            }
        }
        this.keys.remove(rowIndex);
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.totalCount = total;
        final TableModelEvent tme = new TableModelEvent((TableModel)this, affectedRow, affectedRow, -1, -1);
        this.fireTableChanged(tme);
    }
    
    private void updateRow(final CVTableModelRow cvtmr, final int rowIndex, final long startIndex, final long endIndex, final long total) {
        final List row = cvtmr.getRowContents();
        final List pk = cvtmr.getKey();
        final CVTableModelRow oldCvtmr = this.tableData.get(rowIndex);
        final List oldRow = oldCvtmr.getRowContents();
        CVTableModelImpl.OUT.log(Level.FINER, "updating OLDROW : {0} at {1} with ROW : {2}.\n SI : {3}, EI : {4}, T : {5}", new Object[] { oldRow, new Integer(rowIndex), row, new Long(startIndex), new Long(endIndex), new Long(total) });
        int startRow = rowIndex;
        int endRow = rowIndex;
        if (this.viewSortEnabled) {
            CVTableModelImpl.OUT.log(Level.FINER, " removing {0} from viewSortedData", oldCvtmr);
            final int rowIndexInViewSortedData = this.viewSortedData.indexOf(oldCvtmr);
            if (rowIndexInViewSortedData > -1) {
                final int compareVal = this.comparator.compare(oldCvtmr, cvtmr);
                if (compareVal == 0) {
                    CVTableModelImpl.OUT.log(Level.FINER, " client sorted columns' values HAS NOT CHANGED. New state of row is {0} ", cvtmr);
                    this.viewSortedData.set(rowIndexInViewSortedData, cvtmr);
                    startRow = rowIndexInViewSortedData;
                    endRow = rowIndexInViewSortedData;
                }
                else {
                    CVTableModelImpl.OUT.log(Level.FINER, " client sorted columns' values HAS CHANGED. New state of row is {0} ", cvtmr);
                    this.viewSortedData.remove(rowIndexInViewSortedData);
                    final int addedIndex = this.addRowInViewSortedData(cvtmr);
                    startRow = 0;
                    endRow = this.viewSortedData.size() - 1;
                }
            }
            else {
                CVTableModelImpl.OUT.log(Level.SEVERE, " Client sorting failed - Could not update {0} in {1} during update notification", new Object[] { oldCvtmr, this.viewSortedData });
            }
        }
        this.tableData.set(rowIndex, cvtmr);
        this.keys.add(rowIndex, pk);
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.totalCount = total;
        CVTableModelImpl.OUT.log(Level.FINER, " Firing TME with startRow : {0}, endRow : {1} ", new Object[] { new Integer(startRow), new Integer(endRow) });
        final TableModelEvent tme = new TableModelEvent((TableModel)this, startRow, endRow, -1, 0);
        this.fireTableChanged(tme);
    }
    
    private void updateIndices(final long startIndex, final long endIndex, final long total) {
        CVTableModelImpl.OUT.log(Level.FINER, "updating INDICES....\n SI : {0}, EI : {1}, T : {2}", new Object[] { new Long(startIndex), new Long(endIndex), new Long(total) });
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.totalCount = total;
        final TableModelEvent tme = new TableModelEvent((TableModel)this, 0, 0, -1, 0);
        this.fireTableChanged(tme);
    }
    
    public synchronized TableModelData getModelData() {
        return this.tableModelData;
    }
    
    public synchronized void sortModel(final com.adventnet.beans.xtable.SortColumn[] xTableSortColumns) {
        if (xTableSortColumns == null) {
            CVTableModelImpl.OUT.finer("Model SortColumns cannot be null");
            return;
        }
        this.sortColumns = this.getSortColumns(xTableSortColumns);
        this.xSortColumns = xTableSortColumns;
        this.refresh();
    }
    
    protected SortColumn[] getSortColumns(final com.adventnet.beans.xtable.SortColumn[] xTableSortColumns) {
        if (xTableSortColumns == null) {
            return null;
        }
        final int len = xTableSortColumns.length;
        final SortColumn[] newSortColumns = new SortColumn[len];
        for (int i = 0; i < len; ++i) {
            newSortColumns[i] = new SortColumn(this.columns[xTableSortColumns[i].getColumnIndex()], xTableSortColumns[i].isAscending());
            if (this.sortOrders != null) {
                final Vector sortOrder = this.sortOrders.get(this.columns[xTableSortColumns[i].getColumnIndex()]);
                if (sortOrder != null) {
                    newSortColumns[i].setSortOrder(sortOrder);
                }
            }
        }
        return newSortColumns;
    }
    
    public com.adventnet.beans.xtable.SortColumn[] getModelSortedColumns() {
        if (this.sortColumns != null && this.xSortColumns == null) {
            this.xSortColumns = this.getXSortColumns(this.sortColumns);
        }
        return this.xSortColumns;
    }
    
    private int findIndex(final Column column) {
        for (int i = 0; i < this.columns.length; ++i) {
            if (this.columns[i].equals((Object)column)) {
                return i;
            }
        }
        for (int i = 0; i < this.columns.length; ++i) {
            if (this.columns[i].getColumnName() == null || this.columns[i].getTableAlias() == null) {
                return -1;
            }
            if (this.columns[i].getTableAlias().equals(column.getTableAlias()) && this.columns[i].getColumnName().equals(column.getColumnName())) {
                return i;
            }
        }
        return -1;
    }
    
    public synchronized void sortView(final com.adventnet.beans.xtable.SortColumn[] xTableSortColumns) {
        CVTableModelImpl.OUT.log(Level.FINER, "Inside sortView : xTableSortColumns : {0}", xTableSortColumns);
        this.viewSortColumns = this.getSortColumns(xTableSortColumns);
        this.xViewSortColumns = xTableSortColumns;
        this.sortInternal();
    }
    
    public com.adventnet.beans.xtable.SortColumn[] getViewSortedColumns() {
        if (this.viewSortColumns != null && this.xViewSortColumns == null) {
            this.xViewSortColumns = this.getXSortColumns(this.viewSortColumns);
        }
        return this.xViewSortColumns;
    }
    
    protected synchronized void sortInternal() {
        CVTableModelImpl.OUT.finer("Inside sortInternal...");
        if (!(this.viewSortEnabled = (this.viewSortColumns != null))) {
            CVTableModelImpl.OUT.finer("sortInternal : viewSortEnabled is disabled");
            this.viewSortedData = null;
            return;
        }
        this.viewSortedData = (Vector)this.tableData.clone();
        CVTableModelImpl.OUT.log(Level.FINER, "sortInternal : before sorting, viewSortedData : {0}", this.viewSortedData);
        for (int len = this.xViewSortColumns.length, i = 0; i < len; ++i) {
            final int colIndex = this.xViewSortColumns[i].getColumnIndex();
            CVTableModelImpl.OUT.log(Level.FINER, " setting {0} with column type...{1}", new Object[] { this.viewSortColumns[i], new Integer(this.columns[colIndex].getType()) });
            this.viewSortColumns[i].getColumn().setType(this.columns[colIndex].getType());
        }
        this.comparator = new RowComparator(this.xViewSortColumns, this.viewSortColumns);
        Collections.sort((List<Object>)this.viewSortedData, this.comparator);
        CVTableModelImpl.OUT.log(Level.FINER, "sortInternal : after sorting, viewSortedData : {0}", this.viewSortedData);
    }
    
    @Override
    public String toString() {
        final StringBuffer buff = new StringBuffer();
        buff.append("<CVTABLEMODELIMPL instanceId=\"").append(this.instanceId);
        buff.append("\" currentBaseDataStateId=\"").append(this.currentBaseDataStateId).append("\" >");
        buff.append(this.tableModelData);
        if (this.viewSortColumns != null) {
            buff.append("\n<VIEWSORTEDDATA viewSortColumns=\"" + Arrays.asList(this.viewSortColumns) + "\"");
            buff.append("\n                sortOrders=\"" + this.sortOrders + "\" >");
            buff.append("\n\t<ROWS>");
            for (int len = this.viewSortedData.size(), i = 0; i < len; ++i) {
                buff.append("\n\t\t<ROW index=\"" + i + "\">");
                buff.append(this.viewSortedData.get(i));
                buff.append("</ROW>");
            }
            buff.append("\n\t</ROWS>");
            buff.append("\n</VIEWSORTEDDATA>");
        }
        else {
            buff.append("\n<VIEWSORTEDDATA viewSortColumns=\"null\" />");
        }
        buff.append("\n");
        buff.append(this.selectQuery);
        buff.append("\n</CVTABLEMODELIMPL>");
        return buff.toString();
    }
    
    public Class[] getColumnClasses() {
        return this.colClasses;
    }
    
    public synchronized void setColumnClasses(final Class[] colClasses) {
        this.colClasses = colClasses;
    }
    
    public Map getSortOrders() {
        return this.sortOrders;
    }
    
    public void setSortOrders(final Map v) {
        this.sortOrders = v;
        this.setSortOrderToSortColumns(this.sortColumns);
        if (this.sortColumns != null && this.xSortColumns == null) {
            this.xSortColumns = this.getXSortColumns(this.sortColumns);
        }
        this.setSortOrderToSortColumns(this.viewSortColumns);
        if (this.viewSortColumns != null && this.xViewSortColumns == null) {
            this.xViewSortColumns = this.getXSortColumns(this.viewSortColumns);
        }
        this.sortInternal();
    }
    
    public long getPageLength() {
        return this.fetchSize;
    }
    
    public void setPageLength(final long pageLength) {
        if (pageLength <= 0L) {
            CVTableModelImpl.OUT.log(Level.INFO, " Invalid PageLength : {0}", new Long(pageLength));
            return;
        }
        this.showRange(this.startIndex, this.startIndex + pageLength - 1L);
    }
    
    public synchronized void addNavigationListener(final NavigationListener nl) {
        CVTableModelImpl.OUT.log(Level.FINER, " CVTableModelImpl : adding NavigationListener : {0}", nl);
        if (!this.nListeners.contains(nl)) {
            this.nListeners.add(nl);
        }
        CVTableModelImpl.OUT.log(Level.FINER, " CVTableModelImpl : nListeners : {0}", this.nListeners);
    }
    
    public synchronized void removeNavigationListener(final NavigationListener nl) {
        CVTableModelImpl.OUT.log(Level.FINER, " CVTableModelImpl : removing NavigationListener : {0}", nl);
        this.nListeners.remove(nl);
        CVTableModelImpl.OUT.log(Level.FINER, " CVTableModelImpl : nListeners : {0}", this.nListeners);
    }
    
    public String getTableNameForAlias(final String tableName) {
        return this.tableAliasesToTableNames.getProperty(tableName);
    }
    
    public static synchronized void useSwingThread(final boolean useSwingThread) {
        CVTableModelImpl.useSwingThread = useSwingThread;
    }
    
    public synchronized void update(final TableModelNotification tmn) {
        final int currentBaseDataStateIdInTMN = tmn.getCurrentBaseDataStateId();
        if (this.currentBaseDataStateId != currentBaseDataStateIdInTMN) {
            CVTableModelImpl.OUT.log(Level.FINER, " CVTableModelImpl[{0}] : currentBaseDataStateId != currentBaseDataStateIdInTMN : {1} != {2} ", new Object[] { this.instanceIdObj, new Integer(this.currentBaseDataStateId), new Integer(currentBaseDataStateIdInTMN) });
            return;
        }
        if (CVTableModelImpl.useSwingThread) {
            this.queue.add(tmn);
            SwingUtilities.invokeLater(this.sfq);
        }
        else {
            this.process(tmn);
        }
    }
    
    private synchronized void process(final TableModelNotification tmn) {
        final int currentBaseDataStateIdInTMN = tmn.getCurrentBaseDataStateId();
        if (this.currentBaseDataStateId != currentBaseDataStateIdInTMN) {
            CVTableModelImpl.OUT.log(Level.FINER, " CVTableModelImpl[{0}] : currentBaseDataStateId != currentBaseDataStateIdInTMN : {1} != {2} ", new Object[] { this.instanceIdObj, new Integer(this.currentBaseDataStateId), new Integer(currentBaseDataStateIdInTMN) });
            return;
        }
        final List tableRowActions = tmn.getTableRowActions();
        CVTableModelImpl.OUT.log(Level.FINER, " CVTableModelImpl[{0}] : tableRowActions : {1}", new Object[] { this.instanceIdObj, tableRowActions });
        for (int raSize = tableRowActions.size(), j = 0; j < raSize; ++j) {
            final TableRowAction rowAction = tableRowActions.get(j);
            final int actionType = rowAction.getType();
            final long startIndex = rowAction.getStartIndex();
            final long endIndex = rowAction.getEndIndex();
            final long total = rowAction.getTotal();
            final CVTableModelRow row = rowAction.getRow();
            final int rowIndex = rowAction.getRowIndex();
            CVTableModelImpl.OUT.log(Level.FINER, " CVTableModelImpl[{0}] : rowAction : {1}", new Object[] { this.instanceIdObj, rowAction });
            switch (actionType) {
                case 0: {
                    CVTableModelImpl.OUT.log(Level.FINER, " CVTableModelImpl[{0}] : INSERT : invoking addRow()..", this.instanceIdObj);
                    this.addRow(row, rowIndex, startIndex, endIndex, total);
                    break;
                }
                case 2: {
                    CVTableModelImpl.OUT.log(Level.FINER, " CVTableModelImpl[{0}] : DELETE : invoking deleteRow()..", this.instanceIdObj);
                    this.deleteRow(rowIndex, startIndex, endIndex, total);
                    break;
                }
                case 1: {
                    CVTableModelImpl.OUT.log(Level.FINER, " CVTableModelImpl[{0}] : UPDATE : invoking updateRow()..", this.instanceIdObj);
                    this.updateRow(row, rowIndex, startIndex, endIndex, total);
                    break;
                }
                case 4: {
                    CVTableModelImpl.OUT.log(Level.FINER, " CVTableModelImpl[{0}] : INDICES_CHANGE : invoking updateIndices()..", this.instanceIdObj);
                    this.updateIndices(startIndex, endIndex, total);
                    break;
                }
                case 3: {
                    CVTableModelImpl.OUT.log(Level.FINER, " CVTableModelImpl[{0}] : REFRESH", this.instanceIdObj);
                    CVTableModelImpl.OUT.log(Level.FINER, " CVTableModelImpl[{0}] : INDICES_CHANGE : invoking updateIndices()..", this.instanceIdObj);
                    this.updateIndices(startIndex, endIndex, total);
                    CVTableModelImpl.OUT.log(Level.FINER, " CVTableModelImpl[{0}] : INDICES_CHANGE : invoking refresh()..", this.instanceIdObj);
                    this.refresh();
                    break;
                }
                default: {
                    CVTableModelImpl.OUT.log(Level.INFO, "Unknown type received in updates, TableRowAction : {0}", rowAction);
                    break;
                }
            }
        }
    }
    
    public void setCarryoverCountOnRefresh(final boolean flag) {
        this.carryOverCount = flag;
    }
    
    public boolean canCarryoverCountOnRefresh() {
        return this.carryOverCount;
    }
    
    static {
        CLASS_NAME = CVTableModelImpl.class.getName();
        CVTableModelImpl.OUT = Logger.getLogger(CVTableModelImpl.CLASS_NAME);
        CVTableModelImpl.instanceIdCounter = 0;
        CVTableModelImpl.lockObj = new byte[0];
        CVTableModelImpl.useSwingThread = true;
    }
    
    private class SendFromQueue implements Runnable
    {
        @Override
        public void run() {
            synchronized (CVTableModelImpl.this) {
                if (CVTableModelImpl.this.queue.size() > 0) {
                    final TableModelNotification tmn = CVTableModelImpl.this.queue.remove(0);
                    CVTableModelImpl.this.process(tmn);
                }
            }
        }
    }
}
