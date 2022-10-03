package net.sf.jsqlparser.statement.create.table;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;

public class CheckConstraint extends NamedConstraint
{
    private Table table;
    private Expression expression;
    
    public Table getTable() {
        return this.table;
    }
    
    public void setTable(final Table table) {
        this.table = table;
    }
    
    public Expression getExpression() {
        return this.expression;
    }
    
    public void setExpression(final Expression expression) {
        this.expression = expression;
    }
    
    @Override
    public String toString() {
        return "CONSTRAINT " + this.getName() + " CHECK (" + this.expression + ")";
    }
}
