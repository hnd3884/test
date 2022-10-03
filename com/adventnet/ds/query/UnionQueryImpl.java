package com.adventnet.ds.query;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

public class UnionQueryImpl implements UnionQuery, Cloneable
{
    private Query leftQuery;
    private Query rightQuery;
    private boolean retainDuplicateRows;
    private Range range;
    private ArrayList<SortColumn> sortColumns;
    
    public UnionQueryImpl(final Query leftQuery, final Query rightQuery, final boolean retainDuplicateRows) {
        this.leftQuery = null;
        this.rightQuery = null;
        this.retainDuplicateRows = false;
        this.sortColumns = new ArrayList<SortColumn>();
        this.leftQuery = leftQuery;
        this.rightQuery = rightQuery;
        this.retainDuplicateRows = retainDuplicateRows;
    }
    
    @Override
    public UnionQuery union(final Query query, final boolean retainDuplicateRows) {
        return new UnionQueryImpl(this, query, retainDuplicateRows);
    }
    
    @Override
    public void setRange(final Range range) {
        this.range = range;
    }
    
    @Override
    public Range getRange() {
        return this.range;
    }
    
    @Override
    public Query getLeftQuery() {
        return this.leftQuery;
    }
    
    @Override
    public Query getRightQuery() {
        return this.rightQuery;
    }
    
    @Override
    public boolean isRetainDuplicateRows() {
        return this.retainDuplicateRows;
    }
    
    @Override
    public Object clone() {
        UnionQueryImpl unionQuery = null;
        try {
            unionQuery = (UnionQueryImpl)super.clone();
        }
        catch (final CloneNotSupportedException excp) {
            return null;
        }
        unionQuery.leftQuery = (Query)this.leftQuery.clone();
        unionQuery.rightQuery = (Query)this.rightQuery.clone();
        unionQuery.retainDuplicateRows = this.retainDuplicateRows;
        if (this.range != null) {
            unionQuery.range = (Range)this.range.clone();
        }
        return unionQuery;
    }
    
    @Override
    public String toString() {
        StringBuilder buff = new StringBuilder();
        buff = this.getStringBuilder(buff);
        return buff.toString();
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.leftQuery, this.rightQuery, this.retainDuplicateRows, this.range, this.sortColumns);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof UnionQueryImpl) {
            final UnionQueryImpl uq = (UnionQueryImpl)obj;
            return Objects.equals(this.leftQuery, uq.leftQuery) && Objects.equals(this.rightQuery, uq.rightQuery) && Objects.equals(this.retainDuplicateRows, uq.retainDuplicateRows) && Objects.equals(this.range, uq.range) && Objects.equals(this.sortColumns, uq.sortColumns);
        }
        return false;
    }
    
    private StringBuilder getStringBuilder(final StringBuilder buff) {
        buff.append("( ");
        String leftString = null;
        String rightString = null;
        if (this.leftQuery != null) {
            leftString = this.leftQuery.toString();
        }
        if (this.rightQuery != null) {
            rightString = this.rightQuery.toString();
        }
        if (leftString != null && rightString != null) {
            buff.append("( ").append(leftString).append(" ) UNION ");
            if (this.retainDuplicateRows) {
                buff.append("ALL ");
            }
            buff.append("( ").append(rightString).append(" )");
        }
        buff.append(" )\n\tNumber of Objects=");
        buff.append((this.range == null) ? 0 : this.range.getNumberOfObjects());
        buff.append("\n\tStarting row=");
        buff.append((this.range == null) ? 1 : this.range.getStartIndex());
        buff.append("\n\tOrder by columnNames=");
        buff.append(String.valueOf(this.sortColumns));
        return buff;
    }
    
    @Override
    public void addSortColumn(final SortColumn sortColumn) {
        this.sortColumns.add(sortColumn);
    }
    
    @Override
    public void addSortColumn(final SortColumn sortColumn, final int index) {
        this.sortColumns.add(index, sortColumn);
    }
    
    @Override
    public List<SortColumn> getSortColumns() {
        return (List)this.sortColumns.clone();
    }
    
    @Override
    public List<Column> getSelectColumns() {
        return this.getSelectColumns(this.leftQuery);
    }
    
    private List<Column> getSelectColumns(final Query query) {
        if (query instanceof UnionQuery) {
            return this.getSelectColumns(((UnionQuery)query).getLeftQuery());
        }
        if (query instanceof SelectQuery) {
            final SelectQuery select = (SelectQuery)query;
            return select.getSelectColumns();
        }
        return null;
    }
    
    private List<Table> getTableList(final Query query) {
        if (query instanceof SelectQuery) {
            return ((SelectQuery)query).getTableList();
        }
        if (query instanceof UnionQuery) {
            return this.getTableList(((UnionQuery)query).getLeftQuery());
        }
        return null;
    }
    
    @Override
    public List<Table> getTableList() {
        final Set<Table> tableList = new HashSet<Table>();
        this.getTableList(this.leftQuery, tableList);
        this.getTableList(this.rightQuery, tableList);
        return new ArrayList<Table>(tableList);
    }
    
    private void getTableList(final Query queryInstance, final Set tableList) {
        if (queryInstance != null && queryInstance instanceof SelectQuery) {
            tableList.addAll(((SelectQuery)queryInstance).getTableList());
        }
        else if (queryInstance != null && queryInstance instanceof UnionQuery) {
            this.getTableList(((UnionQuery)queryInstance).getLeftQuery(), tableList);
            this.getTableList(((UnionQuery)queryInstance).getRightQuery(), tableList);
        }
    }
    
    @Override
    public boolean removeSortColumn(final SortColumn sortColumn) {
        return this.sortColumns.remove(sortColumn);
    }
    
    @Override
    public SortColumn removeSortColumn(final int sortIndex) {
        return this.sortColumns.remove(sortIndex);
    }
}
