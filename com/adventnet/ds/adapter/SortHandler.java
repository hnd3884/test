package com.adventnet.ds.adapter;

import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Level;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SortColumn;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.List;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.DataSet;

public class SortHandler
{
    private DataSet[] dataSets;
    private SelectQuery query;
    private static SelectQuery modified;
    private List unProcessedList;
    int[] sortIndexes;
    boolean[] sortOrders;
    private static final Logger OUT;
    
    public SortHandler(final DataSet[] dataSets, final SelectQuery query) throws DataSourceException {
        this.dataSets = null;
        this.query = null;
        this.unProcessedList = new ArrayList();
        this.sortIndexes = null;
        this.sortOrders = null;
        this.dataSets = dataSets;
        this.query = query;
        if (SortHandler.modified == null) {
            SortHandler.modified = query;
        }
        final List selectCols = SortHandler.modified.getSelectColumns();
        final List<SortColumn> sortCols = SortHandler.modified.getSortColumns();
        this.fillSortIndexes(selectCols, sortCols);
    }
    
    public static void doPreProcessing(final SelectQuery modifiable) throws DataSourceException {
        SortHandler.modified = modifiable;
        final List<SortColumn> sortCols = SortHandler.modified.getSortColumns();
        if (sortCols.size() != 0) {
            addSortColAsSelectCol();
        }
    }
    
    private static void addSortColumns() {
        final List selectCols = SortHandler.modified.getSelectColumns();
        for (int selSize = selectCols.size(), i = 0; i < selSize; ++i) {
            final Column col = selectCols.get(i);
            if (col.getColumn() == null) {
                SortHandler.modified.addSortColumn(new SortColumn(col, true));
            }
            else if (col.getFunction() == 1) {
                SortHandler.modified.addSortColumn(new SortColumn(col.getColumn(), true));
            }
        }
    }
    
    private static void addSortColAsSelectCol() {
        final List<SortColumn> sortCols = SortHandler.modified.getSortColumns();
        final List selectCols = SortHandler.modified.getSelectColumns();
        for (int sortSize = sortCols.size(), i = 0; i < sortSize; ++i) {
            final SortColumn sortCol = sortCols.get(i);
            final Column col = sortCol.getColumn();
            if (!selectCols.contains(col)) {
                SortHandler.modified.addSelectColumn(col);
            }
        }
    }
    
    public List getNextRows() throws DataSourceException {
        try {
            for (int i = 0; i < this.dataSets.length; ++i) {
                final DataSet ds = this.dataSets[i];
                SortHandler.OUT.log(Level.FINE, "DataSets : " + ds);
            }
            final List toBeProcessed = new ArrayList();
            DataSet mainDataSet = this.dataSets[0];
            final int dsSize = this.dataSets.length;
            this.callNext();
            if (!mainDataSet.isClosed()) {
                toBeProcessed.add(mainDataSet);
            }
            for (int j = 1; j < dsSize; ++j) {
                final DataSet dsSet = this.dataSets[j];
                if (!dsSet.isClosed()) {
                    final int compared = this.compareDataSets(mainDataSet, dsSet);
                    if (compared == 0) {
                        toBeProcessed.add(dsSet);
                        mainDataSet = dsSet;
                    }
                    else if (compared < 0) {
                        this.unProcessedList.add(dsSet);
                    }
                    else if (compared > 0) {
                        this.unProcessedList.addAll(toBeProcessed);
                        toBeProcessed.clear();
                        toBeProcessed.add(dsSet);
                        mainDataSet = dsSet;
                    }
                }
            }
            return this.formRowsList(toBeProcessed);
        }
        catch (final SQLException excp) {
            throw new DataSourceException(excp.getMessage());
        }
    }
    
    private List processDataSets() throws SQLException {
        final List rows = new ArrayList();
        for (int dsLength = this.dataSets.length, i = 0; i < dsLength; ++i) {
            final DataSet ds = this.dataSets[i];
            final int cnt = ds.getColumnCount();
            if (ds.next()) {
                final List row = new ArrayList();
                for (int j = 0; j < cnt; ++j) {
                    row.add(ds.getValue(j + 1));
                }
                rows.add(row);
            }
        }
        return rows;
    }
    
