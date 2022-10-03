package net.sf.jsqlparser.expression;

public class WhenClause implements Expression
{
    private Expression whenExpression;
    private Expression thenExpression;
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    public Expression getThenExpression() {
        return this.thenExpression;
    }
    
    public void setThenExpression(final Expression thenExpression) {
        this.thenExpression = thenExpression;
    }
    
    public Expression getWhenExpression() {
        return this.whenExpression;
    }
    
    public void setWhenExpression(final Expression whenExpression) {
        this.whenExpression = whenExpression;
    }
    
    @Override
    public String toString() {
        return "WHEN " + this.whenExpression + " THEN " + this.thenExpression;
    }
}
