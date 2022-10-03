package net.sf.jsqlparser.expression;

public class NullValue implements Expression
{
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    @Override
    public String toString() {
        return "NULL";
    }
}
