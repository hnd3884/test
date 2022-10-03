package net.sf.jsqlparser.expression.operators.relational;

import net.sf.jsqlparser.expression.ExpressionVisitor;

public class NotEqualsTo extends ComparisonOperator
{
    public NotEqualsTo() {
        super("<>");
    }
    
    public NotEqualsTo(final String operator) {
        super(operator);
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
}
