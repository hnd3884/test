package net.sf.jsqlparser.expression.operators.relational;

import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.Expression;

public class Between implements Expression
{
    private Expression leftExpression;
    private boolean not;
    private Expression betweenExpressionStart;
    private Expression betweenExpressionEnd;
    
    public Between() {
        this.not = false;
    }
    
    public Expression getBetweenExpressionEnd() {
        return this.betweenExpressionEnd;
    }
    
    public Expression getBetweenExpressionStart() {
        return this.betweenExpressionStart;
    }
    
    public Expression getLeftExpression() {
        return this.leftExpression;
    }
    
    public boolean isNot() {
        return this.not;
    }
    
    public void setBetweenExpressionEnd(final Expression expression) {
        this.betweenExpressionEnd = expression;
    }
    
    public void setBetweenExpressionStart(final Expression expression) {
        this.betweenExpressionStart = expression;
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
        return this.leftExpression + " " + (this.not ? "NOT " : "") + "BETWEEN " + this.betweenExpressionStart + " AND " + this.betweenExpressionEnd;
    }
}
