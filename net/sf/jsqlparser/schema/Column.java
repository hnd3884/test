package net.sf.jsqlparser.schema;

import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.ASTNodeAccessImpl;

public final class Column extends ASTNodeAccessImpl implements Expression, MultiPartName
{
    private Table table;
    private String columnName;
    
    public Column() {
    }
    
    public Column(final Table table, final String columnName) {
        this.setTable(table);
        this.setColumnName(columnName);
    }
    
    public Column(final String columnName) {
        this(null, columnName);
    }
    
    public Table getTable() {
        return this.table;
    }
    
    public void setTable(final Table table) {
        this.table = table;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    public void setColumnName(final String string) {
        this.columnName = string;
    }
    
    @Override
    public String getFullyQualifiedName() {
        return this.getName(false);
    }
    
    public String getName(final boolean aliases) {
        final StringBuilder fqn = new StringBuilder();
        if (this.table != null) {
            if (this.table.getAlias() != null && aliases) {
                fqn.append(this.table.getAlias().getName());
            }
            else {
                fqn.append(this.table.getFullyQualifiedName());
            }
        }
        if (fqn.length() > 0) {
            fqn.append('.');
        }
        if (this.columnName != null) {
            fqn.append(this.columnName);
        }
        return fqn.toString();
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    @Override
    public String toString() {
        return this.getName(true);
    }
}
