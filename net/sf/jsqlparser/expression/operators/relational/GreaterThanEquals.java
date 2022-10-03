package net.sf.jsqlparser.expression.operators.relational;

import net.sf.jsqlparser.expression.ExpressionVisitor;

public class GreaterThanEquals extends ComparisonOperator
{
    public GreaterThanEquals() {
        super(">=");
    }
    
    public GreaterThanEquals(final String operator) {
        super(operator);
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
}
