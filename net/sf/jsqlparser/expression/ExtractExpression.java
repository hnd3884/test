package net.sf.jsqlparser.expression;

public class ExtractExpression implements Expression
{
    private String name;
    private Expression expression;
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Expression getExpression() {
        return this.expression;
    }
    
    public void setExpression(final Expression expression) {
        this.expression = expression;
    }
    
    @Override
    public String toString() {
        return "EXTRACT(" + this.name + " FROM " + this.expression + ')';
    }
}
