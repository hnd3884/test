package com.adventnet.ds.query;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

public class GroupByClause implements Serializable, Cloneable
{
    private static final long serialVersionUID = 3256994925354713004L;
    private ArrayList groupByColumns;
    private Criteria criteriaForHavingClause;
    int hashCode;
    
    public GroupByClause(final List groupByColumns) {
        this.groupByColumns = new ArrayList();
        this.hashCode = -1;
        this.groupByColumns.addAll(groupByColumns);
    }
    
    public GroupByClause(final List groupByColumns, final Criteria criteriaForHavingClause) {
        this.groupByColumns = new ArrayList();
        this.hashCode = -1;
        this.groupByColumns.addAll(groupByColumns);
        this.criteriaForHavingClause = criteriaForHavingClause;
    }
    
    public List getGroupByColumns() {
        return (List)this.groupByColumns.clone();
    }
    
    public Criteria getCriteriaForHavingClause() {
        return this.criteriaForHavingClause;
    }
    
    public Object clone() {
        return this;
    }
    
    @Override
    public int hashCode() {
        if (this.hashCode == -1) {
            this.hashCode = this.hashCode(this.groupByColumns) + this.hashCode(this.criteriaForHavingClause);
        }
        return this.hashCode;
    }
    
    private int hashCode(final Object obj) {
        return (obj != null) ? obj.hashCode() : 0;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof GroupByClause)) {
            return false;
        }
        final GroupByClause clause2 = (GroupByClause)obj;
        if (this.criteriaForHavingClause == null) {
            if (clause2.criteriaForHavingClause != null) {
                return false;
            }
        }
        else if (!this.criteriaForHavingClause.equals(clause2.criteriaForHavingClause)) {
            return false;
        }
        return this.groupByColumns.equals(clause2.getGroupByColumns());
    }
    
    @Override
    public String toString() {
        if (this.groupByColumns.isEmpty()) {
            return "";
        }
        final StringBuffer buff = new StringBuffer();
        buff.append("GROUP BY ").append(this.groupByColumns);
        if (this.criteriaForHavingClause != null) {
            buff.append(" HAVING ").append(this.criteriaForHavingClause);
        }
        return buff.toString();
    }
}
