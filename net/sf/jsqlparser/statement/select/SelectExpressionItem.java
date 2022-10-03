package net.sf.jsqlparser.statement.select;

import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;

public class SelectExpressionItem implements SelectItem
{
    private Expression expression;
    private Alias alias;
    
    public SelectExpressionItem() {
    }
    
    public SelectExpressionItem(final Expression expression) {
        this.expression = expression;
    }
    
    public Alias getAlias() {
        return this.alias;
    }
    
    public Expression getExpression() {
        return this.expression;
    }
    
    public void setAlias(final Alias alias) {
        this.alias = alias;
    }
    
    public void setExpression(final Expression expression) {
        this.expression = expression;
    }
    
    @Override
    public void accept(final SelectItemVisitor selectItemVisitor) {
        selectItemVisitor.visit(this);
    }
    
    @Override
    public String toString() {
        return this.expression + ((this.alias != null) ? this.alias.toString() : "");
    }
}
