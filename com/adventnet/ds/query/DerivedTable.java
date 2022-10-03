package com.adventnet.ds.query;

import java.io.Serializable;

public class DerivedTable extends Table implements Serializable, Cloneable
{
    private static final long serialVersionUID = 6702559897994332103L;
    private String tableAlias;
    private Query subQuery;
    int hashCode;
    
    public DerivedTable(final String tableAlias, final Query subQuery) {
        super(tableAlias);
        this.tableAlias = null;
        this.subQuery = null;
        this.hashCode = -1;
        this.subQuery = subQuery;
    }
    
    public Query getSubQuery() {
        return this.subQuery;
    }
    
    @Override
    public boolean equals(final Object obj) {
        DerivedTable table = null;
        if (this == obj) {
            return true;
        }
        if (obj instanceof DerivedTable) {
            table = (DerivedTable)obj;
            return !super.equals(obj) || this.subQuery.equals(table.subQuery);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        if (this.hashCode == -1) {
            this.hashCode = super.hashCode() + this.subQuery.hashCode();
        }
        return this.hashCode;
    }
    
    private int hashCode(final Object obj) {
        return (obj == null) ? 0 : obj.hashCode();
    }
    
    @Override
    public String toString() {
        return "( " + this.subQuery.toString() + ") AS " + this.getTableAlias();
    }
    
    @Override
    public Object clone() {
        return super.clone();
    }
}
