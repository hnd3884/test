package net.sf.jsqlparser.expression.operators.relational;

import net.sf.jsqlparser.expression.ExpressionVisitor;

public class MinorThanEquals extends ComparisonOperator
{
    public MinorThanEquals() {
        super("<=");
    }
    
    public MinorThanEquals(final String operator) {
        super(operator);
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
}
