package com.adventnet.persistence;

import com.adventnet.persistence.internal.UniqueValueHolder;
import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;
import com.zoho.conf.AppResources;
import java.util.Comparator;
import java.util.Collections;
import com.adventnet.ds.query.SelectQuery;
import java.util.Map;
import com.adventnet.ds.query.Query;
import com.adventnet.ds.query.util.QueryUtil;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import java.util.HashMap;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.ds.query.Column;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import java.util.Collection;
import java.util.ArrayList;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.Criteria;
import java.util.List;
import java.util.logging.Logger;
import java.util.Iterator;

public class CriteriaBasedRowIterator implements Iterator
{
    private static final String CLASS_NAME;
    private static final Logger OUT;
    private DataObject dataObject;
    private List<Row> projectedTableRows;
    private Criteria criteria;
    private Join join;
    private ITERATOR_FOR iteratorFor;
    private Iterator<Row> iterator;
    private List criteriaTableRows;
    private Iterator<Row> projectedTableRowsIterator;
    private transient int expectedModCount;
    private String projectedTableName;
    private String criteriaTableName;
    private Row currentRow;
    private boolean findNext;
    private ColumnBasedComparator<Row> comparator;
    private SortedRows<Row> matchedCriteriaRows;
    
    CriteriaBasedRowIterator(final ITERATOR_FOR iteratorFor, final List criteriaTableRows, final Criteria criteria, final DataObject dataObject) throws DataAccessException {
        this.dataObject = null;
        this.projectedTableRows = null;
        this.criteria = null;
        this.join = null;
        this.iteratorFor = null;
        this.iterator = null;
        this.criteriaTableRows = null;
        this.projectedTableRowsIterator = null;
        this.projectedTableName = null;
        this.criteriaTableName = null;
        this.currentRow = null;
        this.findNext = true;
        this.comparator = new ColumnBasedComparator<Row>();
        this.matchedCriteriaRows = null;
        try {
            this.dataObject = dataObject;
            this.criteria = criteria;
            this.expectedModCount = ((WritableDataObject)dataObject).modCount;
            if (criteria != null) {
                this.criteria.validateInput();
            }
            this.iteratorFor = iteratorFor;
            this.iterator = new ArrayList<Row>(criteriaTableRows).iterator();
        }
        catch (final Exception e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }
    
    CriteriaBasedRowIterator(final ITERATOR_FOR iteratorFor, final String projectedTableName, final List projectedTableRows, final Criteria criteria, final List criteriaTableRows, final DataObject dataObject, final Join join) throws DataAccessException {
        this.dataObject = null;
        this.projectedTableRows = null;
        this.criteria = null;
        this.join = null;
        this.iteratorFor = null;
        this.iterator = null;
        this.criteriaTableRows = null;
        this.projectedTableRowsIterator = null;
        this.projectedTableName = null;
        this.criteriaTableName = null;
        this.currentRow = null;
        this.findNext = true;
        this.comparator = new ColumnBasedComparator<Row>();
        this.matchedCriteriaRows = null;
        try {
            this.iteratorFor = iteratorFor;
            this.join = join;
            this.dataObject = dataObject;
            this.projectedTableName = projectedTableName;
            this.criteria = criteria;
            this.expectedModCount = ((WritableDataObject)dataObject).modCount;
            if (criteria != null) {
                this.criteria.validateInput();
            }
            this.criteriaTableName = getTableName(criteria, dataObject);
            if (join != null && join.getCriteria() != null) {
                try {
                    if (this.criteriaTableName.equals(this.projectedTableName)) {
                        this.projectedTableRows = this.getMatchingRows(this.criteriaTableName, criteria, criteriaTableRows);
                    }
                    else {
                        final List retCriteriaList = this.getMatchingRows(this.criteriaTableName, criteria, criteriaTableRows);
                        this.projectedTableRows = new SortedRows<Row>((ColumnBasedComparator)null);
                        for (int retListSize = retCriteriaList.size(), i = 0; i < retListSize; ++i) {
                            final Row condn = retCriteriaList.get(i);
                            final Iterator itrRow = dataObject.getRows(projectedTableName, condn, join);
                            while (itrRow.hasNext()) {
                                final Row nextRow = itrRow.next();
                                if (!this.projectedTableRows.contains(nextRow)) {
                                    this.projectedTableRows.add(nextRow);
                                }
                            }
                        }
                    }
                    this.projectedTableRowsIterator = this.projectedTableRows.iterator();
                    return;
                }
                catch (final Exception e) {
                    throw new DataAccessException(e.getMessage(), e);
                }
            }
            if (iteratorFor == ITERATOR_FOR.MULTIPLE_ROWS) {
                this.comparator.iterableTableColumns = this.getJoinColumnIndices(projectedTableName);
                this.projectedTableRows = new ArrayList<Row>(projectedTableRows);
                this.criteriaTableRows = new ArrayList(criteriaTableRows);
            }
            else {
                (this.projectedTableRows = new SortedRows<Row>((ColumnBasedComparator)null)).addAll(projectedTableRows);
                this.iterator = criteriaTableRows.iterator();
            }
        }
        catch (final Exception e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }
    
    static String getTableName(final Criteria cr, final DataObject data) throws DataAccessException {
        String tabName = null;
        if (cr.getLeftCriteria() != null && cr.getRightCriteria() != null) {
            tabName = getTableName(cr.getLeftCriteria(), data);
            final String rightTabName = getTableName(cr.getRightCriteria(), data);
            if (!tabName.equals(rightTabName)) {
                throw new DataAccessException("The Criteria specified has conditions from different tables including " + tabName + " and " + rightTabName);
            }
        }
        else {
            final Column col = cr.getColumn();
            if (col == null) {
                throw new DataAccessException("Column is null in criteria : " + cr);
            }
            tabName = getTableName(col);
            final String origTabName = ((WritableDataObject)data).getOrigTableName(tabName);
            final TableDefinition td = getTableDefinition(origTabName);
            if (td != null) {
                ColumnDefinition cd = null;
                if (col.getColumnName() != null) {
                    cd = td.getColumnDefinitionByName(col.getColumnName());
                    if (cd == null) {
                        throw new DataAccessException("Unknown column [" + col.getColumnName() + "] in the table :: [" + td.getTableName() + "]");
                    }
                    col.setColumnIndex(cd.index());
                }
                else if (col.getColumnIndex() > 0) {
                    cd = td.getColumnList().get(col.getColumnIndex() - 1);
                }
                if (cd == null) {
                    throw new DataAccessException("Unkown column [" + col + "] specified in the criteria [" + cr + "]");
                }
                col.setType(cd.getSQLType());
                col.setDataType(cd.getDataType());
                final Object value = cr.getValue();
                if (value instanceof Column) {
                    final Column v = (Column)value;
                    if (v.getColumnName() != null) {
                        cd = td.getColumnDefinitionByName(v.getColumnName());
                        v.setColumnIndex(cd.index());
                    }
                    else if (v.getColumnIndex() > 0) {
                        cd = td.getColumnList().get(v.getColumnIndex() - 1);
                    }
                    if (cd == null) {
                        throw new DataAccessException("Unkown column [" + v + "] specified in the value part of the criteria [" + cr + "]");
                    }
                    v.setType(cd.getSQLType());
                }
            }
        }
        return tabName;
    }
    
    private static String getTableName(Column column) {
        String tabName = null;
        while (column.getColumn() != null) {
            column = column.getColumn();
        }
        tabName = column.getTableAlias();
        return tabName;
    }
    
    private static TableDefinition getTableDefinition(final String tableName) {
        try {
            return MetaDataUtil.getTableDefinitionByName(tableName);
        }
        catch (final MetaDataException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    private SortedRows<Row> getMatchingRows(final String tableName, final Criteria criteria, final List valueList) throws DataAccessException {
        try {
            Row initRow = null;
            final Map compMap = new HashMap();
            List listarray = new ArrayList();
            final SortedRows<Row> retRow = new SortedRows<Row>((ColumnBasedComparator)null);
            String origTableName = ((WritableDataObject)this.dataObject).getOrigTableName(tableName);
            origTableName = ((origTableName == null) ? tableName : origTableName);
            final SelectQuery query = new SelectQueryImpl(Table.getTable(origTableName, tableName));
            final Criteria newCriteria = (criteria != null) ? ((Criteria)criteria.clone()) : null;
            query.setCriteria(newCriteria);
            final Column[] columns = this.createColumns(origTableName, tableName);
            QueryUtil.setDataType(query);
            for (int size = valueList.size(), i = 0; i < size; ++i) {
                initRow = valueList.get(i);
                if (initRow.getTableName().equals(tableName)) {
                    listarray = initRow.getColumns();
                    for (int j = 0; j < columns.length; ++j) {
                        final Column column = columns[j];
                        compMap.put(column, initRow.get(column.getColumnName()));
                    }
                    if (newCriteria == null || newCriteria.matches(compMap)) {
                        retRow.add(initRow);
                    }
                }
            }
            return retRow;
        }
        catch (final Exception qce) {
            throw new DataAccessException(qce.getMessage(), qce);
        }
    }
    
    private Column[] createColumns(final String origTableName, final String tableName) throws MetaDataException {
        final TableDefinition td = MetaDataUtil.getTableDefinitionByName(origTableName);
        final List<String> columnList = td.getColumnNames();
        final int columnSize = columnList.size();
        final Column[] columns = new Column[columnSize];
        for (int j = 0; j < columnSize; ++j) {
            final Column column = new Column(tableName, j + 1);
            column.setColumnName(columnList.get(j));
            columns[j] = column;
        }
        return columns;
    }
    
    private Row getNextRow() {
        Row row = null;
        if (this.join == null) {
            while (this.iterator.hasNext()) {
                row = this.iterator.next();
                if (this.criteria == null || this.criteria.matches(row)) {
                    return row;
                }
            }
        }
        else {
            if (this.join.getCriteria() != null) {
                return this.projectedTableRowsIterator.hasNext() ? this.projectedTableRowsIterator.next() : null;
            }
            Row criteriaRow = null;
            if (this.iteratorFor != ITERATOR_FOR.SINGLE_ROW) {
                final Iterator iterator = this.getIterator();
                while (iterator.hasNext()) {
                    row = iterator.next();
                    if (Collections.binarySearch(this.matchedCriteriaRows, row, this.comparator) >= 0) {
                        return row;
                    }
                }
                return null;
            }
            while (this.iterator.hasNext()) {
                criteriaRow = this.iterator.next();
                if (this.criteria.matches(criteriaRow)) {
                    final Criteria criteriaOnProjectedTable = this.getCriteriaForProjectedTable(criteriaRow);
                    this.projectedTableRowsIterator = this.projectedTableRows.iterator();
                    while (this.projectedTableRowsIterator.hasNext()) {
                        row = this.projectedTableRowsIterator.next();
                        if (criteriaOnProjectedTable.matches(row)) {
                            return row;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    void checkForConcurrentMod() {
        if (AppResources.getString("do.iterator.exception.type", "").equalsIgnoreCase("concurrent") && this.expectedModCount != ((WritableDataObject)this.dataObject).modCount) {
            throw new ConcurrentModificationException("Modifying data object during iteration is illegal/not supported.");
        }
    }
    
    private Iterator<Row> getIterator() {
        if (this.iterator == null) {
            this.fillMatchedCriteriaRows();
            this.iterator = this.projectedTableRows.iterator();
            this.comparator.iterableTableColumns = this.getJoinColumnIndices(this.projectedTableName);
            this.comparator.searchableTableColumns = this.getJoinColumnIndices(this.criteriaTableName);
        }
        return this.iterator;
    }
    
    @Override
    public boolean hasNext() {
        if (this.findNext) {
            this.currentRow = this.getNextRow();
            this.findNext = false;
        }
        return this.currentRow != null;
    }
    
    @Override
    public Object next() {
        this.checkForConcurrentMod();
        if (this.findNext) {
            this.currentRow = this.getNextRow();
        }
        if (this.currentRow == null) {
            throw new NoSuchElementException("All (matching) rows are exhausted and no more rows to return");
        }
        this.findNext = true;
        return this.currentRow;
    }
    
    @Override
    public void remove() {
        if (this.currentRow == null) {
            throw new IllegalAccessError("This method [iterator.remove] can be called only once per call to next().");
        }
        this.checkForConcurrentMod();
        try {
            this.dataObject.deleteRow(this.currentRow);
            this.expectedModCount = ((WritableDataObject)this.dataObject).modCount;
            this.currentRow = null;
            if (this.join != null && this.join.getCriteria() != null && this.projectedTableRowsIterator != null) {
                this.projectedTableRowsIterator.remove();
            }
            else {
                this.iterator.remove();
            }
        }
        catch (final DataAccessException excp) {
            final String message = "Exception occured while trying to remove Row from iterator";
            throw new RuntimeException(message, excp);
        }
    }
    
    public void removeIgnoreFK() {
        if (this.currentRow == null) {
            throw new IllegalAccessError("This method [iterator.remove] can be called only once per call to next().");
        }
        this.checkForConcurrentMod();
        try {
            this.dataObject.deleteRowIgnoreFK(this.currentRow);
            this.expectedModCount = ((WritableDataObject)this.dataObject).modCount;
            this.currentRow = null;
            if (this.join != null && this.join.getCriteria() != null && this.projectedTableRowsIterator != null) {
                this.projectedTableRowsIterator.remove();
            }
            else {
                this.iterator.remove();
            }
        }
        catch (final DataAccessException excp) {
            final String message = "Exception occured while trying to remove Row from iterator";
            throw new RuntimeException(message, excp);
        }
    }
    
    private SortedRows<Row> fillMatchedCriteriaRows() {
        if (this.matchedCriteriaRows == null) {
            Row row = null;
            this.comparator.iterableTableColumns = this.getJoinColumnIndices(this.criteriaTableName);
            this.matchedCriteriaRows = new SortedRows<Row>(this.comparator);
            this.iterator = this.criteriaTableRows.iterator();
            while (this.iterator.hasNext()) {
                row = this.iterator.next();
                if (this.criteria.matches(row)) {
                    this.matchedCriteriaRows.add(row);
                }
            }
            this.iterator = null;
        }
        return this.matchedCriteriaRows;
    }
    
    private Criteria getCriteriaForProjectedTable(final Row criteriaRow) {
        Criteria criteria = null;
        String[] projTableColNames = null;
        String[] criTableColNames = null;
        String baseTableName = this.join.getBaseTableName();
        if (baseTableName == null) {
            baseTableName = this.join.getBaseTable().getTableName();
        }
        if (baseTableName.equals(this.projectedTableName)) {
            projTableColNames = this.join.getBaseTableColumns();
            criTableColNames = this.join.getReferencedTableColumns();
        }
        else {
            projTableColNames = this.join.getReferencedTableColumns();
            criTableColNames = this.join.getBaseTableColumns();
        }
        final TableDefinition td = getTableDefinition(this.projectedTableName);
        for (int index = 0; index < projTableColNames.length; ++index) {
            final Column col = Column.getColumn(this.projectedTableName, projTableColNames[index]);
            if (td != null && col.getType() == 1111) {
                col.setDefinition(td.getColumnList().get(index));
            }
            if (index == 0) {
                criteria = new Criteria(col, criteriaRow.get(criTableColNames[index]), 0);
            }
            else {
                criteria = criteria.and(col, criteriaRow.get(criTableColNames[index]), 0);
            }
        }
        return criteria;
    }
    
    private int[] getJoinColumnIndices(final String tableName) {
        final String origTableName = ((WritableDataObject)this.dataObject).getOrigTableName(tableName);
        TableDefinition td = null;
        try {
            td = MetaDataUtil.getTableDefinitionByName(origTableName);
        }
        catch (final MetaDataException e) {
            e.printStackTrace();
        }
        final String[] cols = this.getJoinColumns(tableName);
        final int[] indices = new int[cols.length];
        for (int index = 0; index < cols.length; ++index) {
            indices[index] = td.getColumnIndex(cols[index]);
        }
        return indices;
    }
    
    private String[] getJoinColumns(final String tableName) {
        return (tableName.equals(this.join.getBaseTableName()) || (this.join.getBaseTable() != null && this.join.getBaseTable().getTableName().equals(tableName))) ? this.join.getBaseTableColumns() : this.join.getReferencedTableColumns();
    }
    
    static {
        CLASS_NAME = CriteriaBasedRowIterator.class.getName();
        OUT = Logger.getLogger(CriteriaBasedRowIterator.CLASS_NAME);
    }
    
    enum ITERATOR_FOR
    {
        SINGLE_ROW, 
        MULTIPLE_ROWS;
    }
    
    private static class ColumnBasedComparator<Row> implements Comparator<com.adventnet.persistence.Row>
    {
        public int[] iterableTableColumns;
        public int[] searchableTableColumns;
        
        private ColumnBasedComparator() {
            this.iterableTableColumns = null;
            this.searchableTableColumns = null;
        }
        
        @Override
        public int compare(final com.adventnet.persistence.Row searchableRow, final com.adventnet.persistence.Row iterableRow) {
            for (int index = 0; index < this.iterableTableColumns.length; ++index) {
                final Comparable iterableRowValue = (Comparable)iterableRow.get(this.iterableTableColumns[index]);
                final Comparable searchableRowValue = (Comparable)searchableRow.get((this.searchableTableColumns == null) ? this.iterableTableColumns[index] : this.searchableTableColumns[index]);
                final int compareValue = (iterableRowValue instanceof UniqueValueHolder) ? iterableRowValue.compareTo(searchableRowValue) : searchableRowValue.compareTo(iterableRowValue);
                if (compareValue != 0) {
                    return compareValue;
                }
            }
            return 0;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("<ColumnBasedComparator iterableTableColumns=[");
            this.toString(this.iterableTableColumns, sb);
            sb.append("] searchableTableColumns=[");
            this.toString(this.searchableTableColumns, sb);
            sb.append("]/>");
            return sb.toString();
        }
        
        private void toString(final int[] a, final StringBuilder sb) {
            for (int index = 0; index < a.length; ++index) {
                if (index > 0) {
                    sb.append(", ");
                }
                sb.append(a[index]);
            }
        }
    }
    
    private static class SortedRows<Row> extends ArrayList<Row>
    {
        Comparator c;
        
        public SortedRows(final ColumnBasedComparator c) {
            this.c = null;
            this.c = c;
        }
        
        @Override
        public boolean add(final Row row) {
            if (this.c == null) {
                return super.add(row);
            }
            int index = Collections.binarySearch((List<? extends Row>)this, row, this.c);
            if (index >= 0) {
                return false;
            }
            index = Math.abs(index) - 1;
            super.add(index, row);
            return true;
        }
    }
}
