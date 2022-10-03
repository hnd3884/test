package net.sf.jsqlparser.statement.replace;

import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.schema.Column;
import java.util.List;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;

public class Replace implements Statement
{
    private Table table;
    private List<Column> columns;
    private ItemsList itemsList;
    private List<Expression> expressions;
    private boolean useValues;
    private boolean useIntoTables;
    
    public Replace() {
        this.useValues = true;
        this.useIntoTables = false;
    }
    
    @Override
    public void accept(final StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }
    
    public Table getTable() {
        return this.table;
    }
    
    public void setTable(final Table name) {
        this.table = name;
    }
    
    public boolean isUseIntoTables() {
        return this.useIntoTables;
    }
    
    public void setUseIntoTables(final boolean useIntoTables) {
        this.useIntoTables = useIntoTables;
    }
    
    public List<Column> getColumns() {
        return this.columns;
    }
    
    public ItemsList getItemsList() {
        return this.itemsList;
    }
    
    public void setColumns(final List<Column> list) {
        this.columns = list;
    }
    
    public void setItemsList(final ItemsList list) {
        this.itemsList = list;
    }
    
    public List<Expression> getExpressions() {
        return this.expressions;
    }
    
    public void setExpressions(final List<Expression> list) {
        this.expressions = list;
    }
    
    public boolean isUseValues() {
        return this.useValues;
    }
    
    public void setUseValues(final boolean useValues) {
        this.useValues = useValues;
    }
    
    @Override
    public String toString() {
        final StringBuilder sql = new StringBuilder();
        sql.append("REPLACE ");
        if (this.isUseIntoTables()) {
            sql.append("INTO ");
        }
        sql.append(this.table);
        if (this.expressions != null && this.columns != null) {
            sql.append(" SET ");
            for (int i = 0, s = this.columns.size(); i < s; ++i) {
                sql.append(this.columns.get(i)).append("=").append(this.expressions.get(i));
                sql.append((i < s - 1) ? ", " : "");
            }
        }
        else if (this.columns != null) {
            sql.append(" ").append(PlainSelect.getStringList(this.columns, true, true));
        }
        if (this.itemsList != null) {
            if (this.useValues) {
                sql.append(" VALUES");
            }
            sql.append(" ").append(this.itemsList);
        }
        return sql.toString();
    }
}
