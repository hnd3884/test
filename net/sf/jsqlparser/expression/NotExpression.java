package net.sf.jsqlparser.expression;

public class NotExpression implements Expression
{
    private Expression expression;
    
    public NotExpression(final Expression expression) {
        this.setExpression(expression);
    }
    
    public Expression getExpression() {
        return this.expression;
    }
    
    public final void setExpression(final Expression expression) {
        this.expression = expression;
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    @Override
    public String toString() {
        return "NOT " + this.expression.toString();
    }
}
