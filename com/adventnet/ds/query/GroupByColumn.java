package com.adventnet.ds.query;

import java.util.Objects;

public class GroupByColumn
{
    private Column column;
    private boolean caseSensitive;
    
    public GroupByColumn(final Column column, final boolean caseSensitive) {
        this.column = null;
        if (column == null) {
            throw new IllegalArgumentException("column cannot be null");
        }
        this.column = column;
        this.caseSensitive = caseSensitive;
    }
    
    public boolean isCaseSensitive() {
        return this.caseSensitive;
    }
    
    public Column getGroupByColumn() {
        return this.column;
    }
    
    @Override
    public String toString() {
        return this.column.toString();
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.column, this.caseSensitive);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof GroupByColumn) {
            final GroupByColumn groupByColumn = (GroupByColumn)obj;
            return this.column.equals(groupByColumn.column) && this.caseSensitive == groupByColumn.caseSensitive;
        }
        return false;
    }
}
