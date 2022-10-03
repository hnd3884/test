package net.sf.jsqlparser.expression;

import net.sf.jsqlparser.expression.operators.relational.ExpressionList;

public class RowConstructor implements Expression
{
    private ExpressionList exprList;
    private String name;
    
    public RowConstructor() {
        this.name = null;
    }
    
    public ExpressionList getExprList() {
        return this.exprList;
    }
    
    public void setExprList(final ExpressionList exprList) {
        this.exprList = exprList;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    @Override
    public String toString() {
        return ((this.name != null) ? this.name : "") + this.exprList.toString();
    }
}
