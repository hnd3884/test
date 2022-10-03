package net.sf.jsqlparser.statement.select;

import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;

public class ExpressionListItem
{
    private ExpressionList expressionList;
    private Alias alias;
    
    public ExpressionList getExpressionList() {
        return this.expressionList;
    }
    
    public void setExpressionList(final ExpressionList expressionList) {
        this.expressionList = expressionList;
    }
    
    public Alias getAlias() {
        return this.alias;
    }
    
    public void setAlias(final Alias alias) {
        this.alias = alias;
    }
    
    @Override
    public String toString() {
        return this.expressionList + ((this.alias != null) ? this.alias.toString() : "");
    }
}
