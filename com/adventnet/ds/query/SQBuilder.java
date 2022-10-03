package com.adventnet.ds.query;

import com.adventnet.db.api.RelationalAPI;
import java.util.ArrayList;
import java.util.List;

public class SQBuilder
{
    private SelectQuery squery;
    private List<Column> groupByColumns;
    private Criteria havingCriteria;
    
    public SQBuilder(final String tableName) {
        this.squery = null;
        this.squery = new SelectQueryImpl(Table.getTable(tableName));
    }
    
    public SQBuilder(final String tableName, final String tableAlias) {
        this.squery = null;
        this.squery = new SelectQueryImpl(Table.getTable(tableName, tableAlias));
    }
    
    public SQBuilder selectAll() {
        return this.select(new Column(null, "*"));
    }
    
    public SQBuilder select(final String tableName, final String columnName) {
        return this.select(Column.getColumn(tableName, columnName));
    }
    
    public SQBuilder select(final String tableName, final String columnName, final String columnAlias) {
        return this.select(Column.getColumn(tableName, columnName, columnAlias));
    }
    
    public SQBuilder select(final Column column) {
        this.squery.addSelectColumn(column);
        return this;
    }
    
    public SQBuilder innerJoin(final String baseTableName, final String referencedTableName, final String baseColumnName, final String referencedColumnName) {
        return this.innerJoin(baseTableName, referencedTableName, new String[] { baseColumnName }, new String[] { referencedColumnName });
    }
    
    public SQBuilder innerJoin(final String baseTableName, final String referencedTableName, final String[] baseColumnNames, final String[] referencedColumnNames) {
        final Join j = new Join(baseTableName, referencedTableName, baseColumnNames, referencedColumnNames, 2);
        this.squery.addJoin(j);
        return this;
    }
    
    public SQBuilder leftJoin(final String baseTableName, final String referencedTableName, final String baseColumnName, final String referencedColumnName) {
        return this.leftJoin(baseTableName, referencedTableName, new String[] { baseColumnName }, new String[] { referencedColumnName });
    }
    
    public SQBuilder leftJoin(final String baseTableName, final String referencedTableName, final String[] baseColumnNames, final String[] referencedColumnNames) {
        final Join j = new Join(baseTableName, referencedTableName, baseColumnNames, referencedColumnNames, 1);
        this.squery.addJoin(j);
        return this;
    }
    
    public SQBuilder join(final Join join) {
        this.squery.addJoin(join);
        return this;
    }
    
    public SQBuilder where(final Column column, final int operator, final Object value) {
        return this.where(new Criteria(column, value, operator));
    }
    
    public SQBuilder whereAllOf(final Criteria... criterions) {
        final Criteria c = new Criteria();
        for (final Criteria criterion : criterions) {
            c.and(criterion);
        }
        return this.where(c);
    }
    
    public SQBuilder whereAnyOf(final Criteria... criterions) {
        final Criteria c = new Criteria();
        for (final Criteria criterion : criterions) {
            c.or(criterion);
        }
        return this.where(c);
    }
    
    public SQBuilder where(final Criteria criteria) {
        this.squery.setCriteria(criteria);
        return this;
    }
    
    public SQBuilder groupBy(final String tableName, final String columnName) {
        return this.groupBy(Column.getColumn(tableName, columnName));
    }
    
    public SQBuilder groupBy(final Column column) {
        if (this.groupByColumns == null) {
            this.groupByColumns = new ArrayList<Column>();
        }
        this.groupByColumns.add(column);
        return this;
    }
    
    public SQBuilder having(final Column column, final int operator, final Object value) {
        return this.having(new Criteria(column, value, operator));
    }
    
    public SQBuilder having(final Criteria havingCriteria) {
        if (this.groupByColumns == null) {
            throw new IllegalStateException("Seems having() is invoked before invoking groupBy(). Not Allowed");
        }
        this.havingCriteria = havingCriteria;
        return this;
    }
    
    public SQBuilder orderByAsc(final String tableName, final String columnName) {
        return this.orderByAsc(Column.getColumn(tableName, columnName));
    }
    
    public SQBuilder orderByAsc(final Column column) {
        this.squery.addSortColumn(new SortColumn(column, true));
        return this;
    }
    
    public SQBuilder orderByDesc(final String tableName, final String columnName) {
        return this.orderByDesc(Column.getColumn(tableName, columnName));
    }
    
    public SQBuilder orderByDesc(final Column column) {
        this.squery.addSortColumn(new SortColumn(column, false));
        return this;
    }
    
    public SQBuilder limit(final int noOfObject) {
        return this.limit(1, noOfObject);
    }
    
    public SQBuilder limit(final int startIndex, final int noOfObjects) {
        this.squery.setRange(new Range(startIndex, noOfObjects));
        return this;
    }
    
    public SelectQuery toSelectQuery() {
        if (this.groupByColumns != null) {
            this.squery.setGroupByClause(new GroupByClause(this.groupByColumns, this.havingCriteria));
        }
        return this.squery;
    }
    
    public String toSqlString() {
        try {
            return RelationalAPI.getInstance().getSelectSQL(this.toSelectQuery());
        }
        catch (final QueryConstructionException exp) {
            throw new RuntimeException("Problem while generating SQL string", exp);
        }
    }
}