    private List formRowsList(final List dataSets) throws SQLException {
        List rowsList = new ArrayList();
        final List selectCols = this.query.getSelectColumns();
        final int selSize = selectCols.size();
        final int dsSize = dataSets.size();
        SortHandler.OUT.logp(Level.FINE, SortHandler.class.getName(), "formRowsList", "SelectedColumns : {0} , DataSets : {1}", new Object[] { selectCols, dataSets });
        for (int i = 0; i < dsSize; ++i) {
            final DataSet current = dataSets.get(i);
            if (current != null) {
                final List row = new ArrayList();
                for (int j = 0; j < selSize; ++j) {
                    final Object value = current.getValue(j + 1);
                    row.add(value);
                }
                rowsList.add(row);
            }
        }
        SortHandler.OUT.logp(Level.FINE, SortHandler.class.getName(), "formRowsList", "rowsList : {0}", new Object[] { rowsList });
        if (rowsList.size() == 0) {
            rowsList = null;
        }
        return rowsList;
    }
    
    private void callNext() throws DataSourceException {
        try {
            for (int dsSize = this.dataSets.length, i = 0; i < dsSize; ++i) {
                final DataSet ds = this.dataSets[i];
                if (ds != null) {
                    if (!this.unProcessedList.contains(ds)) {
                        if (!ds.isClosed() && !ds.next()) {
                            ds.close();
                        }
                    }
                    else {
                        this.unProcessedList.remove(ds);
                    }
                }
            }
        }
        catch (final SQLException excp) {
            throw new DataSourceException(excp.getMessage());
        }
    }
    
    private int compareDataSets(final DataSet ds1, final DataSet ds2) throws SQLException {
        if (ds1.isClosed()) {
            return 1;
        }
        for (int sortLength = this.sortIndexes.length, i = 0; i < sortLength; ++i) {
            int currIdx = this.sortIndexes[i];
            currIdx = ((currIdx == 0) ? 1 : currIdx);
            final Object value1 = ds1.getValue(currIdx);
            final Object value2 = ds2.getValue(currIdx);
            int compared = 0;
            if (value1 != null && value2 != null) {
                compared = ((Comparable)value1).compareTo(value2);
            }
            else if (value1 == null) {
                compared = -1;
            }
            else {
                compared = 1;
            }
            if (compared != 0) {
                if (compared < 0) {
                    if (this.sortOrders[i]) {
                        return compared;
                    }
                    return -compared;
                }
                else if (compared > 0) {
                    if (this.sortOrders[i]) {
                        return compared;
                    }
                    return -compared;
                }
            }
        }
        return 0;
    }
    
    private void fillSortIndexes(final List selectCols, final List sortCols) {
        boolean orderSet = false;
        this.sortIndexes = new int[sortCols.size()];
        this.sortOrders = new boolean[sortCols.size()];
        final int selSize = selectCols.size();
        int currIndex = 0;
        for (int i = 0; i < selSize; ++i) {
            final Column col = selectCols.get(i);
            final SortColumn sortCol = this.getRelatedSortCol(col, sortCols);
            if (sortCol != null) {
                orderSet = true;
                this.sortIndexes[currIndex] = i + 1;
                this.sortOrders[currIndex] = sortCol.isAscending();
                ++currIndex;
            }
        }
        if (!orderSet && this.sortOrders.length > 0) {
            this.sortOrders[0] = true;
        }
    }
    
    private SortColumn getRelatedSortCol(final Column col, final List sortCols) {
        SortColumn retSortCol = null;
        for (int sortSize = sortCols.size(), i = 0; i < sortSize; ++i) {
            final SortColumn sortCol = sortCols.get(i);
            final Column retCol = sortCol.getColumn();
            if (col.equals(retCol)) {
                retSortCol = sortCol;
                break;
            }
        }
        return retSortCol;
    }
    
    static {
        SortHandler.modified = null;
        OUT = Logger.getLogger(SortHandler.class.getName());
    }
}
