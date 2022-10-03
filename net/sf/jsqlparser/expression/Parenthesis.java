package net.sf.jsqlparser.expression;

public class Parenthesis implements Expression
{
    private Expression expression;
    private boolean not;
    
    public Parenthesis() {
        this.not = false;
    }
    
    public Parenthesis(final Expression expression) {
        this.not = false;
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
    
    public void setNot() {
        this.not = true;
    }
    
    public boolean isNot() {
        return this.not;
    }
    
    @Override
    public String toString() {
        return (this.not ? "NOT " : "") + "(" + this.expression + ")";
    }
}
