package com.adventnet.ds.query;

import com.adventnet.ds.query.util.QueryUtil;
import java.util.Iterator;
import java.util.Collections;
import com.zoho.conf.AppResources;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class SelectQueryImpl implements SelectQuery
{
    private String sql;
    private boolean changed;
    private ArrayList<Column> selectColumns;
    private transient ArrayList removeColumns;
    private Criteria criteria;
    private Range range;
    private ArrayList<SortColumn> sortColumns;
    private ArrayList groupByColumns;
    private GroupByClause groupByClause;
    private ArrayList<Table> tableList;
    private ArrayList<Join> joins;
    private boolean lockState;
    private boolean isDistinct;
    private int derivedObjectsCount;
    private List<DerivedTable> derivedTables;
    private List<DerivedColumn> derivedColumns;
    private SelectQuery parentQuery;
    private boolean isParallel;
    private int parallelWorkers;
    private transient boolean isCached;
    protected Map<Table, List<IndexHintClause>> indexHintMap;
    private boolean isSubQuery;
    private int hashCode;
    
    protected SelectQueryImpl() {
        this.sql = null;
        this.changed = true;
        this.selectColumns = new ArrayList<Column>();
        this.removeColumns = null;
        this.sortColumns = new ArrayList<SortColumn>();
        this.groupByColumns = new ArrayList();
        this.tableList = new ArrayList<Table>();
        this.joins = new ArrayList<Join>();
        this.isDistinct = false;
        this.derivedObjectsCount = 0;
        this.derivedTables = new ArrayList<DerivedTable>(1);
        this.derivedColumns = new ArrayList<DerivedColumn>(1);
        this.parentQuery = null;
        this.isParallel = false;
        this.parallelWorkers = -1;
        this.isCached = false;
        this.indexHintMap = null;
        this.isSubQuery = false;
        this.hashCode = -1;
    }
    
    public SelectQueryImpl(final Table table) {
        this.sql = null;
        this.changed = true;
        this.selectColumns = new ArrayList<Column>();
        this.removeColumns = null;
        this.sortColumns = new ArrayList<SortColumn>();
        this.groupByColumns = new ArrayList();
        this.tableList = new ArrayList<Table>();
        this.joins = new ArrayList<Join>();
        this.isDistinct = false;
        this.derivedObjectsCount = 0;
        this.derivedTables = new ArrayList<DerivedTable>(1);
        this.derivedColumns = new ArrayList<DerivedColumn>(1);
        this.parentQuery = null;
        this.isParallel = false;
        this.parallelWorkers = -1;
        this.isCached = false;
        this.indexHintMap = null;
        this.isSubQuery = false;
        this.hashCode = -1;
        if (table instanceof DerivedTable) {
            ++this.derivedObjectsCount;
            this.derivedTables.add((DerivedTable)table);
        }
        this.tableList.add(table);
    }
    
    private void validateIndexHint(final Table table, final IndexHintClause idxHint) {
        final Clause forClause = idxHint.getIndexHintFor();
        if (table instanceof DerivedTable) {
            throw new IllegalArgumentException("Index Hint can be given only for Table instance not for DerviedTable.");
        }
        if (forClause != null && forClause != Clause.JOIN && forClause != Clause.GROUPBY && forClause != Clause.ORDERBY) {
            throw new IllegalArgumentException("Index Hint can have JOIN or GROUPBY or ORDERBY as for clause.");
        }
        final List<IndexHintClause> idxHintList = this.indexHintMap.get(table);
        if (idxHintList != null) {
            for (int i = 0; i < idxHintList.size(); ++i) {
                if (idxHint.equals(idxHintList.get(i))) {
                    throw new IllegalArgumentException("Already an IndexHint Clause has been added for the table : " + table.getTableName() + " index hint:: " + idxHint);
                }
            }
        }
    }
    
    @Override
    public void addIndexHint(final Table table, final IndexHintClause idxHint) {
        if (idxHint != null) {
            if (this.indexHintMap == null) {
                this.indexHintMap = new HashMap<Table, List<IndexHintClause>>();
            }
            this.validateIndexHint(table, idxHint);
            List<IndexHintClause> idxHintList = this.indexHintMap.get(table);
            if (idxHintList == null) {
                idxHintList = new ArrayList<IndexHintClause>();
                this.indexHintMap.put(table, idxHintList);
            }
            idxHintList.add(idxHint);
        }
    }
    
    @Override
    public void addIndexHint(final Table table, final List<IndexHintClause> idxHint) {
        if (idxHint != null) {
            for (int i = 0; i < idxHint.size(); ++i) {
                this.addIndexHint(table, idxHint.get(i));
            }
        }
    }
    
    @Override
    public Map<Table, List<IndexHintClause>> getIndexHintMap() {
        return this.indexHintMap;
    }
    
    @Override
    public List<IndexHintClause> getIndexHint(final Table table) {
        if (this.indexHintMap == null) {
            return null;
        }
        return this.indexHintMap.get(table);
    }
    
    @Override
    public List<IndexHintClause> removeIndexHint(final Table table) {
        if (this.indexHintMap == null) {
            return null;
        }
        return this.indexHintMap.remove(table);
    }
    
    @Override
    public boolean removeIndexHint(final Table table, final IndexHintClause idxHint) {
        if (this.indexHintMap == null) {
            return false;
        }
        final List<IndexHintClause> indexHintClauses = this.indexHintMap.get(table);
        return indexHintClauses != null && indexHintClauses.remove(idxHint);
    }
    
    public void markAsSubQuery() {
        this.isSubQuery = true;
    }
    
    public void clearSubQueryFlag() {
        this.isSubQuery = false;
    }
    
    public boolean isSubQuery() {
        return this.isSubQuery;
    }
    
    @Override
    public List<Column> getSelectColumns() {
        return new ArrayList<Column>(this.selectColumns);
    }
    
    @Override
    public void addSelectColumn(final Column column) {
        this.changed = true;
        this.selectColumns.add(column);
        if (column instanceof DerivedColumn) {
            final DerivedColumn dc = (DerivedColumn)column;
            final SelectQuery query = (SelectQuery)dc.getSubQuery();
            query.setParent(this);
            ++this.derivedObjectsCount;
            this.derivedColumns.add((DerivedColumn)column);
        }
    }
    
    @Override
    public void addSelectColumn(final Column column, final int index) {
        this.changed = true;
        this.selectColumns.add(index, column);
        if (column instanceof DerivedColumn) {
            final DerivedColumn dc = (DerivedColumn)column;
            final SelectQuery query = (SelectQuery)dc.getSubQuery();
            query.setParent(this);
            ++this.derivedObjectsCount;
            this.derivedColumns.add((DerivedColumn)column);
        }
    }
    
    @Override
    public boolean removeSelectColumn(final Column column) {
        this.changed = true;
        if (AppResources.getString("process.removecolumn.instantly", "true").equalsIgnoreCase("false")) {
            if (this.removeColumns == null) {
                this.removeColumns = new ArrayList();
            }
            this.removeColumns.add(column);
            return true;
        }
        if (column instanceof DerivedColumn) {
            final DerivedColumn dc = (DerivedColumn)column;
            final SelectQuery query = (SelectQuery)dc.getSubQuery();
            query.setParent(null);
            --this.derivedObjectsCount;
            this.derivedColumns.remove(column);
        }
        return this.selectColumns.remove(column);
    }
    
    @Override
    public void processRemoveColumns() {
        if (this.removeColumns == null || this.removeColumns.isEmpty()) {
            return;
        }
        ArrayList<Integer> numList = new ArrayList<Integer>();
        for (final Object col : this.removeColumns) {
            if (col instanceof Integer) {
                numList.add((Integer)col);
            }
        }
        this.removeColumns.removeAll(numList);
        Collections.sort(numList, Collections.reverseOrder());
        for (final int obj : numList) {
            final Object column = this.selectColumns.remove(obj);
            if (column instanceof DerivedColumn) {
                --this.derivedObjectsCount;
                this.derivedColumns.remove(column);
            }
        }
        for (final Object column2 : this.removeColumns) {
            if (column2 instanceof DerivedColumn) {
                --this.derivedObjectsCount;
                this.derivedColumns.remove(column2);
            }
            this.selectColumns.remove(column2);
        }
        this.removeColumns = null;
        numList = null;
    }
    
    @Override
    public Column removeSelectColumn(final int colIndex) {
        this.changed = true;
        if (AppResources.getString("process.removecolumn.instantly", "true").equalsIgnoreCase("false")) {
            if (this.removeColumns == null) {
                this.removeColumns = new ArrayList();
            }
            this.removeColumns.add(colIndex);
            return this.selectColumns.get(colIndex);
        }
        final Object column = this.selectColumns.remove(colIndex);
        if (column instanceof DerivedColumn) {
            final DerivedColumn dc = (DerivedColumn)column;
            final SelectQuery query = (SelectQuery)dc.getSubQuery();
            query.setParent(null);
            --this.derivedObjectsCount;
            this.derivedColumns.remove(column);
        }
        return (Column)column;
    }
    
    @Override
    public void addSelectColumns(final List columns) {
        this.changed = true;
        for (int i = 0; i < columns.size(); ++i) {
            final Object column = columns.get(i);
            if (column instanceof DerivedColumn) {
                final DerivedColumn dc = (DerivedColumn)column;
                final SelectQuery query = (SelectQuery)dc.getSubQuery();
                query.setParent(this);
                ++this.derivedObjectsCount;
                this.derivedColumns.add((DerivedColumn)column);
            }
        }
        this.selectColumns.addAll(columns);
    }
    
    @Override
    public void addSelectColumns(final List columns, final int index) {
        this.changed = true;
        for (int i = 0; i < columns.size(); ++i) {
            final Object column = columns.get(i);
            if (column instanceof DerivedColumn) {
                final DerivedColumn dc = (DerivedColumn)column;
                final SelectQuery query = (SelectQuery)dc.getSubQuery();
                query.setParent(this);
                ++this.derivedObjectsCount;
                this.derivedColumns.add((DerivedColumn)column);
            }
        }
        this.selectColumns.addAll(index, columns);
    }
    
    @Override
    public Criteria getCriteria() {
        return this.criteria;
    }
    
    @Override
    public void setCriteria(final Criteria criteria) {
        this.changed = true;
        this.criteria = criteria;
        if (null != criteria) {
            this.setParentQuery(this.criteria);
        }
    }
    
    @Override
    public Range getRange() {
        return this.range;
    }
    
    @Override
    public void setRange(final Range range) {
        this.changed = true;
        this.range = range;
    }
    
    @Override
    public List getGroupByColumns() {
        return (List)this.groupByColumns.clone();
    }
    
    @Override
    public void addGroupByColumn(final Column column) {
        this.changed = true;
        this.groupByColumns.add(column);
    }
    
    @Override
    public void addGroupByColumn(final Column groupByColumn, final int index) {
        this.changed = true;
        this.groupByColumns.add(index, groupByColumn);
    }
    
    @Override
    public boolean removeGroupByColumn(final Column groupByColumn) {
        this.changed = true;
        return this.groupByColumns.remove(groupByColumn);
    }
    
    @Override
    public Column removeGroupByColumn(final int groupIndex) {
        this.changed = true;
        return this.groupByColumns.remove(groupIndex);
    }
    
    @Override
    public void addGroupByColumns(final List groupByColumns) {
        this.changed = true;
        this.groupByColumns.addAll(groupByColumns);
    }
    
    @Override
    public void addGroupByColumns(final List groupByColumns, final int index) {
        this.changed = true;
        groupByColumns.addAll(index, groupByColumns);
    }
    
    @Override
    public GroupByClause getGroupByClause() {
        return this.groupByClause;
    }
    
    @Override
    public void setGroupByClause(final GroupByClause newGroupByClause) {
        this.changed = true;
        this.groupByClause = newGroupByClause;
    }
    
    @Override
    public void addSortColumn(final SortColumn sortColumn) {
        this.changed = true;
        this.sortColumns.add(sortColumn);
    }
    
    @Override
    public void addSortColumn(final SortColumn sortColumn, final int index) {
        this.changed = true;
        this.sortColumns.add(index, sortColumn);
    }
    
    @Override
    public boolean removeSortColumn(final SortColumn sortColumn) {
        this.changed = true;
        return this.sortColumns.remove(sortColumn);
    }
    
    @Override
    public SortColumn removeSortColumn(final int sortIndex) {
        this.changed = true;
        return this.sortColumns.remove(sortIndex);
    }
    
    @Override
    public void addSortColumns(final List<SortColumn> sortColumns) {
        this.changed = true;
        this.sortColumns.addAll(sortColumns);
    }
    
    @Override
    public void addSortColumns(final List<SortColumn> sortColumns, final int index) {
        this.changed = true;
        sortColumns.addAll(index, sortColumns);
    }
    
    @Override
    public List<SortColumn> getSortColumns() {
        return new ArrayList<SortColumn>(this.sortColumns);
    }
    
    @Override
    public List<Table> getTableList() {
        return new ArrayList<Table>(this.tableList);
    }
    
    @Override
    public String getTableNameForTableAlias(final String tableAlias) {
        final Table table = this.findTableByAlias(tableAlias);
        if (table != null) {
            return table.getTableName();
        }
        return null;
    }
    
    private Table findTableByAlias(final String tableAlias) {
        for (int i = 0; i < this.tableList.size(); ++i) {
            final Table table = this.tableList.get(i);
            if (table.getTableAlias().equals(tableAlias)) {
                return table;
            }
        }
        throw new IllegalArgumentException("Specified table alias haven't added into this object yet");
    }
    
    @Override
    public void addJoin(final Join join) {
        if (join != null) {
            if (!this.checkTable(join.getBaseTableAlias())) {
                throw new IllegalArgumentException("Base table " + join.getBaseTableAlias() + " specified in this join [" + join + "]  is not already added to this query :: " + this);
            }
            if (this.checkTable(join.getReferencedTableAlias())) {
                throw new IllegalArgumentException("Referenced table " + join.getReferencedTableAlias() + " is already specified in this query. So it can't be added again");
            }
            final Table referencedTable = join.getReferencedTable();
            Table toAddTable = new Table(join.getReferencedTableName(), join.getReferencedTableAlias());
            if (referencedTable instanceof DerivedTable) {
                ++this.derivedObjectsCount;
                this.derivedTables.add((DerivedTable)referencedTable);
                toAddTable = new DerivedTable(referencedTable.getTableAlias(), ((DerivedTable)referencedTable).getSubQuery());
            }
            this.joins.add(join);
            this.tableList.add(toAddTable);
        }
    }
    
    private boolean checkTable(final String tableAlias) {
        for (int size = this.tableList.size(), i = 0; i < size; ++i) {
            if (tableAlias.equals(this.tableList.get(i).getTableAlias())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public List<Join> getJoins() {
        return new ArrayList<Join>(this.joins);
    }
    
    protected void setBaseTable(final Table table) {
        if (this.tableList.size() > 0) {
            throw new IllegalArgumentException("The base table has already been set. So, this can not be set.");
        }
        this.tableList.add(table);
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("SelectQuery Object:");
        buf.append("\n\tSelect columnNames=");
        buf.append(String.valueOf(this.selectColumns));
        buf.append("\n\tCriteria=");
        buf.append(String.valueOf(this.criteria));
        buf.append("\n\tNumber of Objects=");
        buf.append((this.range == null) ? 0 : this.range.getNumberOfObjects());
        buf.append("\n\tStarting row=");
        buf.append((this.range == null) ? 1 : this.range.getStartIndex());
        buf.append("\n\tOrder by columnNames=");
        buf.append(String.valueOf(this.sortColumns));
        if (!this.groupByColumns.isEmpty()) {
            buf.append("\n\tGroup by columns=");
            buf.append(String.valueOf(this.groupByColumns));
        }
        else if (this.groupByClause != null) {
            buf.append("\n\tGroup by clause=");
            buf.append(this.groupByClause);
        }
        buf.append("\n\tTable List=");
        buf.append(String.valueOf(this.tableList));
        buf.append("\n\tJoins= " + this.getJoinString());
        buf.append("\n\tSetLock= " + this.getLockStatus());
        buf.append("\n\tIsCached= ").append(this.isCached());
        return buf.toString();
    }
    
    private String getJoinString() {
        final int joinsSize = this.joins.size();
        final StringBuffer overAllJoinBuffer = new StringBuffer();
        for (int i = 0; i < joinsSize; ++i) {
            if (i != 0) {
                overAllJoinBuffer.append(" , ");
            }
            overAllJoinBuffer.append(this.joins.get(i).toString());
        }
        return overAllJoinBuffer.toString();
    }
    
    @Override
    public Object clone() {
        SelectQueryImpl query = null;
        try {
            query = (SelectQueryImpl)super.clone();
        }
        catch (final CloneNotSupportedException excp) {
            return null;
        }
        query.selectColumns = new ArrayList<Column>(this.selectColumns);
        if (this.removeColumns != null && AppResources.getString("process.removecolumn.instantly", "true").equalsIgnoreCase("false")) {
            query.removeColumns = new ArrayList(this.removeColumns);
        }
        if (this.criteria != null) {
            query.criteria = (Criteria)this.criteria.clone();
        }
        if (this.groupByClause != null) {
            query.groupByClause = (GroupByClause)this.groupByClause.clone();
        }
        if (this.range != null) {
            query.range = (Range)this.range.clone();
        }
        query.lockState = this.lockState;
        query.sortColumns = new ArrayList<SortColumn>(this.sortColumns);
        query.groupByColumns = (ArrayList)this.groupByColumns.clone();
        query.tableList = new ArrayList<Table>(this.tableList);
        query.joins = new ArrayList<Join>(this.joins);
        query.isDistinct = this.isDistinct;
        query.derivedObjectsCount = this.derivedObjectsCount;
        query.derivedColumns = new ArrayList<DerivedColumn>(this.derivedColumns);
        query.derivedTables = new ArrayList<DerivedTable>(this.derivedTables);
        query.isParallel = this.isParallel;
        query.parallelWorkers = this.parallelWorkers;
        query.isCached = this.isCached;
        if (this.indexHintMap != null) {
            query.indexHintMap = new HashMap<Table, List<IndexHintClause>>();
            for (final Table key : this.indexHintMap.keySet()) {
                final List<IndexHintClause> clonedList = new ArrayList<IndexHintClause>();
                for (final IndexHintClause ihc : this.indexHintMap.get(key)) {
                    try {
                        clonedList.add((IndexHintClause)ihc.clone());
                    }
                    catch (final CloneNotSupportedException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
                query.indexHintMap.put(key, clonedList);
            }
        }
        return query;
    }
    
    @Override
    public int hashCode() {
        if (this.hashCode == -1) {
            this.hashCode = this.hashCode(this.tableList) + this.hashCode(this.selectColumns) + this.hashCode(this.joins) + this.hashCode(this.criteria) + this.hashCode(this.groupByClause) + this.hashCode(this.sortColumns) + this.hashCode(this.range) + this.hashCode(this.groupByColumns);
        }
        return this.hashCode;
    }
    
    private int hashCode(final Object obj) {
        return (obj != null) ? obj.hashCode() : 0;
    }
    
    @Override
    public boolean equals(final Object object) {
        final boolean compareSQEquals = AppResources.getBoolean("sq.equals.ignore.columnorder", Boolean.valueOf(true));
        if (object == null) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (!(object instanceof SelectQueryImpl)) {
            return false;
        }
        final SelectQueryImpl passedSQ = (SelectQueryImpl)object;
        if (!QueryUtil.compareList(this.tableList, passedSQ.tableList, true)) {
            return false;
        }
        if (!QueryUtil.compareList(this.selectColumns, passedSQ.selectColumns, compareSQEquals)) {
            return false;
        }
        if (!QueryUtil.compareList(this.joins, passedSQ.joins, true)) {
            return false;
        }
        if ((this.criteria == null && passedSQ.criteria != null) || (this.criteria != null && passedSQ.criteria == null)) {
            return false;
        }
        if (!this.equals(this.criteria, passedSQ.criteria)) {
            return false;
        }
        if (!this.equals(this.groupByClause, passedSQ.groupByClause)) {
            return false;
        }
        if ((this.getRange() == null && passedSQ.getRange() != null) || (this.getRange() != null && passedSQ.getRange() == null)) {
            return false;
        }
        if (this.getRange() != null && passedSQ.getRange() != null) {
            if (this.getRange().getNumberOfObjects() != passedSQ.getRange().getNumberOfObjects()) {
                return false;
            }
            if (this.getRange().getStartIndex() != passedSQ.getRange().getStartIndex()) {
                return false;
            }
        }
        return QueryUtil.compareList(this.sortColumns, passedSQ.sortColumns, compareSQEquals) && QueryUtil.compareList(this.groupByColumns, passedSQ.groupByColumns, compareSQEquals);
    }
    
    private boolean equals(final Object o1, final Object o2) {
        return o1 == o2 || (o1 != null && o2 != null && o1.equals(o2));
    }
    
    @Override
    public void setLock(final boolean lockState) {
        this.lockState = lockState;
    }
    
    @Override
    public boolean getLockStatus() {
        return this.lockState;
    }
    
    @Override
    public boolean containsSubQuery() {
        return this.derivedObjectsCount > 0;
    }
    
    @Override
    public List<DerivedTable> getDerivedTables() {
        return this.derivedTables;
    }
    
    @Override
    public List<DerivedColumn> getDerivedColumns() {
        return this.derivedColumns;
    }
    
    @Override
    public boolean isDistinct() {
        return this.isDistinct;
    }
    
    @Override
    public void setDistinct(final boolean isDistinct) {
        this.isDistinct = isDistinct;
    }
    
    @Override
    public void setParent(final SelectQuery query) {
        this.parentQuery = query;
    }
    
    @Override
    public SelectQuery getParent() {
        return this.parentQuery;
    }
    
    private void setParentQuery(final Criteria criteria) {
        if (null != criteria) {
            if (null == criteria.getLeftCriteria()) {
                if (criteria.getValue() instanceof DerivedColumn) {
                    final DerivedColumn dc = (DerivedColumn)criteria.getValue();
                    final SelectQuery query = (SelectQuery)dc.getSubQuery();
                    query.setParent(this);
                }
            }
            else {
                this.setParentQuery(criteria.getLeftCriteria());
                this.setParentQuery(criteria.getRightCriteria());
            }
        }
    }
    
    @Override
    public void setParallelSelect(final boolean isParallel, final int workerThreads) {
        this.isParallel = isParallel;
        this.parallelWorkers = workerThreads;
    }
    
    @Override
    public boolean isParallelSelect() {
        return this.isParallel;
    }
    
    @Override
    public int getParallelWorkers() {
        return this.parallelWorkers;
    }
    
    @Override
    public void setCached(final boolean isCached) {
        this.isCached = isCached;
    }
    
    @Override
    public boolean isCached() {
        return this.isCached;
    }
}
