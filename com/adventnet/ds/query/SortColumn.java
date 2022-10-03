package com.adventnet.ds.query;

import java.util.Vector;
import java.util.logging.Logger;
import java.io.Serializable;

public class SortColumn implements Serializable
{
    private static final long serialVersionUID = -6458907283560886756L;
    public static final SortColumn NULL_COLUMN;
    private static Logger logger;
    private Column column;
    private boolean ascending;
    private Boolean isNullsFirst;
    private boolean isCaseSensitive;
    private Vector sortOrder;
    int hashCode;
    
    public SortColumn(final String tableAlias, final String columnName, final boolean isAscending) throws IllegalArgumentException {
        this(Column.getColumn(tableAlias, columnName), isAscending);
    }
    
    public SortColumn(final String tableAlias, final String columnName, final boolean isAscending, final boolean isCaseSensitive, final Boolean isNullsFirst) throws IllegalArgumentException {
        this(Column.getColumn(tableAlias, columnName), isAscending, isCaseSensitive, isNullsFirst);
    }
    
    @Deprecated
    public SortColumn(final String tableAlias, final String columnName, final boolean isAscending, final boolean isCaseSensitive) throws IllegalArgumentException {
        this(Column.getColumn(tableAlias, columnName), isAscending, isCaseSensitive);
    }
    
    public SortColumn(final Column column, final boolean isAscending) throws IllegalArgumentException {
        this(column, isAscending, false, null);
    }
    
    @Deprecated
    public SortColumn(final Column column, final boolean isAscending, final boolean isCaseSensitive) throws IllegalArgumentException {
        this.isNullsFirst = null;
        this.isCaseSensitive = Boolean.FALSE;
        this.sortOrder = null;
        this.hashCode = -1;
        if (column == null) {
            throw new IllegalArgumentException("column cannot be null");
        }
        this.column = column;
        this.ascending = isAscending;
        this.setCaseSensitive(isCaseSensitive);
    }
    
    public SortColumn(final Column column, final boolean isAscending, final boolean isCaseSensitive, final Boolean isNullsFirst) throws IllegalArgumentException {
        this.isNullsFirst = null;
        this.isCaseSensitive = Boolean.FALSE;
        this.sortOrder = null;
        this.hashCode = -1;
        if (column == null) {
            throw new IllegalArgumentException("column cannot be null");
        }
        this.column = column;
        this.ascending = isAscending;
        this.isCaseSensitive = isCaseSensitive;
        this.isNullsFirst = isNullsFirst;
    }
    
    public SortColumn(final String tableAlias, final String columnName, final String columnAlias, final boolean isAscending) throws IllegalArgumentException {
        this(Column.getColumn(tableAlias, columnName, columnAlias), isAscending);
    }
    
    public SortColumn(final String tableAlias, final String columnName, final String columnAlias, final boolean isAscending, final boolean isCaseSensitive, final Boolean isNullsFirst) throws IllegalArgumentException {
        this(Column.getColumn(tableAlias, columnName, columnAlias), isAscending, isCaseSensitive, isNullsFirst);
    }
    
    @Deprecated
    public SortColumn(final String tableAlias, final String columnName, final String columnAlias, final boolean isAscending, final boolean isCaseSensitive) throws IllegalArgumentException {
        this(Column.getColumn(tableAlias, columnName, columnAlias), isAscending, isCaseSensitive);
    }
    
    public String getColumnName() {
        return this.column.getColumnName();
    }
    
    public String getColumnAlias() {
        return this.column.getColumnAlias();
    }
    
    public String getTableAlias() {
        return this.column.getTableAlias();
    }
    
    public Column getColumn() {
        return this.column;
    }
    
    public boolean isAscending() {
        return this.ascending;
    }
    
    public void setAscending(final boolean ascending) {
        this.ascending = ascending;
    }
    
    public Boolean isNullsFirst() {
        return this.isNullsFirst;
    }
    
    public void setSortOrder(final Vector sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public Vector getSortOrder() {
        return this.sortOrder;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof SortColumn)) {
            return false;
        }
        final SortColumn sc = (SortColumn)obj;
        return this.column.equals(sc.column) && this.ascending == sc.ascending && this.equals(this.sortOrder, sc.sortOrder);
    }
    
    @Override
    public int hashCode() {
        if (this.hashCode == -1) {
            this.hashCode = this.hashCode(this.column) + this.hashCode(this.sortOrder) + (this.ascending ? 1 : 0);
        }
        return this.hashCode;
    }
    
    private int hashCode(final Object obj) {
        return (obj != null) ? obj.hashCode() : 0;
    }
    
    private boolean equals(final Object o1, final Object o2) {
        return o1 == o2 || (o1 != null && o2 != null && o1.equals(o2));
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(" ORDER BY ");
        if (this.column instanceof Function || this.column instanceof Operation) {
            sb.append(this.column.toString());
        }
        else {
            String alias = null;
            if (this.column instanceof LocaleColumn) {
                alias = ((LocaleColumn)this.column).getColumn().getColumnAlias();
            }
            else {
                alias = this.column.getColumnAlias();
            }
            sb.append((alias != null && alias.equals(this.column.getColumnName())) ? String.valueOf(this.column) : alias);
        }
        if (this.ascending) {
            sb.append(" ASC ");
        }
        else {
            sb.append(" DESC ");
        }
        if (this.isNullsFirst() != null) {
            if (this.isNullsFirst()) {
                sb.append(" NULLS FIRST");
            }
            else {
                sb.append(" NULLS LAST");
            }
        }
        return sb.toString();
    }
    
    public boolean isCaseSensitive() {
        return this.isCaseSensitive;
    }
    
    public void setCaseSensitive(final boolean isCaseSensitivity) {
        this.isCaseSensitive = isCaseSensitivity;
    }
    
    static {
        NULL_COLUMN = null;
        SortColumn.logger = Logger.getLogger(SortColumn.class.getName());
    }
}
