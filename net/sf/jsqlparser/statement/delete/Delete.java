package net.sf.jsqlparser.statement.delete;

import java.util.Iterator;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.Join;
import java.util.List;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;

public class Delete implements Statement
{
    private Table table;
    private List<Table> tables;
    private List<Join> joins;
    private Expression where;
    private Limit limit;
    private List<OrderByElement> orderByElements;
    
    public List<OrderByElement> getOrderByElements() {
        return this.orderByElements;
    }
    
    public void setOrderByElements(final List<OrderByElement> orderByElements) {
        this.orderByElements = orderByElements;
    }
    
    @Override
    public void accept(final StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }
    
    public Table getTable() {
        return this.table;
    }
    
    public Expression getWhere() {
        return this.where;
    }
    
    public void setTable(final Table name) {
        this.table = name;
    }
    
    public void setWhere(final Expression expression) {
        this.where = expression;
    }
    
    public Limit getLimit() {
        return this.limit;
    }
    
    public void setLimit(final Limit limit) {
        this.limit = limit;
    }
    
    public List<Table> getTables() {
        return this.tables;
    }
    
    public void setTables(final List<Table> tables) {
        this.tables = tables;
    }
    
    public List<Join> getJoins() {
        return this.joins;
    }
    
    public void setJoins(final List<Join> joins) {
        this.joins = joins;
    }
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder("DELETE");
        if (this.tables != null && this.tables.size() > 0) {
            b.append(" ");
            for (final Table t : this.tables) {
                b.append(t.toString());
            }
        }
        b.append(" FROM ");
        b.append(this.table);
        if (this.joins != null) {
            for (final Join join : this.joins) {
                if (join.isSimple()) {
                    b.append(", ").append(join);
                }
                else {
                    b.append(" ").append(join);
                }
            }
        }
        if (this.where != null) {
            b.append(" WHERE ").append(this.where);
        }
        if (this.orderByElements != null) {
            b.append(PlainSelect.orderByToString(this.orderByElements));
        }
        if (this.limit != null) {
            b.append(this.limit);
        }
        return b.toString();
    }
}
