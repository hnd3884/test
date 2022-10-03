package net.sf.jsqlparser.expression.operators.relational;

import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.Expression;

public class ExistsExpression implements Expression
{
    private Expression rightExpression;
    private boolean not;
    
    public ExistsExpression() {
        this.not = false;
    }
    
    public Expression getRightExpression() {
        return this.rightExpression;
    }
    
    public void setRightExpression(final Expression expression) {
        this.rightExpression = expression;
    }
    
    public boolean isNot() {
        return this.not;
    }
    
    public void setNot(final boolean b) {
        this.not = b;
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    public String getStringExpression() {
        return (this.not ? "NOT " : "") + "EXISTS";
    }
    
    @Override
    public String toString() {
        return this.getStringExpression() + " " + this.rightExpression.toString();
    }
}
