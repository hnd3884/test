package net.sf.jsqlparser.expression.operators.relational;

import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.Expression;

public class IsNullExpression implements Expression
{
    private Expression leftExpression;
    private boolean not;
    
    public IsNullExpression() {
        this.not = false;
    }
    
    public Expression getLeftExpression() {
        return this.leftExpression;
    }
    
    public boolean isNot() {
        return this.not;
    }
    
    public void setLeftExpression(final Expression expression) {
        this.leftExpression = expression;
    }
    
    public void setNot(final boolean b) {
        this.not = b;
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    @Override
    public String toString() {
        return this.leftExpression + " IS " + (this.not ? "NOT " : "") + "NULL";
    }
}
