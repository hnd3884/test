package com.adventnet.ds.query;

import java.io.Serializable;

public class DerivedColumn extends Column implements Serializable, Cloneable
{
    private static final long serialVersionUID = 1608416054029634478L;
    private Query subQuery;
    
    public DerivedColumn(final String columnName, final SelectQuery subQuery) throws IllegalArgumentException {
        super(null, columnName);
        this.subQuery = null;
        this.subQuery = subQuery;
        if (subQuery.getSelectColumns().size() > 1) {
            throw new IllegalArgumentException("Column subquery cannot contain more than one column");
        }
    }
    
    public Query getSubQuery() {
        return this.subQuery;
    }
    
    @Override
    public Object clone() {
        final DerivedColumn dc = (DerivedColumn)super.clone();
        dc.subQuery = (Query)this.subQuery.clone();
        return dc;
    }
    
    @Override
    public boolean equals(final Object obj) {
        boolean equals = super.equals(obj);
        if (equals) {
            if (obj instanceof DerivedColumn) {
                final DerivedColumn dc = (DerivedColumn)obj;
                equals = this.subQuery.equals(dc.subQuery);
            }
            return equals;
        }
        return equals;
    }
}
